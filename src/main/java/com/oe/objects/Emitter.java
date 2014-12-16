package com.oe.objects;

import java.util.Iterator;
import java.util.LinkedList;

import com.oe.math.Vector3;
import com.oe.scene.GameObject;

public class Emitter extends GameObject
{
	public class ChildWrapper {
		public Emitter emitter;
		public GameObject child;
		public Vector3 velocity = new Vector3(0.0f);
		public int lifetime = 0;
		public boolean deleteFlag = false;
		
		public ChildWrapper(Emitter e, GameObject obj) {
			emitter = e;
			child = obj;
		}
		public void update() {
			lifetime++;
			if (lifetime > emitter.mLifespan) {
				deleteFlag = true;
			}
			else {
				Vector3 pos = child.getPos();
				pos.addBy(velocity);
				child.setPos(pos);
				velocity.addBy(emitter.mAcceleration);
				velocity.mulBy(emitter.mFriction);
				emitter.mEmitHandler.update(this);
			}
		}
	}
	public interface EmitHandler {
		public GameObject create();
		public void update(ChildWrapper wrapper);
	}

	/** The lifespan of each particle object emitted. */
	public int mLifespan = 100;
	/** The number of particle objects to emit at each burst. */
	public int mParticlesPerBurst = 1;
	/** The number of frames between bursts. */
	public int mFramesPerBurst = 2;
	/** The min and max speed of particle objects when emitted. */
	public float mSpeed1 = 5.0f, mSpeed2 = 15.0f;
	/** The min and max distance from this emitter of particle objects when emitted. */
	public float mRadius1 = 0.0f, mRadius2 = 0.0f;
	/** The friction of movement for each particle emitted. */
	public float mFriction = 0.98f;
	/** The initial velocity of particle objects when emitted. */
	public Vector3 mInitVelocity = new Vector3(0.0f);
	/** The acceleration of particle objects when emitted. */
	public Vector3 mAcceleration = new Vector3(0.0f);
	/** The offset position from this emitter of particle objects when emitted. */
	public Vector3 mEmitPos = new Vector3(0.0f);
	
	private int mCounter = 0;
	private boolean mDeleteFlag = false;
	
	public EmitHandler mEmitHandler;
	protected LinkedList<ChildWrapper> mWrappers;
	
	public Emitter() {
		mWrappers = new LinkedList<ChildWrapper>();
	}
	
	/**
	 * Schedules for this emitter to be destroyed after all objects it has
	 * created have disappeared. The emitter will no longer emit objects after
	 * being scheduled for deletion.
	 */
	public void scheduleDestroy() {
		mDeleteFlag = true;
	}
	
	/**
	 * Emits one particle object, created through the attached EmitHandler.
	 */
	public void emit() {
		GameObject obj = mEmitHandler.create();
		
		boolean mFollowEmitter = false;
		if (!mFollowEmitter && mParent != null) {
			mScene.getRoot().addChild(obj);
			obj.setPos(this.getTransform(false).getPos());
		}
		else {
			this.addChild(obj);
		}
		ChildWrapper wrapper = new ChildWrapper(this, obj);
		mWrappers.add(wrapper);
		
		float speed = mSpeed1 + (mSpeed2 - mSpeed1) * (float)Math.random();
		float distance = mRadius1 + (mRadius2 - mRadius1) * (float)Math.random();
		
		Vector3 direction;
		int axes = 2;
		if (axes == 2) {
			direction = new Vector3((float)Math.random(),
									(float)Math.random(), 0.5f)
					.mul(2.0f).sub(1.0f).getNormal();
		}
		else {
			direction = new Vector3((float)Math.random(),
									(float)Math.random(),
									(float)Math.random())
					.mul(2.0f).sub(1.0f).getNormal();
		}
		
		Vector3 pos = obj.getPos();
		obj.setPos(pos.add(mEmitPos.add(direction.mul(distance))));
		wrapper.velocity = direction.mul(speed).add(mInitVelocity);
	}
	
	/**
	 * Emits mParticlesPerBurst particle objects by calling emit().
	 */
	public void burst() {
		for (int i = 0; i < mParticlesPerBurst; i++) {
			emit();
		}
	}
	
	@Override
	public void onUpdate(float timeScale) {
		if (mDeleteFlag) {
			if (mWrappers.size() == 0) {
				this.destroy();
			}
		}
		else if (mEmitHandler != null) {
			mCounter++;
			if (mCounter > mFramesPerBurst) {
				mCounter = 0;
				burst();
			}
		}
		Iterator<ChildWrapper> it = mWrappers.iterator();
		while (it.hasNext()) {
			ChildWrapper wrapper = it.next();
			wrapper.update();
			if (wrapper.deleteFlag) {
				wrapper.child.destroy();
				it.remove();
			}
		}
	}
}
