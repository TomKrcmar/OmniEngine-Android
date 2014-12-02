package com.oe.rendering;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.oe.scene.Camera;
import com.oe.scene.Viewport;

public class OESurfaceView extends GLSurfaceView
{
	public interface Listener
	{
		public void onSurfaceCreated(OESurfaceView target);
		public void onSurfaceChanged(OESurfaceView target, int width, int height);
		public void onRenderingFrame(OESurfaceView target);
		public void onRenderedFrame(OESurfaceView target);
	}
	
	private class Renderer implements GLSurfaceView.Renderer
	{
		OESurfaceView mTarget;
		
		public Renderer(OESurfaceView target) {
			mTarget = target;
		}
		
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			for (int i = 0; i < mListeners.size(); i++)
				mListeners.get(i).onSurfaceCreated(mTarget);
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			for (int i = 0; i < mListeners.size(); i++)
				mListeners.get(i).onSurfaceChanged(mTarget, width, height);
		}
		@Override
		public void onDrawFrame(GL10 gl) {
			for (int i = 0; i < mListeners.size(); i++)
				mListeners.get(i).onRenderingFrame(mTarget);
			
			mTarget.render();
			
			for (int i = 0; i < mListeners.size(); i++)
				mListeners.get(i).onRenderedFrame(mTarget);
		}
	}
	
	public ArrayList<Listener> mListeners;
	private ArrayList<Viewport> mViewports;
	
	public OESurfaceView(Context context) {
		super(context);
		Renderer renderer = new Renderer(this);
		this.setEGLContextClientVersion(2);
		this.setRenderer(renderer);
		
		mListeners = new ArrayList<Listener>();
		mViewports = new ArrayList<Viewport>();
	}
	
	public Viewport createViewport(Camera camera) {
		Viewport viewport = new Viewport(this, camera);
		mViewports.add(viewport);
		return viewport;
	}
	
	public void render() {
		int size = mViewports.size();
		for (int i = 0; i < size; i++)
			mViewports.get(i).render();
	}
}
