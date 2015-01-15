package com.example.stargl;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
public class SpriteHolder {
	private Bitmap sprite;
	public int height;
	public int width;
	
	SpriteHolder(View panel, int id){
		Bitmap bit = BitmapFactory.decodeResource(panel.getResources(),
   		     id);
	
	    sprite = bit;
	    height = sprite.getHeight();
	    width = sprite.getWidth();
		
	}
	
	
	public Bitmap getBitmap(){
		return sprite;
	}

}
