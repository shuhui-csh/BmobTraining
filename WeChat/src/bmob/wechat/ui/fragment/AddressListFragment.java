package bmob.wechat.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import bmob.wechat.adapter.UserFriendAdapter;
import bmob.wechat.bean.LocalUser;
import bmob.wechat.bean.User;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;

import com.wechat.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class AddressListFragment extends Fragment{
	private BmobUserManager userManager;
	private ListView lv;
	private List<BmobChatUser> bmobChatUserList = new ArrayList<BmobChatUser>();
	private List<User> friends = new ArrayList<User>();
	
	private View parentView;
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.address_list_fragment, container,
				false);//加载布局文件
		
		lv = (ListView) parentView.findViewById(R.id.lv);
		
		userManager = BmobUserManager.getInstance(getActivity());
		//查询所有的联系人
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				Toast.makeText(getActivity(), "失败", 1000).show();
				// TODO Auto-generated method stub
				if (arg0 == BmobConfig.CODE_COMMON_NONE) {
					Log.i("AddressListFragment", "失败"+arg1);
				} else {
					Log.i("AddressListFragment","查询好友列表失败：" + arg1);
				}
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), arg0.size() + "", 1000).show();
				//成功，得到联系人列表，将其设置到ListView中去
				bmobChatUserList = arg0;
				setAdapter();
			}
		});
		/*
		 * 设置监听，长按删除
		 */
		lv.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("删除！");
				builder.setMessage("您想将好友“"+friends.get(position).getUsername()+"”删除？");
				builder.setNegativeButton("取消", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			}			
		});
		
		return parentView;

	}
	/*
	 * 将List<BmobChatUser>转化为List<User>
	 */
	private List<User> filledData(List<BmobChatUser> datas) {
		// 清除原有list
		Log.i("MainActivity", datas.size() + "-----data");
		List<User> friends = new ArrayList<User>();
		friends.clear();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			BmobChatUser user = datas.get(i);
			User sortModel = new User();
			sortModel.setAvatar(user.getAvatar());
			sortModel.setNick(user.getNick());// 昵称
			sortModel.setUsername(user.getUsername());
			sortModel.setObjectId(user.getObjectId());
			sortModel.setContacts(user.getContacts());
			
			friends.add(sortModel);// 添加到盆友的数组中去
		}
		// 根据a-z进行排序
		// Collections.sort(friends, pinyinComparator);
		return friends;
	}
	
	/*
	 * 设置ListView
	 */
	public void setAdapter() {
		Log.i("MainActivity", bmobChatUserList.size() + "===");
		friends = filledData(bmobChatUserList);
		Log.i("MainActivity", friends.size() + "---->friends");
		UserFriendAdapter userFriendAdapter = new UserFriendAdapter(getActivity(),friends);
		lv.setAdapter(userFriendAdapter);
	}

}
