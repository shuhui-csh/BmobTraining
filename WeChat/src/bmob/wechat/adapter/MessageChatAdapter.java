package bmob.wechat.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import bmob.wechat.adapter.base.BaseListAdapter;
import bmob.wechat.adapter.base.ViewHolder;
import bmob.wechat.ui.ImageBrowserActivity;
import bmob.wechat.ui.SetMyInfoActivity;
import bmob.wechat.utils.FaceTextUtils;
import bmob.wechat.utils.ImageLoadOptions;
import bmob.wechat.utils.TimeUtil;
import cn.bmob.im.BmobDownloadManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.inteface.DownloadListener;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wechat.R;


/** 聊天适配器
  * @ClassName: MessageChatAdapter
  * @Description: TODO
  * @author smile
  * @date 2014-5-28 下午5:34:07
  */
public class MessageChatAdapter extends BaseListAdapter<BmobMsg> {

	//8种Item的类型
	//接受与发送文本
	private final int TYPE_RECEIVER_TXT = 0;
	private final int TYPE_SEND_TXT = 1;
	//图片
	private final int TYPE_SEND_IMAGE = 2;
	private final int TYPE_RECEIVER_IMAGE = 3;
	//位置
	private final int TYPE_SEND_LOCATION = 4;
	private final int TYPE_RECEIVER_LOCATION = 5;
	//语音
	private final int TYPE_SEND_VOICE =6;
	private final int TYPE_RECEIVER_VOICE = 7;
	
	String currentObjectId = "";

