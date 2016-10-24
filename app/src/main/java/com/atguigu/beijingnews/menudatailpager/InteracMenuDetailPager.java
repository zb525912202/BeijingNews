package com.atguigu.beijingnews.menudatailpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.base.MenuDetaiBasePager;
import com.atguigu.beijingnews.domain.NewsCenterPagerBean2;
import com.atguigu.beijingnews.domain.PhotosMenuDetailPagerBean;
import com.atguigu.beijingnews.utils.BitmapCacheUtils;
import com.atguigu.beijingnews.utils.CacheUtils;
import com.atguigu.beijingnews.utils.Constants;
import com.atguigu.beijingnews.utils.LogUtil;
import com.atguigu.beijingnews.utils.NetCacheUtils;
import com.atguigu.beijingnews.volley.VolleyManager;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 作用：互动详情页面
 */
public class InteracMenuDetailPager extends MenuDetaiBasePager {

    private PhotosMenuDetailPagerAdapter adapter;

    private final NewsCenterPagerBean2.DetailPagerData detailPagerData;
    @ViewInject(R.id.listview)
    private ListView listview;

    @ViewInject(R.id.gridview)
    private GridView gridview;

    private String url;
    private List<PhotosMenuDetailPagerBean.DataEntity.NewsEntity> news;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NetCacheUtils.SUCESS://图片请求成功
                    int position = msg.arg1;
                    Bitmap bitmap = (Bitmap) msg.obj;

                    if(listview.isShown()){
                        ImageView iv_icon = (ImageView) listview.findViewWithTag(position);
                        if(iv_icon != null && bitmap!= null){
                            iv_icon.setImageBitmap(bitmap);
                        }
                    }

                    if(gridview.isShown()){
                        ImageView iv_icon = (ImageView) gridview.findViewWithTag(position);
                        if(iv_icon != null && bitmap!= null){
                            iv_icon.setImageBitmap(bitmap);
                        }
                    }

                    LogUtil.e("请求图片成功=="+position);

                    break;
                case NetCacheUtils.FAIL://图片请求失败
                    position = msg.arg1;
                    LogUtil.e("请求图片失败=="+position);
                    break;
            }
        }
    };
    /***
     * 图片三级缓存工具类：
     */
    private BitmapCacheUtils bitmapCacheUtils;

    public InteracMenuDetailPager(Context context, NewsCenterPagerBean2.DetailPagerData detailPagerData) {
        super(context);
        this.detailPagerData = detailPagerData;
        bitmapCacheUtils = new BitmapCacheUtils(handler);

    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.photos_menudetail_pager, null);
        x.view().inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("互动详情页面数据被初始化了..");
        url = Constants.BASE_URL + detailPagerData.getUrl();
        String saveJson = CacheUtils.getString(context, url);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
        getDataFromNet();
    }


    private void getDataFromNet() {
        //String请求
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                LogUtil.e("使用Volley联网请求成功==" + result);
                //缓存数据
                CacheUtils.putString(context, url, result);

                processData(result);
                //设置适配器
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("使用Volley联网请求失败==" + volleyError.getMessage());
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String parsed = new String(response.data, "UTF-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };

        //添加到队列
        VolleyManager.getRequestQueue().add(request);
    }


    /**
     * 解析和显示数据
     *
     * @param json
     */
    private void processData(String json) {

        PhotosMenuDetailPagerBean bean = parsedJson(json);
        LogUtil.e("图组解析成功==" + bean.getData().getNews().get(0).getTitle());

        isShowListView = true;
        //设置适配器
        news = bean.getData().getNews();
        adapter = new PhotosMenuDetailPagerAdapter();
        listview.setAdapter(adapter);
    }

    /**
     * true,显示ListView，隐藏GridView
     * false,显示GridView,隐藏ListView
     */
    private boolean isShowListView = true;

    public void swichListAndGrid(ImageButton ib_swich_list_grid) {
        if(isShowListView){
            isShowListView = false;
            //显示GridView,隐藏ListView
            gridview.setVisibility(View.VISIBLE);
            adapter = new PhotosMenuDetailPagerAdapter();
            gridview.setAdapter(adapter);
            listview.setVisibility(View.GONE);
            //按钮显示--ListView
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_list_type);


        }else{
            isShowListView = true;
            //显示ListView，隐藏GridView
            listview.setVisibility(View.VISIBLE);
            adapter = new PhotosMenuDetailPagerAdapter();
            listview.setAdapter(adapter);
            gridview.setVisibility(View.GONE);
            //按钮显示--GridView
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_grid_type);
        }
    }


    class PhotosMenuDetailPagerAdapter extends BaseAdapter {
        private DisplayImageOptions options;

        public PhotosMenuDetailPagerAdapter(){
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.home_scroll_default)
                    .showImageForEmptyUri(R.drawable.home_scroll_default)
                    .showImageOnFail(R.drawable.home_scroll_default)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new RoundedBitmapDisplayer(10))//设置矩形圆角
                    .build();
        }

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
            if(convertView == null){
                convertView = View.inflate(context,R.layout.item_photos_menudetail_pager,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //根据位置得到对应的数据
            PhotosMenuDetailPagerBean.DataEntity.NewsEntity newsEntity = news.get(position);
            viewHolder.tv_title.setText(newsEntity.getTitle());
            String imageUrl = Constants.BASE_URL+newsEntity.getSmallimage();
            //1.使用Volley请求图片-设置图片了
//            loaderImager(viewHolder, imageUrl );
            //2.使用自定义的三级缓存请求图片
//            viewHolder.iv_icon.setTag(position);
//            Bitmap bitmap = bitmapCacheUtils.getBitmap(imageUrl,position);//内存或者本地
//            if(bitmap != null){
//                viewHolder.iv_icon.setImageBitmap(bitmap);
//            }
            //2.使用Picasso请求网络图片
//            Picasso.with(context)
//                    .load(imageUrl)
//                    .placeholder(R.drawable.home_scroll_default)
//                    .error(R.drawable.home_scroll_default)
//                    .into(viewHolder.iv_icon);

            //3.使用Glide请求图片

//            Glide.with(context)
//                    .load(imageUrl)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.home_scroll_default)//真正加载的默认图片
//                    .error(R.drawable.home_scroll_default)//失败的默认图片
//                    .into(viewHolder.iv_icon);

            //4.使用ImageLoader加载图片
            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(imageUrl, viewHolder.iv_icon, options);

            return convertView;
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_title;
    }


    /**
     *
     * @param viewHolder
     * @param imageurl
     */
    private void loaderImager(final ViewHolder viewHolder, String imageurl) {

        //设置tag
        viewHolder.iv_icon.setTag(imageurl);
        //直接在这里请求会乱位置
        ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer != null) {

                    if (viewHolder.iv_icon != null) {
                        if (imageContainer.getBitmap() != null) {
                            //设置图片
                            viewHolder.iv_icon.setImageBitmap(imageContainer.getBitmap());
                        } else {
                            //设置默认图片
                            viewHolder.iv_icon.setImageResource(R.drawable.home_scroll_default);
                        }
                    }
                }
            }
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //如果出错，则说明都不显示（简单处理），最好准备一张出错图片
                viewHolder.iv_icon.setImageResource(R.drawable.home_scroll_default);
            }
        };
        VolleyManager.getImageLoader().get(imageurl, listener);
    }

    private PhotosMenuDetailPagerBean parsedJson(String json) {
        return new Gson().fromJson(json, PhotosMenuDetailPagerBean.class);
    }
}
