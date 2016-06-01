package com.oterman.njubbs.activity.expore;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.MyActionBarActivity;
import com.oterman.njubbs.dialog.WaitDialog;
import com.oterman.njubbs.holders.UserDetailHolder;
import com.oterman.njubbs.protocol.QueryFriendProtocol;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;

/**
 * ��Ӻ���
 * 
 * @author oterman
 * 
 */
public class AddFriendActivity extends MyActionBarActivity implements
		OnClickListener {

	private Spinner spKeys;
	private EditText etKeys;
	private int spPosition = 0;
	private ImageButton ibSearch;
	private WaitDialog waitDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friends);

		spKeys = (Spinner) this.findViewById(R.id.sp_keys);
		etKeys = (EditText) this.findViewById(R.id.et_keys);
		ibSearch = (ImageButton) this.findViewById(R.id.ib_search);
		lvResult = (ListView) this.findViewById(R.id.lv_result);
		tvStatus = (TextView) this.findViewById(R.id.tv_status);
		
//		resultAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, resultList);
//		lvResult.setAdapter(resultAdapter);
		
		ibSearch.setOnClickListener(this);

		List<String> list = new ArrayList<>();
		list.add("��id����");
		list.add("���ǳƲ�");

		ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spKeys.setAdapter(spAdapter);
		spKeys.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//MyToast.toast("ѡ�е�λ�ã�" + position);
				spPosition = position;
				if (position == 0) {
					etKeys.setHint("������id");
				} else {
					etKeys.setHint("�������ǳ�");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				etKeys.setHint("������id");
			}
		});

	}

	@Override
	protected String getBarTitle() {
		return "�����û�";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_search:
			if(TextUtils.isEmpty(etKeys.getText().toString())){
				
				MyToast.toast("���벻��Ϊ��!");
				return;
			}
			
			if (waitDialog == null) {
				waitDialog = new WaitDialog(this);
			}
			waitDialog.setMessage("����Ŭ����ѯ");
			waitDialog.show();
			
			handleQuery();

			break;

		default:
			break;
		}
	}
	private List<String> resultList;
	private ListView lvResult;
	private ArrayAdapter<String> resultAdapter;
	private TextView tvStatus;

	/**
	 * �����ѯ
	 */
	private void handleQuery() {
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					QueryFriendProtocol protocol = new QueryFriendProtocol();
					String userid = etKeys.getText().toString().trim();

					final List<String> tempList = protocol.loadFromServer(userid,spPosition == 0 ? false : true);
					
					if(resultList!=null){
						resultList.clear();
						if(tempList!=null){
							resultList.addAll(tempList);
						}
					}else{
						resultList=tempList;
					}
					// LogUtil.d("��ѯ�����"+resultList.toString());
					// �����ˢ�µ�����
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							waitDialog.dismiss();
							updateResult(tempList);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							waitDialog.dismiss();
						}
					});
				}
			}
		});
	}
	private void updateResult(List<String> tempList) {
		if(tempList==null||tempList.size()==0){
			tvStatus.setVisibility(View.VISIBLE);
			if(lvResult!=null){
				lvResult.setVisibility(View.GONE);
			}
		}else{
			tvStatus.setVisibility(View.INVISIBLE);
			lvResult.setVisibility(View.VISIBLE);
			
			if(resultAdapter==null){
				
				resultAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, resultList);
				lvResult.setAdapter(resultAdapter);
				//����ʱ�����
				lvResult.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						String userid= resultList.get(position);
						
						if(userid.contains("(")){
							userid=userid.substring(0, userid.indexOf("(")).trim();
						}
						AlertDialog.Builder builder=new AlertDialog.Builder(AddFriendActivity.this);
						AlertDialog dialog2=null;
						UserDetailHolder holder=new UserDetailHolder(AddFriendActivity.this);
						holder.updateStatus(userid);
						builder.setView(holder.getRootView());
						dialog2 = builder.show();
						holder.setOwnerDialog(dialog2);
					}
				});
				
			}else{
				resultAdapter.notifyDataSetChanged();
			}
		}
		

		
	}

}
