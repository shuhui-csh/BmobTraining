package bmob.wechat.residememu;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.wechat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * User: special Date: 13-12-10 Time: 下午10:44 Mail: specialcyci@gmail.com
 */
public class ResideMenu extends FrameLayout {

	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_RIGHT = 1;
	private static final int PRESSED_MOVE_HORIZANTAL = 2;
	private static final int PRESSED_DOWN = 3;
	private static final int PRESSED_DONE = 4;
	private static final int PRESSED_MOVE_VERTICAL = 5;
	private ImageView imageViewShadow;
	private ImageView imageViewBackground;
	private LinearLayout layoutLeftMenu;
	private LinearLayout layoutRightMenu;
	private ScrollView scrollViewLeftMenu;
	private ScrollView scrollViewRightMenu;
	private ScrollView scrollViewMenu;
	/** 查看附加的活动 */
	private Activity activity;
	/** the decorview of the activity */
	private ViewGroup viewDecor;
	/** the viewgroup of the activity */
	private TouchDisableView viewActivity;
	/** 菜单打开状态的标志 */
	private boolean isOpened;
	private GestureDetector gestureDetector;
	private float shadowAdjustScaleX;
	private float shadowAdjustScaleY;
	/** 不想拦截事件的视图 */
	private List<View> ignoredViews;
	private List<ResideMenuItem> leftMenuItems;
	private List<ResideMenuItem> rightMenuItems;
	private DisplayMetrics displayMetrics = new DisplayMetrics();
	private OnMenuListener menuListener;
	private float lastRawX;
	private boolean isInIgnoredView = false;
	private int scaleDirection = DIRECTION_LEFT;
	private int pressedState = PRESSED_DOWN;
	private List<Integer> disabledSwipeDirection = new ArrayList<Integer>();
	// 0.0f和1.0f之间的有效的比例因子.
	private float mScaleValue = 0.5f;

	public ResideMenu(Context context) {
		super(context);
		initViews(context);
	}

