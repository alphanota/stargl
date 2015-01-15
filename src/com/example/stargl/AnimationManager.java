package com.example.stargl;

public class AnimationManager {
	
	//image information
	int GL_TEX_REF;
	int IMAGE_WIDTH_PIX;
	int IMAGE_HEIGHT_PIX;
	int FRAME_WIDTH_PIX;
	int FRAME_HEIGHT_PIX;
	
	int IMAGE_WIDTH_FRAMES;
	int IMAGE_HEIGHT_FRAMES;
	
	float IMAGE_WIDTH_RATIO;
	float IMAGE_HEIGHT_RATIO;
	
	//playback utils
	boolean ANIMATED;
	boolean TIMED;
	boolean LOOPED;
	int DURATION_FRAMES;
	int current_frame;
	
	public void AnimationManagager(int ref, int iwp, 
			           int ihp, int fwp, 
			           int fhp, int df,
			           boolean animated, boolean looped, boolean timed){
		
		init(ref, iwp, 
			          ihp, fwp, 
			           fhp, df,
			           animated,looped, timed);
	}
	
	
	public AnimationManager(int params[]){
		
		init(params[0],params[1],params[2],
				         params[3],params[4],params[5], 
				         (params[6]==1),(params[7]==1),(params[8]==1));
	}
	
	
	
	void init(int ref, int iwp, 
	           int ihp, int fwp, 
	           int fhp, int df,
	           boolean animated, boolean looped, boolean timed){
		
		GL_TEX_REF = ref;
		IMAGE_WIDTH_PIX = iwp;
		IMAGE_HEIGHT_PIX = ihp;
		FRAME_WIDTH_PIX = fwp;
		FRAME_HEIGHT_PIX = fhp;
		DURATION_FRAMES  = df;
		ANIMATED = animated;
		LOOPED = looped;
		current_frame = 0;
		TIMED = timed;
		
		
		IMAGE_WIDTH_FRAMES = IMAGE_WIDTH_PIX/FRAME_WIDTH_PIX;
		IMAGE_HEIGHT_FRAMES = IMAGE_HEIGHT_PIX/FRAME_HEIGHT_PIX;
		
		
		IMAGE_WIDTH_RATIO = (float)FRAME_WIDTH_PIX/(float)IMAGE_WIDTH_PIX;
		IMAGE_HEIGHT_RATIO = (float)FRAME_HEIGHT_PIX/(float)IMAGE_HEIGHT_PIX;
	}
	
	float[] getFrame(){
		
		int cf = current_frame;
		current_frame = current_frame+ 1;
		
		if (cf > DURATION_FRAMES-1){
			if(LOOPED == true) {
				current_frame = 0;
				cf = 0;
			}
			
			
			else return null;
		}
		
		float width_s = (float)FRAME_WIDTH_PIX/(float)IMAGE_WIDTH_PIX;
		float height_s = (float)FRAME_HEIGHT_PIX/(float)IMAGE_HEIGHT_PIX;
		
		
		float[] texData = new float[]{
				 // Mapping coordinates for the vertices
				        0.0f, height_s,     // top left     (V2)
				        0f, 0f,     // bottom left  (V1)
				        width_s, height_s,     // top right    (V4)			
				        width_s, 0      // bottom right (V3)
	
		};
	   
	   if(ANIMATED == false) return texData;
	   float sx =  (cf%IMAGE_WIDTH_FRAMES) * IMAGE_WIDTH_RATIO;
	   float sy = (cf/IMAGE_WIDTH_FRAMES) *  IMAGE_HEIGHT_RATIO;
	   
	   
	   // printf(" [%d, %d, %d] ", i/8, i%8, i);
		                           //y  x    frame
	   
		 for(int i = 0; i < texData.length; i+=2){
		    	float x = texData[i];
		    	float y = texData[i+1];
		    	
		    	texData[i] = x +sx;
		    	texData[i+1] = y - sy;
		    	
		    }
		
		return texData;
	}
	
	
	

}
