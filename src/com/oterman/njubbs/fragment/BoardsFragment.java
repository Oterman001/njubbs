package com.oterman.njubbs.fragment;

import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.BoardDetailActivity;
import com.oterman.njubbs.activity.TopicDetailActivity;
import com.oterman.njubbs.bean.BoardInfo;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.protocol.HotBoardProtocol;
import com.oterman.njubbs.protocol.TopTenProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.view.LoadingView.LoadingState;

public class BoardsFragment extends BaseFragment {



	private List<BoardInfo> dataList;
	private ListView lv;
	
	@Override
	public View createSuccessView() {
		lv = new ListView(getContext());
		lv.setDivider(new ColorDrawable(0x55888888));  
		lv.setDividerHeight(1);
		lv.setAdapter(new BoardAdapter());
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BoardInfo info = dataList.get(position);
				
				Intent intent=new Intent(getContext(),BoardDetailActivity.class);
				
				intent.putExtra("boardUrl", info.boardUrl);
				startActivity(intent);
			}
		});
		return lv;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		HotBoardProtocol protocol=new HotBoardProtocol();
		dataList = protocol.loadFromServer(Constants.HOT_BOARD_ULR);
		
		return dataList==null?LoadingState.LOAD_FAILED:LoadingState.LOAD_SUCCESS;
	}

	class BoardAdapter extends BaseAdapter{
		Random r=new Random();
		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=null;
			ViewHolder holder=null;
			if(convertView==null){
				view=View.inflate(getContext(), R.layout.list_item_board, null);
				holder=new ViewHolder();
				holder.tvBoard=(TextView) view.findViewById(R.id.tv_board);
				view.setTag(holder);
			}else{
				view=convertView;
				holder=(ViewHolder) view.getTag();
			}
			
			BoardInfo info = dataList.get(position);
			
			holder.tvBoard.setText(info.boardName+"("+info.chineseName+")");
			
			return view;
		}
		
		class ViewHolder{
			TextView tvBoard;
		}
		
	}
}
