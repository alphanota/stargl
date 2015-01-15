package com.example.stargl;

public class MathUtils {
	
	float Distance(float x1, float x2, float y1, float y2, float z1, float z2){
		float dx = x2-x1;
		float dy = y2-y1;
		float dz = z2-z1;
	    return (float) Math.sqrt((dx*dx)+ (dy*dy) + (dz*dz));
	}
	
	

}
