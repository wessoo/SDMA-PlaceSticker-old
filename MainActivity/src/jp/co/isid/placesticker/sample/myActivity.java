package jp.co.isid.placesticker.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;

public class myActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //keep the screen on
      	getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
      	
        //title bar color
        View title = getWindow().findViewById( android.R.id.title );
        View titleBar = ( View ) title.getParent();
        titleBar.setBackgroundColor( Color.GRAY );
    }
}//EOF
