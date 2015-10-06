package com.ezardlabs.dethsquare.util;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
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

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEGLContextClientVersion(2);
		mRenderer = new GLRenderer((BaseGame) context);
		setRenderer(mRenderer);
	}

	@Override
	public void onPause() {
		super.onPause();
		mRenderer.onPause();
		Utils.pauseAllAudio();
	}

	@Override
	public void onResume() {
		super.onResume();
		mRenderer.onResume();
		Utils.resumeAllAudio();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int index = event.getActionIndex();
		int id = event.getPointerId(event.getActionIndex());
		switch(event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				Input.addTouch(id, event.getX(index), event.getY(index));
				break;
			case MotionEvent.ACTION_OUTSIDE:
			case MotionEvent.ACTION_CANCEL:
				Input.cancelTouch(id, event.getX(index), event.getY(index));
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				Input.removeTouch(id, event.getX(index), event.getY(index));
				break;
			case MotionEvent.ACTION_MOVE:
				Input.moveTouch(id, event.getX(index), event.getY(index));
				break;
		}
		return true;
	}

	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		((BaseGame) getContext()).onResize(w, h);
	}
}
