package bmob.wechat.ui;

import com.wechat.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

/**
 * 入口Activity
 * 
 * @author Administrator
 *
 */
public class EnterActivity extends Activity {
	// 记录已经打开应用的次数
	private String USED_TIMES = "used_times";
	Intent intent = null;
	SharedPreferences sp = null;
	SharedPreferences.Editor editor = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.enter_page);
		// 新建的SharedPreferences，文件名为：used_times，用于记录应用打开次数
		sp = getSharedPreferences("used_times", MODE_WORLD_READABLE);
		// 用SharedPreferences对象来读取其里面的数据：已打开次数，不存在的话，其默认值为0
		int used_count = sp.getInt(USED_TIMES, 0);
		// 若used_count为0,即首次安装，进入引导---->进入登陆界面
		if (used_count == 0) {
			System.out.println("first to use");
			intent = new Intent(EnterActivity.this, GuideActivity.class);
		} else {
			System.out.println("not first");
			// 否则，非首次安装，直接跳到主界面
			intent = new Intent(EnterActivity.this, LoginActivity.class);
		}
		editor = sp.edit();
		// 使用次数自加后，再用editor放入sharePreferences中
		editor.putInt(USED_TIMES, ++used_count);
		// 这个必须要提交，勿忘！！！！！
		editor.commit();

		/**
		 * 系统会为需要启动的activity寻找与当前activity不同的task; 这句话必须要有，因为我是在一个Activity的
		 * context以外调用了startActivity方法
		 */
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 新建1个线程，若干秒后启动此线程，跳转到登LoginActivity
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					// 当我们从1个Activity的context外去调用startActivity()的时候，前方必须要设置旗标Intent.FLAG_ACTIVITY_NEW_TASK
					getApplicationContext().startActivity(intent);
					// 跳转后结束当前的Activity
					finish();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

}