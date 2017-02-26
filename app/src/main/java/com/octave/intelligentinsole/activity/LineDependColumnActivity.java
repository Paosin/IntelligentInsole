package com.octave.intelligentinsole.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.octave.intelligentinsole.BaseActivity;
import com.octave.intelligentinsole.R;
import com.octave.intelligentinsole.entity.EntityOfHistory;
import com.octave.intelligentinsole.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * @author lecho
 * @revision xiarui 2016.09.08
 * @description 线性图依赖柱状图的使用
 */
public class LineDependColumnActivity extends BaseActivity {
    private static final String TAG = "LineDependColumnActivit";
    /*========== 控件相关 ==========*/
    private LineChartView mLineView;
    private ColumnChartView mColumnView;

    /*========== 数据相关 ==========*/
    private LineChartData mLineData;
    private ColumnChartData mColumnData;
    //从数据库中读取的一年份的数据
    private List<EntityOfHistory> dataOfYear = new ArrayList<EntityOfHistory>();
    //将年份的数据拆分为月
    private List<EntityOfHistory> dataOfMonth = new ArrayList<EntityOfHistory>();
    private List<EntityOfHistory>[] listOfMonth = new List[12];
    //将月份的数据拆分为日
    private List<EntityOfHistory> dataOfDay = new ArrayList<EntityOfHistory>();
    private List<EntityOfHistory>[] listOfDay = new List[31];
    public final static String[] MONTH_STRS = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",};
    public final static String[] DAY_STRS = new String[]{"Mon", "Tue", "Wen", "Thu", "Fri", "Sat", "Sun"};

    @Override
    public int getLayoutId() {
        return R.layout.activity_line_depend_column;
    }

    @Override
    public void initView() {
        mLineView = (LineChartView) findViewById(R.id.lcv_depend_main);
        mColumnView = (ColumnChartView) findViewById(R.id.ccv_depend_main);
        mLineView.setOnValueTouchListener(new LineValueTouchListener());
        mColumnView.setOnValueTouchListener(new ColumnValueTouchListener());
    }

    @Override
    public void initData(Bundle parms) {
        dataOfYear = (List<EntityOfHistory>) parms.getSerializable("dataOfYear");
        splitDataByMonth();
        setInitialLineDatas();
        setColumnDatas();   //禁用缩放
    }

    /**
     * 将一年份的数据按月份分割用于柱状图的显示
     */
    private void splitDataByMonth() {
        long previousTime = 0;
        List<EntityOfHistory> dataOfMonth = new ArrayList<EntityOfHistory>();
        for (int i = 0; i < dataOfYear.size(); i++) {

        }
    }


    @Override
    public void initListener() {

    }

    @Override
    public void processClick(View v) {

    }

    /**
     * 设置初始化线性图数据
     */
    private void setInitialLineDatas() {
        int numValues = 31;                      //7个值 注意与定义的X轴数量相同

        List<AxisValue> axisValues = new ArrayList<>();
        List<PointValue> values = new ArrayList<>();

        //设置默认值 都为0
        for (int i = 0; i < numValues; ++i) {
            values.add(new PointValue(i, 0));
            axisValues.add(new AxisValue(i));
        }

        //设置线
        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);
        line.setHasPoints(false);//不显示节点
        line.setCubic(false);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        //对数据进行一些设置 类似Line Chart
        mLineData = new LineChartData(lines);

        Axis aX = new Axis();                    //X轴
        Axis aY = new Axis().setHasLines(true);  //Y轴
        aX.setTextColor(Color.GRAY);             //X轴灰色
        aY.setTextColor(Color.GRAY);             //Y轴灰色
        //setLineColor()：此方法是设置图表的网格线颜色 并不是轴本身颜色
        aX.setName("Axis X");                //设置名称
        aY.setName("Axis Y");

        mLineData.setAxisXBottom(aX);
        mLineData.setAxisYLeft(aY);
        mLineView.setLineChartData(mLineData);
        mLineView.setViewportCalculationEnabled(false);

        //设置到窗口上
        Viewport v = new Viewport(0, 1000, 31, -5);   //防止曲线超过范围 边界保护
        mLineView.setMaximumViewport(v);
        mLineView.setCurrentViewport(v);
        mLineView.setZoomType(ZoomType.HORIZONTAL);
    }

    /**
     * 设置柱状图数据
     */
    private void setColumnDatas() {
        int numSubcolumns = 1;                  //一个子列
        int numColumns = MONTH_STRS.length;      //长度与定义的X轴长度相同

        List<AxisValue> axisValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;

        //设置一些随机值、颜色、标签等
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            for (int j = 0; j < numSubcolumns; ++j) {
                int steps = 0;
                for (int k = 0; k < dataOfYear.size(); k++) {
                    if (i == TimeUtils.getMonth(dataOfYear.get(k).getStartTime()))
                        steps += dataOfYear.get(k).getSteps();
                }
                values.add(new SubcolumnValue(steps, ChartUtils.pickColor()));
            }
            axisValues.add(new AxisValue(i).setLabel(MONTH_STRS[i]));
            columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
        }

        //对数据进行一些设置 类似Column Chart
        mColumnData = new ColumnChartData(columns);
        mColumnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        mColumnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2));
        mColumnView.setColumnChartData(mColumnData);
        mColumnView.setOnValueTouchListener(new ColumnValueTouchListener());
        mColumnView.setValueSelectionEnabled(true);
        mColumnView.setZoomEnabled(false); //禁用缩放
    }

    /**
     * 设置选中的条目的线性图
     *
     * @param color 颜色
     * @param month 月份
     */
    private void setLineDatas(int color, int month) {
        //如果上一个动画未完成 先结束动画
        mLineView.cancelDataAnimation();

        // 设置第几条线变化 默认为第一条线
        Line line = mLineData.getLines().get(0);
        line.setColor(color);
        int day = 0;
        int step = 0;
        for (PointValue value : line.getValues()) {
            for (int i = 0; i < dataOfYear.size(); i++) {
                long time = dataOfYear.get(i).getStartTime();
                if (day == TimeUtils.getDay(time)&&month==TimeUtils.getMonth(time))
                    step += dataOfYear.get(i).getSteps();
            }
            //这里只是随机值 可以添加相对应的数据
            value.setTarget(value.getX(), step);
            step = 0;
            day++;
        }
        mLineView.startDataAnimation();
    }

    @Override
    public void onClick(View v) {

    }


    /**
     * 节点触摸监听
     */
    private class LineValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(LineDependColumnActivity.this, "选中第 " + ((int) value.getX() + 1) + " 个节点", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
        }
    }

    /**
     * 子列触摸监听
     */
    private class ColumnValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            setLineDatas(value.getColor(), columnIndex);
        }

        @Override
        public void onValueDeselected() {
            setLineDatas(ChartUtils.COLOR_GREEN, 0);
        }
    }
}
