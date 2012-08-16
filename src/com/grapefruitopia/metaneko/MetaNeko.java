package com.grapefruitopia.metaneko;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MetaNeko extends Service {
	public static String TAG = "MetaNeko";
	
	final public static String id = "com.grapefruitopia.metaneko";
	final static String name = "MetaNeko";
	
	Bitmap bitmap = null;
		
	static boolean isRunning = false;
	static TimerTask task = null;
	private static Timer timer = new Timer();
	
    //
    //Constants

    private final double pi = Math.PI;
    private final int over = 1;
    private final int under = 2;
    private final int left = 3;
    private final int right = 4;
    //
    //Variables
    private int pos;      //neko's position
    private int x, y;	    //mouse pos.
    private int ox, oy;    //image pos.
    private int dx, dy;    //image-mouse distance
    private static int mx, my;		// "mouse" position
    private int no;	    //image number.
//    private int init;	    //for image loading initialize counter
    private int slp;	    //sleep time
    private int ilc1;     //image loop counter
    private int ilc2;     //second loop counter
    private static boolean move=true;     //mouse move, flag
    private boolean out;      //mouse exiseted, flag
    private double theta;    //image-mouse polar data
    private double dist;     //distance
    private Bitmap image[] = null;  //images
    private Rect nekoBounds = new Rect(0,0,64,64);
    
    static Random rnd = new Random();
    static Typeface typeface = null;
    
 
    @Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "Neko Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		
    	loadKitten(this);
    	
    	no=25;
    	
    	ox=rnd.nextInt(64);
    	oy=rnd.nextInt(64);
    	
    	mx=rnd.nextInt(112)-8;
    	my=rnd.nextInt(112)-8;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Neko Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		stopUpdate();
		//stop();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "Neko Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		//start(this);
		startUpdate(this);
	}
    
    private void loadKitten(Context context) {
        image = new Bitmap[33];
        for (int i = 1; i <= 32; i++) {
            image[i] = Utils.loadBitmapFromAssets(context, i + ".GIF");
        }
    }
    
    /** Locates the mouse on the screen and determines what the cat shall do. */
    private void locateMouseAndAnimateCat() {

        //Determines what the cat should do, if the mouse moves
        out = !nekoBounds.contains(mx, my);
        if (out) {
            x = mx;
            y = my;
            if (y < nekoBounds.top) {
                y = nekoBounds.top;
                pos = over;
            }
            if (y > nekoBounds.bottom) {
                y = nekoBounds.bottom;
                pos = under;
            }
            if (x < nekoBounds.left) {
                x = nekoBounds.left;
                pos = left;
            }
            if (x > nekoBounds.right) {
                x = nekoBounds.right;
                pos = right;
            }
        } 
//            else {
//            move = (x != mx || y != my);
//            x = mx;
//            y = my;
//            if (move) {
//                slp = Math.min(slp, 200);
//            }
//
//        }
        dx = x - ox;
        dy = oy - y;
        dist = Math.sqrt(dx * dx + dy * dy); //distance formula (from mouse to cat)
        theta = Math.atan2(dy, dx);     //angle from mouse to cat
        //
        slp=0;
        //slp = Math.max(0, slp - timer.getDelay());
        if (slp == 0) {
            animateCat();
        }
    }

    private void animateCat() {
        //
    	final int moveDist = 8;
    	
        if (dist > moveDist) { //moves cat n pixels
            slp = 200;
            ox = (int) (ox + Math.cos(theta) * moveDist);
            oy = (int) (oy - Math.sin(theta) * moveDist);
            dist = dist - moveDist;

            /*
            The following conditions determine what image should be shown.
            Remember there are two images for each action. For example if the cat's
            going right, display the cat with open legs and then with close legs,
            open, and so on.
             */
            if (theta >= -pi / 8 && theta <= pi / 8) //right
            {
                no = (no == 5) ? 6 : 5;
            }
            if (theta > pi / 8 && theta < 3 * pi / 8) //upper-right
            {
                no = (no == 3) ? 4 : 3;
            }
            if (theta >= 3 * pi / 8 && theta <= 5 * pi / 8) //up
            {
                no = (no == 1) ? 2 : 1;
            }
            if (theta > 5 * pi / 8 && theta < 7 * pi / 8) //upper-left
            {
                no = (no == 15) ? 16 : 15;
            }
            if (theta >= 7 * pi / 8 || theta <= -7 * pi / 8) //left
            {
                no = (no == 13) ? 14 : 13;
            }
            if (theta > -7 * pi / 8 && theta < -5 * pi / 8) //bottom-left
            {
                no = (no == 11) ? 12 : 11;
            }
            if (theta >= -5 * pi / 8 && theta <= -3 * pi / 8) //down
            {
                no = (no == 9) ? 10 : 9;
            }
            if (theta > -3 * pi / 8 && theta < -pi / 8) //bottom-right
            {
                no = (no == 7) ? 8 : 7;
            }
            //sets move back to false
            move = false;
        } else {   //-if the mouse hasn't moved or the cat's over the mouse-
            ox = x;
            oy = y;
            slp = 800;
            switch (no) {
                case 25: //<cat sit>
                    //If the mouse is outside the applet
                    if (out == true) {
                        switch (pos) {
                            case over:
                                no = 17;
                                break;
                            case under:
                                no = 21;
                                break;
                            case left:
                                no = 23;
                                break;
                            case right:
                                no = 19;
                                break;
                            default:
                                no = 31;
                                break;
                        }
                        pos = 0;
                        break;
                    }
                    no = 31;
                    break; //<31: cat lick>
                //
                case 17: //The mouse is outside, above applet
                    no = 18;    //show images 17 & 18, 6 times
                    ilc1++;
                    if (ilc1 == 6) {
                        no = 27;
                        ilc1 = 0;
                    }
                    break;
                //
                case 18:
                    no = 17;
                    break;
                //
                case 21: //The mouse is outside, under applet
                    no = 22;    //show images 21 & 22, 6 times
                    ilc1++;
                    if (ilc1 == 6) {
                        no = 27;
                        ilc1 = 0;
                    }
                    break;
                //
                case 22:
                    no = 21;
                    break;
                //
                case 23: //the mouse is outside, left
                    no = 24;    //show images 23 & 24, 6 times
                    ilc1++;
                    if (ilc1 == 6) {
                        no = 27;
                        ilc1 = 0;
                    }
                    break;
                //
                case 24:
                    no = 23;
                    break;
                //
                case 19: //The mouse is outside, right
                    no = 20;    //show images 19 & 20, 6 times
                    ilc1++;
                    if (ilc1 == 6) {
                        no = 27;
                        ilc1 = 0;
                    }
                    break;
                //
                case 20:
                    no = 19;
                    break;
                //
                case 31: //cat lick (6  times)
                    no = 25;
                    ilc1++;
                    if (ilc1 == 6) {
                        no = 27;
                        ilc1 = 0;
                    }
                    break;
                //
                case 27:
                    no = 28;
                    break; //cat scratch (27 & 28, 4 times)
                case 28:
                    no = 27;
                    ilc2++;
                    if (ilc2 == 4) {
                        no = 26;
                        ilc2 = 0;
                    }
                    break;
                case 26:
                    no = 29;
                    slp = 1600;
                    break; //cat yawn (26)
                case 29:
                    no = 30;
                    slp = 1600;
                    break; //cat sleep (29 & 30, forever)
                case 30:
                    no = 29;
                    slp = 1600;
                    break;
                default:
                    no = 25;
                    break;
            }
            if (move == true) {
                //re-initialize some variables
                no = 32;
                ilc1 = 0;
                ilc2 = 0;
                slp = 1000;
                move = false;
            }
        }
    }
	

	public static void announce(Context context) {
		Intent intent = new Intent("org.metawatch.manager.APPLICATION_ANNOUNCE");
		Bundle b = new Bundle();
		b.putString("id", id);
		b.putString("name", name);
		intent.putExtras(b);
		context.sendBroadcast(intent);
		Log.d(MetaNeko.TAG, "Sent APPLICATION_ANNOUNCE");
	}
	
	private void refreshApp(Context context) {
		if (bitmap==null) {
			bitmap = Bitmap.createBitmap(96, 96, Bitmap.Config.RGB_565);
		}

		if (rnd.nextInt(25)==0) {
	    	mx=rnd.nextInt(112)-8;
	    	my=rnd.nextInt(112)-8;
	    	move=true;
		}
		
		locateMouseAndAnimateCat();
		
		Canvas c = new Canvas(bitmap);
		c.drawColor(Color.WHITE);
		
		//Log.d(TAG, "Drawing frame "+no);
		if (image[no]!=null)
			c.drawBitmap(image[no], ox, oy, null);
		
	}
	
	private void update(Context context) {
		refreshApp(context);
		
		Intent intent = new Intent("org.metawatch.manager.APPLICATION_UPDATE");
		Bundle b = new Bundle();
		b.putString("id", id);
		b.putIntArray("array", Utils.makeSendableArray(bitmap));
		b.putBoolean("autoinvert", true);
		intent.putExtras(b);

		context.sendBroadcast(intent);
		
		slp = Math.max(200, slp);
		scheduleUpdate(context, slp);
	}
	
	public static void button(Context context, int button, int type) {
		
    	mx=rnd.nextInt(112)-8;
    	my=rnd.nextInt(112)-8;
    	move=true;
    	
	}
	
	private class UpdateTask extends TimerTask
    { 
		Context context;
		UpdateTask(Context ctx) {
			context = ctx;
		}
		
        public void run() 
        {
            update(context);
        }
    }    
 
	
	private void startUpdate(final Context context) {
		
		timer.schedule(new UpdateTask(context), 0);

		isRunning = true;
	}
	
	private void scheduleUpdate(final Context context, long interval) {
		Log.d(TAG, "scheduleUpdate "+interval);
		if (isRunning) {
			timer.schedule(new UpdateTask(context), interval);
		}
	}
	
	public void stopUpdate() {
		isRunning = false;
		timer.cancel();
		timer = new Timer();
	}

	
}
