package com.ezardlabs.dethsquare.util;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.ezardlabs.dethsquare.Input;

public class GameView extends GLSurfaceView {

	private final GLRenderer mRenderer;

	public GameView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		mRenderer = new GLRenderer((BaseGame) context);
		setRenderer(mRenderer);
	}

	@Override
	public void onPause() {
		super.onPause();
		mRenderer.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mRenderer.onResume();
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		int index;
//		if (event.getPointerCount() >= 2) {
//			index = event.getActionIndex();
//		} else {
//			index = 0;
//		}
//		switch (event.getActionMasked()) {
//			case MotionEvent.ACTION_DOWN:
//			case MotionEvent.ACTION_POINTER_DOWN:
//				if (event.getX(index) > getWidth() / 2) {
//					Input.jump = true;
//					return true;
//				}
//				break;
//			case MotionEvent.ACTION_MOVE:
//				if (event.getX(index) < getWidth() / 2f) {
//					int x;
//					if (event.getX(index) < getWidth() / 8f) {
//						x = -1;
//					} else {
//						x = 1;
//					}
//					Input.set(x, 0);
//				}
//				break;
//			case MotionEvent.ACTION_UP:
//			case MotionEvent.ACTION_POINTER_UP:
//				Input.set(0, 0);
//				Input.jump = false;
//				break;
//		}
//		return true;
//	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		for (Input.OnTouchListener otl : Input.onTouchListeners) {
			switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					otl.onTouchDown(event.getActionIndex(), event.getX(event.getActionIndex()),
							event.getY(event.getActionIndex()));
					break;
				case MotionEvent.ACTION_OUTSIDE:
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					otl.onTouchUp(event.getActionIndex(), event.getX(event.getActionIndex()),
							event.getY(event.getActionIndex()));
					break;
				case MotionEvent.ACTION_MOVE:
					otl.onTouchMove(event.getActionIndex(), event.getX(event.getActionIndex()),
							event.getY(event.getActionIndex()));
					break;
			}
		}
		return true;
	}

	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		((BaseGame) getContext()).onResize(w, h);
	}
}
