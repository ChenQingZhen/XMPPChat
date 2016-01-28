package com.cqz.chatsmark.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cqz.chatsmark.data.APPConstant;
import com.cqz.chatsmark.util.TextUtil;
import com.cqz.chatsmark.xmpp.XMPPUtil;

public class ContactsFragment extends Fragment {
	private View mContentView;
	private ListView lvContacts;
	private List<RosterEntry> mEntryList;
	private Roster roster;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		if(mContentView!=null){
			return mContentView;
		}
		mContentView=inflater.inflate(R.layout.fragment_contacts, container, false);
		initialWidgets();
		initialData();
		return mContentView;
	}


	private void initialWidgets() {
		lvContacts=(ListView) mContentView.findViewById(R.id.lv_contacts);
		lvContacts.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				RosterEntry entry= mEntryList.get(position);
				Intent intent=new Intent(getActivity(),ChatActivity.class);
				intent.putExtra(APPConstant.USER, entry.getUser());
				startActivity(intent);
			}
		});
	}


	private void initialData() {
		roster= Roster.getInstanceFor(XMPPUtil.getInstance().getConnection());
		//设置加好友的模式
		roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
		//获取好友集合
		Set<RosterEntry>  entries=roster.getEntries();
		mEntryList=new ArrayList<RosterEntry>(entries);
//		for (RosterEntry rosterEntry : entries) {
//			Log.d("entries", ""+rosterEntry);
//			Presence p=roster.getPresence(rosterEntry.getUser());
//		}
		ListAdapter adapter=new ListAdapter();
		lvContacts.setAdapter(adapter);
		//监听好友状态信息变化
		roster.addRosterListener(new RosterListener() {

			@Override
			public void presenceChanged(Presence presence) {
				Log.d("presence_change", "Presence changed: " + presence.getFrom() + " " + presence);

			}

			@Override
			public void entriesUpdated(Collection<String> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void entriesDeleted(Collection<String> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void entriesAdded(Collection<String> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	class ListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mEntryList.size();
		}

		@Override
		public RosterEntry getItem(int position) {

			return mEntryList.get(position);
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=convertView;
			ViewHolder vh=null;
			if(view==null){
				view=LayoutInflater.from(getActivity()).inflate(R.layout.view_contacts_list_item, parent, false);
				vh=new ViewHolder();
				vh.ivAvater=(ImageView) view.findViewById(R.id.iv_avater);
				vh.tvName=(TextView) view.findViewById(R.id.tv_name);
				view.setTag(vh);
			}else{
				vh= (ViewHolder) view.getTag();
			}
			RosterEntry entry= getItem(position);
			vh.tvName.setText(TextUtil.getName(entry.getUser()) +" presence:"+roster.getPresence(entry.getUser()));
			return view;
		}
		class ViewHolder{
			ImageView ivAvater;
			TextView tvName;
		}
	}


	@Override
	public void onDestroyView() {
		// 移除当前视图，防止重复加载相同视图使得程序闪退
		((ViewGroup) mContentView.getParent()).removeView(mContentView);
		super.onDestroyView();
	}

}
