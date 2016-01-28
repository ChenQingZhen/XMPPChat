package com.cqz.chatsmark.ui;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cqz.chatsmark.data.APPConstant;
import com.cqz.chatsmark.model.Message;
import com.cqz.chatsmark.util.TextUtil;
import com.cqz.chatsmark.util.ToastUtil;
import com.cqz.chatsmark.xmpp.XMPPUtil;

public class ChatActivity extends Activity {
	private ListView lvChat;
	private List<Message> mMessageList = new ArrayList<Message>();
	private ChatAdapter mAdapter;
	private String user;
	private String me;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		initialWidgets();
	}

	private void initialWidgets() {
		
		user= getIntent().getStringExtra(APPConstant.USER);
		setTitle(TextUtil.getName(user));
		me=XMPPUtil.getInstance().getConnection().getUser();
		lvChat=(ListView) findViewById(R.id.lv_chat);
		ChatManager chatManager=ChatManager.getInstanceFor(XMPPUtil.getInstance().getConnection());
		chatManager.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean createdLocally) {
				if(!createdLocally){
					chat.addMessageListener(new MyMessageListener());
				}
			}
		});

		final Chat chat= chatManager.createChat(user);
		final Button btnSend=(Button) findViewById(R.id.btn_send);
		
	final EditText etMsg=(EditText) findViewById(R.id.et_msg);
	btnSend.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String msg= etMsg.getText().toString();
			try {
				chat.sendMessage(msg);
				Message m=new Message();
				m.setType(Message.TYPE_SEND);
				m.setContent(msg);
				mMessageList.add(m);
				if(mAdapter==null){
					mAdapter=new ChatAdapter();
					lvChat.setAdapter(mAdapter);
				}else{
				mAdapter.notifyDataSetChanged();
				}
				etMsg.setText("");
			} catch (NotConnectedException e) {
				ToastUtil.showToast(ChatActivity.this, R.string.noconnection_exception);
				e.printStackTrace();
			}
		}
	});
	etMsg.addTextChangedListener(new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			if(etMsg.getText().length()>0){
				btnSend.setEnabled(true);
			}else{
				btnSend.setEnabled(false);
			}
			
		}
	});
	
	
	}

	class ChatAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mMessageList.size();
		}

		@Override
		public Message getItem(int position) {
			return mMessageList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Message msg = getItem(position);
			View view = convertView;
			ViewHolder vh = null;
			if (view == null) {
				if (msg.getType() == Message.TYPE_RECEIVE) {
					view = LayoutInflater.from(ChatActivity.this).inflate(
							R.layout.view_chat_list_left_item, parent, false);
					

				} else {
					view = LayoutInflater.from(ChatActivity.this).inflate(
							R.layout.view_chat_list_right_item, parent, false);
				}
				vh = new ViewHolder();
				vh.ivAvater = (ImageView) view.findViewById(R.id.iv_avater);
				vh.tvName = (TextView) view.findViewById(R.id.tv_name);
				vh.tvMsg = (TextView) view.findViewById(R.id.tv_msg);
				view.setTag(vh);
			} else {
				vh = (ViewHolder) view.getTag();
			}
			if (msg.getType() == Message.TYPE_RECEIVE) {
				vh.tvName.setText(TextUtil.getName(user));
				vh.tvMsg.setText(msg.getContent());
			} else {
				vh.tvName.setText(TextUtil.getName(me));
				vh.tvMsg.setText(msg.getContent());
			}
			return view;
		}

		class ViewHolder {
			ImageView ivAvater;
			TextView tvName;
			TextView tvMsg;
		}

	}
class MyMessageListener implements ChatMessageListener{

	@Override
	public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
			if(message.getBodies().size()==0){
				return;
			}
			Message m=new Message();
		if(message.getFrom().equals(me)){
			//自己发送的消息
			m.setType(Message.TYPE_SEND);
		}else{
			//接收到的消息
			m.setType(Message.TYPE_RECEIVE);
		}
		m.setContent(message.getBody());
		mMessageList.add(m);
		h.sendEmptyMessage(0x123);
	}
}
	private Handler h=new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0x123){
				if(mAdapter==null){
					mAdapter=new ChatAdapter();
					lvChat.setAdapter(mAdapter);
				}else{
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

}
