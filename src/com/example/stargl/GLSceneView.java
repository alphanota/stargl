package com.example.stargl;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import com.montero.stargl.R;



public class GLSceneView extends GLSurfaceView {

	private StarRenderer renderer;
	  private SoundPool SoundFX;
	    int starfighter_enemy_fire_sound;
	    int starfighter_friendly_fire_sound;
	    float game_volume;
	    boolean is_game_paused;
	    SharedPreferences sharedPref;
	    SharedPreferences.Editor editor;
	  
	    int HIGH_SCORE;
	    
	    
	    GamePauseDialog pauseDialog;
	    
	    Handler mHandler;
	
	public GLSceneView(Context context) {
		super(context);
		
		
		mHandler = new Handler(Looper.getMainLooper() ) {
			
			
			@Override
			public void handleMessage(Message inputMessage){
				
				is_game_paused = true;
				int score  = (int) inputMessage.arg1;
				
				
				int defaultValue = 10;
				int highScore = sharedPref.getInt(getResources().getString(R.string.saved_high_score), defaultValue);
				if (score > highScore){
					editor.putInt(getResources().getString(R.string.saved_high_score), score);
					   editor.commit();
				}
				
				
				
				
				GameOverDialog gpd = new GameOverDialog(getContext(),GLSceneView.this,score,highScore);
				gpd.setCancelable(false);
				gpd.show();
				onPause(); 		
				
			}
			
			
		};
		
		
		
		renderer = new StarRenderer(this);
		setRenderer(renderer);
		init();
		
		
		
		
		
		// TODO Auto-generated constructor stub
	}
	
	
	void init(){
          
		
	   sharedPref = getContext().getSharedPreferences("HIGH_SCORES", Context.MODE_PRIVATE);
	   editor = sharedPref.edit();
	   
		
		is_game_paused = false;
		game_volume = 1.0f;
		 SoundFX = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
	        starfighter_enemy_fire_sound = 
	        		SoundFX.load(getContext(), R.raw.starfighter_enemy_fire_sound, 1);
	        
	        starfighter_friendly_fire_sound = 
	        		SoundFX.load(getContext(), R.raw.starfighter_friendly_fire_sound, 1);
	}
	
	
	void playSound(int n){
    	SoundFX.play(n, game_volume, game_volume, 1, 0, 1);
    }
	
	@SuppressWarnings("static-access")
		@Override
	    public boolean onTouchEvent(MotionEvent event) {
	    	int _x = (int) event.getX();
	    	int _y = (int) event.getY();
	    	
	    	if (event.getAction() == event.ACTION_DOWN){
	    		
	    		if(renderer.guiHit(_x,_y) == true){
	    	    	is_game_paused = true;
	    			GamePauseDialog gpd = new GamePauseDialog(getContext(),this);
	    			gpd.setCancelable(false);
	    			gpd.show();
	    	    	
	    			this.onPause();
	    	    	
	    	    	return true;
	    	    }
	    	    
	    		renderer.moveFighter(_x, _y);
	    		renderer.friendlyFire();
	    	}
	    	
	    	
	    	if(event.getAction() == event.ACTION_UP){
	    	    renderer.stopFighter();
	    	}
	        return true;
	        
	    
	    }
	
	
	  void gamePause(int vol){
		  
		  is_game_paused = false;
		  game_volume = (float)vol/100;
		  this.onResume();
	  }
	
	  @Override
	  public void onResume(){
		  
		  if(is_game_paused == false) super.onResume();
		  
	  }
	  
	  public void onGameOver(StarRenderer renderer, int SCORE){
	     Message gameOverMessage = mHandler.obtainMessage(SCORE,renderer);
	     gameOverMessage.arg1 = SCORE;
	     gameOverMessage.sendToTarget();
		  		  
	  }
	  
	  void onGameReplay(){
		 //renderer = new StarRenderer(this);
		 //setRenderer(renderer);
		  renderer.reset();
		  gamePause(100); 
	  }

}
