package com.octave.intelligentinsole;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.octave.intelligentinsole.interfaces.BaseActivityInterface;

import butterknife.ButterKnife;

/**
 * Created by Paosin Von Scarlet on 2017/2/18.
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseActivityInterface, View.OnClickListener {
    protected final String TAG = this.getClass().getSimpleName();
    /**
     * 是否沉浸状态栏
     **/
    private boolean isSetStatusBar = true;
    /**
     * 是否允许全屏
     **/
    private boolean mAllowFullScreen = true;
    /**
     * 是否禁止旋转屏幕
     **/
    private boolean isAllowScreenRoate = false;
    /**
     * 当前Activity渲染的视图View
     **/
    private View mContextView = null;


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate:");
        Bundle bundle = getIntent().getExtras();

        //得到布局文件
        setContentView(getLayoutId());

        //初始化View
        initView();

        //初始化界面数据
        initData(bundle);

        //绑定监听器与适配器
        initListener();

    }

    /**
     * 对统一的按钮进行统一处理
     *
     * @param v 点击的View
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                processClick(v);
                break;
        }
    }

    /**
     * 显示一个Toast
     *
     * @param msg 吐司内容
     */
    protected void baseToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个Toast
     *
     * @param msgId 吐司内容
     */
    protected void baseToast(int msgId) {
        Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个SnackBar
     * 使用的时候会自动屏蔽掉系统自带的ui比如说华为坑爹的虚拟按键栏
     *
     * @param msg
     */
    protected void baseSnackBar(String msg) {
        //去掉虚拟按键
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏虚拟按键栏
                | View.SYSTEM_UI_FLAG_IMMERSIVE //防止点击屏幕时,隐藏虚拟按键栏又弹了出来
        );
        final Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), msg, Snackbar.LENGTH_SHORT);
        snackbar.setAction("close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).show();
    }

    /**
     * 显示一个SnackBar
     *
     * @param msgId
     */
    protected void baseSnackBar(int msgId) {
        //去掉虚拟按键
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏虚拟按键栏
                | View.SYSTEM_UI_FLAG_IMMERSIVE //防止点击屏幕时,隐藏虚拟按键栏又弹了出来
        );
        final Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), msgId, Snackbar.LENGTH_SHORT);
        snackbar.setAction("close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).show();
    }

    /**
     * [页面跳转]
     *
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(BaseActivity.this, clz));
    }

    /**
     * [携带数据的页面跳转]
     *
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * [携带数据的页面跳转]
     *
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle,View sharedElement, String sharedElementName) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent, ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, sharedElement, sharedElementName).toBundle());
    }

    /**
     * [含有Bundle通过Class打开编辑界面]
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
}
