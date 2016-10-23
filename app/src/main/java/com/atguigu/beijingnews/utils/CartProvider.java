package com.atguigu.beijingnews.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.atguigu.beijingnews.domain.ShoppingCart;
import com.atguigu.beijingnews.domain.SmartServicePagerBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：尚硅谷-杨光福 on 2016/8/29 15:31
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：数据存储类，存储数据；存储数据--把集合转成String类型存储（Gson）；取数据--把String转换成列表数据(Gson)
 */
public class CartProvider {

    public static final String JSON_CART = "json_cart";
    private final Context context;
    /**
     * SparseArray替换HashMap,性能好于HashMap
     */
    private SparseArray<ShoppingCart> datas;

    public CartProvider(Context context) {
        this.context = context;
        datas = new SparseArray<>(10);
        listToSparse();
    }

    private void listToSparse() {
        List<ShoppingCart> carts = getAllData();
        if (carts != null && carts.size() > 0) {

            for (int i = 0; i < carts.size(); i++) {

                ShoppingCart cart = carts.get(i);
                datas.put(cart.getId(), cart);
            }

        }
    }

    /**
     * 得到所有数据
     *
     * @return
     */
    public List<ShoppingCart> getAllData() {
        return getDataFromLocal();
    }

    /**
     * 从本地获取json数据，并且通过Gson解析成List列表数据
     *
     * @return
     */
    private List<ShoppingCart> getDataFromLocal() {
        List<ShoppingCart> carts = new ArrayList<>();
        //从本地获取缓存的数据
        String saveJson = CacheUtils.getString(context, JSON_CART);
        if (!TextUtils.isEmpty(saveJson)) {
            //通过Gson把数据转换成List列表
            carts = new Gson().fromJson(saveJson, new TypeToken<List<ShoppingCart>>() {
            }.getType());
        }
        return carts;
    }

    /**
     * 增加数据
     *
     * @param cart
     */
    public void addData(ShoppingCart cart) {

        //1.添加数据
        ShoppingCart tempCart = datas.get(cart.getId());
        if (tempCart != null) {
            //在列表中已经存在该条数据
            tempCart.setCount(tempCart.getCount()+1);
        }else{
            tempCart = cart;
            tempCart.setCount(1);
        }
        datas.put(tempCart.getId(),tempCart);


        //2.保存数据
        commit();

    }

    /**
     * 保存数据
     */
    private void commit() {
        //1.把SparseArray转换成List
       List<ShoppingCart> carts =  parsesToList();

        //2.用Gson把List转换成String
        String json = new Gson().toJson(carts);

        //3.保存数据
        CacheUtils.putString(context, JSON_CART, json);



    }

    /**
     * 从parses的数据转换成List列表数据
     * @return
     */
    private List<ShoppingCart> parsesToList() {
        List<ShoppingCart> carts = new ArrayList<>();
        if(datas != null && datas.size() >0){

            for (int i=0;i<datas.size();i++){

                ShoppingCart cart = datas.valueAt(i);
                carts.add(cart);
            }
        }
        return carts;
    }


    /**
     * 删除数据
     *
     * @param cart
     */
    public void deleteData(ShoppingCart cart) {

        //1.删除数据
        datas.delete(cart.getId());

        //2.保存数据
        commit();

    }


    /**
     * 修改数据
     * @param cart
     */
    public void updataData(ShoppingCart cart) {

        //1.修改-count
        datas.put(cart.getId(), cart);

        //2.保存数据
        commit();
    }

    /**
     *  把商品Wares转换成ShoppingCart
     */

    public ShoppingCart conversion(SmartServicePagerBean.Wares wares) {
        ShoppingCart cart = new ShoppingCart();
        cart.setImgUrl(wares.getImgUrl());
        cart.setDescription(wares.getDescription());
        cart.setName(wares.getName());
        cart.setId(wares.getId());
        cart.setSale(wares.getSale());
        cart.setPrice(wares.getPrice());
        return cart;
    }
}
