package jp.co.isid.placesticker.sample;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import jp.co.isid.placesticker.lib.DevicePosition;
import jp.co.isid.placesticker.lib.PlaceStickerListener;
import jp.co.isid.placesticker.lib.PlaceStickerReceiver;
import jp.co.isid.placesticker.lib.Exception.PlaceStickerException;


public class startTour extends myActivity implements PlaceStickerListener
{
	//declare placesticker var
	private PlaceStickerReceiver receiver;
	
	//NOTIFY
	//declare notification var
	//private Notification notifyDetails; 
	//private NotificationManager mNotificationManager;
	private int SIMPLE_NOTFICATION_ID;
	
	//declare button var
	private Button reset;
	
	//declare check notification var
	private boolean checkNotification = false;
	private String lastPlaceSticker = null;
	public static int infoID;
	
	private boolean chosen = false;
	
	private ImageView mapView;
	
	//declare grid var
    private GridView gridView;
	private ImageAdapter imgAdptr;
	private ArrayList<String> numOfArtArr;
	
    public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.starttour);
		//keep the screen on
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// Set custom adapter to gridview
        gridView = ( GridView ) findViewById( R.id.gridView1 );
        updateGrid();
		//text
		TextView t=new TextView(this);
		t=(TextView)findViewById(R.id.textView1); 
	    t.setText("Arts you selected are in :\n" + 
		          searchLocation( MainActivity.roomArr, MainActivity.titleArr ) );
		
		//button listeners
		reset = (Button) findViewById(R.id.reset);
		reset.setOnClickListener(onClickListener);
		
		//set placeSticker
		receiver = new PlaceStickerReceiver(this);
        receiver.setPlaceStcikerListener(this);
        
        //NOTIFY
        //assign notification var
        //mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		//notifyDetails = new Notification(R.drawable.icon,"Location Alert",System.currentTimeMillis());
	}
    
    public void updateGrid()
    {
	    numOfArtArr = new ArrayList<String>();
	    for( int i=0; i< MainActivity.chosenArr.size(); i++ ) {
	        numOfArtArr.add( ""+ (i+1) );
	    }
	    
	    imgAdptr = new ImageAdapter( this, MainActivity.titleArr, MainActivity.roomArr, numOfArtArr );
	    gridView.setAdapter( imgAdptr );

    }//update grid
	
    /* disable back button */
    @Override
    public void onBackPressed() {
    	
    }
    
    /*
     * return locations and remove duplicate rooms.
     */
    private String searchLocation( ArrayList<String> room, ArrayList<String> art ){
    	
    	//remove duplicate addresses
    	HashSet<String> hs = new HashSet<String>();
    	ArrayList<String> listedRoom = new ArrayList<String>();
    	hs.addAll(room);
    	listedRoom.clear();
    	listedRoom.addAll(hs);
    	
    	//print locations
    	String printLocation = "";
    	Iterator iterator = listedRoom.iterator();
    	
    	while( iterator.hasNext() ){
    		printLocation = iterator.next() + " " + printLocation;
    	}
    	
		return printLocation;
    }
    
	private OnClickListener onClickListener = new OnClickListener() 
    {
		public void onClick(final View v) 
        {
		   	switch(v.getId())
            {
	        	//reset button pushed
	            case R.id.reset:
	            	//run main activity
	            	MainActivity.updateReady = false;
	            	Intent i = new Intent(startTour.this, MainActivity.class);
	            	startActivity(i);
	            	break;
	                 
	            //default
	            default:
		   	}
	    }
	};
	
	//PlaceSticker
	@Override
	protected void onResume() 
    {
		super.onResume();
		
		/* SD
		File appdir = new File(Environment.getExternalStorageDirectory(),"placesticker");
		File configfile = new File(appdir,"config.xml");
		*/
		receiver = new PlaceStickerReceiver(this);
        receiver.setPlaceStcikerListener(this);

        try {
        	receiver.loadSettingFile(R.raw.config);
			receiver.scanStart();
		} catch (PlaceStickerException e) {
			e.printStackTrace();
		}
	}
	
	//PlaceSticker
	@Override
	protected void onPause()
    {
		super.onPause();
		receiver.scanStop();
	}

	//PlaceSticker
	public void onPositionChanged(DevicePosition position, int style) 
	{
		if( position != null ) 
        {
    		String PsId = position.getNearestPlaceSticker().getId();
    		if( !PsId.equals( "table" ) ) {
    		    int PsDistance = position.getNearestPlaceSticker().getDistance();
                
                //check if the artwork is selected by the user or not
                infoID = Integer.parseInt(PsId);
                for( int j = 0; j<MainActivity.chosenArr.size(); j++ )
                {
                    if( infoID == Integer.parseInt( MainActivity.chosenArr.get( j ) ) )
                    {
                        infoID = j;
                        chosen = true;
                        break;
                    } else {
                        chosen = false;
                    }
                }
                    
                if( lastPlaceSticker != null && checkNotification == true && 
                    lastPlaceSticker.compareTo(PsId) != 0 )
                {
                    checkNotification = false;
                }

                if( checkNotification == false && PsDistance <= 25 && chosen )
                {
                    lastPlaceSticker = PsId;
                        
                    Intent videoIntent = new Intent(startTour.this, videoScreen.class);
                    startActivity(videoIntent);
                    
                    onNotificationChanged(PsId, PsDistance);
                    
                    checkNotification = true;
                }
                else
                {
                    checkNotification = false;
                } 
    		}
	    }
	}
	
	public void onNotificationChanged(String PsId, int PsDistance) 
	{
		//call Notification.
		Context context = getApplicationContext();
		CharSequence contentTitle = MainActivity.titleArr.get(infoID);
		CharSequence contentText = MainActivity.authorArr.get(infoID);

		Intent notifyIntent = new Intent(startTour.this, videoScreen.class);
		PendingIntent intent = 
				PendingIntent.getActivity(
							startTour.this, 0, 
							notifyIntent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		//NOTIFY
		//notifyDetails.defaults |= Notification.DEFAULT_ALL;
		//notifyDetails.setLatestEventInfo(context, contentTitle, contentText, intent);
		//mNotificationManager.notify(SIMPLE_NOTFICATION_ID, notifyDetails);
	}
}
