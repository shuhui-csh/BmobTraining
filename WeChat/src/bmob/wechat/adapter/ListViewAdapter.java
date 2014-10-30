package bmob.wechat.adapter;

import java.io.File;
import java.util.List;
import com.wechat.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	private Context context;
	private List<String> list;
	private LayoutInflater mInflater;// 得到一个LayoutInfalter对象用来导入布局

	public ListViewAdapter(Context context, List<String> list) {
		this.context = context;
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		//
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_adapter_item, parent,
					false);
			holder = new ViewHolder();
			holder.text1 = (TextView) convertView
					.findViewById(R.id.tv1_base_adapter);
			holder.text2 = (TextView) convertView
					.findViewById(R.id.tv2_base_adapter);
			// holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.text1.setText((position + 1) + "、");
		holder.text2.setText(list.get(position));
		// holder.icon.setImageBitmap((position & 1) == 1 ? mIcon1 : mIcon2);
		return convertView;
	}

	static class ViewHolder {
		TextView text1, text2;
		// ImageView icon;
	}

}
