package com.kalianey.oxapp.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelFriend;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.views.Message;
import com.kalianey.oxapp.views.Profile;

import java.util.List;

/**
 * Created by kalianey on 14/08/2015.
 */
public class FriendListAdapter extends ArrayAdapter<ModelFriend> {

    private LayoutInflater inflater;
    private List<ModelFriend> friends; //data
    private Activity listContext;
    private int listRowLayoutId;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private ViewHolder viewHolder;


    public FriendListAdapter(Activity context, int resource, List<ModelFriend> objs) {
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
    public ModelFriend getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(ModelFriend item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

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
            viewHolder.avatarImageView = (NetworkImageView) row.findViewById(R.id.item_avatar_image);
            viewHolder.number = (TextView) row.findViewById(R.id.item_number);
            viewHolder.sex = (TextView) row.findViewById(R.id.item_numbertext);

            // store the holder with the view.
            row.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();

        }

        viewHolder.friend = friends.get(position);

        final String item = friends.get(position).getDisplayName();
        final String desc = friends.get(position).getAddress();
        final String coverImage = friends.get(position).getCoverUrl();
        final String avatarImage = friends.get(position).getAvatarUrl();
        final String number = friends.get(position).getAge();
        String sex = friends.get(position).getSex();
        if(sex.equals("base+questions_question_sex_value_1") || sex.equals("base+questions_question_sex_value_2")){
            sex = "";
        }

        viewHolder.coverImageView.setImageUrl(coverImage, imageLoader);
        viewHolder.avatarImageView.setImageUrl(avatarImage, imageLoader);
        viewHolder.title.setText(item);
        viewHolder.descr.setText(desc);
        viewHolder.number.setText(number);
        viewHolder.sex.setText(sex);

        //Set onClick item
        final ViewHolder finalViewHolder = viewHolder;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QueryAPI query = new QueryAPI();
                query.user(viewHolder.friend.getOpponentId(), new QueryAPI.ApiResponse<ModelUser>() {
                    @Override
                    public void onCompletion(ModelUser result) {
                        Intent i = new Intent(listContext, Profile.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("userObj", result);
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

        ModelFriend friend;
        NetworkImageView coverImageView;
        NetworkImageView avatarImageView;
        TextView title;
        TextView descr;
        TextView number;
        TextView sex;

    }


}
