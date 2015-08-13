package com.kalianey.oxapp.views.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelMessage;
import com.kalianey.oxapp.utils.AppController;

import java.util.List;

/**
 * Created by kalianey on 13/08/2015.
 */
public class MessageListAdapter extends ArrayAdapter<ModelMessage> {

    private LayoutInflater inflater;
    private List<ModelMessage> messages; //data
    private Activity listContext;
    private int listRowLayoutId;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public MessageListAdapter(Activity context, int resource, List<ModelMessage> objs) {
        super(context, resource, objs);
        messages = objs;
        listContext = context;
        listRowLayoutId = resource;
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
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View row = convertView;
        ViewHolder viewHolder = null;

        //Row holds our layout
        if (row == null) {
            inflater = LayoutInflater.from(listContext);

//            String senderId = viewHolder.message.getSenderId();
//            String userId = AppController.getInstance().getLoggedInUser().getUserId();
//
//            if (senderId == userId) {
//                row = inflater.inflate(listRowLayoutId, parent, false);
//            }
//            else {
//                row = inflater.inflate(R.layout.chat_item_rcv, parent, false);
//            }

            row = inflater.inflate(listRowLayoutId, parent, false) ; //resource, viewGroup, attachToGroup

            viewHolder = new ViewHolder();

            //Get references to our views
            //viewHolder.avatarImageView = (NetworkImageView) row.findViewById(R.id.avatarImageView);
            viewHolder.text = (TextView) row.findViewById(R.id.lbl1);

            row.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) row.getTag();
        }

        viewHolder.message = messages.get(position);

        //We can now display the data
        //viewHolder.avatarImageView.setImageUrl(viewHolder.message.get(), imageLoader);
        viewHolder.text.setText(viewHolder.message.getText());

        return  row;
    }


    public class ViewHolder {

        ModelMessage message;
        NetworkImageView avatarImageView;
        TextView text;

    }
}
