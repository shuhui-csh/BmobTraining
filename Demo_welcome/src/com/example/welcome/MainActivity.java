package com.example.welcome;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

//软件入口，闪屏界面。
public class MainActivity extends Activity {
	private boolean first;	//判断是否第一次打开软件
	private View view;
	private Context context;
	private Animation animation;
	private NetManager netManager;
	private SharedPreferences shared;
	private static int TIME = 1000; 										// 进入主程序的延迟时间

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = View.inflate(this, R.layout.activity_main, null);
		setContentView(view);
		context = this;							//得到上下文
		shared = new SharedConfig(context).GetConfig(); 	// 得到配置文件
		netManager = new NetManager(context); 				// 得到网络管理器
	}

	@Override
	protected void onResume() {
		into();
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}
	// 进入主程序的方法
	private void into() {
		if (netManager.isOpenNetwork()) {
			// 如果网络可用则判断是否第一次进入，如果是第一次则进入欢迎界面
			first = shared.getBoolean("First", true);

			// 设置动画效果是alpha，在anim目录下的alpha.xml文件中定义动画效果
			animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
			// 给view设置动画效果
			view.startAnimation(animation);
			animation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation arg0) {
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
				}

				// 这里监听动画结束的动作，在动画结束的时候开启一个线程，这个线程中绑定一个Handler,并
				// 在这个Handler中调用goHome方法，而通过postDelayed方法使这个方法延迟500毫秒执行，达到
				// 达到持续显示第一屏500毫秒的效果
				@Override
				public void onAnimationEnd(Animation arg0) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							Intent intent;
							//如果第一次，则进入引导页WelcomeActivity
							if (first) {
								intent = new Intent(MainActivity.this,
										WelcomeActivity.class);
							} else {
								intent = new Intent(MainActivity.this,
										HomeActivity.class);
							}
							startActivity(intent);
							// 设置Activity的切换效果
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
							MainActivity.this.finish();
						}
					}, TIME);
				}
			});
		} else {
			// 如果网络不可用，则弹出对话框，对网络进行设置
			Builder builder = new Builder(context);
			builder.setTitle("没有可用的网络");
			builder.setMessage("是否对网络进行设置?");
			builder.setPositiveButton("确定",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = null;
							try {
								String sdkVersion = android.os.Build.VERSION.SDK;
								if (Integer.valueOf(sdkVersion) > 10) {
									intent = new Intent(
											android.provider.Settings.ACTION_WIRELESS_SETTINGS);
								} else {
									intent = new Intent();
									ComponentName comp = new ComponentName(
											"com.android.settings",
											"com.android.settings.WirelessSettings");
									intent.setComponent(comp);
									intent.setAction("android.intent.action.VIEW");
								}
								MainActivity.this.startActivity(intent);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			builder.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							MainActivity.this.finish();
						}
					});
			builder.show();
		}
	}
}
