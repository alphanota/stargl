package com.example.stargl;

import java.nio.IntBuffer;
import java.util.Random;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import com.montero.stargl.R;





/**
 * 
 * @author Angel Montero
 * @Copyright 2014
 * @version 1.0
 * The main renderer for the game
 *
 */
public class StarRenderer implements GLSurfaceView.Renderer 
{
	/**
	 * resourceArrays will load android bitmap references into memory.
	 * these bitmaps addresses will be passed to a GLSpriteHolder for 
	 * loading into OpenGL
	 */
	int resourceArrays[]; 
	/**
	* non- user adjustable
	* Game Variables, game speed no. of ships etc
	*/
	float friendly_bullet_width = 0.1f;
	float friendly_bullet_height = 0.5f;
	float enemy_bullet_width = 0.2f;
	float enemy_bullet_height = 0.2f;
	
	float friendly_bullet_vel_x = 0.0f;
	float friendly_bullet_vel_y = 30.0f;
	float friendly_bullet_vel_z = 0.0f;
	float enemy_bullet_vel_magnitude = 8.0f;
	float alien_speed_mag = 7.0f;
			
	int friendly_cur_bullet;
	int enemy_cur_bullet;
	int MAX_BULLETS = 20;
	int MAX_ENEMY = 7;
	int STAR_DENSITY = 200;
	
	float game_volume;
	int enemy_time;
	int cur_enemy;
	boolean GAMEOVER;
	boolean running;		
	/**
	** global game time
	**/
	long millis = 0;	
	long game_time = 0;
	long game_start_time = 0;
	/**sets boundaries for resource culling
	* 
	*/
	float VIEW_BOUNDS = 20f;
	GLSceneView parentView;
			
	/**
	 * Our Objects go here...		
	 */
	 int SCORE;
    IntBuffer tex_array;
	int texa[];
	
	/**
	 * Our Sprites go here
	 */
	GLBaseShape fighter;
    GLBaseShape[] aliens;
    GLBaseShape[] bullets;
    GLBaseShape[] enemy_bullets;
    GLBaseShape test_bullet;
    
    GLBaseShape anim_test;
    
    GLBaseShape gui_health;
    GLBaseShape gui_energy;
    GLBaseShape gui_play_pause;
    GLBaseShape gui_base_black;
    
    
    GLBaseShape[] alien_explode_effects;
    GLBaseShape fighter_hit_effect;
    
    GLBaseShape[] score_digits;
    GLBaseShape background;
    
    GLSpriteHolder sprites;
   
    
    GLBaseShape background_stars[];
    
    
    
	GLCamera camera;
	Random game_random;
	
	Path[] alien_paths;
	
	long next_enemy_occurence;
	long GAME_START_GLOBAL;
	
	GameClock clock;
	
	/*end
	 * */
	
	public StarRenderer(GLSceneView v)
	{		
		parentView = v;
		initObjects();
	}
	
	private void calcNextEnemyOccurence(){
		next_enemy_occurence =  game_time +  3000 + game_random.nextLong()%2000;
	}
	
