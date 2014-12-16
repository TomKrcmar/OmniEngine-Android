package com.oe.resources;

import android.content.res.AssetManager;

import com.oe.general.LogSystem;
import com.oe.materials.Material;

public class MaterialManager extends ResourceManager<Material>
{
	private static MaterialManager mInstance;
	public static MaterialManager getInstance() {return mInstance;}
	
	public MaterialManager(AssetManager am) {
		super(am);
		if (mInstance != null)
			LogSystem.warning(this, "New instance created after singleton.");
		mInstance = this;
	}
	
	@Override
	protected Material create() {
		return new Material();
	}
}
