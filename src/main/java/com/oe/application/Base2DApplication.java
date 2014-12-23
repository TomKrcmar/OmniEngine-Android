package com.oe.application;

import android.app.Activity;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.oe.Core;
import com.oe.materials.Material;
import com.oe.materials.Pass;
import com.oe.rendering.OESurfaceView;
import com.oe.resources.MaterialManager;
import com.oe.resources.ShaderManager;
import com.oe.resources.TextureManager;
import com.oe.scene.Camera;
import com.oe.scene.Scene;
import com.oe.scene.Viewport;

public abstract class Base2DApplication extends Activity implements OESurfaceView.Listener
{
	public static float VIEW_MIN_RES = 1024.0f;

	public static float VIEW_WIDTH = VIEW_MIN_RES;
	public static float VIEW_HEIGHT = VIEW_WIDTH * (2.0f); // 2.0f = aspectRatio
	public static float VIEW_EXTENTS_X = VIEW_WIDTH / 2.0f;
	public static float VIEW_EXTENTS_Y = VIEW_HEIGHT / 2.0f;

	public Core mCore = null;
	public OESurfaceView mSurfaceView = null;
	public int mWidth = 0;
	public int mHeight = 0;

	public Scene mScene;
	public Viewport mViewport;
	public Camera mCamera;

	public TextureManager mTextureMgr;
	public ShaderManager mShaderMgr;
	public MaterialManager mMaterialMgr;

	protected abstract void onInitScene();
	public void onSurfaceTouch(MotionEvent event) {}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCore != null) {
			mCore.destroy();
			mCore = null;
		}
	}
	protected final void initSurfaceView(ViewGroup container) {
		if (mCore == null) {
			mCore = new Core();
			mSurfaceView = mCore.createSurfaceView(this);
			mSurfaceView.mListeners.add(this);
			container.addView(mSurfaceView);
		}
	}

	@Override
	public final void onSurfaceCreated(OESurfaceView target) {
		mScene = mCore.getScene();
		mCamera = new Camera(mScene);
		mViewport = mSurfaceView.createViewport(mCamera);

		mWidth = mSurfaceView.getWidth();
		mHeight = mSurfaceView.getHeight();

		AssetManager am = this.getAssets();
		mTextureMgr = new TextureManager(am);
		mShaderMgr = new ShaderManager(am);
		mMaterialMgr = new MaterialManager(am);
		mTextureMgr.declareAll("Textures");
		mShaderMgr.declareAll("Shaders");
		mMaterialMgr.declareAll("Materials");

		GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		//GLES20.glDepthMask(true);

		onInitScene();
	}

	@Override
	public void onSurfaceChanged(OESurfaceView target, int width, int height) {
		mWidth = Math.max(width, 1);
		mHeight = Math.max(height, 1);
		float aspectRatio = 1.0f;

		if (mWidth <= mHeight) {
			aspectRatio = (float) mHeight / (float) mWidth;
			VIEW_WIDTH = VIEW_MIN_RES;
			VIEW_HEIGHT = VIEW_WIDTH * aspectRatio;
		}
		else {
			aspectRatio = (float) mWidth / (float) mHeight;
			VIEW_HEIGHT = VIEW_MIN_RES;
			VIEW_WIDTH = VIEW_HEIGHT * aspectRatio;
		}

		VIEW_EXTENTS_X = VIEW_WIDTH / 2.0f;
		VIEW_EXTENTS_Y = VIEW_HEIGHT / 2.0f;

		mCamera.setOrtho(-VIEW_EXTENTS_X, VIEW_EXTENTS_X,
				-VIEW_EXTENTS_Y, VIEW_EXTENTS_Y,
				-1.0f, 1.0f);

		//mCamera.setPerspective(45.0f, aspectRatio, 0.1f, 100.0f);
	}

	private Runnable mTouchRunnable = null;
	private MotionEvent mTouchEvent = null;
	@Override
	public final boolean onTouchEvent(MotionEvent event) {
		if (mTouchRunnable == null) {
			mTouchRunnable = new Runnable() {
				@Override public void run() {
					if (mTouchEvent != null)
						onSurfaceTouch(mTouchEvent);
				}
			};
		}
		mTouchEvent = event;
		mSurfaceView.queueEvent(mTouchRunnable);
		return true;
	}

	@Override
	public void onRenderingFrame(OESurfaceView target) {
		mScene.update();
	}
	@Override
	public void onRenderedFrame(OESurfaceView target) {}
}
