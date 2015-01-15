package com.example.stargl;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends Activity {
	GLSceneView mGLSurfaceView;
	SharedPreferences sharedPref;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		        // Request an OpenGL ES 2.0 compatible context.
	       // mGLSurfaceView.setEGLContextClientVersion(1);
		
		
		sharedPref = getPreferences(Context.MODE_PRIVATE);

		
	    mGLSurfaceView = new GLSceneView(this);
	    setContentView(mGLSurfaceView);
	}

	
	@Override
	protected void onResume()
	{
	    // The activity must call the GL surface view's onResume() on activity onResume().
	    super.onResume();
	    mGLSurfaceView.onResume();
	}
	 
	@Override
	protected void onPause()
	{
	    // The activity must call the GL surface view's onPause() on activity onPause().
	    super.onPause();
	    mGLSurfaceView.onPause();
	}
	

}
