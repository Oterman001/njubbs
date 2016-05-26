package com.oterman.njubbs.activity.expore;

import java.util.List;

import android.app.AlertDialog;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.BaseActivity;
import com.oterman.njubbs.bean.FriendInfo;
import com.oterman.njubbs.bean.UserInfo;
import com.oterman.njubbs.holders.UserDetailHolder;
import com.oterman.njubbs.protocol.FriendsProtocol;
import com.oterman.njubbs.protocol.UserProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.view.LoadingView.LoadingState;

public class FriendsActivity extends BaseActivity{

	private List<FriendInfo> list;
	private ListView lvFriends;
	private TextView tvStatus;

	@Override
	protected CharSequence getBarTitle() {
		return "ÎÒµÄºÃÓÑ";
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		FriendsProtocol protocol=new FriendsProtocol();
		String url=Constants.BBS_FRIEND_ALL_URL;
		list = protocol.loadFromCache(url);
		return list==null?LoadingState.LOAD_FAILED:LoadingState.LOAD_SUCCESS;
	}
	
	@Override
	public View createSuccessView() {
		
		View rootView=View.inflate(this, R.layout.acitivity_friends, null);
		
		lvFriends = (ListView) rootView.findViewById(R.id.lv_friends);
		tvStatus = (TextView) rootView.findViewById(R.id.tv_status);
		if(list.size()!=0){
			lvFriends.setDivider(getResources().getDrawable(R.color.list_divider_color));
			lvFriends.setDividerHeight(1);
			
			FriendAdapter adapter=new FriendAdapter();
			
			lvFriends.setAdapter(adapter);
			
			lvFriends.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					FriendInfo friendInfo = list.get(position);
					
					AlertDialog.Builder builder=new AlertDialog.Builder(FriendsActivity.this);
					AlertDialog dialog2=null;
					UserDetailHolder holder=new UserDetailHolder(FriendsActivity.this);
					holder.updateStatus(friendInfo.id);
					
					builder.setView(holder.getRootView());
					dialog2 = builder.show();
					holder.setOwnerDialog(dialog2);
					
				}
			});
		}else{
			tvStatus.setVisibility(View.VISIBLE);
		}

		return rootView;
	}
	
	public class FriendAdapter extends BaseAdapter{
		
		private UserProtocol userProtocol;
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=null;
			ViewHolder holder=null;
			if(convertView==null){
				view=View.inflate(getApplicationContext(), R.layout.list_item_friend, null);
				holder=new ViewHolder();
				holder.tvFriend=(TextView) view.findViewById(R.id.tv_friend_id);
				holder.tvDesc=(TextView) view.findViewById(R.id.tv_friend_desc);
				view.setTag(holder);
			}else{
				view=convertView;
				holder=(ViewHolder) view.getTag();
			}

			FriendInfo friendInfo = list.get(position);
			String str=null;
			UserInfo userInfo=friendInfo.userInfo;
			
			if(userInfo!=null&&userInfo.isOnline){
				holder.tvFriend.setTextColor(Color.BLUE);
			}else{
				holder.tvFriend.setTextColor(Color.BLACK);
			}
			
			holder.tvDesc.setText(friendInfo.desc);
			holder.tvFriend.setText(friendInfo.id);
			
			return view;
		}
		class ViewHolder{
			TextView tvFriend;
			TextView tvDesc;
		}
		
	}



}
