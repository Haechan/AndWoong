package com.Appeyroad.andwoong;

import java.io.IOException;
import java.io.InputStream;

import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	private CCGLSurfaceView mGLSurfaceView;
	static SQLiteDatabase db;
	static Cursor setting;
	enum SceneIndex {loading, menu, intro, cave, outro, gameover, tutorial, option, sound, developer}
	static SceneIndex currentScene;
	static Handler h;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		currentScene=SceneIndex.loading;
		
		SQLiteOpenHelper dbHelper=new DBHelper(this);
		db=dbHelper.getWritableDatabase();//is final ok?
        setting=db.query("woong", null, null, null, null, null, null);
        setting.moveToPosition(0);
        if(setting.getInt(setting.getColumnIndex("startTime"))==0){
        	setting.close();
        	ContentValues newValues=new ContentValues();
        	newValues.put("startTime", System.currentTimeMillis());
    		String[] whereArgs={"0"};//maybe gg
    		db.update("woong", newValues, "startTime = ?", whereArgs);
            setting=db.query("woong", null, null, null, null, null, null);
            setting.moveToPosition(0);
        }
        /*
        Cursor setting1=db.query("woong", null, null, null, null, null, null);
        setting1.moveToPosition(0);
        Log.e("startTime",""+setting1.getInt(setting1.getColumnIndex("startTime")));
        setting1.close();
        */
		
		// set the window status, no tile, full screen and don't sleep
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mGLSurfaceView = new CCGLSurfaceView(this);

		setContentView(mGLSurfaceView);

		// attach the OpenGL view to a window
		CCDirector.sharedDirector().attachInView(mGLSurfaceView);

		// no effect here because device orientation is controlled by manifest
		CCDirector.sharedDirector().setDeviceOrientation(
				CCDirector.kCCDeviceOrientationLandscapeLeft);

		// show FPS
		// set false to disable FPS display, but don't delete fps_images.png!!
		CCDirector.sharedDirector().setDisplayFPS(true);

		// frames per second
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

		currentScene=SceneIndex.loading;
		
		CCScene scene = TemplateLayer.scene();
        
		// Make the Scene active
		CCDirector.sharedDirector().runWithScene(scene);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();

		CCDirector.sharedDirector().pause();
	}

	@Override
	public void onResume() {
		super.onResume();

		CCDirector.sharedDirector().resume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		CCDirector.sharedDirector().end();
		setting.close();
		db.close();
	}

	static class TemplateLayer extends CCLayer {
		//units called by any methods
		CCLabel lbl;
		CCSprite background, bear;

		//method calling scene
		public static CCScene scene() {
			CCScene scene = CCScene.node();
			CCLayer layer = new TemplateLayer();
			
			scene.addChild(layer);

			return scene;
		}

		//method calling layer
		//it will call proper layer by case(by currentScene)
		//and this part has initial settings(something to do when they are first called) for each scenes
		//(z=0 : background)
		//(z>0 : unit)
		protected TemplateLayer() {
			//layer for loading scene
			if(currentScene==SceneIndex.loading){
				Log.e("LoadingScene", "start");
				
				background=CCSprite.sprite("default.png");
				addChild(background, 0);
				background.setPosition(CGPoint.ccp(400, 240));
				
				h= new Handler();
		        h.postDelayed(runLoading, 2000);				
			}
			//layer for menu scene
			else if(currentScene==SceneIndex.menu){
				Log.e("MenuScene", "start");
				
				background=CCSprite.sprite("bgp2.png");
				addChild(background, 0);
				background.setPosition(CGPoint.ccp(400, 240));
				
				h= new Handler();
				h.postDelayed(runLoading, 2000);
			}
			//layer for tutorial scene
			else if(currentScene==SceneIndex.tutorial){}
			//layer for option scene
			else if(currentScene==SceneIndex.option){}
			//layer for about developer scene
			else if(currentScene==SceneIndex.developer){}
			//layer for sound setting scene
			else if(currentScene==SceneIndex.sound){}
			//layer for game over scene
			else if(currentScene==SceneIndex.gameover){}
			//layer for intro scene
			else if(currentScene==SceneIndex.intro){}
			//layer for outro scene
			else if(currentScene==SceneIndex.outro){}
			//layer for cave scene
			//this scene is for play game
			else if(currentScene==SceneIndex.cave){
				Log.e("CaveScene", "start");
				this.setIsTouchEnabled(true);

				lbl = CCLabel.makeLabel("Hello World!", "DroidSans", 24);
				addChild(lbl, 1);
				lbl.setPosition(CGPoint.make(400, 240));
				
				bear=CCSprite.sprite("woong.png");
				addChild(bear, 1);
				//setting bear's position
		        if(setting.getInt(setting.getColumnIndex("lastTime"))==0 && setting.getInt(setting.getColumnIndex("lastTime"))==0){
		        	//bear's position when new game is started
		        	CGPoint startPoint = CGPoint.make(200, 400);
		        	bear.setPosition(startPoint);
		        }
		        else{
		        	//bear's position when game is re-loaded
		        }
		        //setting bear's action
				bear.schedule("CaveLogic", 1);
			}
		}

		//change loading scene to menu scene
		//it has function unnecessary later(function : change menu scene to cave scene)
		Runnable runLoading=new Runnable(){
			public void run(){
				if(currentScene==SceneIndex.loading) currentScene=SceneIndex.menu;
				else if(currentScene==SceneIndex.menu) currentScene=SceneIndex.cave;//remove this later
				CCDirector.sharedDirector().replaceScene(TemplateLayer.scene());
			}
		};
		
		//method called when touch began for each case(scene)
		@Override
		public boolean ccTouchesBegan(MotionEvent event) {
			//when touched in menu scene
			if(currentScene==SceneIndex.menu){}
			//when touched in tutorial scene
			else if(currentScene==SceneIndex.tutorial){}
			//when touched in option scene
			else if(currentScene==SceneIndex.option){}
			//when touched in about developer scene
			else if(currentScene==SceneIndex.developer){}
			//when touched in sound setting scene
			else if(currentScene==SceneIndex.sound){}
			//when touched in game over scene
			else if(currentScene==SceneIndex.gameover){}
			//when touched in intro scene
			else if(currentScene==SceneIndex.intro){}
			//when touched in outro scene
			else if(currentScene==SceneIndex.outro){}
			//when touched in cave scene
			else if(currentScene==SceneIndex.cave){
				CGPoint convertedLocation = CCDirector.sharedDirector().convertToGL(CGPoint.make(event.getX(), event.getY()));

				String title = String.format("touch at point(%.2f, %.2f)",
					convertedLocation.x, convertedLocation.y);

				if (lbl != null) {
					lbl.setString(title);
				}
			}
			return CCTouchDispatcher.kEventHandled;			
		}
		
		//bear's action
		//logics in cave
		public void CaveLogic(){
			if(bear!=null){
				
			}
		}

	}

}