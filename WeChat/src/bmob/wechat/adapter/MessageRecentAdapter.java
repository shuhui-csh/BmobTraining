package bmob.wechat.adapter;

import java.util.List;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import bmob.wechat.adapter.base.ViewHolder;
import bmob.wechat.utils.FaceTextUtils;
import bmob.wechat.utils.ImageLoadOptions;
import bmob.wechat.utils.TimeUtil;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.wechat.R;

/** 会话适配器，会自动匹配输入内容更改，应该是在现实好友列表，查看最后一句并且看是否已读
  * @ClassName: MessageRecentAdapter
  * @Description: TODO
  * @author smile
  * @date 2014-6-7 下午2:34:10
  */
public class MessageRecentAdapter extends ArrayAdapter<BmobRecent> implements Filterable{
	//AutoCompleteTextview的核心就是 Filterable
	//必须实现的两个重要方法
	//protected FilterResults  performFiltering(CharSequence prefix) 字符前缀 执行过滤方法
	//protected void publishResults(CharSequence constraint, 发布筛选过后得到的数据同时更新Adapter
	private LayoutInflater inflater; //放置页面
	private List<BmobRecent> mData;//老数据
	private Context mContext;
	
	public MessageRecentAdapter(Context context, int textViewResourceId, List<BmobRecent> objects) {
		super(context, textViewResourceId, objects);
		inflater = LayoutInflater.from(context);
		this.mContext = context;
		mData = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//获取实例
		final BmobRecent item = mData.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_conversation, parent, false);
		}
		ImageView iv_recent_avatar = ViewHolder.get(convertView, R.id.iv_recent_avatar);
		TextView tv_recent_name = ViewHolder.get(convertView, R.id.tv_recent_name);
		TextView tv_recent_msg = ViewHolder.get(convertView, R.id.tv_recent_msg);
		TextView tv_recent_time = ViewHolder.get(convertView, R.id.tv_recent_time);
		TextView tv_recent_unread = ViewHolder.get(convertView, R.id.tv_recent_unread);
		
		//这个是从数据mData获取的图像的url
		String avatar = item.getAvatar();
		//填充头像数据
		if(avatar!=null&& !avatar.equals("")){
			//单个图像的加载和显示，参数是 图像URI；显示的imageview组件；选择图像解码和显示选项，如果为空，默认显示图像选项的配置将使用；
			ImageLoader.getInstance().displayImage(avatar, iv_recent_avatar, ImageLoadOptions.getOptions());//
		}else{
			//如果图像地址为空，则设置默认头像
			iv_recent_avatar.setImageResource(R.drawable.head);
		}
		//从数据mData获取用户名和最后发送时间
		tv_recent_name.setText(item.getUserName());
		tv_recent_time.setText(TimeUtil.getChatTime(item.getTime()));
		//根据类型装载内容显示内容。我只是奇怪为什么能加载所有的，又没有循环
		if(item.getType()==BmobConfig.TYPE_TEXT){
			SpannableString spannableString = FaceTextUtils.toSpannableString(mContext, item.getMessage());
			tv_recent_msg.setText(spannableString);
		}else if(item.getType()==BmobConfig.TYPE_IMAGE){
			tv_recent_msg.setText("[图片]");
		}else if(item.getType()==BmobConfig.TYPE_VOICE){
			tv_recent_msg.setText("[语音]");
		}
		//获取未读数据的条数，有则显示，没有则设置红色未读tv不可见
		int num = BmobDB.create(mContext).getUnreadCount(item.getTargetid());
		if (num > 0) {
			tv_recent_unread.setVisibility(View.VISIBLE);
			tv_recent_unread.setText(num + "");
		} else {
			tv_recent_unread.setVisibility(View.GONE);
		}
		return convertView;
	}

}
