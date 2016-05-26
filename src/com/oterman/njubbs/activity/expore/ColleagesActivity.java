package com.oterman.njubbs.activity.expore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.MyActionBarActivity;

public class ColleagesActivity extends MyActionBarActivity{
	private ListView lvColleages;
	
	static Map<String, String> dataMap=new TreeMap();
	static List<String> list=new ArrayList<>();
	static{
		dataMap.put("北大未名（北大）", "http://www.bdwm.net/bbs/");
		dataMap.put("水木清华（清华）", "http://www.newsmth.net/nForum/#!mainpage");
		dataMap.put("饮水思源（上交）", "https://bbs.sjtu.edu.cn/frame2.html");
		dataMap.put("日月光华（复旦）", "https://bbs.fudan.edu.cn/bbs/top10");
		dataMap.put("珞珈山水（武大）", "http://bbs.whu.edu.cn/");
		dataMap.put("我爱南开（南开）", "http://bbs.nankai.edu.cn/main");
	
		dataMap.put("虎踞龙蟠（东大）", "http://sbbs.seu.edu.cn/frames.html");
		dataMap.put("白云黄鹤（华中科大）", "http://bbs.whnet.edu.cn/main.html");
		dataMap.put("瀚海星云（中科大）", "http://bbs.ustc.edu.cn/main.html");
		list.addAll(dataMap.keySet());
	}
	private ColleageAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frag_discovery);
		lvColleages = (ListView)findViewById(R.id.lv_colleages);
		
		adapter = new ColleageAdapter();
		
		lvColleages.setDivider(getResources().getDrawable(R.color.list_divider_color));
		lvColleages.setDividerHeight(1);
		
		lvColleages.setAdapter(adapter);
		
		lvColleages.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String url = dataMap.get(list.get(position));
				
				Intent intent=new Intent(ColleagesActivity.this,ColleageContentActivity.class);
				intent.putExtra("url", url);
				intent.putExtra("name",list.get(position));
				startActivity(intent);
						
			}
		});
		
	}
	@Override
	protected String getBarTitle() {
		return "高校bbs";
	}
	public class ColleageAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view=View.inflate(getApplicationContext(), R.layout.list_item_colleage, null);
			TextView tv=(TextView) view.findViewById(R.id.tv_colleage);
			
			tv.setText(list.get(position));
			
			return view;
		}
		
	}
}
