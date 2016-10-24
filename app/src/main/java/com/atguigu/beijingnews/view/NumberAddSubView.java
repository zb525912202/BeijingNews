package com.atguigu.beijingnews.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.TintTypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atguigu.beijingnews.R;

/**
 * 作用：自定义数子添加减少按钮
 */
public class NumberAddSubView extends LinearLayout implements View.OnClickListener {

    private Button btn_sub;
    private TextView tv_value;
    private Button btn_add;
    private int value = 1;
    private int minValue = 1;
    private int maxValue = 10;

    public int getValue() {
         String valueStr =  tv_value.getText().toString().trim();//文本的内容
        if(!TextUtils.isEmpty(valueStr)){
            value = Integer.valueOf(valueStr);
        }
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        tv_value.setText(value+"");
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public NumberAddSubView(Context context) {
        this(context, null);
    }

    public NumberAddSubView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public NumberAddSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //要让布局和当前类形成一个整体
        View.inflate(context, R.layout.number_add_sub_view,this);
        btn_sub = (Button) findViewById(R.id.btn_sub);
        tv_value = (TextView) findViewById(R.id.tv_value);
        btn_add = (Button) findViewById(R.id.btn_add);

        getValue();

        //设置点击事件
        btn_sub.setOnClickListener(this);
        btn_add.setOnClickListener(this);

        if(attrs != null){

            TintTypedArray typedArray = TintTypedArray.obtainStyledAttributes(context,attrs,R.styleable.NumberAddSubView);

            int value = typedArray.getInt(R.styleable.NumberAddSubView_value, 0);
            if(value >0){
                setValue(value);
            }

            int minValue = typedArray.getInt(R.styleable.NumberAddSubView_minValue,0);
            if(minValue >0){
                setMinValue(minValue);
            }

            int maxValue = typedArray.getInt(R.styleable.NumberAddSubView_maxValue,0);
            if(maxValue >0){
                setMaxValue(maxValue);
            }

            Drawable numberAddSubBackground = typedArray.
            getDrawable(R.styleable.NumberAddSubView_numberAddSubBackground);
            if(numberAddSubBackground != null){
                setBackground(numberAddSubBackground);
            }

            Drawable numberSubBackground = typedArray.
                    getDrawable(R.styleable.NumberAddSubView_numberSubBackground);
            if(numberSubBackground != null){
                btn_sub.setBackground(numberSubBackground);
            }


            Drawable numberAddBackground = typedArray.
                    getDrawable(R.styleable.NumberAddSubView_numberAddBackground);
            if(numberAddBackground != null){
                btn_add.setBackground(numberAddBackground);
            }


        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sub://减
                subNumber();
                if(listener!= null){
                    listener.onButtonSub(v,value);
                }
                break;
            case R.id.btn_add://加
                addNumber();
                if(listener!= null){
                    listener.onButtonAdd(v, value);
                }
                break;
        }
    }

    private void addNumber() {
        if(value < maxValue){
            value += 1;
        }
        setValue(value);
    }

    /**
     * 减
     */
    private void subNumber() {
        if(value > minValue){
//            value = value -1;
            value -= 1;
        }

        setValue(value);
    }

    /**
     * 监听数字增加减少控件
     */
    public interface OnNumberClickListener{

        /**
         * 当减少按钮被点击的时候回调
         * @param view
         * @param value
         */
        public void onButtonSub(View view, int value);

        /**
         * 当增加按钮被点击的时候回调
         * @param view
         * @param value
         */
        public void onButtonAdd(View view, int value);


    }

    private OnNumberClickListener listener;

    /**
     * 设置监听数子按钮
     * @param listener
     */
    public void setOnNumberClickListener(OnNumberClickListener listener) {
        this.listener = listener;
    }
}
