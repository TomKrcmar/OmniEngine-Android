package com.oe.scene;

import android.opengl.Matrix;

public class Camera extends GameObject
{
	private Scene mScene;
	
	private float[] mProjectionMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	
	public Camera(Scene scene) {
		mScene = scene;
		Matrix.setIdentityM(mProjectionMatrix, 0);
		Matrix.setIdentityM(mViewMatrix, 0);
	}
	
	public void setPerspective(	float fieldOfView, float aspectRatio,
								float nearClipPlane, float farClipPlane) {
		Matrix.perspectiveM(mProjectionMatrix, 0,
			fieldOfView, aspectRatio, nearClipPlane, farClipPlane);
	}
	public void setOrtho(float left, float right, float bottom, float top, float near, float far) {
		Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}
	
	public void onTransformChanged() {
		super.onTransformChanged();
		Matrix.invertM(mViewMatrix, 0, mWorldTransform.getMatrix(), 0);
	}
	
	public float[] getProjectionMatrix() {
		return mProjectionMatrix;
	}
	public float[] getViewMatrix() {
		return mViewMatrix;
	}
	
	public Scene getScene() {
		return mScene;
	}
}
