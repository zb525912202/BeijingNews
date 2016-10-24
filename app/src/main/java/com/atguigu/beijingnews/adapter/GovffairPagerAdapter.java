package com.atguigu.beijingnews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.beijingnews.R;
import com.atguigu.beijingnews.domain.ShoppingCart;
import com.atguigu.beijingnews.utils.CartProvider;
import com.atguigu.beijingnews.view.NumberAddSubView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Iterator;
import java.util.List;

/**
 * 作用：购物车的适配器
 */
public class GovffairPagerAdapter extends RecyclerView.Adapter<GovffairPagerAdapter.ViewHolder> {

    private final Context context;
    private final List<ShoppingCart> datas;
    private final CheckBox checkbox_all;
    private final TextView tv_total_price;
    private CartProvider cartProvider;

    public GovffairPagerAdapter(Context context, final List<ShoppingCart> datas, final CheckBox checkbox_all, TextView tv_total_price) {
        this.context = context;
        this.datas = datas;
        this.checkbox_all = checkbox_all;
        this.tv_total_price = tv_total_price;
        cartProvider = new CartProvider(context);
        showTotalPrice();
        //设置item的监听
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int positiion) {
                //1.得到对应位置的对象
                ShoppingCart cart = datas.get(positiion);
                //2.勾选状态取反
                cart.setIsCheck(!cart.isCheck());
                //3.状态刷新
                notifyItemChanged(positiion);
                //4.校验全选和非全选-一会做
                checkAll();
                //5.显示总价格
                showTotalPrice();

            }
        });
        checkAll();//校验全选

        //设置点击事件
        checkbox_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //1.得到它是否选中的状态
                boolean isCheck = checkbox_all.isChecked();
                //2.设置全选和非全选
                checkAll_none(isCheck);
                //3.重新计算总价格
                showTotalPrice();

            }
        });
    }

    /**
     * 设置全选和非全选
     * @param isCheck
     */
    public void checkAll_none(boolean isCheck) {
        if(datas != null && datas.size() >0){
            for (int i=0;i<datas.size();i++){
                ShoppingCart cart = datas.get(i);
                cart.setIsCheck(isCheck);
                notifyItemChanged(i);
            }
        }
    }

    /**
     * 校验是否全选
     */
    public void checkAll() {
        if(datas != null && datas.size() >0){
            int number = 0;
            for (int i=0;i<datas.size();i++){
                ShoppingCart cart = datas.get(i);
                if(!cart.isCheck()){//只要有一个没有被选中，就是非全选
                    //没有选中
                    checkbox_all.setChecked(false);//非勾选
                }else{
                    //选中
                    number ++;
                }
            }

            if(number==datas.size()){//选中的个数和集合总数相同
                checkbox_all.setChecked(true);//勾选
            }

        }else{
            checkbox_all.setChecked(false);//非勾选
        }

    }

    /**
     * 显示总价格
     */
    public void showTotalPrice() {
        tv_total_price.setText("合计￥" + getTotalPrice());
    }

    /**
     * 得到购物车选中商品的总价格
     *
     * @return
     */
    public double getTotalPrice() {
        double totalPrice = 0;
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {

                ShoppingCart cart = datas.get(i);

                //只用选择的参与计算
                if (cart.isCheck()) {
                    totalPrice += (cart.getCount() * cart.getPrice());
                }
            }
        }
        return totalPrice;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.item_govaffair_pager, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //1.根据位置得到对应的数据
        final ShoppingCart cart = datas.get(position);
        //2.绑定数据
        Glide.with(context)
                .load(cart.getImgUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.news_pic_default)//真正加载的默认图片
                .error(R.drawable.news_pic_default)//失败的默认图片
                .into(holder.iv_icon);

        holder.tv_name.setText(cart.getName());
        holder.tv_price.setText("￥" + cart.getPrice());
        holder.numberAddSeubView.setValue(cart.getCount());
        holder.checkbox.setChecked(cart.isCheck());//设置CheckBox是否为选中状态

        //设置增加减少按钮的点击监听
        holder.numberAddSeubView.setOnNumberClickListener(new NumberAddSubView.OnNumberClickListener() {
            @Override
            public void onButtonSub(View view, int value) {
                //减
                //1.跟新数据
                cart.setCount(value);//跟新了购买的数量
                cartProvider.updataData(cart);

                //2.重新显示价格
                showTotalPrice();


            }

            @Override
            public void onButtonAdd(View view, int value) {
                //加
                //1.跟新数据
                cart.setCount(value);//跟新了购买的数量
                cartProvider.updataData(cart);//重新跟新到本地

                //2.重新显示价格
                showTotalPrice();


            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * 删除选中的数据
     */
    public void deleteData() {
//        if(datas != null && datas.size() >0){
//            for(int i=0;i<datas.size();i++){
//
//                ShoppingCart cart = datas.get(i);
//
//                if(cart.isCheck()){
//
//                    //1.删除本地缓存的
//                    cartProvider.deleteData(cart);
//
//                    //2.删除当前内存的
//                    datas.remove(cart);
//
//                    //3.刷新数据
//                    notifyItemRemoved(i);
//
//                    i--;
//                }
//            }
//        }

        if(datas != null && datas.size() >0){
            for(Iterator iterator = datas.iterator();iterator.hasNext();){

                ShoppingCart cart = (ShoppingCart) iterator.next();

                if(cart.isCheck()){

                    //这行代码放在前面
                    int position = datas.indexOf(cart);
                    //1.删除本地缓存的
                    cartProvider.deleteData(cart);

                    //2.删除当前内存的
//                    datas.remove(cart);
                    iterator.remove();


                    //3.刷新数据
                    notifyItemRemoved(position);

                }
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkbox;
        private ImageView iv_icon;
        private TextView tv_name;
        private TextView tv_price;
        private NumberAddSubView numberAddSeubView;

        public ViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            numberAddSeubView = (NumberAddSubView) itemView.findViewById(R.id.numberAddSeubView);

            //设置点击某条的监听
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, getLayoutPosition());
                    }

                }
            });
        }
    }

    /**
     * 点击某个的监听
     */
    public interface OnItemClickListener {
        /**
         * 当某个item被点击的时候回调
         *
         * @param view
         * @param positiion
         */
        public void onItemClick(View view, int positiion);
    }

    private OnItemClickListener onItemClickListener;

    /**
     * 设置某条的监听
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
