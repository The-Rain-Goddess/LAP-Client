package com.raingoddess.lapclient.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by Black Lotus on 8/9/2016.
 */
public class ObservableScrollView extends ScrollView {

    private ScrollViewListener scrollViewListener = null;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {

        View view = (View) getChildAt(getChildCount() - 1);
        int diff = (view.getBottom() - (getHeight() + getScrollY()));
        //System.err.println("Diff: " + diff);
        if (diff == 0) { // if diff is zero, then the bottom has been reached
            if (scrollViewListener != null) {
                scrollViewListener.onScrollEnded(this, x, y, oldx, oldy);
            }
        }
        super.onScrollChanged(x, y, oldx, oldy);
    }
}