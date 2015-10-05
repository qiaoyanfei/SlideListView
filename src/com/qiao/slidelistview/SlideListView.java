package com.qiao.slidelistview;



import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * 侧向滑出菜单的ListView
 * 使用请注意与ListView的Item的布局配合，
 * 该效果的实现是基于在Item的布局中通过设置PaddingLeft和PaddingRight来隐藏左右菜单的，
 * 所以使用此ListView时，请务必在布局Item时使用PaddingLeft和PaddingRight；
 * 或者自己改写此ListView，已达到想要的实现方式
 * @author zhangshuo
 */
public class SlideListView extends ListView {
	
	/**禁止侧滑模式*/
	public static int MOD_FORBID = 0;
	/**从左向右滑出菜单模式*/
	public static int MOD_LEFT = 1;
	/**从右向左滑出菜单模式*/
	public static int MOD_RIGHT = 2;
	/**左右均可以滑出菜单模式*/
	public static int MOD_BOTH = 3;
	/**当前的模式*/
	private int mode = MOD_FORBID;
	/**左侧菜单的长度*/
	private int leftLength = 0;
	/**右侧菜单的长度*/
	private int rightLength = 0;
	
	/**
	 * 当前滑动的ListView　position
	 */
	private int slidePosition;
	/**
	 * 手指按下X的坐标
	 */
	private int downY;
	/**
	 * 手指按下Y的坐标
	 */
	private int downX;
	/**
	 * ListView的item
	 */
	private View itemView;
	/**
	 * 滑动类
	 */
	private Scroller scroller;
	/**
	 * 认为是用户滑动的最小距离
	 */
	private int mTouchSlop;

	/**
	 * 判断是否可以侧向滑动
	 */
	private boolean canMove = false;
	/**
	 * 标示是否完成侧滑
	 */
	public static boolean isSlided = false;
	
	public static boolean iscancel = false;

	private final int slideWidthDp=62;
	private int slideWidth = dip2px(getContext(), slideWidthDp);
	private int screenWidth;
	private int firstVisiblePos;
	
	private DeleteListener mDeleteListener;
	
	public SlideListView(Context context) {
		this(context, null);
	}

	public SlideListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		scroller = new Scroller(context);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		screenWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		
	}

	/**
	 * 滑动撤销删除监听器
	 */
	public void setDeleteListener(DeleteListener deleteListener){
		this.mDeleteListener = deleteListener;
	}
	
	/**
	 * 初始化菜单的滑出模式
	 * @param mode
	 */
	public void initSlideMode(int mode){
		this.mode = mode;
	}
	
	/**
	 * 处理我们拖动ListView item的逻辑
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		int lastX = (int) ev.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			System.out.println("touch-->" + "down");
			
			/*当前模式不允许滑动，则直接返回，交给ListView自身去处理*/
			if(this.mode == MOD_FORBID){
				Log.e("SlideListView", "this.mode == MOD_FORBID");
				return super.onTouchEvent(ev);
			}
			
			// 如果处于侧滑完成状态，侧滑回去，并直接返回
			if (isSlided) {
				Log.e("SlideListView", "isSlided");
				scrollBack();
				return false;
			}
			
			if(iscancel && slidePosition != pointToPosition((int) ev.getX(), (int) ev.getY())){
				Log.e("SlideListView", "删除");
				mDeleteListener.Delete(this, itemView, slidePosition, firstVisiblePos, true);
				return false;
			}
			// 假如scroller滚动还没有结束，我们直接返回
			if (!scroller.isFinished()) {
				Log.e("SlideListView", "!scroller.isFinished()");
				return false;
			}
			downX = (int) ev.getX();
			downY = (int) ev.getY();

			slidePosition = pointToPosition(downX, downY);

			Log.e("SlideListView", "slidePosition = " + slidePosition);
			// 无效的position, 不做任何处理
			if (slidePosition == AdapterView.INVALID_POSITION) {
				return super.onTouchEvent(ev);
			}

			// 获取我们点击的item view
			firstVisiblePos = getFirstVisiblePosition();
			
			Object temp=getChildAt(slidePosition - firstVisiblePos).getTag();
			itemView =getChildAt(slidePosition - firstVisiblePos);
			
		
		
