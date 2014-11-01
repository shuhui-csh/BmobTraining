package bmob.wechat.ui;

import bmob.wechat.residememu.ResideMenu;

import bmob.wechat.residememu.ResideMenuItem;
import bmob.wechat.ui.fragment.AddressListFragment;
import bmob.wechat.ui.fragment.SettingsFragment;
import bmob.wechat.ui.fragment.WechatFragment;
import bmob.wechat.utils.Exit;

import com.wechat.R;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements
		View.OnClickListener {
	private TextView title;
	private ResideMenu resideMenu;
	private MainActivity mContext;
	private Context context = MainActivity.this;
	private ResideMenuItem itemWechat;
	private ResideMenuItem itemAddressList;
	private ResideMenuItem itemSettings;
	// 用于双击退出程序的判断时间变量
	private long mExitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化 Bmob SDK
		// 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
		//Bmob.initialize(this, "463e9b381a27dd43f2667f6b98d0aa11");
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		setUpMenu();
		//changeFragment(new WechatFragment());
	}

	private void setUpMenu() {
		// 将侧滑菜单添加到当前的activity;
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.menu_background);
		resideMenu.attachToActivity(this);
		// resideMenu.setMenuListener(menuListener);
		// 设置缩放比例值.
		resideMenu.setScaleValue(0.6f);
		// 创建菜单项;
		itemWechat = new ResideMenuItem(this, R.drawable.wechat, "聊天");
		itemAddressList = new ResideMenuItem(this, R.drawable.address_list,
				"通讯录");
		itemSettings = new ResideMenuItem(this, R.drawable.settings, "设置");
		// 为每个菜单项创建点击事件
		itemWechat.setOnClickListener(this);
		itemAddressList.setOnClickListener(this);
		itemSettings.setOnClickListener(this);
		// 设置侧滑菜单是在左侧还是在右侧
		resideMenu.addMenuItem(itemWechat, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemAddressList, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);
		// 通过设置可以禁用方向 ->
		resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
		// 设置MainActivity上的左右菜单按钮的点击事件
		findViewById(R.id.title_bar_left_menu).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
					}
				});
		findViewById(R.id.add_friends).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, AddfriendsActivity.class);
						startActivity(intent);
					}
				});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return resideMenu.dispatchTouchEvent(ev);
	}

	// 设置点击事件逻辑
	@Override
	public void onClick(View view) {

		// 通过changeFragment()方法替换fragment
		if (view == itemWechat) {
			changeFragment(new WechatFragment());
		} else if (view == itemAddressList) {
			changeFragment(new AddressListFragment());
		} else if (view == itemSettings) {
			changeFragment(new SettingsFragment());
		}
		// 关闭菜单
		resideMenu.closeMenu();
	}

	private void changeFragment(Fragment targetFragment) {
		// 清除忽略视图列表
		resideMenu.clearIgnoredViewList();
		// 通过beginTransaction()方法即可开启并返回FragmentTransaction对象
		getSupportFragmentManager().beginTransaction()
				// replace()方法可以替换Fragment,从id-->targetFragment
				.replace(R.id.home_fragment, targetFragment, "fragment")
				.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit();
	}

	// 使用onKeyDown()方法监听菜单键和返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 监听返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 双击退出程序，调用一个退出程序类
			Exit myExit = new Exit(this);
			mExitTime = myExit.exit(mExitTime);
			return false;
		}
		// 为菜单按钮添加点击事件
		else if (keyCode == KeyEvent.KEYCODE_MENU) {
			resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public ResideMenu getResideMenu() {
		return resideMenu;
	}

}
