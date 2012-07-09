package com.Appeyroad.andwoong;

import java.io.IOException;
import java.io.InputStream;

import org.cocos2d.events.TouchDispatcher;
import org.cocos2d.layers.Layer;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Label;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCRect;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	private CCGLSurfaceView mGLSurfaceView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the window status, no tile, full screen and don't sleep
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mGLSurfaceView = new CCGLSurfaceView(this);

		setContentView(mGLSurfaceView);

		// attach the OpenGL view to a window
		Director.sharedDirector().attachInView(mGLSurfaceView);

		// no effect here because device orientation is controlled by manifest
		Director.sharedDirector().setDeviceOrientation(
				Director.CCDeviceOrientationPortrait);

		// show FPS
		// set false to disable FPS display, but don't delete fps_images.png!!
		Director.sharedDirector().setDisplayFPS(true);

		// frames per second
		Director.sharedDirector().setAnimationInterval(1.0f / 60);

		Scene scene = TemplateLayer.scene();

		// Make the Scene active
		Director.sharedDirector().runWithScene(scene);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();

		Director.sharedDirector().pause();
	}

	@Override
	public void onResume() {
		super.onResume();

		Director.sharedDirector().resume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Director.sharedDirector().end();
	}


	static class TemplateLayer extends Layer {
		Label lbl;

		public static Scene scene() {
			Scene scene = Scene.node();
			Layer layer = new TemplateLayer();

			scene.addChild(layer);

			return scene;
		}

		protected TemplateLayer() {

			this.setIsTouchEnabled(true);

			lbl = Label.label("Hello World!", "DroidSans", 24);

			addChild(lbl, 0);
			lbl.setPosition(160, 240);
		}

		@Override
		public boolean ccTouchesBegan(MotionEvent event) {
			CCPoint convertedLocation = Director.sharedDirector()
					.convertToGL(CCPoint.make(event.getX(), event.getY()).x,CCPoint.make(event.getX(), event.getY()).y);

			String title = String.format("touch at point(%.2f, %.2f)",
					convertedLocation.x, convertedLocation.y);

			if (lbl != null) {
				lbl.setString(title);
			}

			return TouchDispatcher.kEventHandled;
		}

	}

}