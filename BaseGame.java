package com.ezardlabs.dethsquare.util;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.ezardlabs.dethsquare.GameObject;
import com.ezardlabs.dethsquare.Input;
import com.ezardlabs.dethsquare.R;
import com.ezardlabs.dethsquare.Renderer;
import com.ezardlabs.dethsquare.Screen;
import com.ezardlabs.dethsquare.Time;

public abstract class BaseGame extends Activity {
	private GameView gameView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		gameView = (GameView) findViewById(R.id.root);
		Utils.init(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
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
		gameView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		gameView.onPause();
	}

	public abstract void create();

	void update() {
		Input.update();
		GameObject.updateAll();
		Time.frameCount++;
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
