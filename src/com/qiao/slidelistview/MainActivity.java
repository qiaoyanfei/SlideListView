package com.qiao.slidelistview;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.qiao.slidelistview.SlideListView.DeleteListener;

public class MainActivity extends Activity {
	private SlideListView mCallList;
	
	private  RelativeLayout re_empty;
	
	private  CallListAdapter mCallListAdapter;
	
	private ArrayList<CallsListItem> mCallLogs=new ArrayList<CallsListItem>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		mCallList = (SlideListView)findViewById(R.id.lv_dialing);
		
		re_empty = (RelativeLayout)findViewById(R.id.re_empty);
		
		mCallList.initSlideMode(SlideListView.MOD_BOTH);
		mCallList.setEmptyView(re_empty);
		setData();
		mCallListAdapter=new CallListAdapter(this, mCallLogs);
		mCallList.setAdapter(mCallListAdapter);
		mCallListAdapter.setmListView(mCallList);
		mCallList.setDeleteListener(new DeleteListener() {

			@Override
			public void Delete(ListView listView, View itemView,
					final int position, final int firstVisiblePos,
					boolean isdelete) {
				// TODO Auto-generated method stub

				Log.e("DialController", "setCancelDeleteListener");
				CallListAdapter.pos = -1;
				SlideListView.iscancel = false;
				mCallList.slideBackInstant();
				mCallListAdapter.notifyDataSetChanged();
				if (isdelete == true) {

			
					ExpandAnimation expandAni = new ExpandAnimation(mCallList
							.getChildAt(position
									- mCallList.getFirstVisiblePosition()), 300);
					mCallList.getChildAt(
							position - mCallList.getFirstVisiblePosition())
							.startAnimation(expandAni);
					expandAni.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {

						}

						@Override
						public void onAnimationRepeat(Animation animation) {

						}

						@Override
						public void onAnimationEnd(Animation animation) {
						
							mCallLogs.remove(position - mCallList.getFirstVisiblePosition());
						
						}
						
					});
				}
			}

		});
		
		
		mCallList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
		
						if(position == CallListAdapter.pos){
							CallListAdapter.pos = -1;
							SlideListView.iscancel = false;
							mCallList.slideBackInstant();
							mCallListAdapter.notifyDataSetChanged();
						}
					}
					
				
			});

		super.onCreate(savedInstanceState);
	}

	private void setData(){
		for(int i=0;i<10;i++)
		{
			CallsListItem callItem=new CallsListItem();
			callItem.setName("qiao");
			callItem.setPhoneNum("10086");
			mCallLogs.add(callItem);
		}
	}
	
}
