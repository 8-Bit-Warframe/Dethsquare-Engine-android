package com.ezardlabs.dethsquare.util;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ezardlabs.dethsquare.GameObject;
import com.ezardlabs.dethsquare.R;
import com.ezardlabs.dethsquare.Renderer;
import com.ezardlabs.dethsquare.Screen;

public abstract class BaseGame extends Activity {
	ViewGroup root;
	View gestureOverlay;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (VERSION.SDK_INT >= 14) {
			int visibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
			if (VERSION.SDK_INT >= 16) {
				visibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
						View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
			}
			if (VERSION.SDK_INT >= 19) {
				visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			}
			getWindow().getDecorView().setSystemUiVisibility(visibility);
		}
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);
		root = (ViewGroup) findViewById(R.id.root);
		gestureOverlay = findViewById(R.id.gestures);
		Utils.init(this);
		while (root.getChildCount() > 1) root.removeViewAt(0);
		((ViewGroup) findViewById(R.id.root)).addView(new GameView(this), 0);
	}

	public abstract void create();

	void update() {
		GameObject.updateAll();
	}

	void render() {
		Renderer.renderAll();
	}

	void onResize(int width, int height) {
		Screen.scale = (float) width / 1920f;
		Screen.width = width;
		Screen.height = height;
	}
}
