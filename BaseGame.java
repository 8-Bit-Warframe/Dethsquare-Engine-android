package com.ezardlabs.dethsquare.util;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.ezardlabs.dethsquare.R;

public abstract class BaseGame extends Activity {
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
		setContentView(new GameView(this));
		gestureOverlay = findViewById(R.id.gestures);
		create();
	}

	public abstract void create();

	public abstract void update();

	public abstract void render();

	public abstract void onResize(int width, int height);
}
