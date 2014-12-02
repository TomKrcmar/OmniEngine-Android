
package com.game;

import com.oe.general.Color;
import com.oe.math.Vector3;
import com.oe.rendering.OESurfaceView;
import com.oe.scene.Camera;
import com.oe.scene.GameObject;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

public class MainActivity extends BaseApplication
{
	/** Viewport background color. */
	public static final Color BG_COLOR = new Color(0.1f, 0.3f, 0.5f);
	
	public TextView mOverlayView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mOverlayView = (TextView) this.findViewById(R.id.overlay);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onSurfaceCreated(OESurfaceView target) {
		super.onSurfaceCreated(target);
		
		mScene = mCore.getScene();
		mCamera = new Camera(mScene);
		mViewport = mSurfaceView.createViewport(mCamera);
		mViewport.setClearColor(BG_COLOR); // Day
		
	//	mCamera.setPos(new Vector3(0.0f, 0.0f, 0.0f)); // Scene origin
		
		GameObject root = mScene.getRoot();
		
		root.addChild(new Sky());
		
		updateOverlay();
	}
	
	public void updateOverlay() {
		runOnUiThread(new Runnable() {
			@Override public void run() {
				mOverlayView.setText("Hello, world!");
			}
		});
	}
	
	@Override
	public void onSurfaceChanged(OESurfaceView target, int width, int height) {
		super.onSurfaceChanged(target, width, height);
	}
	
	private Rect surfaceRect = new Rect();
	@Override
	public void onSurfaceTouch(MotionEvent event) {
		int action = event.getAction();

		mSurfaceView.getGlobalVisibleRect(surfaceRect);
		int x = (int) (event.getX() - surfaceRect.left);
		int y = (int) (event.getY() - surfaceRect.top);
		
		if (action == MotionEvent.ACTION_DOWN ||
			action == MotionEvent.ACTION_MOVE ||
			action == MotionEvent.ACTION_UP) {
			
			Vector3 worldPos = mViewport.unProject(x, y, 0.0f);
			//someObject.setPos(worldPos);
		}
	}
	
	@Override
	public void onRenderingFrame(OESurfaceView target) {
		super.onRenderingFrame(target);
	}
	
	@Override
	public void onRenderedFrame(OESurfaceView target) {
		super.onRenderedFrame(target);
	}
}
