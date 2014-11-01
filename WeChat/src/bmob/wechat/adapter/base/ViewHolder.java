package bmob.wechat.adapter.base;

import android.util.SparseArray;
import android.view.View;

/** Viewholder的简化
  * @ClassName: ViewHolder
  * @Description: TODO
  * @author limiao
  * 
  */
@SuppressWarnings("unchecked")
public class ViewHolder {
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		//1、下面这句应该 view.getTag(); 中没有设置 viewHolder的时候为 view设置 viewHolder ，下次调用就不会为空
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);  //by yingo 这个的意思应该是实例化ViewHolder中把传过来的参数view设置成SparseArray<View>
		}
		View childView = viewHolder.get(id);
		//2、当1、成立时，(childView == null。则要设置id，第二次就不用了
		if (childView == null) {
			childView = view.findViewById(id);  //by yingo 获得 view.getTag(); 为空则是SparseArray<View>(); 中的ID 再传到 childView
			viewHolder.put(id, childView);
		}
		return (T) childView;///by yingo 返回的是 id所实例化的组建
	}
}
