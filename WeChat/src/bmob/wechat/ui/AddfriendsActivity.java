package bmob.wechat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class AddfriendsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

}
