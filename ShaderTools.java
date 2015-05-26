package com.ezardlabs.dethsquare.util;

public class ShaderTools {
	/**
	 * The ID of the shader program for images
	 */
	public static int sp_Image;

	/**
	 * Vertex shader for images
	 */
	public static final String vs_Image = "uniform mat4 uMVPMatrix;" +
			"attribute vec4 vPosition;" +
			"attribute vec2 a_texCoord;" +
			"varying vec2 v_texCoord;" +
			"void main() {" +
			"	gl_Position = uMVPMatrix * vPosition;" +
			"	v_texCoord = a_texCoord;" +
			"}";

	/**
	 * Fragment shader for images
	 */
	public static final String fs_Image = "precision mediump float;" +
			"varying vec2 v_texCoord;" +
			"uniform sampler2D s_texture;" +
			"void main() {" +
			"	gl_FragColor = texture2D(s_texture, v_texCoord);" +
			"}";
}
