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
		//更改状态栏的颜色
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
			Window window = this.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources().getColor(R.color.green));
		}
		
		
		initViews();
	}

	private void initViews() {
		//处理actionbar
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

		tvTitle.setText("登陆");
		tvTitle.setTextSize(22);
		
		
		etId = (EditText) this.findViewById(R.id.et_id);
		
		etPwd = (EditText) this.findViewById(R.id.et_passsword);
		//初始化数据
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
		dialog.setMessage("努力登陆中。。。");
		//显示等待
		WaitDialog.show(dialog);
		//处理登陆
		handleLogin(id, pwd);
		
		
	}

	//处理登陆
	private void handleLogin(final String id, final String pwd) {
		
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				//处理登陆的逻辑
				RequestParams params=new RequestParams();
				params.addBodyParameter("id", id);
				params.addBodyParameter("pw", pwd);
				
				try {
					ResponseStream responseStream = httpUtil.sendSync(HttpMethod.POST, Constants.LOGIN_URL, params);
					//将返回的流解析为字符串
					StringBuffer sb = StreamToStr(responseStream);
				
					final String result=sb.toString();
					
					LogUtil.d("登陆结果:"+result);
					
					if(result.contains("Net.BBS.setCookie")){//登陆成功
						//保存起来
						SPutils.saveToSP("id", id);
						SPutils.saveToSP("pwd", pwd);
						
						//处理cookie
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
							
							//发送请求
							ResponseStream stream = httpUtil.sendSync(HttpMethod.GET, Constants.BBSLEFT_URL, rp);
							
							StringBuffer favHtml = StreamToStr(stream);
							
							//LogUtil.d("bbsleft:\n"+favHtml.toString());
							
							//解析
							Document doc= Jsoup.parse(favHtml.toString());
							
							Elements aEles = doc.select("a");
							
							StringBuffer sbFav=new StringBuffer();
							boolean flag=false;
							for (int i = 0; i < aEles.size(); i++) {
								Element aEle = aEles.get(i);
								if(flag&&!aEle.text().equals("预定管理")){//找到收藏的版面
									sbFav.append(aEle.text()).append("#");
								}
								if(aEle.text().equals("预定讨论区")){//开始记录
									flag=true;
								}
								if(aEle.text().equals("预定管理")){//开始记录
									flag=false;
								}
							}
							
							SPutils.saveToSP("favBoards", sbFav.toString());
							
							LogUtil.d("favBoards:"+sbFav.toString());
						}
						//从网络获取user的信息
						UserProtocol protocol=new UserProtocol();
						userInfo = protocol.getUserInfoFromServer(id);
						
						//回到主线程，提示登陆成功
						logOk(result);
					}
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
					//登陆成功后显示
					UiUtils.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							MyToast.toast("登陆失败，请检查网络！");
							dialog.dismiss();
						}
					});
				}

			}

			
			private void logOk(final String result) {
				//登陆成功后显示
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(userInfo!=null){//登陆成功
							
							getIntent().putExtra("userInfo", userInfo);
							MyToast.toast("登陆成功");
							
							setResult(100, getIntent());
							
							finish();
						}else if(result.contains("错误")){
							MyToast.toast("用户名或密码不匹配！");
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