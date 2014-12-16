package com.oe.scene;

import java.util.ArrayList;

import com.oe.general.Observable;
import com.oe.rendering.RenderQueue;
import com.oe.rendering.RenderSystem;

public class Scene extends Observable<Scene.Listener>
{
	private RenderSystem mRenderSystem;
	private RenderQueue mRenderQueue;
	
	GameObject mRoot;

	public static final int TARGET_FPS = 60;
	public static final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;  
	private long mLastUpdateTime = System.nanoTime();
	private float mTimeScale = 1.0f;
	private long mUpdateCount = 0;
	
	/*private ArrayList<Listener> mListeners = new ArrayList<Listener>();
	public void addListener(Listener listener) {mListeners.add(listener);}
	public void removeListener(Listener listener) {mListeners.remove(listener);}*/
	public interface Listener {
		public void onUpdating(float timeFactor);
		public void onRenderingViewport(Viewport viewport, Camera camera);
	}
	
	public Scene() {
		mRenderQueue = new RenderQueue();
		mRenderQueue.setRenderSystem(mRenderSystem);
		mRoot = new GameObject();
		mRoot.mScene = this;
	}
	
	private void queueRenderables(GameObject object) {
		if (object.isActive()) {
			object.queueRenderables(mRenderQueue);
			ArrayList<GameObject> list = object.mChildren;
			int size = list.size();
			for (int i = 0; i < size; i++) {
				queueRenderables(list.get(i));
			}
		}
	}
	
	public void renderViewport(Viewport viewport, Camera camera) {
		mRenderSystem.activateViewport(viewport);
		mRenderSystem.setProjectionMatrix(camera.getProjectionMatrix());
		mRenderSystem.setViewMatrix(camera.getViewMatrix());
		
		int size = mListeners.size();
		for (int i = 0; i < size; i++)
			mListeners.get(i).onRenderingViewport(viewport, camera);
		
		mRenderQueue.clearRenderables();
		queueRenderables(mRoot);
		mRenderQueue.renderRenderables();
	}
	
	public void update() {
		long now = System.nanoTime();
		long updateLength = now - mLastUpdateTime;
		mLastUpdateTime = now;
		double delta = (double) updateLength / OPTIMAL_TIME;
		mTimeScale = (float) delta;
		
		if (mUpdateCount < 5) {
			mTimeScale = 1.0f;
		}
		
		int size = mListeners.size();
		for (int i = 0; i < size; i++)
			mListeners.get(i).onUpdating(mTimeScale);
		
		mRoot.update(mTimeScale);
		
		mUpdateCount++;
		
		try {
			Thread.sleep((mLastUpdateTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
		}
		catch(Exception e) {}
	}
	
	public void setRenderSystem(RenderSystem renderSystem) {
		mRenderSystem = renderSystem;
		mRenderQueue.setRenderSystem(mRenderSystem);
	}

	public GameObject getRoot() {
		return mRoot;
	}

	public float getTimeScale() {
		return mTimeScale;
	}
}
