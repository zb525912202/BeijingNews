package com.atguigu.beijingnews.menudatailpager.tabdetailpager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.activity.NewsDetailActivity;
import com.atguigu.beijingnews.base.MenuDetaiBasePager;
import com.atguigu.beijingnews.domain.NewsCenterPagerBean2;
import com.atguigu.beijingnews.domain.TabDetailPagerBean;
import com.atguigu.beijingnews.utils.CacheUtils;
import com.atguigu.beijingnews.utils.Constants;
import com.atguigu.beijingnews.utils.LogUtil;
import com.atguigu.beijingnews.view.HorizontalScrollViewPager;
import com.atguigu.refreshlistview.RefreshListview;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * 作用：页签详情页面
 */
public class TabDetailPager extends MenuDetaiBasePager {

    public static final String READ_ARRAY_ID = "read_array_id";
    private HorizontalScrollViewPager  viewpager;
    private TextView tv_title;
    private LinearLayout ll_point_group;
    private RefreshListview listview;
    private TabDetailPagerListAdapter adapter;
    private ImageOptions imageOptions;
    private final NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData;
    //    private TextView textView;
    private String url;
    /**
     * 顶部轮播图部分的数据
     */
    private List<TabDetailPagerBean.DataEntity.TopnewsData> topnews;
    /**
     * 新闻列表数据集合
     */
    private List<TabDetailPagerBean.DataEntity.NewsData> news;
    /**
     * 下一页的联网路径
     */
    private String moreUrl;
    /**
     * 是否加载更多
     */
    private boolean isLoadMore = false;
    private InternalHandler internalHandler;

    public TabDetailPager(Context context, NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(100), DensityUtil.dip2px(100))
                .setRadius(DensityUtil.dip2px(5))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.news_pic_default)
                .setFailureDrawableId(R.drawable.news_pic_default)
                .build();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.tabdetail_pager, null);
        listview = (RefreshListview ) view.findViewById(R.id.listview);


        View topNewsView = View.inflate(context,R.layout.topnews,null);
        viewpager = (HorizontalScrollViewPager ) topNewsView.findViewById(R.id.viewpager);
        tv_title = (TextView) topNewsView.findViewById(R.id.tv_title);
        ll_point_group = (LinearLayout) topNewsView.findViewById(R.id.ll_point_group);

        //把顶部轮播图部分视图，以头的方式添加到ListView中
//        listview.addHeaderView(topNewsView);

        listview.addTopNewsView(topNewsView);

        //设置监听下拉刷新
        listview.setOnRefreshListener(new MyOnRefreshListener());

        //设置ListView的item的点击监听
        listview.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            int realPosition = position - 1;
            TabDetailPagerBean.DataEntity.NewsData newsData = news.get(realPosition);
