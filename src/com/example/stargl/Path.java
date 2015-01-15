package com.example.stargl;

import java.util.Random;

public class Path{
	
	float  x;
	float  y;
	float c_x;
	float c_y;
	
	float radius;
	
	int time;
	Random rand;
	
	Path(float c_x, float c_y, float radius){
	rand = new Random();
	this.radius = radius;
	this.c_x = c_x;
	this.c_y = c_y;
	reset();
	
	}
	
	void update(){
		
		time--;
	}
	
	void reset(){
		float rad = rand.nextInt ( (int)(radius*1000));
		double angle = rand.nextInt(360) * Math.PI/180;
		
		float rad1  = rad/1000;
		
		float pos_x = rad1 *  (float)Math.cos(angle);
		float pos_y = rad1 *  (float)Math.sin(angle);
		
		
		x = c_x+pos_x;
		y = c_y+pos_y;
		time=1000;
		
		
	}
	
	
	
}