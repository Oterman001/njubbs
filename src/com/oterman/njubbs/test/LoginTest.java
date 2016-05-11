package com.oterman.njubbs.test;

import android.test.AndroidTestCase;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.utils.LogUtil;

public class LoginTest  extends AndroidTestCase{

	
	public void testPost(){
		HttpUtils util=new HttpUtils();
		
		String url="http://bbs.nju.edu.cn/bbslogin?type=2";
		
		RequestParams params=new RequestParams();
		params.addBodyParameter("id", "mmlover");
		params.addBodyParameter("pw", "mrtian");
		util.send(HttpMethod.POST, url,params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtil.d("³É¹¦£¡"+responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.d("Ê§°Ü£¡"+msg);
			}
		});
		
	}
}
