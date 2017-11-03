package com.example.flipboard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.Toast;

/**
 * author Gao
 * date 2017/11/1 0001
 * description 模仿 Flipboard 动画
 */
@SuppressWarnings("unused")
public class FlipBoardView extends AppCompatImageView {

    private Bitmap bitmap;
    private Paint flipPaint = new Paint();
    private Paint slopePaint = new Paint();
    private Path flatPath = new Path();
    private Path slopePath = new Path();
    private Camera flatCamera = new Camera();
    private Camera slopeCamera = new Camera();
    private AnimatorSet animatorSet = new AnimatorSet();

    private float rotateAngle = 0;
    private float slopeAngleY = 0;
    private float flipAngleY = 0;
    private Context context;

    private float customSlope = -45;
    private float customFlip = 30;


    public FlipBoardView(Context context) {
        super(context);
    }

    public FlipBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlipBoardView);
        Drawable drawable = a.getDrawable(R.styleable.FlipBoardView_img); //button的名称
        this.customSlope = a.getInteger(R.styleable.FlipBoardView_slopeAngle, -45);
        this.customFlip = a.getInteger(R.styleable.FlipBoardView_flipAngle, 30);
        bitmap = drawable != null ? ((BitmapDrawable) drawable).getBitmap() : null;
        a.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bitmap == null) return;
        float bitmapWidth = bitmap.getWidth();
        float bitmapHeight = bitmap.getHeight();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        float x = centerX - bitmapWidth / 2;
        float y = centerY - bitmapHeight / 2;

        if (rotateAngle > -90 && rotateAngle < 90) {
            flatPath.reset();
            flatPath.moveTo(x, y);
            float movePath = (float) Math.tan(rotateAngle * Math.PI / 180) * (bitmapHeight / 2);
            flatPath.rLineTo(bitmapWidth / 2 - movePath, 0);
            flatPath.rLineTo(2 * movePath, bitmapHeight);
            flatPath.lineTo(x, y + bitmapHeight);

            slopePath.reset();
            slopePath.moveTo(x + bitmapWidth, y + bitmapHeight);
            slopePath.rLineTo(-bitmapWidth / 2 + movePath, 0);
            slopePath.rLineTo(-2 * movePath, -bitmapHeight);
            slopePath.lineTo(x + bitmapWidth, y);

        } else if (rotateAngle > 90 && rotateAngle < 270) {
            flatPath.reset();
            flatPath.moveTo(x + bitmapWidth, y + bitmapHeight);
            float movePath = (float) Math.tan(rotateAngle * Math.PI / 180) * (bitmapHeight / 2);
            flatPath.rLineTo(-bitmapWidth / 2 + movePath, 0);
            flatPath.rLineTo(-2 * movePath, -bitmapHeight);
            flatPath.lineTo(x + bitmapWidth, y);

            slopePath.reset();
            slopePath.moveTo(x, y);
            float moveCameraPath = (float) Math.tan(rotateAngle * Math.PI / 180) * (bitmapHeight / 2);
            slopePath.rLineTo(bitmapWidth / 2 - moveCameraPath, 0);
            slopePath.rLineTo(2 * moveCameraPath, bitmapHeight);
            slopePath.lineTo(x, y + bitmapHeight);

        }


        canvas.save();
        flatCamera.save();
        flatCamera.setLocation(0, 0, -60);
        flatCamera.rotateY(flipAngleY);
        canvas.translate(centerX, centerY);
        canvas.rotate(-rotateAngle, 0, 0);
        flatCamera.applyToCanvas(canvas);
        canvas.rotate(rotateAngle, 0, 0);
        canvas.translate(-centerX, -centerY);
        canvas.clipPath(flatPath);
        canvas.drawBitmap(bitmap, x, y, flipPaint);
        flatCamera.restore();
        canvas.restore();

        canvas.save();
        slopeCamera.save();
        slopeCamera.setLocation(0, 0, -60);
        slopeCamera.rotateY(slopeAngleY);
        canvas.translate(centerX, centerY);
        canvas.rotate(-rotateAngle, 0, 0);
        slopeCamera.applyToCanvas(canvas);
        canvas.rotate(rotateAngle, 0, 0);
        canvas.translate(-centerX, -centerY);
        canvas.clipPath(slopePath);
        canvas.drawBitmap(bitmap, x, y, slopePaint);
        slopeCamera.restore();
        canvas.restore();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animatorSet.isRunning()) animatorSet.end();
    }

    public void start() {
        if (bitmap == null) {
            Toast.makeText(context, "图片为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (animatorSet.isRunning()) animatorSet.end();

        this.rotateAngle = 0;
        this.flipAngleY = 0;
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "slopeAngleY", 0, customSlope);
        animator1.setDuration(1000);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(this, "rotateAngle", 0, 270);
        animator2.setDuration(1000);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(this, "flipAngleY", 0, customFlip);
        animator3.setDuration(1000);

        animatorSet.playSequentially(animator1, animator2, animator3);
        animatorSet.start();
    }


    public float getRotateAngle() {
        return rotateAngle;
    }

    public void setRotateAngle(float rotateAngle) {
        this.rotateAngle = rotateAngle;
        invalidate();
    }

    public float getSlopeAngleY() {
        return slopeAngleY;
    }

    public void setSlopeAngleY(float slopeAngleY) {
        this.slopeAngleY = slopeAngleY;
        invalidate();
    }

    public float getFlipAngleY() {
        return flipAngleY;
    }

    public void setFlipAngleY(float flipAngleY) {
        this.flipAngleY = flipAngleY;
        invalidate();
    }
}
