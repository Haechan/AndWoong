package com.Appeyroad.andwoong;

import java.io.IOException;
import java.io.InputStream;

import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

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
		//unit pixel, max size  width(x):*800  height(y):*480
		final CGPoint unitPixel = CGPoint.ccp(CCDirector.sharedDirector().displaySize().width/800, CCDirector.sharedDirector().displaySize().height/480);
		//units called by any methods
		CCLabel lbl;
		CCMenu menu;
		CCSprite background, bear, wormwood, garlic, sleepItem;

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
				background.setPosition(CGPoint.ccp(400*unitPixel.x, 240*unitPixel.y));
				background.setScaleX(1.6666666f);
				background.setScaleY(1.25f);
				
				h= new Handler();
		        h.postDelayed(runLoading, 2000);				
			}
			//layer for menu scene
			else if(currentScene==SceneIndex.menu){
				Log.e("MenuScene", "start");
				this.setIsTouchEnabled(true);
				
				background=CCSprite.sprite("bgp2.png");
				addChild(background, 0);
				background.setPosition(CGPoint.ccp(400*unitPixel.x, 240*unitPixel.y));
				background.setScaleX(1.6666666f);
				background.setScaleY(1.25f);
				
				CCMenuItem newGameBtn = CCMenuItemImage.item("new_game.png", "new_game_sel.png");
				CCMenuItem loadGameBtn = CCMenuItemImage.item("loadGame.png", "new_game_sel.png");
				CCMenuItem tutorialBtn = CCMenuItemImage.item("tutorial.png", "tutorial_sel.png");
				CCMenuItem aboutBtn = CCMenuItemImage.item("about.png", "about_sel.png");
				menu = CCMenu.menu(newGameBtn, loadGameBtn, tutorialBtn, aboutBtn);
			    menu.alignItemsVertically();
			    menu.setPosition(400*unitPixel.x, 240*unitPixel.y);
			    addChild(menu, 1);
				
			    //h= new Handler();
				//h.postDelayed(runLoading, 2000);
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

				background=CCSprite.sprite("night_with_letter.png");
				addChild(background, 0);
				background.setPosition(CGPoint.ccp(400*unitPixel.x, 240*unitPixel.y));
				background.setScaleX(1.6666666f);
				background.setScaleY(1.25f);
				
				lbl = CCLabel.makeLabel("Hello World!", "DroidSans", 24);
				addChild(lbl, 1);
				lbl.setPosition(CGPoint.make(400*unitPixel.x, 240*unitPixel.y));
				
				sleepItem=CCSprite.sprite("sleep_item.png");
				addChild(sleepItem, 2);
				sleepItem.setPosition(CGPoint.make(610*unitPixel.x, 450*unitPixel.y));
				sleepItem.setScale(0.58f);
				
				wormwood=CCSprite.sprite("wormwood.png");
				addChild(wormwood, 2);
				wormwood.setPosition(CGPoint.make(680*unitPixel.x, 450*unitPixel.y));
				wormwood.setScale(1);
				
				garlic=CCSprite.sprite("garlic.png");
				addChild(garlic, 2);
				garlic.setPosition(CGPoint.make(750*unitPixel.x, 450*unitPixel.y));
				garlic.setScale(1);
				
				bear=CCSprite.sprite("woong.png");
				addChild(bear, 1);
				//setting bear's position
		        if(setting.getInt(setting.getColumnIndex("lastTime"))==0 && setting.getInt(setting.getColumnIndex("lastTime"))==0){
		        	//bear's position when new game is started
		        	CGPoint startPoint = CGPoint.make(170*unitPixel.x, 130*unitPixel.y);
		        	bear.setPosition(startPoint);
		        }
		        else{
		        	//bear's position when game is re-loaded
		        }
		        //setting bear's action
				//schedule("CaveLogic", 1);
			}
		}

		//change loading scene to menu scene
		//it has function unnecessary later(function : change menu scene to cave scene)
		Runnable runLoading=new Runnable(){
			public void run(){
				if(currentScene==SceneIndex.loading) currentScene=SceneIndex.menu;
				//else if(currentScene==SceneIndex.menu) currentScene=SceneIndex.cave;//remove this later
				CCDirector.sharedDirector().replaceScene(TemplateLayer.scene());
			}
		};
		
		//method called when touch began for each case(scene)
		//usually used when buttons are touched
		@Override
		public boolean ccTouchesBegan(MotionEvent event) {
			CGPoint touchedPoint = CCDirector.sharedDirector().convertToGL(CGPoint.make(event.getX(), event.getY()));
			
			//when touched in menu scene
			if(currentScene==SceneIndex.menu){
				Log.e("menu","clicked");
				
				//when new game is clicked
				if(menu.getSelectedItem().getTag()==0){
					Log.e("menu","new game");
					currentScene=SceneIndex.cave;
					CCDirector.sharedDirector().replaceScene(TemplateLayer.scene());
				}
				//when load game is clicked
				else if(menu.getSelectedItem().getTag()==1){
					Log.e("menu","new game");
				}
				//when tutorial is clicked
				else if(menu.getSelectedItem().getTag()==2){
					Log.e("menu","new game");
				}
				//when about is clicked
				else if(menu.getSelectedItem().getTag()==3){
					Log.e("menu","new game");
				}
				
				/*
				//when new game is clicked
				if(CGRect.containsPoint(menu.getChildByTag(0).getBoundingBox(), touchedPoint)){
					Log.e("menu","new game");
					currentScene=SceneIndex.cave;
					CCDirector.sharedDirector().replaceScene(TemplateLayer.scene());
				}
				//when load game is clicked
				else if(CGRect.containsPoint(menu.getChildByTag(1).getBoundingBox(), touchedPoint)){
					Log.e("menu","new game");
				}
				//when tutorial is clicked
				else if(CGRect.containsPoint(menu.getChildByTag(2).getBoundingBox(), touchedPoint)){
					Log.e("menu","new game");
				}
				//when about is clicked
				else if(CGRect.containsPoint(menu.getChildByTag(3).getBoundingBox(), touchedPoint)){
					Log.e("menu","new game");
				}
				*/
			}
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
				String title = String.format("touch at point(%.2f, %.2f)",
						touchedPoint.x, touchedPoint.y);

				if (lbl != null) {
					lbl.setString(title);
				}
			}
			return CCTouchDispatcher.kEventHandled;			
		}

		//method called when touch moved
		//usually used when something is dragged
		//(dragging happens only in cave scene)
		@Override
		public boolean ccTouchesMoved(MotionEvent event){
			if(currentScene==SceneIndex.cave){
				CGPoint touchedPoint=CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
				//convertToGL float float CGPoint
				if(CGRect.containsPoint(garlic.getBoundingBox(), touchedPoint)) garlic.setPosition(touchedPoint);
				else if(CGRect.containsPoint(wormwood.getBoundingBox(), touchedPoint)) wormwood.setPosition(touchedPoint);
				else if(CGRect.containsPoint(sleepItem.getBoundingBox(), touchedPoint)) sleepItem.setPosition(touchedPoint);
			}

			return CCTouchDispatcher.kEventHandled;
		}
		
		//method called when touch moved
		//usually used when dragging ends
		//(dragging happens only in cave scene)
		@Override
		public boolean ccTouchesEnded(MotionEvent event){
			if(currentScene==SceneIndex.cave){
				CGPoint touchedPoint=CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
				
				if(CGRect.containsPoint(bear.getBoundingBox(), touchedPoint)){
					if(CGRect.containsPoint(sleepItem.getBoundingBox(), touchedPoint)){
						Log.e("bear","sleepItem");
					}           /*animation happens*/
					else if(CGRect.containsPoint(wormwood.getBoundingBox(), touchedPoint)){
						Log.e("bear","wormwood");
					}           /*animation happens*/  
					else if(CGRect.containsPoint(garlic.getBoundingBox(), touchedPoint)){
						Log.e("bear","garlic");
					}           /*animation happens */
				}
				
				sleepItem.setPosition(CGPoint.make(610*unitPixel.x, 450*unitPixel.y));
				wormwood.setPosition(CGPoint.make(680*unitPixel.x, 450*unitPixel.y));
				garlic.setPosition(CGPoint.make(750*unitPixel.x, 450*unitPixel.y));
			}
			
			return CCTouchDispatcher.kEventHandled;
		}
		
		//bear's action
		//logics in cave
		public void CaveLogic(){
			if(bear!=null){
				bear.runAction(CCMoveBy.action(0.2f, CGPoint.ccp(15*unitPixel.x, 0*unitPixel.y)));
				Log.e("bear","move");
			}
		}

	}

}