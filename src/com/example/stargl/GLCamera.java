package com.example.stargl;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLU;

public class GLCamera {
	// Position the eye behind the origin.
				final float eyeX = 0.0f;
				final float eyeY = 0.0f;
				final float eyeZ = 2.0f;

				// We are looking toward the distance
				final float lookX = 0.0f;
				final float lookY = 0.0f;
				final float lookZ = -1.0f;

				
				// Set our up vector. This is where our head would be pointing were we holding the camera.
				final float upX = 0.0f;
				final float upY = 1.0f;
				final float upZ = 0.0f;
				
				float zoomfactor = 7.0f;
				
				int width = 1;
				int height = 1;
				float ratio = (float) width / height;
				float left = -(width/2)*ratio * zoomfactor;
				float right = (width/2)*ratio * zoomfactor;
				float bottom = -1.0f * zoomfactor;
				float top = 1.0f * zoomfactor;
				float near = 1f * zoomfactor;
				float far = 10000.0f * zoomfactor;
				
	
	
	
	
	GLCamera(){
		
		
	}
	
	public void adjustView(GL10 gl, int width, int height){
		
		

				// Create a new perspective projection matrix. The height will stay the same
				// while the width will vary as per aspect ratio.
				ratio = (float) width / height;
				left = -ratio * zoomfactor;
				right = ratio * zoomfactor;
				bottom = -1.0f * zoomfactor;
				top = 1.0f * zoomfactor;
				near = 0.01f;
				far = 100.0f;
				this.width = width;
				this.height = height;
				
				
		        gl.glViewport (0, 0, width, height); // Reset The Current Viewport
		        gl.glEnable(GL10.GL_TEXTURE_2D);
		        gl.glEnable(GL10.GL_BLEND);
		        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		        //gl.glClearColor(0, 0, 0, 1);
		        gl.glMatrixMode (GL10.GL_PROJECTION); // Select The Projection Matrix

		        
		        //gl.glTranslatef (0f, -height/2, 0.0f); // move the camera !!
		        
		        //gl.glOrthof (left,right,bottom,top,near,far);
		        
		        //GLU.gluOrtho2D(0.0f, width, height*ratio, 0.0f);
		        
		        gl.glLoadIdentity (); // Reset The Modelview Matrix
		        
		        //gl.glFrustumf(left, right, bottom, top, near, far);
		        //gl.glFrustumf(-ratio, ratio, -1, 1, 1, 4);
		        //GLU.gluPerspective(gl, 10f, ratio, 1f, 100f);
		        gl.glOrthof(left, right, bottom, top, near, far);
		        //gl.glScalef(1.0f, ratio, 1.0f);
		        //gl.glOrthof (left, right, bottom, top, near, far);
		        //GLU.gluOrtho2D(gl, 0, right, 0, top);
		        //Matrix.frustumM(mProjectionMatrix, 0, right, left, bottom/10, top, near, far);
		
		
		
		
	}
	
	
	public void lookAt(GL10 glUnused){
					        
		
		 // Set GL_MODELVIEW transformation mode
	    glUnused.glMatrixMode(GL10.GL_MODELVIEW);
	    glUnused.glLoadIdentity();                      // reset the matrix to its default state

	    // When using GL_MODELVIEW, you must set the camera view
	    
	    GLU.gluLookAt(glUnused, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
		
		
	}
	
	
	public float[] selection(float x, float y){
		
		
		float dx =  x/width;
		float dy = y/height;
		
		float ddx = dx*right*2;
		float ddy = dy*top*2;
		
		float xc = (left + ddx);
		float yc = (top - ddy);
		
		//String s = String.format("%f, %f\n%d, %d\n%f,%f\n%f, %f\n%f, %f\n",x,y,width,height,left,right,ddx,ddy,xc,yc);
		
		//System.out.println(s);
		return new float[]{xc,yc};
		
		
	}
	
	

}
