package com.yan.refreshloadlayouttest.widget;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.pullrefreshlayout.ShowGravity;
import com.yan.refreshloadlayouttest.R;

/**
 * Created by Administrator on 2017/10/1 0001.
 */

public class DiDiHeader extends FrameLayout implements PullRefreshLayout.OnPullListener, NestedScrollView.OnScrollChangeListener {
    private PullRefreshLayout prl;
    private ClassicsHeader loadingView;
    private View fixedHeader;
    private boolean isNeedDispatchTouchEvent;

    public DiDiHeader(Context context, final PullRefreshLayout prl) {
        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.prl = prl;
        prl.setHeaderFront(false);
        prl.setHeaderShowGravity(ShowGravity.PLACEHOLDER);
        scrollInit();
        LayoutInflater.from(context).inflate(R.layout.didi_header, this, true);
        fixedHeader = findViewById(R.id.fixed_top);
        findViewById(R.id.test).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext().getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
            }
        });
        fixedHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext().getApplicationContext(), "you touch the header", Toast.LENGTH_SHORT).show();
            }
        });
        loadingView = (ClassicsHeader) findViewById(R.id.loading);
    }

    private void scrollInit() {
        post(new Runnable() {
            @Override
            public void run() {
                View target = prl.getTargetView();
                prl.setRefreshTriggerDistance(loadingView.getHeight());
                target.setOverScrollMode(OVER_SCROLL_NEVER);
                target.setPadding(target.getPaddingLeft()
                        , fixedHeader.getHeight()
                        , target.getPaddingRight()
                        , target.getPaddingBottom());
                target.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                            isNeedDispatchTouchEvent = true;//这里重置是否需要分发事件的标志位
                        } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE && prl.isDragVertical()) {
                            isNeedDispatchTouchEvent = false;//如果prl处在纵向拖动，则取消header的事件分发
                        }
                        if (isNeedDispatchTouchEvent) {
                            DiDiHeader.this.dispatchTouchEvent(event);
                        }
                        return false;
                    }
                });
                if (target instanceof NestedScrollView) {
                    ((NestedScrollView) target).setOnScrollChangeListener(DiDiHeader.this);
                    ((NestedScrollView) target).setClipToPadding(false);
                }//else if target instanceof RecyclerView ...
            }
        });
    }

    @Override
    public void onPullChange(float percent) {
        loadingView.setTranslationY(prl.getMoveDistance());
    }

    @Override
    public void onPullHoldTrigger() {
        loadingView.onPullHoldTrigger();
    }

    @Override
    public void onPullHoldUnTrigger() {
        loadingView.onPullHoldUnTrigger();
    }

    @Override
    public void onPullHolding() {
        loadingView.onPullHolding();
    }

    @Override
    public void onPullFinish() {
        loadingView.onPullFinish();
    }

    @Override
    public void onPullReset() {
        loadingView.onPullReset();
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        scrollTo(0, scrollY);
    }
}
