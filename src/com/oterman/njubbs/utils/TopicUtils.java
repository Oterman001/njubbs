package com.oterman.njubbs.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.activity.topic.NewTopicActivity;
import com.oterman.njubbs.dialog.WaitDialog;

/**
 * 帖子的帮助类
 * 
 * @author oterman
 * 
 */
public class TopicUtils {

	static String resultUrl = null;//上传图片成功后返回的url

	/**
	 * 从intent中获取选中图片的路径
	 */
	public static String getPicPathFromUri(final Context context, Intent data) {
		// content://media/external/images/media/660109
		Uri selectedImage = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		String picturePath = null;
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturePath = cursor.getString(columnIndex);
		} else {
			picturePath = selectedImage.getPath();
			// 根据path处理文件
		}
		return picturePath;
	}

	/**
	 * 展示从对话框中选中的图片
	 */
	public static void showChosedPic(final Activity activity,
			String picturePath, final EditText etContent) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		ImageView iv = new ImageView(activity);
		final Bitmap bitmap = UiUtils.parseUriToBm(activity, picturePath);
		// 将bitmap存到本地

		iv.setImageBitmap(bitmap);
		iv.setPadding(6, 6, 6, 6);
		builder.setTitle("选择图片");
		builder.setView(iv);

		builder.setNegativeButton("重新选择",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						activity.startActivityForResult(intent, 100);
					}
				});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 处理上传图片逻辑
				handleUploadPic(activity, bitmap, etContent);
			}
		});
		builder.show();
	}

	/**
	 * 处理上传图片
	 */
	public static void handleUploadPic(final Activity activity,
			final Bitmap bitmap, final EditText etContent) {

		final WaitDialog waitDialog = new WaitDialog(activity);
		waitDialog.setMessage("正在努力上传。。");
		waitDialog.show();

		// 将bitmap缓存到本地
		String dirPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/njubbs/photo/";
		File dirFile = new File(dirPath);
		if (!dirFile.exists())
			dirFile.mkdirs();

		// 处理文件名
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		String date2 = sdf.format(date);
		String filename = "nju_bbs" + date2 + ".jpg";

		// 将图片保存到本地
		UiUtils.saveBitmapToLocal(bitmap, filename);

		File file = new File(dirFile, filename);
		// 上传图片
		TopicUtils.uploadFile(activity, waitDialog, file, etContent);

	}

	/**
	 * 上传图片
	 */
	public static void uploadFile(final Context context,
			final WaitDialog dialog, final File file, final EditText etContent) {

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

						// 上传图片第二步
						handleUploadStepTwo(context, url2, dialog, etContent);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 上传图片第二步
	 */
	protected static void handleUploadStepTwo(final Context context, String url2,
			final WaitDialog waitDialog, final EditText etContent) {

		AsyncHttpClient ahc = new SyncHttpClient();
		String cookie = BaseApplication.getCookie();
		if (cookie == null) {
			cookie = BaseApplication.autoLogin(context, true);
		}

		ahc.addHeader("Cookie", cookie);
		ahc.get(url2, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					cz.msebera.android.httpclient.Header[] headers,
					byte[] responseBody) {
				// 这个方法会执行，但是得到的responsebody没有值，但是应该有值得。

				LogUtil.d("结果：" + statusCode);// Content-Encoding: gzip
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "gb2312");

						LogUtil.d("result:" + result);
						Pattern p = Pattern.compile(".*?(http://.*?.jpg).*",
								Pattern.DOTALL);

						Matcher matcher = p.matcher(result);

						if (matcher.find()) {
							resultUrl = matcher.group(1);
							LogUtil.d("匹配结果：" + resultUrl);
						}

						UiUtils.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								waitDialog.dismiss();
//								String origin = etContent.getText().toString();
//								if (!TextUtils.isEmpty(origin)) {
//									origin = origin + "\n" + resultUrl;
//								} else {
//									origin = resultUrl;
//								}
//								etContent.setText(origin);
								
								resultUrl="\n"+resultUrl+"\n";
								//在光标处插入图片链接
								String oriText=etContent.getText().toString();//原始文字
								int index=Math.max(etContent.getSelectionStart(),0);//获取光标处位置，没有光标，返回-1
								
								StringBuffer sb=new StringBuffer(oriText);
								sb.insert(index, resultUrl);
								String string = sb.toString().replaceAll("\n", "<br>");
								
								Spanned spanned = Html.fromHtml(string);
								CharSequence text = SmileyParser.getInstance(context).strToSmiley(spanned);
								etContent.setText(text);
								
								etContent.setSelection(index+resultUrl.length());
								
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

	/**
	 * 用httputils来上传图片，失败了
	 */
	protected void handleUploadPicWithHttpUtils(final Activity activity,
			final Bitmap bitmap) {
		final WaitDialog waitDialog = new WaitDialog(activity);
		waitDialog.setMessage("正在努力上传。。");
		waitDialog.show();

		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				HttpUtils httpUtils = new HttpUtils();
				httpUtils.configTimeout(100000);
				// httpUtils.configResponseTextCharset("gbk");
				RequestParams rp = new RequestParams();
				rp.setContentType("multipart/form-data");

				// 将bitmap缓存到本地
				String dirPath = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/njubbs/photo/";
				File dirFile = new File(dirPath);
				if (!dirFile.exists())
					dirFile.mkdirs();

				String filename = "njubbs_upload"
						+ SystemClock.elapsedRealtime() + ".jpg";
				UiUtils.saveBitmapToLocal(bitmap, filename);

				File file = new File(dirFile, filename);

				rp.addBodyParameter("up", file);

				rp.addHeader("Accept-Encoding", "identity");
				String cookie = BaseApplication.getCookie();
				if (cookie == null) {
					cookie = BaseApplication.autoLogin(activity, true);
				}

				rp.addHeader("Cookie", cookie);
				rp.addBodyParameter("exp", "xixi");
				rp.addBodyParameter("ptext", "text");
				rp.addBodyParameter("board", "Pictures");

				try {
					String url = Constants.getUploadUrl();

					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST,
							url, rp);
					String result = BaseApplication.StreamToStr(stream);
					LogUtil.d("上传结果：" + result);

					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (waitDialog != null) {
								waitDialog.dismiss();
							}
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (waitDialog != null) {
								waitDialog.dismiss();
								MyToast.toast("上传失败");
							}
						}
					});
				}
			}
		});
	}

}
