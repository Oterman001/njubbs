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
import com.oterman.njubbs.protocol.UserProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.WaitDialog;

@SuppressLint("NewApi")
public class LoginActivity extends FragmentActivity implements OnClickListener {

	
	private EditText etId;
	private EditText etPwd;
	private CheckBox cbAutoLogin;
	private Button btnLogin;
	private WaitDialog dialog;
	private HttpUtils httpUtil;
	private UserInfo userInfo=BaseApplication.userInfo;

	private  ActionBar actionBar;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		//����״̬������ɫ
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
			Window window = this.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources().getColor(R.color.green));
		}
		
		
		initViews();
	}

	private void initViews() {
		//����actionbar
		actionBar=getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		View view=View.inflate(getApplicationContext(), R.layout.actionbar_custom_backtitle, null);
		
		View back = view.findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        TextView tvTitle=(TextView) view.findViewById(R.id.tv_actionbar_title);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, params);

		tvTitle.setText("��½");
		tvTitle.setTextSize(22);
		
		
		etId = (EditText) this.findViewById(R.id.et_id);
		
		etPwd = (EditText) this.findViewById(R.id.et_passsword);
		//��ʼ������
		etId.setText(SPutils.getFromSP("id"));
		etPwd.setText(SPutils.getFromSP("pwd"));
		
		
		cbAutoLogin = (CheckBox) this.findViewById(R.id.ch_auto_login);
	
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
		dialog.setMessage("Ŭ����½�С�����");
		//��ʾ�ȴ�
		WaitDialog.show(dialog);
		//�����½
		handleLogin(id, pwd);
		
		
	}

	//�����½
	private void handleLogin(final String id, final String pwd) {
		
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				//�����½���߼�
				RequestParams params=new RequestParams();
				params.addBodyParameter("id", id);
				params.addBodyParameter("pw", pwd);
				
				try {
					ResponseStream responseStream = httpUtil.sendSync(HttpMethod.POST, Constants.LOGIN_URL, params);
					//�����ص�������Ϊ�ַ���
					StringBuffer sb = StreamToStr(responseStream);
				
					final String result=sb.toString();
					
					LogUtil.d("��½���:"+result);
					
					if(result.contains("Net.BBS.setCookie")){//��½�ɹ�
						//��������
						SPutils.saveToSP("id", id);
						SPutils.saveToSP("pwd", pwd);
						
						//����cookie
						String reg="Net.BBS.setCookie\\(\\'" +
								"(\\d+)" +"N"+"(.*?)\\+"+"(\\d+)"+
								"\\'\\)";
						Pattern p=Pattern.compile(reg, Pattern.DOTALL);
						Matcher matcher = p.matcher(result);
						
						if(matcher.find()){
							Integer _U_NUM=Integer.parseInt(matcher.group(1))+2;
							String _U_UID=matcher.group(2);
							Integer _U_KEY=Integer.parseInt(matcher.group(3))-2;
							
							String cookie="_U_NUM="+_U_NUM+";_U_UID="+_U_UID+";_U_KEY="+_U_KEY;
							LogUtil.d("cookie:"+cookie);
							BaseApplication.cookie=cookie;
							
							RequestParams rp=new RequestParams();
							rp.addHeader("Cookie", cookie);
							
							//��������
							ResponseStream stream = httpUtil.sendSync(HttpMethod.GET, Constants.BBSLEFT_URL, rp);
							
							StringBuffer favHtml = StreamToStr(stream);
							
							//LogUtil.d("bbsleft:\n"+favHtml.toString());
							
							//����
							Document doc= Jsoup.parse(favHtml.toString());
							
							Elements aEles = doc.select("a");
							
							StringBuffer sbFav=new StringBuffer();
							boolean flag=false;
							for (int i = 0; i < aEles.size(); i++) {
								Element aEle = aEles.get(i);
								if(flag&&!aEle.text().equals("Ԥ������")){//�ҵ��ղصİ���
									sbFav.append(aEle.text()).append("#");
								}
								if(aEle.text().equals("Ԥ��������")){//��ʼ��¼
									flag=true;
								}
								if(aEle.text().equals("Ԥ������")){//��ʼ��¼
									flag=false;
								}
							}
							
							SPutils.saveToSP("favBoards", sbFav.toString());
							
							LogUtil.d("favBoards:"+sbFav.toString());
						}
						//�������ȡuser����Ϣ
						UserProtocol protocol=new UserProtocol();
						userInfo = protocol.getUserInfoFromServer(id);
						
						//�ص����̣߳���ʾ��½�ɹ�
						logOk(result);
					}
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
					//��½�ɹ�����ʾ
					UiUtils.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							MyToast.toast("��½ʧ�ܣ��������磡");
							dialog.dismiss();
						}
					});
				}

			}

			
			private void logOk(final String result) {
				//��½�ɹ�����ʾ
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(userInfo!=null){//��½�ɹ�
							
							getIntent().putExtra("userInfo", userInfo);
							MyToast.toast("��½�ɹ�");
							
							setResult(100, getIntent());
							
							finish();
						}else if(result.contains("����")){
							MyToast.toast("�û��������벻ƥ�䣡");
							etPwd.setText("");
						}
						
						dialog.dismiss();
						
					}
				});
			}

			private StringBuffer StreamToStr(ResponseStream responseStream)
					throws UnsupportedEncodingException, IOException {
				InputStream is = responseStream.getBaseStream();
				
				BufferedReader br=new BufferedReader(new InputStreamReader(is,"gbk"));
				
				String line=null;
				StringBuffer sb=new StringBuffer();
				
				while((line=br.readLine())!=null){
					sb.append(line);
				}
				return sb;
			}
		});
	}
	
	
}