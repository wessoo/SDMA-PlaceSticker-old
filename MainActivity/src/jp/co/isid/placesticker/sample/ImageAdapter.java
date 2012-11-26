package jp.co.isid.placesticker.sample;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<String> authorArr;
    private ArrayList<String> titleArr;
    private ArrayList<String> numArr;

    //ctor
    public ImageAdapter( Activity activity, 
                         ArrayList<String> titleArr,
                         ArrayList<String> authorArr,
                         ArrayList<String> numArr)
    {  
        super();
        this.activity = activity;
        this.authorArr = authorArr;
        this.titleArr = titleArr;
        this.numArr = numArr;
    }  
    
    //need this to keep img and txt in order
    public static class ViewHolder  
    {  
        public ImageView img;  
        public TextView title;  
        public TextView author;
        public TextView numOfArt;
    }  

	public View getView( int position, View gridView, ViewGroup parent ) {
	    
		ViewHolder view;  
        LayoutInflater inflator = activity.getLayoutInflater();
  
        if( gridView == null )  
        {  
            view = new ViewHolder();  
            gridView = inflator.inflate( R.layout.grid, null );  
  
            view.title    = (TextView )  gridView.findViewById( R.id.grid_item_label  );  
            view.img      = (ImageView)  gridView.findViewById( R.id.grid_item_image  );
            view.author   = (TextView )  gridView.findViewById( R.id.grid_item_author );
            view.numOfArt = (TextView )  gridView.findViewById( R.id.grid_item_number );
            
            // set value into textview
            gridView.setTag(view);
        }  
        else  
        {  
            view = (ViewHolder) gridView.getTag();  
        }  
  
        view.title.setText( titleArr.get( position ) );
        view.author.setText( authorArr.get( position ) );
        view.numOfArt.setText( numArr.get( position ) );
        view.img.setImageBitmap( MainActivity.imgArr.get( position ) );

        return gridView;
	}

	public int getCount() {   
        return titleArr.size();
    } 

    /* Unimplemented methods */
	public long getItemId(int position) {
		return 0;
	}

    public Object getItem(int arg0) {
        return null;
    }
}
