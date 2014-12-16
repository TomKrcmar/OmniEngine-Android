package com.oe.general;

import java.util.ArrayList;

public class Observable<T>
{
	protected ArrayList<T> mListeners = new ArrayList<T>();
	public void addListener(T listener) {
		mListeners.add(listener);
	}
	public void removeListener(T listener) {
		mListeners.remove(listener);
	}
}