//            Toast.makeText(context, "newsData==id=="+newsData.getId()+",newsData_title=="+newsData.getTitle(), Toast.LENGTH_SHORT).show();

            LogUtil.e("newsData==id=="+newsData.getId()+",newsData_title=="+newsData.getTitle()+",url==="+newsData.getUrl());
            //1,取出保存的id集合
            String idArray = CacheUtils.getString(context, READ_ARRAY_ID);//"3511,"
            //2，判断是否存在，如果不存在，才保存，并且刷新适配器
            if(!idArray.contains(newsData.getId()+"")){//3512

                CacheUtils.putString(context,READ_ARRAY_ID,idArray+newsData.getId()+",");//"3511,3512,"

                //刷新适配器
                adapter.notifyDataSetChanged();//getCount-->getView

            }

            //跳转到新闻浏览页面
            Intent intent = new Intent(context,NewsDetailActivity.class);
            intent.putExtra("url",Constants.BASE_URL+newsData.getUrl());
            context.startActivity(intent);


        }
    }

    class MyOnRefreshListener implements RefreshListview.OnRefreshListener {

        @Override
        public void onPullDownRefresh() {
//            Toast.makeText(context, "下拉刷新被回调了", Toast.LENGTH_SHORT).show();
            getDataFromNet();
        }

        @Override
        public void onLoadMore() {
//
            if(TextUtils.isEmpty(moreUrl)){
                //没有更多数据
                Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
                listview.onRefreshFinish(false);
            }else{
                getMoreDataFromNet();
            }

        }
    }

    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(moreUrl);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                LogUtil.e("加载更多联网成功=="+result);
                listview.onRefreshFinish(false);
                //把这个放在前面
                isLoadMore = true;
                //解析数据
                processData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("加载更多联网失败onError=="+ex.getMessage());
                listview.onRefreshFinish(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("加载更多联网onCancelled"+cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("加载更多联网onFinished");
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL + childrenData.getUrl();
        //把之前缓存的数据取出
        String saveJson = CacheUtils.getString(context, url);
        if (!TextUtils.isEmpty(saveJson)) {
            //解析数据和处理显示数据
            processData(saveJson);
        }
//        LogUtil.e(childrenData.getTitle()+"的联网地址=="+url);
        //联网请求数据
        getDataFromNet();
    }

    private void getDataFromNet() {
        prePosition = 0;
        LogUtil.e("url地址==="+url);
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //缓存数据
                CacheUtils.putString(context, url, result);
                LogUtil.e(childrenData.getTitle() + "-页面数据请求成功==" + result);
                //解析和处理显示数据
                processData(result);

                //隐藏下拉刷新控件-重写显示数据，更新时间
                listview.onRefreshFinish(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(childrenData.getTitle() + "-页面数据请求失败==" + ex.getMessage());
                //隐藏下拉刷新控件 - 不更新时间，只是隐藏
                listview.onRefreshFinish(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e(childrenData.getTitle() + "-页面数据请求onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e(childrenData.getTitle() + "-onFinished==");
            }
        });
    }

    /**
     * 之前点高亮显示的位置
     */
    private int prePosition;

    private void processData(String json) {
        TabDetailPagerBean bean = parsedJson(json);
        LogUtil.e(childrenData.getTitle() + "解析成功==" + bean.getData().getNews().get(0).getTitle());

        moreUrl = "";
        if(TextUtils.isEmpty(bean.getData().getMore())){
            moreUrl = "";
        }else{
            moreUrl = Constants.BASE_URL + bean.getData().getMore();
        }

        LogUtil.e("加载更多的地址==="+moreUrl);
        //默认和加载更多
        if(!isLoadMore){
            //默认
            //顶部轮播图数据
            topnews = bean.getData().getTopnews();
            //设置ViewPager的适配器
            viewpager.setAdapter(new TabDetailPagerTopNewsAdapter());


            //添加红点
            addPoint();

            //监听页面的改变，设置红点变化和文本变化
            viewpager.addOnPageChangeListener(new MyOnPageChangeListener());
            tv_title.setText(topnews.get(prePosition).getTitle());

            //准备ListView对应的集合数据
            news = bean.getData().getNews();
            //设置ListView的适配器
            adapter = new TabDetailPagerListAdapter();
            listview.setAdapter(adapter);
        }else{
            //加载更多
            isLoadMore = false;
            //添加到原来的集合中
            news.addAll(bean.getData().getNews());
            //刷新适配器
            adapter.notifyDataSetChanged();


        }



        //发消息每隔4000切换一次ViewPager页面
        if(internalHandler == null){
            internalHandler = new InternalHandler();
        }

        //是把消息队列所有的消息和回调移除
        internalHandler.removeCallbacksAndMessages(null);
        internalHandler.postDelayed(new MyRunnable(),4000);



    }

    class InternalHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //切换ViewPager的下一个页面
            int item = (viewpager.getCurrentItem()+1)%topnews.size();
            viewpager.setCurrentItem(item);
            internalHandler.postDelayed(new MyRunnable(), 4000);

        }
    }

    class MyRunnable implements  Runnable{

        @Override
        public void run() {
            internalHandler.sendEmptyMessage(0);
        }
    }

    class  TabDetailPagerListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView ==null){
                convertView = View.inflate(context,R.layout.item_tabdetail_pager,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

                convertView.setTag(viewHolder);

            }else{
                viewHolder = (ViewHolder) convertView.getTag();

            }

            //根据位置得到数据
            TabDetailPagerBean.DataEntity.NewsData newsData = news.get(position);
            String imageUrl = Constants.BASE_URL+ newsData.getListimage();
            //请求图片XUtils3
//            x.image().bind( viewHolder.iv_icon,imageUrl,imageOptions);
            //请求图片使用glide
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.news_pic_default)
                    .error(R.drawable.news_pic_default)
                    .into(viewHolder.iv_icon);
            //设置标题
            viewHolder.tv_title.setText(newsData.getTitle());

            //设置更新时间
            viewHolder.tv_time.setText(newsData.getPubdate());


            String idArray = CacheUtils.getString(context,READ_ARRAY_ID);
            if(idArray.contains(newsData.getId()+"")){
                //设置灰色
                viewHolder.tv_title.setTextColor(Color.GRAY);
            }else{
                //设置黑色
                viewHolder.tv_title.setTextColor(Color.BLACK);
            }




            return convertView;
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_time;
    }

    private void addPoint() {
        ll_point_group.removeAllViews();//移除所有的红点
        for (int i = 0; i < topnews.size(); i++) {

            ImageView imageView = new ImageView(context);
            //设置背景选择器
            imageView.setBackgroundResource(R.drawable.point_selector);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(5),DensityUtil.dip2px(5));

            if(i==0){
                imageView.setEnabled(true);
            }else{
                imageView.setEnabled(false);
                params.leftMargin = DensityUtil.dip2px(8);
            }


            imageView.setLayoutParams(params);

            ll_point_group.addView(imageView);

        }
    }



    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //1.设置文本
            tv_title.setText(topnews.get(position).getTitle());
            //2.对应页面的点高亮-红色
             //把之前的变成灰色
            ll_point_group.getChildAt(prePosition).setEnabled(false);
            //把当前设置红色
            ll_point_group.getChildAt(position).setEnabled(true);

            prePosition = position;

        }

        private  boolean isDragging = false;
        @Override
        public void onPageScrollStateChanged(int state) {

            if(state ==ViewPager.SCROLL_STATE_DRAGGING){//拖拽
                isDragging = true;
                LogUtil.e("拖拽");
                //拖拽要移除消息
                internalHandler.removeCallbacksAndMessages(null);
            }else if(state ==ViewPager.SCROLL_STATE_SETTLING&&isDragging){//惯性
                //发消息
                LogUtil.e("惯性");
                isDragging = false;
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(),4000);

            }else if(state ==ViewPager.SCROLL_STATE_IDLE&&isDragging){//静止状态
                //发消息
                LogUtil.e("静止状态");
                isDragging = false;
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(),4000);
            }

        }
    }

    class TabDetailPagerTopNewsAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            //设置图片默认北京
            imageView.setBackgroundResource(R.drawable.home_scroll_default);
            //x轴和Y轴拉伸
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //把图片添加到容器(ViewPager)中
            container.addView(imageView);

            TabDetailPagerBean.DataEntity.TopnewsData topnewsData = topnews.get(position);
            //图片请求地址
            String imageUrl = Constants.BASE_URL + topnewsData.getTopimage();

            //联网请求图片
            x.image().bind(imageView, imageUrl,imageOptions);

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN://按下
                            LogUtil.e("按下");
                            //是把消息队列所有的消息和回调移除
                            internalHandler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_UP://离开
                            LogUtil.e("离开");
                            //是把消息队列所有的消息和回调移除
                            internalHandler.removeCallbacksAndMessages(null);
                            internalHandler.postDelayed(new MyRunnable(), 4000);
                            break;
//                        case MotionEvent.ACTION_CANCEL://取消
//                            LogUtil.e("取消");
//                            //是把消息队列所有的消息和回调移除
//                            internalHandler.removeCallbacksAndMessages(null);
//                            internalHandler.postDelayed(new MyRunnable(), 4000);
//                            break;
                    }
                    return true;
                }
            });

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }

    private TabDetailPagerBean parsedJson(String json) {
        return new Gson().fromJson(json, TabDetailPagerBean.class);
    }
}
