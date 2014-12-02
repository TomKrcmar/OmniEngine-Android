package com.oe.math;

import android.opengl.Matrix;

public class Transform
{
	private float[] mMatrix = new float[16];
	private Vector3 mPosition;
	private Quaternion mRotation;
	
	private Vector4 mAxisAngle;
	
	private boolean mChanged = true;
	
	public Transform() {
		mPosition = new Vector3();
		mRotation = new Quaternion();
		mAxisAngle = new Vector4(0.0f, 0.0f, 1.0f, 0.0f);
	}
	
	public Vector3 getPos() {
		return mPosition;
	}
	public Quaternion getRot() {
		return mRotation;
	}

	public void setPos(Vector3 pos) {
		mPosition.set(pos);
		mChanged = true;
	}
	public void setPos(float x, float y, float z) {
		mPosition.set(x, y, z);
		mChanged = true;
	}
	public void setRot(Quaternion rot) {
		mRotation.set(rot);
		mRotation.getAxisAngle(mAxisAngle);
		mChanged = true;
	}
	public void setRot(float angle, Vector3 axis) {
		mRotation.fromAxisAngle(angle, axis);
		mAxisAngle.set(axis.x, axis.y, axis.z, angle);
		mChanged = true;
	}
	
	private void updateMatrix() {
		Matrix.setIdentityM(mMatrix, 0);
		Matrix.translateM(mMatrix, 0, mPosition.x, mPosition.y, mPosition.z);
		Matrix.rotateM(mMatrix, 0, mAxisAngle.w, mAxisAngle.x, mAxisAngle.y, mAxisAngle.z);
		
		//float[] rot = mRotation.getMatrix();
		//Matrix.multiplyMM(mMatrix, 0, mMatrix, 0, rot, 0);
	}
	
	public static void apply(Transform result, Transform a, Transform b) {
		result.mPosition.set(
			a.mPosition.x + b.mPosition.x,
			a.mPosition.y + b.mPosition.y,
			a.mPosition.z + b.mPosition.z);
		Quaternion.mul(result.mRotation, a.mRotation, b.mRotation);
		result.mChanged = true;
	}
	public float[] getMatrix() {
		if (mChanged) {
			mChanged = false;
			updateMatrix();
		}
		return mMatrix;
	}
}
