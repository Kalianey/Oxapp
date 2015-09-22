package com.kalianey.oxapp.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.UICircularImage;
import com.kalianey.oxapp.views.activities.Profile;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kalianey on 14/08/2015.
 */
public class FriendRequestListAdapter extends ArrayAdapter<ModelUser> {

    private LayoutInflater inflater;
    private List<ModelUser> friends; //data
    private Activity listContext;
    private int listRowLayoutId;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    //Vars
    private String PACKAGE = "IDENTIFY";


    public FriendRequestListAdapter(Activity context, int resource, List<ModelUser> objs) {
        super(context, resource, objs);
        friends = objs;
        listContext = context;
        listRowLayoutId = resource;
    }

    @Override
    public int getCount() {
        return friends.size();
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

        if(convertView==null){

            // inflate the layout
            LayoutInflater vi = (LayoutInflater) listContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = vi.inflate(R.layout.fragment_friend_list_item, null);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) row.findViewById(R.id.item_title);
            viewHolder.descr = (TextView) row.findViewById(R.id.item_description);
            viewHolder.coverImageView = (NetworkImageView) row.findViewById(R.id.item_cover_image);
            viewHolder.avatarImageView = (UICircularImage) row.findViewById(R.id.item_avatar_image);
            viewHolder.number = (TextView) row.findViewById(R.id.item_number);
            viewHolder.sex = (TextView) row.findViewById(R.id.item_numbertext);

            // store the holder with the view.
            row.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();

        }

        viewHolder.friend = friends.get(position);

        final String item = friends.get(position).getName();
        final String desc = friends.get(position).getAddress();
        final String coverImage = friends.get(position).getCover_url();
        final String avatarImage = friends.get(position).getAvatar_url();
        final String number = friends.get(position).getAge();
        String sex = friends.get(position).getSex();
        if(sex.equals("base+questions_question_sex_value_1") || sex.equals("base+questions_question_sex_value_2")){
            sex = "";
        }

        viewHolder.coverImageView.setImageUrl(coverImage, imageLoader);
        //viewHolder.avatarImageView.setImageUrl(avatarImage, imageLoader); //TODO: placeholder
        Picasso.with(listContext)
                .load(viewHolder.friend.getAvatar_url())
                .noFade()
                .into(viewHolder.avatarImageView);
        viewHolder.title.setText(item);
        viewHolder.descr.setText(desc);
        viewHolder.number.setText(number);
        viewHolder.sex.setText(sex);

        //Set onClick item
        final ViewHolder finalViewHolder = viewHolder;

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Friend clicked: ", finalViewHolder.friend.getName());
                QueryAPI query = new QueryAPI();
                query.user(finalViewHolder.friend.getUserId(), new QueryAPI.ApiResponse<ModelUser>() {
                    @Override
                    public void onCompletion(ModelUser user) {

                        Intent i = new Intent(listContext, Profile.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("userObj", user);
                        Log.d("Friend clicked: ", user.getName());

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

            }
        });


        return row;
    }

    //allow us to put all the subviews so when we create listAdapters all our view are recycled (performance)
    public class ViewHolder {

        ModelUser friend;
        NetworkImageView coverImageView;
        UICircularImage avatarImageView;
        TextView title;
        TextView descr;
        TextView number;
        TextView sex;

    }


}
