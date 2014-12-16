package com.oe.materials;

import java.util.ArrayList;

import android.content.res.AssetManager;

import com.oe.rendering.BlendMode;
import com.oe.resources.Resource;

public class Material implements Resource
{
	public static class ParamDefs
	{
		public ArrayList<Texture> mTextures;
		public ArrayList<Shader.Uniform> mUniforms;
		public BlendMode mBlendMode;
		
		public ParamDefs() {
			mTextures = new ArrayList<Texture>();
			mUniforms = new ArrayList<Shader.Uniform>();
			mBlendMode = BlendMode.MODULATE;
		}
	}
	
	public static class Pass
	{
		public Shader mShader;
		public ParamDefs mParamDefs;
		
		public Pass() {
			mParamDefs = new ParamDefs();
		}
	}
	
	private ArrayList<Pass> mPasses;
	
	public Material() {
		mPasses = new ArrayList<Pass>();
	}
	
	public ArrayList<Pass> getPasses() {
		return mPasses;
	}

	@Override
	public boolean loadResource(AssetManager am, String filePath) {
		return true;
	}
	@Override
	public boolean unloadResource() {
		return true;
	}
}
