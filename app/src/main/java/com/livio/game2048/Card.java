package com.livio.game2048;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;

/**
 * 一个小四方形包括了TextView
 */
public class Card extends FrameLayout {

    private int num = 0;

    private CircleLabel label;

    // 背景颜色
    private int color = 0x33ffffff;

    public Card(@NonNull Context context) {
        super(context);
        label = new CircleLabel(getContext(), this);
        label.setGravity(Gravity.CENTER);
        label.setTextSize(25f);
        label.setTextColor(Color.rgb(124, 115, 106));
        LayoutParams params = new LayoutParams(-1, -1);
        params.setMargins(10, 10, 0, 0);
        addView(label, params);
        setNum(0);
    }


    public void setColor(int color) {
        this.color = color;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
        if (num <= 0) {
            label.setText("");
        } else {
            label.setText(num + "");
        }
    }

    public int getColor() {
        return color;
    }

    public boolean equals(Card o) {
        return getNum() == o.getNum();
    }

}
