package nl.schmit.animationView;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

import nl.schmit.animationView.crossView.R;

/**
 * Animating check mark.
 */
public class ExclamationView extends View {

    private static final String TAG = ExclamationView.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static final long CHECK_ANIM_DURATION = 300L;
    private static final long SCALE_ANIM_DELAY = 280L;
    private static final long SCALE_ANIM_DURATION = 250L;
    private static final float DEFAULT_STROKE_WIDTH = 8F;
    private static final int DEFAULT_STROKE_COLOR = 0xFFFF0000; // greenish
    private static final float SCALE_MIN = 0.80F;

    private Interpolator mCheckInterpolator;
    /**
     * The path of the circle around the check mark
     */
    private Path mPathCircle;
    /**
     * The path of the check mark
     */
    private Path mPathCheck,mPathCheck2;
    /**
     * The length of the start of the check mark, before the pivot point
     */
    private float mMinorContourLength,mMinorContourLength2;
    /**
     * The length of the check mark after the pivot point, and up to the end point.
     */
    private float mMajorContourLength,mMajorContourLength2;
    /**
     * The size of the check mark and circle paths.
     */
    private float mStrokeWidth = DEFAULT_STROKE_WIDTH;
    private int mStrokeColor = DEFAULT_STROKE_COLOR;
    /**
     * A Rect describing the area on this View's canvas where the check mark should be drawn.
     * This is intended to account for padding.
     */
    private RectF mDrawingRect;
    /**
     * A Rect describing the drawable area for the circle around the check mark.
     * This takes into account the extra room needed for the stroke width.
     */
    private RectF mCircleRect;
    private Paint mPaint;
    private PathMeasure mPathMeasure,mPathMeasure2;
    /**
     * A pre-allocated float array to hold path measure results.
     */
    private float[] mPoint,mPoint2;
    /**
     * Where the check mark starts
     */
    private PointF mCheckStart;
    /**
     * Where the check mark turns upward
     */
    private PointF mCheckPivot;
    /**
     * Where the check mark ends
     */
    private PointF mCheckEnd;


    private PointF mCheckStart2;
    /**
     * Where the check mark turns upward
     */
    private PointF mCheckPivot2;
    /**
     * Where the check mark ends
     */
    private PointF mCheckEnd2;
    /**
     * Where the circle border starts
     */
    private PointF mCircleStart;
    private ValueAnimator mCheckAnimator;
    private ValueAnimator mCircleAnimator;
    private ValueAnimator mScaleAnimator;
    private boolean mChecked = false;

    public ExclamationView(Context context) {
        super(context);
        init(context, null);
    }

    public ExclamationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExclamationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExclamationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        resolveAttributes(context, attrs);
        mPathCheck = new Path();
        mPathCheck2 = new Path();
        mPathCircle = new Path();
        mDrawingRect = new RectF();
        mCircleRect = new RectF();
        mPathMeasure = new PathMeasure();
        mPoint = new float[2];
        mPathMeasure2 = new PathMeasure();
        mPoint2 = new float[2];
        mCheckStart = new PointF();
        mCheckPivot = new PointF();
        mCheckEnd = new PointF();

