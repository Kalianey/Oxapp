package com.kalianey.oxapp.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private final int CELL_RCV = 0;
    private final int CELL_SENT = 1;

    private int w = Utility.convertDiptoPix(200);
    private int h = Utility.convertDiptoPix(150);

    private LayoutInflater inflater;
    private Activity context;
    private ArrayList<ModelMessage> messages = new ArrayList<ModelMessage>(); //data

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private ModelUser loggedInUser = AppController.getInstance().getLoggedInUser();
    private ModelUser senderUser;
    private Integer countMessage = 0;


    public MessageListAdapter(Activity context, ArrayList<ModelMessage> objs) {
        super();
        this.messages = objs;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public ModelUser getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(ModelUser senderUser) {
        this.senderUser = senderUser;
    }

    public void add(int position, ModelMessage item) {
        messages.add(position, item);
        notifyItemInserted(position);
    }

//    public void remove(ModelMessage item) {
//        int position = messages.indexOf(item);
//        messages.remove(position);
//        notifyItemRemoved(position);
//    }

    @Override
    public MessageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == CELL_SENT) {
            v = inflater.inflate(R.layout.message_item_sent, parent, false);
        } else {
            v = inflater.inflate(R.layout.message_item_rcv, parent, false);
        }
        ViewHolder vh = new MessageListAdapter.ViewHolder(v);
        if (viewType == CELL_SENT) {
            vh.msgBubbleBg = R.drawable.bubble_out;
        }
        else {
            vh.msgBubbleBg = R.drawable.bubble_in;
        }

        String avatarUrl = (viewType == CELL_SENT) ? loggedInUser.getAvatar_url() : senderUser.getAvatar_url();
        //Avatar
        Picasso.with(context)
                .load(avatarUrl)
                .noFade()
                .into(vh.avatarImageView);

        return vh;
    }

    @Override
    public int getItemViewType(int position) {

        // Define a way to determine which layout to use, here it's just evens and odds.
        String senderId = messages.get(position).getSenderId();
        String userId = loggedInUser.getUserId();

        int type = senderId.equals(userId)? CELL_SENT : CELL_RCV;

        return type;
    }

    @Override
    public void onBindViewHolder(final MessageListAdapter.ViewHolder viewHolder, int position) {

        /** ViewHolder is set, so we can initialize the data **/
        final int pos = position;
        int cellType = this.getItemViewType(position);


        viewHolder.position = pos;
        viewHolder.message = messages.get(pos);


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
                    Bitmap result = Utility.drawMediaWithMask(context, bitmap, viewHolder.msgBubbleBg, w, h);
                    viewHolder.attachment.setImageBitmap(result);
                    viewHolder.attachment.setScaleType(ImageView.ScaleType.FIT_XY);
                    viewHolder.loadingBar.setVisibility(View.GONE);
                    viewHolder.attachment.setVisibility(View.VISIBLE);
                }

            }
            //Otherwise, we download it from the server
            else {
                viewHolder.loadingBar.setVisibility(View.VISIBLE);
                viewHolder.attachment.setVisibility(View.GONE);
                final Bitmap mask = BitmapFactory.decodeResource(context.getResources(), viewHolder.msgBubbleBg);

                final ViewHolder finalViewHolder = viewHolder;

                viewHolder.target = new Target() {
                    @Override
                    public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                        Bitmap result;
                        //the first time we call, from is set because passing through picasso, we draw the mask and set it in imageCache
                        if ( from != null ) {
                            result = Utility.drawMediaWithMask(context, bitmap, finalViewHolder.msgBubbleBg, w, h);
                            finalViewHolder.message.setImageCache(result);
                        }else {
                            result = finalViewHolder.message.getImageCache();
                        }

                        finalViewHolder.loadingBar.setVisibility(View.GONE);
                        finalViewHolder.attachment.setVisibility(View.VISIBLE);
                        finalViewHolder.attachment.setImageBitmap(result);
                        finalViewHolder.attachment.setScaleType(ImageView.ScaleType.FIT_XY);

                        //OnClick, open the image in full screen
                        finalViewHolder.attachment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Create the array to hold our photo
                                ArrayList<ModelAttachment> photos = new ArrayList<ModelAttachment>();
                                ModelAttachment attachment = new ModelAttachment();
                                attachment.setId("0");
                                attachment.setUrl(messages.get(pos).getDownloadUrl());
                                photos.add(0, attachment);
                                //Create the intent
                                Intent intent = new Intent(context, ProfilePhotos.class);
                                Bundle mBundle = new Bundle();
                                mBundle.putSerializable("photoList", photos);
                                mBundle.putInt("photoIndex", 0);
                                intent.putExtras(mBundle);
                                context.startActivity(intent);
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

                Bitmap imageCache = viewHolder.message.getImageCache();
                if(imageCache == null) {
                    Picasso.with(context)
                            .load(viewHolder.message.getDownloadUrl())
                            .into(viewHolder.target);
                }
                else
                {
                    viewHolder.target.onBitmapLoaded(imageCache, null);
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        if (messages != null) {
            return messages.size();
        } else {
            return 0;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

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

        public ViewHolder(View v) {
            super(v);
            this.loadingBar = (RelativeLayout) v.findViewById(R.id.loadingPanel);
            this.avatarImageView = (UICircularImage) v.findViewById(R.id.avatarImageView);
            this.attachment = (ImageView) v.findViewById(R.id.attachment);
            this.text = (TextView) v.findViewById(R.id.text);
            this.date = (TextView) v.findViewById(R.id.date);
            this.recipientRead = (ImageView) v.findViewById(R.id.recipientRead);
        }

    }

}
