package bmob.wechat.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * 默认首选项管理类
 * @class  SharePreferenceUtil
 * @author Administrator
 *
 */
@SuppressLint("CommitPrefEdits")
public class SharePreferenceUtil {
	private SharedPreferences	preferences;
	private SharedPreferences.Editor editor;
	/**
	 * @param context 全局变量
	 * @param name    sharePreference文件名
	 */
	public SharePreferenceUtil(Context context,String name) {
		//实例化SharedPreferences，创建的文件只允许被此应用程序调用读写
		preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		//实例化SharedPreferences对应Editor
		editor = preferences.edit();
	}
	
	private  String KEY_NOTIFY = "switch_key_notify";
	private  String KEY_VOICE = "switch_key_voice";
	private  String KEY_VIBRATE = "switch_key_vibrate";
	/**
	 *	以下三个方法是初始化的时候被调用，缺省值全为true
	 *	isAllowPushNotify
	 *	isAllowVoice
	 *
	 */
	//允许接收消息,默认缺省值为true
	public boolean isAllowPushNotify(){
		return preferences.getBoolean(KEY_NOTIFY, true);
	}
	//允许声音
	public boolean isAllowVoice(){
		return preferences.getBoolean(KEY_VOICE, true);
	}
	//允许震动
	public boolean isAllowVibrate(){
		return preferences.getBoolean(KEY_VIBRATE, true);	
	}
	
	/**
	 * 根据传入的isChecked布尔变量设置
	 * setAllowPushNotify 设置是否允许消息
	 * setAllowVoice	     设置是否允许声音
	 * setAllowVibrate	     设置是否允许震动
	 */
	public void setAllowPushNotify(boolean isChecked){
		editor.putBoolean(KEY_NOTIFY, isChecked);
		editor.commit();
	}
	public void setAllowVoice(boolean isChecked){
		editor.putBoolean(KEY_VOICE, isChecked);
		editor.commit();
		
	}
	public void setAllowVibrate(boolean isChecked){
		
		editor.putBoolean(KEY_VIBRATE, isChecked);
		editor.commit();
	}
	
	
}
