package com.octave.intelligentinsole.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.octave.intelligentinsole.R;
import com.octave.intelligentinsole.entity.CenterOfPressure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SurfaceView多线程刷新
 * Created by Paosin Von Scarlet on 2017/1/11.
 */

public class DrawPressurePath extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = "DrawPressurePath";
    public static Handler handler;

    //region 成员变量
    //压力中心线绘图模式，点或线
    private final int DRAW_POINT = 0;
    private final int DRAW_LINE = 1;
    private int mDrawMode;
    //路径坐标
    private float mX;
    private float mY;
    //View宽高
    private float mWidth;
    private float mHeight;
    //画笔
    private final Paint mGesturePaint = new Paint();
    private boolean isAntiAlias;    //是否开启抗锯齿

    private int mColorInt;      //笔刷颜色
    private int mDimension;     //线宽度
    //路径
    private final Path mPath = new Path();
    private float multiple;     //根据绘图选取倍数
    //点
    private float[] mPoints;
    //矩形
    private static float RECT_WIDTH = 40f;
    private static float RECT_HEIGHT = 30f;
    private static float RECT_COMM = 10f;
    private static float RECT_LENGTH = 46f;
    private boolean isFlipHorizonta;
    private DrawRects[] mRects;

    //要绘制的背景图片
    private Bitmap mBitmap;
    private Drawable mDrawable = null;
    private LayerDrawable mDrawables;
    //-----------------------------
    //读取Excel的工具
