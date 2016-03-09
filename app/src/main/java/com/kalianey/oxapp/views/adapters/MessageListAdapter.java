package com.kalianey.oxapp.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelAttachment;
import com.kalianey.oxapp.models.ModelMessage;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.UICircularImage;
import com.kalianey.oxapp.utils.Utility;
import com.kalianey.oxapp.views.activities.ProfilePhotos;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kalianey on 13/08/2015.
 */
public class MessageListAdapter extends ArrayAdapter<ModelMessage> {
    private final int CELL_RCV = 0;
    private final int CELL_SENT = 1;

    private int w = 500;
    private int h = 380;

    private LayoutInflater inflater;
    private List<ModelMessage> messages; //data
    private Activity listContext;
    private int listRowLayoutId;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private ModelUser loggedInUser = AppController.getInstance().getLoggedInUser();
    private ModelUser senderUser;
    private Integer countMessage = 0;
    private ViewHolder viewHolder;

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

        int type = senderId.equals(userId)? CELL_SENT:CELL_RCV;

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
        viewHolder = null;
        final int pos = position;

        int cellType = this.getItemViewType(position);
        String avatarUrl = (cellType == CELL_SENT) ? loggedInUser.getAvatar_url() : senderUser.getAvatar_url();

        //Row holds our layout
        if (row == null) {

            viewHolder = new ViewHolder();
            inflater = LayoutInflater.from(listContext);

            if (cellType == CELL_SENT) {
                row = inflater.inflate(R.layout.message_item_sent, parent, false);
            }
            else {
                row = inflater.inflate(R.layout.message_item_rcv, parent, false);
            }

            //Get references to our views
            viewHolder.loadingBar = (RelativeLayout) row.findViewById(R.id.loadingPanel);
            viewHolder.avatarImageView = (UICircularImage) row.findViewById(R.id.avatarImageView);
            viewHolder.attachment = (ImageView) row.findViewById(R.id.attachment);
            viewHolder.text = (TextView) row.findViewById(R.id.text);
            viewHolder.date = (TextView) row.findViewById(R.id.date);
            viewHolder.recipientRead = (ImageView) row.findViewById(R.id.recipientRead);

            row.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) row.getTag();
        }

        viewHolder.position = pos;
        viewHolder.message = messages.get(pos);

        if (cellType == CELL_SENT) {
            viewHolder.msgBubbleBg = R.drawable.bubble_out;
        }
        else {
            viewHolder.msgBubbleBg = R.drawable.bubble_in;
        }

        /** ViewHolder is set, so we can initialize the data **/

        //Avatar
        Picasso.with(listContext)
                .load(avatarUrl)
                .noFade()
                .into(viewHolder.avatarImageView);

        //Date
        countMessage ++;
        if (countMessage < 3) {
            viewHolder.date.setVisibility(View.GONE);
        }
        else {
            viewHolder.date.setVisibility(View.VISIBLE);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String stringDate = sdf.format(new Date(viewHolder.message.getTimeStamp() * 1000));
            viewHolder.date.setText(stringDate);
            countMessage = 0;
        }

        //Msg Read
        int read = viewHolder.message.getRecipientRead();
        if (read == 0) {
            viewHolder.recipientRead.setImageResource(R.drawable.tick_icon);
        }
        else if (read == 1) {
            viewHolder.recipientRead.setImageResource(R.drawable.double_tick_icon);
        }
        else {
            viewHolder.recipientRead.setVisibility(View.GONE);
        }

        //If the message is just Text
        if (!viewHolder.message.getIsMediaMessage()){
            viewHolder.attachment.setVisibility(View.GONE);
            viewHolder.loadingBar.setVisibility(View.GONE);
            viewHolder.text.setText(viewHolder.message.getText());
            viewHolder.text.setVisibility(View.VISIBLE);
        }
        //If the msg is an Image
        else {
            viewHolder.text.setVisibility(View.GONE);

            // If the user just sent the image, we don't have a download url, so we load the image directly from the local file
            if (viewHolder.message.getDownloadUrl() == null) {

                if(viewHolder.message.getImage().exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(viewHolder.message.getImage().getAbsolutePath());
                    Bitmap result = Utility.drawMediaWithMask(listContext, bitmap, viewHolder.msgBubbleBg, w, h);
                    viewHolder.attachment.setImageBitmap(result);
                    viewHolder.attachment.setScaleType(ImageView.ScaleType.FIT_XY);
                    viewHolder.loadingBar.setVisibility(View.GONE);
                    viewHolder.attachment.setVisibility(View.VISIBLE);
                }

            }
            //Otherwise, we download it from the server
            else {
                row.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                final Bitmap mask = BitmapFactory.decodeResource(listContext.getResources(), viewHolder.msgBubbleBg);

                final ViewHolder finalViewHolder = viewHolder;

                viewHolder.target = new Target() {
                    @Override
                    public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){

                        Bitmap result = Utility.drawMediaWithMask(listContext, bitmap, viewHolder.msgBubbleBg, w, h);
                        Drawable drawable = new BitmapDrawable(listContext.getResources(), result);
                        viewHolder.attachment.setVisibility(View.VISIBLE);
                        viewHolder.loadingBar.setVisibility(View.GONE);
                        viewHolder.attachment.setImageBitmap(result);
                        viewHolder.attachment.setScaleType(ImageView.ScaleType.FIT_XY);
                        notifyDataSetChanged();

                        //OnClick, open the image in full screen
                        viewHolder.attachment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Create the array to hold our photo
                                ArrayList<ModelAttachment> photos = new ArrayList<ModelAttachment>();
                                ModelAttachment attachment = new ModelAttachment();
                                attachment.setId("0");
                                attachment.setUrl(messages.get(pos).getDownloadUrl());
                                photos.add(0, attachment);
                                //Create the intent
                                Intent intent = new Intent(listContext, ProfilePhotos.class);
                                Bundle mBundle = new Bundle();
                                mBundle.putSerializable("photoList", photos);
                                mBundle.putInt("photoIndex", 0);
                                intent.putExtras(mBundle);
                                listContext.startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                Picasso.with(listContext)
                        .load(viewHolder.message.getDownloadUrl())
                        .into(viewHolder.target);
            }
        }

        return  row;
    }


    public class ViewHolder {

        ModelMessage message;
        RelativeLayout loadingBar;
        UICircularImage avatarImageView;
        TextView text;
        TextView date;
        ImageView recipientRead;
        ImageView attachment;
        int msgBubbleBg;
        int position=-1;
        Target target;

    }


}
