package com.oe.resources;

import android.content.res.AssetManager;

import com.oe.general.LogSystem;
import com.oe.materials.Texture;

public class TextureManager extends ResourceManager<Texture>
{
	private static TextureManager mInstance;
	public static TextureManager getInstance() {return mInstance;}
	
	public TextureManager(AssetManager am) {
		super(am);
		if (mInstance != null)
			LogSystem.warning(this, "New instance created after singleton.");
		mInstance = this;
	}
	
	@Override
	protected Texture create() {
		return new Texture();
	}
}
