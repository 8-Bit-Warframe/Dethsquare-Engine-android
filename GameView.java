package com.ezardlabs.dethsquare.util;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

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
					otl.onTouchOutside(event.getActionIndex());
					break;
				case MotionEvent.ACTION_CANCEL:
					otl.onTouchCancel(event.getActionIndex());
					break;
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
