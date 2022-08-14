package com.livio.game2048;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * 重写带字的小圆形
 */
public class CircleLabel extends androidx.appcompat.widget.AppCompatTextView {

    private Card card;

    public CircleLabel(Context context, Card card) {
        super(context);
        this.card = card;
    }

    @Override
    public void draw(Canvas canvas) {

        float cx = this.getX() + this.getWidth() / 2 - 6;
        float cy = this.getY() + this.getHeight() / 2 - 6;
        float radius = (float) (this.getWidth() / 2.3);
        Paint paint = getPaint();
        paint.setColor(getColor());
        canvas.drawCircle(cx, cy, radius, paint);
        super.draw(canvas);

//        //涂红色
//        canvas.drawColor(Color.RED);
//        Paint paint=getPaint();
//        //画笔设置为黄色
//        paint.setColor(Color.YELLOW);
//        //画实心圆
//        canvas.drawCircle(getWidth()/2, getHeight()/2, 30, paint);
    }

    /**
     * 返回根据card中的数字返回颜色信息
     *
     * @return
     */
    private int getColor() {
        if (card == null) {
            return Color.YELLOW;
        }
        return card.getColor();
    }


}
