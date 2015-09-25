package com.ezardlabs.dethsquare.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ezardlabs.dethsquare.AudioSource.AudioClip;
import com.ezardlabs.dethsquare.Camera;
import com.ezardlabs.dethsquare.Screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

public class Utils {
	public static final Platform PLATFORM = Platform.ANDROID;

	public enum Platform {
		ANDROID,
		DEKSTOP
	}

	/**
	 * Only needed for Android
	 */
	private static Context context;
	/**
	 * Used for storing PlayerPrefs on Android
	 */
	private static SharedPreferences prefs;

	/**
	 * Only needed for Android
	 */
	public static void init(Context context) {
		Utils.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
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
			Log.e("", "Texture at " + path + " could not be loaded");
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
							  short[] indices, ShortBuffer indexBuffer) {
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

		GLES20.glDrawElements(0, 0, 0, 0);

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT,
				indexBuffer);

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
			Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, -100f, 0f, 1.0f, 0.0f);
			Matrix.translateM(mtrxView, 0, (int) -camera.transform.position.x * Screen.scale,
					(int) -camera.transform.position.y * Screen.scale, 0);
			Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);
		}
	}

	private static HashMap<AudioClip, MediaPlayer> playingAudio = new HashMap<>();

	public static void playAudio(final AudioClip audioClip) {
		playAudio(audioClip, false);
	}

	public static void playAudio(final AudioClip audioClip, final boolean loop) {
		MediaPlayer player;
		if (playingAudio.containsKey(audioClip)) {
			player = playingAudio.get(audioClip);
			player.stop();
			player.release();
		} else {
			player = new MediaPlayer();
		}
		try {
			AssetFileDescriptor afd = context.getAssets().openFd(audioClip.getPath());
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			player.prepare();
			player.setLooping(loop);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		player.start();
		playingAudio.put(audioClip, player);
	}

	public static void stopAudio(AudioClip audioClip) {
		if (playingAudio.containsKey(audioClip)) {
			playingAudio.remove(audioClip).stop();
		}
	}

	public static void resumeAllAudio() {
		for (MediaPlayer m : playingAudio.values()) {
			m.start();
		}
	}

	public static void pauseAllAudio() {
		for (MediaPlayer m : playingAudio.values()) {
			m.pause();
		}
	}

	public static void setBoolean(String key, boolean value) {
		prefs.edit().putBoolean(key, value).apply();
	}

	public static void setInt(String key, int value) {
		prefs.edit().putInt(key, value).apply();
	}

	public static void setFloat(String key, float value) {
		prefs.edit().putFloat(key, value).apply();
	}

	public static void setString(String key, String value) {
		prefs.edit().putString(key, value).apply();
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		return prefs.getBoolean(key, defaultValue);
	}

	public static int getInt(String key, int defaultValue) {
		return prefs.getInt(key, defaultValue);
	}

	public static float getFloat(String key, float defaultValue) {
		return prefs.getFloat(key, defaultValue);
	}

	public static String getString(String key, String defaultValue) {
		return prefs.getString(key, defaultValue);
	}
}
