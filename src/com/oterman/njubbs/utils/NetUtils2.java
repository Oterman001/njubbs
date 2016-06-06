//package com.oterman.njubbs.utils;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.InputStreamReader;
//
//import org.apache.commons.httpclient.Cookie;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.cookie.CookiePolicy;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.methods.multipart.FilePart;
//import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
//import org.apache.commons.httpclient.methods.multipart.Part;
//import org.apache.commons.httpclient.methods.multipart.StringPart;
//
//import android.content.Context;
//
//import com.oterman.njubbs.BaseApplication;
//import com.oterman.njubbs.dialog.WaitDialog;
//
//public class NetUtils2 {
//	public static Cookie[] cookies = null;
//	
//	public static void uploadFile(final Context context,final WaitDialog dialog,final File file){
//		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
//			@Override
//			public void run() {
//				HttpClient  httpClient=new HttpClient();
//				httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
//				httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
//
//				httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
//				httpClient.getParams().setParameter("http.protocol.single-cookie-header", true);
//				
//				//处理cookie
//				String cookie = BaseApplication.getCookie();
//				if(cookie==null){
//					cookie=BaseApplication.autoLogin(context, true);
//				}
//				
//				//添加cookie
//				if (cookies != null) {
//					for (Cookie cc : cookies) {
//						httpClient.getState().addCookie(cc);
//					}
//				}
//				
//				String url=Constants.getUploadUrl();
//				PostMethod post = new PostMethod(url);
//				
//				try {
//					FilePart part1 = new FilePart("up",file.getName(),file);
//					StringPart sp = new StringPart("board","Pictures");
//					StringPart sp1 = new StringPart("ptext","");
//					StringPart sp2 = new StringPart("exp","njubbs");
//					
//					Part[] parts = {part1,sp2,sp1,sp };
//					post.getParams().setContentCharset("GB2312");
//					post.setRequestEntity(new MultipartRequestEntity(parts,	post.getParams()));
//					
//					post.addRequestHeader("Content-Type", "multipart/form-data");
//					
//					int statusCode = httpClient.executeMethod(post);
//					if (statusCode != HttpStatus.SC_OK) {
//						System.err.println("Method failed: " + post.getStatusLine());
//					}
//					
//					String result = "";// 返回的结果
//					StringBuffer resultBuffer = new StringBuffer();
//					
//					BufferedReader in = new BufferedReader(new InputStreamReader(post
//							.getResponseBodyAsStream(), post.getResponseCharSet()));
//					String inputLine = null;
//					while ((inputLine = in.readLine()) != null) {
//						resultBuffer.append(inputLine);
//						resultBuffer.append("\n");
//					}
//					
//					result = new String(resultBuffer);
//					
//					LogUtil.d("结果："+result);
//					
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
//	
//	protected static void handleCookie(Context context) {
//	}
//
//	public static void setMyCookie(String NUM,String id ,String KEY)
//	{
//		cookies = new Cookie[3];
//
//		cookies[0] = new Cookie();
//		cookies[0].setDomain("bbs.nju.edu.cn");
//		cookies[0].setPath("/");
//		cookies[0].setName("_U_NUM");
//		cookies[0].setValue(NUM);
//
//		cookies[1] = new Cookie();
//		cookies[1].setDomain("bbs.nju.edu.cn");
//		cookies[1].setPath("/");
//		cookies[1].setName("_U_UID");
//		cookies[1].setValue(id);
//
//		cookies[2] = new Cookie();
//		cookies[2].setDomain("bbs.nju.edu.cn");
//		cookies[2].setPath("/");
//		cookies[2].setName("_U_KEY");
//		cookies[2].setValue(KEY);
//	}
//	
//	
//}
