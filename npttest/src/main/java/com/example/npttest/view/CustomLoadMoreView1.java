package com.example.npttest.view;


import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.example.npttest.R;

/**
 * Created by BlingBling on 2016/10/15.
 */

public final class CustomLoadMoreView1 extends LoadMoreView {

    @Override public int getLayoutId() {
        return R.layout.view_load_more1;
    }

    @Override protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
