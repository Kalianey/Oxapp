package com.kalianey.oxapp.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kalianey.oxapp.AppController;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelConversation;

import java.util.ArrayList;
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
            viewHolder.avatarImageView = (NetworkImageView) row.findViewById(R.id.avatarImageView);
            viewHolder.userStatusImageView = (ImageView) row.findViewById(R.id.userStatus);
            viewHolder.username = (TextView) row.findViewById(R.id.username);
            viewHolder.previewText = (TextView) row.findViewById(R.id.previewText);

            row.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) row.getTag();
        }

        viewHolder.conversation = conversations.get(position);

        //We can now display the data
        viewHolder.avatarImageView.setImageUrl(viewHolder.conversation.getAvatarUrl(), imageLoader);
        viewHolder.username.setText(viewHolder.conversation.getName());
        viewHolder.previewText.setText(viewHolder.conversation.getPreviewText());
        //viewHolder.userStatusImageView : here change colour of image of  here according to bool

        return row;
    }

    //allow us to put all the subviews so when we create listAdapters all our view are recycled (performance)
    public class ViewHolder {

        ModelConversation conversation;
        NetworkImageView avatarImageView;
        ImageView userStatusImageView;
        TextView username;
        TextView previewText;

    }

}
