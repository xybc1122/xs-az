package com.example.xs.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.example.xs.R;
import com.example.xs.utils.StrUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 时间尺控件
 * Created by king on 2020/9/8
 */

public class TimeScaleView extends View {
    private int viewWidth;
    private int viewHeight;
    private Paint linePaint = new Paint();
    private Paint midPaint = new Paint();
    private Paint timePaint = new Paint();
    private Paint bgPaint = new Paint();
    private Set<RangeXY> setIndex;
    private boolean isDrawTimeRect = false;
    //时间间隔用小时计算
    private int timeScale = 1;
    //时间的长度
    private int totalTime = 1;
    //滚动
    private Scroller scroller;
    float lastX = 0;
    //选中的数据
    private List<TimePart> data;
    //矩形
    private Rect rect;
    //选中时间片段颜色
    private String timePartColor = "#02A7DD";
    //背景颜色，可以修改
    private String bgColor = "#303133";

    //展示新时间
    private boolean isTime = false;


    //滚动监听
    private OnScrollListener scrollListener;

    public TimeScaleView(Context context) {
        super(context);
    }

    public TimeScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //  横坐标
        linePaint.setAntiAlias(true);
        linePaint.setColor(getResources().getColor(R.color.qmui_config_color_white));
        linePaint.setTextSize(16);

        timePaint.setAntiAlias(true);
        timePaint.setColor(Color.parseColor(timePartColor));

        midPaint.setAntiAlias(true);
        midPaint.setStrokeWidth(3);
        midPaint.setColor(getResources().getColor(R.color.qmui_config_color_white));

        bgPaint.setAntiAlias(true);
        bgPaint.setColor(Color.parseColor(bgColor));

