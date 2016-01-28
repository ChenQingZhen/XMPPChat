package com.cqz.chatsmark.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	private FragmentTabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initialWidgets();
	}

	private void initialWidgets() {
		mTabHost = (FragmentTabHost) findViewById(R.id.tab_host);
		
		mTabHost.setup(this, getSupportFragmentManager(), R.id.container);

		
		TabSpec specMessage = mTabHost.newTabSpec("message");
		
		specMessage.setIndicator(getIndicatorView(R.string.message,
				R.drawable.selector_tab_message));

		
		mTabHost.addTab(specMessage, MessageFragment.class, null);
		
		TabSpec specContacts=mTabHost.newTabSpec("contacts");
		specContacts.setIndicator(getIndicatorView(R.string.contacts, R.drawable.selector_tab_contacts));
		mTabHost.addTab(specContacts, ContactsFragment.class, null);
		
		TabSpec specMe=mTabHost.newTabSpec("me");
		specMe.setIndicator(getIndicatorView(R.string.me, R.drawable.selector_tab_me));
		mTabHost.addTab(specMe,MeFragment.class,null);
	}
	 private View getIndicatorView(int textId, int drawableId) {
	        View view = LayoutInflater.from(this).inflate(
	                R.layout.view_tab_indicator, null);
	        TextView tvIndicator = (TextView) view.findViewById(R.id.tv_indicator);
	        tvIndicator.setText(textId);
	        Drawable top = getResources().getDrawable(drawableId);
	        tvIndicator.setCompoundDrawablesWithIntrinsicBounds(null, top, null,
	                null);
	        return view;
	    }


}
