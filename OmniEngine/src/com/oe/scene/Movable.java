package com.oe.scene;

import com.oe.math.Quaternion;
import com.oe.math.Transform;
import com.oe.math.Vector3;

public class Movable
{
	protected Transform mTransform = new Transform();
	
	public void onTransformChanged() {}

	public final void setPos(Vector3 pos) {
		mTransform.setPos(pos);
		onTransformChanged();
	}
	public final void setPos(float x, float y, float z) {
		mTransform.setPos(x, y, z);
		onTransformChanged();
	}
	public final void setRot(Quaternion rot) {
		mTransform.setRot(rot);
		onTransformChanged();
	}
	public final void setRot(float angle, Vector3 axis) {
		mTransform.setRot(angle, axis);
		onTransformChanged();
	}
	
	public final Vector3 getPos() {
		return mTransform.getPos();
	}
	public final Quaternion getRot() {
		return mTransform.getRot();
	}
	public final Transform getTransform() {
		return mTransform;
	}
	public final float[] getTransformMatrix() {
		return mTransform.getMatrix();
	}
}
