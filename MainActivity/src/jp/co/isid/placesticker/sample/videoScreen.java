package jp.co.isid.placesticker.sample;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.controlledsenility.android.youtube.OpenYouTubePlayerActivity;

public class videoScreen extends myActivity{

	//declare button var
	private Button back;
	private Button more;
	private ImageButton play;
	
	private static Intent videoIntent;
	
	//private static ArrayList<Integer> hdImgArr;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoscreen);
      
		//getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

		/* button listeners */
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(onClickListener);
		
		more = (Button) findViewById(R.id.more);
		more.setOnClickListener(onClickListener);
		
		play = (ImageButton) findViewById(R.id.play);
		play.setOnClickListener(onClickListener);

		/* Image in the middle */
        ImageView img = (ImageView) findViewById(R.id.grid_item_Vimage);
        img.setImageBitmap( MainActivity.imgArr.get( startTour.infoID ) );
        
        /* Setting up YouTube player */
		String url = MainActivity.vidUrlArr.get( startTour.infoID );
        Uri uri = Uri.parse(url);
        String videoId = uri.getQueryParameter("v");
        videoIntent = new Intent(null, Uri.parse("ytv://" + videoId), this,OpenYouTubePlayerActivity.class);
	}//onCreate
	
	
	private OnClickListener onClickListener = new OnClickListener() 
    {
		public void onClick(final View v) 
        {
		   	switch(v.getId()){
	            case R.id.back:
	            	
	            	//remove the list
	            	MainActivity.titleArr.remove(startTour.infoID);
	            	MainActivity.authorArr.remove(startTour.infoID);
	            	MainActivity.descArr.remove(startTour.infoID);
	            	MainActivity.imgUrlArr.remove(startTour.infoID);
	            	MainActivity.vidUrlArr.remove(startTour.infoID);
	            	MainActivity.imgArr.remove(startTour.infoID);
	            	MainActivity.roomArr.remove(startTour.infoID);
	            	MainActivity.chosenArr.remove(startTour.infoID);

	            	Intent i = new Intent(videoScreen.this, startTour.class);
	            	startActivity(i);
	            	break;
	            case R.id.more:
	            	Intent j = new Intent(videoScreen.this, infoScreen.class);
	            	startActivity(j);
	            	break;
	            case R.id.play:
                    startActivity(videoIntent);
	            default:
		   	}
	    }//onClick
	};//listener
	
	/* disable back button */
    @Override
    public void onBackPressed() {
    	
    }

}//EOF
