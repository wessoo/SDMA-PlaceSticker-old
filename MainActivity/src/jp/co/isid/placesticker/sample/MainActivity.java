package jp.co.isid.placesticker.sample;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import jp.co.isid.placesticker.lib.DevicePosition;
import jp.co.isid.placesticker.lib.PlaceStickerListener;
import jp.co.isid.placesticker.lib.PlaceStickerReceiver;
import jp.co.isid.placesticker.lib.Exception.PlaceStickerException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends myActivity implements PlaceStickerListener
{	
	//public static String serverAddr = "192.168.100.101";
	public static String serverAddr = "172.16.4.85"; //SDMA touchtable 1
	
	//declare grid var
    private GridView gridView;
	private ImageAdapter imgAdptr;

	//initialize arrays with artworks' information
    public static ArrayList<String> titleArr;
    public static ArrayList<String> authorArr;
    public static ArrayList<String> descArr;
    public static ArrayList<String> imgUrlArr;
    public static ArrayList<String> vidUrlArr;
    public static ArrayList<Bitmap> imgArr;
    public static ArrayList<String> roomArr;
    
    public static ArrayList<String> chosenArr;

	//declare loading scene
	private ProgressDialog progressDialog;
	
	//declare button var
	private Button ready;
	private Button updateList;
	
	//declare notification var
	private int psCounter = 0;
	private boolean psChecker = false;
	private boolean psChecker2 = false;
	public static boolean updateReady = false;
	
	////////////////////  demo only   ////////////////////////////
	//declare placesticker var
	private PlaceStickerReceiver receiver;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
      	
      	//set placeSticker
      	receiver = new PlaceStickerReceiver(this);
      	receiver.setPlaceStcikerListener(this);
      	
        // Set custom adapter to gridview
        gridView = ( GridView ) findViewById( R.id.gridView1 );
		
		//button listeners
		ready = (Button) findViewById(R.id.right);
		ready.setOnClickListener(readyListner);
		updateList = (Button) findViewById(R.id.left);
        updateList.setOnClickListener(updateListListener);
    }

    /** 
     * Update artwork list;
     * Please check DownloadImagesTask()'s onPostExecute() for actual UI updates
     */
	public void updateGrid()
    {
	    ArrayList<String> numOfArtArr = new ArrayList<String>();
	    for( int i=0; i<chosenArr.size(); i++ ) {
	        numOfArtArr.add( ""+ (i+1) );
	    }
	    
	    imgAdptr = new ImageAdapter( this, titleArr, authorArr, numOfArtArr );
	    
	    progressDialog.dismiss();
	    //pops up when downloading images
	    progressDialog = ProgressDialog.show(this, "Please wait....", "Loading artworks...");
	    
	    //gridView is updated after images are downloaded
	    imgArr = new ArrayList<Bitmap>();
	    new DownloadImagesTask().execute();
    }//update grid
	
	private void dialog( final int seconds, final String text ) 
	{
	    progressDialog = ProgressDialog.show(this, "Please wait....", text);
	    if( seconds != 0 ) {
            new Thread(new Runnable(){
                public void run(){
                    try {
                        Thread.sleep(seconds * 1000);
                        progressDialog.dismiss();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
	    }
	}
	
	/** Start Tour button listener */
	private OnClickListener readyListner = new OnClickListener()
    {
	    public void onClick(final View v)
        {
            if( titleArr != null ) { //start tour
                Intent i = new Intent(MainActivity.this, startTour.class);
                startActivity(i);
            } else { // artwork list is not selected yet
                showPrompt( "Not Ready", "Please select artwork for your tour first!" );
            }
	    }
	};
    
	/** Update photo list listener */
    private OnClickListener updateListListener = new OnClickListener()
    {
        public void onClick(final View v) {
            /*
            connectToTable task = new connectToTable();
            task.execute();
            */
            if( psChecker2 ) {
                connectToTable task = new connectToTable();
                task.execute();
            } else {
                showPrompt( "Connection failed", "Please synchrnoize with the touch table first!" );
            }
            
        }
    };

    /**
     * Connect to touch table server and receive strings to set up all arrayList
     * Prompt user to check WiFi and place android to touch table when connection failed
     */
    private class connectToTable extends AsyncTask<Void, Void, Void> {
        
        @Override
        protected void onPreExecute() {
            dialog( 0, "Transfering Data..." );
        }
        
        @Override
        protected Void doInBackground(Void...param) {
            Socket socket = null;
            try {
                socket = new Socket( serverAddr, 8880 );
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                output.println( "connected" );
                
                chosenArr = new ArrayList<String>();
                titleArr  = new ArrayList<String>();
                authorArr = new ArrayList<String>();
                descArr   = new ArrayList<String>();
                imgUrlArr = new ArrayList<String>();
                vidUrlArr = new ArrayList<String>();
                roomArr  = new ArrayList<String>();

                String str;
                while( !(str = inputStream.readLine()).equals( "token sent" ) ) {
                    chosenArr.add( str );
                }
                
                while( !(str = inputStream.readLine()).equals( "titles sent" ) ) {
                    titleArr.add( str );
                }
                
                while( !(str = inputStream.readLine()).equals( "authors sent" ) ) {
                    authorArr.add( str );
                }
                
                String temp = "";
                while( !(str = inputStream.readLine()).equals( "desc sent" ) ) {
                    if( str.equals("//end")){
                        descArr.add( temp );
                        temp = "";
                    } else {
                        temp += str + "\n";
                    }
                }
                
                while( !(str = inputStream.readLine()).equals( "imgurl sent" ) ) {
                    imgUrlArr.add( str );
                }
                
                while( !(str = inputStream.readLine()).equals( "vidurl sent" ) ) {
                    vidUrlArr.add( str );
                }
                
                while( !(str = inputStream.readLine()).equals( "room sent" ) ) {
                    roomArr.add( str );
                }

            } catch (UnknownHostException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            } finally{
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }//doinbackground

        @Override
        protected void onPostExecute(Void param) {
            if( chosenArr != null ) {
                updateGrid();
            } else {
                progressDialog.dismiss();
                String msg = "Please place your android in the circle of the touch table and" +
                             "make sure there is WiFi connection.";
                showPrompt( "Update Failed!", msg );
            }
        }
    }//connectToTable
    
    /** show alert box to prompt user */
    private void showPrompt( String title, String msg ) {
        AlertDialog.Builder adb=new AlertDialog.Builder( MainActivity.this );
        adb.setTitle( title );
        adb.setMessage( msg );
        adb.setPositiveButton("OK", null);
        adb.show();
    }
    
    /** download images from website and set up imgArr */
    private class DownloadImagesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... param) {
            for( int i=0; i<chosenArr.size(); i++ ) {
                imgArr.add( download_Image( imgUrlArr.get(i) ) );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            
            progressDialog.dismiss();
            
            TextView prompt = (TextView) findViewById(R.id.artwkPrompt);
            prompt.setText("These are the art works you selected.");
            
            gridView.setAdapter( imgAdptr );
        }

        private Bitmap download_Image(String url) {
            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e( "Hub","Error loading image: " + e.getMessage().toString() );
            } 
            return bm;
        }
    }

    //////////////////   demo only   ///////////////////////////
    ////////////////////////////////////////////////////////////
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
	    updateReady = true;
		if( position != null && updateReady == true) 
        {
			String PsId = position.getNearestPlaceSticker().getId();
			int PsDistance = position.getNearestPlaceSticker().getDistance();

			if( psCounter == 1 && PsDistance <= 0 && PsId.compareTo("table") == 0 && psChecker2 == false)
			{
				progressDialog.dismiss();
				dialog( 1, "Synchronized");
				psCounter = 0;
				psChecker2 = true;
			}else if( PsDistance <= 0 && PsId.compareTo("table") == 0 && psChecker == false && psCounter == 0)
			{
				dialog( 0, "Synchronizing");
				psCounter++;
				psChecker = true;
			}
			
		}else{
			psChecker = false;
			psChecker2 = false;
			psCounter = 0;
		}
	}
	
	/* disable back button */
    @Override
    public void onBackPressed() {
    	
    }
    
}//EOF
