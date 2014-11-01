package bmob.wechat.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import bmob.wechat.bean.LocalUser;

import com.wechat.R;
/**
 * 以下两种情况会调用此类：
 * 1.设置我的个人资料
 * 2.查找好友成功后的显示结果
 * @author Administrator
 *
 */
public class SetMyInfoActivity extends Activity implements OnClickListener{
	Button btn_back,btn_chat;
	RelativeLayout rl_headLayout,rl_nickLayout,rl_accountLayout,rl_sexLayout;
	FrameLayout backFrameLayout;
	//三个显示用户昵称，账号，性别的TextView
	TextView tv_nick,tv_account,tv_sex,tv_title;
	ImageView iv_head,iv_head_arrow;
	String from ="";
	String nickName = ""; 
	
	@Override
	protected void onResume() {
		 /**
		  * 设置为横屏
		  */
		 if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		 }
		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 因为魅族手机下面有三个虚拟的导航按钮，需要将其隐藏掉，不然会遮掉拍照和相册两个按钮，且在setContentView之前调用才能生效
//		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//		if (currentapiVersion >= 14) 
//		{
//			getWindow().getDecorView().setSystemUiVisibility(
//			View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//		}
		setContentView(R.layout.activity_set_info);
		//获取传进此Activity的标识
		from = getIntent().getStringExtra("from");
		//获取传入此Activity的昵称
		nickName = getIntent().getStringExtra("nickName");
		initView();
	}
	private void initView() 
	{
		//初始化所有组件
		backFrameLayout = (FrameLayout) findViewById(R.id.back_layout);
		btn_back = (Button) findViewById(R.id.back_btn);
		btn_chat = (Button) findViewById(R.id.btn_chat);
		rl_headLayout = (RelativeLayout) findViewById(R.id.rl_info_head);
		rl_nickLayout = (RelativeLayout) findViewById(R.id.rl_info_nickname);
		rl_accountLayout = (RelativeLayout) findViewById(R.id.rl_info_account);
		rl_sexLayout = (RelativeLayout) findViewById(R.id.rl_info_sex);
		iv_head = (ImageView) findViewById(R.id.iv_head);
		iv_head_arrow = (ImageView) findViewById(R.id.iv_head_arrow);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_nick = (TextView) findViewById(R.id.tv_nick);
		tv_account = (TextView) findViewById(R.id.tv_account);
		tv_sex = (TextView) findViewById(R.id.tv_sex);
		tv_nick.setText(nickName);
		//返回按钮,标题，头像栏,昵称栏,性别栏，设置监听
		btn_back.setOnClickListener(this);
//		backFrameLayout.setOnClickListener(this);
		//如果from携带的值是me,表示的是个人资料
		if (from.equals("me")) {
			//设置标题栏为个人资料
			tv_title.setText("个人资料");
			//为头像设置监听
			rl_headLayout.setOnClickListener(this);
			//为昵称设置监听器
			rl_nickLayout.setOnClickListener(this);
			//为账户设置监听器
			rl_accountLayout.setOnClickListener(this);
			//为性别设置监听器
			rl_sexLayout.setOnClickListener(this);
			//隐藏发起会话的按钮
			btn_chat.setVisibility(View.INVISIBLE);
			
		}
		else {
			tv_title.setText("详细资料");
			//。。。。这里靠队友了。。。。信息栏不需要设置监听，并隐藏信息栏最右边的箭头即可
			
		}
		//判断从昵称设置的界面返回以后修改昵称
		
	
		rl_headLayout.setOnClickListener(this);
//		iv_head.setOnClickListener(this);
//		iv_head_arrow.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		//左上角返回按钮
		case R.id.back_btn:
			//跳回SettingFragment
			finish();
			break;
		
		case R.id.rl_info_head:
			//从底部谈起动画效果
			Toast.makeText(getApplicationContext(), "更换头像", Toast.LENGTH_SHORT).show();
			break;
		//
		case R.id.rl_info_nickname:
			Log.i("account", "准备修改昵称");
			Intent intent = new Intent(SetMyInfoActivity.this,
					UpdateNickActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_info_account:
			Toast.makeText(getApplicationContext(), "账户管理", Toast.LENGTH_SHORT).show();
			break;	
		//性别
		case R.id.rl_info_sex:
			Toast.makeText(getApplicationContext(), "修改性别", Toast.LENGTH_SHORT).show();
			chooseSex();
			break;
		default:
			break;
		}
	}
	//设置性别
	String[] sexs = new String[]{ "男", "女" };
	private void chooseSex() {
			//AlertDialog的创建
			new AlertDialog.Builder(this)
			.setTitle("单选框")
			.setIcon(android.R.drawable.ic_dialog_info)
			//默认选中项为男的,默认为单选项
			/**
			 * 加载一个字符数组sexs，
			 * 中间是默认选中项：对应数组的index
			 * 设置监听器
			 */
			.setSingleChoiceItems(sexs, 0,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							//输出点击的是那个
//							BmobLog.i("点击的是"+sexs[which]);
							//更新数据
							updateSexInfo(which);
							//更新完就让dialog消失
							dialog.dismiss();
						}
					})
			.setNegativeButton("取消", null)
			.show();
	}
	
	/** 修改性别资料：此更新必须要在登陆后才能进行
	  * 这里暂时自定义一个本地LocalUser类
	  * updateInfo
	  * @Title: updateInfo
	  * @return void
	  * @throws
	  */
	//实例化本地的User对象
	LocalUser user1 = new LocalUser("jack", "291823", R.drawable.icon_contact_normal);
