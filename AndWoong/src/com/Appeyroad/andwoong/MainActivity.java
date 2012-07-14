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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	private CCGLSurfaceView mGLSurfaceView;
	SQLiteDatabase db;
	enum SceneIndex {loading, menu, intro, cave, outro, gameover, tutorial, option, sound, developer}
	static SceneIndex currentScene;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		currentScene=SceneIndex.loading;
		
		SQLiteOpenHelper dbHelper=new DBHelper(this);
		db=dbHelper.getWritableDatabase();//is final ok?
        Cursor setting=db.query("woong", null, null, null, null, null, null);
        setting.moveToPosition(0);
        if(setting.getInt(setting.getColumnIndex("startTime"))==0){
        	setting.close();
        	ContentValues newValues=new ContentValues();
        	newValues.put("startTime", System.currentTimeMillis());
    		String[] whereArgs={"0"};//maybe gg
    		db.update("woong", newValues, "startTime = ?", whereArgs);
        }
        else setting.close();
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
				CCDirector.kCCDeviceOrientationPortrait);

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
		db.close();
	}


	static class TemplateLayer extends CCLayer {
		CCLabel lbl;
		CCSprite background, bear;

		public static CCScene scene() {
			CCScene scene = CCScene.node();
			CCLayer layer = new TemplateLayer();

			scene.addChild(layer);

			return scene;
		}

		protected TemplateLayer() {
			if(currentScene==SceneIndex.loading){
				Log.e("LoadingScene", "start");
				background=CCSprite.sprite("default.png");
				addChild(background, 0);
				background.setPosition(CGPoint.make(240, 400));
				currentScene=SceneIndex.menu;
				CCDirector.sharedDirector().replaceScene(TemplateLayer.scene());
			}
			else if(currentScene==SceneIndex.menu){
				Log.e("MenuScene", "start");
				background=CCSprite.sprite("bgp2.png");
				addChild(background, 0);
				background.setPosition(CGPoint.make(240, 400));
				currentScene=SceneIndex.cave;
				CCDirector.sharedDirector().pushScene(TemplateLayer.scene());
			}
			else if(currentScene==SceneIndex.tutorial){}
			else if(currentScene==SceneIndex.option){}
			else if(currentScene==SceneIndex.developer){}
			else if(currentScene==SceneIndex.sound){}
			else if(currentScene==SceneIndex.gameover){}
			else if(currentScene==SceneIndex.intro){}
			else if(currentScene==SceneIndex.outro){}
			else if(currentScene==SceneIndex.cave){
				Log.e("CaveScene", "start");
				this.setIsTouchEnabled(true);

				lbl = CCLabel.makeLabel("Hello World!", "DroidSans", 24);
				
				addChild(lbl, 0);
				lbl.setPosition(CGPoint.make(240, 400));
				
				bear=CCSprite.sprite("fps_images.png");
				addChild(bear,0);
				bear.setPosition(CGPoint.ccp(120, 600));
				bear.runAction(CCSequence.actions(CCMoveTo.action(0.1f, CGPoint.ccp(140, 150)), CCMoveTo.action(1, CGPoint.ccp(400, 150))));
			}
		}

		@Override
		public boolean ccTouchesBegan(MotionEvent event) {
			CGPoint convertedLocation = CCDirector.sharedDirector().convertToGL(CGPoint.make(event.getX(), event.getY()));

			String title = String.format("touch at point(%.2f, %.2f)",
					convertedLocation.x, convertedLocation.y);

			if (lbl != null) {
				lbl.setString(title);
			}

			return CCTouchDispatcher.kEventHandled;
		}

	}

}