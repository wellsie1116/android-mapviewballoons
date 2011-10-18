/***
 * Copyright (c) 2010 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.readystatesoftware.mapviewballoons;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A view representing a MapView marker information balloon.
 * <p>
 * This class has a number of Android resource dependencies:
 * <ul>
 * <li>drawable/balloon_overlay_bg_selector.xml</li>
 * <li>drawable/balloon_overlay_close.png</li>
 * <li>drawable/balloon_overlay_focused.9.png</li>
 * <li>drawable/balloon_overlay_unfocused.9.png</li>
 * <li>layout/balloon_map_overlay.xml</li>
 * </ul>
 * </p>
 * 
 * @author Jeff Gilfelt
 *
 */
public class BalloonOverlayView extends FrameLayout {

	private LinearLayout layout;
	private TextView titleView;
	private TextView snippetView;
	private OnTouchListener customTouchListener;

	/**
	 * Create a new BalloonOverlayView.
	 * 
	 * @param context - The activity context.
	 * @param balloonBottomOffset - The bottom padding (in pixels) to be applied
	 * when rendering this view.
	 */
	public BalloonOverlayView(Context context, int balloonBottomOffset) {

		super(context);

		setPadding(10, 0, 10, balloonBottomOffset);
		layout = new LinearLayout(context);
		layout.setVisibility(VISIBLE);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.balloon_overlay, layout);
		titleView = (TextView) v.findViewById(R.id.balloon_item_title);
		snippetView = (TextView) v.findViewById(R.id.balloon_item_snippet);

		ImageView close = (ImageView) v.findViewById(R.id.close_img_button);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				layout.setVisibility(GONE);
			}
		});

		//set up our touch event handler
		View clickRegion = v.findViewById(R.id.balloon_inner_layout);
		clickRegion.setOnTouchListener(touchListener);
		customTouchListener = defaultCustomTouchListener;
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.NO_GRAVITY;
		
		addView(layout, params);

	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View#setOnTouchListener(android.view.View.OnTouchListener)
	 */
	@Override 
	public void setOnTouchListener(OnTouchListener listener) {
		if (listener == null) {
			customTouchListener = defaultCustomTouchListener;
		} else {
			customTouchListener = listener;
		}
	}
	
	/**
	 * Sets the text in the balloon
	 * 
	 * @param title The title to display, or null
	 * @param snippet The subtext to display, or null
	 */
	public void setText(String title, String snippet) {
		layout.setVisibility(VISIBLE);
		setViewText(titleView, title);
		setViewText(snippetView, snippet);
	}
	
	private void setViewText(TextView view, String text) {
		if (text!= null) {
			view.setVisibility(VISIBLE);
			view.setText(text);
		} else {
			view.setVisibility(GONE);
		}
	}
	
	private OnTouchListener touchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			View l =  ((View) getParent()).findViewById(R.id.balloon_main_layout);
			Drawable d = l.getBackground();
			
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				int[] states = {android.R.attr.state_pressed};
				if (d.setState(states)) {
					d.invalidateSelf();
				}
				customTouchListener.onTouch(v, event);
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				int newStates[] = {};
				if (d.setState(newStates)) {
					d.invalidateSelf();
				}
			}
			
			return customTouchListener.onTouch(v, event);
		}
	};
	
	private static OnTouchListener defaultCustomTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return false;
		}
	};

}
