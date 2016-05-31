package com.oterman.njubbs.activity.expore;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.board.BoardDetailActivity;
import com.oterman.njubbs.bean.BoardInfo;
import com.oterman.njubbs.db.BoardDao;
import com.oterman.njubbs.utils.MyToast;

@SuppressLint("NewApi")
public  class FindBoardActivity extends FragmentActivity {

	protected ActionBar actionBar;
	protected View actionBarView;
	private ListView lvBoards;
	private BoardDao boardDao;
	private List<BoardInfo> boardList;
	private BoardAdaopter adapter;
	private EditText etBoard;
	private ImageButton ibSearch;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 更改状态栏的颜色
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = this.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources()
					.getColor(R.color.green));
		}

		//处理actionBar
		initActionBar();
		
		setContentView(R.layout.activity_search_board);
		
		initViews();

	}

	
	private void initViews() {
		lvBoards = (ListView) this.findViewById(R.id.lv_boards);
		
		boardDao = new BoardDao();
		boardList = boardDao.queryAll();
		
		adapter = new BoardAdaopter();
		
		lvBoards.setAdapter(adapter);
		
		lvBoards.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BoardInfo info = boardList.get(position);
				//MyToast.toast(info.boardUrl);
				
				Intent intent=new Intent(getApplicationContext(),BoardDetailActivity.class);
				String boardUrl=info.boardUrl;
				intent.putExtra("boardUrl", boardUrl);
				startActivity(intent);
			}
		});
		
	}

	private void initActionBar() {
		// 自定义actionbar
		actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		actionBarView = View.inflate(getApplicationContext(),R.layout.actionbar_custom_search, null);
		
		View back = actionBarView.findViewById(R.id.btn_back2);
		etBoard = (EditText) actionBarView.findViewById(R.id.et_board);
		
		etBoard.setImeOptions(EditorInfo.IME_ACTION_SEND);  //回车
		
		etBoard.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				updateResult();
				
//				String text=s.toString().trim();
//				BoardDao dao=new BoardDao();
//				List<BoardInfo> tempList= dao.queryByCondition(text);
//				if(boardList!=null){
//					boardList.clear();
//					boardList.addAll(tempList);
//				}
//				adapter.notifyDataSetChanged();
				
			}
		});
		
		ibSearch = (ImageButton) actionBarView.findViewById(R.id.ib_search);
		
		etBoard.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				
				if (actionId==EditorInfo.IME_ACTION_SEND||(event!=null&&event.getKeyCode()==KeyEvent.KEYCODE_ENTER)) {
					
//					String text=etBoard.getText().toString().trim();
//					MyToast.toast(text);
//					
//					BoardDao dao=new BoardDao();
//					List<BoardInfo> tempList= dao.queryByCondition(text);
//					
//					if(boardList!=null){
//						boardList.clear();
//						boardList.addAll(tempList);
//						
//					}
//					adapter.notifyDataSetChanged();
					
					updateResult();
					
					return true;
				}
				
				return false;
			}
		});
		
		
		ibSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateResult();
			}
		});
		
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		actionBar.setCustomView(actionBarView, params);
	}
	
	//更新查询结果
	private void updateResult() {
		String text=etBoard.getText().toString().trim();
		
		BoardDao dao=new BoardDao();
		List<BoardInfo> tempList= dao.queryByCondition(text);
		
		if(boardList!=null){
			boardList.clear();
			boardList.addAll(tempList);
			
		}
		adapter.notifyDataSetChanged();
	}
	
	
	private class BoardAdaopter extends BaseAdapter{
		@Override
		public int getCount() {
			return boardList.size();
		}

		@Override
		public Object getItem(int position) {
			return boardList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder=null;
			if (convertView==null) {
				holder=new ViewHolder();
				convertView=View.inflate(getApplicationContext(), R.layout.list_item__search_board, null);
				holder.tvBoard=(TextView) convertView.findViewById(R.id.tv_search_board);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			
			BoardInfo info = boardList.get(position);
			String str=info.boardName+"("+info.chineseName+")";
			
			holder.tvBoard.setText(str);
			return convertView;
		}
		class ViewHolder{
			TextView tvBoard;
		}
		
	}

}