        mCheckStart2 = new PointF();
        mCheckPivot2 = new PointF();
        mCheckEnd2 = new PointF();
        mCircleStart = new PointF();
        mCheckAnimator = ValueAnimator.ofFloat(0, 1);
        mCircleAnimator = ValueAnimator.ofFloat(0, 1);
        mScaleAnimator = ValueAnimator.ofFloat(1, SCALE_MIN, 1);
        mCheckInterpolator = createCheckInterpolatorCompat();
        mPaint = createPaint(mStrokeColor, mStrokeWidth);
    }

    private void resolveAttributes(Context c, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        
        TypedArray a = c.getTheme().obtainStyledAttributes(attrs, R.styleable.CrossView, 0, 0);

        try {
            mStrokeWidth = a.getDimension(R.styleable.CrossView_crossView_strokeWidth, DEFAULT_STROKE_WIDTH);
            mStrokeColor = a.getColor(R.styleable.CrossView_crossView_strokeColor, DEFAULT_STROKE_COLOR);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mDrawingRect.left = getPaddingLeft();
            mDrawingRect.top = getPaddingTop();
            mDrawingRect.right = getMeasuredWidth() - getPaddingRight();
            mDrawingRect.bottom = getMeasuredHeight() - getPaddingBottom();

            mCheckStart.x = mDrawingRect.left + mDrawingRect.width() / 2;
            mCheckStart.y = mDrawingRect.top + mDrawingRect.height() / 5;
            mCheckPivot.x = mDrawingRect.left + mDrawingRect.width() /2;
            mCheckPivot.y = mDrawingRect.top + mDrawingRect.height() /2;
            mCheckEnd.x = mDrawingRect.left + mDrawingRect.width() / 2;
            mCheckEnd.y = mDrawingRect.top + 3*(mDrawingRect.height() / 5) ;


            mCheckStart2.x = mDrawingRect.left + mDrawingRect.width() / 2;
            mCheckStart2.y = mDrawingRect.top + 4*(mDrawingRect.height() / 5)-5 ;
            mCheckPivot2.x = mDrawingRect.left + mDrawingRect.width() / 2;
            mCheckPivot2.y = mDrawingRect.top + 4*(mDrawingRect.height() / 5) ;
            mCheckEnd2.x = mDrawingRect.left + mDrawingRect.width() / 2;
            mCheckEnd2.y = mDrawingRect.top + 4*(mDrawingRect.height() / 5) ;


            mMinorContourLength = distance(mCheckStart.x, mCheckStart.y, mCheckPivot.x, mCheckPivot.y);
            mMajorContourLength = distance(mCheckPivot.x, mCheckPivot.y, mCheckEnd.x, mCheckEnd.y);

            mMinorContourLength2 = distance(mCheckStart2.x, mCheckStart2.y, mCheckPivot2.x, mCheckPivot2.y);
            mMajorContourLength2 = distance(mCheckPivot2.x, mCheckPivot2.y, mCheckEnd2.x, mCheckEnd2.y);

            mCircleRect.left = mDrawingRect.left + mStrokeWidth /2;
            mCircleRect.top = mDrawingRect.top + mStrokeWidth /2;
            mCircleRect.right = mDrawingRect.right - mStrokeWidth /2;
            mCircleRect.bottom = mDrawingRect.bottom - mStrokeWidth /2;
            mCircleStart.x = mCircleRect.right;
            mCircleStart.y = mCircleRect.bottom /2;

            if (DEBUG && (mDrawingRect.width() != mDrawingRect.height())) {
                Log.w(TAG, "WARNING: " + ExclamationView.class.getSimpleName() + " will look weird because you've given it a non-square drawing area.  " +
                        "Make sure the width, height, and padding resolve to a square.");
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mChecked) {
            return;
        }
        canvas.drawPath(mPathCheck, mPaint);
        canvas.drawPath(mPathCheck2, mPaint);
        canvas.drawPath(mPathCircle, mPaint);
    }

    //region instance methods

    /**
     * Tell this {@link ExclamationView} to animate into the checked state.
     */
    public void check() {
        mChecked = true;
        mCheckAnimator.removeAllUpdateListeners();
        mCheckAnimator.setDuration(CHECK_ANIM_DURATION)
                .setInterpolator(mCheckInterpolator);
        mCheckAnimator.addUpdateListener(mCheckAnimatorListener);

        mCircleAnimator.removeAllUpdateListeners();
        mCircleAnimator.setDuration(CHECK_ANIM_DURATION)
                .setInterpolator(mCheckInterpolator);
        mCircleAnimator.addUpdateListener(mCircleAnimatorListener);

        mScaleAnimator.removeAllUpdateListeners();
        mScaleAnimator.setDuration(SCALE_ANIM_DURATION)
                .setStartDelay(SCALE_ANIM_DELAY);
        mScaleAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mScaleAnimator.addUpdateListener(mScaleAnimatorListener);

        mCheckAnimator.start();
        mCircleAnimator.start();
        mScaleAnimator.start();
    }

    /**
     * Reset to an unchecked state.  This will not animate.
     */
    public void uncheck() {
        mChecked = false;
        invalidate();
    }
    //endregion instance methods

    //region private methods
    private Paint createPaint(@ColorInt int color, float strokeWidth) {
        Paint p = new Paint();
        p.setColor(color);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(strokeWidth);
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setAntiAlias(true);
        p.setStrokeCap(Paint.Cap.ROUND);
        return p;
    }

    private Interpolator createCheckInterpolatorCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new PathInterpolator(0.755F, 0.05F, 0.855F, 0.06F);
        } else {
            return new AccelerateInterpolator();
        }
    }

    /**
     * What does the check mark path look like at it's full length?
     */
    private void setCheckPathFull() {
        mPathCheck.reset();
        mPathCheck.moveTo(mCheckStart.x, mCheckStart.y);
        mPathCheck.lineTo(mCheckPivot.x, mCheckPivot.y);
        mPathCheck.lineTo(mCheckEnd.x, mCheckEnd.y);
        mPathCheck2.reset();
        mPathCheck2.moveTo(mCheckStart2.x, mCheckStart2.y);
        mPathCheck2.lineTo(mCheckPivot2.x, mCheckPivot2.y);
        mPathCheck2.lineTo(mCheckEnd2.x, mCheckEnd2.y);
    }

    /**
     * What does the check mark path look like at {@code percent} of it's total length?
     */
    private void setCheckPathPercentage(@FloatRange(from = 0, to = 1) float percent) {
        setCheckPathFull();
        final float totalLength = mMinorContourLength + mMajorContourLength;
        final float pivotPercent = mMinorContourLength / totalLength;

        // TODO: try this with a simple getSegment();
        if (percent > pivotPercent) {
            final float remainder = percent - pivotPercent;
            final float distance = totalLength * remainder;
            mPathCheck.reset();
            mPathCheck.moveTo(mCheckPivot.x, mCheckPivot.y);
            mPathCheck.lineTo(mCheckEnd.x, mCheckEnd.y);
            mPathMeasure.setPath(mPathCheck, false);
            mPathMeasure.getPosTan(distance, mPoint, null);
            mPathCheck.reset();
            mPathCheck.moveTo(mCheckStart.x, mCheckStart.y);
            mPathCheck.lineTo(mCheckPivot.x, mCheckPivot.y);
            mPathCheck.lineTo(mPoint[0], mPoint[1]);


            mPathCheck2.reset();
            mPathCheck2.moveTo(mCheckPivot2.x, mCheckPivot2.y);
            mPathCheck2.lineTo(mCheckEnd2.x, mCheckEnd2.y);
            mPathMeasure2.setPath(mPathCheck2, false);
            mPathMeasure2.getPosTan(distance, mPoint2, null);
            mPathCheck2.reset();
            mPathCheck2.moveTo(mCheckStart2.x, mCheckStart2.y);
            mPathCheck2.lineTo(mCheckPivot2.x, mCheckPivot2.y);
            mPathCheck2.lineTo(mPoint2[0], mPoint2[1]);
        } else if (percent < pivotPercent) {
            final float minorPercent = percent / pivotPercent;
            final float distance = mMinorContourLength * minorPercent;
            mPathMeasure.setPath(mPathCheck, false);
            mPathMeasure.getPosTan(distance, mPoint, null);
            mPathCheck.reset();
            mPathCheck.moveTo(mCheckStart.x, mCheckStart.y);
            mPathCheck.lineTo(mPoint[0], mPoint[1]);

            mPathMeasure2.setPath(mPathCheck2, false);
            mPathMeasure2.getPosTan(distance, mPoint2, null);
            mPathCheck2.reset();
            mPathCheck2.moveTo(mCheckStart2.x, mCheckStart2.y);
            mPathCheck2.lineTo(mPoint2[0], mPoint2[1]);

        } else if (percent == pivotPercent) {
            mPathCheck.lineTo(mCheckPivot.x, mCheckPivot.y);
            mPathCheck2.lineTo(mCheckPivot2.x, mCheckPivot2.y);
        }
    }

    private void setCirclePathPercentage(@FloatRange(from = 0, to = 1) float percent) {
        mPathCircle.reset();
        mPathCircle.moveTo(mCircleStart.x, mCircleStart.y);
        mPathCircle.addArc(mCircleRect, 0, 360);

        mPathMeasure.setPath(mPathCircle, false);
        final float distance = mPathMeasure.getLength() * percent;
        mPathMeasure.getPosTan(distance, mPoint, null);
        mPathCircle.reset();
        mPathCircle.moveTo(mCircleStart.x, mCircleStart.y);
        mPathCircle.arcTo(mCircleRect, 0, (359 * percent));
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        final float xAbs = Math.abs(x1 - x2);
        final float yAbs = Math.abs(y1 - y2);
        return (float) Math.sqrt((yAbs * yAbs) + (xAbs * xAbs));
    }
    //endregion private methods

    //region animator listeners
    private final ValueAnimator.AnimatorUpdateListener mCheckAnimatorListener = animation -> {
        final float fraction = animation.getAnimatedFraction();
        setCheckPathPercentage(fraction);
        invalidate();
    };

    private final ValueAnimator.AnimatorUpdateListener mCircleAnimatorListener = animation -> {
        final float fraction = animation.getAnimatedFraction();
        setCirclePathPercentage(fraction);
        invalidate();
    };

    private final ValueAnimator.AnimatorUpdateListener mScaleAnimatorListener = animation -> {
        final float value = (float) animation.getAnimatedValue();
        ExclamationView.this.setScaleX(value);
        ExclamationView.this.setScaleY(value);
        invalidate();
    };
    //endregion
}
