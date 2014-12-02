package com.oe.resources;

import android.content.res.AssetManager;

import com.oe.general.LogSystem;
import com.oe.materials.Shader;

public class ShaderManager extends ResourceManager<Shader>
{
	private static ShaderManager mInstance;
	public static ShaderManager getInstance() {return mInstance;}
	
	public ShaderManager(AssetManager am) {
		super(am);
		if (mInstance != null)
			LogSystem.warning(this, "New instance created after singleton.");
		mInstance = this;
	}
	
	@Override
	protected Shader create() {
		return new Shader();
	}
}
