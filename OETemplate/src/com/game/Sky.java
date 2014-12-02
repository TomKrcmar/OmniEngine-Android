package com.game;

import com.oe.math.OEMath;
import com.oe.scene.GameObject;

public class Sky extends GameObject
{
	public int timer = 0;
	public int timerMax = 50;
	public float speedAverage = 6.0f;
	public float speedVariance = 2.0f;
	
	public Sky() {
	}
	
	@Override public void onAddedToParent(GameObject lastParent) {}
	@Override public void onDestroy() {}
	@Override public void onUpdate(float timeScale) {
		timer++;
		if (timer > timerMax) {
			timer = 0;
			timerMax = (int)OEMath.linInterp(10, 90, (float) Math.random());
			
			float speed = speedAverage + speedVariance*(float)(Math.random()*2.0-1.0);
			this.addChild(new Cloud(speed));
		}
	}
}
