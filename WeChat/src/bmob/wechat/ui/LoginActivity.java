package bmob.wechat.ui;

import bmob.wechat.residememu.ResideMenu;
import bmob.wechat.utils.Exit;

import com.wechat.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private Button login, register, forget;
	private EditText account, password;
	private Context context = LoginActivity.this;
	// 用于双击退出程序的判断时间变量
	private long mExitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		account = (EditText) findViewById(R.id.et_account);
		password = (EditText) findViewById(R.id.et_password);
		login = (Button) findViewById(R.id.bt_login);
		register = (Button) findViewById(R.id.bt_register);
		forget = (Button) findViewById(R.id.bt_forget);

		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String admin = account.getText().toString();
				String passwordstr = password.getText().toString();
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				// mishowview = new MIshowview();
				// task = new MyAsyncTaskUtils(LoginActivity.this, mishowview);
				// String[] str = { admin, passwordstr, HttpURL.Login };
				// task.execute(str);
			}
		});

		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
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
		// else if (keyCode == KeyEvent.KEYCODE_MENU) {
		// resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
		// }
		return true;
	}

}
