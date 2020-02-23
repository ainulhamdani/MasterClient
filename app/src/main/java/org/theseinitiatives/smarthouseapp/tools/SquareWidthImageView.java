package org.theseinitiatives.smarthouseapp.tools;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class SquareWidthImageView extends AppCompatImageView {

    public SquareWidthImageView(Context context) {
        super(context);
    }

    public SquareWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareWidthImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widht = getMeasuredWidth();
        setMeasuredDimension(widht, widht);
    }
}
