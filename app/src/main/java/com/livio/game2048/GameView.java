package com.livio.game2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

public class GameView extends GridLayout {
    /**
     * 难度系数（方格数DIFFICULT*DIFFICULT）
     */
    private static final int DIFFICULT = 4;

    private Card[][] cards = new Card[DIFFICULT][DIFFICULT];
    private List<Point> emptyPoint = new ArrayList<Point>();

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameView();
        //没渲染是获取不到高和宽的  这里写死 是有问题的
//        int cardWidth = (Math.min(getWidth(), getHeight()) - 10) / DIFFICULT;
        int cardWidth = (Math.min(1080, 1672) - 10) / DIFFICULT;
        int cardHeight = cardWidth;
        addCards(cardWidth, cardHeight);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int cardWidth = (Math.min(w, h) - 10) / DIFFICULT;
        int cardHeight = cardWidth;
//        addCards(cardWidth, cardHeight);
        startGame();
    }

//    @Override
//    protected void onMeasure(int widthSpec, int heightSpec) {
//        super.onMeasure(widthSpec, heightSpec);
//        final int count = getChildCount();
//        for (int i = 0; i < count; i++) {
//            //这个很重要，没有就不显示
//            getChildAt(i).measure(widthSpec, heightSpec);
//        }
//    }

    private void startGame() {
        for (int i = 0; i < DIFFICULT; i++) {
            for (int j = 0; j < DIFFICULT; j++) {
                cards[i][j].setNum(0);
            }
        }
        changeCardColor();
        MainActivity mainActivity = MainActivity.getMainActivity();
        if (mainActivity != null) {
            mainActivity.clearScore();
        }
        addRandomNum();
        addRandomNum();
    }

    private void addCards(int cardWidth, int cardHeight) {
        Card c;
        for (int i = 0; i < DIFFICULT; i++) {
            for (int j = 0; j < DIFFICULT; j++) {
                c = new Card(getContext());
                c.setNum(0);
                addView(c, cardWidth, cardHeight);
                cards[i][j] = c;

            }
        }

    }

    private void addRandomNum() {
        emptyPoint.clear();
        for (int i = 0; i < DIFFICULT; i++) {
            for (int j = 0; j < DIFFICULT; j++) {
                if (cards[i][j].getNum() <= 0) {
                    emptyPoint.add(new Point(i, j));
                }
            }
        }

        Point p = emptyPoint.get((int) (Math.random() * emptyPoint.size()));
        int num = Math.random() > 0.1 ? 2 : 4;
        cards[p.x][p.y].setNum(num);
    }

    public GameView(Context context) {
        super(context);
        initGameView();
    }

    private void initGameView() {
        setColumnCount(DIFFICULT);
        setBackgroundColor(0xffbbada0);
        setOnTouchListener(new View.OnTouchListener() {

            private float startX, startY, offsetX, offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;
                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            if (offsetX < -5) {
                                // left
                                swipeLeft();
                            } else if (offsetX > 5) {
                                swipeRigth();
                            }

                        } else {
                            if (offsetY < -5) {
                                // up
                                swipeUp();
                            } else if (offsetY > 5) {
                                swipeDown();
                            }
                        }

                    default:
                        break;
                }
                return true;

            }
        });
    }

    private void swipeLeft() {
        boolean merge = false;
        for (int i = 0; i < DIFFICULT; i++) {
            for (int j = 0; j < DIFFICULT; j++) {
                for (int k = j + 1; k < DIFFICULT; k++) {
                    if (cards[i][k].getNum() > 0) {

                        if (cards[i][j].getNum() <= 0) {
                            cards[i][j].setNum(cards[i][k].getNum());
                            cards[i][k].setNum(0);
                            j--;
                            merge = true;
                        } else if (cards[i][j].equals(cards[i][k])) {
                            cards[i][j].setNum(cards[i][j].getNum() * 2);
                            cards[i][k].setNum(0);
                            MainActivity.getMainActivity().addScore(cards[i][j].getNum());
                            merge = true;

                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            changeCardColor();
            checkGameOver();
        }

    }

    private void swipeRigth() {
        boolean merge = false;
        for (int i = 0; i < DIFFICULT; i++) {
            for (int j = DIFFICULT - 1; j >= 0; j--) {
                for (int k = j - 1; k >= 0; k--) {
                    if (cards[i][k].getNum() > 0) {

                        if (cards[i][j].getNum() <= 0) {
                            cards[i][j].setNum(cards[i][k].getNum());
                            cards[i][k].setNum(0);
                            j++;
                            merge = true;
                        } else if (cards[i][j].equals(cards[i][k])) {
                            cards[i][j].setNum(cards[i][j].getNum() * 2);
                            cards[i][k].setNum(0);
                            MainActivity.getMainActivity().addScore(cards[i][j].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            changeCardColor();
            checkGameOver();
        }
    }

    private void swipeUp() {
        boolean merge = false;
        for (int i = 0; i < DIFFICULT; i++) {
            for (int j = 0; j < DIFFICULT; j++) {
                for (int k = j + 1; k < DIFFICULT; k++) {
                    if (cards[k][i].getNum() > 0) {

                        if (cards[j][i].getNum() <= 0) {
                            cards[j][i].setNum(cards[k][i].getNum());
                            cards[k][i].setNum(0);
                            j--;
                            merge = true;
                        } else if (cards[j][i].equals(cards[k][i])) {
                            cards[j][i].setNum(cards[j][i].getNum() * 2);
                            cards[k][i].setNum(0);
                            MainActivity.getMainActivity().addScore(cards[j][i].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            changeCardColor();
            checkGameOver();
        }
    }

    private void swipeDown() {

        boolean merge = false;
        for (int i = DIFFICULT - 1; i >= 0; i--) {
            for (int j = DIFFICULT - 1; j >= 0; j--) {
                for (int k = j - 1; k >= 0; k--) {
                    if (cards[k][i].getNum() > 0) {

                        if (cards[j][i].getNum() <= 0) {
                            cards[j][i].setNum(cards[k][i].getNum());
                            cards[k][i].setNum(0);
                            j++;
                            merge = true;
                        } else if (cards[j][i].equals(cards[k][i])) {
                            cards[j][i].setNum(cards[j][i].getNum() * 2);
                            cards[k][i].setNum(0);
                            MainActivity.getMainActivity().addScore(cards[j][i].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            changeCardColor();
            checkGameOver();
        }
    }

    private void checkGameOver() {
        // 无空位且相邻两个无相同数字
        boolean gameOver = true;
        ALL:
        for (int i = 0; i < DIFFICULT; i++) {
            for (int j = 0; j < DIFFICULT; j++) {
                if (cards[i][j].getNum() == 0 || (i < DIFFICULT - 1 && cards[i][j].equals(cards[i + 1][j])) || (i > 0 && cards[i][j].equals(cards[i - 1][j]))
                        || (j < DIFFICULT - 1 && cards[i][j].equals(cards[i][j + 1])) || (j > 1 && cards[i][j].equals(cards[i][j - 1]))) {
                    gameOver = false;
                    break ALL;
                }

            }
        }
        if (gameOver) {
            // MainActivity.getMainActivity().gameOver();
            new AlertDialog.Builder(getContext()).setTitle("Sorry").setMessage("Game Over!").setPositiveButton("Try again", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startGame();
                }
            }).show();

        }
    }

    private void changeCardColor() {
        for (int i = 0; i < DIFFICULT; i++) {
            for (int j = 0; j < DIFFICULT; j++) {
                switch (cards[i][j].getNum()) {

                    case 0:
                        cards[i][j].setColor(0x33ffffff);
                        break;
                    case 2:
                        cards[i][j].setColor(Color.rgb(238, 228, 218));
                        break;
                    case 4:
                        cards[i][j].setColor(Color.rgb(236, 224, 200));
                        break;
                    case 8:
                        cards[i][j].setColor(Color.rgb(242, 177, 121));
                        break;
                    case 16:
                        cards[i][j].setColor(Color.rgb(245, 145, 99));
                        break;
                    case 32:
                        cards[i][j].setColor(Color.rgb(245, 124, 95));
                        break;
                    case 64:
                        cards[i][j].setColor(Color.rgb(246, 93, 59));
                        break;
                    case 128:
                        cards[i][j].setColor(Color.rgb(237, 206, 113));
                        break;
                    case 256:
                        cards[i][j].setColor(Color.rgb(229, 203, 106));
                        break;
                    case 512:
                        cards[i][j].setColor(Color.rgb(237, 200, 90));
                        break;
                    case 1024:
                        cards[i][j].setColor(Color.rgb(236, 198, 63));
                        break;
                    default:
                        cards[i][j].setColor(0x33ffffff);
                        break;

                }
            }
        }
    }
}