//    private FileTools file = new FileTools();
    private List<CenterOfPressure> data = new ArrayList<CenterOfPressure>();
    //xls文件名，默认数据放置在/storage/emulated/0/目录下
    private String mFilePath;
    //-----------------------------
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    //用于绘制的线程
    private Thread mThread;
    //线程开关
    private boolean isRunning;
    private Matrix mMatrix;
    //endregion

    public DrawPressurePath(Context context) {
        this(context, null);
    }

    public DrawPressurePath(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())
            mDrawables = (LayerDrawable) getBackground();

        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        mHolder.addCallback(this);

        //可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常量
        setKeepScreenOn(true);
        if (!isInEditMode())
            setZOrderOnTop(true);
        //初始化控件View属性
        initAttrs(attrs);

    }

    //初始化属性
    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = null;
            try {
                array = getContext().obtainStyledAttributes(attrs, R.styleable.DrawPressurePath);
                //TODO:现在不适用Drawable来绘制背景，直接用android:background设置
//                mDrawable = array.getDrawable(R.styleable.DrawPressurePath_backgroundsrc);
//                mFilePath = array.getString(R.styleable.DrawPressurePath_filepath);
                //dp,sp都会乘desity,px直接等于
                mDimension = (int) array.getDimension(R.styleable.DrawPressurePath_lineweight, 6);
                mColorInt = array.getColor(R.styleable.DrawPressurePath_draw_color, Color.BLACK);
                isAntiAlias = array.getBoolean(R.styleable.DrawPressurePath_antialias, true);
                mDrawMode = array.getInteger(R.styleable.DrawPressurePath_drawmode, 0);
                isFlipHorizonta = array.getBoolean(R.styleable.DrawPressurePath_isflip_horizontal, false);
                measureDrawable();
            } finally {
                if (array != null)
                    array.recycle();
            }
        }
    }

    //利用Point画点，寻找sd卡上的测试数据
    //可以删
    public void initPoint(String path) {
        //读取Excel用的
        mFilePath = path;
//        data = file.readExcel(mFilePath);
        List<Float> X = new ArrayList<Float>();
        List<Float> Y = new ArrayList<Float>();
        for (int i = 0; i < data.size(); i++) {
            X.add(data.get(i).getX());
            Y.add(data.get(i).getY());
        }
        float maxX = Collections.max(X) - Collections.min(X);
        float maxY = Collections.max(Y) - Collections.min(Y);
        //获取View高度以及数据最后一个点的Y坐标，计算绘制图像的放大倍率
        //----------------应与真实鞋垫的长宽为基准--------------------
        //----------------此处仅用Y测试--------------------
        float multipleX = getMeasuredWidth() / (maxX + 100);
        float multipleY = getMeasuredHeight() / (maxY + 80);
        mPoints = new float[data.size() * 2];
        for (int i = 0; i < data.size(); i++) {
//            mPoints[2 * i] = data.get(i).getX();
//            mPoints[2 * i + 1] = data.get(i).getY();
            mPoints[2 * i] = (data.get(i).getX() * multipleX) + getMeasuredWidth() / 2;
            mPoints[2 * i + 1] = (getMeasuredHeight() - data.get(i).getY() * multipleY);
        }
    }

    //利用Line划线或Point画点，采用随机数来模拟数据
    //Line利用Matrix进行缩放平移，点不用
    public void initPoint(List<CenterOfPressure> path) {
        data = path;
        if (mDrawMode == DRAW_LINE) {
            //获取View高度以及数据最后一个点的Y坐标，计算绘制图像的放大倍率
            multiple = (6 * (getMeasuredHeight() / 11)) / (data.get(data.size() - 1).getY() - data.get(0).getY());
            /**
             * 仅在SerfaceView中测试
             * Matrix要对Path进行矩阵变换时，应该只变换一次，即在初始化坐标的时候
             * 如果在Draw()里面transform，会疯狂的进行变换，鬼知道为啥
             * 如果要一个点一个点的画，就不能只变化一次，具体咋弄还不知道
             * View中没测试
             */
            mMatrix = new Matrix();
            mMatrix.postScale(multiple, -multiple);
            mMatrix.postTranslate(getMeasuredWidth() / 2, getMeasuredHeight() * 0.83f);
            //读取Excel用的
//        mFilePath = path;
//        data = file.readExcel(mFilePath);
            mPath.reset();
            if (data.size() != 0) {
                mX = data.get(0).getX();
                mY = data.get(0).getY();
                mPath.moveTo(mX, mY);
                for (int i = 1; i < data.size(); i++) {
                    pointRead(data.get(i));
                }
            }
            mPath.transform(mMatrix);
        } else {
            List<Float> X = new ArrayList<Float>();
            List<Float> Y = new ArrayList<Float>();
            for (int i = 0; i < data.size(); i++) {
                X.add(data.get(i).getX());
                Y.add(data.get(i).getY());
            }
            float maxX = Collections.max(X) - Collections.min(X);
            float maxY = Collections.max(Y) - Collections.min(Y);
            //获取View高度以及数据最后一个点的Y坐标，计算绘制图像的放大倍率
            //----------------应与真实鞋垫的长宽为基准--------------------
            //----------------此处仅用Y测试--------------------
            float multipleX = getMeasuredWidth() / (maxX + 100);
            float multipleY = getMeasuredHeight() / (maxY + 80);
            mPoints = new float[data.size() * 2];
            for (int i = 0; i < data.size(); i++) {
//            mPoints[2 * i] = data.get(i).getX();
//            mPoints[2 * i + 1] = data.get(i).getY();
                mPoints[2 * i] = (data.get(i).getX() * multipleX) + getMeasuredWidth() / 2;
                mPoints[2 * i + 1] = (getMeasuredHeight() - data.get(i).getY() * multipleY);
            }
        }
    }

    private void pointRead(CenterOfPressure cop) {
        final float x = cop.getX();
        final float y = cop.getY();
        final float previousX = mX;
        final float previousY = mY;
        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);
        //两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        if (dx >= 3 || dy >= 3) {
            //设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (x + previousX) / 2;
            float cY = (y + previousY) / 2;

            //二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            mPath.quadTo(previousX, previousY, cX, cY);
            //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mX = x;
            mY = y;
        }
