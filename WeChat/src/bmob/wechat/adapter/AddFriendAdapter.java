package bmob.wechat.adapter;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import bmob.wechat.adapter.base.BaseListAdapter;
import bmob.wechat.adapter.base.ViewHolder;
import bmob.wechat.utils.ImageLoadOptions;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.PushListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wechat.R;

/**查找好友
  * @ClassName: AddFriendAdapter
  * @Description: TODO
  * @author smile
  * @date 2014-6-25 上午10:56:33
  */
public class AddFriendAdapter extends BaseListAdapter<BmobChatUser> {

	public AddFriendAdapter(Context context, List<BmobChatUser> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View bindView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			//在查找到好友界面，列表是，一个头像，中间名字，右边一个“添加”按钮
			convertView = mInflater.inflate(R.layout.item_add_friend, null);
		}
		//获取列表中某个用户
		final BmobChatUser contract = getList().get(arg0);
		//根据id获得实例化组建，只绑定一次
		TextView name = ViewHolder.get(convertView, R.id.name);
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.avatar);
		Button btn_add = ViewHolder.get(convertView, R.id.btn_add);
		//不明所以的获取BmobChatUser.getAvatar，！！！不知道是什么东西！！！应该是判断是否有自己上传头像
		String avatar = contract.getAvatar();
		//如果头像不为空，则设置通过网络加载头像，如果为空，则设置默认头像
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
		} else {
			iv_avatar.setImageResource(R.drawable.default_head);
		}
		//获取并设置用户的名字
		name.setText(contract.getUsername());
		btn_add.setText("添加");
		btn_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final ProgressDialog progress = new ProgressDialog(mContext);
				//这里是一个ProgressDialog转圈，并有"正在添加..."，没有其他东西
				progress.setMessage("正在添加...");
				progress.setCanceledOnTouchOutside(false);//设置单击外圈不可取消
				progress.show();
				//发送tag请求，底层方法不可见
				BmobChatManager.getInstance(mContext).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, contract.getObjectId(),new PushListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证!");
					}
					
					@Override
					public void onFailure(int arg0, final String arg1) {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求失败，请重新添加!");
						ShowLog("发送请求失败:"+arg1);
					}
				});
			}
		});
		return convertView;
	}

}
