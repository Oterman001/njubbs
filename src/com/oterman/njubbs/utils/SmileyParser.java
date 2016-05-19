package com.oterman.njubbs.utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.oterman.njubbs.R;
    
public class SmileyParser {    
    private static SmileyParser sInstance;    
    
    public static SmileyParser getInstance(Context context) {    
    	if(sInstance==null){
   		 sInstance = new SmileyParser(context);
    	}
       return sInstance;      
    }    
    
    private final Context mContext;    
    private final String[] mSmileyTexts;//表情文本    
    private final Pattern mPattern;    
    private final HashMap<String, Integer> mSmileyToRes;//表情文字和图片id的对应关系    
    // 表情图片集合   
    private static final int[] sIconIds = { 
    		R.drawable.s01,R.drawable.s02,R.drawable.s03,R.drawable.s04,    
    		R.drawable.s05,R.drawable.s06,R.drawable.s07,R.drawable.s08,    
    		R.drawable.s09,R.drawable.s10,R.drawable.s11,R.drawable.s12,    
    		R.drawable.s13,R.drawable.s14,R.drawable.s15,R.drawable.s16,    
    		R.drawable.s17,R.drawable.s18,R.drawable.s19,R.drawable.s20,    
    		R.drawable.s21,R.drawable.s22,R.drawable.s23,R.drawable.s24,    
    		R.drawable.s25,R.drawable.s26,R.drawable.s27,R.drawable.s28,    
    		R.drawable.s29,R.drawable.s30,R.drawable.s31,R.drawable.s32, 
    		R.drawable.s33,R.drawable.s34,R.drawable.s35,R.drawable.s36,    
    	};  
    
    
    private SmileyParser(Context context) {    
        mContext = context;    
        mSmileyTexts = mContext.getResources().getStringArray(R.array.smiley_array);    
        mSmileyToRes = buildSmileyToRes();    
        mPattern = buildPattern();    
    }    
    
    private HashMap<String, Integer> buildSmileyToRes() {    
        if (sIconIds.length != mSmileyTexts.length) {    
            throw new IllegalStateException("Smiley resource ID/text mismatch");    
        }    
        
        HashMap<String, Integer> smileyToRes = new HashMap<String, Integer>(mSmileyTexts.length);    
        
        for (int i = 0; i < mSmileyTexts.length; i++) {    
            smileyToRes.put(mSmileyTexts[i], sIconIds[i]);    
        }    
        
        return smileyToRes;    
    }    
    
    // 构建正则表达式    
    private Pattern buildPattern() {    
        StringBuilder patternString = new StringBuilder(mSmileyTexts.length * 3);    
        patternString.append('(');    
        for (String s : mSmileyTexts) {    
            patternString.append(Pattern.quote(s));    
            patternString.append('|');    
        }    
        patternString.replace(patternString.length() - 1, patternString.length(), ")");
        return Pattern.compile(patternString.toString(),Pattern.DOTALL);    
    }    
    
    // 根据文本替换成图片    
    public CharSequence strToSmiley(CharSequence text) {    
        SpannableStringBuilder builder = new SpannableStringBuilder(text);    
        Matcher matcher = mPattern.matcher(text);    
        while (matcher.find()) {    
            int resId = mSmileyToRes.get(matcher.group());    
            Drawable drawable = mContext.getResources().getDrawable(resId);    
//            drawable.setBounds(0, 0, 25, 25);//这里设置图片的大小
//            drawable.setBounds(0, 0, UiUtils.dip2px(45), UiUtils.dip2px(45));//这里设置图片的大小    
            drawable.setBounds(0, 0, UiUtils.dip2px(25), UiUtils.dip2px(25));//这里设置图片的大小    
            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);    
            builder.setSpan(imageSpan, matcher.start(),matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);    
        }    
        return builder;    
    }    
}    
  