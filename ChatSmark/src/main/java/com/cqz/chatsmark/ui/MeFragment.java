package com.cqz.chatsmark.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cqz.chatsmark.xmpp.XMPPUtil;

public class MeFragment extends Fragment {
	private View mContentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(mContentView!=null){
			return mContentView;
		}
		mContentView=inflater.inflate(R.layout.fragment_me, container, false);
		initialWidgets();
		return mContentView;
	}

	private void initialWidgets() {
		View containerLogout=mContentView.findViewById(R.id.container_log_out);
		containerLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				XMPPUtil.getInstance().closeConnection();
				Intent intent=new Intent(getActivity(),LoginActivity.class);
				startActivity(intent);
				getActivity().finish();
			}
		});
	}

	@Override
	public void onDestroyView() {

        ((ViewGroup) mContentView.getParent()).removeView(mContentView);
		super.onDestroyView();
	}
}
