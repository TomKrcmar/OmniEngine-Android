package com.oe.resources;

import android.content.res.AssetManager;

public interface Resource
{
	public boolean loadResource(AssetManager am, String filePath);
	public boolean unloadResource();
}
