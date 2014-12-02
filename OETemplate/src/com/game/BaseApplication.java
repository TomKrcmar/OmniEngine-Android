
package com.game;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.oe.Core;
import com.oe.materials.Material;
import com.oe.materials.Shader;
import com.oe.rendering.OESurfaceView;
import com.oe.rendering.VertexAttribute;
import com.oe.resources.MaterialManager;
import com.oe.resources.ResourceManager;
import com.oe.resources.ShaderManager;
import com.oe.resources.TextureManager;
import com.oe.scene.Camera;
import com.oe.scene.Scene;
import com.oe.scene.Viewport;

import android.app.Activity;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.LinearLayout;

public class BaseApplication extends Activity implements OESurfaceView.Listener
{
	public static float VIEW_MIN_RES = 1024.0f;
	
	public static float VIEW_WIDTH = VIEW_MIN_RES;
	public static float VIEW_HEIGHT = VIEW_WIDTH * (2.0f); // 2.0f = aspectRatio
	public static float VIEW_EXTENTS_X = VIEW_WIDTH / 2.0f;
	public static float VIEW_EXTENTS_Y = VIEW_HEIGHT / 2.0f;
	
	public Core mCore = null;
	public OESurfaceView mSurfaceView;
	public int mWidth = 0;
	public int mHeight = 0;
	
	public Scene mScene;
	public Viewport mViewport;
	public Camera mCamera;
	
	public TextureManager mTextureMgr;
	public ShaderManager mShaderMgr;
	public MaterialManager mMaterialMgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_ad);
		
		LinearLayout container = (LinearLayout) this.findViewById(R.id.surfaceContainer);
		
		mCore = new Core();
		mSurfaceView = mCore.createSurfaceView(this);
		mSurfaceView.mListeners.add(this);
		container.addView(mSurfaceView);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mCore.destroy();
		mCore = null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final MotionEvent e = event;
		mSurfaceView.queueEvent(new Runnable() {
			@Override public void run() {
				onSurfaceTouch(e);
			}
		});
		return true;
	}
	
	public void onSurfaceTouch(MotionEvent event) {
		
	}
	
	@Override
	public void onSurfaceCreated(OESurfaceView target) {
		mWidth = mSurfaceView.getWidth();
		mHeight = mSurfaceView.getHeight();
		
		AssetManager am = this.getAssets();
		mTextureMgr = new TextureManager(am);
		mShaderMgr = new ShaderManager(am);
		mMaterialMgr = new MaterialManager(am);
		
		try {
			String[] folders = new String[] {
				"textures",
				"shaders"
			};
			ResourceManager<?>[] managers = new ResourceManager<?>[] {
				mTextureMgr,
				mShaderMgr
			};
			
			for (int i = 0; i < folders.length; i++) {
				String[] resources = am.list(folders[i]);
				for (int j = 0; j < resources.length; j++) {
					String path = resources[j];
					
					String name = path;
					if (name.indexOf(".") > 0)
						name = name.substring(0, name.lastIndexOf("."));
					
					managers[i].declare(name, folders[i]+File.separator+path);
				}
			}
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Material mtl = new Material();
		Material.Pass pass = new Material.Pass();
		mtl.getPasses().add(pass);
		
		mMaterialMgr.declareUnmanaged("Sprite", mtl);
		
		pass.mParamDefs.mTextures.add(mTextureMgr.getLoaded("DefaultSprite"));
		
		Shader shdr = new Shader();
		pass.mShader = shdr;
		
		try {
			String vertPath = "shaders/sources/SpriteL.vert";
			String fragPath = "shaders/sources/SpriteL.frag";
			
			String vertSrc = "";
			Scanner scanner = new Scanner(am.open(vertPath));
			while (scanner.hasNextLine())
				vertSrc += scanner.nextLine()+"\n";
			scanner.close();
			shdr.compileSource(Shader.Type.VERTEX, vertSrc);
			
			String fragSrc = "";
			scanner = new Scanner(am.open(fragPath));
			while (scanner.hasNextLine())
				fragSrc += scanner.nextLine()+"\n";
			scanner.close();
			shdr.compileSource(Shader.Type.FRAGMENT, fragSrc);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		shdr.generateAndAttach();
		
		shdr.mapAttribute("vertPosition", VertexAttribute.Preset.POSITION);
		shdr.mapAttribute("vertTexCoord", VertexAttribute.Preset.TEXCOORD);
		shdr.mapAttribute("vertColor", VertexAttribute.Preset.COLOR);
		
		shdr.link();
		
		shdr.mapUniform("mvpMatrix", Shader.Uniform.Preset.MVP_MATRIX);
		shdr.mapUniform("diffuseSampler", Shader.Uniform.Type.INT, Integer.valueOf(0));
		
		GLES20.glEnable(GLES20.GL_BLEND);
		
		//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		//GLES20.glDepthMask(true);
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
	
	@Override
	public void onRenderingFrame(OESurfaceView target) {
		mScene.update();
	}
	
	@Override
	public void onRenderedFrame(OESurfaceView target) {
	}
}
