package bmob.wechat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import bmob.wechat.bean.User;

import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.listener.UpdateListener;

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
			switch (v.getId()){
				case R.id.title_bar_right_finish:
					//获取文本信息
					String nickName = edit_nick.getText().toString();
					//文本信息为空则再次提示填写昵称
					if (nickName.equals("")) {
						Toast.makeText(getApplicationContext(),
							"请填写昵称", Toast.LENGTH_SHORT).show();
					}
					else{
						//否则更新用户昵称
						UpdateNick(nickName);
					}
					break;
				case R.id.title_bar_left_back:
				finish();
				break;

			default:
				break;
			}
		}
		
		
		/**
		 * 更新昵称
		 * @param nickName 传入文本框信息
		 */
		private void UpdateNick(String nickName) {
			//用户管理类：所有和用户有关的操作均使用此类
			final BmobUserManager userManager = BmobUserManager.getInstance(this);
			//获取当前用户
			final User user = userManager.getCurrentUser(User.class);
			//设置用户名
			user.setNick(nickName);
			//调用方法更新User的数据
			user.update(this, new UpdateListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					final User u = userManager.getCurrentUser(User.class);
					Toast.makeText(getApplicationContext(), "新昵称:"
								+u.getNick(), Toast.LENGTH_SHORT).show();
					//更新成功立马销毁此Aty
					finish();
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), 
							"更新失败"+arg1,Toast.LENGTH_SHORT).show();
				}
			});
			
			
		}
		
	
		
}