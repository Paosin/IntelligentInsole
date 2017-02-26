package com.octave.intelligentinsole.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.octave.intelligentinsole.BaseActivity;
import com.octave.intelligentinsole.R;
import com.octave.intelligentinsole.adapter.ItemOfMainAdapter;
import com.octave.intelligentinsole.entity.Lv0EntityOfMain;
import com.octave.intelligentinsole.entity.Lv1EntityOfMain;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.header)
    LinearLayout header;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.app_bar)
    AppBarLayout appBar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.rv_activity)
    RecyclerView recyclerView;

    private List<Lv0EntityOfMain> list;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        setSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ItemOfMainAdapter(generateData()));
    }

    @Override
    public void initData(Bundle parms) {
        list = new ArrayList<Lv0EntityOfMain>();
        Lv0EntityOfMain data = new Lv0EntityOfMain(R.drawable.ic_walk, "设备连接", R.drawable.ic_chevron_right);
        list.add(data);
        data = new Lv0EntityOfMain(R.drawable.ic_walk, "今日活动", R.drawable.ic_chevron_right);
        list.add(data);
        data = new Lv0EntityOfMain(R.drawable.ic_walk, "异常步态检测", R.drawable.ic_chevron_right);
        list.add(data);
        data = new Lv0EntityOfMain(R.drawable.ic_walk, "评估参数", R.drawable.ic_chevron_right);
        list.add(data);
    }

    @Override
    public void initListener() {
        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                Intent intent;
                switch (i) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    default:
                        Snackbar.make(view, "No Action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                }
            }
        });
    }

    @Override
    public void processClick(View v) {

    }

    @OnClick(R.id.fab)
    public void onClick() {
        startActivity(RunActivity.class);
    }

    private ArrayList<MultiItemEntity> generateData() {
        ArrayList<MultiItemEntity> res = new ArrayList<>();
        Lv0EntityOfMain lv0 = new Lv0EntityOfMain(R.drawable.ic_walk, "设备连接", R.drawable.ic_chevron_right);
        Lv1EntityOfMain lv1 = new Lv1EntityOfMain(R.drawable.ic_walk, "设备连接", R.drawable.ic_chevron_right, null);
        lv0.addSubItem(lv1);
        res.add(lv0);
        lv0 = new Lv0EntityOfMain(R.drawable.ic_walk, "今日活动", R.drawable.ic_chevron_right);
        lv1 = new Lv1EntityOfMain(R.drawable.ic_walk, "坐", R.drawable.ic_chevron_right, EvaluationParamActivity.class);
        lv0.addSubItem(lv1);
        lv1 = new Lv1EntityOfMain(R.drawable.ic_walk, "行走", R.drawable.ic_chevron_right, TestActivity.class);
        lv0.addSubItem(lv1);
        lv1 = new Lv1EntityOfMain(R.drawable.ic_walk, "上楼梯", R.drawable.ic_chevron_right, null);
        lv0.addSubItem(lv1);
        lv1 = new Lv1EntityOfMain(R.drawable.ic_run, "跑步", R.drawable.ic_chevron_right, null);
        lv0.addSubItem(lv1);
        lv1 = new Lv1EntityOfMain(R.drawable.ic_walk, "还有啥", R.drawable.ic_chevron_right, null);
        lv0.addSubItem(lv1);
        res.add(lv0);
        lv0 = new Lv0EntityOfMain(R.drawable.ic_walk, "评估参数", R.drawable.ic_chevron_right);
        lv1 = new Lv1EntityOfMain(R.drawable.ic_walk, "评估参数", R.drawable.ic_chevron_right, null);
        lv0.addSubItem(lv1);
        res.add(lv0);
        lv0 = new Lv0EntityOfMain(R.drawable.ic_walk, "评估参数", R.drawable.ic_chevron_right);
        lv1 = new Lv1EntityOfMain(R.drawable.ic_walk, "评估参数", R.drawable.ic_chevron_right, null);
        lv0.addSubItem(lv1);
        res.add(lv0);
        return res;
    }
}
