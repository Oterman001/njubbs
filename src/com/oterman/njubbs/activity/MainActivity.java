package com.oterman.njubbs.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.oterman.njubbs.R;
import com.oterman.njubbs.R.color;
import com.oterman.njubbs.R.id;
import com.oterman.njubbs.R.layout;
import com.oterman.njubbs.R.menu;
import com.oterman.njubbs.fragment.BaseFragment;
import com.oterman.njubbs.fragment.factory.FragmentFactory;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity {

	private ViewPager vpPages;
	private RadioGroup rgGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//更改状态栏的颜色
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
			Window window = this.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources().getColor(R.color.green));
		}
		
		initViews();
	}

	private void initViews() {
		//初始化viewpager
		vpPages = (ViewPager) this.findViewById(R.id.vp_pages);
		vpPages.setAdapter(new MyPageAdapter(getSupportFragmentManager()));

		//初始化radiogroup
		rgGroup = (RadioGroup) this.findViewById(R.id.rg_bottom_group);
		
		rgGroup.check(R.id.rb_topten);
		
		//设置监听 同步viewpager
		rgGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_topten:
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
				FragmentFactory.creatFragment(position).showViewFromServer();
				//选中页面时，切换到对应的radiobutton;
				switch (position) {
				case 0:
					rgGroup.check(R.id.rb_topten);
					break;
				case 1:
					rgGroup.check(R.id.rb_boards);
					break;
				case 2:
					rgGroup.check(R.id.rb_find);
					break;
				case 3:
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

	class MyPageAdapter extends FragmentPagerAdapter{

		public MyPageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			
			BaseFragment fragment = FragmentFactory.creatFragment(position);
			
			return fragment;
		}

		@Override
		public int getCount() {
			return 4;
		}
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
