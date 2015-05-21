package com.ezardlabs.dethsquare.util;

import android.opengl.GLES20;

import com.ezardlabs.dethsquare.Camera;
import com.ezardlabs.dethsquare.Collider;
import com.ezardlabs.dethsquare.GameObject;
import com.ezardlabs.dethsquare.Renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements android.opengl.GLSurfaceView.Renderer {
	private final BaseGame baseGame;

	public GLRenderer(BaseGame baseGame) {
		this.baseGame = baseGame;
	}

	public void onPause() {
	}

	public void onResume() {
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1);

		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		int vertexShader = Utils.loadShader(GLES20.GL_VERTEX_SHADER, ShaderTools.vs_Image);
		int fragmentShader = Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderTools.fs_Image);

		ShaderTools.sp_Image = GLES20.glCreateProgram();
		GLES20.glAttachShader(ShaderTools.sp_Image, vertexShader);
		GLES20.glAttachShader(ShaderTools.sp_Image, fragmentShader);
		GLES20.glLinkProgram(ShaderTools.sp_Image);

		GLES20.glUseProgram(ShaderTools.sp_Image);

		baseGame.create();

		GameObject.startAll();
		Renderer.init();
		Collider.init();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Utils.onScreenSizeChanged(width, height);
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		baseGame.update();

		Utils.setCameraPosition(Camera.main);

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		baseGame.render();
	}
}
