package bmob.wechat.adapter.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bmob.wechat.bean.FaceText;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


/**
 * 在实例化这个类的时候 ，通过上下文和 FaceText... datas 获得和  设置List<“FaceText”> datas 数据;
 * 类的作用是 获得长度，选项，选项的位置和空视图
 * @author 我只是加注释的yingo
 * */
public class BaseArrayListAdapter extends BaseAdapter {

	protected Context mContext;
	protected LayoutInflater mInflater;
	protected List<FaceText> mDatas = new ArrayList<FaceText>();

	public BaseArrayListAdapter(Context context, FaceText... datas) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		if (datas != null && datas.length > 0) {
			mDatas = Arrays.asList(datas);
		}
	}

	public BaseArrayListAdapter(Context context, List<FaceText> datas) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		if (datas != null && datas.size() > 0) {
			mDatas = datas;
		}
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}
}
