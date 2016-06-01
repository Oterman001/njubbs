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
		return "��½";
	}
	
	private void initViews() {
		
		etId = (EditText) this.findViewById(R.id.et_id);
		etPwd = (EditText) this.findViewById(R.id.et_passsword);
		//��ʼ������
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
		//У��ֵ
		final String id=etId.getText().toString().trim();
		final String pwd=etPwd.getText().toString().trim();
		
		if(TextUtils.isEmpty(id)){
			MyToast.toast("�ף�������id��Ӵ��");
			return ;
		}
		
		if(TextUtils.isEmpty(pwd)){
			MyToast.toast("�ף�������������Ŷ��");
			return ;
		}
		
		//�����½
		handleLogin(id, pwd);
		
	}

	//�����½
	private void handleLogin(final String id, final String pwd) {
		
		dialog.setMessage("Ŭ����½�С�����");
		//��ʾ�ȴ�
		dialog.show();
		
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				// �����½���߼�
				RequestParams params = new RequestParams();
				params.addBodyParameter("id", id);
				params.addBodyParameter("pw", pwd);
				
				try {
					if (httpUtil == null) {
						httpUtil = new HttpUtils();
					}
					ResponseStream responseStream = httpUtil.sendSync(HttpMethod.POST,Constants.LOGIN_URL, params);
					// �����ص�������Ϊ�ַ���
					String sb = BaseApplication.StreamToStr(responseStream);
					final String result = sb.toString();
					LogUtil.d("��½���:" + result);
					
					//���ݵ�½������ж�
					if(result.contains("Net.BBS.setCookie")){//��½�ɹ�
						//��������
						SPutils.saveToSP("id", id);
						SPutils.saveToSP("pwd", pwd);
						
						//����cookie ��ȡ�û���Ϣ
						BaseApplication.handleCookie(result);
						
						//��ȡ�û���Ϣ
						UserProtocol protocol=new UserProtocol();
						userInfo=protocol.getUserInfoFromServer(id);
						BaseApplication.setLogedUser(userInfo);
						
						//��ʾ��½�ɹ�
						logOk();
						
					}else if(result.contains("��¼�������")){//��½�������
						loginFailed("��¼�����������10�룡",false);
					}else if(result.contains("�����ʹ�����ʺ�")){//�˺Ŵ���
						loginFailed("�����ʹ�����ʺ�!",false);
					}else if(result.contains("��������ҪС��10��")){//���������̫ƽ��
						loginFailed("��������ҹ���Ƶ����",true);
					}else {//�������
						loginFailed("�������",true);
					}
					
				} catch (Exception e) {//�����쳣
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
				//��½�ɹ�����ʾ
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(userInfo!=null){//��½�ɹ�
							getIntent().putExtra("userInfo", userInfo);
							setResult(100, getIntent());
						}
						MyToast.toast("��½�ɹ�");
						dialog.dismiss();
						finish();
						
					}
				});
			}
			
			//��½ʧ��
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