package com.octave.intelligentinsole.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.octave.intelligentinsole.BaseActivity;
import com.octave.intelligentinsole.R;
import com.octave.intelligentinsole.views.TestView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestActivity extends BaseActivity {

    @Bind(R.id.test)
    TestView test;
    @Bind(R.id.button)
    Button button;

    @Override
    public int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData(Bundle parms) {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void processClick(View v) {

    }


    @OnClick(R.id.button)
    public void onClick() {
        int[] a = {1, 4, 5, 3};
        test.initColor(a);
    }
}
