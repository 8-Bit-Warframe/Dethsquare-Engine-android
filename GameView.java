package com.ezardlabs.dethsquare.util;

import android.content.Context;
import android.view.View;

public class GameView extends View {

	public GameView(Context context) {
		super(context);
	}

	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		((BaseGame) getContext()).onResize(w, h);
	}
}
