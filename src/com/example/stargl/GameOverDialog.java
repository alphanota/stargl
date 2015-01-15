package com.example.stargl;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.montero.stargl.R;

public class GameOverDialog extends Dialog {
	GLSceneView parent_panel;
    Button replay;
    
	public GameOverDialog(Context context, GLSceneView panel, int score, int high_score) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game_over_menu);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		parent_panel = panel;
		
		TextView scoreView = (TextView) findViewById(R.id.score_text);
		scoreView.setText("SCORE\n"+score);
		
		TextView highScoreView = (TextView) findViewById(R.id.high_score_text);
		highScoreView.setText("HIGH SCORE\n"+high_score);
		
		
		
		
		replay = (Button) findViewById(R.id.game_options_replay);
	    replay.setOnClickListener(new View.OnClickListener(){
	    	
	    	
			@Override
	    	public void onClick(View view){
				parent_panel.onGameReplay();
				dismiss();
	    	}    
	    });
		
	}


}
