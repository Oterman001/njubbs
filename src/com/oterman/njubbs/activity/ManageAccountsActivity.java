package com.oterman.njubbs.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.bean.UserInfo;
import com.oterman.njubbs.dialog.WaitDialog;
import com.oterman.njubbs.protocol.UserProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("NewApi")
public class ManageAccountsActivity extends MyActionBarActivity implements
		OnClickListener {

	private ListView lvAccounts;
	private HashMap<String, String> userMap;
	private List<String> idList;
	private BaseAdapter adapter;

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data!=null&&requestCode==100){
			UserInfo userInfo = (UserInfo) data.getSerializableExtra("userInfo");
			
			if(!idList.contains(userInfo.id)){
				idList.add(0, userInfo.id);
				userMap.put(userInfo.id,SPutils.getFromSP("pwd"));
				adapter.notifyDataSetChanged();
			}

		}
	}
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_manage_accounts);
		lvAccounts = (ListView) this.findViewById(R.id.lv_accounts);

		// ׼������
		prepareData();

		if (idList == null) {
			idList = new ArrayList<>();
		}

		idList.add("����˺�");

		adapter = new AccountsAdapter();
		lvAccounts.setAdapter(adapter);

		lvAccounts.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == idList.size() - 1) {// ��ת����½����
					
					Intent intent=new Intent(ManageAccountsActivity.this,LoginActivity.class);
					startActivityForResult(intent,100);
					
				} else {
					// ��ȡ�û��������룬��½
					String userId = idList.get(position);
					String userPwd = userMap.get(userId);

					/**
					 * �����½
					 */
					handleLogin(userId, userPwd);

				}

			}
		});
	}

	private UserInfo userInfo;
	private WaitDialog dialog;
	private void prepareData() {
		// mmlover#mima;
		String ids = SPutils.getFromSP("ids");
		String id = SPutils.getFromSP("id");
		String pwd = SPutils.getFromSP("pwd");
		String temp = id + "#" + pwd;
		if (!ids.contains(temp)) {// ������
			// ����
			ids = ids + temp + ";";
			SPutils.saveToSP("ids", ids);
		}
	
		if (!TextUtils.isEmpty(ids)) {
			userMap = new HashMap<>();
			String[] users = ids.split(";");
			for (int i = 0; i < users.length; i++) {
				String userStr = users[i];
				String[] strs = userStr.split("#");
				if (strs.length == 2) {
					String userId = strs[0];
					String userPwd = strs[1];
					userMap.put(userId, userPwd);
				}
			}
		}
	
		if (userMap != null) {
			idList = new ArrayList<>(userMap.keySet());
		}
	}

	// �����½
	private void handleLogin(final String id, final String pwd) {
		dialog = new WaitDialog(this);
		dialog.setMessage("�л��˺��С�����");

		// ��ʾ�ȴ�
		dialog.show();
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {

			@Override
			public void run() {
				// �����½���߼�
				RequestParams params = new RequestParams();
				params.addBodyParameter("id", id);
				params.addBodyParameter("pw", pwd);
				HttpUtils httpUtil = new HttpUtils();
				;
				try {
					ResponseStream responseStream = httpUtil.sendSync(
							HttpMethod.POST, Constants.LOGIN_URL, params);
					// �����ص�������Ϊ�ַ���
					String sb = BaseApplication.StreamToStr(responseStream);
					final String result = sb.toString();
					LogUtil.d("��½���:" + result);

					// ���ݵ�½������ж�
					if (result.contains("Net.BBS.setCookie")) {// ��½�ɹ�
						// ��������
						SPutils.saveToSP("id", id);
						SPutils.saveToSP("pwd", pwd);
						
						String ids = SPutils.getFromSP("ids");
						String temp = id + "#" + pwd;
						if (!ids.contains(temp)) {// ������
							// ����
							ids = ids + temp + ";";
							SPutils.saveToSP("ids", ids);
						}

						// ����cookie ��ȡ�û���Ϣ
						BaseApplication.handleCookie(result);

						// ��ȡ�û���Ϣ
						UserProtocol protocol = new UserProtocol();
						userInfo = protocol.getUserInfoFromServer(id);
						BaseApplication.setLogedUser(userInfo);

						// ��½ͳ��
						MobclickAgent.onProfileSignIn(id);

						// ��ʾ��½�ɹ�
						logOk();

					} else if (result.contains("��¼�������")) {// ��½�������
						loginFailed("��¼�����������10�룡", false);
					} else if (result.contains("�����ʹ�����ʺ�")) {// �˺Ŵ���
						loginFailed("�����ʹ�����ʺ�!", false);
					} else if (result.contains("��������ҪС��10��")) {// ���������̫ƽ��
						loginFailed("��������ҹ���Ƶ����", true);
					} else {// �������
						loginFailed("�������", true);
					}

				} catch (Exception e) {// �����쳣
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							MyToast.toast("��½ʧ�ܣ���������");
							dialog.dismiss();
						}
					});
				}
			}

			private void logOk() {
				// ��½�ɹ�����ʾ
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
//						if (userInfo != null) {// ��½�ɹ�
//							getIntent().putExtra("userInfo", userInfo);
//							setResult(100, getIntent());
//						}
						
						MyToast.toast("�л��ɹ�");
						dialog.dismiss();
						finish();

					}
				});
			}

			// ��½ʧ��
			private void loginFailed(final String msg, final boolean ifclear) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyToast.toast(msg);
						if (ifclear) {
							//etPwd.setText("");
						}
						dialog.dismiss();
					}
				});
			}
		});
	}

	@Override
	protected String getBarTitle() {
		return "�˺��л�";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_down_pic:
			break;

		default:
			break;
		}

	}

	public class AccountsAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return idList.size();
		}

		@Override
		public Object getItem(int position) {
			return idList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.list_item_account, null);

			TextView tvId = (TextView) view.findViewById(R.id.tv_account);
			ImageView iv=(ImageView) view.findViewById(R.id.iv_current);
			iv.setVisibility(View.INVISIBLE);
			String id = idList.get(position);
			
			String currentId=SPutils.getFromSP("id");
			if(id.equals(currentId)){
//				tvId.setTextColor(Color.BLUE);
				iv.setVisibility(View.VISIBLE);
			}else{
//				tvId.setTextColor(Color.BLACK);
			}
			
			tvId.setText(id);

			return view;
		}

	}

}
