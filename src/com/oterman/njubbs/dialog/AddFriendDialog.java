package com.oterman.njubbs.dialog;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;

public class AddFriendDialog {

	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private TextView tvId;
	private EditText etDesc;
	private String friendId;

	private Context context;
	public AddFriendDialog(Context context) {
		this.context=context;
		builder = new AlertDialog.Builder(context);
		View view = View.inflate(context, R.layout.dialog_add_friend, null);

		etDesc = (EditText) view.findViewById(R.id.et_addf_desc);
		tvId = (TextView) view.findViewById(R.id.tv_addf_id);

		builder.setView(view);

		builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String str = etDesc.getText().toString();
				try {
					if (TextUtils.isEmpty(str)) {

						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, false); // false -不能关闭
						
						MyToast.toast("备注不能为空哦");
						return;
					}else{
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true); // true　关闭对话框
						// 处理添加好友
						handleAddFriend();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		builder.setNegativeButton("取消", null);
		builder.setTitle("添加好友");
		dialog = builder.create();

	}

	protected void handleAddFriend() {
		// 处理添加好友
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				HttpUtils httpUtils = new HttpUtils();

				String cookie = BaseApplication.getCookie();
				if (cookie == null) {
					cookie = BaseApplication.autoLogin(context,true);
				}
				RequestParams rp = new RequestParams("gbk");
				rp.addQueryStringParameter("userid", friendId);
				String desc = etDesc.getText().toString();
				rp.addQueryStringParameter("exp", desc);

				rp.addHeader("Cookie", cookie);

				try {
					String url = Constants.BBS_ADD_FRIEND_URL;
					ResponseStream stream = httpUtils.sendSync(HttpMethod.GET,
							url, rp);

					String result = BaseApplication.StreamToStr(stream);

					LogUtil.d("添加好友结果：" + result);
					if (result.contains("已加入您的好友名单")) {// 成功
						UiUtils.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								MyToast.toast("添加成功");
							}
						});
					} else {// 失败
						UiUtils.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								MyToast.toast("添加失败");
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	public void show() {
		builder.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}

	public void setAddId(String id) {
		friendId = id.trim();

		String str = "<font color='grey'>添加id：</font>" + id;
		tvId.setText(Html.fromHtml(str));
	}

	public String getDesc() {
		return etDesc.getText().toString();
	}

}