	private void initObjects(){
		resourceArrays = new int[]{
				R.drawable.starfighter_fighter,         //0
				R.drawable.starfighter_enemy,           //1
				R.drawable.flame_long_blue_small,       //2
				R.drawable.flame_red_small,             //3
				R.drawable.ic_9_av_play_over_video,     //4
				R.drawable.ic_9_av_pause_over_video,    //5
				R.drawable.gui_health_glow,                  //6
				R.drawable.gui_energy_glow,                  //7
				R.drawable.gui_play_centered,           //8
				R.drawable.gui_pause_centered,          //9
				R.drawable.ascii_sheet,                  //10
				R.drawable.gui_base_black,                //11
				R.drawable.asteroid_sprite,               //12
				R.drawable.kaboom_hit,                   //13
				R.drawable.kaboom_strip,                  //14
				R.drawable.star,                           //15
		};
		
		
	    game_random = new Random();
	    calcNextEnemyOccurence();
	    friendly_cur_bullet = 0;
	    enemy_cur_bullet = 0;
	    tex_array =  IntBuffer.allocate(1);
        texa = new int[1];     
        camera = new GLCamera();
        sprites = new GLSpriteHolder(parentView,R.drawable.starfighter_enemy, resourceArrays);
        fighter = new GLBaseShape();
        fighter.setPosition(0,(camera.bottom)+1,0);
        test_bullet = new GLBaseShape();
        bullets = new GLBaseShape[MAX_BULLETS];
        for(int i =0; i < MAX_BULLETS; i++){
            bullets[i] = new GLBaseShape(-1000, -1000, -1000, 
        			                      friendly_bullet_width, friendly_bullet_height, 
        			                      friendly_bullet_vel_x, friendly_bullet_vel_y, 
        			                      friendly_bullet_vel_z);
        } 
        enemy_bullets = new GLBaseShape[MAX_BULLETS];
        for(int i =0; i < MAX_BULLETS; i++){
            enemy_bullets[i] = new GLBaseShape(-1000, -1000, -1000, 
        			                      enemy_bullet_width, enemy_bullet_height, 
        			                      0, 0, 
        			                      0);
        }
         
        aliens = new GLBaseShape[MAX_ENEMY];
        alien_paths = new Path[20];
        for(int i =0; i < MAX_ENEMY; i++){
            aliens[i] = new GLBaseShape(-1000, -1000, -1000, 
        			                      1f,1f, 
        			                      0, 0, 
        			                      0);
        }
        anim_test = new GLBaseShape(5f,5f);
        anim_test.setPosition(0,0,0); 
        alien_explode_effects = new GLBaseShape[MAX_ENEMY];
        /*
         * 
         * 
         * int ref, int iwp, 
	           int ihp, int fwp, 
	           int fhp, int df,
	           boolean animated, boolean looped, boolean timed
         * 
         */
        int kaboom_params[] = new int[]{
        		0,2048,1024,256,256,32,1,0,0
        };
        
        for(int i =0; i < MAX_ENEMY; i++){
            alien_explode_effects[i] = new GLBaseShape(-1000, -1000, -1000, 
        			                      2f,2f, 
        			                      0, 0, 
        			                      0);
            
            
            alien_explode_effects[i].setAnimation(kaboom_params);
         } 
         gui_health = new GLBaseShape(2.0f,2.0f);
         gui_energy = new GLBaseShape(2.0f,2.0f);
         gui_play_pause = new GLBaseShape(2.0f,2.0f);
         gui_base_black = new GLBaseShape(2.0f,2.0f);
         
         
         SCORE = 0;
         
         score_digits = new GLBaseShape[6];
        
         float score_l = 2.0f/6.0f;
         
         for(int i = 0; i < 6; i++){
             score_digits[i] = new GLBaseShape(score_l,score_l);
        	 score_digits[i].scaleTexPoints(1f/9f, 1f/4f);
         }
         
         fighter_hit_effect = new GLBaseShape(3f,3f);
         int hit_params[] = new int[]{0, 256,256,
                 256,256, 5,
                 0, 0, 1};

         fighter_hit_effect.setAnimation(hit_params);

      
         background_stars = new GLBaseShape[STAR_DENSITY];
         
         for(int i =0; i < STAR_DENSITY; i++){
             float x = game_random.nextFloat()/10f+ 0.0002f;
             //float y = game_random.nextFloat()/10f+ 0.0002f;
             float boundsy = camera.bottom + (camera.top*2 *game_random.nextFloat()) + game_random.nextFloat();
             float boundsx = -6 + x*100f;
        	 background_stars[i] = new GLBaseShape(x,x);
             background_stars[i].positionData = new float[]{boundsx,boundsy,-1f};
             background_stars[i].setVelVector(0f, -0.6f, 0f);
         }
         
         millis = SystemClock.uptimeMillis();
         game_time = millis;
         GAME_START_GLOBAL = millis;
         
         
	}
	
