package com.example.akremlov.nytimes.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.akremlov.nytimes.R;
import com.example.akremlov.nytimes.content.NYLoader;
import com.example.akremlov.nytimes.content.NYItem;
import com.example.akremlov.nytimes.content.NYRecyclerAdapter;
import com.example.akremlov.nytimes.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class NYFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<NYItem>>,
        NYRecyclerAdapter.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    private NYRecyclerAdapter nyRecyclerAdapter;
    private Context mContext;
    private int pageNum = 0;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<NYItem> mDataList = new ArrayList<>();
    private SwipeRefreshLayout mFragment;

    @Override
    public void onScrollEnd() {
        initiateNewLoading();
    }

    public static NYFragment newInstance(String query) {
        NYFragment nyFragment = new NYFragment();
        Bundle sBundle = new Bundle();
        //Page number is associated with queries in assets
        sBundle.putString(Constants.QUERY, query);
        nyFragment.setArguments(sBundle);
        return nyFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getBaseContext();
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        if (savedInstanceState != null) {
            mLinearLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable("layoutManager"));
            mDataList = savedInstanceState.getParcelableArrayList("data");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putParcelable("layoutManager", mLinearLayoutManager.onSaveInstanceState());
            outState.putParcelableArrayList("data", mDataList);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragment = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment, null, false);
        mFragment.setOnRefreshListener(this);
        RecyclerView recyclerView = (RecyclerView) mFragment.findViewById(R.id.recycler);
        nyRecyclerAdapter = new NYRecyclerAdapter(getActivity(), mDataList);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(nyRecyclerAdapter);
        nyRecyclerAdapter.setOnScrollListener(this);
        getLoaderManager().initLoader(Constants.ARTICLES, getArguments(), this);
        return mFragment;
    }

    @Override
    public Loader<List<NYItem>> onCreateLoader(int id, Bundle args) {
        return new NYLoader(mContext, args);
    }

    @Override
    public void onLoadFinished(Loader<List<NYItem>> loader, List<NYItem> data) {
        //Delete it!!! Filter items with image only
//        Iterator<NYItem> itemIterator = data.iterator();
//        while(itemIterator.hasNext()){
//            if(TextUtils.isEmpty(itemIterator.next().getmPhoto())){
//                itemIterator.remove();
//            }
//        }
        if (!mDataList.containsAll(data)) {
            if (loader.getId() == Constants.ARTICLES) {
                mDataList.addAll(data);
                nyRecyclerAdapter.notifyDataSetChanged();
            } else if (loader.getId() == Constants.REFRESH_ARTICLES) {
                mDataList.addAll(0, data);
                nyRecyclerAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NYItem>> loader) {

    }

    public void initiateNewLoading() {
        pageNum++;
        Bundle bundle = getArguments();
        bundle.putInt(Constants.PAGE_NUMBER, pageNum);
        getLoaderManager().restartLoader(Constants.ARTICLES, bundle, this).forceLoad();
    }

    @Override
    public void onRefresh() {
        pageNum = 0;
        Bundle bundle = getArguments();
        bundle.putInt(Constants.PAGE_NUMBER, 0);
        getLoaderManager().restartLoader(Constants.REFRESH_ARTICLES, bundle, this).forceLoad();
        mFragment.setRefreshing(false);
    }
}
