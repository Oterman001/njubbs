package com.oterman.njubbs.view;

import java.util.Locale;

import org.xml.sax.XMLReader;

import com.oterman.njubbs.utils.MyToast;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;

public class URLDrawable extends BitmapDrawable {
	protected Bitmap bitmap;

	@Override
	public void draw(Canvas canvas) {
		if (bitmap != null) {
			canvas.drawBitmap(bitmap, 0, 0, getPaint());
		}
	}
}

class MyTagHandler2 implements TagHandler {

	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {

	}

}

