package com.wangshijia.www.radarchartview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/***
 * 自定义网格雷达View
 * @author wangshijia 编辑于 2018/7/19
 */
public class RadarChartView extends View {

    /**
     * 多边形边个数
     */
    private int sideNum = 5;
    /**
     * 刻度绘制网格圈的个数
     */
    private int gridNum = 4;
    /**
     * 360 / sideNum 得出的每一份的角度
     */
    private double angle;

    /**
     * 雷达图圆的半径
     */
    private float radius;
    /**
     * 数据区域参考最大值用于计算设置数据相对于半径的比例
     */
    private float axisMax = 100f;
    /**
     * 字体间距
     */
    private float textSpacing = 8f;

    private PointF pointF = new PointF();
    private Path gridPath = new Path();
    private Path connPath = new Path();
    private Path dataPath = new Path();
    private Path dataOverLinePath = new Path();

    private HashMap<String, Float> valueHash;

    private Paint gridPathPaint;
    private Paint valueStrikePaint;
    private Paint valueFillPaint;
    private Paint valueOverPaint;
    private Paint textPaint;
    private Paint circleValueTextPaint;
    private Paint valueCirclePaint;
    private Paint valueCircleFillPaint;

    /**
     * 数据覆盖层线条是否绘制的宽度 颜色
     */
    private boolean drawValueOverStrike = false;
    private float dataOverStrikeWidth = 1;
    private int colorDataOverStrike = Color.TRANSPARENT;
    /**
     * 网格线的颜色 和宽度
     */
    private int colorGrid = Color.GRAY;
    private float gridStrikeWidth = 2;
    /**
     * 数据描边的是否绘制颜色宽度
     */
    private boolean drawValueStrike = false;
    private int colorDataStrike = Color.TRANSPARENT;
    private float dataStrikeWidth = 1;
    /**
     * 数据填充的颜色
     */
    private boolean drawValueFill = true;
    private int colorDataFill = Color.TRANSPARENT;
    /**
     * 数据文字颜色大小
     */
    private int colorValueText = Color.BLACK;
    private int textSize = 16;

    /**
     * 渐变颜色色值
     */
    private int shaderStartColor = -1;
    private int shaderEndColor = -1;
    /**
     * 是否绘制内容部分尾部的圆圈
     */
    private boolean drawEndCircle = false;
    /**
     * 是否绘制内容部分的数值  valueHash.entry.getValue
     */
    private boolean drawEndValue = true;
    private int colorValueCircle = Color.BLACK;
    private float valueCircleStrikeWidth = 1;
    private int colorValueCircleFill = Color.TRANSPARENT;
    /**
     * 文字大小
     */
    private int circleValueTextSize = 8;
    private int circleValueTextColor = Color.BLACK;
    /**
     * 文字与圆圈的间距
     */
    private float circleTextSpacing = 8;

    private int maxTextNum = 0;


    public RadarChartView(Context context) {
        this(context, null);
    }