//			Log.e("SlideListView", "getFirstVisiblePosition() = " + getFirstVisiblePosition());
			/*此处根据设置的滑动模式，自动获取左侧或右侧菜单的长度*/
			if(this.mode == MOD_BOTH){
				this.leftLength = dip2px(getContext(), slideWidthDp);
				this.rightLength = dip2px(getContext(), slideWidthDp);
				
			}else if(this.mode == MOD_LEFT){
				this.leftLength = dip2px(getContext(), slideWidthDp);
			}else if(this.mode == MOD_RIGHT){
				this.rightLength = dip2px(getContext(), slideWidthDp);
			}
			
			break;
		case MotionEvent.ACTION_MOVE:
			System.out.println("touch-->" + "move");
			
			if (!canMove
					&& slidePosition != AdapterView.INVALID_POSITION
					&& (Math.abs(ev.getX() - downX) > mTouchSlop && Math.abs(ev
							.getY() - downY) < mTouchSlop)) {
				int offsetX = downX - lastX;
				if(offsetX > 0 && (this.mode == MOD_BOTH || this.mode == MOD_RIGHT)){
					/*从右向左滑*/
					canMove = true;
				}else if(offsetX < 0 && (this.mode == MOD_BOTH || this.mode == MOD_LEFT)){
					/*从左向右滑*/
					canMove = true;
				}else{
					canMove = false;
				}
				/*此段代码是为了避免我们在侧向滑动时同时出发ListView的OnItemClickListener时间*/
				MotionEvent cancelEvent = MotionEvent.obtain(ev);
				cancelEvent
						.setAction(MotionEvent.ACTION_CANCEL
								| (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
				onTouchEvent(cancelEvent);
			}
			if (canMove) {
				/*设置此属性，可以在侧向滑动时，保持ListView不会上下滚动*/
				requestDisallowInterceptTouchEvent(true);
				
				// 手指拖动itemView滚动, deltaX大于0向左滚动，小于0向右滚
				int deltaX = downX - lastX;
				if(deltaX < 0 && (this.mode == MOD_BOTH || this.mode == MOD_LEFT)){
					/*向左滑*/
					if(deltaX > -slideWidth || iscancel == true){
						itemView.scrollTo(deltaX, 0);
						}
					else {
						itemView.scrollTo(-slideWidth, 0);
					}
				}else if(deltaX > 0 && (this.mode == MOD_BOTH || this.mode == MOD_RIGHT)){
					/*向右滑*/
					if(deltaX < slideWidth || iscancel == true){
						itemView.scrollTo(deltaX, 0);
						}
					else {
						itemView.scrollTo(slideWidth, 0);
					}
				}else{
					itemView.scrollTo(0, 0);
				}
				return true; // 拖动的时候ListView不滚动
			}
		case MotionEvent.ACTION_UP:
			System.out.println("touch-->" + "up");
			if (canMove){
				canMove = false;
				if(iscancel == false){
					scrollByDistanceX();
				}
				else {
					if (itemView.getScrollX() >= screenWidth / 3) {
						final int delta = (screenWidth - itemView.getScrollX());
						// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
						scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
								Math.abs(delta));						
						postInvalidate(); 
						mDeleteListener.Delete(this, itemView, slidePosition, firstVisiblePos, false);
					} else if (itemView.getScrollX() <= -screenWidth / 3) {
						final int delta = (screenWidth + itemView.getScrollX());
						// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
						scroller.startScroll(itemView.getScrollX(), 0, -delta, 0,
								Math.abs(delta));
						postInvalidate();
						mDeleteListener.Delete(this, itemView, slidePosition, firstVisiblePos, false);
					} else {
						// 滚回到原始位置
						scrollBack();
					}
				}
			}
			break;
		}

		// 否则直接交给ListView来处理onTouchEvent事件
		return super.onTouchEvent(ev);
	}

	/**
	 * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
	 */
	private void scrollByDistanceX() {
		/*当前模式不允许滑动，则直接返回*/
		if(this.mode == MOD_FORBID){
			return;
		}
		if(itemView.getScrollX() > 0 && (this.mode == MOD_BOTH || this.mode == MOD_RIGHT)){
			/*从右向左滑*/
			if (itemView.getScrollX() >= rightLength / 2) {
				scrollLeft();
			}  else {
				// 滚回到原始位置
				scrollBack();
			}
		}else if(itemView.getScrollX() < 0 && (this.mode == MOD_BOTH || this.mode == MOD_LEFT)){
			/*从左向右滑*/
			if (itemView.getScrollX() <= -leftLength / 2) {
				scrollRight();
			} else {
				// 滚回到原始位置
				scrollBack();
			}
		}else{
			// 滚回到原始位置
			scrollBack();
		}

	}

	/**
	 * 往右滑动，getScrollX()返回的是左边缘的距离，就是以View左边缘为原点到开始滑动的距离，所以向右边滑动为负值
	 */
	private void scrollRight() {
		isSlided = true;
		final int delta = (leftLength + itemView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		scroller.startScroll(itemView.getScrollX(), 0, -delta, 0,
				Math.abs(delta));
		postInvalidate(); // 刷新itemView
	}

	/**
	 * 向左滑动，根据上面我们知道向左滑动为正值
	 */
	private void scrollLeft() {
		isSlided = true;
		final int delta = (rightLength - itemView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
				Math.abs(delta));
		postInvalidate(); // 刷新itemView
	}

	/**
	 * 滑动会原来的位置
	 */
	private void scrollBack() {
		isSlided = false;
		if(scroller != null && itemView != null)
			scroller.startScroll(itemView.getScrollX(), 0, -itemView.getScrollX(),
					0, Math.abs(itemView.getScrollX()));
		postInvalidate(); // 刷新itemView
	}

	@Override
	public void computeScroll() {
		// 调用startScroll的时候scroller.computeScrollOffset()返回true，
		if (scroller.computeScrollOffset()) {
			// 让ListView item根据当前的滚动偏移量进行滚动
			itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());

			postInvalidate();

		}
	}

	/**
	 * 提供给外部调用，用以将侧滑出来的滑回去
	 */
	public void slideBack() {
		this.scrollBack();
	}
	
	public void slideBackInstant() {
		isSlided = false;
		if(scroller != null && itemView != null)
			scroller.startScroll(itemView.getScrollX(), 0, -itemView.getScrollX(),
					0, 0);
		postInvalidate(); // 刷新itemView
	}
	
	/**
	 * 滑动撤销删除监听器
	 */
	public interface DeleteListener {
		public void Delete(ListView listtView, View itemView, int position, int firstVisiblePos, boolean isdelete);
	}
	
	
	/**
	 * dip转化像素
	 * @param context
	 * @param dipValue
	 * @return
	 */
	private  int dip2px(Context context, float dipValue){

		final float scale = context.getResources().getDisplayMetrics().density;

		return (int)(dipValue * scale + 0.5f);

	}
	

}