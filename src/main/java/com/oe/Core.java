package com.oe;

import com.oe.rendering.OESurfaceView;
import com.oe.rendering.RenderSystem;
import com.oe.scene.Scene;

import android.content.Context;

public class Core
{
	private Scene mScene;
	private RenderSystem mRenderSystem;
	
	public Core() {
		mScene = new Scene();
		mRenderSystem = new RenderSystem();
		
		mScene.setRenderSystem(mRenderSystem);
	}
	
	public void destroy() {
		
	}
	
	public OESurfaceView createSurfaceView(Context context) {
		OESurfaceView surfaceView = mRenderSystem.createSurfaceView(context);
		return surfaceView;
	}

	public Scene getScene() {return mScene;}
	public RenderSystem getRenderSystem() {return mRenderSystem;}

	public void setScene(Scene scene) {
		mScene = scene;
	}
	public void setRenderSystem(RenderSystem renderSystem) {
		mRenderSystem = renderSystem;
		mScene.setRenderSystem(renderSystem);
	}
}
