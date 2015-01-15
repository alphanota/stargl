package com.example.stargl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.Matrix;
import android.os.SystemClock;

public class GLBaseShape {
	/** Store our model data in a float buffer. */
	FloatBuffer mVertices;
	FloatBuffer mTexBuffer;
	FloatBuffer mColorBuffer;
	float[] mVerticesData;
	float[] positionData;
	float[] texData;
	float[] colorData;
	float[] vel_vector;
    AnimationManager animation;
	
	int texture_reference;
	long time;
	long dtime;
	long xtime;
	long planned_time;
	
	int health;
	int energy;
	
	
	
	Random random;
	
	
	boolean alive;
	boolean running;
	
	public GLBaseShape(int ref){
		initShape(1,1);
		texture_reference = ref;
	}
	
	public GLBaseShape(){
		initShape(1,1);
	}
	
	public GLBaseShape(float x, float y){
		initShape(x,y);
	}
	
	
	public GLBaseShape(float i_x, float i_y, float i_z, float d_x, float d_y, float v_x, float v_y, float v_z){
		initShape(d_x,d_y);
		this.setPosition(i_x, i_y, i_z);
		this.setVelVector(v_x, v_y, v_z);
	}
	
	
	public void reset(float i_x, float i_y, float i_z, float d_x, float d_y, float v_x, float v_y, float v_z, boolean animation_too){
		//initShape(d_x,d_y);
		this.setPosition(i_x, i_y, i_z);
		this.setVelVector(v_x, v_y, v_z);
		if (animation_too = true) resetAnimation();
		
		//set alive = true?
	}
	
	
	public void resetAnimation(){
		if(animation !=null) animation.current_frame = 0;
	}
	
	void initShape(float width, float height){
		//(0-width/2, 0-height-2, 0);
		//(0-width/2, height/2, 0);
		//(0+width/2, 0-height/2);
		//(0+width/2, 0+height/2);
		
mVerticesData = new float[]{
				
				-width/2, -height/2, 0.0f, 
	        	
				-width/2, height/2, 0.0f, 
	        
	             width/2, -height/2, 0.0f, 
	            
	             width/2, height/2, 0.0f 
	    
		};
		
		colorData = new float[]{
				0.0f, 1.0f, 0.0f, 1.0f,
		};		
		
		texData = new float[]{
				 // Mapping coordinates for the vertices
				        0.0f, 1.0f,     // top left     (V2)
				        0.0f, 0.0f,     // bottom left  (V1)
				        1.0f, 1.0f,     // top right    (V4)			
				        1.0f, 0.0f      // bottom right (V3)
	
		};
		
		
		mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
		        .order(ByteOrder.nativeOrder()).asFloatBuffer();
		mVertices.put(mVerticesData).position(0);
		
		
		mColorBuffer = ByteBuffer.allocateDirect(colorData.length*4)
						.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mColorBuffer.put(colorData).position(0);
		
		
		mTexBuffer = ByteBuffer.allocateDirect(texData.length*4)
				     .order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTexBuffer.put(texData).position(0);
		
		positionData = new float[]{0f,0f,0f};
		
		
		vel_vector  = new float[]{0,0,0};
		
		time = SystemClock.uptimeMillis();
		dtime = 0;
		alive = false;
		running = false;
		random = new Random();
		
		health = 100;
		energy = 100;
		xtime = 0;
		setTime(0);
		
		animation = null;
		
	}
	
	
	void setPosition(float x, float y, float z){
		
		positionData[0] = x;
		positionData[1] = y;
		positionData[2] = z;
		
	}
	
	
	void translate(float x, float y, float z){
		positionData[0] += x;
		positionData[1] += y;
		positionData[2] += z;
	}
	
	
	
	
	void setTexRef(int ref){
		texture_reference = ref;
	}
	
	
	/** update position along x,y,z coordinates*/
	void updatePosition(){
		for(int i =0; i < 3; i++)                              //1000
		positionData[i] = positionData[i]+  (vel_vector[i] * dtime/1000);
	}
	
	/** update the baseshape state, inheritor classes must override this method 
	 * to implement additional functionality
	 */
	void update(long millis){
		updatePosition();
		updateTime(millis);
		xtime++;
	}
	
	
	
	void updateTime(long millis){
		
		dtime = millis-time;
		time = millis;
	}
	
