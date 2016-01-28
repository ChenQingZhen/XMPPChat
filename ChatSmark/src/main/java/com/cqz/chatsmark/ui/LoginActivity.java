package com.cqz.chatsmark.ui;

import java.io.IOException;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.cqz.chatsmark.data.APPConstant;
import com.cqz.chatsmark.util.ToastUtil;
import com.cqz.chatsmark.xmpp.XMPPUtil;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initialWidgets();
	}

	private void initialWidgets() {
		final EditText etUsername = (EditText) findViewById(R.id.et_username);
		final EditText etPassword = (EditText) findViewById(R.id.et_password);
		Button btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = etUsername.getText().toString().trim();
				String password = etPassword.getText().toString().trim();
				login(username, password);
			}
		});
		Button btnRegister = (Button) findViewById(R.id.btn_register);
		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});
	}

	private void login(final String username, final String password) {
		new Thread() {
			public void run() {
				boolean succ = XMPPUtil.getInstance().login(username, password);
				if (succ) {
					h.sendEmptyMessage(0x123);
				}else{
					h.sendEmptyMessage(0x125);
				}
			}
		}.start();

	}

	private Handler h = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x123) {
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
			}else if(msg.what==0x125){
				ToastUtil.showToast(LoginActivity.this,R.string.login_failed);
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
