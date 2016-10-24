package com.atguigu.beijingnews.fragment;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.activity.MainActivity;
import com.atguigu.beijingnews.base.BaseFragment;
import com.atguigu.beijingnews.domain.NewsCenterPagerBean2;
import com.atguigu.beijingnews.pager.NewsCenterPager;
import com.atguigu.beijingnews.utils.DensityUtil;
import com.atguigu.beijingnews.utils.LogUtil;

import java.util.List;

/**
 * 作用：左侧菜单的Fragment
 */
public class LeftmenuFragment extends BaseFragment {


    private List<NewsCenterPagerBean2.DetailPagerData> data;

    private LeftmenuFragmentAdapter adapter;

    private ListView listView;

    /**
     * 点击的位置
     */
    private int prePosition;

    @Override
    public View initView() {
        LogUtil.e("左侧菜单视图被初始化了");
        listView = new ListView(context);
        listView.setPadding(0, DensityUtil.dip2px(context,40),0,0);
        listView.setDividerHeight(0);//设置分割线高度为0
        listView.setCacheColorHint(Color.TRANSPARENT);

        //设置按下listView的item不变色
        listView.setSelector(android.R.color.transparent);

        //设置item的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1.记录点击的位置，变成红色
                prePosition = position;
                adapter.notifyDataSetChanged();//getCount()-->getView

                //2.把左侧菜单关闭
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();//关<->开

                //3.切换到对应的详情页面：新闻详情页面，专题详情页面，图组详情页面，互动详情页面
                swichPager(prePosition);

            }
        });

        return listView;
    }

    /**
     * 根据位置切换不同详情页面
     * @param position
     */
    private void swichPager(int position) {
        MainActivity mainActivity = (MainActivity) context;
        ContentFragment contentFragment = mainActivity.getContentFragment();
        NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();
        newsCenterPager.swichPager(position);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("左侧菜单数据被初始化了");
    }

    /**
     * 接收数据
     * @param data
     */
    public void setData(List<NewsCenterPagerBean2.DetailPagerData> data) {
        this.data = data;
        for(int i=0;i<data.size();i++){
            LogUtil.e("title=="+data.get(i).getTitle());
        }

        //设置适配器
        adapter   = new LeftmenuFragmentAdapter();
        listView.setAdapter(adapter);

       //设置默认页面
        swichPager(prePosition);

    }


    class LeftmenuFragmentAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) View.inflate(context, R.layout.item_leftmenu,null);
            textView.setText(data.get(position).getTitle());
//            if(position==prePosition){
//                //设置红色
//                textView.setEnabled(true);
//
//            }else{
//                textView.setEnabled(false);
//            }

            textView.setEnabled(position==prePosition);
            return textView;
        }
        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


    }
}
