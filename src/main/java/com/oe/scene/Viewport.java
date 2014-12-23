package com.oe.scene;

import android.opengl.Matrix;

import com.oe.rendering.OESurfaceView;
import com.oe.general.Color;
import com.oe.math.OEMath;
import com.oe.math.Rectangle;
import com.oe.math.Vector3;

public class Viewport
{
	private OESurfaceView mRenderTarget;
	private Camera mCamera;
	
	private Color mClearColor;

	private Rectangle mScreenRect;
	
	public Viewport(OESurfaceView target, Camera camera) {
		mRenderTarget = target;
		mCamera = camera;
		mClearColor = Color.BLACK;
		mScreenRect = new Rectangle(0.0f, 0.0f, 1.0f, 1.0f);
		getScreenRect(mScreenRect);
	}
	
	/**
	 * Renders the scene to this viewport through the attached camera.
	 * <p>
	 * Uses the scene to which the camera is attached.
	 * </p>
	 */
	public void render() {
		mCamera.getScene().renderViewport(this, mCamera);
	}
	
	/**
	 * Maps a given world space point to screen space coordinates.
	 * 
	 * @param world A point in world space.
	 * @return The world position mapped to screen space coordinates.
	 */
	public Vector3 project(Vector3 world) {
		float[] view = mCamera.getViewMatrix();
		float[] proj = mCamera.getProjectionMatrix();
		float[] trans = new float[16];
		float[] worldVec = new float[] {world.x, world.y, world.z, 1.0f};
		float[] screenVec = new float[4];
		
		Matrix.multiplyMM(trans, 0, proj, 0, view, 0); // Pre-multiply matrices
		Matrix.multiplyMV(screenVec, 0, trans, 0, worldVec, 0); // World space to normalized space
		Vector3 screen = new Vector3(screenVec[0], screenVec[1], screenVec[2]);
		screen.x /= screenVec[3];
		screen.y /= screenVec[3];
		screen.z /= screenVec[3];
		
		// Normalized space to viewport space
		getScreenRect(mScreenRect);
		float xmin = (float) mScreenRect.x;
		float ymin = (float) mScreenRect.y;
		float xmax = xmin + (float) mScreenRect.width;
		float ymax = ymin + (float) mScreenRect.height;
		screen.y = -screen.y;
		screen.x = OEMath.linInterp(xmin, xmax, screen.x * 0.5f + 0.5f);
		screen.y = OEMath.linInterp(ymin, ymax, screen.y * 0.5f + 0.5f);
		screen.z = screen.z * 0.5f + 0.5f;
		return screen;
	}

	/**
	 * Maps the given screen space coordinates to a world space point.
	 * 
	 * @param screen Screen space coordinates.
	 * @return The screen coordinates mapped to a world space point.
	 */
	public Vector3 unProject(Vector3 screen) {
		// Viewport space to normalized space.
		getScreenRect(mScreenRect);
		float xmin = (float) mScreenRect.x;
		float ymin = (float) mScreenRect.y;
		float xmax = xmin + (float) mScreenRect.width;
		float ymax = ymin + (float) mScreenRect.height;
		screen.x = OEMath.linInterpInv(xmin, xmax, screen.x) * 2.0f - 1.0f;
		screen.y = OEMath.linInterpInv(ymin, ymax, screen.y) * 2.0f - 1.0f;
		screen.z = screen.z * 2.0f - 1.0f;
		screen.y = -screen.y;
		
		float[] proj = mCamera.getProjectionMatrix().clone();
		float[] view = mCamera.getViewMatrix().clone();
		// float[] trans = new float[16];
		float[] worldVec = new float[4];
		float[] screenVec = new float[] {screen.x, screen.y, screen.z, 1.0f};
		
		Matrix.invertM(proj, 0, proj, 0);
		Matrix.invertM(view, 0, view, 0);
		
		Matrix.multiplyMV(worldVec, 0, proj, 0, screenVec, 0); // Normalized space to camera space
		Matrix.multiplyMV(worldVec, 0, view, 0, worldVec, 0); // Camera space to world space
		
		//Matrix.multiplyMM(trans, 0, proj, 0, view, 0); // Pre-multiply matrices
		//Matrix.multiplyMV(worldVec, 0, trans, 0, screenVec, 0); // Normalized space to world space
		
		Vector3 world = new Vector3(worldVec[0], worldVec[1], worldVec[2]);
		return world;
	}

	/**
	 * Maps the given screen space coordinates to a world space point.
	 * <p>
	 * Equivalent to:<br />
	 * <code>unProject(new Vector3(screenX, screenY, 0.0f))</code>
	 * </p>
	 * @param screenX Screen space x coordinate.
	 * @param screenY Screen space y coordinate.
	 * @return The screen coordinates mapped to a world space point.
	 */
	public Vector3 unProject(int screenX, int screenY, float resultDepth) {
		Vector3 result = unProject(new Vector3(screenX, screenY, 0.0f));
		result.z = resultDepth;
		return result;
	}
	
	/**
	 * Computes the screen-space rectangle this viewport occupies inside
	 * it's parent RenderTarget, and stores it in result.
	 */
	public void getScreenRect(Rectangle result) {
		int w = mRenderTarget.getWidth();
		int h = mRenderTarget.getHeight();
		result.set(0, 0, w, h);
	}
	
	/**
	 * Returns the last known screen-space rectangle this viewport
	 * occupies inside it's parent RenderTarget.
	 * @return The screen-space rectangle.
	 */
	public Rectangle getScreenRect() {
		return mScreenRect;
	}
	
	public Color getClearColor() {
		return mClearColor;
	}
	
	public void setClearColor(Color color) {
		mClearColor = color;
	}
}
