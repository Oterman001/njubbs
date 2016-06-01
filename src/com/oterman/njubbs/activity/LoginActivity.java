package com.oterman.njubbs.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

@SuppressLint("NewApi")
public class LoginActivity extends MyActionBarActivity  implements OnClickListener {

	
	private EditText etId;
	private EditText etPwd;
	private Button btnLogin;
	private WaitDialog dialog;
	private HttpUtils httpUtil;
	private UserInfo userInfo=BaseApplication.getLogedUser();

	private  ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		initViews();
	}

	@Override
	protected String getBarTitle() {
		return "登陆";
	}
	
	private void initViews() {
		
		etId = (EditText) this.findViewById(R.id.et_id);
		etPwd = (EditText) this.findViewById(R.id.et_passsword);
		//初始化数据
		String id=SPutils.getFromSP("id");
		String pwd=SPutils.getFromSP("pwd");
		if(id!=null){
			etId.setText(id);
		}
		if(pwd!=null){
			etPwd.setText(pwd);
		}
	
		btnLogin = (Button) this.findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(this);
		
		dialog = new WaitDialog(this);
		httpUtil = new HttpUtils();
		
	}

	@Override
	public void onClick(View v) {
		//校验值
		final String id=etId.getText().toString().trim();
		final String pwd=etPwd.getText().toString().trim();
		
		if(TextUtils.isEmpty(id)){
			MyToast.toast("亲，忘记输id了哟！");
			return ;
		}
		
		if(TextUtils.isEmpty(pwd)){
			MyToast.toast("亲，忘记输密码了哦！");
			return ;
		}
		
		//处理登陆
		handleLogin(id, pwd);
		
	}

	//处理登陆
	private void handleLogin(final String id, final String pwd) {
		
		dialog.setMessage("努力登陆中。。。");
		//显示等待
		dialog.show();
		
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				// 处理登陆的逻辑
				RequestParams params = new RequestParams();
				params.addBodyParameter("id", id);
				params.addBodyParameter("pw", pwd);
				
				try {
					if (httpUtil == null) {
						httpUtil = new HttpUtils();
					}
					ResponseStream responseStream = httpUtil.sendSync(HttpMethod.POST,Constants.LOGIN_URL, params);
					// 将返回的流解析为字符串
					String sb = BaseApplication.StreamToStr(responseStream);
					final String result = sb.toString();
					LogUtil.d("登陆结果:" + result);
					
					//根据登陆结果来判断
					if(result.contains("Net.BBS.setCookie")){//登陆成功
						//保存起来
						SPutils.saveToSP("id", id);
						SPutils.saveToSP("pwd", pwd);
						
						//处理cookie 获取用户信息
						BaseApplication.handleCookie(result);
						
						//获取用户信息
						UserProtocol protocol=new UserProtocol();
						userInfo=protocol.getUserInfoFromServer(id);
						BaseApplication.setLogedUser(userInfo);
						
						//提示登陆成功
						logOk();
						
					}else if(result.contains("登录间隔过密")){//登陆间隔过密
						loginFailed("登录间隔不能少于10秒！",false);
					}else if(result.contains("错误的使用者帐号")){//账号错误
						loginFailed("错误的使用者帐号!",false);
					}else if(result.contains("密码间隔不要小于10秒")){//密码错误且太平凡
						loginFailed("密码错误且过于频繁！",true);
					}else {//密码错误
						loginFailed("密码错误！",true);
					}
					
				} catch (Exception e) {//联网异常
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							MyToast.toast("登陆失败，请检查网络");
							dialog.dismiss();
						}
					});
				}

			}

			private void logOk() {
				//登陆成功后显示
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(userInfo!=null){//登陆成功
							getIntent().putExtra("userInfo", userInfo);
							setResult(100, getIntent());
						}
						MyToast.toast("登陆成功");
						dialog.dismiss();
						finish();
						
					}
				});
			}
			
			//登陆失败
			private void loginFailed(final String msg,final boolean ifclear) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyToast.toast(msg);
						if(ifclear){
							etPwd.setText("");
						}
						dialog.dismiss();
					}
				});
			}
		});
		
		
	}
	
	
}