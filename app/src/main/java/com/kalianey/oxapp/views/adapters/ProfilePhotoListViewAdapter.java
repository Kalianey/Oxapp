package com.kalianey.oxapp.views.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelAttachment;
import com.kalianey.oxapp.utils.AppController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalianey on 18/08/2015.
 */
public class ProfilePhotoListViewAdapter extends ArrayAdapter<ModelAttachment> {

    private LayoutInflater inflater;
    private List<ModelAttachment> photos; //data
    private Activity listContext;
    //private Context listContext;
    private int listRowLayoutId;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ProfilePhotoListViewAdapter(Activity context, int resource, List<ModelAttachment> objs) {
        super(context, resource, objs);
        photos = objs;
        listContext = context;
        listRowLayoutId = resource;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public ModelAttachment getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(ModelAttachment item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder viewHolder = null;

        if (row == null) {
            inflater = LayoutInflater.from(listContext);

            //Row holds our layout
            row = inflater.inflate(listRowLayoutId, parent, false) ; //resource, viewGroup, attachToGroup

            viewHolder = new ViewHolder();

            //Get references to our views
            viewHolder.imageView = (NetworkImageView) row.findViewById(R.id.imageView);

            row.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) row.getTag();
        }

        viewHolder.photo = photos.get(position);
        viewHolder.imageView.setImageUrl(viewHolder.photo.getUrl(), imageLoader);


        //Set onClick item
        final ViewHolder finalViewHolder = viewHolder;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent i = new Intent(listContext, Message.class);
//                Bundle mBundle = new Bundle();
//                mBundle.putSerializable("convObj", finalViewHolder.conversation);
//                i.putExtras(mBundle);
//                listContext.startActivity(i);
            }
        });

        return row;
    }

    //allow us to put all the subviews so when we create listAdapters all our view are recycled (performance)
    public class ViewHolder {

        ModelAttachment photo;
        NetworkImageView imageView;

    }

}
