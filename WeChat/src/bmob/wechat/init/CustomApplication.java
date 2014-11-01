package bmob.wechat.init;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.wechat.R;

import bmob.wechat.utils.CollectionUtils;
import bmob.wechat.utils.SharePreferenceUtil;


import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;

/**
 * @ClassName: CustomApplcation
 * @Description:  自定义全局Applcation类
 * @author shuwei-csh
 */

/**
 * application是用来保存全局变量的，并且是在package创建的时候就跟着存在了。所以当我们需要创建全局变量的时候，不需
 * 要再像j2se那样需要创建public权限的static变量而直接在application中去实现
 * 。只需要调用Context的getApplicationContext
 * 或者Activity的getApplication方法来获得一个application对象，再做出相应的处理。
 */

/**
 * 启动Application时，系统会创建一个PID，即进程ID，所有的Activity都会在此进程上运行。
 * 那么我们在Application创建的时候初始化全局变量
 * ，同一个应用的所有Activity都可以取到这些全局变量的值，换句话说，我们在某一个Activity中改变了这些全局变量的值，
 * 那么在同一个应用的其他Activity中值就会改变。
 * 
 * Application对象的生命周期是整个程序中最长的，它的生命周期就等于这个程序的生命周期。因为它是全局的单例的，所以在不同的Activity,
 * Service中获得的对象都是同一个对象。所以可以通过Application来进行一些，如：数据传递、数据共享和数据缓存等操作。
 */
public class CustomApplication extends Application {
	public static CustomApplication mInstance;
	private MediaPlayer mMediaplayer;
	private NotificationManager mNotificationManager;
	private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();
	private SharePreferenceUtil mSpUtil;
	public static final String SHAREPREFERENCE_NAME = "_sharedinfo";

	public CustomApplication() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 是否开启debug模式--默认开启状态
		BmobChat.DEBUG_MODE = true;
		mInstance = this;
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		mMediaplayer = MediaPlayer.create(this, R.raw.notify);
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		// 我们经常会从网上加载大量的图片，处理不好，经常会出现内存溢出，导致app崩溃，还有下载速度慢登问题。ImageLoader基本避免了这些问题，下载速度快，
		// 基本不会出现内存泄漏，还有很好的缓存管理机制
		initImageLoader(getApplicationContext());
		// 若用户登陆过，则先从好友数据库中取出好友list存入内存中
		if (BmobUserManager.getInstance(getApplicationContext())
				.getCurrentUser() != null) {
			// 获取本地好友user list到内存,方便以后获取好友list
			// getContactList()，获取本地数据库中的好友列表：不包含黑名单用户
			contactList = CollectionUtils.list2map(BmobDB.create(
					getApplicationContext()).getContactList());
		}
	}

	public static CustomApplication getInstance() {
		return mInstance;
	}

	/**
	 * 初始化ImageLoader
	 * Android-Universal-Image-Loader是一个开源的UI组件程序，该项目的目的是提供一个可重复使用的仪器为异步图像加载
	 * ，缓存和显示
	 */
	private void initImageLoader(Context context) {
		// TODO Auto-generated method stub
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"bmobim/Cache");// 获取到缓存的目录地址
		// 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// 线程池内加载的数量
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}

	/** 单例模式，才能及时返回数据 */
	public synchronized SharePreferenceUtil getSpUtil() {
		if (mSpUtil == null) {
			String currentId = BmobUserManager.getInstance(
					getApplicationContext()).getCurrentUserObjectId();
			String sharedName = currentId + SHAREPREFERENCE_NAME;
			mSpUtil = new SharePreferenceUtil(this, sharedName);
		}
		return mSpUtil;
	}

	/**
	 * 单例模式，在别的类里面调用CustomApplication 用于获取同一个NotificationManager
	 */
	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}

	/**
	 * 单例模式，在别的类里面调用CustomApplication 用于获取同一个MediaPlayer
	 */
	public synchronized MediaPlayer getMediaPlayer() {
		if (mMediaplayer == null)
			mMediaplayer = MediaPlayer.create(this, R.raw.notify);
		return mMediaplayer;
	}

	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, BmobChatUser> getContactList() {
		return contactList;
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, BmobChatUser> contactList) {
		if (this.contactList != null) {
			this.contactList.clear();
		}
		this.contactList = contactList;
	}

	/**
	 * 退出登录,清空缓存数据
	 */
	public void logout() {
		// 退出登录
		BmobUserManager.getInstance(getApplicationContext()).logout();
		// 清空联系人列表缓存数据
		setContactList(null);
	}
}
