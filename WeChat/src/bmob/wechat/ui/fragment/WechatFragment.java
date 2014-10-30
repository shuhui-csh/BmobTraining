package bmob.wechat.ui.fragment;
import java.util.List;

import bmob.wechat.adapter.ListViewAdapter;

import com.wechat.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class WechatFragment extends Fragment{
	private View parentView;
	private List<String> list;
	private ListView WechatlistView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.wechat_fragment, container, false);
		WechatlistView = (ListView) parentView.findViewById(R.id.chat_list);
		ListViewAdapter adapter = new ListViewAdapter(getActivity(), list);
		//WechatlistView.setAdapter(adapter);
		return parentView;
		
	}

}
