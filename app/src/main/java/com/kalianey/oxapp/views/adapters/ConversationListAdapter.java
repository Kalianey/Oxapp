package com.kalianey.oxapp.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.UICircularImage;
import com.kalianey.oxapp.views.activities.Message;
import com.kalianey.oxapp.views.activities.Profile;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kalianey on 10/08/2015.
 */
public class ConversationListAdapter extends ArrayAdapter<ModelConversation> {

    private LayoutInflater inflater;
    private List<ModelConversation> conversations; //data
    private Activity listContext;
    private int listRowLayoutId;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private String PACKAGE = "IDENTIFY";


    public ConversationListAdapter(Activity context, int resource, List<ModelConversation> objs) {
        super(context, resource, objs);
        conversations = objs;
        listContext = context;
        listRowLayoutId = resource;
    }

    @Override
    public int getCount() {
        return conversations.size();
    }

    @Override
    public ModelConversation getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(ModelConversation item) {
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
            viewHolder.userStatusImageView = (ImageView) row.findViewById(R.id.userStatus);
            viewHolder.username = (TextView) row.findViewById(R.id.username);
            viewHolder.previewText = (TextView) row.findViewById(R.id.previewText);
            viewHolder.date = (TextView) row.findViewById(R.id.date);

            row.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) row.getTag();
        }

        viewHolder.conversation = conversations.get(position);

        //We can now display the data
        // viewHolder.avatarImageView.setImageUrl(viewHolder.conversation.getAvatarUrl(), imageLoader); //TODO: placeholder
        Picasso.with(listContext)
                .load(viewHolder.conversation.getAvatarUrl())
                .noFade()
                .into(viewHolder.avatarImageView);
        viewHolder.username.setText(viewHolder.conversation.getName());
        viewHolder.previewText.setText(viewHolder.conversation.getPreviewText());
        viewHolder.date.setText(viewHolder.conversation.getTimeLabel());
        //viewHolder.userStatusImageView : here change colour of image of  here according to bool

        //Set onClick item
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.avatarImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                QueryAPI query = new QueryAPI();
                query.user(finalViewHolder.conversation.getOpponentId(), new QueryAPI.ApiResponse<ModelUser>() {
                    @Override
                    public void onCompletion(ModelUser user) {

                        Intent i = new Intent(listContext, Profile.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("userObj", user);

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
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(listContext, Message.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("convObj", finalViewHolder.conversation);
                i.putExtras(mBundle);
                listContext.startActivity(i);
            }
        });

        return row;
    }

    //allow us to put all the subviews so when we create listAdapters all our view are recycled (performance)
    public class ViewHolder {

        ModelConversation conversation;
        UICircularImage avatarImageView;
        ImageView userStatusImageView;
        TextView username;
        TextView previewText;
        TextView date;

    }

}
