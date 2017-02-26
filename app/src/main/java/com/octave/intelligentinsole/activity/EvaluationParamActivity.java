package com.octave.intelligentinsole.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.octave.intelligentinsole.BaseActivity;
import com.octave.intelligentinsole.R;
import com.octave.intelligentinsole.utils.AnalogData;
import com.octave.intelligentinsole.views.DrawPressurePath;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class EvaluationParamActivity extends BaseActivity {
    public final static int SUCCESS_ADD_STEPCOUNT = 1;
    public final static int FAILURE_ADD_STEPCOUNT = 0;
    public static int StepCount = 0;
    @Bind(R.id.left)
    DrawPressurePath mLeft;
    @Bind(R.id.right)
    DrawPressurePath mRight;

    @Bind(R.id.lcv_evaluation_main)
    LineChartView mLineChartView;
    private View view;
    private Handler mHandler;

    @Override
    public int getLayoutId() {
        return R.layout.activity_evaluation_param;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData(Bundle parms) {
        mHandler = new Handler();
        //为两个控件开启两个线程，测试以不同的速率传输不同的值到两个控件刷新正误
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                AnalogData a = new AnalogData();
                mLeft.initPoint(a.getData());
                mHandler.postDelayed(this, 3000);
                float[] p = new float[9];
                p[AnalogData.getRandom(0, 8)] = AnalogData.getRandom(0, 20);
                p[AnalogData.getRandom(0, 8)] = AnalogData.getRandom(0, 20);
                p[AnalogData.getRandom(0, 8)] = AnalogData.getRandom(0, 20);
                p[AnalogData.getRandom(0, 8)] = AnalogData.getRandom(0, 20);
                //TODO:改成了用背景颜色来显示压力，不使用矩形了
//                mLeft.initRect(p);
                mLeft.initPressureColor(p);
                StepCount++;
//                DataShowFragment.handler.sendEmptyMessage(StepCount);
            }
        });

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                AnalogData a = new AnalogData();
                mRight.initPoint(a.getData());
                mHandler.postDelayed(this, 2000);
                StepCount++;
//                DataShowFragment.handler.sendEmptyMessage(StepCount);
                float[] p = new float[9];
                p[AnalogData.getRandom(0, 8)] = AnalogData.getRandom(0, 20);
                p[AnalogData.getRandom(0, 8)] = AnalogData.getRandom(0, 20);
                p[AnalogData.getRandom(0, 8)] = AnalogData.getRandom(0, 20);
                p[AnalogData.getRandom(0, 8)] = AnalogData.getRandom(0, 20);
                //TODO:改成了用背景颜色来显示压力，不使用矩形了
//                mRight.initRect(p);
                mRight.initPressureColor(p);
//                DrawPressurePath.handler.sendEmptyMessage(AnalogData.getRandom(0, 8));
//                mRight.setmRectColor(Color.GREEN,AnalogData.getRandom(0, 8));
            }
        });

        initChart();
    }

    private Timer timer;
    private int position = 0;
    private List<PointValue> pointValueList = new ArrayList<>();
    private Random random = new Random();
    private List<Line> linesList = new ArrayList<>();
    private LineChartData lineChartData;
    private boolean isFinish;

    private void initChart() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isFinish) {
                    //实时添加新的点
                    PointValue value1 = new PointValue(position * 5, 40 + random.nextInt(20));
                    value1.setLabel("00:00");
                    pointValueList.add(value1);
                    float x = value1.getX();
                    //根据新的点的集合画出新的线
                    Line line = new Line(pointValueList);
                    line.setColor(Color.RED);
                    line.setShape(ValueShape.CIRCLE);
                    line.setCubic(true);//曲线是否平滑，即是曲线还是折线

                    linesList.clear();
                    linesList.add(line);
                    lineChartData = initDatas(linesList);
                    mLineChartView.setLineChartData(lineChartData);
                    //根据点的横坐标实时变幻坐标的视图范围
                    Viewport port;
                    if (x > 50) {
                        port = initViewPort(x - 50, x);
                    } else {
                        port = initViewPort(0, 50);
                    }
                    mLineChartView.setCurrentViewport(port);//当前窗口

                    Viewport maPort = initMaxViewPort(x);
                    mLineChartView.setMaximumViewport(maPort);//最大窗口

                    position++;
                    if (position > 100 - 1) {
                        isFinish = true;
                        mLineChartView.setInteractive(true);
                    }
                }
            }
        }, 300, 300);
    }
    private LineChartData initDatas(List<Line> lines) {
        LineChartData data = new LineChartData(lines);
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setTextColor(Color.GRAY);
        axisY.setTextColor(Color.GRAY);
        axisX.setName("Axis X");
        axisY.setName("Axis Y");
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        return data;
    }
    private Viewport initViewPort(float left, float right) {
        Viewport port = new Viewport();
        port.top = 100;
        port.bottom = 0;
        port.left = left;
        port.right = right;
        return port;
    }
    private Viewport initMaxViewPort(float right) {
        Viewport port = new Viewport();
        port.top = 100;
        port.bottom = 0;
        port.left = 0;
        port.right = right + 50;
        return port;
    }
    @Override
    public void initListener() {

    }

    @Override
    public void processClick(View v) {

    }
}
