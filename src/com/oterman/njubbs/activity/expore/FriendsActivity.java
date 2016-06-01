package com.oterman.njubbs.activity.expore;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.BaseActivity;
import com.oterman.njubbs.bean.FriendInfo;
import com.oterman.njubbs.bean.UserInfo;
import com.oterman.njubbs.dialog.WaitDialog;
import com.oterman.njubbs.holders.UserDetailHolder;
import com.oterman.njubbs.protocol.FriendsProtocol;
import com.oterman.njubbs.protocol.UserProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.view.LoadingView.LoadingState;

public class FriendsActivity extends BaseActivity implements OnClickListener, OnRefreshListener{

	private List<FriendInfo> list;
	private ListView lvFriends;
	private TextView tvStatus;
	private FriendAdapter adapter;
	private SwipeRefreshLayout srl;
	private FriendsProtocol protocol;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageButton ibAddFriend=(ImageButton) actionBarView.findViewById(R.id.btn_add_friend);
		ibAddFriend.setVisibility(View.VISIBLE);
		ibAddFriend.setOnClickListener(this);
	}
	
	@Override
	protected CharSequence getBarTitle() {
		return "我的好友";
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		if(protocol==null){
			protocol = new FriendsProtocol();
		}
		
		String url=Constants.BBS_FRIEND_ALL_URL;
		list = protocol.loadFromCache(url,FriendsActivity.this);
		return list==null?LoadingState.LOAD_FAILED:LoadingState.LOAD_SUCCESS;
	}
	
	@Override
	public View createSuccessView() {
		srl = new SwipeRefreshLayout(getBaseContext());
		View rootView=View.inflate(this, R.layout.acitivity_friends, null);
		srl.addView(rootView);
		
		lvFriends = (ListView) rootView.findViewById(R.id.lv_friends);
		tvStatus = (TextView) rootView.findViewById(R.id.tv_status);
		if(list.size()!=0){//有好友
			initLv();
		}else{//没有好友
			tvStatus.setVisibility(View.VISIBLE);
		}
		
		srl.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_blue_light);
		//下拉刷新 当下拉时 会出发该方法
		srl.setOnRefreshListener(this);

		return srl;
	}
	
	//有好友
	private void initLv() {
		
		tvStatus.setVisibility(View.INVISIBLE);
		
		lvFriends.setDivider(getResources().getDrawable(R.color.list_divider_color));
		lvFriends.setDividerHeight(1);
		
		adapter = new FriendAdapter();
		
		lvFriends.setAdapter(adapter);
		
		lvFriends.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FriendInfo friendInfo = list.get(position);
				
				AlertDialog.Builder builder=new AlertDialog.Builder(FriendsActivity.this);
				AlertDialog dialog2=null;
				UserDetailHolder holder=new UserDetailHolder(FriendsActivity.this,true);
				holder.updateStatus(friendInfo.id);
				
				builder.setView(holder.getRootView());
				dialog2 = builder.show();
				holder.setOwnerDialog(dialog2);
				
			}
		});
		
		lvFriends.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
					View view, int position, long id) {
				//长按删除好友
			final	FriendInfo friendInfo = list.get(position);
				
				AlertDialog.Builder builder=new AlertDialog.Builder(FriendsActivity.this);
				builder.setTitle("通知");
				builder.setMessage("确定要删除好友"+friendInfo.id+"吗？");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleDeleteFriend(friendInfo);//处理删除好友
					}
				});
				builder.setNegativeButton("取消", null);
				builder.show();
				return true;
			}
		});
	}
	
	//删除好友
	protected void handleDeleteFriend(final FriendInfo friendInfo) {
		final WaitDialog waitDialog=new WaitDialog(this);
		waitDialog.setMessage("正在努力操作中..");
		waitDialog.show();
		
		ThreadManager.getInstance().createLongPool().execute((new Runnable() {
			@Override
			public void run() {
				//处理删除操作
				HttpUtils httpUtils=new HttpUtils();
				String cookie=BaseApplication.getCookie();
				if(cookie==null){
					cookie=BaseApplication.autoLogin(FriendsActivity.this,true);
				}
				
				RequestParams rp=new RequestParams();
				rp.addHeader("Cookie",cookie);
				
				String url=Constants.BBS_FRIEND_DEL_URL+"?userid="+friendInfo.id.trim();
				
				try {
					ResponseStream stream = httpUtils.sendSync(HttpMethod.GET, url, rp);
					final String result = BaseApplication.StreamToStr(stream);
					
					LogUtil.d("删除好友结果："+result);
					if(result.contains("好友名单中删除")){//删除成功
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								waitDialog.dismiss();
								MyToast.toast("删除成功");
								
								//刷新界面
								list.remove(friendInfo);
								adapter.notifyDataSetChanged();
							}
						});
						
					}else{//删除失败
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								waitDialog.dismiss();
								MyToast.toast("删除失败"+result);
							}
						});
						
					}
					
				} catch (final Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							waitDialog.dismiss();
							MyToast.toast("删除失败"+e.getMessage());
						}
					});
				}
			}
		}));
		
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add_friend:
			//MyToast.toast("哎呀，人家来没来得及做啦");
			//跳转到查找用户的页面
			Intent  intent=new Intent(this,AddFriendActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	//下拉刷新
	@Override
	public void onRefresh() {
		
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				if(protocol==null){
					protocol = new FriendsProtocol();
				}
				String url=Constants.BBS_FRIEND_ALL_URL;
				List<FriendInfo> tempList = protocol.loadFromServer(url, false,FriendsActivity.this);
				
				list=tempList;
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(adapter!=null){
							adapter.notifyDataSetChanged();
						}else{
							if(list!=null&list.size()>0){
								initLv();
							}
						}
						srl.setRefreshing(false);
					}
				});
				
			}
		});

		
	}



}