//        mPath.lineTo(x, y);
//        mX = x;
//        mY = y;
    }

    private void drawBitmap() {
        if (mBitmap == null) {
            mBitmap = Bitmap.createScaledBitmap(
                    drawableToBitmap(mDrawable),
                    getMeasuredWidth(), getMeasuredHeight(), true);
        }
        mCanvas.drawBitmap(mBitmap, 0, 0, mGesturePaint);
    }

    public void drawPath() {
        mCanvas.drawPath(mPath, mGesturePaint);
    }

    private void drawPoint() {
        mGesturePaint.setColor(mColorInt);
//        画点不采用矩阵来平移缩放
//        mCanvas.setMatrix(mMatrix);
        mCanvas.drawPoints(mPoints, mGesturePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取宽度的模式与大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = getMeasuredWidth();
        //获取高度的模式与大小
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = getMeasuredHeight();
        setMeasuredDimension((int) measureWidth(widthMode, width), (int) measureHeight(heightMode, height));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mGesturePaint.setAntiAlias(isAntiAlias);
        mGesturePaint.setStyle(Paint.Style.STROKE);
        mGesturePaint.setStrokeWidth(mDimension);
        mGesturePaint.setColor(mColorInt);

        isRunning = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        //不断进行绘制
        long t = 0;
        while (isRunning) {
            //每100ms刷新一次
            t = System.currentTimeMillis();
            drawSurface();
            try {
                Thread.sleep(Math.max(0, 1000 - (System.currentTimeMillis() - t)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawSurface() {
        synchronized (mHolder) {
//            initPoint("left_foot.xls");
            try {
                mCanvas = mHolder.lockCanvas();
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                //TODO:现在不用绘制背景了
//                drawBitmap();
                //如果没有数据就不绘制
                if (data.size() != 0) {
                    if (mDrawMode == DRAW_LINE)
                        drawPath();
                    else
                        drawPoint();
                }
//                if (mRects != null)
//                    drawRect();
            } finally {
                if (mCanvas != null)
                    mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    public void initPressureColor(float[] pressure) {
        DrawPressureColor[] drawlist = new DrawPressureColor[pressure.length];
        for (int i = 0; i < pressure.length; i++) {
            drawlist[i] = new DrawPressureColor(pressure[i]);
            Drawable drawable = mDrawables.getDrawable(i);
            if (drawable != null)
                drawable.setColorFilter(drawlist[i].getColor(), PorterDuff.Mode.SRC_IN);
        }
    }

    public void initRect(float[] pressure) {
        //初始化9个矩形
        float maxY = getMeasuredHeight();
        float maxX = getMeasuredWidth();
        final float PADDING = 20;
        float eachX = maxX / 6 - PADDING;
        float eachY = eachX * 0.618f;
        final float startXTop = maxX * 1 / 10;
        final float startYTop = maxY * 8 / 33;
        final float startXMid = maxX * 1 / 5;
        final float startYMid = maxY * 1 / 2;
        final float startXBtm = maxX * 1 / 3;
        final float startYBtm = maxY * 25 / 33;
        final RectF[] rects = {
                new RectF(startXTop, startYTop + 30, startXTop + eachX, startYTop + 30 + eachY),
                new RectF(startXTop + (eachX + PADDING), startYTop + 15, startXTop + (eachX + PADDING) + eachX, startYTop + 15 + eachY),
                new RectF(startXTop + (eachX + PADDING) * 2, startYTop, startXTop + (eachX + PADDING) * 2 + eachX, startYTop + eachY),
                new RectF(startXTop + (eachX + PADDING) * 3, startYTop - 15, startXTop + (eachX + PADDING) * 3 + eachX, startYTop - 15 + eachY),
                new RectF(startXTop + (eachX + PADDING) * 4, startYTop + 20, startXTop + (eachX + PADDING) * 4 + eachX, startYTop + 20 + eachY),
                new RectF(startXMid, startYMid, startXMid + eachX, startYMid + eachY),
                new RectF(startXMid + 3 * eachX + PADDING, startYMid, startXMid + 4 * eachX + PADDING, startYMid + eachY),
                new RectF(startXBtm, startYBtm, startXBtm + eachX, startYBtm + eachY),
                new RectF(startXBtm + 2 * eachX + PADDING, startYBtm, startXBtm + 3 * eachX + PADDING, startYBtm + eachY)};
        mRects = new DrawRects[9];
        for (int i = 0; i < mRects.length; i++) {
            mRects[i] = new DrawRects(rects[i], pressure[i]);
        }
    }

    private void drawRect() {
        float maxX = getMeasuredWidth();
        //初始化Paint和Matrix
        if (isFlipHorizonta) {
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1);
            matrix.postTranslate(maxX, 0);
            mCanvas.setMatrix(matrix);
        }
        mGesturePaint.setStyle(Paint.Style.FILL);

        try {
            for (int i = 0; i < mRects.length; i++) {
                mGesturePaint.setColor(mRects[i].getColor());
                mCanvas.drawRect(mRects[i].getRect(), mGesturePaint);
            }
        } catch (Exception e) {
            Snackbar.make(getRootView(), e.toString(), Snackbar.LENGTH_SHORT).show();
        }
    }


    //测量Drawable长宽
    private void measureDrawable() {
        //TODO:因为不用绘制背景，所以直接测量layer-list
        if (mDrawables == null) {
            throw new RuntimeException("drawable 不能为空");
        }
    }

    private float measureHeight(int mode, int height) {
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                mHeight = height;
                break;
            case MeasureSpec.EXACTLY:
                mHeight = height;
                break;
        }
        return mHeight;
    }

    private float measureWidth(int mode, int width) {
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                mWidth = width;
                break;
            case MeasureSpec.EXACTLY:
                mWidth = width;
                break;
        }
        return mWidth;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public class DrawPressureColor {
        private float pressure;
        @ColorInt
        private int color;

        public DrawPressureColor(float pressure) {
            this.pressure = pressure;
            this.color = getColorFromPressure(this.pressure);
        }

        public float getPressure() {
            return pressure;
        }

        public void setPressure(float pressure) {
            this.pressure = pressure;
        }

        @ColorInt
        public int getColor() {
            return color;
        }

        private int getResColor(int resId) {
            return ContextCompat.getColor(getContext(), resId);
        }

        @ColorInt
        private int getColorFromPressure(float f) {
            @ColorInt int c;
            if (f == 0)
                c = getResColor(R.color.defaultpressurecolor);
            else if (f < 2)
                c = getResColor(R.color.navajowhite);
            else if (f < 4)
                c = getResColor(R.color.gold);
            else if (f < 6)
                c = getResColor(R.color.hotpink);
            else if (f < 10)
                c = getResColor(R.color.tomato);
            else if (f < 15)
                c = getResColor(R.color.red);
            else
                c = getResColor(R.color.coral);
            return c;
        }

    }

    public class DrawRects {
        private RectF rect;
        private float pressure;
        @ColorInt
        private int color;

        public DrawRects(RectF rect, float pressure) {
            this.rect = rect;
            this.pressure = pressure;
            this.color = getColorFromPressure(this.pressure);
        }

        public RectF getRect() {
            return rect;
        }

        public void setRect(RectF rect) {
            this.rect = rect;
        }

        public float getPressure() {
            return pressure;
        }

        public void setPressure(float pressure) {
            this.pressure = pressure;
        }

        @ColorInt
        public int getColor() {
            return color;
        }

        private int getResColor(int resId) {
            return ContextCompat.getColor(getContext(), resId);
        }

        @ColorInt
        private int getColorFromPressure(float f) {
            @ColorInt int c;
            if (f == 0)
                c = getResColor(R.color.white);
            else if (f < 2)
                c = getResColor(R.color.navajowhite);
            else if (f < 4)
                c = getResColor(R.color.gold);
            else if (f < 6)
                c = getResColor(R.color.hotpink);
            else if (f < 10)
                c = getResColor(R.color.tomato);
            else if (f < 15)
                c = getResColor(R.color.red);
            else
                c = getResColor(R.color.coral);
            return c;
        }
    }
}
