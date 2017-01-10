package com.xufeng.xfproject.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.code19.library.DensityUtil;
import com.google.common.base.Preconditions;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.xufeng.databindingadapter.BindingRecyclerViewAdapter;
import com.xufeng.xfproject.R;
import com.xufeng.xfproject.adapter.SpaceItemDecoration;
import com.xufeng.xfproject.base.BaseFragment;
import com.xufeng.xfproject.contract.JokeContract;
import com.xufeng.xfproject.data.dto.JokeDto;
import com.xufeng.xfproject.databinding.JokeImgFragBinding;
import com.xufeng.xfproject.viewholder.JokeViewHolder;

import java.util.List;


/**
 * Created by xufeng on 2016/12/8.
 */

public class JokeImgFragment extends BaseFragment implements JokeContract.View {

    JokeContract.Presenter mPresenter;
    JokeImgFragBinding mBinding;
    BindingRecyclerViewAdapter<JokeDto> mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = JokeImgFragBinding.inflate(inflater, container, false);
        mBinding.setPresenter(mPresenter);
        View root = mBinding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new BindingRecyclerViewAdapter<>(R.layout.joke_img_item, JokeViewHolder.class);
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerview.addItemDecoration(new SpaceItemDecoration(DensityUtil.dip2px(getContext(),6)));
        mBinding.recyclerview.setAdapter(mAdapter);
        mBinding.refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                mPresenter.loadFirstPage();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                mPresenter.loadNextPage();
            }
        });
        mBinding.loadinglayout.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.loadinglayout.showLoading();
                mPresenter.loadFirstPage();
            }
        });
    }

    @Override
    public void setPresenter(@NonNull JokeContract.Presenter presenter) {
        mPresenter = Preconditions.checkNotNull(presenter);
    }

    @Override
    public void showFirstPage(int pageRows, int totalRows, List<JokeDto> list) {
        if(null==list||list.isEmpty()){
            mBinding.loadinglayout.showEmpty();
            return;
        }
        mAdapter.resetAll(list);
        mBinding.refreshLayout.finishRefreshing();
        mBinding.loadinglayout.showContent();
    }

    @Override
    public void showNextPage(int cruPage, int pageRows, int totalRows, List<JokeDto> list) {
        if(null==list||list.isEmpty()){
            mBinding.loadinglayout.showEmpty();
            return;
        }
        mAdapter.addAll(list);
        mBinding.refreshLayout.finishLoadmore();
        mBinding.loadinglayout.showContent();
    }

    @Override
    public void showPageErrMsg(String code, String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        mBinding.refreshLayout.finishRefreshing();
        mBinding.refreshLayout.finishLoadmore();
        if(mAdapter.getObjects().size()==0){
            mBinding.loadinglayout.showError();
        }
    }


}