	private void initViews(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.residemenu, this);
		scrollViewLeftMenu = (ScrollView) findViewById(R.id.sv_left_menu);
		scrollViewRightMenu = (ScrollView) findViewById(R.id.sv_right_menu);
		imageViewShadow = (ImageView) findViewById(R.id.iv_shadow);
		layoutLeftMenu = (LinearLayout) findViewById(R.id.layout_left_menu);
		layoutRightMenu = (LinearLayout) findViewById(R.id.layout_right_menu);
		imageViewBackground = (ImageView) findViewById(R.id.iv_background);
	}

	/**
	 * 使用该方法来设置residemenu需要显示的活动;
	 *
	 * @param activity
	 */
	public void attachToActivity(Activity activity) {
		initValue(activity);
		setShadowAdjustScaleXByOrientation();
		viewDecor.addView(this, 0);
		setViewPadding();
	}

	private void initValue(Activity activity) {
		this.activity = activity;
		leftMenuItems = new ArrayList<ResideMenuItem>();
		rightMenuItems = new ArrayList<ResideMenuItem>();
		ignoredViews = new ArrayList<View>();
		viewDecor = (ViewGroup) activity.getWindow().getDecorView();
		viewActivity = new TouchDisableView(this.activity);
		View mContent = viewDecor.getChildAt(0);
		viewDecor.removeViewAt(0);
		viewActivity.setContent(mContent);
		addView(viewActivity);
		ViewGroup parent = (ViewGroup) scrollViewLeftMenu.getParent();
		parent.removeView(scrollViewLeftMenu);
		parent.removeView(scrollViewRightMenu);
	}

	private void setShadowAdjustScaleXByOrientation() {
		int orientation = getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			shadowAdjustScaleX = 0.034f;
			shadowAdjustScaleY = 0.12f;
		} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			shadowAdjustScaleX = 0.06f;
			shadowAdjustScaleY = 0.07f;
		}
	}

	/**
	 * 设置菜单背景图片;
	 *
	 * @param imageResrouce
	 */
	public void setBackground(int imageResrouce) {
		imageViewBackground.setImageResource(imageResrouce);
	}

	/**
	 * visiblity活动视图下的阴影;
	 *
	 * @param isVisible
	 */
	public void setShadowVisible(boolean isVisible) {
		if (isVisible)
			imageViewShadow.setImageResource(R.drawable.shadow);
		else
			imageViewShadow.setImageBitmap(null);
	}

	/**
	 * 左侧菜单添加单个项目;
	 *
	 * @param menuItem
	 */
	@Deprecated
	public void addMenuItem(ResideMenuItem menuItem) {
		this.leftMenuItems.add(menuItem);
		layoutLeftMenu.addView(menuItem);
	}

	/**
	 * 添加单个项目;
	 *
	 * @param menuItem
	 * @param direction
	 */
	public void addMenuItem(ResideMenuItem menuItem, int direction) {
		if (direction == DIRECTION_LEFT) {
			this.leftMenuItems.add(menuItem);
			layoutLeftMenu.addView(menuItem);
		} else {
			this.rightMenuItems.add(menuItem);
			layoutRightMenu.addView(menuItem);
		}
	}

	/**
	 * 数组列表左边的菜单来设置菜单项;
	 *
	 * @param menuItems
	 */
	@Deprecated
	public void setMenuItems(List<ResideMenuItem> menuItems) {
		this.leftMenuItems = menuItems;
		rebuildMenu();
	}

	/**
	 * 数组列表的菜单项设置;
	 *
	 * @param menuItems
	 * @param direction
	 */
	public void setMenuItems(List<ResideMenuItem> menuItems, int direction) {
		if (direction == DIRECTION_LEFT)
			this.leftMenuItems = menuItems;
		else
			this.rightMenuItems = menuItems;
		rebuildMenu();

	}

	private void rebuildMenu() {
		layoutLeftMenu.removeAllViews();
		layoutRightMenu.removeAllViews();
		for (int i = 0; i < leftMenuItems.size(); i++)
			layoutLeftMenu.addView(leftMenuItems.get(i), i);
		for (int i = 0; i < rightMenuItems.size(); i++)
			layoutRightMenu.addView(rightMenuItems.get(i), i);
	}

	/**
	 * 得到左菜单项;
	 *
	 * @return
	 */
	@Deprecated
	public List<ResideMenuItem> getMenuItems() {
		return leftMenuItems;
	}

	/**
	 * 获取菜单项;
	 *
	 * @return
	 */
	public List<ResideMenuItem> getMenuItems(int direction) {
		if (direction == DIRECTION_LEFT)
			return leftMenuItems;
		else
			return rightMenuItems;
	}

	/**
	 * 如果你需要做一些行动关闭或打开菜单上，设置侦听器.
	 *
	 * @return
	 */
	public void setMenuListener(OnMenuListener menuListener) {
		this.menuListener = menuListener;
	}

	public OnMenuListener getMenuListener() {
		return menuListener;
	}

	/**
	 * 在菜单显示之前，我们需要调用该方法，因为填充的活动，在onCreateView时刻得不到;
	 */
	private void setViewPadding() {
		this.setPadding(viewActivity.getPaddingLeft(),
				viewActivity.getPaddingTop(), viewActivity.getPaddingRight(),
				viewActivity.getPaddingBottom());
	}

	/**
	 * 显示菜单;
	 */
	public void openMenu(int direction) {

		setScaleDirection(direction);

		isOpened = true;
		AnimatorSet scaleDown_activity = buildScaleDownAnimation(viewActivity,
				mScaleValue, mScaleValue);
		AnimatorSet scaleDown_shadow = buildScaleDownAnimation(imageViewShadow,
				mScaleValue + shadowAdjustScaleX, mScaleValue
						+ shadowAdjustScaleY);
		AnimatorSet alpha_menu = buildMenuAnimation(scrollViewMenu, 1.0f);
		scaleDown_shadow.addListener(animationListener);
		scaleDown_activity.playTogether(scaleDown_shadow);
		scaleDown_activity.playTogether(alpha_menu);
		scaleDown_activity.start();
	}

	/**
	 * 关闭reslide菜单;
	 */
	public void closeMenu() {

		isOpened = false;
		AnimatorSet scaleUp_activity = buildScaleUpAnimation(viewActivity,
				1.0f, 1.0f);
		AnimatorSet scaleUp_shadow = buildScaleUpAnimation(imageViewShadow,
				1.0f, 1.0f);
		AnimatorSet alpha_menu = buildMenuAnimation(scrollViewMenu, 0.0f);
		scaleUp_activity.addListener(animationListener);
		scaleUp_activity.playTogether(scaleUp_shadow);
		scaleUp_activity.playTogether(alpha_menu);
		scaleUp_activity.start();
	}

	@Deprecated
	public void setDirectionDisable(int direction) {
		disabledSwipeDirection.add(direction);
	}

	public void setSwipeDirectionDisable(int direction) {
		disabledSwipeDirection.add(direction);
	}

	private boolean isInDisableDirection(int direction) {
		return disabledSwipeDirection.contains(direction);
	}

	private void setScaleDirection(int direction) {

		int screenWidth = getScreenWidth();
		float pivotX;
		float pivotY = getScreenHeight() * 0.5f;

		if (direction == DIRECTION_LEFT) {
			scrollViewMenu = scrollViewLeftMenu;
			pivotX = screenWidth * 1.5f;
		} else {
			scrollViewMenu = scrollViewRightMenu;
			pivotX = screenWidth * -0.5f;
		}

		ViewHelper.setPivotX(viewActivity, pivotX);
		ViewHelper.setPivotY(viewActivity, pivotY);
		ViewHelper.setPivotX(imageViewShadow, pivotX);
		ViewHelper.setPivotY(imageViewShadow, pivotY);
		scaleDirection = direction;
	}

	/**
	 * 返回菜单状态的标志;
	 *
	 * @return
	 */
	public boolean isOpened() {
		return isOpened;
	}

	private OnClickListener viewActivityOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (isOpened())
				closeMenu();
		}
	};

	private Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {
			if (isOpened()) {
				showScrollViewMenu();
				if (menuListener != null)
					menuListener.openMenu();
			}
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			// reset the view;
			if (isOpened()) {
				viewActivity.setTouchDisable(true);
				viewActivity.setOnClickListener(viewActivityOnClickListener);
			} else {
				viewActivity.setTouchDisable(false);
				viewActivity.setOnClickListener(null);
				hideScrollViewMenu();
				if (menuListener != null)
					menuListener.closeMenu();
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {

		}

		@Override
		public void onAnimationRepeat(Animator animation) {

		}
	};

	/**
	 * a helper method to build scale down animation;
	 *
	 * @param target
	 * @param targetScaleX
	 * @param targetScaleY
	 * @return
	 */
	private AnimatorSet buildScaleDownAnimation(View target,
			float targetScaleX, float targetScaleY) {

		AnimatorSet scaleDown = new AnimatorSet();
		scaleDown.playTogether(
				ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
				ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));
		scaleDown.setInterpolator(AnimationUtils.loadInterpolator(activity,
				android.R.anim.decelerate_interpolator));
		scaleDown.setDuration(250);
		return scaleDown;
	}

	/**
	 * a helper method to build scale up animation;
	 *
	 * @param target
	 * @param targetScaleX
	 * @param targetScaleY
	 * @return
	 */
	private AnimatorSet buildScaleUpAnimation(View target, float targetScaleX,
			float targetScaleY) {

		AnimatorSet scaleUp = new AnimatorSet();
		scaleUp.playTogether(
				ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
				ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));
		scaleUp.setDuration(250);
		return scaleUp;
	}

	private AnimatorSet buildMenuAnimation(View target, float alpha) {

		AnimatorSet alphaAnimation = new AnimatorSet();
		alphaAnimation.playTogether(ObjectAnimator.ofFloat(target, "alpha",
				alpha));
		alphaAnimation.setDuration(250);
		return alphaAnimation;
	}

	/**
	 * 如果有一些观点你不想住菜单拦截事件，你可以使用这个方法来设置.
	 *
	 * @param v
	 */
	public void addIgnoredView(View v) {
		ignoredViews.add(v);
	}

	/**
	 * 从忽略视图列表中删除视图;
	 * 
	 * @param v
	 */
	public void removeIgnoredView(View v) {
		ignoredViews.remove(v);
	}

	/**
	 * 清除忽略视图列表;
	 */
	public void clearIgnoredViewList() {
		ignoredViews.clear();
	}

	/**
	 * 如果议案evnent是相对忽略视图的视图列表，返回true;
	 *
	 * @param ev
	 * @return
	 */
	private boolean isInIgnoredView(MotionEvent ev) {
		Rect rect = new Rect();
		for (View v : ignoredViews) {
			v.getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX(), (int) ev.getY()))
				return true;
		}
		return false;
	}

	private void setScaleDirectionByRawX(float currentRawX) {
		if (currentRawX < lastRawX)
			setScaleDirection(DIRECTION_RIGHT);
		else
			setScaleDirection(DIRECTION_LEFT);
	}

	private float getTargetScale(float currentRawX) {
		float scaleFloatX = ((currentRawX - lastRawX) / getScreenWidth()) * 0.75f;
		scaleFloatX = scaleDirection == DIRECTION_RIGHT ? -scaleFloatX
				: scaleFloatX;
		float targetScale = ViewHelper.getScaleX(viewActivity) - scaleFloatX;
		targetScale = targetScale > 1.0f ? 1.0f : targetScale;
		targetScale = targetScale < 0.5f ? 0.5f : targetScale;
		return targetScale;
	}

	private float lastActionDownX, lastActionDownY;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		float currentActivityScaleX = ViewHelper.getScaleX(viewActivity);
		if (currentActivityScaleX == 1.0f)
			setScaleDirectionByRawX(ev.getRawX());

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastActionDownX = ev.getX();
			lastActionDownY = ev.getY();
			isInIgnoredView = isInIgnoredView(ev) && !isOpened();
			pressedState = PRESSED_DOWN;
			break;

		case MotionEvent.ACTION_MOVE:
			if (isInIgnoredView || isInDisableDirection(scaleDirection))
				break;

			if (pressedState != PRESSED_DOWN
					&& pressedState != PRESSED_MOVE_HORIZANTAL)
				break;

			int xOffset = (int) (ev.getX() - lastActionDownX);
			int yOffset = (int) (ev.getY() - lastActionDownY);

			if (pressedState == PRESSED_DOWN) {
				if (yOffset > 25 || yOffset < -25) {
					pressedState = PRESSED_MOVE_VERTICAL;
					break;
				}
				if (xOffset < -50 || xOffset > 50) {
					pressedState = PRESSED_MOVE_HORIZANTAL;
					ev.setAction(MotionEvent.ACTION_CANCEL);
				}
			} else if (pressedState == PRESSED_MOVE_HORIZANTAL) {
				if (currentActivityScaleX < 0.95)
					showScrollViewMenu();

				float targetScale = getTargetScale(ev.getRawX());
				ViewHelper.setScaleX(viewActivity, targetScale);
				ViewHelper.setScaleY(viewActivity, targetScale);
				ViewHelper.setScaleX(imageViewShadow, targetScale
						+ shadowAdjustScaleX);
				ViewHelper.setScaleY(imageViewShadow, targetScale
						+ shadowAdjustScaleY);
				ViewHelper.setAlpha(scrollViewMenu, (1 - targetScale) * 2.0f);

				lastRawX = ev.getRawX();
				return true;
			}

			break;

		case MotionEvent.ACTION_UP:

			if (isInIgnoredView)
				break;
			if (pressedState != PRESSED_MOVE_HORIZANTAL)
				break;

			pressedState = PRESSED_DONE;
			if (isOpened()) {
				if (currentActivityScaleX > 0.56f)
					closeMenu();
				else
					openMenu(scaleDirection);
			} else {
				if (currentActivityScaleX < 0.94f) {
					openMenu(scaleDirection);
				} else {
					closeMenu();
				}
			}

			break;

		}
		lastRawX = ev.getRawX();
		return super.dispatchTouchEvent(ev);
	}

	public int getScreenHeight() {
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	public int getScreenWidth() {
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}

	public void setScaleValue(float scaleValue) {
		this.mScaleValue = scaleValue;
	}

	public interface OnMenuListener {

		/**
		 * 方法将调用打开菜单的动画完成时间.
		 */
		public void openMenu();

		/**
		 * 方法将调用关闭菜单的动画完成时间 .
		 */
		public void closeMenu();
	}

	private void showScrollViewMenu() {
		if (scrollViewMenu != null && scrollViewMenu.getParent() == null) {
			addView(scrollViewMenu);
		}
	}

	private void hideScrollViewMenu() {
		if (scrollViewMenu != null && scrollViewMenu.getParent() != null) {
			removeView(scrollViewMenu);
		}
	}
}
