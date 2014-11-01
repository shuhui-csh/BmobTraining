package bmob.wechat.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import bmob.wechat.ui.LoginActivity;
import bmob.wechat.ui.SetMyInfoActivity;
import bmob.wechat.utils.SharePreferenceUtil;

import com.wechat.R;
/**
 * 设置的主界面
 * @class  SettingFragment
 * @author Administrator
 *
 */

public class SettingsFragment extends Fragment implements OnClickListener{
	//个人资料，接收消息，声音，震动等四个列表项的父容器，均为相对布局.rl是相对布局的简称
		RelativeLayout rl_info,rl_switch_notification,
		rl_switch_voice,rl_switch_vibrate;
		//开，关状态按钮（其实是两图片），分别对应的是消息接收，声音，震动三个情况
		ImageView iv_open_notification, iv_close_notification,
				  iv_open_voice,iv_close_voice,
				  iv_open_vibrate,iv_close_vibrate;
		Button btn_logout;
		//管理首选项的SharePreference使用类
		 SharePreferenceUtil firstOptionPreferenceUtil;
		 //声音和震动底部的两条线
		 View lineBottomVoice,lineBottomVibrate;
		 //与Fragment相关的Activity的标题栏布局，标题栏信息框，右边的按钮
		 FrameLayout title_layout; TextView tv_title; Button btn_title_right; 
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			//CustomApplication类的实例mApplication来自于此类的父类FragmentBase
			//用CustomApplication类的方法实例化此工具类
//			firstOptionPreferenceUtil = mApplication.getSpUtil();
			firstOptionPreferenceUtil = new SharePreferenceUtil(getActivity(), "first_options");
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			View view =  inflater.inflate(R.layout.setting_fragment, container,false);
			//初始化各种组件
			initView(view);
			return view;
		}
		//对标题栏的一些设置
		@SuppressLint("ResourceAsColor")
		private void setHeadLayout(){
			//对与之相关的Activity带有的标题栏进行设置
			//标题栏信息框显示标题
			tv_title = (TextView) getActivity().findViewById(R.id.tv_title);
			tv_title.setText("设置");
			//右边按钮设置为隐藏
			btn_title_right	= (Button) getActivity().findViewById(R.id.add_friends);				
			btn_title_right.setVisibility(View.GONE);
			btn_title_right.setClickable(false);
			//获取头部的布局，并对其进行颜色设置
			title_layout = (FrameLayout) getActivity().findViewById(R.id.title_layout);
			
		}
		
		private void initView(View view) {
					//设置标题栏
					setHeadLayout();
					//个人资料列表项
					rl_info = (RelativeLayout) view.findViewById(R.id.rl_info);
					//接受新消息通知列表项
					rl_switch_notification = (RelativeLayout) view.findViewById(R.id.rl_switch_notification);
					//声音列表项
					rl_switch_voice = (RelativeLayout) view.findViewById(R.id.rl_switch_voice);
					//震动列表项
					rl_switch_vibrate = (RelativeLayout) view.findViewById(R.id.rl_switch_vibrate);
					//为个人资料，接受消息，声音，震动4个列表项设置监听器
					//为个人资料列表项设置监听
					rl_info.setOnClickListener(this);
					rl_switch_notification.setOnClickListener(this);
					rl_switch_voice.setOnClickListener(this);
					rl_switch_vibrate.setOnClickListener(this);
					//初始化所有列表项对应的图片等等
					iv_open_notification = (ImageView) view.findViewById(R.id.iv_open_notification);
					iv_close_notification = (ImageView) view.findViewById(R.id.iv_close_notification);
					iv_open_voice = (ImageView) view.findViewById(R.id.iv_open_voice);
					iv_close_voice = (ImageView) view.findViewById(R.id.iv_close_voice);
					iv_open_vibrate = (ImageView)view.findViewById(R.id.iv_open_vibrate);
					iv_close_vibrate = (ImageView)view.findViewById(R.id.iv_close_vibrate);
					btn_logout = (Button) view.findViewById(R.id.btn_logout);
					//为退出登陆按钮设置监听
					btn_logout.setOnClickListener(this);
					//初始化声音选项和震动选项的下面两条线
					lineBottomVoice = view.findViewById(R.id.line_voice);
					lineBottomVibrate = view.findViewById(R.id.line_vibrate);
					//一开始就是允许推送通知的，布尔值为true
					boolean isAllowNotify = firstOptionPreferenceUtil.isAllowPushNotify();
					//设置显示声音项和震动项
					if (isAllowNotify == true) {
						iv_open_notification.setVisibility(View.VISIBLE);
						iv_close_notification.setVisibility(View.INVISIBLE);
					} else {
						iv_open_notification.setVisibility(View.INVISIBLE);
						iv_close_notification.setVisibility(View.VISIBLE);
					}
					//一开始就是允许声音
					boolean isAllowVoice = firstOptionPreferenceUtil.isAllowVoice();
					//根据不同情况，设置隐藏或者显示哪个ImageView
					if (isAllowVoice == true) {
						iv_open_voice.setVisibility(View.VISIBLE);
						iv_close_voice.setVisibility(View.INVISIBLE);
					} else {
						iv_open_voice.setVisibility(View.INVISIBLE);
						iv_close_voice.setVisibility(View.VISIBLE);
					}
					//一开始就是允许震动
					boolean isAllowVibrate = firstOptionPreferenceUtil.isAllowVibrate();
					//设置隐藏或者显示
					if (isAllowVibrate == true) {
						iv_open_vibrate.setVisibility(View.VISIBLE);
						iv_close_vibrate.setVisibility(View.INVISIBLE);
					} else {
						iv_open_vibrate.setVisibility(View.INVISIBLE);
						iv_close_vibrate.setVisibility(View.VISIBLE);
					}
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.rl_info:
				// 前往个人资料页面
				Intent intent =new Intent(getActivity(),SetMyInfoActivity.class);
				//传递一个标识作用的数据过去,表示是“我”的个人资料
				intent.putExtra("from", "me");
				startActivity(intent);
				break;
				//如果被点击的是接收新消息的列表项
				case R.id.rl_switch_notification:
					//如果原来是可以接受新消息的，就收信息按钮是打开的
					if (iv_open_notification.getVisibility() == View.VISIBLE) 
					{
						//显示出   不可接收消息的ImageView，隐藏  可接收出新消息的imageview
						iv_open_notification.setVisibility(View.INVISIBLE);
						iv_close_notification.setVisibility(View.VISIBLE);
						//设置不允许消息推送
						firstOptionPreferenceUtil.setAllowPushNotify(false);
						//只要是这样，就连声音和震动的列表项都被隐藏
						rl_switch_vibrate.setVisibility(View.GONE);
						rl_switch_voice.setVisibility(View.GONE);
						//隐藏声音和震动连个列表项底下的那两条线
						lineBottomVoice.setVisibility(View.GONE);
						lineBottomVibrate.setVisibility(View.GONE);
					} 
					else 
					{
						//如果原来是不可以接收消息的
						//显示出 可接收消息的ImageView，隐藏 不可接收出新消息的imageview
						iv_open_notification.setVisibility(View.VISIBLE);
						iv_close_notification.setVisibility(View.INVISIBLE);
						//设置允许接收消息
						firstOptionPreferenceUtil.setAllowPushNotify(true);
						//声音震动两个列表项得以显示
						rl_switch_vibrate.setVisibility(View.VISIBLE);
						rl_switch_voice.setVisibility(View.VISIBLE);
						lineBottomVoice.setVisibility(View.VISIBLE);
						lineBottomVibrate.setVisibility(View.VISIBLE);
					}
					break;
					
			case R.id.rl_switch_voice:
				//当前声音栏按钮是打开的
				if (iv_open_voice.getVisibility() == View.VISIBLE)
				{
					//隐藏on按钮状态
					iv_open_voice.setVisibility(View.INVISIBLE);
					//显示off按钮状态
					iv_close_voice.setVisibility(View.VISIBLE);
					//设置首选项管理器实例中的KEY_VOICE为false
					firstOptionPreferenceUtil.setAllowVoice(false);
				}
				else{//当前声音栏是关闭的
					//隐藏off按钮状态，显示on按钮状态
					iv_open_voice.setVisibility(View.VISIBLE);
					iv_close_voice.setVisibility(View.INVISIBLE);
					firstOptionPreferenceUtil.setAllowVoice(true);
				}
				break;
			case R.id.rl_switch_vibrate:
				
				if (iv_open_vibrate.getVisibility() == View.VISIBLE) {
					iv_open_vibrate.setVisibility(View.INVISIBLE);
					iv_close_vibrate.setVisibility(View.VISIBLE);
					firstOptionPreferenceUtil.setAllowVibrate(false);
				} else {
					iv_open_vibrate.setVisibility(View.VISIBLE);
					iv_close_vibrate.setVisibility(View.INVISIBLE);
					firstOptionPreferenceUtil.setAllowVibrate(true);
				}
			case R.id.btn_logout:
				//退出登陆界面
				startActivity(new Intent(getActivity(), LoginActivity.class));
				//销毁所在Aty，销毁此Fragment
				getActivity().finish();
				break;
			default:
				break;
			}
		}
		
		@Override
		public void onResume() {
		 /**
		  * 设置为横屏
		  */
		 if(getActivity().getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			 getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		 }
		 super.onResume();
		}
		
		@Override
		public void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
		}

}
