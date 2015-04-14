package com.hannesdorfmann.mosby.sample.mvp.lce.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.layout.MvpViewStateFrameLayout;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.CastedArrayListLceViewState;
import com.hannesdorfmann.mosby.sample.R;
import com.hannesdorfmann.mosby.sample.mvp.CountriesAdapter;
import com.hannesdorfmann.mosby.sample.mvp.CountriesErrorMessage;
import com.hannesdorfmann.mosby.sample.mvp.CountriesPresenter;
import com.hannesdorfmann.mosby.sample.mvp.CountriesView;
import com.hannesdorfmann.mosby.sample.mvp.lce.SimpleCountriesPresenter;
import com.hannesdorfmann.mosby.sample.mvp.model.Country;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class CountriesLayout extends MvpViewStateFrameLayout<CountriesPresenter>
    implements CountriesView, SwipeRefreshLayout.OnRefreshListener {

  @InjectView(R.id.loadingView) View loadingView;
  @InjectView(R.id.errorView) TextView errorView;
  @InjectView(R.id.contentView) SwipeRefreshLayout contentView;
  @InjectView(R.id.recyclerView) RecyclerView recyclerView;

  private CountriesAdapter adapter;

  public CountriesLayout(Context context) {
    super(context);
  }

  public CountriesLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CountriesLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(21)
  public CountriesLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();

    contentView.setOnRefreshListener(this);

    adapter = new CountriesAdapter(getContext());
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter);
  }

  @Override protected CountriesPresenter createPresenter() {
    return new SimpleCountriesPresenter();
  }

  @Override public ViewState<CountriesView> createViewState() {
    return new CastedArrayListLceViewState<List<Country>, CountriesView>();
  }

  @Override public CastedArrayListLceViewState<List<Country>, CountriesView> getViewState() {
    return (CastedArrayListLceViewState) super.getViewState();
  }

  @Override public void onNewViewStateInstance() {
    loadData(false);
  }

  @Override public void showLoading(boolean pullToRefresh) {
    errorView.setVisibility(View.GONE);

    if (pullToRefresh) {
      if (pullToRefresh && !contentView.isRefreshing()) {
        // Workaround for measure bug: https://code.google.com/p/android/issues/detail?id=77712
        contentView.post(new Runnable() {
          @Override public void run() {
            contentView.setRefreshing(true);
          }
        });
      }
      contentView.setVisibility(View.VISIBLE);
    } else {
      loadingView.setVisibility(View.VISIBLE);
      contentView.setVisibility(View.GONE);
    }
    getViewState().setStateShowLoading(pullToRefresh);
  }

  @Override public void showContent() {
    loadingView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    contentView.setVisibility(View.VISIBLE);

    contentView.setRefreshing(false);
    getViewState().setStateShowContent(adapter.getCountries());
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    getViewState().setStateShowError(e, pullToRefresh);

    String msg = CountriesErrorMessage.get(e, pullToRefresh, getContext());

    loadingView.setVisibility(View.GONE);
    if (pullToRefresh) {
      contentView.setRefreshing(false);
      if (!isRestoringViewState()) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
      }
    } else {
      contentView.setVisibility(View.GONE);
      errorView.setText(msg);
      errorView.setVisibility(View.VISIBLE);
    }
  }

  @Override public void setData(List<Country> data) {
    adapter.setCountries(data);
    adapter.notifyDataSetChanged();
  }

  @Override public void loadData(boolean pullToRefresh) {
    presenter.loadCountries(pullToRefresh);
  }

  @Override public void onRefresh() {
    loadData(true);
  }

  @OnClick(R.id.errorView) public void onErrorViewClicked() {
    loadData(false);
  }

  @Override protected Parcelable onSaveInstanceState() {
    Log.e("Test", "saveInstanceState");
    return super.onSaveInstanceState();
  }

  @Override protected void onRestoreInstanceState(Parcelable state) {
    super.onRestoreInstanceState(state);
    Log.e("Test", "restore");
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
  }
}