	DisplayImageOptions options;
	//ImageLoadingListener是加载图片的接口，里面有开始加载，加载失败，加载完成，跟加载取消四个方法
	//新建的对象是只有加载图片完成时的图片进入效果
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	//设置聊天适配的Adapter，DisplayImageOptions，图片加载选项
	public MessageChatAdapter(Context context,List<BmobMsg> msgList) {
		// TODO Auto-generated constructor stub
		super(context, msgList);
		//获取当前目标的id，格式是String
		currentObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();
		//包含图片加载显示时的各种选项，new对象时，不仅新建对象，还设置由此产生的位图将分配像素
		//他们可以被清除，如果系统需要回收内存。当像素需要再次被访问,他们将自动重新解码;图片可被拷贝
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.ic_launcher)//图片为空时的默认图片
		.showImageOnFail(R.drawable.ic_launcher)//加载失败时显示的图片	
		.resetViewBeforeLoading(true)//当图片开始加载之前，图片将设为空
		.cacheOnDisc(true)//加载的图片将缓存到磁盘上
		.cacheInMemory(true)//加载的图片将缓存到内存上
		.imageScaleType(ImageScaleType.EXACTLY)//图像会按比例缩小的确切目标尺寸
		.bitmapConfig(Bitmap.Config.RGB_565)//图像颜色
		.considerExifParams(true)//设置图像加载是否会考虑JPEG图像EXIF参数（旋转，翻转）
		.displayer(new FadeInBitmapDisplayer(300))//图像将显示，动画时长为0.3s
		.build();
	}
	/**
	 * 判断聊天的类型，图像，位置，声音，文字，且判断发送还是接受
	 * */
	@Override
	public int getItemViewType(int position) {
		BmobMsg msg = list.get(position);
		if(msg.getMsgType()==BmobConfig.TYPE_IMAGE){
			return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_IMAGE: TYPE_RECEIVER_IMAGE;
		}else if(msg.getMsgType()==BmobConfig.TYPE_VOICE){
			return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_VOICE: TYPE_RECEIVER_VOICE;
		}else{
		    return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT: TYPE_RECEIVER_TXT;
		}
	}
	/**
	 * 对话两天类型一共6种
	 * */
	@Override
	public int getViewTypeCount() {
		return 6;
	}
	/**
	 * 根据BmobMsg获取对话聊天类型和从position获取位置，然后获得视图View；
	 *每个视图都对应一个 xml
	 * */
	private View createViewByType(BmobMsg message, int position) {
		int type = message.getMsgType();
	   if(type==BmobConfig.TYPE_IMAGE){
			return getItemViewType(position) == TYPE_RECEIVER_IMAGE ? 
					mInflater.inflate(R.layout.item_chat_received_image, null) 
					:
					mInflater.inflate(R.layout.item_chat_sent_image, null);
		}else if(type==BmobConfig.TYPE_VOICE){//语音类型
			return getItemViewType(position) == TYPE_RECEIVER_VOICE ? 
					mInflater.inflate(R.layout.item_chat_received_voice, null) 
					:
					mInflater.inflate(R.layout.item_chat_sent_voice, null);
		}else{//剩下默认的都是文本
			return getItemViewType(position) == TYPE_RECEIVER_TXT ? 
					mInflater.inflate(R.layout.item_chat_received_message, null) 
					:
					mInflater.inflate(R.layout.item_chat_sent_message, null);
		}
	}

	@Override
	public View bindView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final BmobMsg item = list.get(position);
		//获取聊天单种类型视图
		if (convertView == null) {
			convertView = createViewByType(item, position);
		}
		//实例化各种组件
		//文本类型
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
		final ImageView iv_fail_resend = ViewHolder.get(convertView, R.id.iv_fail_resend);//失败重发
		final TextView tv_send_status = ViewHolder.get(convertView, R.id.tv_send_status);//发送状态
		TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
		TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);
		//图片
		ImageView iv_picture = ViewHolder.get(convertView, R.id.iv_picture);
		final ProgressBar progress_load = ViewHolder.get(convertView, R.id.progress_load);//进度条
		//语音
		final ImageView iv_voice = ViewHolder.get(convertView, R.id.iv_voice);
		//语音长度
		final TextView tv_voice_length = ViewHolder.get(convertView, R.id.tv_voice_length);
		
		//点击头像进入个人资料
		String avatar = item.getBelongAvatar();
		if(avatar!=null && !avatar.equals("")){//加载头像-为了不每次都加载头像
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions(),animateFirstListener);
		}else{
			iv_avatar.setImageResource(R.drawable.head);
		}
		
		iv_avatar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//在聊天窗口，单击头像，弹开个人信息，跳转Activity内包括区别他人和自己的信息，如果是他人，则还有他人的用户名
				Intent intent =new Intent(mContext,SetMyInfoActivity.class);
				if(getItemViewType(position) == TYPE_RECEIVER_TXT 
						||getItemViewType(position) == TYPE_RECEIVER_IMAGE
				        ||getItemViewType(position)==TYPE_RECEIVER_LOCATION
				        ||getItemViewType(position)==TYPE_RECEIVER_VOICE){
					intent.putExtra("from", "other");
					intent.putExtra("username", item.getBelongUsername());
				}else{
					intent.putExtra("from", "me");
				}
				mContext.startActivity(intent);
			}
		});
		
		tv_time.setText(TimeUtil.getChatTime(Long.parseLong(item.getMsgTime())));
		//主要是关于聊天过程中视图能见度的处理
		if(getItemViewType(position)==TYPE_SEND_TXT
//				||getItemViewType(position)==TYPE_SEND_IMAGE//图片单独处理
				||getItemViewType(position)==TYPE_SEND_LOCATION
				||getItemViewType(position)==TYPE_SEND_VOICE){//只有自己发送的消息才有重发机制
			//状态描述
			if(item.getStatus()==BmobConfig.STATUS_SEND_SUCCESS){//发送成功
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				if(item.getMsgType()==BmobConfig.TYPE_VOICE){
					tv_send_status.setVisibility(View.GONE);
					tv_voice_length.setVisibility(View.VISIBLE);
				}else{
					tv_send_status.setVisibility(View.VISIBLE);
					tv_send_status.setText("已发送");
				}
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_FAIL){//服务器无响应或者查询失败等原因造成的发送失败，均需要重发
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.VISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
				if(item.getMsgType()==BmobConfig.TYPE_VOICE){
					tv_voice_length.setVisibility(View.GONE);
				}
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_RECEIVERED){//对方已接收到
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				if(item.getMsgType()==BmobConfig.TYPE_VOICE){
					tv_send_status.setVisibility(View.GONE);
					tv_voice_length.setVisibility(View.VISIBLE);
				}else{
					tv_send_status.setVisibility(View.VISIBLE);
					tv_send_status.setText("已阅读");
				}
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_START){//开始上传
				progress_load.setVisibility(View.VISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
				if(item.getMsgType()==BmobConfig.TYPE_VOICE){
					tv_voice_length.setVisibility(View.GONE);
				}
			}
		}
		//根据类型为聊天填充内容显示内容
		final String text = item.getContent();
		switch (item.getMsgType()) {
		case BmobConfig.TYPE_TEXT://设置文字内容
			try {
				SpannableString spannableString = FaceTextUtils
						.toSpannableString(mContext, text);
				tv_message.setText(spannableString);
			} catch (Exception e) {
			}
			break;

		case BmobConfig.TYPE_IMAGE://图片类
			try {
				//text应该是是有关于获取类型的，虽然不明白为什么要判断两次
				if (text != null && !text.equals("")) {//发送成功之后存储的图片类型的content和接收到的是不一样的
					//处理发送图片和接受过程各种视图的可见与不可见，还有就是 对话中 图片的缩略图都是取本地的数据，传送的参数是各个视图组件
					dealWithImage(position, progress_load, iv_fail_resend, tv_send_status, iv_picture, item);
				}
				iv_picture.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						//单击图片会出现放大，且可以左右滑动查看前后张图片
						Intent intent =new Intent(mContext,ImageBrowserActivity.class);
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(getImageUrl(item));
						intent.putStringArrayListExtra("photos", photos);
						intent.putExtra("position", 0);
						mContext.startActivity(intent);
					}
				});
				
			} catch (Exception e) {
			}
			break;
		case BmobConfig.TYPE_VOICE://语音消息
			try {
				if (text != null && !text.equals("")) {
					tv_voice_length.setVisibility(View.VISIBLE);
					String content = item.getContent();
					if (item.getBelongId().equals(currentObjectId)) {//发送的消息
						if(item.getStatus()==BmobConfig.STATUS_SEND_RECEIVERED
								||item.getStatus()==BmobConfig.STATUS_SEND_SUCCESS){//当发送成功或者发送已阅读的时候，则显示语音长度
							tv_voice_length.setVisibility(View.VISIBLE);
							String length = content.split("&")[2];
							tv_voice_length.setText(length+"\''");
						}else{
							tv_voice_length.setVisibility(View.INVISIBLE);
						}
					} else {//收到的消息
						boolean isExists = BmobDownloadManager.checkTargetPathExist(currentObjectId,item);
						if(!isExists){//若指定格式的录音文件不存在，则需要下载，因为其文件比较小，故放在此下载
							String netUrl = content.split("&")[0];
							final String length = content.split("&")[1];
							BmobDownloadManager downloadTask = new BmobDownloadManager(mContext,item,new DownloadListener() {
								
								@Override
								public void onStart() {
									// TODO Auto-generated method stub
									progress_load.setVisibility(View.VISIBLE);
									tv_voice_length.setVisibility(View.GONE);
									iv_voice.setVisibility(View.INVISIBLE);//只有下载完成才显示播放的按钮
								}
								
								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									progress_load.setVisibility(View.GONE);
									tv_voice_length.setVisibility(View.VISIBLE);
									tv_voice_length.setText(length+"\''");
									iv_voice.setVisibility(View.VISIBLE);
								}
								@Override
								public void onError(String error) {
									// TODO Auto-generated method stub
									progress_load.setVisibility(View.GONE);
									tv_voice_length.setVisibility(View.GONE);
									iv_voice.setVisibility(View.INVISIBLE);
								}
							});
							downloadTask.execute(netUrl);
						}else{
							String length = content.split("&")[2];
							tv_voice_length.setText(length+"\''");
						}
					}
				}
				//播放语音文件
				iv_voice.setOnClickListener(new NewRecordPlayClickListener(mContext,item,iv_voice));
			} catch (Exception e) {
				
			}
			
			break;
		default:
			break;
		}
		return convertView;
	}
	
	/** 获取图片的地址--
	  * @Description: TODO
	  * @param @param item
	  * @param @return 
	  * @return String
	  * @throws
	  */
	private String getImageUrl(BmobMsg item){
		String showUrl = "";
		String text = item.getContent();
		//本地地址+"&"+网络地址，如果有&就获取本地地址 没有就直接赋值
		if(item.getBelongId().equals(currentObjectId)){
			if(text.contains("&")){
				showUrl = text.split("&")[0];
			}else{
				showUrl = text;
			}
		}else{//如果是收到的消息，则需要从网络下载
			showUrl = text;
		}
		return showUrl;
	}
	
	
	/** 
	 * 处理发送图片和接受过程各种视图的可见与不可见，还有就是 对话中 图片的缩略图都是取本地的数据
	  * @Description: TODO
	  * @param @param position
	  * @param @param progress_load
	  * @param @param iv_fail_resend
	  * @param @param tv_send_status
	  * @param @param iv_picture
	  * @param @param item 
	  * @return void
	  * @throws
	  */
	private void dealWithImage(int position,final ProgressBar progress_load,ImageView iv_fail_resend,TextView tv_send_status,ImageView iv_picture,BmobMsg item){
		String text = item.getContent();
		if(getItemViewType(position)==TYPE_SEND_IMAGE){//发送的图片消息
			if(item.getStatus()==BmobConfig.STATUS_SEND_START){//开始发送图片消息
				progress_load.setVisibility(View.VISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_SUCCESS){
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("已发送");
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_FAIL){
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.VISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_RECEIVERED){
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("已阅读");
			}
//			如果是发送的图片的话，因为开始发送存储的地址是本地地址，发送成功之后存储的是本地地址+"&"+网络地址，因此需要判断下
			String showUrl = "";
			if(text.contains("&")){
				showUrl = text.split("&")[0];
			}else{
				showUrl = text;
			}
			//为了方便每次都是取本地图片显示，在对话中
			ImageLoader.getInstance().displayImage(showUrl, iv_picture);
		}else{
			//应该是接受图片的时候的各个组件的显示情况
			ImageLoader.getInstance().displayImage(text, iv_picture,options,new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					// TODO Auto-generated method stub
					progress_load.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					// TODO Auto-generated method stub
					progress_load.setVisibility(View.INVISIBLE);
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					// TODO Auto-generated method stub
					progress_load.setVisibility(View.INVISIBLE);
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					// TODO Auto-generated method stub
					progress_load.setVisibility(View.INVISIBLE);
				}
			});
		}
	}
	
	//SimpleImageLoadingListener是实现ImageLoadingListener接口的一个类
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		//new的对象不知道是什么
		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
		//四种方法只有加载图片完成时候的方法被子类复写
		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				//测试这个清单是否包含指定的对象
				boolean firstDisplay = !displayedImages.contains(imageUri);
				//没有包含对象，就加载图像
				if (firstDisplay) {
					//进入动画 。0.5s；然后再把不存在于displayedImages的地址加入进去
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
	
}
