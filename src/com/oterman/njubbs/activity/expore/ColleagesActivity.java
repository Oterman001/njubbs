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
		dataMap.put("����δ��������", "http://www.bdwm.net/bbs/");
		dataMap.put("ˮľ�廪���廪��", "http://www.newsmth.net/nForum/#!mainpage");
		dataMap.put("��ˮ˼Դ���Ͻ���", "https://bbs.sjtu.edu.cn/frame2.html");
		dataMap.put("���¹⻪��������", "https://bbs.fudan.edu.cn/bbs/top10");
		dataMap.put("����ɽˮ�����", "http://bbs.whu.edu.cn/");
		dataMap.put("�Ұ��Ͽ����Ͽ���", "http://bbs.nankai.edu.cn/main");
	
		dataMap.put("������󴣨����", "http://sbbs.seu.edu.cn/frames.html");
		dataMap.put("���ƻƺף����пƴ�", "http://bbs.whnet.edu.cn/main.html");
		dataMap.put("嫺����ƣ��пƴ�", "http://bbs.ustc.edu.cn/main.html");
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
		return "��Уbbs";
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