        //数据设置
        data = new ArrayList<>();
        rect = new Rect();
        setIndex = new HashSet<>();
        scroller = new Scroller(context);

    }

    public TimeScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCurrentHostTime(int host) {
        //计算时
        int i = timeScale * (host - 3);
        scroller.startScroll(i, 0, 0, 0);
    }

    public void startCurrentHostTime(int x) {
        int finalx = scroller.getFinalX();
        System.out.println(finalx);
        if (finalx > timeScale * 21) {
            return;
        }
        scroller.startScroll(scroller.getFinalX(), scroller.getFinalY(), x, 0);
        postInvalidate();
    }

    /**
     * 画背景
     *
     * @param canvas 画布
     */
    public void drawBg(Canvas canvas) {
        rect.set(-1, 0, timeScale * 24 + 1, viewHeight);
        canvas.drawRect(rect, bgPaint);
    }

    /**
     * 画刻度
     *
     * @param canvas 画布
     */
    public void drawLines(Canvas canvas) {
        //画刻度值
        for (int i = 0; i <= totalTime; i++) {
            if (i % timeScale == 0) {
                //每一行的距离是90 余数
                canvas.drawLine(i, (float) viewHeight, i,
                        (float) (viewHeight * 0.8), linePaint);
                String strTime = StrUtil.formatString(i / timeScale, 0, 0);
                canvas.drawText(
                        strTime, i, (float) (viewHeight * 0.5), linePaint);

            }
        }
    }

    /**
     * 画时间片段
     *
     * @param canvas
     */
    public void drawTimeRect(Canvas canvas) {
        for (TimePart temp : data) {
            int seconds1 = temp.sHour * 3600 + temp.sMinute * 60 + temp.sSeconds;
            int seconds2 = temp.eHour * 3600 + temp.eMinute * 60 + temp.eSeconds;
            //如果是先除以3600小数点的数据会被舍去 位置就不准确了
            int x1 = seconds1 * timeScale / 3600;
            int x2 = seconds2 * timeScale / 3600;
            if (!isDrawTimeRect) {
                setIndex.add(new RangeXY(x1, x2));
            }
            rect.set(x1, 0, x2, viewHeight);
            canvas.drawRect(rect, timePaint);
        }
    }

    /**
     * 画指针
     *
     * @param canvas 画布
     */
    public void drawMidLine(Canvas canvas) {
        //移动的距离整个view内容移动的距离
        int finalX = scroller.getFinalX();
        //表示每一个屏幕刻度的一半的总秒数，每一个屏幕有6格
        int sec = 3 * 3600;
        //滚动的秒数
        int temsec = (int) Math.rint((double) finalX / (double) timeScale * 3600);
        sec += temsec;
        //获取的时分秒
        int thour = sec / 3600;
        int tmin = (sec - thour * 3600) / 60;
        int tsec = sec - thour * 3600 - tmin * 60;
        //滚动时的监听
        if (scrollListener != null) {
            scrollListener.onScroll(thour, tmin, tsec);
            isPlayIndex(finalX);
        }
        //画指针
        canvas.drawLine(timeScale * 3 + finalX, 0,
                timeScale * 3 + finalX, viewHeight, midPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (scroller != null && !scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                lastX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dataX = lastX - x;
                int finalx = scroller.getFinalX();
                //右边

                if (dataX > 0) {
                    if (dataX < 0) {
                        if (finalx < -viewWidth / 2) {
                            return super.onTouchEvent(event);
                        }
                    }
                    //这里阻止滑动
                    if (finalx > timeScale * 21) {
                        return super.onTouchEvent(event);
                    }
                }
                scroller.startScroll(scroller.getFinalX(), scroller.getFinalY(), (int) dataX, 0);
                lastX = x;
                postInvalidate();
                return true;
            case MotionEvent.ACTION_UP:
                int finalx1 = scroller.getFinalX();
                if (finalx1 < -viewWidth / 2) {
                    scroller.setFinalX(-viewWidth / 2);
                }
                if (finalx1 > timeScale * 21) {
                    scroller.setFinalX(timeScale * 21);
                }
                if (scrollListener != null) {
                    int finalX = scroller.getFinalX();
                    isPlayIndex(finalX);
                    //表示每一个屏幕刻度的一半的总秒数，每一个屏幕有6格   1小时3600秒
                    int sec = 3 * 3600;
                    //滚动的秒数
                    int temsec = (int) Math.rint((double) finalX / (double) timeScale * 3600);
                    sec += temsec;
                    //获取的时分秒
                    int thour = sec / 3600;
                    int tmin = (sec - thour * 3600) / 60;
                    int tsec = sec - thour * 3600 - tmin * 60;
                    if (thour == 24) {
                        scrollListener.onScrollFinish(thour - 1, 59, 59);
                    } else {
                        scrollListener.onScrollFinish(thour, tmin, tsec);
                    }
                }
                postInvalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    //是否是可以播放的地址
    public void isPlayIndex(int finalX) {
        int index = finalX + timeScale * 3;
        int i = 0;
        for (RangeXY r : setIndex) {
            i++;
            if (index == r.x || index == r.y) {
                scrollListener.isIndex(true);
                break;
            } else if (index > r.x && index < r.y) {
                scrollListener.isIndex(true);
                break;
            } else {
                if (i == setIndex.size()) {
                    scrollListener.isIndex(false);
                }
            }
        }
    }

    //获得是否是可播放视频
    public boolean getIsIndexPlay() {
        int finalX = scroller.getFinalX();
        int index = finalX + timeScale * 3;
        int i = 0;
        for (RangeXY r : setIndex) {
            i++;
            if (index == r.x || index == r.y) {
                return true;
            } else if (index > r.x && index < r.y) {
                return true;
            } else {
                if (i == setIndex.size()) {
                    return false;
                }
            }
        }
        return false;
    }


    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
        super.computeScroll();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        viewWidth = getWidth();
        viewHeight = getHeight();
        //每小时的刻度一个屏幕分成6格   540/6 =90         2160/540 4个屏幕 6*4 24个刻度尺
        timeScale = viewWidth / 6;
        //总的时间刻度距离      2160
        totalTime = timeScale * 24;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        //选中的时间
        drawTimeRect(canvas);
        drawLines(canvas);
        drawMidLine(canvas);
        isDrawTimeRect = true;
    }

    //滚动监听类
    public interface OnScrollListener {

        void onScroll(int hour, int min, int sec);

        void onScrollFinish(int hour, int min, int sec);

        //是否支持有这个坐标 回调
        void isIndex(boolean isFlg);

    }

    public OnScrollListener getScrollListener() {
        return scrollListener;
    }

    public void setScrollListener(OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    //添加时间片段到容器中
    public void addTimePart(List<TimePart> temp) {
        if (temp != null) {
            data.addAll(temp);
            postInvalidate();
        }
    }

    //清除所有的时间片段数据
    public void clearData() {
        data.clear();
        postInvalidate();
    }

    public String getTimePartColor() {
        return timePartColor;
    }

    //设置时间片段的颜色
    public void setTimePartColor(String timePartColor) {
        this.timePartColor = timePartColor;
        postInvalidate();
    }

    //时间片段 用于标记选中的时间
    public static class TimePart {
        //开始的时间
        public int sHour, sMinute, sSeconds;
        //结束的时间
        public int eHour, eMinute, eSeconds;

        public TimePart(int sHour, int sMinute, int sSeconds, int eHour, int eMinute, int eSeconds) {
            this.sHour = sHour;
            this.sMinute = sMinute;
            this.sSeconds = sSeconds;
            this.eHour = eHour;
            this.eMinute = eMinute;
            this.eSeconds = eSeconds;
        }
    }

    public static class RangeXY {
        int x;
        int y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public RangeXY(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
