package com.oe.resources;

import java.util.HashMap;

import android.content.res.AssetManager;

import com.oe.general.LogSystem;

public abstract class ResourceManager<T extends Resource>
{
	private class ResourceDeclaration
	{
		public T resource;
		public String name;
		public String filePath;
	}
	
	private HashMap<String, ResourceDeclaration> mResourceMap;
	private AssetManager mAssetManager;
	
	protected ResourceManager(AssetManager am) {
		mResourceMap = new HashMap<String, ResourceDeclaration>();
		mAssetManager = am;
	}
	protected abstract T create();
	
	public void declare(String name, String filePath) {
		ResourceDeclaration res = mResourceMap.get(name);
		if (res == null) {
			res = new ResourceDeclaration();
			res.resource = null;
			res.name = name;
			res.filePath = filePath;
			mResourceMap.put(name, res);
		}
		else {
			res.filePath = filePath;
			LogSystem.warning(this, "Resource "+name+" location overwritten.");
		}
	}
	public void declare(String filePath) {
		String name = filePath;
		declare(name, filePath);
	}
	public void declareUnmanaged(String name, T resource) {
		declare(name, null);
		ResourceDeclaration res = mResourceMap.get(name);
		if (res != null) {
			res.resource = resource;
		}
	}
	public boolean contains(String name) {
		ResourceDeclaration res = mResourceMap.get(name);
		return (res != null);
	}
	public T retrieve(String name) {
		ResourceDeclaration res = mResourceMap.get(name);
		if (res != null) {
			return res.resource;
		}
		return null;
	}
	public T getLoaded(String name) {
		ResourceDeclaration res = mResourceMap.get(name);
		if (res != null) {
			load(res);
			return res.resource;
		}
		return null;
	}
	public void load(String name) {
		ResourceDeclaration res = mResourceMap.get(name);
		if (res != null) {
			load(res);
		}
	}
	public void unload(String name) {
		ResourceDeclaration res = mResourceMap.get(name);
		if (res != null) {
			unload(res);
		}
	}
	protected void load(ResourceDeclaration res) {
		if (res != null) {
			if (res.resource == null && res.filePath != null) {
				res.resource = create();
				boolean loaded = res.resource.loadResource(mAssetManager, res.filePath);
				if (!loaded)
					LogSystem.warning(this, "Resource '"+res.name+"' not loaded properly.");
			}
		}
	}
	protected void unload(ResourceDeclaration res) {
		if (res != null) {
			if (res.resource != null && res.filePath != null) {
				boolean unloaded = res.resource.unloadResource();
				res.resource = null;
				if (!unloaded)
					LogSystem.warning(this, "Resource '"+res.name+"' not unloaded properly.");
			}
		}
	}
}
