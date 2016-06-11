package com.oterman.njubbs.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.dialog.WaitDialog;

public class NetUtils {

	public static void uploadFile3(final Context context,
			final WaitDialog dialog, final File file,final EditText etContent) {

		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				HttpClient httpClient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				HttpPost httpPost = new HttpPost(Constants.getUploadUrl());

				try {
					MultipartEntity entity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);

					entity.addPart("up", new FileBody(file));
					entity.addPart("exp", new StringBody(""));
					entity.addPart("ptext", new StringBody("text"));
					entity.addPart("board", new StringBody("Pictures"));

					httpPost.setEntity(entity);

					HttpResponse response = httpClient.execute(httpPost,
							localContext);

					int statusCode = response.getStatusLine().getStatusCode();

					if (statusCode == HttpStatus.SC_OK) {
						System.out.println("服务器正常响应.....");
						HttpEntity resEntity = response.getEntity();
						InputStream inputStream = resEntity.getContent();

						BufferedReader br = new BufferedReader(
								new InputStreamReader(inputStream, "gbk"));
						// nju_bbs160605153704.jpg
						String line = null;
						
						StringBuffer sb = new StringBuffer();

						while ((line = br.readLine()) != null) {
							sb.append(line);
							sb.append("\n");
						}
						String result = sb.toString();
						LogUtil.d("result2:" + result);

						result = result.replaceAll("\n", "");
						// 截取 &file=19068&name=njubbskdsadjkfa.jpg
						int start = result.indexOf("&file=");
						int end = result.indexOf("&exp=");
						result = result.substring(start, end);

						// bbsupload2?board=Pictures&file=2672&name=1.jpg&exp=&ptext=text
						// HTTP/1.1
						String url2 = "http://bbs.nju.edu.cn/bbsupload2?board=Pictures"
								+ result + "&exp=&ptext=text";
						LogUtil.d("url2:" + url2);
						
						handleUpload2(context, url2, dialog,etContent);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	static String resultUrl=null;
	protected static void  handleUpload2(Context context, String url2,
			final WaitDialog waitDialog, final EditText etContent) {
		
		AsyncHttpClient ahc = new SyncHttpClient();
		String cookie = BaseApplication.getCookie();
		if (cookie == null) {
			cookie = BaseApplication.autoLogin(context, true);
		}

		ahc.addHeader("Cookie", cookie);
		ahc.get(url2, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers,
					byte[] responseBody) {
				// 这个方法会执行，但是得到的responsebody没有值，但是应该有值得。

				LogUtil.d("结果：" + statusCode);// Content-Encoding: gzip
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "gb2312");
						
						LogUtil.d("result:" + result);
						/*
						 * <html><head>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=gb2312">
<link rel=stylesheet type=text/css href="/images/bbs.css?Net_3">
</head><script src="/js/bbs.js?Net_9"></script>
<script>parent.Net.Form.addText('text', '\nhttp://bbs.nju.edu.cn/file/Pictures/1465569512184015156.jpg\n');history.back();</script>
						 * 
						 * 
						 */
						
						Pattern p=Pattern.compile(".*?(http://.*?.jpg).*",Pattern.DOTALL);
						
						Matcher matcher = p.matcher(result);
						
						if(matcher.find()){
							resultUrl=matcher.group(1);
							LogUtil.d("匹配结果："+resultUrl);
						}
						
						UiUtils.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								waitDialog.dismiss();
								String origin=etContent.getText().toString();
								origin=origin+"\n"+resultUrl;
								
								etContent.setText(origin);
							}
						});
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode,
					cz.msebera.android.httpclient.Header[] headers,
					byte[] responseBody, Throwable error) {
				LogUtil.d("结果：" + statusCode
						+ new String(responseBody).toString());
				error.printStackTrace();
			}

		});

	}

	
	

	// public static void uploadFile(Context context,final WaitDialog
	// waitDialog,File file){
	//
	// AsyncHttpClient ahc=new AsyncHttpClient();
	//
	// String url=Constants.getUploadUrl();
	// // ahc.addHeader("Content-Type", "multipart/form-data");
	//
	// String cookie=BaseApplication.getCookie();
	// if(cookie==null){
	// cookie=BaseApplication.autoLogin(context, true);
	// }
	// //njubbs_upload36649831.jpg
	// ahc.addHeader("Cookie", cookie);
	// RequestParams rp=new RequestParams();
	// try {
	// rp.put("up", file);
	// } catch (Exception e1) {
	// e1.printStackTrace();
	// }
	//
	// rp.put("exp", "");
	// rp.put("ptext", "text");
	// rp.put("board", "Pictures");
	// rp.setForceMultipartEntityContentType(true);
	//
	// ahc.post(url, rp,new AsyncHttpResponseHandler() {
	// public void onSuccess(int statusCode, Header[] headers, byte[]
	// responseBody) {
	// /*
	// responseBody is empty. but should not be empty. the responseBody shouled
	// be like this:
	//
	// <meta http-equiv='Refresh' content='0; url=bbsupload2?board=Pictures
	// &file=10104&name=LilyDroid0605184406.jpg&exp=UploadByLilyDroid
	// &ptext=
	// '>
	//
	// but i got nothing! so confused. somebody help me.
	// */
	// LogUtil.d("结果："+statusCode);
	// waitDialog.dismiss();
	// if(statusCode==200){
	// try {
	// String result=new String(responseBody,"gb2312");
	// LogUtil.d("result:"+result);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// public void onFailure(int statusCode,
	// Header[] headers,
	// byte[] responseBody, Throwable error) {
	// waitDialog.dismiss();
	// LogUtil.d("结果："+statusCode+ new String(responseBody).toString());
	// error.printStackTrace();
	// }
	//
	// });
	// }
	//
	//
	//
	//
	//
	//
	// public static void uploadFile2(final Context context,final WaitDialog
	// dialog,final File file){
	// ThreadManager.getInstance().createLongPool().execute(new Runnable() {
	// @Override
	// public void run() {
	// HttpClient httpClient=new DefaultHttpClient();
	// try {
	// String url=Constants.getUploadUrl();
	// HttpPost httpPost=new HttpPost(url);
	//
	// FileBody fb=new FileBody(file,"image/jpeg");
	// String cookie=BaseApplication.getCookie();
	// if(cookie==null){
	// cookie=BaseApplication.autoLogin(context, true);
	// }
	//
	// //httpPost.setHeader("User-Agent","SOHUWapRebot");
	// httpPost.addHeader("Cookie", cookie);
	// httpPost.addHeader("Content-Type", "multipart/form-data");
	// MultipartEntity me=new MultipartEntity();
	//
	// StringBody sb1=new StringBody("");
	// StringBody sb2=new StringBody("text",Charset.forName("GB2312"));
	// StringBody sb3=new StringBody("Pictures",Charset.forName("GB2312"));
	//
	// me.addPart("up",fb);
	// me.addPart("exp", sb1);
	// me.addPart("ptext",sb2);
	// me.addPart("board", sb3);
	//
	//
	// httpPost.setEntity(me);
	// HttpResponse response=httpClient.execute(httpPost);
	//
	// int statusCode = response.getStatusLine().getStatusCode();
	//
	// if(statusCode == HttpStatus.SC_OK){
	// System.out.println("服务器正常响应.....");
	// HttpEntity resEntity = response.getEntity();
	// InputStream inputStream = resEntity.getContent();
	//
	// BufferedReader br=new BufferedReader(new
	// InputStreamReader(inputStream,"gbk"));
	// //nju_bbs160605153704.jpg
	// String line=null;
	// StringBuffer sb=new StringBuffer();
	//
	// while((line=br.readLine())!=null){
	// sb.append(line);
	// sb.append("\n");
	// }
	// String result=sb.toString();
	//
	// LogUtil.d("result2:"+result);
	//
	// /**
	// <meta http-equiv='Refresh' content='0; url=bbsupload2?board=
	// &file=19068&name=njubbskdsadjkfa.jpg&exp=Content-Transfer-Encoding: 8bit
	// &ptext=Content-Disposition: form-data; name="p'>
	// */
	// // String result=EntityUtils.toString(resEntity);
	// result=result.replaceAll("\n", "");
	// //截取 &file=19068&name=njubbskdsadjkfa.jpg
	// int start= result.indexOf("&file=");
	// int end=result.indexOf("&exp=");
	// result = result.substring(start, end);
	//
	// //bbsupload2?board=Pictures&file=2672&name=1.jpg&exp=&ptext=text HTTP/1.1
	// String url2="http://bbs.nju.edu.cn/bbsupload2?board=Pictures"+
	// result+"&exp=&ptext=text";
	// LogUtil.d("url2:"+url2);
	//
	// handleUpload2(context,url2,dialog);
	//
	// }
	//
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	// });
	//
	//
	// }
	//
	
}
