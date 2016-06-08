package com.oterman.njubbs.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.expore.FindBoardActivity;
import com.oterman.njubbs.activity.expore.FindTopicActivity;
import com.oterman.njubbs.fragment.BoardsFragment;
import com.oterman.njubbs.fragment.factory.FragmentFactory;
import com.oterman.njubbs.protocol.CheckNewMailProtocol;
import com.oterman.njubbs.protocol.HotBoardProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.ThreadManager;

@SuppressLint("NewApi")
public class MainActivity_new extends FragmentActivity implements OnClickListener {

	private ViewPager vpPages;
	private RadioGroup rgGroup;
	private int newMailCount;
	private RadioButton rbMe;
	
	protected ActionBar actionBar;
	protected View actionBarView;
	private ImageButton ibSearch;
	
	private int currentPos=0;
	private TextView tvNewMailCount;
	
	boolean isBoardUpdated=false;//版面page是否更新

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_new);
		
		//更改状态栏的颜色
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
			Window window = this.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources().getColor(R.color.green));
		}
		
		//处理actionBar
		initActionBar();
		
		
		initViews();
		
	}
	
	//初始化actionBar
	private void initActionBar() {
		// 自定义actionbar
		actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBarView = View.inflate(getApplicationContext(),R.layout.actionbar_main, null);
		
		ibSearch = (ImageButton) actionBarView.findViewById(R.id.btn_main_search);
		
		ibSearch.setOnClickListener(this);
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		actionBar.setCustomView(actionBarView, params);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		//检查新邮件
		checkHasNewMail();
		LogUtil.d("onResume  检查新邮件");
	}
	
	public void initViews() {
		
		//初始化viewpager
		vpPages = (ViewPager) this.findViewById(R.id.vp_pages);
		vpPages.setAdapter(new MyPageAdapter(getSupportFragmentManager()));

		//初始化radiogroup
		rgGroup = (RadioGroup) this.findViewById(R.id.rg_bottom_group);
		rbMe = (RadioButton) this.findViewById(R.id.rb_me);
		
		tvNewMailCount = (TextView) this.findViewById(R.id.tv_new_mail_count);
		
		
		rgGroup.check(R.id.rb_hottopic);
		
		
		//设置监听 同步viewpager
		rgGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_hottopic:
					vpPages.setCurrentItem(0);
					break;
				case R.id.rb_boards:
					vpPages.setCurrentItem(1);
					break;
				case R.id.rb_find:
					vpPages.setCurrentItem(2);
					break;
				case R.id.rb_me:
					vpPages.setCurrentItem(3);
					break;
				}
			}
		});
		
		//设置监听 同步radiobutton
		vpPages.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				//加载数据
				currentPos=position;
				Fragment fragment = FragmentFactory.creatFragment(position);
				
				if(fragment instanceof BoardsFragment){
					if(!isBoardUpdated){
						//版面没有更新
						((BoardsFragment)fragment).firstRefresh();
						isBoardUpdated=true;
					}
					
				}
				
//				if(fragment instanceof BaseFragment){
//					//((BaseFragment)fragment).showViewFromServer();
//				}
//				
//				if(fragment instanceof AboutMeFragment){
//					((AboutMeFragment)fragment).updateViews();
//				}
				
				//选中页面时，切换到对应的radiobutton;
				switch (position) {
				case 0:
					//ibSearch.setVisibility(View.VISIBLE);
					rgGroup.check(R.id.rb_hottopic);
					break;
				case 1:
					//ibSearch.setVisibility(View.VISIBLE);
					rgGroup.check(R.id.rb_boards);
					
					
					
					break;
				case 2:
					//ibSearch.setVisibility(View.INVISIBLE);
					rgGroup.check(R.id.rb_find);
					break;
				case 3:
					//ibSearch.setVisibility(View.INVISIBLE);
					rgGroup.check(R.id.rb_me);
					break;
				default:
					break;
				}
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		
		
	}

	//检查是否有新的站内
	public void checkHasNewMail() {
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				LogUtil.d("检查是否有新邮件啦");
				CheckNewMailProtocol protocol=new CheckNewMailProtocol();
				String url=Constants.HAS_NEW_MAIL_URL;
				newMailCount = protocol.checkFromServer(url,MainActivity_new.this);
				LogUtil.d("检查结果："+newMailCount);
				if(newMailCount>0){//有新邮件
					//更新状态
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							tvNewMailCount.setVisibility(View.VISIBLE);
							tvNewMailCount.setText(newMailCount+"");
						}
					});
				}else{//没有新邮件
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							tvNewMailCount.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		});
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_main_search:
			if(currentPos==1){//搜索帖子
				Intent findBoardIntent=new Intent(this,FindBoardActivity.class);
				startActivity(findBoardIntent);
			}else{//搜索版面
				Intent findTopicIntent=new Intent(this,FindTopicActivity.class);
				startActivity(findTopicIntent);
			}
			break;

		default:
			break;
		}
	}

	class MyPageAdapter extends FragmentPagerAdapter{

		public MyPageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			
			Fragment fragment = FragmentFactory.creatFragment(position);
			
			return fragment;
		}

		@Override
		public int getCount() {
			return 4;
		}
		
	}
	
	
}
