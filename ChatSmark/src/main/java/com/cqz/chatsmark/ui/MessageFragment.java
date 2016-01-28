package com.cqz.chatsmark.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessageFragment extends Fragment {
	private View mContentView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if(mContentView!=null){
			return mContentView;
		}
		mContentView=inflater.inflate(R.layout.fragment_message, container, false);
		return mContentView;
	}
	@Override
	public void onDestroyView() {
		// 移除当前视图，防止重复加载相同视图使得程序闪退
		((ViewGroup) mContentView.getParent()).removeView(mContentView);
		super.onDestroyView();
	}
}
