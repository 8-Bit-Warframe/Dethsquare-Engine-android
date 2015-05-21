package com.ezardlabs.dethsquare.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.ezardlabs.dethsquare.Camera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Utils {
	public static final Platform PLATFORM = Platform.ANDROID;

	public enum Platform {
		ANDROID,
		DEKSTOP
	}

	private static Context context;

	/**
	 * Only needed for Android
	 */
	public static void init(Context context) {
		Utils.context = context;
	}

	/**
	 * Loads the image at the given path, then binds it to an OpenGL 2D texture
	 *
	 * @param path The location of the image to load
	 * @return The OpenGL texture name of the loaded image, the width of the loaded image, and
	 * the height of the loaded image
	 */
	public static int[] loadImage(String path) {
		int[] returnVals = new int[3];

		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeStream(context.getAssets().open(path));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int[] textureNames = new int[1];
		GLES20.glGenTextures(1, textureNames, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureNames[0]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
				GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
				GLES20.GL_NEAREST);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		returnVals[0] = textureNames[0];
		if (bmp != null) {
			returnVals[1] = bmp.getWidth();
			returnVals[2] = bmp.getHeight();
			bmp.recycle();
		}
		return returnVals;
	}

	/**
	 * Opens a connection to a local file
	 *
	 * @param path The path of the file to read
	 * @return A {@link BufferedReader} for the specified file
	 * @throws IOException If the file cannot be opened for some reason (e.g. doesn't exist)
	 */
	public static BufferedReader getReader(String path) throws IOException {
		return new BufferedReader(
				new InputStreamReader(context.getApplicationContext().getAssets().open(path)));
	}

	/**
	 * Loads an OpenGL shader
	 *
	 * @param type       The type of shader - either GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
	 * @param shaderCode The source code of the shader
	 * @return The shader's ID
	 */
	public static int loadShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);

		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	private static int vPositionLoc;
	private static int texCoordLoc;
	private static int mtrxLoc;
	private static int textureLoc;

	private static boolean inited = false;

	public static void render(int textureName, FloatBuffer vertexBuffer, FloatBuffer uvBuffer,
			short[] indices, ShortBuffer drawListBuffer) {
		if (!inited) {
			vPositionLoc = GLES20.glGetAttribLocation(ShaderTools.sp_Image, "vPosition");
			texCoordLoc = GLES20.glGetAttribLocation(ShaderTools.sp_Image, "a_texCoord");
			mtrxLoc = GLES20.glGetUniformLocation(ShaderTools.sp_Image, "uMVPMatrix");
			textureLoc = GLES20.glGetUniformLocation(ShaderTools.sp_Image, "s_texture");
			inited = true;
		}

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);

		GLES20.glVertexAttribPointer(vPositionLoc, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
		GLES20.glEnableVertexAttribArray(vPositionLoc);

		GLES20.glVertexAttribPointer(texCoordLoc, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);
		GLES20.glEnableVertexAttribArray(texCoordLoc);

		GLES20.glUniformMatrix4fv(mtrxLoc, 1, false, mtrxProjectionAndView, 0);

		GLES20.glUniform1i(textureLoc, 0);

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT,
				drawListBuffer);

		GLES20.glDisableVertexAttribArray(vPositionLoc);
		GLES20.glDisableVertexAttribArray(texCoordLoc);
	}

	private static final float[] mtrxProjection = new float[16];
	private static final float[] mtrxView = new float[16];
	private static final float[] mtrxProjectionAndView = new float[16];

	public static void onScreenSizeChanged(int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		for (int i = 0; i < 16; i++) {
			mtrxProjection[i] = 0.0f;
			mtrxView[i] = 0.0f;
			mtrxProjectionAndView[i] = 0.0f;
		}

		// Setup our screen width and height for normal sprite translation.
		Matrix.orthoM(mtrxProjection, 0, 0f, width, height, 0.0f, 0, 50);

		// Set the camera position (view matrix)
		Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);

		Camera.main.bounds.set(0, 0, width, height);
	}

	public static void setCameraPosition(Camera camera) {
		if (camera != null) {
			Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
			Matrix.translateM(mtrxView, 0, (int) -camera.transform.position.x,
					(int) -camera.transform.position.y, 0);
			Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);
		}
	}
}
