package com.example.akremlov.nytimes.fragment;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.akremlov.nytimes.R;
import com.example.akremlov.nytimes.content.NYLoader;
import com.example.akremlov.nytimes.content.NYItem;
import com.example.akremlov.nytimes.content.NYRecyclerAdapter;
import com.example.akremlov.nytimes.utils.Constants;
import com.example.akremlov.nytimes.utils.InternetChangeReceiver;

import java.util.ArrayList;
import java.util.List;

public class NYFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<NYItem>>,
        NYRecyclerAdapter.OnScrollListener, SwipeRefreshLayout.OnRefreshListener, InternetChangeReceiver.OnInternetListener {

    private NYRecyclerAdapter mRecyclerAdapter;
    private Context mContext;
    private int pageNum = 0;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<NYItem> mDataList = new ArrayList<>();
    private SwipeRefreshLayout mFragment;
    private ProgressBar mProgressBar;
    private int mTries = 0;
    private InternetChangeReceiver mInternetReceiver = new InternetChangeReceiver();

    @Override
    public void onScrollEnd() {
        mProgressBar.setVisibility(View.GONE);
        initiateNewLoading();
    }

    public static NYFragment newInstance(String query) {
        NYFragment nyFragment = new NYFragment();
        Bundle sBundle = new Bundle();
        //Page number is associated with queries in assets
        sBundle.putString(Constants.QUERY, query);
        sBundle.putInt(Constants.PAGE_NUMBER, 0);
        nyFragment.setArguments(sBundle);
        return nyFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getBaseContext();
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        if (savedInstanceState != null) {
            mLinearLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(Constants.LAYOUT_MANAGER));
            mDataList = savedInstanceState.getParcelableArrayList(Constants.DATA);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext.registerReceiver(mInternetReceiver, new IntentFilter(Constants.CONNECTIVITY_CHANGE));
        mInternetReceiver.setListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mContext.unregisterReceiver(mInternetReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putParcelable(Constants.LAYOUT_MANAGER, mLinearLayoutManager.onSaveInstanceState());
            outState.putParcelableArrayList(Constants.DATA, mDataList);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragment = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment, null, false);
        mFragment.setOnRefreshListener(this);
        mProgressBar = (ProgressBar) mFragment.findViewById(R.id.progressBar);
        RecyclerView recyclerView = (RecyclerView) mFragment.findViewById(R.id.recycler);
        mRecyclerAdapter = new NYRecyclerAdapter(getActivity(), mDataList);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setOnScrollListener(this);
        if (savedInstanceState == null) {
            mProgressBar.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(Constants.ARTICLES, getArguments(), this);
        }
        return mFragment;
    }

    @Override
    public Loader<List<NYItem>> onCreateLoader(int id, Bundle args) {
        return new NYLoader(mContext, args);
    }

    @Override
    public void onLoadFinished(Loader<List<NYItem>> loader, List<NYItem> data) {


        if (data.isEmpty() && InternetChangeReceiver.isNetworkAvailable()) {
            retryLoading();
            return;
        } else if (data.isEmpty() && !(InternetChangeReceiver.isNetworkAvailable())) {
            mProgressBar.setVisibility(View.GONE);
            return;
        }
        if (!mDataList.isEmpty() && mDataList.containsAll(data)) {
            mProgressBar.setVisibility(View.GONE);
            return;
        }
        if (loader.getId() == Constants.ARTICLES) {
            mDataList.addAll(data);
        } else if (loader.getId() == Constants.REFRESH_ARTICLES) {
            mDataList.addAll(0, data);
        }
        mTries = 0;
        mRecyclerAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<NYItem>> loader) {

    }

    public void initiateNewLoading() {
        if (InternetChangeReceiver.isNetworkAvailable()) {
            pageNum++;
            Bundle bundle = getArguments();
            bundle.putInt(Constants.PAGE_NUMBER, pageNum);
            getLoaderManager().restartLoader(Constants.ARTICLES, bundle, this).forceLoad();
        } else {
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRefresh() {
        if (InternetChangeReceiver.isNetworkAvailable()) {
            pageNum = 0;
            mTries = 0;
            Bundle bundle = getArguments();
            bundle.putInt(Constants.PAGE_NUMBER, 0);
            mProgressBar.setVisibility(View.VISIBLE);
            getLoaderManager().restartLoader(Constants.REFRESH_ARTICLES, bundle, this).forceLoad();
            mFragment.setRefreshing(false);
        } else {
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            mFragment.setRefreshing(false);
        }

    }

    public void retryLoading() {
        mTries++;
        if (mTries == Constants.TIMES_TO_TRY_DOWNLOADING) {
            mProgressBar.setVisibility(View.GONE);
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(Constants.ARTICLES, getArguments(), this).forceLoad();
    }

    @Override
    public void initiateLoading() {
        retryLoading();
    }
}
