package com.oe.scene;

import java.util.ArrayList;

import com.oe.math.Transform;
import com.oe.rendering.RenderQueue;

public class GameObject extends Movable
{
	public interface Visitor {
		public void visit(GameObject object);
	}
	
	public interface Behavior {
		public void onAddedToParent(GameObject lastParent);
		public void onUpdate(float timeScale);
		public void onDestroy();
	}
	
	protected Scene mScene;
	protected GameObject mParent;
	protected ArrayList<GameObject> mChildren;
	protected boolean mMarkForDeletion = false;
	protected Transform mWorldTransform;
	protected Behavior mBehavior = null;
	protected boolean mActive = true;
	
	public GameObject() {
		mScene = null;
		mParent = null;
		mChildren = new ArrayList<GameObject>();
		mWorldTransform = new Transform();
	}
	
	public void setBehavior(Behavior behavior) {
		mBehavior = behavior;
	}
	
	public boolean isActive() {
		return mActive;
	}
	public void setActive(boolean active) {
		mActive = active;
	}
	
	public void onAddedToParent(GameObject lastParent) {}
	public void onUpdate(float timeScale) {}
	public void onDestroy() {}
	
	public void visitSubtree(Visitor visitor) {
		visitor.visit(this);
		int size = mChildren.size();
		for (int i = 0; i < size; i++) {
			GameObject child = mChildren.get(i);
			child.visitSubtree(visitor);
		}
	}
	
	private void setScene(Scene scene) {
		mScene = scene;
		int size = mChildren.size();
		for (int i = 0; i < size; i++) {
			mChildren.get(i).setScene(scene);
		}
	}
	
	public final GameObject addChild(GameObject object) {
		GameObject lastParent = object.mParent;
		
		if (lastParent != null)
			lastParent.removeChild(object);
		
		mChildren.add(object);
		object.mParent = this;
		
		object.setScene(mScene);
		
		object.onAddedToParent(lastParent);
		if (object.mBehavior != null)
			object.mBehavior.onAddedToParent(lastParent);
		
		return object;
	}
	private final void removeChild(GameObject child) {
		boolean removed = false;
		int size = mChildren.size();
		for (int i = 0; i < size; i++) {
			if (mChildren.get(i) == child) {
				removed = true;
				mChildren.remove(i);
				size = mChildren.size();
				i--;
			}
		}
		if (removed) {
			child.setScene(null);
			child.mParent = null;
		}
	}
	public void destroyAllChildren() {
		while (mChildren.size() > 0) {
			mChildren.get(0)._destroy();
		}
	}
	private final void _destroy() {
		onDestroy();
		if (mBehavior != null)
			mBehavior.onDestroy();
		
		if (mParent != null)
			mParent.removeChild(this);
	}
	public final void destroy() {
		mMarkForDeletion = true;
	}
	public boolean markedForDeletion() {
		return mMarkForDeletion;
	}
	
	public final void update(float timeScale) {
		if (mActive) {
			onUpdate(timeScale);
			if (mBehavior != null)
				mBehavior.onUpdate(timeScale);
			
			int size = mChildren.size();
			for (int i = 0; i < size; i++) {
				GameObject child = mChildren.get(i);
				
				child.update(timeScale);
				
				if (child.markedForDeletion()) {
					child._destroy();
					size = mChildren.size();
					i--;
				}
			}
		}
	}
	
	public void queueRenderables(RenderQueue renderQueue) {
		
	}
	
	@Override
	public void onTransformChanged() {
		if (mParent != null) {
			Transform.apply(mWorldTransform,
					mParent.mWorldTransform,
					mTransform);
		}
		else {
			mWorldTransform = mTransform;
		}
		
		int size = mChildren.size();
		for (int i = 0; i < size; i++) {
			mChildren.get(i).onTransformChanged();
		}
	}
	
	public Transform getTransform(boolean local) {
		return local ? mTransform : mWorldTransform;
	}
}