	void drawShape(GL10 gl, int handle){
		
		if(animation !=null){
			
				float[] tex = animation.getFrame();
				if(tex == null){
					alive = false;
					return;
				}
				texData = tex;
				this.updateTexBuffer();
				
		 }
			
		
		
		
		
        gl.glPushMatrix();
		
		gl.glTranslatef(positionData[0], 
				        positionData[1],
				        positionData[2]);
		FloatBuffer vBuffer = mVertices;
		vBuffer.position(0);
		
		FloatBuffer tBuffer = mTexBuffer;
		tBuffer.position(0);
		
		// bind the previously generated texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, handle);

        // Point to our buffers
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        // set the colour for the square
        gl.glColor4f (0.0f, 1.0f, 0.0f, 0.5f);

        // Set the face rotation
        gl.glFrontFace(GL10.GL_CW);     

        // Point to our vertex buffer
        gl.glVertexPointer (3, GL10.GL_FLOAT, 0, vBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tBuffer);

        // Draw the vertices as triangle strip
        gl.glDrawArrays (GL10.GL_TRIANGLE_STRIP, 0, mVerticesData.length/3);

        // Disable the client state before leaving
        gl.glDisableClientState (GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glPopMatrix(); //
	}
	
	
	
	void setVelVector(float v[]){
		for(int i = 0; i < vel_vector.length;i++ )
		vel_vector[i] = v[i];
	}
	
	void setVelVector(float x, float y, float z){
		vel_vector[0] = x;
		vel_vector[1] = y;
		vel_vector[2] = z;
	}
	
	
	void setTime(long stime){
		long ttime = stime;
		if (ttime == 0){ 
			ttime =  ( 150 +(random.nextLong()%100));
		}
		
		planned_time = ttime;
		xtime=0;
	}
	
	
	boolean isTime(){
		if(xtime > planned_time) return true;
		else return false;
	}
	
	
	void wakeup(long mill){
		time = mill;
		dtime = 0;
		alive = true;
		setTime(0);
	}
	
	void sleep(){
		time = 0;
		dtime = 0;
		alive = false;
	}
	
	
	
	
	

	void changeCourse(float speed, float x, float y){
		double dx = (x -positionData[0]);
		double dy = (y -positionData[1]);
		double d = Math.sqrt(dx*dx + dy*dy);
		float vel_x = (float) (dx/d);
		float vel_y = (float) (dy/d);
		vel_x = vel_x*speed;
		vel_y = vel_y*speed;
		setVelVector(vel_x,vel_y,0f);
		
	}
	


	void setAnimation(int  params[]){
		animation = new AnimationManager(params);
	}
	
	
	
	void scaleTexPoints(float dx, float dy){
		
		texData = new float[]{
				 // Mapping coordinates for the vertices
				        0.0f, dy,     // top left     (V2)
				        0.0f, 0.0f,     // bottom left  (V1)
				        dx, dy,     // top right    (V4)			
				        dx, 0.0f      // bottom right (V3)
	
		};
		
		
		
		
	}
	
   void shiftTexPoints(float sx, float sy){
		
	   
	   for(int i = 0; i < texData.length; i+=2){
	    	float x = texData[i];
	    	float y = texData[i+1];
	    	
	    	texData[i] = x +sx;
	    	texData[i+1] = y + sy;
	    	
	    	
	    }
		
		
		
		
	}
	
   
   void rotateTex(float angle){
		
	   //x' = xcos(t) - ysin(t)
	   //y' = xsin(t) + ycos(t)
	   
	   shiftTexPoints(-0.5f,-0.5f);
	   
	   double radians  = Math.toRadians(angle);
	   
	   double sin_theta = Math.sin(radians);
	   double cos_theta = Math.cos(radians);
	   
	   for(int i = 0; i < texData.length; i+=2){
	    	float x = texData[i];
	    	float y = texData[i+1];
	    	
	    	double _x =  (x * cos_theta) - (y * sin_theta);
	    	double _y =  (x * sin_theta) + (y * cos_theta);
	    	
	    	texData[i] = (float)_x;
	    	texData[i+1] = (float)_y;
	    	
	    	
	    }
	  
	   
	   shiftTexPoints(0.5f,0.5f);
	   
		
		
	}
   
   
   void updateTexBuffer(){
	   mTexBuffer.clear();
	   mTexBuffer.put(texData).position(0);
   }
	
	
	


}