    public RadarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RadarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.RadarChartView, defStyleAttr, 0);
        //内容及网格线属性
        this.colorGrid = values.getColor(R.styleable.RadarChartView_colorGrid, colorGrid);
        this.gridStrikeWidth = values.getDimension(R.styleable.RadarChartView_gridStrikeWidth, gridStrikeWidth);
        //填充数据线条颜色
        this.colorDataStrike = values.getColor(R.styleable.RadarChartView_colorDataStrike, colorDataStrike);
        this.dataStrikeWidth = values.getDimension(R.styleable.RadarChartView_dataStrikeWidth, dataStrikeWidth);

        //填充颜色
        this.drawValueFill = values.getBoolean(R.styleable.RadarChartView_drawValueFill, drawValueFill);
        this.colorDataFill = values.getColor(R.styleable.RadarChartView_colorDataFill, colorDataFill);
        this.shaderStartColor = values.getColor(R.styleable.RadarChartView_shaderStartColor, shaderStartColor);
        this.shaderEndColor = values.getColor(R.styleable.RadarChartView_shaderEndColor, shaderEndColor);

        //填充颜色上边覆盖的线条颜色
        this.colorDataOverStrike = values.getColor(R.styleable.RadarChartView_colorDataOverStrike, colorDataOverStrike);
        this.dataOverStrikeWidth = values.getDimension(R.styleable.RadarChartView_dataOverStrikeWidth, dataOverStrikeWidth);

        //外圈文字属性
        this.textSize = values.getDimensionPixelSize(R.styleable.RadarChartView_textSize, textSize);
        this.colorValueText = values.getColor(R.styleable.RadarChartView_colorValueText, colorValueText);
        this.textSpacing = dp2px(getContext(), (int) textSpacing);
        this.textSpacing = values.getDimension(R.styleable.RadarChartView_textSpacing, textSpacing);

        //边数
        this.sideNum = values.getInt(R.styleable.RadarChartView_sideNum, sideNum);
        this.gridNum = values.getInt(R.styleable.RadarChartView_gridNum, gridNum);

        // 内部圆圈属性
        this.drawEndValue = values.getBoolean(R.styleable.RadarChartView_drawEndValue, drawEndValue);
        this.drawEndCircle = values.getBoolean(R.styleable.RadarChartView_drawEndCircle, drawEndCircle);
        this.colorValueCircle = values.getColor(R.styleable.RadarChartView_colorValueCircle, colorValueCircle);
        this.valueCircleStrikeWidth = values.getDimension(R.styleable.RadarChartView_valueCircleStrikeWidth, valueCircleStrikeWidth);
        this.circleTextSpacing = dp2px(getContext(), (int) circleTextSpacing);
        this.circleTextSpacing = values.getDimension(R.styleable.RadarChartView_circleTextSpacing, circleTextSpacing);
        this.colorValueCircleFill = values.getColor(R.styleable.RadarChartView_colorValueCircleFill, colorValueCircleFill);
        this.circleValueTextColor = values.getColor(R.styleable.RadarChartView_circleValueTextColor, circleValueTextColor);
        this.circleValueTextSize = values.getDimensionPixelSize(R.styleable.RadarChartView_circleValueTextSize, circleValueTextSize);

        this.drawValueStrike = values.getBoolean(R.styleable.RadarChartView_drawValueStrike, drawValueStrike);
        this.drawValueOverStrike = values.getBoolean(R.styleable.RadarChartView_drawValueOverStrike, drawValueOverStrike);

        this.axisMax = values.getFloat(R.styleable.RadarChartView_axisMax, axisMax);

        values.recycle();
        valueHash = new LinkedHashMap<>(sideNum);
        initPaint();
    }


    private void initPaint() {
        gridPathPaint = createPaint(colorGrid, 0, gridStrikeWidth, 0);

        valueStrikePaint = createPaint(colorDataStrike, 0, dataStrikeWidth, 0);
        valueFillPaint = createPaint(colorDataFill, 1, 1, 0);

        valueOverPaint = createPaint(colorDataOverStrike, 0, dataOverStrikeWidth, 0);
        textPaint = createPaint(colorValueText, 2, 1, textSize);

        valueCirclePaint = createPaint(colorValueCircle, 0, valueCircleStrikeWidth, 0);
        valueCircleFillPaint = createPaint(colorValueCircleFill, 1, 1, 0);
        circleValueTextPaint = createPaint(circleValueTextColor, 1, 1, circleValueTextSize);
    }

    /**
     * 创建画笔
     */
    private Paint createPaint(int color, int style, float width, int textSize) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        switch (style) {
            case 0:
                paint.setStyle(Paint.Style.STROKE);
                break;
            case 1:
                paint.setStyle(Paint.Style.FILL);
                break;
            case 2:
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                break;
            default:
                break;
        }
        paint.setStrokeWidth(width);
        paint.setTextSize(textSize);
        return paint;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = Math.min(w, h) / 2 - maxTextNum * textSize - textSpacing;
        pointF.x = w / 2;
        pointF.y = h / 2;
        postInvalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);


        int width = getResultMeasureDimension(widthSpec, widthMode);
        int height = getResultMeasureDimension(heightSpec, heightMode);
        setMeasuredDimension(width, height);
    }

    private int getResultMeasureDimension(int size, int mode) {
        int result;
        //View 设置为 wrap_content 的时候
        if (mode == MeasureSpec.AT_MOST) {
            result = Math.min(size, dp2px(getContext(), 400));
        } else {
            result = size;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 2 π r = C 假设 r=1  angle 就是 360 度的多少份
        angle = (Math.PI * 2 / sideNum);

        canvas.translate(pointF.x, pointF.y);

        drawGrids(canvas);

        if (!valueHash.isEmpty()) {
            drawData(canvas);
            drawText(canvas);
        }
    }

    private void drawText(Canvas canvas) {
        Iterator<Map.Entry<String, Float>> iterator = valueHash.entrySet().iterator();

        for (int i = 0; i < valueHash.size(); i++) {
            Map.Entry<String, Float> next = iterator.next();
            double degree = angle * i;

            double x = radius * Math.sin(degree);
            double y = -radius * Math.cos(degree);

            PointF position = getRelativePositionByText(next.getKey(), textPaint, textSpacing, Math.toDegrees(degree), x, y);

            canvas.drawText(next.getKey(), position.x, position.y, textPaint);
        }
    }

    /**
     * 画网格线
     *
     * @param canvas
     */
    private void drawGrids(Canvas canvas) {
        float perR = radius / gridNum;
        for (int i = 1; i <= gridNum; i++) {
            float r = perR * i;
            this.gridPath.reset();
            this.connPath.reset();
            //绘制开始的时 path 起点在原点，需要lineTo第一个点的坐标
            for (int j = 0; j < sideNum; j++) {
                float x = (float) (r * Math.sin(angle * j));
                float y = (float) (-r * Math.cos(angle * j));
                if (j == 0) {
                    gridPath.moveTo(x, y);
                } else {
                    gridPath.lineTo(x, y);
                }
                connPath.moveTo(0, 0);
                connPath.lineTo(x, y);
            }
            gridPath.close();
            canvas.drawPath(gridPath, gridPathPaint);
            canvas.drawPath(connPath, gridPathPaint);
        }
    }

    /**
     * 画内部填充区域
     *
     * @param canvas
     */
    private void drawData(Canvas canvas) {
        Iterator<Map.Entry<String, Float>> iterator = valueHash.entrySet().iterator();
        dataPath.reset();
        dataOverLinePath.reset();

        //坐标缩放系数
        float dataRadius = radius / axisMax;
        for (int i = 0; i < valueHash.size(); i++) {
            Map.Entry<String, Float> data = iterator.next();

            float x = (float) (Math.sin(angle * i) * dataRadius * Math.min(data.getValue(), axisMax));
            float y = -(float) (Math.cos(angle * i) * dataRadius * Math.min(data.getValue(), axisMax));

            if (i == 0) {
                dataPath.moveTo(x, y);
            } else {
                dataPath.lineTo(x, y);
            }

            dataOverLinePath.moveTo(0, 0);
            dataOverLinePath.lineTo(x, y);
        }
        dataPath.close();
        if (drawValueStrike) {
            //画线
            canvas.drawPath(dataPath, valueStrikePaint);
        }

        if (shaderStartColor != -1 && shaderEndColor != -1) {
            valueFillPaint.setShader(new LinearGradient(0, -radius, 0, radius, shaderStartColor, shaderEndColor, Shader.TileMode.CLAMP));
            canvas.drawPath(dataPath, valueFillPaint);
        }

        //是否绘制填充颜色
        if (drawValueFill) {
            canvas.drawPath(dataPath, valueFillPaint);
        }


        //填充颜色上边是否添加额外的线条覆盖
        if (drawValueOverStrike) {
            canvas.drawPath(dataOverLinePath, valueOverPaint);
        }
        //是否绘制数据末尾的源泉
        if (drawEndCircle) {
            drawCircle(canvas, dataRadius);
        }

    }

    private void drawCircle(Canvas canvas, float dataRadius) {

        Iterator<Map.Entry<String, Float>> entryIterator = valueHash.entrySet().iterator();
        for (int i = 0; i < valueHash.size(); i++) {

            Map.Entry<String, Float> data = entryIterator.next();

            double x = (Math.sin(angle * i) * dataRadius * Math.min(data.getValue(), axisMax));
            double y = -(Math.cos(angle * i) * dataRadius * Math.min(data.getValue(), axisMax));

            canvas.drawCircle((float) x, (float) y, 10, valueCircleFillPaint);
            canvas.drawCircle((float) x, (float) y, 10, valueCirclePaint);

            if (drawEndValue) {
                PointF pointF = getRelativePositionByText(String.valueOf(data.getValue()), circleValueTextPaint, circleTextSpacing, Math.toDegrees(angle * i), x, y);
                canvas.drawText(String.valueOf(data.getValue()), pointF.x, pointF.y, circleValueTextPaint);
            }
        }
    }

    /**
     * 原来的坐标和文字以及间距计算出应该放置的点的位置
     * 一共八个方向
     */
    private PointF getRelativePositionByText(String text, Paint paint, float textSpacing, double degree, double pointX, double pointY) {

        PointF resultPoint = new PointF();
        float tW = paint.measureText(text);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float tH = fontMetrics.descent - fontMetrics.ascent;

        //第一象限
        if (pointX > 0 && pointY < 0 && degree != 0) {
            resultPoint = new PointF((float) pointX + textSpacing, (float) pointY);
        }

        //第=象限
        if (pointX < 0 && pointY < 0 && degree != 90.0) {
            resultPoint = new PointF((float) pointX - textSpacing - tW, (float) (pointY));
        }
        //第三象限
        if (pointX < 0 && pointY > 0 && degree != 180.0) {
            resultPoint = new PointF((float) pointX - textSpacing - tW, (float) (pointY + tH / 2));
        }
        //第四象限
        if (pointX > 0 && pointY > 0 && degree != 270 && degree != 180.0) {
            resultPoint = new PointF((float) pointX + textSpacing, (float) (pointY + tH / 2));
        }

        //X轴正
        if (degree == 0) {
            resultPoint = new PointF((float) pointX - tW / 2, (float) (pointY - textSpacing));
        }
        //X轴负
        if (degree == 180) {
            resultPoint = new PointF((float) pointX - tW / 2, (float) (pointY + textSpacing + tH / 2));
        }
        //y轴负
        if (degree == 90) {
            resultPoint = new PointF((float) pointX + textSpacing, (float) (pointY + tH / 4));//修正为除 4
        }
        //y轴正
        if (degree == 270) {
            resultPoint = new PointF((float) pointX - textSpacing - tW, (float) (pointY + tH / 4));
        }
        return resultPoint;
    }


    public int getSideNum() {
        return sideNum;
    }

    public void setSideNum(int sideNum) {
        this.sideNum = sideNum;
        invalidate();
    }

    public int getGridNum() {
        return gridNum;
    }

    public void setGridNum(int gridNum) {
        this.gridNum = gridNum;
    }


    public int getColorGrid() {
        return colorGrid;
    }

    public void setColorGrid(int colorGrid) {
        this.colorGrid = colorGrid;
    }

    public int getColorDataStrike() {
        return colorDataStrike;
    }

    public void setColorDataStrike(int colorDataStrike) {
        this.colorDataStrike = colorDataStrike;
    }

    public int getColorDataFill() {
        return colorDataFill;
    }

    public void setColorDataFill(int colorDataFill) {
        this.colorDataFill = colorDataFill;
    }

    public int getColorDataOverStrike() {
        return colorDataOverStrike;
    }

    public void setColorDataOverStrike(int colorDataOverStrike) {
        this.colorDataOverStrike = colorDataOverStrike;
    }

    public int getColorValueText() {
        return colorValueText;
    }

    public void setColorValueText(int colorValueText) {
        this.colorValueText = colorValueText;
    }

    public boolean isDrawValueStrike() {
        return drawValueStrike;
    }

    public void setDrawValueStrike(boolean drawValueStrike) {
        this.drawValueStrike = drawValueStrike;
    }

    public boolean isDrawValueOverStrike() {
        return drawValueOverStrike;
    }

    public void setDrawValueOverStrike(boolean drawValueOverStrike) {
        this.drawValueOverStrike = drawValueOverStrike;
    }

    public float getAxisMax() {
        return axisMax;
    }

    public void setAxisMax(float axisMax) {
        this.axisMax = axisMax;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public float getTextSpacing() {
        return textSpacing;
    }

    public void setTextSpacing(float textSpacing) {
        this.textSpacing = textSpacing;
    }

    public void addValues(@NonNull String key, @NonNull float presentValue) {
        maxTextNum = Math.max(key.length(), maxTextNum);
        valueHash.put(key, presentValue);
    }

    public void clear() {
        valueHash.clear();
        invalidate();
    }

    public float getGridStrikeWidth() {
        return gridStrikeWidth;
    }

    public void setGridStrikeWidth(float gridStrikeWidth) {
        this.gridStrikeWidth = gridStrikeWidth;
    }

    public float getDataOverStrikeWidth() {
        return dataOverStrikeWidth;
    }

    public void setDataOverStrikeWidth(float dataOverStrikeWidth) {
        this.dataOverStrikeWidth = dataOverStrikeWidth;
    }

    public float getDataStrikeWidth() {
        return dataStrikeWidth;
    }

    public void setDataStrikeWidth(float dataStrikeWidth) {
        this.dataStrikeWidth = dataStrikeWidth;
    }

    public boolean isDrawValueFill() {
        return drawValueFill;
    }

    public void setDrawValueFill(boolean drawValueFill) {
        this.drawValueFill = drawValueFill;
    }

    public int getShaderStartColor() {
        return shaderStartColor;
    }

    public void setShaderStartColor(int shaderStartColor) {
        this.shaderStartColor = shaderStartColor;
    }

    public int getShaderEndColor() {
        return shaderEndColor;
    }

    public void setShaderEndColor(int shaderEndColor) {
        this.shaderEndColor = shaderEndColor;
    }

    public boolean isDrawEndCircle() {
        return drawEndCircle;
    }

    public void setDrawEndCircle(boolean drawEndCircle) {
        this.drawEndCircle = drawEndCircle;
    }

    public boolean isDrawEndValue() {
        return drawEndValue;
    }

    public void setDrawEndValue(boolean drawEndValue) {
        this.drawEndValue = drawEndValue;
    }

    public int getColorValueCircle() {
        return colorValueCircle;
    }

    public void setColorValueCircle(int colorValueCircle) {
        this.colorValueCircle = colorValueCircle;
    }

    public int getColorValueCircleFill() {
        return colorValueCircleFill;
    }

    public void setColorValueCircleFill(int colorValueCircleFill) {
        this.colorValueCircleFill = colorValueCircleFill;
    }

    public int getCircleValueTextSize() {
        return circleValueTextSize;
    }

    public void setCircleValueTextSize(int circleValueTextSize) {
        this.circleValueTextSize = circleValueTextSize;
    }

    public float getCircleTextSpacing() {
        return circleTextSpacing;
    }

    public void setCircleTextSpacing(float circleTextSpacing) {
        this.circleTextSpacing = circleTextSpacing;
    }

    public int getCircleValueTextColor() {
        return circleValueTextColor;
    }

    public void setCircleValueTextColor(int circleValueTextColor) {
        this.circleValueTextColor = circleValueTextColor;
    }

    public float getValueCircleStrikeWidth() {
        return valueCircleStrikeWidth;
    }

    public void setValueCircleStrikeWidth(float valueCircleStrikeWidth) {
        this.valueCircleStrikeWidth = valueCircleStrikeWidth;
    }


    private float getCenterY() {
        return pointF.y;
    }

    private float getCenterX() {
        return pointF.x;
    }

    public static int dp2px(Context context, int dp) {
        return (int) (getDensity(context) * dp + 0.5);
    }

    public static int sp2px(Context context, int sp) {
        return (int) (getFontDensity(context) * sp + 0.5);
    }

    public static int px2dp(Context context, int px) {
        return (int) (px / getDensity(context) + 0.5);
    }

    public static int px2sp(Context context, int px) {
        return (int) (px / getFontDensity(context) + 0.5);
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static float getFontDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }
}
