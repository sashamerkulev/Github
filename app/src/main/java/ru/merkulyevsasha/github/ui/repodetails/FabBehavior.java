package ru.merkulyevsasha.github.ui.repodetails;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;


public class FabBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    public FabBehavior() {
    }

    public FabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof RecyclerView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {

        int scrollState = ((RecyclerView)dependency).getScrollState();

        if (scrollState == SCROLL_STATE_DRAGGING || scrollState == SCROLL_STATE_SETTLING)
        {
            //child.setSize(FloatingActionButton.SIZE_MINI);
            child.setVisibility(View.INVISIBLE);
        } else { // SCROLL_STATE_IDLE
            //child.setSize(FloatingActionButton.SIZE_NORMAL);
            child.setVisibility(View.VISIBLE);
        }

        return false;
    }

}