	void fire(GLBaseShape ammo[],int index, float i_x, float i_y, float d_x, float d_y, float v_x, float v_y){
	    ammo[index].reset(i_x, i_y, 0, d_x, d_y, v_x, v_y, 0,false);
	    ammo[index].wakeup(game_time);	
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config){ 
	    // Load the texture for the square
	    sprites.loadGLTextureArray(gl);
	
	    System.out.println("creating surface");
		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		game_start_time = millis;
		GAME_START_GLOBAL = game_time;
	    
		
	}	
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height){
	    camera.adjustView(gl, width, height);
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		
		 millis = SystemClock.uptimeMillis(); 
		 //game_time = GAME_START_GLOBAL + millis - game_start_time;
		 //game_time = SystemClock.currentThreadTimeMillis();
		 //game_time = clock.getTime();
		 
		 //millis++;
		 glUnused.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		 camera.lookAt(glUnused);
		 cullResources();
		 checkEnemyCollisions();
	     checkFriendlyCollisions();
		 fighter.update(game_time);
         updateEnemy();
         drawBackground(glUnused);
		 fighter.drawShape(glUnused,sprites.getImageHandle(0));
         drawEnemy(glUnused);
         drawBullets(glUnused);
         updateFighter();
         drawGUI(glUnused);
         drawEffects(glUnused);
		 
	}
	
	private void drawBullets(GL10 gl){	
	    for(int i =0; i < MAX_BULLETS; i++){
	        if(!bullets[i].alive) continue;
		    bullets[i].update(game_time);
		    bullets[i].drawShape(gl, sprites.getImageHandle(2));
		}
	    
		for(int i =0; i < MAX_BULLETS; i++){	
			if(!enemy_bullets[i].alive) continue;
		    enemy_bullets[i].update(game_time);
			enemy_bullets[i].drawShape(gl, sprites.getImageHandle(3));
		}
	}
	
	private void playSound(int n){
	    parentView.playSound(n);
	}
	
	public void friendlyFire(){	
		//float mx = 0.0f;
		//float my = 1.0f;
		if(fighter.energy < 10){
		    fighter.energy = -50;
		    return;
		}
		fighter.energy-=10;
		playSound(parentView.starfighter_friendly_fire_sound);
		
		fire(bullets,friendly_cur_bullet,fighter.positionData[0]-0.25f,fighter.positionData[1]+0.75f, 
		     friendly_bullet_width, friendly_bullet_height, friendly_bullet_vel_x,
		     friendly_bullet_vel_y);
		
		friendly_cur_bullet = (friendly_cur_bullet+1)%MAX_BULLETS;
		
		fire(bullets,friendly_cur_bullet,fighter.positionData[0]+0.25f,fighter.positionData[1]+0.75f, 
			     friendly_bullet_width, friendly_bullet_height, friendly_bullet_vel_x,
			     friendly_bullet_vel_y);
		
		friendly_cur_bullet = (friendly_cur_bullet+1)%MAX_BULLETS;
	}
	
	
	void updateFighter(){
		
	    float px = fighter.positionData[0];
	    if( px < camera.left +0.5){
	    	stopFighter();
	    	fighter.positionData[0] = camera.left+0.501f;
	    	
	    }
	    else if (px > camera.right - 0.5 ){
	    	stopFighter();
	    	fighter.positionData[0] = camera.right-0.501f;
		}
	    
		if(fighter.energy < 100) fighter.energy++;
	}
	
	void moveFighter(int x, int y){	
		float[] dcoord = camera.selection(x,y);
		float dir = 1f;
		float speed = 12f;
		float px = fighter.positionData[0];
		float dx = dcoord[0] - px; 
		if(dx < 0) {
			dx = dx*-1;
			
			dir = -1;
		}
		if(dx < 1) return;
		fighter.setVelVector(dir*speed, 0, 0);
	}
	
	void stopFighter(){
		fighter.setVelVector(0,0,0);	
	}
	
	void wakeEnemy(GLBaseShape enemies[], int index ){
	    float screen_ratio = camera.ratio*2;
		float start_y = camera.eyeY + camera.top +2;
		float bit = screen_ratio/100f;
		float chosen_bit =(float)game_random.nextInt(100);
		float x_1 = (camera.left)+   chosen_bit*bit;
		float start_x = x_1; 
		float angle1 = game_random.nextInt(15) - start_x*2;
		float angle2 = game_random.nextInt(15) + start_x*2;
		float angle3 =  (270f + angle1-angle2) * (float)(Math.PI/180);
		double vel_x = alien_speed_mag * Math.cos(angle3);
		double vel_y = alien_speed_mag * Math.sin(angle3);
		float vx = (float)vel_x;
		float vy = (float)vel_y;
		alien_paths[index] = new Path(0,3,10f);
		enemies[index].reset(start_x, start_y, 0, 1, 1, vx, vy, 0,false);
		enemies[index].changeCourse(alien_speed_mag,alien_paths[index].x,alien_paths[index].y);
		enemies[index].wakeup(game_time);
		enemies[index].update(game_time);
	}
	
	void updateEnemy(){   
 	    if(game_time > next_enemy_occurence){
 		    for(int i =0; i < MAX_ENEMY;i++){
 			    if(aliens[i].alive == false){
 				    wakeEnemy(aliens,i);
 				    calcNextEnemyOccurence();
 				    break;
 			    }
 		    }
 	    }
 	   for(int i = 0; i < MAX_ENEMY; i++){
 		    if( aliens[i] ==null) continue;
 	        if(!aliens[i].alive) continue;
 	        alien_paths[i].update();
 	        updateAI(aliens[i], i);
 	        aliens[i].update(game_time);
 	       
 	   }
    }
	
	void updateAI(GLBaseShape path, int index){
		float pos_x = path.positionData[0];
		float pos_y = path.positionData[1];
		if(pos_x < camera.left){
			path.positionData[0] = camera.right;
			alien_paths[index].reset();
			path.changeCourse(alien_speed_mag,alien_paths[index].x,alien_paths[index].y);
		}
		else if(pos_x > camera.right){
			path.positionData[0] = camera.left;
			alien_paths[index].reset();
			path.changeCourse(alien_speed_mag,alien_paths[index].x,alien_paths[index].y);
		}
 	    if(path.isTime()){
 	    if ((path.positionData[1] > -3)){
 	    	   enemyFire(path);	
 	       }
 	       path.setTime(0);
 	   }
 	  for(int i = 0; i < MAX_BULLETS; i++){
 	      if (bullets[i].alive){
 	          if(withinBounds(path,bullets[i],3)){ // nasty near a bullet?
 	              float bullet_x = bullets[i].positionData[0];
 	              float nasty_x = path.positionData[0];
 	              float v = 1.0f;
 	   	          //adjust our nasty's velocity to avoid the bullet 
 	              if(nasty_x < bullet_x){
 	                  v = v * -1.0f;	
 	              }
 	              path.changeCourse(alien_speed_mag+1 , 30*v, pos_y);
 	              path.running =true;
 	          } 
 	          
 	          
 	          
 	          float dx = pos_x - alien_paths[index].x;
		      float dy = pos_y - alien_paths[index].y;
		      if(dx < 0) dx*=-1;
		      if(dy < 0) dy*=-1;
		      boolean reached = true;
		      if(dx > 1.5) reached = false;
		      if(dy > 1.5) reached = false;
		      //if(alien_paths[index].time < 0) reached = false;
		      if(reached == true){
		          alien_paths[index].reset();
	              path.changeCourse(alien_speed_mag,alien_paths[index].x,alien_paths[index].y);
		      }
 	        } 
 	      }
 	}//updateAI()
	
	void enemyFire(GLBaseShape alien){
		
		//get target
		playSound(parentView.starfighter_enemy_fire_sound);
		float[] target = fighter.positionData;
		float[] source = alien.positionData;
		float speed = enemy_bullet_vel_magnitude;
	    double dx = (target[0] - source[0]);
		double dy = (target[1] - source[1] );
		double d = Math.sqrt(dx*dx + dy*dy);
		float vel_x = (float) (dx/d);
		float vel_y = (float) (dy/d);
		vel_x = vel_x*speed;
		vel_y = vel_y*speed;
		
		
		fire(enemy_bullets,enemy_cur_bullet,source[0],source[1], 
			     enemy_bullet_width, enemy_bullet_height, vel_x,
			     vel_y);
		
		enemy_cur_bullet = (enemy_cur_bullet+1)%MAX_BULLETS;
	}
	
	
	void drawEnemy(GL10 gl){
		for(int i = 0; i < MAX_ENEMY; i++){
	 		  if( aliens[i] ==null) continue;
	 	      if(!aliens[i].alive) continue;
	 	       aliens[i].drawShape(gl, sprites.getImageHandle(1));   
	 	  }
	}
	
	void cullResources(){
		checkShapeBounds(bullets, VIEW_BOUNDS);
		checkShapeBounds(enemy_bullets, VIEW_BOUNDS);
		checkShapeBounds(aliens, VIEW_BOUNDS);
	}
	
	void checkShapeBounds(GLBaseShape[] shapes, float radius){
		int LEN = shapes.length;
		
		for(int i =0; i < LEN; i++){
			if (!withinBounds(shapes[i],test_bullet,radius)){
				shapes[i].alive=false;
			}
		}	
	}
	
	boolean withinBounds(GLBaseShape shape, GLBaseShape center_shape, float radius){
		boolean visible = true;
		
		
		float[] shapecoords = shape.positionData;
		float[] centercoords = center_shape.positionData;
		
		for(int i =0; i < 3; i++){
			
			float component_distance = shapecoords[i] - centercoords[i];
			if(component_distance < 0) component_distance *= -1;
			
			if(component_distance > radius) {
				visible = false;
				break;
			}
			
			
			
		}
		
		return visible;
	}
	
	
	
	void checkEnemyCollisions(){
		for(int i = 0; i < MAX_ENEMY; i++){
			if(aliens[i].alive == false) continue;
			for(int j = 0; j < MAX_BULLETS; j++){
				if(bullets[j].alive ==false) continue;
				if(withinBounds(aliens[i],bullets[j],0.5f)){
					
					aliens[i].alive=false;
					bullets[j].alive=false;
					SCORE+=10;
					
					alien_explode_effects[i].positionData = aliens[i].positionData;
					alien_explode_effects[i].alive = true;
					alien_explode_effects[i].resetAnimation();
					
			    }
			}	
		}
	}
	
	
	
	void checkFriendlyCollisions(){
		for(int i = 0; i < MAX_BULLETS; i++){
			if(enemy_bullets[i].alive == false) continue;
			if(withinBounds(fighter,enemy_bullets[i],0.5f)){
				fighter.health-=5;
				enemy_bullets[i].sleep();
				enemy_bullets[i].setPosition(100,100 , 100);
				enemy_bullets[i].setVelVector(0, 0, 0);
				fighter_hit_effect.alive = true;
				fighter_hit_effect.resetAnimation();
				gameOver();
		    }
		}
	}
	
	
	
	void drawGUI(GL10 gl){
		
		int health_handle = sprites.getImageHandle(6); //static tex,
		int energy_handle = sprites.getImageHandle(7); //static tex,
		sprites.getImageHandle(8);
		int pause_handle = sprites.getImageHandle(9); //static tex,
		int base_handle = sprites.getImageHandle(11); // base
		
		float gui_pos_x = camera.left + 1;
		
		gui_health.setPosition(gui_pos_x, camera.top-1, 0.5f);
		gui_energy.setPosition(gui_pos_x, camera.top-1, 0.5f);
		gui_play_pause.setPosition(gui_pos_x, camera.top-1, 0.5f);
		gui_base_black.setPosition(gui_pos_x, camera.top-1, 0.5f);
		
		float h_percent = ((float)fighter.health / 100f);
		float h_angle  =  180 - (180 * h_percent);
		
		float e_percent = ((float)fighter.energy / 100f);
		float e_angle = 180 - (180 * e_percent);
		
		gui_health.rotateTex(h_angle);
		gui_energy.rotateTex(e_angle);
		
		gui_health.updateTexBuffer();
		gui_energy.updateTexBuffer();
		
		gui_health.drawShape(gl, health_handle);
		gui_energy.drawShape(gl, energy_handle);
		gui_play_pause.drawShape(gl, pause_handle);
		gui_base_black.drawShape(gl, base_handle);
		
		gui_health.rotateTex(-h_angle);
		gui_energy.rotateTex(-e_angle);
		
		gui_health.updateTexBuffer();
		gui_energy.updateTexBuffer();
		
		//score
		float init_x = gui_health.positionData[0]-0.8f;
		float init_y = gui_health.positionData[1]-0.6f;
		float l = 2.0f/6.0f;
		
		String score = getScore(SCORE); 
		
		int text_handler = sprites.getImageHandle(10);
		
		float ratio = 1f/9f;
		
        for(int i = 0; i < 6; i++){
       	 score_digits[i].reset(init_x + (l*i), init_y, 0.6f, 1, 1, 0, 0, 0, false);
       	 	int dig =  (int)score.charAt(i) - 48;
       	    if(dig == 9){
       	    	score_digits[i].shiftTexPoints(0, 1f/4f);
       	    	score_digits[i].updateTexBuffer();
       	    	score_digits[i].drawShape(gl, text_handler);
       	    	score_digits[i].shiftTexPoints(0, -1f/4f);
       	    	score_digits[i].updateTexBuffer();
       	    	
       	    }else {
       	    	score_digits[i].shiftTexPoints(ratio * dig, 0);
       	    	score_digits[i].updateTexBuffer();
       	    	score_digits[i].drawShape(gl, text_handler);
       	    	score_digits[i].shiftTexPoints(-ratio * dig, 0);
       	    	score_digits[i].updateTexBuffer();
       	    }
        }
		/*
		 * delete the dynamically generated textures to make room for the 
		 * next iterations, will help fix memory loss issue?
		 */
	}
	
	
	public static String getScore(int score){
		String s  = ("000000" + score).substring(Integer.toString(score).length());
		return s;
	}
	
	void drawBackground(GL10 gl){

		int star_handler = sprites.getImageHandle(15);
		
		
		for(int i =0; i < STAR_DENSITY; i++){
            background_stars[i].update(game_time);
            if(background_stars[i].positionData[1] < (camera.bottom)){
            	float d_y = camera.bottom - background_stars[i].positionData[1];
            	background_stars[i].positionData[1] = camera.top - d_y;
            }
            background_stars[i].drawShape(gl, star_handler);
        }
		
	}
	
	void drawEffects(GL10 gl){	
		

		int fighter_hit = sprites.getImageHandle(13);
		int alien_hit = sprites.getImageHandle(14);
		
		
 		if(fighter_hit_effect.alive == true){
 			
 			fighter_hit_effect.positionData = fighter.positionData;
 			
 			drawEffectShape(gl, fighter_hit_effect,fighter_hit);
 		}
		
 		
 		
		for(int i =0; i < MAX_ENEMY;i++){
			drawEffectShape(gl, alien_explode_effects[i],alien_hit);
		}
		
		
	}
	
	void drawEffectShape(GL10 gl, GLBaseShape shape, int frame){
		if (shape.alive == false) return;
		shape.drawShape(gl, frame);
		
	}
	
	void putEffect(GLBaseShape[] effects, GLSpriteHolder anim_sprite, int index, float x, float y, float z, boolean animated){
		
		effects[index].reset(x, y, z, 1, 1, 0, 0, 0, false);
		effects[index].alive = true;
	
		effects[index].animation.ANIMATED = animated;
		
	}
	
	
	boolean guiHit(int _x, int _y){
		
		
		float[] dcoord = camera.selection((float)_x,(float)_y);
		
		boolean hit = true;
		
		float px = gui_play_pause.positionData[0] - dcoord[0];
		float py =  gui_play_pause.positionData[1] - dcoord[1];
		
		if ( (px > 1) || (py > 1) ) hit = false;
		
		
		
	    return hit;
	}
	
	void gameOver(){
		if(fighter.health < 1){
			parentView.onGameOver(this, SCORE);
			//initObjects
		}
	}
	
	
	void reset(){
		this.initObjects();
	}
	
	
}