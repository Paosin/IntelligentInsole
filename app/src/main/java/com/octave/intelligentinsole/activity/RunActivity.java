package com.octave.intelligentinsole.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.octave.intelligentinsole.BaseActivity;
import com.octave.intelligentinsole.R;
import com.octave.intelligentinsole.utils.DensityUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RunActivity extends BaseActivity {

    @Bind(R.id.tv_run_distance)
    TextView tvRunDistance;
    @Bind(R.id.tv_run_steps)
    TextView tvRunSteps;
    @Bind(R.id.tv_run_history)
    Button tvRunHistory;
    @Bind(R.id.btn_run_switch)
    Button btnRunSwitch;
    @Bind(R.id.ll_run_btngroup)
    LinearLayout llRunBtnGroup;
    @Bind(R.id.ch_run_timer)
    Chronometer timer;

    private boolean isStart = false;
    private Button finishBtn;
    private long recordingTime = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_run;
    }

    @Override
    public void initView() {
        finishBtn = new Button(this);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //结束本次跑步
                Snackbar.make(v, "Finish", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                btnRunSwitch.setBackgroundResource(R.drawable.ic_play);
                llRunBtnGroup.removeView(finishBtn);
                //结束计时，计时器复位
                recordingTime = 0;
                timer.setBase(SystemClock.elapsedRealtime());
            }
        });
    }

    @Override
    public void initData(Bundle parms) {
//        tvRunTime.setText("00:00:00");
        tvRunDistance.setText("0.0");
        tvRunSteps.setText("0");
    }

    @Override
    public void initListener() {
    }

    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @OnClick({R.id.tv_run_history, R.id.btn_run_switch})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_run_history:
                startActivity(HistoryActivity.class);
                break;
            case R.id.btn_run_switch:
                if (!isStart) {
                    //will Start
                    //计时器开始计时
                    timer.setBase(SystemClock.elapsedRealtime() - recordingTime);
                    int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
                    timer.setFormat("0" + hour + ":%s");
                    timer.start();
                    btnRunSwitch.setBackgroundResource(R.drawable.ic_pause);
                    //将结束按钮移除
                    llRunBtnGroup.removeView(finishBtn);
                    //开始计时
                    Snackbar.make(view, "Start!!!!!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    //计时器中断计时
                    timer.stop();
                    recordingTime = SystemClock.elapsedRealtime() - timer.getBase();
                    //添加结束按钮
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMarginStart(DensityUtil.px2dip(this, 50));
                    finishBtn.setBackgroundResource(R.drawable.ic_stop);
                    finishBtn.setLayoutParams(params);
                    llRunBtnGroup.addView(finishBtn);
                    btnRunSwitch.setBackgroundResource(R.drawable.ic_play);
                    //Will pause
                    Snackbar.make(view, "Pause!!!!!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                isStart = !isStart;
                break;
        }
    }

}
