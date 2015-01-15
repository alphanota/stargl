package com.example.stargl;

import android.os.SystemClock;

public class GameClock extends Thread {

	private long time;
	private long CLOCK_START;
	private long init_start;
	private boolean running;
	
	
	GameClock(){
		CLOCK_START = SystemClock.uptimeMillis();
		init_start = CLOCK_START;
		running = true;
	}
	
	
	public void run(){
		
		while(running){
			time = CLOCK_START +SystemClock.uptimeMillis()  - init_start;
		}
	}
	
	
	
	public void clockPause(){
		//running = false;
	}
	 
	
	public void clockResume(){
		running = true;
		init_start = SystemClock.uptimeMillis();
		//start();
	}
	
	public long getTime(){
		return time;
	}
}
