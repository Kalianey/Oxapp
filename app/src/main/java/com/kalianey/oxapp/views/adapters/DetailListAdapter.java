package com.kalianey.oxapp.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.utils.UICircularImage;
import com.kalianey.oxapp.views.ListItem;

public class DetailListAdapter {
 
        @SuppressLint("InflateParams")
		public static View getView(ListItem item, Context context) {
 
            // 1. Create inflater 
            LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
            // 2. Get rowView from inflater
            View rowView = inflater.inflate(R.layout.comment, null, false);
 
            // 3. Get the two text view from the rowView
            TextView labelView = (TextView) rowView.findViewById(R.id.name);
            TextView valueView = (TextView) rowView.findViewById(R.id.comment);
            UICircularImage imageview = (UICircularImage) rowView.findViewById(R.id.profile);
 
            // 4. Set the text for textView 
            labelView.setText("Title"/*item.getTitle()*/);
            valueView.setText("Description"/*Html.fromHtml(item.getDesc())*/);
            //imageview.setImageResource(item.getImageId());
 
            // 5. retrn rowView
            return rowView;
        }
       
}