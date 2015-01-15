package com.example.stargl;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import com.montero.stargl.R;


public class GamePauseDialog extends Dialog {
	GLSceneView parent_panel;
    SeekBar seek;
    Button resume;
    
	public GamePauseDialog(Context context, GLSceneView panel) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game_menu);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		parent_panel = panel;
		seek = (SeekBar) findViewById(R.id.options_volume_bar);
		seek.setProgress((int)panel.game_volume*100);
		
		resume = (Button) findViewById(R.id.game_options_resume);
	    resume.setOnClickListener(new View.OnClickListener(){
	    	
	    	
			@Override
	    	public void onClick(View view){
				
				int vol = seek.getProgress();
				
				parent_panel.gamePause(vol);
	    		dismiss();
	    	}    
	    });
		
	}

	
}
