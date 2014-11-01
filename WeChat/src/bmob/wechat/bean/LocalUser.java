package bmob.wechat.bean;


public class LocalUser {
	//本地测试的用户类
	//昵称，账户，性别，头像
	private String nickName;
	private String account;
	private boolean sex;
	private int iv_head_id;
	
	public LocalUser getLocalUser() {
		return null;
		
	}
	
	public LocalUser(String nickName,String account,int iv_head_id) {
		this.nickName = nickName;
		this.account = account;
		this.iv_head_id = iv_head_id;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public int getIv_head() {
		return iv_head_id;
	}
	public void setIv_head(int iv_head_id) {
		this.iv_head_id = iv_head_id;
	}

	public boolean getSex() {
		return sex;
	}
	public void setSex(boolean sex) {
		this.sex = sex;
	}
	
	
	
}
