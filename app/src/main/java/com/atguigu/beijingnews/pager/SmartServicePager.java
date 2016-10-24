package com.atguigu.beijingnews.pager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.adapter.SmartServicePagerAdapter;
import com.atguigu.beijingnews.base.BasePager;
import com.atguigu.beijingnews.domain.SmartServicePagerBean;
import com.atguigu.beijingnews.utils.CacheUtils;
import com.atguigu.beijingnews.utils.Constants;
import com.atguigu.beijingnews.utils.LogUtil;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * 作用：智慧服务
 */
public class SmartServicePager extends BasePager {



    private MaterialRefreshLayout refreshLayout;
    private RecyclerView recyclerview;
    private ProgressBar pb_loading;
    private SmartServicePagerAdapter adapter;
    private String url;

    /**
     * 默认状态
     */
    private static final int STATE_NORMAL = 1;
    /**
     * 下拉刷新状态
     */
    private static final int STATE_REFRES = 2;

    /**
     * 上拉刷新（加载更多）状态
     */
    private static final int STATE_LOADMORE = 3;

    /**
     * 默认是正常状态
     */
    private int state = STATE_NORMAL;
    /**
     * 每页的数据的个数
     */
    private int pageSize = 10;
    /**
     * 当前页
     */
    private int curPage = 1;

    /**
     * 总页数
     */
    private int totalPage = 1;
    /**
     * 商品列表数据
     */
    private List<SmartServicePagerBean.Wares> datas;

    public SmartServicePager(Context context) {
        super(context);

    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("智慧服务数据被初始化了..");
        //1.设置标题
        tv_title.setText("商城热卖");
        //2.联网请求，得到数据，创建视图
        View view = View.inflate(context, R.layout.smartservice_pager, null);
        refreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refreshLayout);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        //3.把子视图添加到BasePager的FrameLayout中
        if(fl_content != null){
            fl_content.removeAllViews();
        }
        fl_content.addView(view);
        //4.绑定数据
//        textView.setText("智慧服务内容");

        initRefresh();

        setRequesParams();
        getDataFromNet();

    }

    private void initRefresh() {
        //设置下拉和上拉刷新
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            /**
             * 下拉刷新
             * @param materialRefreshLayout
             */
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                state = STATE_REFRES;
                curPage = 1;
                url = Constants.WARES_HOT_URL + pageSize + "&curPage=" + curPage;
                getDataFromNet();

            }

            /**
             * 上拉刷新（加载更多）
             * @param materialRefreshLayout
             */
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                if(curPage < totalPage){
                    state = STATE_LOADMORE;
                    curPage += 1;
                    url = Constants.WARES_HOT_URL + pageSize + "&curPage=" + curPage;
                    getDataFromNet();
                }else{
                    Toast.makeText(context, "已经到底了", Toast.LENGTH_SHORT).show();
                    refreshLayout.finishRefreshLoadMore();//把加载更多的ui还原
                }
              
            }
        });
    }

    private void getDataFromNet() {
        //获取保持的数据
        String json = CacheUtils.getString(context, Constants.WARES_HOT_URL);
        if (!TextUtils.isEmpty(json)) {
            processData(json);
        }
        //使用Okhttp第三方封装库请求网络
        OkHttpUtils
                .get()
                .url(url)//每次拼接最新的连接
                .id(100)
                .build()
                .execute(new MyStringCallback());
    }

    private void setRequesParams() {
        state = STATE_NORMAL;
        curPage = 1;
        url = Constants.WARES_HOT_URL + pageSize + "&curPage=" + curPage;
    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onResponse(String response, int id) {
            LogUtil.e("使用okhttp联网请求成功==" + response);
            //缓存数据
            CacheUtils.putString(context, Constants.WARES_HOT_URL, response);

            processData(response);
            //设置适配器

            switch (id) {
                case 100:
//                    Toast.makeText(context, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
//                    Toast.makeText(context, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            LogUtil.e("使用okhttp联网请求失败==" + e.getMessage());
        }


    }

    /**
     * 解析数据和显示数据
     *
     * @param response
     */
    private void processData(String response) {
        SmartServicePagerBean bean = parsedJson(response);
        datas = bean.getList();
        curPage = bean.getCurrentPage();
        totalPage = bean.getTotalPage();

        LogUtil.e("curPage==" + curPage + ",totalPage==" + totalPage + ",datas(1)==" + datas.get(1).getName());
        showData();


    }

    private void showData() {
        switch (state){
            case STATE_NORMAL://默认
                //显示数据
                //设置适配器
                adapter = new SmartServicePagerAdapter(context,datas);
                recyclerview.setAdapter(adapter);
                //布局管理器
                recyclerview.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
                break;
            case STATE_REFRES://下拉刷新
                //1.把之前的数据清除
                adapter.clearData();
                //2.添加新的数据-刷新
                adapter.addData(0,datas);
                //3.把状态还原
                refreshLayout.finishRefresh();

                break;
            case STATE_LOADMORE://上拉刷新（加载更多）

                //1.把新的数据添加到原来的数据的末尾-刷新
                adapter.addData(adapter.getDataCount(),datas);
                //2.把状态还原
                refreshLayout.finishRefreshLoadMore();

                break;
        }


        pb_loading.setVisibility(View.GONE);
    }

    /**
     * 使用Gson解析商城热卖的json数据
     *
     * @param response
     * @return
     */
    private SmartServicePagerBean parsedJson(String response) {
        return new Gson().fromJson(response, SmartServicePagerBean.class);
    }

}
