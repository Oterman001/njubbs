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
		list.add("����δ��������");
		
		dataMap.put("ˮľ�廪���廪��", "http://m.newsmth.net/");
		list.add("ˮľ�廪���廪��");
		
		dataMap.put("��ˮ˼Դ���Ͻ���", "https://bbs.sjtu.edu.cn/frame2.html");
		list.add("��ˮ˼Դ���Ͻ���");
		
		dataMap.put("���¹⻪��������", "https://bbs.fudan.edu.cn/bbs/top10");
		list.add("���¹⻪��������");
		
		dataMap.put("嫺����ƣ��пƴ�", "http://bbs.ustc.edu.cn/main.html");
		list.add("嫺����ƣ��пƴ�");
		
		dataMap.put("����˴��˴�", "http://www.tdrd.org/nForum/#!default");
		list.add("����˴��˴�");
		
		dataMap.put("����ɽˮ�����", "http://bbs.whu.edu.cn/");
		list.add("����ɽˮ�����");
		
		dataMap.put("���ƻƺף����пƴ�", "http://bbs.whnet.edu.cn/main.html");
		list.add("���ƻƺף����пƴ�");
		
		dataMap.put("�Ұ��Ͽ����Ͽ���", "http://bbs.nankai.edu.cn/main");
		list.add("�Ұ��Ͽ����Ͽ���");
		
		dataMap.put("������󴣨����", "http://sbbs.seu.edu.cn/frames.html");
		list.add("������󴣨����");


		dataMap.put("�������Σ��ô�", "http://bbs.xmu.edu.cn/frames.html");
		list.add("�������Σ��ô�");
		
		dataMap.put("����ٸվ��������", "http://bbs.xjtu.edu.cn/BMYAAMWDQQHLCRGYRNCVILMFIPPIVILFOLSE_B/");
		list.add("����ٸվ��������");
		
		dataMap.put("��ɫ�ǿգ�����", "http://bbs.scu.edu.cn/frames.html");
		list.add("��ɫ�ǿգ�����");
		
		dataMap.put("��ˮ���ϣ����ӿƴ�", "http://bbs.uestc.edu.cn/forum.php");
		list.add("��ˮ���ϣ����ӿƴ�");
//		list.addAll(dataMap.keySet());
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
