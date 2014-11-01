package bmob.wechat.adapter;

import java.util.List;

import com.wechat.R;

import bmob.wechat.adapter.base.BaseArrayListAdapter;
import bmob.wechat.bean.FaceText;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 可能是单个表情界面
 * */
public class EmoteAdapter extends BaseArrayListAdapter {

	public EmoteAdapter(Context context, List<FaceText> datas) {
		super(context, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_face_text, null);
			holder = new ViewHolder();
			holder.mIvImage = (ImageView) convertView
					.findViewById(R.id.v_face_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//实例化的时候获得的数据的第几个，获取他的名称
		FaceText faceText = (FaceText) getItem(position);
		String key = faceText.text.substring(1);
		Drawable drawable =mContext.getResources().getDrawable(mContext.getResources().getIdentifier(key, "drawable", mContext.getPackageName()));
		holder.mIvImage.setImageDrawable(drawable);
		return convertView;
	}

	class ViewHolder {
		ImageView mIvImage;
	}
}
