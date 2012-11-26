package jp.co.isid.placesticker.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class infoScreen extends myActivity
{
	private Button back;
	private TextView infoTitle;
	private TextView infoAuthor;
	private TextView infoDesc;

	private String title;
	private String author;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
      
		//getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//button listeners
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(onClickListener);

		//read from info file
		title = MainActivity.titleArr.get(startTour.infoID);
		author = MainActivity.authorArr.get(startTour.infoID);

		infoTitle = (TextView) findViewById( R.id.infoTitle );
		infoTitle.setText( title );
		infoAuthor = (TextView) findViewById( R.id.infoAuthor );
		infoAuthor.setText( author );
		infoDesc = (TextView) findViewById( R.id.infoDesc );
		infoDesc.setText( MainActivity.descArr.get(startTour.infoID) );
	}
	
	private OnClickListener onClickListener = new OnClickListener()
	{
		public void onClick(final View v) {
	        Intent i = new Intent(infoScreen.this, videoScreen.class);
	        startActivity(i);
	    }
	};
	
	/* disable back button */
    @Override
    public void onBackPressed() {
    	
    }
    
}//infoScreen class
