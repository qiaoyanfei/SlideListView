package com.qiao.slidelistview;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class CallListAdapter extends BaseAdapter{

	private Context mContext;
	private List<CallsListItem> mData;


	public static int pos = -1; 
	
	
	private SlideListView mListView;

	public CallListAdapter(Context context, List<CallsListItem> data
			) {

		mContext = context;
		mData = data;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = new ViewHolder();
//		if(position == pos){
//			convertView = null;
//		}
		if(convertView == null){				
			convertView = LayoutInflater.from(mContext).inflate(R.layout.dialing_list_item, null);
			
			viewHolder.tv_phone_name = (TextView) convertView.findViewById(R.id.tv_phone_name);
			viewHolder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
			viewHolder.tv_city = (TextView) convertView.findViewById(R.id.tv_city);
			viewHolder.tv_operator = (TextView) convertView.findViewById(R.id.tv_operator);
			viewHolder.tv_tel_time = (TextView) convertView.findViewById(R.id.tv_tel_time);
			viewHolder.tv_tel = (TextView) convertView.findViewById(R.id.tv_tel);
			viewHolder.btn_call = (ImageView) convertView.findViewById(R.id.btn_call);
			viewHolder.im_tel_state = (ImageView) convertView.findViewById(R.id.im_tel_state);
			viewHolder.ll_location = (LinearLayout) convertView.findViewById(R.id.ll_location);
			viewHolder.delete_left = (ViewGroup) convertView.findViewById(R.id.holder_left);
			viewHolder.delete_right = (ViewGroup) convertView.findViewById(R.id.holder_right);
			viewHolder.cb_dail_check=(CheckBox) convertView.findViewById(R.id.cb_dail_check);
			viewHolder.re_tel_state=(RelativeLayout) convertView.findViewById(R.id.re_tel_state);
			viewHolder.re_left = (RelativeLayout) convertView.findViewById(R.id.re_left);
			viewHolder.im_dialing_canceldelete = (ImageView) convertView.findViewById(R.id.dialing_cancel_delete);
			viewHolder.im_line = (ImageView) convertView.findViewById(R.id.im_line);
			
			convertView.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final CallsListItem item = mData.get(position);
			String nameString = item.getName();
			if (nameString != null && !nameString.isEmpty()) {
				viewHolder.tv_phone_name.setText(nameString);
				viewHolder.ll_location.setVisibility(View.GONE);
				viewHolder.tv_tel.setVisibility(View.VISIBLE);
				viewHolder.tv_tel.setText(item.getPhoneNum());
				

				 viewHolder.tv_city.setText("北京"); //假数据
				 viewHolder.tv_operator.setText("移动"); //假数据
			} else {
				viewHolder.ll_location.setVisibility(View.VISIBLE);
				viewHolder.tv_tel.setVisibility(View.GONE);
				
				viewHolder.tv_city.setText("北京"); // 假数据
				viewHolder.tv_operator.setText("移动"); // 假数据
			}
			
			viewHolder.btn_call.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				if (SlideListView.isSlided == true) {
					pos = -1;
					SlideListView.isSlided = false;
					mListView.slideBackInstant();
					notifyDataSetChanged();
				} 	
				}
			});
		
			
			if(position == pos){
				viewHolder.btn_call.setVisibility(View.GONE);
				viewHolder.re_tel_state.setVisibility(View.GONE);
				viewHolder.re_left.setVisibility(View.GONE);
				viewHolder.im_line.setVisibility(View.GONE);
				viewHolder.delete_left.setVisibility(View.INVISIBLE);
				viewHolder.delete_right.setVisibility(View.INVISIBLE);
				viewHolder.im_dialing_canceldelete.setVisibility(View.VISIBLE);
			}else {

			viewHolder.btn_call.setVisibility(View.VISIBLE);
			viewHolder.cb_dail_check.setVisibility(View.GONE);

			viewHolder.re_tel_state.setVisibility(View.VISIBLE);
			viewHolder.re_left.setVisibility(View.VISIBLE);
			viewHolder.im_line.setVisibility(View.VISIBLE);
			viewHolder.delete_left.setVisibility(View.VISIBLE);
			viewHolder.delete_right.setVisibility(View.VISIBLE);
			viewHolder.im_dialing_canceldelete.setVisibility(View.GONE);
			} 
			
			viewHolder.delete_left.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					pos = position;
					SlideListView.iscancel = true;
					SlideListView.isSlided = false;
					mListView.slideBackInstant();
				
					notifyDataSetChanged();

				}
			});
			viewHolder.delete_right.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					pos = position;
					SlideListView.iscancel = true;
					SlideListView.isSlided = false;
					mListView.slideBackInstant();
					notifyDataSetChanged();

				}
			});

		return convertView;
	}

	public SlideListView getmListView() {
		return mListView;
	}

	public void setmListView(SlideListView mListView) {
		this.mListView = mListView;
	}

	public final class ViewHolder {
		public TextView tv_phone_name;
		public TextView tv_num;
		public TextView tv_city;
		public TextView tv_operator;
		public TextView tv_tel_time;
		public TextView tv_tel;
		public ImageView btn_call;
		public ImageView im_tel_state;
		public LinearLayout ll_location;
		public ViewGroup delete_left;
		public RelativeLayout re_tel_state;
		public RelativeLayout re_left;
		public ViewGroup delete_right;
		public CheckBox cb_dail_check;
		public ImageView im_dialing_canceldelete;
		public ImageView im_line;
		

	}
}
