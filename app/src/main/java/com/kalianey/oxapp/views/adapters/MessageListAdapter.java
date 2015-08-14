package com.kalianey.oxapp.views.adapters;

import android.app.Activity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.models.ModelMessage;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.QueryAPI;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kalianey on 13/08/2015.
 */
public class MessageListAdapter extends ArrayAdapter<ModelMessage> {
    private final int CELL_LEFT = 0;
    private final int CELL_RIGHT = 1;

    private LayoutInflater inflater;
    private List<ModelMessage> messages; //data
    private Activity listContext;
    private int listRowLayoutId;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private ModelUser loggedInUser = AppController.getInstance().getLoggedInUser();
    private ModelUser senderUser;

    public MessageListAdapter(Activity context, int resource, List<ModelMessage> objs) {
        super(context, resource, objs);
        messages = objs;
        listContext = context;
        listRowLayoutId = resource;
    }

    public ModelUser getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(ModelUser senderUser) {
        this.senderUser = senderUser;
    }


    @Override
    public int getCount()
    {
        return messages.size();
    }

    @Override
    public ModelMessage getItem(int position)
    {
        return super.getItem(position);
    }

    @Override
    public int getPosition(ModelMessage item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position)
    {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        // Define a way to determine which layout to use, here it's just evens and odds.

        String senderId = messages.get(position).getSenderId();
        String userId = loggedInUser.getUserId();

        int type = senderId.equals(userId)?CELL_LEFT:CELL_RIGHT;

        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 2; // Count of different layouts
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View row = convertView;
        ViewHolder viewHolder = null;

        //Row holds our layout
        if (row == null) {

            viewHolder = new ViewHolder();
            inflater = LayoutInflater.from(listContext);

            viewHolder.message = messages.get(position);
            int cellType = this.getItemViewType(position);
            if (cellType == CELL_LEFT) {
                row = inflater.inflate(R.layout.chat_item_sent, parent, false);
            }
            //else if (cellType == CELL_RIGHT) {
            else {
                row = inflater.inflate(R.layout.chat_item_rcv, parent, false);
            }

            //Get references to our views
            viewHolder.avatarImageView = (NetworkImageView) row.findViewById(R.id.avatarImageView);
            viewHolder.text = (TextView) row.findViewById(R.id.text);
            viewHolder.date = (TextView) row.findViewById(R.id.date);

            row.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) row.getTag();
        }

        viewHolder.message = messages.get(position);

        //We can now display the data
        String avatarUrl = loggedInUser.getAvatar_url();
        viewHolder.avatarImageView.setImageUrl(avatarUrl, imageLoader);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String stringDate = sdf.format(new Date(viewHolder.message.getTimeStamp() * 1000));
        viewHolder.date.setText(stringDate);
        viewHolder.text.setText(viewHolder.message.getText());

        return  row;
    }


    public class ViewHolder {

        ModelMessage message;
        NetworkImageView avatarImageView;
        TextView text;
        TextView date;
        TextView delivered;

    }
}
