package bmob.wechat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wechat.R;
/**
 * 修改昵称
 * @author Administrator
 *
 */
public class UpdateNickActivity extends Activity implements OnClickListener{
		EditText edit_nick;
		Button btn_tick,btn_back;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.update_nick);
			initView();
		}
		
		private void initView() {
			edit_nick = (EditText) findViewById(R.id.edit_nick);
			btn_tick = (Button) findViewById(R.id.title_bar_right_finish);
			btn_back = (Button) findViewById(R.id.title_bar_left_back);
			btn_tick.setOnClickListener(this);
			btn_back.setOnClickListener(this);
			

		}

		@Override
		public void onClick(View v) 
		{
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.title_bar_right_finish:
				String nickName = edit_nick.getText().toString();
				if (nickName.equals("")) {
					Toast.makeText(getApplicationContext(),
							"请填写昵称", Toast.LENGTH_SHORT).show();
				}
				else {
					Intent intent = new Intent(UpdateNickActivity.this,SetMyInfoActivity.class);
					intent.putExtra("nickName", nickName);
					startActivity(intent);
				
				}
				break;
			case R.id.title_bar_left_back:
				finish();
				break;

			default:
				break;
			}
		}
		
		/** 修改资料
		  * updateInfo
		  * @Title: updateInfo
		  * @return void
		  * @throws
		  */
		
		
//		private void updateInfo(String nick) 
//		{
//
//			final User user = userManager.getCurrentUser(User.class);
//			
//			user.setNick(nick);
//			user.update(this, new UpdateListener() {
//
//				@Override
//				public void onSuccess() {
//					// TODO Auto-generated method stub
//					final User u = userManager.getCurrentUser(User.class);
//					ShowToast("修改成功:"+u.getNick());
//					finish();
//				}
//
//				@Override
//				public void onFailure(int arg0, String arg1) {
//					// TODO Auto-generated method stub
//					ShowToast("onFailure:" + arg1);
//				}
//			});
//		}
		
}