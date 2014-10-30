package bmob.wechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class Exit {

	private Context context;

	public Exit(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	// ---双击退出程序---
	public Long exit(Long mExitTime) {

		if ((System.currentTimeMillis() - mExitTime) > 2000) {
			Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			mExitTime = System.currentTimeMillis();// 重新赋予现在的系统时间
			return mExitTime;
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			((Activity) context).finish();
			System.exit(0);
			return (long) 0;
			// 这句代码也可以结束整个程序。
			// android.os.Process.killProcess(android.os.Process.myPid());
		}

	}

	// 这是为匿名内部里面的startActivity重写的一个方法，因为这个方法只能被Activity调用
	private void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		context.startActivity(intent);
	}
}