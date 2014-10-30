package bmob.wechat.ui.fragment;

import com.wechat.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddressListFragment extends Fragment{
	private View parentView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.address_list_fragment, container,
				false);
		return parentView;

	}

}
