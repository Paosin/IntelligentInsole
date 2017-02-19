package com.octave.intelligentinsole.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.octave.intelligentinsole.BaseActivity;
import com.octave.intelligentinsole.R;
import com.octave.intelligentinsole.adapter.ItemHistoryAdapter;
import com.octave.intelligentinsole.entity.EntityOfHistory;
import com.octave.intelligentinsole.entity.HistorySection;
import com.octave.intelligentinsole.utils.TimeUtils;
import com.octave.intelligentinsole.views.ColChart;
import com.octave.intelligentinsole.views.LineChart;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 展示历史记录
 * Chart用来展示本周的数据
 * 列表用于展示该类型一直以来的所有数据
 * Created by Paosin Von Scarlet on 2017/2/13.
 */
public class HistoryActivity extends BaseActivity {

    @Bind(R.id.fab_history_chartswitch)
    FloatingActionButton fabHistoryChartswitch;
    @Bind(R.id.rv_history_list)
    RecyclerView rvHistoryList;
    @Bind(R.id.chartgroup)
    LinearLayout mChartGroup;
    private List<EntityOfHistory> datas = new ArrayList<EntityOfHistory>();
    private boolean isLineChart = true;
    private LineChart mLineChartView;
    private ColChart mColChartView;
    @Override
    public void initData() {
        addLineChart(this);

        rvHistoryList.setLayoutManager(new LinearLayoutManager(this));

        rvHistoryList.setAdapter(new ItemHistoryAdapter(R.layout.item_history, R.layout.item_header, getList(datas)));

        rvHistoryList.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                Intent intent = new Intent(HistoryActivity.this, LineDependColumnActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(HistoryActivity.this,view,"LineDependColumn").toBundle());

            }
        });

        fabHistoryChartswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLineChart) {
                    addLineChart(v.getContext());
                    fabHistoryChartswitch.setImageResource(R.drawable.ic_chart_line);
                } else {
                    addColChart(v.getContext());
                    fabHistoryChartswitch.setImageResource(R.drawable.ic_chart_bar);
                }
                isLineChart = !isLineChart;
            }
        });
    }
    private void addColChart(Context context) {
        mChartGroup.removeAllViews();
        mColChartView = new ColChart(context);
        //设定的是x=7即一周中每一天的个数
        mColChartView.initData();
        mChartGroup.addView(mColChartView.getmColumnChartView());
    }

    private void addLineChart(Context context) {
        mChartGroup.removeAllViews();
        mLineChartView = new LineChart(context);
        //设定的是x=7即一周中每一天的个数
        mLineChartView.initData();
        //mLineChartView.initData(float[][] data,String[] axisX)
        mChartGroup.addView(mLineChartView.getmLineChartView());
    }
    /**
     * 测试用的RecyclerView的数据
     * @param datas
     * @return
     */
    private List<HistorySection> getList(List<EntityOfHistory> datas) {
        //测试用的时间数据
        //如果使用SQLite的话，直接where type = '?'选取不同的类型
        datas.add(new EntityOfHistory(6, 1487169964915L, TimeUtils.hourToMillis(2), 1000, 0));
        datas.add(new EntityOfHistory(8, 1487164684554L, TimeUtils.hourToMillis(1), 1000, 1));
        datas.add(new EntityOfHistory(16, 1487163548798L + TimeUtils.MILLIS_IN_DAY, TimeUtils.hourToMillis(3), 1000, 0));
        datas.add(new EntityOfHistory(484, 1487169531249L + TimeUtils.MILLIS_IN_DAY, TimeUtils.hourToMillis(4), 1000, 1));
        datas.add(new EntityOfHistory(4846, 1487165489513L + TimeUtils.MILLIS_IN_DAY, TimeUtils.hourToMillis(2), 1000, 0));
        datas.add(new EntityOfHistory(100000, 1487161987456L + 2 * TimeUtils.MILLIS_IN_DAY, TimeUtils.hourToMillis(4.5f), 100000, 0));
        datas.add(new EntityOfHistory(78, 1487163549874L + 2 * TimeUtils.MILLIS_IN_DAY, TimeUtils.hourToMillis(6.5f), 1000, 2));
        datas.add(new EntityOfHistory(1, 1487166965324L + 2 * TimeUtils.MILLIS_IN_DAY, TimeUtils.hourToMillis(3.7f), 1000, 0));
        datas.add(new EntityOfHistory(468, 1487161234567L + 3 * TimeUtils.MILLIS_IN_DAY, TimeUtils.hourToMillis(9.5f), 1000, 1));

        long time = System.currentTimeMillis();
        System.out.println(time);
        List<HistorySection> list = new ArrayList<HistorySection>();
        long previousTime = 0;
        for (int i = 0; i < datas.size(); i++) {
            long thisTime = datas.get(i).getStartTime();
            if (!TimeUtils.isSameDayOfMillis(thisTime, previousTime))
                list.add(new HistorySection(true, TimeUtils.millisToYMD(thisTime)));
            list.add(new HistorySection(datas.get(i)));
            previousTime = thisTime;
        }
        return list;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_history;
    }

    @Override
    public void initView() {

    }



    @Override
    public void initListener() {

    }

    @Override
    public void processClick(View v) {

    }
}
