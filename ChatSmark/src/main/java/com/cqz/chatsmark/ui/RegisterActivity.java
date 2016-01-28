package com.cqz.chatsmark.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.cqz.chatsmark.util.ToastUtil;
import com.cqz.chatsmark.xmpp.XMPPUtil;

public class RegisterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initialWidgets();
	}

	private void initialWidgets() {
		final EditText etUsername = (EditText) findViewById(R.id.et_username);
		final EditText etPassword = (EditText) findViewById(R.id.et_password);
		Button btnRegister = (Button) findViewById(R.id.btn_register);
		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = etUsername.getText().toString().trim();
				String password = etPassword.getText().toString().trim();
				register(username, password);

			}

			private void register(final String username, final String password) {
				new Thread(){
					public void run() {
						String status=XMPPUtil.getInstance().regist(username, password);
						Message msg= new Message();
						msg.what=0x123;
						msg.obj=status;
						h.sendMessage(msg);
					}
				}.start();
				
			}
		});
		Button btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	private Handler h=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0x123){
				String status= (String) msg.obj;
				if("1".equals(status)){
					ToastUtil.showToast(RegisterActivity.this, R.string.register_succ);
				}else if("3".equals(status)){
					ToastUtil.showToast(RegisterActivity.this, R.string.register_error);
				}else if("0".equals(status)){
					ToastUtil.showToast(RegisterActivity.this, R.string.server_no_response);
				}else {
					ToastUtil.showToast(RegisterActivity.this, R.string.account_exist);
				}
			}
		}
	};
			

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

}
