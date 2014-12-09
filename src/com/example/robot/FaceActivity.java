package com.example.robot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FaceActivity extends SurfaceView implements SurfaceHolder. Callback, Runnable {//OnGestureListener, OnTouchListener 

	private int strokeWidth;
	private double startX;
	private double mScrWidth;
	private double mPicWidth;
	private double startY;
	private double mScrHeight;
	private double mPicHeight;
	private double controlX;
	private double controlY;
	private double controlY2;
	
	private double endX;
	private double endY;
	private Path path1;
	private Path path2;
	
	private Paint paintPath;
	private Bitmap imagel;

	private Paint paintPic;
	private Canvas canvas;

	private Paint paintEye;
	private Path eye;
	private double eyelx;
	private double eyely;
	private double eyerx;
	private double eyery;

	private int a;

	private SurfaceHolder holder;

	public FaceActivity(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.getHolder().addCallback(this);
		holder = this.getHolder();

		imagel = BitmapFactory. decodeResource( getResources( ) , R. drawable.body );

		DisplayMetrics num = getResources(). getDisplayMetrics( );
		mScrWidth = num. widthPixels;    // 屏幕宽
		mScrHeight = num. heightPixels;    //屏幕高
		mPicWidth = imagel. getWidth( );    //图片宽
		mPicHeight = imagel. getHeight( );    //图片高

		/**
		 * 控制嘴的高度
		 */
		controlY = (mPicHeight/1.42);
		controlY2 = (mPicHeight/1.28);
		a = -8;

		eyelx = (mPicWidth/4.77);
		eyely = (mPicHeight/3.95);
		eyerx = (mPicWidth/1.57);
		eyery = (mPicHeight/3.95);
	}

	public void surfaceCreated( SurfaceHolder holder ) {
		// TODO Auto-generated method stub
		new Thread(new MyThread()).start();
	}

	// 自定义线程类
	class MyThread implements Runnable {

		@Override
		public void run() {
			while(true){
				try {
					canvas = holder. lockCanvas();

					canvas.drawColor(Color. BLUE);

					imagel = BitmapFactory. decodeResource( getResources( ) , R. drawable. body );

					DisplayMetrics num = getResources(). getDisplayMetrics( );
					mScrWidth = num. widthPixels;    // 屏幕宽
					mScrHeight = num. heightPixels;    //屏幕高
					mPicWidth = imagel. getWidth( );    //图片宽
					mPicHeight = imagel. getHeight( );    //图片高
					
					paintPic = new Paint( );
					canvas. drawBitmap( imagel , 0 , 0 , paintPic );
					
					//mouth
					setPoint1();
					
					DrawPath1( canvas );


					if(controlY> (mPicHeight/1.42) && controlY <= (mPicHeight/1.28)){
						controlY = controlY + a;
						controlY2 = controlY2 + a;
					}else {
						if(controlY <= (mPicHeight/1.42)){
							eyelx = (mPicWidth/4.77);
							eyely = (mPicHeight/3.95);
							eyerx = (mPicWidth/1.57);
							eyery = (mPicHeight/3.95);
						}
						a = -a;
						controlY = controlY + a;
						controlY2 = controlY2 +a;

					}
					

					//Eye
					paintEye = new Paint( );
					paintEye. setAntiAlias( true );
					paintEye. setStyle( Style. STROKE );

					paintEye. setColor( Color. BLACK );


					
					Bitmap bitImage_eyel = BitmapFactory. decodeResource( getResources( ) , R. drawable.eye);
					Bitmap bitImage_eyer = BitmapFactory. decodeResource( getResources( ) , R. drawable.eye );
					canvas.drawBitmap(bitImage_eyel, (float)eyelx, (float)eyely, null);
					canvas.drawBitmap(bitImage_eyer, (float)eyerx, (float)eyery, null);
					
					Thread.sleep(20);
					
				}
				catch ( Exception e ) { e. printStackTrace(); }
				finally {
					if ( canvas != null ) { holder. unlockCanvasAndPost( canvas ); }
				}
			}
		}
	}

	/**
	 * set three points for mouth
	 */
	public void setPoint1( ) {
		// TODO Auto-generated method stub		
		paintPath = new Paint( );
		paintPath. setAntiAlias( true );
		paintPath. setStyle( Style. STROKE );
		paintPath. setColor( Color.BLACK );

		//test
		strokeWidth = 25;
		paintPath. setStrokeWidth( strokeWidth );

		startX = (mPicWidth/4);
		startY = (mPicHeight/1.54);

		controlX = (mPicWidth/2) ;

		endX = (mPicWidth/1.32);
		endY = (mPicHeight/1.54);
	}
	/**
	 * 画出嘴的形状
	 * @param canvas
	 */
	public void DrawPath1( Canvas canvas ) {
		//  TODO Auto-generated method stub
		path1 = new Path( );
		path1. reset( );
		path1. moveTo( (float)startX , (float)startY );
		path1. quadTo( (float)controlX , (float)controlY , (float)endX , (float)endY );
		
		path2 = new Path( );
		path2. reset( );
		path2. moveTo( (float)startX , (float)startY );
		path2. quadTo( (float)controlX , (float)controlY2 , (float)endX , (float)endY );
		canvas.drawPath( path1, paintPath );
		canvas.drawPath( path2 , paintPath );
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	}

	public void OnTouch(MotionEvent event){

		controlY = (mPicHeight/1.28);
		controlY2 = (mPicHeight/1.18);
		a = 10;

		eyelx = (mPicWidth/4.77);
		eyely = (mPicHeight/3.95);
		eyerx = (mPicWidth/1.57);
		eyery = (mPicHeight/3.95);

		//  move up
		if(event.getY() < (mPicHeight*0.3)){
			if(event.getX() < (mPicWidth/18*5)){   			//move left
				eyelx = (mPicWidth/4.77) - 30;
				eyely = (mPicHeight/3.95) - 30;
				eyerx = (mPicWidth/1.57) - 30;
				eyery = (mPicHeight/3.95) - 30;
			}else if(event.getX() > (mPicWidth/18*13)){   	//move right
				eyelx = (mPicWidth/4.77) + 30;
				eyely = (mPicHeight/3.95) - 30;
				eyerx = (mPicWidth/1.57) + 30;
				eyery = (mPicHeight/3.95) - 30;
			}else{											//between
				eyelx = (mPicWidth/4.77) ;
				eyely = (mPicHeight/3.95) - 40;
				eyerx = (mPicWidth/1.57) ;
				eyery = (mPicHeight/3.95) - 40;
			}
			//move down
		}else if(event.getY() > (mPicHeight*0.6)){
			if(event.getX() < (mPicWidth/18*5)){
				eyelx = (mPicWidth/4.77) - 30;
				eyely = (mPicHeight/3.95) + 30;
				eyerx = (mPicWidth/1.57) - 30;
				eyery = (mPicHeight/3.95) + 30;
			}else if(event.getX() > (mPicWidth/18*13)){
				eyelx = (mPicWidth/4.77) + 30;
				eyely = (mPicHeight/3.95) + 30;
				eyerx = (mPicWidth/1.57) + 30;
				eyery = (mPicHeight/3.95) + 30;
			}else{
				eyelx = (mPicWidth/4.77) ;
				eyely = (mPicHeight/3.95) + 35;
				eyerx = (mPicWidth/1.57) ;
				eyery = (mPicHeight/3.95) + 35;
			}
		}else {
			if(event.getX() < (mPicWidth/18*5)){
				eyelx = (mPicWidth/4.77) - 35;
				eyely = (mPicHeight/3.95) ;
				eyerx = (mPicWidth/1.57) - 35;
				eyery = (mPicHeight/3.95) ;
			}else if(event.getX() > (mPicWidth/18*13)){
				eyelx = (mPicWidth/4.77) + 35;
				eyely = (mPicHeight/3.95) ;
				eyerx = (mPicWidth/1.57) + 35;
				eyery = (mPicHeight/3.95) ;
			}else{
				eyelx = (mPicWidth/4.77) ;
				eyely = (mPicHeight/3.95);
				eyerx = (mPicWidth/1.57) ;
				eyery = (mPicHeight/3.95);
			}
		}
	}
}
