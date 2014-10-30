package bmob.wechat.adapter;
import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author yangyu
 *	功能描述：ViewPager适配器，用来绑定数据和view
 */
public class ViewPagerAdapter extends PagerAdapter {
	
	//界面列表
    private ArrayList<View> views;
    
    public ViewPagerAdapter (ArrayList<View> views){
        this.views = views;
    }
       
	/**
	 * 获得当前界面数
	 */
	@Override
	public int getCount() {
		 if (views != null) {
             return views.size();
         }      
         return 0;
	}

	/**
	 * 初始化position位置的界面
	 * 我们将要显示的View加入到ViewGroup/View中，然后作为返回值返回即可
	 */
    @Override
    public Object instantiateItem(ViewGroup view, int position) {
    	//将特定的视图添加到ViewPager中，返回那张特定的视图
        ((ViewPager) view).addView(views.get(position), 0);
       
        return views.get(position);
    }
    
    /**
	 * 判断是否由对象生成界面
	 */
	@Override
	public boolean isViewFromObject(View view, Object arg1) {
		return (view == arg1);
	}

	/**
	 * 销毁position位置的界面
	 */
    @Override
    public void destroyItem(ViewGroup view, int position, Object arg2) {
        ((ViewPager) view).removeView(views.get(position));       
    }
}
