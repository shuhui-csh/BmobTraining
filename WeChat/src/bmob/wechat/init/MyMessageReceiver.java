package bmob.wechat.init;

import java.util.ArrayList;


import java.util.List;
import org.json.JSONObject;

import com.wechat.R;

import bmob.wechat.ui.MainActivity;
import bmob.wechat.ui.NewFriendActivity;
import bmob.wechat.utils.CollectionUtils;
import bmob.wechat.utils.CommonUtils;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * @ClassName: MyMessageReceiver
 * @Description: 通知栏上的通知处理，包括添加好友通知，有新消息通知
 * @author shuwei-csh
 */
public class MyMessageReceiver extends BroadcastReceiver {

	// 事件监听动作列表
	public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();
	public static final int NOTIFY_ID = 0x000;
	public static int mNewNum = 0;
	private BmobUserManager userManager;
	private BmobChatUser currentUser;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String json = intent.getStringExtra("msg");
		BmobLog.i("收到的message = " + json);
		userManager = BmobUserManager.getInstance(context);
		currentUser = userManager.getCurrentUser();
		// 检查网络是否可用
		boolean isNetConnected = CommonUtils.isNetworkAvailable(context);
		if (isNetConnected) {
			// 解析通知返回来的json
			parseMessage(context, json);
		} else {
			// 如果网络不可用，调用Bmob封装好的onNetChange改变网络状态
			for (int i = 0; i < ehList.size(); i++)
				((EventListener) ehList.get(i)).onNetChange(isNetConnected);
		}
	}

	/**
	 * @Title: parseMessage
	 * @Description: 解析通知栏上的Json字符串，并作相应的处理
	 * @param context
	 * @param json
	 * @return void
	 * @throws
	 */
	private void parseMessage(final Context context, String json) {
		// TODO Auto-generated method stub

		JSONObject jo;
		try {
			jo = new JSONObject(json);
			String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
			// TAG_OFFLINE 标签消息种类:同意添加好友
			if (tag.equals(BmobConfig.TAG_OFFLINE)) {
				// 下线通知
				if (currentUser != null) {
					if (ehList.size() > 0) {
						// 有监听的时候，传递下去
						for (EventListener handler : ehList)
							handler.onOffline();
					} else {
						// 清空数据
						CustomApplication.getInstance().logout();
					}
				}
			} else {
				// BmobConstant.PUSH_KEY_TARGETID：聊天消息的相关json字段:标示该消息来源用户objectid
				String fromId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TARGETID);
				// 增加消息接收方的ObjectId--目的是解决多账户登陆同一设备时，无法接收到非当前登陆用户的消息。
				final String toId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TOID);
				String msgTime = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_READED_MSGTIME);
				// 该消息发送方不为黑名单用户
				if (fromId != null
						&& !BmobDB.create(context, toId).isBlackUser(fromId)) {
					// 不携带tag标签--此可接收陌生人的消息
					if (TextUtils.isEmpty(tag)) {
						// createReceiveMsg:创建收到的消息
						BmobChatManager.getInstance(context).createReceiveMsg(
								json, new OnReceiveListener() {

									@Override
									public void onSuccess(BmobMsg msg) {
										// TODO Auto-generated method stub
										if (ehList.size() > 0) {// 有监听的时候，传递下去
											for (int i = 0; i < ehList.size(); i++) {
												((EventListener) ehList.get(i))
														.onMessage(msg);
											}
										} else {
											boolean isAllow = CustomApplication
													.getInstance().getSpUtil()
													.isAllowPushNotify();
											if (isAllow
													&& currentUser != null
													&& currentUser
															.getObjectId()
															.equals(toId)) {// 当前登陆用户存在并且也等于接收方id
												mNewNum++;
												showMsgNotify(context, msg);
											}
										}
									}

									@Override
									public void onFailure(int code, String arg1) {
										// TODO Auto-generated method stub
										BmobLog.i("获取接收的消息失败：" + arg1);
									}
								});

					} else {
						// 带tag标签
						if (tag.equals(BmobConfig.TAG_ADD_CONTACT)) {
							// 保存好友请求到本地，并更新后台的未读字段
							BmobInvitation message = BmobChatManager
									.getInstance(context).saveReceiveInvite(
											json, toId);
							if (currentUser != null) {// 有登陆用户
								if (toId.equals(currentUser.getObjectId())) {
									if (ehList.size() > 0) {// 有监听的时候，传递下去
										for (EventListener handler : ehList)
											handler.onAddUser(message);
									} else {
										showOtherNotify(context,
												message.getFromname(), toId,
												message.getFromname()
														+ "请求添加好友",
												NewFriendActivity.class);
									}
								}
							}
						} else if (tag.equals(BmobConfig.TAG_ADD_AGREE)) {
							String username = BmobJsonUtil.getString(jo,
									BmobConstant.PUSH_KEY_TARGETUSERNAME);
							// 收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
							BmobUserManager.getInstance(context)
									.addContactAfterAgree(username,
											new FindListener<BmobChatUser>() {

												@Override
												public void onError(int arg0,
														final String arg1) {
													// TODO Auto-generated
													// method stub

												}

												@Override
												public void onSuccess(
														List<BmobChatUser> arg0) {
													// TODO Auto-generated
													// method stub
													// 保存到内存中
													CustomApplication
															.getInstance()
															.setContactList(
																	CollectionUtils
																			.list2map(BmobDB
																					.create(context)
																					.getContactList()));
												}
											});
							// 显示通知
							showOtherNotify(context, username, toId, username
									+ "同意添加您为好友", MainActivity.class);
							// 创建一个临时验证会话--用于在会话界面形成初始会话
							BmobMsg.createAndSaveRecentAfterAgree(context, json);

						} else if (tag.equals(BmobConfig.TAG_READED)) {// 已读回执
							String conversionId = BmobJsonUtil.getString(jo,
									BmobConstant.PUSH_READED_CONVERSIONID);
							if (currentUser != null) {
								// 更改某条消息的状态
								BmobChatManager.getInstance(context)
										.updateMsgStatus(conversionId, msgTime);
								if (toId.equals(currentUser.getObjectId())) {
									if (ehList.size() > 0) {// 有监听的时候，传递下去--便于修改界面
										for (EventListener handler : ehList)
											handler.onReaded(conversionId,
													msgTime);
									}
								}
							}
						}
					}
				} else {// 在黑名单期间所有的消息都应该置为已读，不然等取消黑名单之后又可以查询的到
					BmobChatManager.getInstance(context).updateMsgReaded(true,
							fromId, msgTime);
					BmobLog.i("该消息发送方为黑名单用户");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			// 这里截取到的有可能是web后台推送给客户端的消息，也有可能是开发者自定义发送的消息，需要开发者自行解析和处理
			BmobLog.i("parseMessage错误：" + e.getMessage());
		}

	}

	/**
	 * 显示与聊天消息的通知
	 * 
	 * @Title: showNotify
	 * @return void
	 * @throws
	 */
	public void showMsgNotify(Context context, BmobMsg msg) {
		// 更新通知栏
		int icon = R.drawable.ic_launcher;
		String trueMsg = "";
		if (msg.getMsgType() == BmobConfig.TYPE_TEXT
				&& msg.getContent().contains("\\ue")) {
			trueMsg = "[表情]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
			trueMsg = "[图片]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
			trueMsg = "[语音]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
			trueMsg = "[位置]";
		} else {
			trueMsg = msg.getContent();
		}
		CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
		String contentTitle = msg.getBelongUsername() + " (" + mNewNum
				+ "条新消息)";

		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		boolean isAllowVoice = CustomApplication.getInstance().getSpUtil()
				.isAllowVoice();
		boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil()
				.isAllowVibrate();

		BmobNotifyManager.getInstance(context).showNotifyWithExtras(
				isAllowVoice, isAllowVibrate, icon, tickerText.toString(),
				contentTitle, tickerText.toString(), intent);
	}

	/**
	 * 显示其他Tag的通知 showOtherNotify
	 */
	public void showOtherNotify(Context context, String username, String toId,
			String ticker, Class<?> cls) {
		boolean isAllow = CustomApplication.getInstance().getSpUtil()
				.isAllowPushNotify();
		boolean isAllowVoice = CustomApplication.getInstance().getSpUtil()
				.isAllowVoice();
		boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil()
				.isAllowVibrate();
		if (isAllow && currentUser != null
				&& currentUser.getObjectId().equals(toId)) {
			// 同时提醒通知
			// public void showNotify(boolean isAllowVoice,
			// boolean isAllowVirbate,
			// int icon,
			// java.lang.String tickerText,
			// java.lang.String contentTitle,
			// java.lang.String contentText,
			// java.lang.Class<?> targetClass)
			// 创建显示通知栏
			// 参数：
			// @param - icon:通知栏的图标
			// @param - tickerText：状态栏提示语
			// @param - contentTitle：通知标题
			// @param - contentText：通知内容
			// @param - targetClass ：点击之后进入的Class
			BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,
					isAllowVibrate, R.drawable.ic_launcher, ticker, username,
					ticker.toString(), NewFriendActivity.class);
		}
	}

}
