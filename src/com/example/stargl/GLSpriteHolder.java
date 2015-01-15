package com.example.stargl;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.view.View;

public class GLSpriteHolder extends SpriteHolder {

	
	private int bitmaps_handles[];
	View panel;
	AnimationManager animator;
	
	private int resourceArrays[];
	GLSpriteHolder(View panel, int id, int resources[]) {
		super(panel, id);
		this.panel = panel;
		
		// TODO Auto-generated constructor stub
		
		resourceArrays = resources;
		
		
		
	}
	
	
	
	public int getImageHandle(int index){
		return bitmaps_handles[index];
	}
	
	public void loadGLTextureArray(GL10 gl){
		bitmaps_handles = new int[resourceArrays.length];
		for(int i = 0; i < bitmaps_handles.length; i++){
			loadGLTexture(gl, resourceArrays[i], i);
		}
				
	}
	
	
	
	
	private void loadGLTexture(GL10 gl, int id, int index){
		Bitmap bitmap = BitmapFactory.decodeResource(panel.getResources(),
	   		     id);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);
	    gl.glGenTextures(1, bitmaps_handles, index);
	    gl.glBindTexture (GL10.GL_TEXTURE_2D, bitmaps_handles[index]);
        gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexEnvf (GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
        gl.glAlphaFunc(GL10.GL_ALPHA, index);
	    
	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	    
	    bitmap.recycle();
	}
	
	
	int loadBitmap(GL10 gl, Bitmap bitmap){
		int array[] = new int[1];
		
		//Generate texture pointers...
	    //GLES20.glGenTextures(1, array, 0);
	    //...and bind it to our array
	    //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, array[0]);
	    //Create Nearest Filtered Texture
	    
		gl.glGenTextures(1, array,0);
	    gl.glBindTexture (GL10.GL_TEXTURE_2D, array[0]);
        gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        //gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        //gl.glTexParameterf (GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        //gl.glTexEnvf (GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        

        //gl.glAlphaFunc(GL10.GL_ALPHA, 0);
	    
	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	   
	    bitmap.recycle();
	    
	   
		return array[0];
	}
	
	
	void releaseTexture(GL10 gl, int length, int execution_block[]){	
		 gl.glDeleteTextures(length, execution_block, 0);
	}
	
	
	
	
	
	int loadResource(String dir, String imagename){
		 Resources resource = panel.getResources();
		 int  resImgId= resource.getIdentifier("kaboom_"+imagename,"drawable", panel.getContext().getPackageName());
		 

		 
		 return resImgId;
	}

	
	
	int[] loadImageArray(String imgDirectory, int length){
		    int imgArray[] = new int[length];
			
			for(int i =0; i < length; i++){
				String s  = ("0000" + i).substring(Integer.toString(i).length());
				imgArray[i] = loadResource(imgDirectory, s);
			}
			
		return imgArray;
	}
	
	
	
	
}
