package bmob.wechat.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/*
 * User基类
 */
public class User extends BmobChatUser {


	/**
	 * //显示数据拼音的首字母
	 */
	private String sortLetters;

	private boolean sex;
	public boolean getSex() {
		return sex;
	}
	public void setSex(boolean sex) {
		this.sex = sex;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	
}