//	BmobUserManager userManager;
	private void updateSexInfo(int which)
	{
		//登陆之后方能实例化User
//		final User user = userManager.getCurrentUser(User.class);
		
//		//这个无视
//		BmobLog.i("updateInfo 性别："+user.getSex());
		//true：男  false：女
		if(which==0){
			//0就是男，设置true
			user1.setSex(true);
		}else{//1就为女，设置为false
			user1.setSex(false);
		}
		//获取性别标识，true就是男
		tv_sex.setText(user1.getSex() == true? "男":"女");
		
//		/**
//		 * @params:
//		 * 上下问对象
//		 * 更新监听器
//		 */
//		user.update(this, new UpdateListener() {
//
//			@Override
//			public void onSuccess() {
//				// TODO Auto-generated method stub
//				Toast.makeText(getApplicationContext(), "修改性别成功", Toast.LENGTH_SHORT).show();
//				final User u = userManager.getCurrentUser(User.class);
//				BmobLog.i("修改成功后的sex = "+u.getSex());
//				tv_sex.setText(user.getSex() == true ? "男" : "女");
//			}
//
//			@Override
//			public void onFailure(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				Toast.makeText(getApplicationContext(), "修改性别失败", Toast.LENGTH_SHORT).show();
//			}
//		});
	}
	


	
//	RelativeLayout layout_choose;
//	RelativeLayout layout_photo;
//	PopupWindow avatorPop;
//
//	public String filePath = "";
//
//	private void showAvatarPop() {
//		View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
//				null);
//		layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
//		layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
//		layout_photo.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				ShowLog("点击拍照");
//				// TODO Auto-generated method stub
//				layout_choose.setBackgroundColor(getResources().getColor(
//						R.color.base_color_text_white));
//				layout_photo.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.pop_bg_press));
//				File dir = new File(BmobConstants.MyAvatarDir);
//				if (!dir.exists()) {
//					dir.mkdirs();
//				}
//				// 原图
//				File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
//						.format(new Date()));
//				filePath = file.getAbsolutePath();// 获取相片的保存路径
//				Uri imageUri = Uri.fromFile(file);
//
//				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//				startActivityForResult(intent,
//						BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
//			}
//		});
//		layout_choose.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				ShowLog("点击相册");
//				layout_photo.setBackgroundColor(getResources().getColor(
//						R.color.base_color_text_white));
//				layout_choose.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.pop_bg_press));
//				Intent intent = new Intent(Intent.ACTION_PICK, null);
//				intent.setDataAndType(
//						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//				startActivityForResult(intent,
//						BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
//			}
//		});
//
//		avatorPop = new PopupWindow(view, mScreenWidth, 600);
//		avatorPop.setTouchInterceptor(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//					avatorPop.dismiss();
//					return true;
//				}
//				return false;
//			}
//		});
//
//		avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
//		avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//		avatorPop.setTouchable(true);
//		avatorPop.setFocusable(true);
//		avatorPop.setOutsideTouchable(true);
//		avatorPop.setBackgroundDrawable(new BitmapDrawable());
//		// 动画效果 从底部弹起
//		avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
//		avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
//	}
//
//	/**
//	 * @Title: startImageAction
//	 * @return void
//	 * @throws
//	 */
//	private void startImageAction(Uri uri, int outputX, int outputY,
//			int requestCode, boolean isCrop) {
//		Intent intent = null;
//		if (isCrop) {
//			intent = new Intent("com.android.camera.action.CROP");
//		} else {
//			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//		}
//		intent.setDataAndType(uri, "image/*");
//		intent.putExtra("crop", "true");
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);
//		intent.putExtra("outputX", outputX);
//		intent.putExtra("outputY", outputY);
//		intent.putExtra("scale", true);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//		intent.putExtra("return-data", true);
//		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//		intent.putExtra("noFaceDetection", true); // no face detection
//		startActivityForResult(intent, requestCode);
//	}

