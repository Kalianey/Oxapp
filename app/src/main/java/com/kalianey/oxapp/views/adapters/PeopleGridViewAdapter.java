package com.kalianey.oxapp.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.views.Profile;

import java.util.List;

/**
 * Created by kalianey on 11/08/2015.
 */
public class PeopleGridViewAdapter extends ArrayAdapter {

    private LayoutInflater inflater;
    private List<ModelUser> users; //data
    private Activity gridContext;
    private int GridItemLayoutId;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public PeopleGridViewAdapter(Activity context, int resource, List<ModelUser> objs) {
        super(context, resource, objs);
        users = objs;
        gridContext = context;
        GridItemLayoutId = resource;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        ViewHolder viewHolder = null;

        if (item == null) {
            inflater = LayoutInflater.from(gridContext);

            //Row holds our layout
            item = inflater.inflate(GridItemLayoutId, parent, false) ; //resource, viewGroup, attachToGroup

            viewHolder = new ViewHolder();

            //Get references to our views
            viewHolder.avatarImageView = (NetworkImageView) item.findViewById(R.id.avatarImageView);
            viewHolder.userStatusImageView = (ImageView) item.findViewById(R.id.userStatus);
            viewHolder.username = (TextView) item.findViewById(R.id.username);

            item.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) item.getTag();
        }

        viewHolder.user = users.get(position);

        //We can now display the data
        String avatar_url = viewHolder.user.getAvatar_url();
        if (avatar_url == null){
            avatar_url = viewHolder.user.getAvatar_url_default();
        }
        viewHolder.avatarImageView.setImageUrl(avatar_url, imageLoader);
        viewHolder.username.setText(viewHolder.user.getName());
        //viewHolder.userStatusImageView : here change colour of image of  here according to bool

        //Set onClick item
        final ViewHolder finalViewHolder = viewHolder;
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(gridContext, Profile.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("userObj", finalViewHolder.user);
                i.putExtras(mBundle);
                gridContext.startActivity(i);
            }
        });

        return item;
    }

    //allow us to put all the subviews so when we create listAdapters all our view are recycled (performance)
    public class ViewHolder {

        ModelUser user;
        NetworkImageView avatarImageView;
        ImageView userStatusImageView;
        TextView username;

    }

}
