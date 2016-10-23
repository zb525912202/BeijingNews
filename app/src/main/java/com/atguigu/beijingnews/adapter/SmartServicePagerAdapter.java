package com.atguigu.beijingnews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.domain.ShoppingCart;
import com.atguigu.beijingnews.domain.SmartServicePagerBean;
import com.atguigu.beijingnews.utils.CartProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * 作者：尚硅谷-杨光福 on 2016/8/29 10:40
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：商城热卖的适配器
 */
public class SmartServicePagerAdapter extends RecyclerView.Adapter<SmartServicePagerAdapter.ViewHolder> {

    private final Context context;
    private final List<SmartServicePagerBean.Wares> datas;

    /**
     * 数据存储类
     */
    private CartProvider cartProvider;

    public SmartServicePagerAdapter(Context context,List<SmartServicePagerBean.Wares> datas){
        this.context = context;
        this.datas = datas;
        cartProvider = new CartProvider(context);

    }


    /**
     * getView中创建视图和创建ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.item_smartserice_pager,null);
        return new ViewHolder(itemView);
    }

    /**
     * getView方法中绑定数据部分代码
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //1.根据位置得到对应的数据
        final SmartServicePagerBean.Wares wares = datas.get(position);

        //2.绑定数据
        Glide.with(context)
                    .load(wares.getImgUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.news_pic_default)//真正加载的默认图片
                    .error(R.drawable.news_pic_default)//失败的默认图片
                    .into(holder.iv_icon);

        holder.tv_name.setText(wares.getName());
        holder.tv_price.setText("￥"+wares.getPrice());

        //设置点击事件在这里可以
        holder.btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(context, "wares.price=="+wares.getPrice(), Toast.LENGTH_SHORT).show();
                //把商品转换成ShoppingCart
                ShoppingCart cart = cartProvider.conversion(wares);

                cartProvider.addData(cart);
                Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * getCount();返回数据的总条数
     * @return
     */
    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * 清除数据
     */
    public void clearData() {
        datas.clear();
        notifyItemRangeChanged(0,datas.size());
    }

    /**
     * 根据指定位置添加数据
     * @param position
     * @param data
     */
    public void addData(int position, List<SmartServicePagerBean.Wares> data) {
        if(data != null && data.size() >0){
            datas.addAll(position,data);
            notifyItemRangeChanged(position,datas.size());
        }

    }

    /**
     * 得到总的条数
     * @return
     */
    public int getDataCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView iv_icon;
        private TextView tv_name;
        private TextView tv_price;
        private Button btn_buy;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            btn_buy = (Button) itemView.findViewById(R.id.btn_buy);

            //设置点击事件在这里可以
        }
    }
}
