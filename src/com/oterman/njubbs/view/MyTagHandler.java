package com.oterman.njubbs.view;

import java.util.Locale;

import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;

import com.oterman.njubbs.activity.BigImageActivity;
import com.oterman.njubbs.utils.MyToast;

import com.oterman.njubbs.view.MyTagHandler;

public class MyTagHandler implements TagHandler {
	
	private Context mContext;

	public MyTagHandler(Context context) {
		mContext = context.getApplicationContext();
	}

	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		// �����ǩ<img>
		if (tag.toLowerCase(Locale.getDefault()).equals("img")) {
			// ��ȡ����
			int len = output.length();
			// ��ȡͼƬ��ַ 
			ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
			String imgURL = images[0].getSource();
			// ʹͼƬ�ɵ������������¼�
			output.setSpan(new ClickableImage(mContext, imgURL), len - 1, len,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	private class ClickableImage extends ClickableSpan {
		private String url;
		private Context context;

		public ClickableImage(Context context, String url) {
			this.context = context;
			this.url = url;
		}

		@Override
		public void onClick(View widget) { // ����ͼƬ���֮��Ĵ��� 
			
			//MyToast.toast("����ˣ�"+url);
			
			Intent intent=new Intent(context,BigImageActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("imgurl", url);
			context.startActivity(intent);
			
		}
	}
}