//	Bitmap newBitmap;
//	boolean isFromCamera = false;// 区分拍照旋转
//	int degree = 0;
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//		switch (requestCode) {
//		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
//			if (resultCode == RESULT_OK) {
//				if (!Environment.getExternalStorageState().equals(
//						Environment.MEDIA_MOUNTED)) {
//					ShowToast("SD不可用");
//					return;
//				}
//				isFromCamera = true;
//				File file = new File(filePath);
//				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
//				Log.i("life", "拍照后的角度：" + degree);
//				startImageAction(Uri.fromFile(file), 200, 200,
//						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
//			}
//			break;
//		case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
//			if (avatorPop != null) {
//				avatorPop.dismiss();
//			}
//			Uri uri = null;
//			if (data == null) {
//				return;
//			}
//			if (resultCode == RESULT_OK) {
//				if (!Environment.getExternalStorageState().equals(
//						Environment.MEDIA_MOUNTED)) {
//					ShowToast("SD不可用");
//					return;
//				}
//				isFromCamera = false;
//				uri = data.getData();
//				startImageAction(uri, 200, 200,
//						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
//			} else {
//				ShowToast("照片获取失败");
//			}
//
//			break;
//		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
//			// TODO sent to crop
//			if (avatorPop != null) {
//				avatorPop.dismiss();
//			}
//			if (data == null) {
//				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
//				return;
//			} else {
//				saveCropAvator(data);
//			}
//			// 初始化文件路径
//			filePath = "";
//			// 上传头像
//			uploadAvatar();
//			break;
//		default:
//			break;
//
//		}
//	}

/**
 * 上传头像
 */
//	private void uploadAvatar() {
//		BmobLog.i("头像地址：" + path);
//		final BmobFile bmobFile = new BmobFile(new File(path));
//		bmobFile.upload(this, new UploadFileListener() {
//
//			@Override
//			public void onSuccess() {
//				// TODO Auto-generated method stub
//				String url = bmobFile.getFileUrl();
//				// 更新BmobUser对象
//				updateUserAvatar(url);
//			}
//
//			@Override
//			public void onProgress(Integer arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onFailure(int arg0, String msg) {
//				// TODO Auto-generated method stub
//				ShowToast("头像上传失败：" + msg);
//			}
//		});
//	}
/**
 * 更新头像
 */
//	private void updateUserAvatar(final String url) {
//		User user = (User) userManager.getCurrentUser(User.class);
//		user.setAvatar(url);
//		user.update(this, new UpdateListener() {
//			@Override
//			public void onSuccess() {
//				// TODO Auto-generated method stub
//				ShowToast("头像更新成功！");
//				// 更新头像
//				refreshAvatar(url);
//			}
//
//			@Override
//			public void onFailure(int code, String msg) {
//				// TODO Auto-generated method stub
//				ShowToast("头像更新失败：" + msg);
//			}
//		});
//	}
//
//	String path;

/**
 * 保存裁剪的头像
 *  @param data
 */
//	private void saveCropAvator(Intent data) {
//		Bundle extras = data.getExtras();
//		if (extras != null) {
//			Bitmap bitmap = extras.getParcelable("data");
//			Log.i("life", "avatar - bitmap = " + bitmap);
//			if (bitmap != null) {
//				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
//				if (isFromCamera && degree != 0) {
//					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
//				}
//				iv_set_avator.setImageBitmap(bitmap);
//				// 保存图片
//				String filename = new SimpleDateFormat("yyMMddHHmmss")
//						.format(new Date());
//				path = BmobConstants.MyAvatarDir + filename;
//				PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
//						bitmap, true);
//				// 上传头像
//				if (bitmap != null && bitmap.isRecycled()) {
//					bitmap.recycle();
//				}
//			}
//		}
//	}

}
