package com.atguigu.beijingnews.base;

import android.content.Context;
import android.view.View;

/**
 * 作用：详情页面的基类
 */
public abstract class MenuDetaiBasePager {

    /**
     * 上下文
     */
    public final Context context;

    /**
     * 代表各个详情页面的视图
     */
    public View rootView;

    public MenuDetaiBasePager(Context context){
        this.context = context;
        rootView = initView();
    }

    /**
     * 抽象方法，强制孩子实现该方法，每个页面实现不同的效果
     * @return
     */
    public abstract View initView() ;

    /**
     * 子页面需要绑定数据，联网请求数据等的时候，重写该方法
     */
    public void initData(){

    }
}
