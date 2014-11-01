package bmob.wechat.adapter.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;
import cn.bmob.im.util.BmobLog;


/**
 * 基础的适配器
 * 
 * @ClassName: BaseListAdapter
 * @Description: TODO
 * @author yingo
 * @param <E>
 */
@SuppressLint("UseSparseArrays")
public abstract class BaseListAdapter<E> extends BaseAdapter {

	public List<E> list;

	public Context mContext;

	public LayoutInflater mInflater;

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
//		有时候我们需要修改已经生成的列表，添加或者修改数据，
//		notifyDataSetChanged()可以在修改适配器绑定的数组后，
//		不用重新刷新Activity，通知Activity更新ListView。
//		今天的例子就是通过Handler AsyncTask两种方式来动态更新ListView.
		//但是调用一般要在另外的线程调用：例如:
//		 Runnable add=new Runnable(){
//			 35  
//			 36         @Override
//			 37         public void run() {
//			 38             // TODO Auto-generated method stub
//			 39             arr.add("增加一项");//增加一项
//			 40             Adapter.notifyDataSetChanged();    
//			 41         }       
//			 42     };
		notifyDataSetChanged();
	}

	public void add(E e) {
		this.list.add(e);
		notifyDataSetChanged();
	}

	public void addAll(List<E> list) {
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	public void remove(int position) {
		this.list.remove(position);
		notifyDataSetChanged();
	}

	public BaseListAdapter(Context context, List<E> list) {
		super();
		this.mContext = context;
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = bindView(position, convertView, parent);
		// 绑定内部点击监听
		addInternalClickListener(convertView, position, list.get(position));
		return convertView;
	}

	/**
	 * bindView抽象接口
	 * */
	public abstract View bindView(int position, View convertView,
			ViewGroup parent);

	// adapter中的内部点击事件
	
	//Map里面携带 整数（id）和内部监听器 
	public Map<Integer, onInternalClickListener> canClickItem;
	/**
	 * addInternalClickListener就是getView方法里面绑定inView内部监听器
	 * @param 视图（包含需要绑定组建inview的view），位置，（为inview绑定监听事件所需要的位置参数）键值对（为inview绑定监听事件所需要的valuesMap参数）
	 * 
	 */
	private void addInternalClickListener(final View itemV, final Integer position,final Object valuesMap) {
		if (canClickItem != null) {
			for (Integer key : canClickItem.keySet()) {//对Integer类型进行循环取出canClickItem.keySet()上的set对象，里面只有key值
				View inView = itemV.findViewById(key);//实例化canClickItem中key保存的id的对象
				final onInternalClickListener inviewListener = canClickItem.get(key);//通过key获得value。就是onInternalClickListener
				if (inView != null && inviewListener != null) {//如果key不为空且有监听器，则为这个这个id的view设置单击事件为监听器为 OnClickListener，不知道有什么用
					inView.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							inviewListener.OnClickListener(itemV, v, position,
									valuesMap);
						}
					});
				}
			}
		}
	}

	/**
	 * 设置canClickItem，把 key跟value设置成 id 和 监听器
	 * */
	public void setOnInViewClickListener(Integer key,
			onInternalClickListener onClickListener) {
		
		if (canClickItem == null)
			canClickItem = new HashMap<Integer, onInternalClickListener>();
		canClickItem.put(key, onClickListener);
	}

	public interface onInternalClickListener {
		public void OnClickListener(View parentV, View v, Integer position,
				Object values);
	}

	Toast mToast;

	public void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			((Activity) mContext).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (mToast == null) {
						mToast = Toast.makeText(mContext, text,
								Toast.LENGTH_SHORT);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});

		}
	}

	/**
	 * 打Log ShowLog
	 * @return void
	 * @throws
	 */
	public void ShowLog(String msg) {
		BmobLog.i(msg);
	}

}
