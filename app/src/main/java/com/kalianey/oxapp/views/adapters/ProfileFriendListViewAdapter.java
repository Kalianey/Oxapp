package com.kalianey.oxapp.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.UICircularImage;
import com.kalianey.oxapp.views.activities.Profile;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kalianey on 20/08/2015.
 */
public class ProfileFriendListViewAdapter extends ArrayAdapter<ModelUser> {

    private LayoutInflater inflater;
    private List<ModelUser> users; //data
    private Activity listContext;
    private int listRowLayoutId;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private String PACKAGE = "IDENTIFY";


    public ProfileFriendListViewAdapter(Activity context, int resource, List<ModelUser> objs) {
        super(context, resource, objs);
        users = objs;
        listContext = context;
        listRowLayoutId = resource;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public ModelUser getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(ModelUser item) {
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
            viewHolder.avatarImageView = (UICircularImage) row.findViewById(R.id.avatarImageView);

            row.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) row.getTag();
        }

        viewHolder.user = users.get(position);
        Picasso.with(listContext)
                .load(viewHolder.user.getAvatar_url())
                .noFade()
                .into(viewHolder.avatarImageView);


        //Set onClick item
        final ViewHolder finalViewHolder = viewHolder;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(listContext, Profile.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("userObj", finalViewHolder.user);

                int[] screen_location = new int[2];
                finalViewHolder.avatarImageView.getLocationOnScreen(screen_location);

                mBundle.putInt(PACKAGE + ".left", screen_location[0]);
                mBundle.putInt(PACKAGE + ".top", screen_location[1]);
                mBundle.putInt(PACKAGE + ".width", finalViewHolder.avatarImageView.getWidth());
                mBundle.putInt(PACKAGE + ".height", finalViewHolder.avatarImageView.getHeight());

                i.putExtras(mBundle);
                listContext.startActivity(i);
            }
        });

        return row;
    }

    //allow us to put all the subviews so when we create listAdapters all our view are recycled (performance)
    public class ViewHolder {

        ModelUser user;
        UICircularImage avatarImageView;

    }
}
