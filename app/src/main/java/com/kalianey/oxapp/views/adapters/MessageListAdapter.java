package com.kalianey.oxapp.views.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelMessage;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.UICircularImage;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kalianey on 13/08/2015.
 */
public class MessageListAdapter extends ArrayAdapter<ModelMessage> {
    private final int CELL_RCV = 0;
    private final int CELL_SENT = 1;

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

        int cellType = this.getItemViewType(position);
        String avatarUrl = (cellType == CELL_SENT) ? loggedInUser.getAvatar_url() : senderUser.getAvatar_url();

        //Row holds our layout
        if (row == null) {

            viewHolder = new ViewHolder();
            inflater = LayoutInflater.from(listContext);

            viewHolder.message = messages.get(position);

            if (cellType == CELL_SENT) {
                row = inflater.inflate(R.layout.message_item_sent, parent, false);
            }
            else {
                row = inflater.inflate(R.layout.message_item_rcv, parent, false);
            }

            //Get references to our views
            viewHolder.linearLayout = (LinearLayout) row.findViewById(R.id.v1);
            viewHolder.avatarImageView = (UICircularImage) row.findViewById(R.id.avatarImageView);
            viewHolder.text = (TextView) row.findViewById(R.id.text);
            viewHolder.date = (TextView) row.findViewById(R.id.date);
            viewHolder.attachment = (ImageView) row.findViewById(R.id.attachment);
            row.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) row.getTag();
        }

        viewHolder.message = messages.get(position);
        countMessage ++;
        if (countMessage < 3) {
            //viewHolder.date.setVisibility(View.GONE);
        }
        else {
            viewHolder.date.setVisibility(View.VISIBLE);
            countMessage = 0;
        }
        //We can now display the data
        //viewHolder.avatarImageView.setImageUrl(avatarUrl, imageLoader); //TODO: Placeholder

        Picasso.with(listContext)
                .load(avatarUrl)
                .noFade()
                .into(viewHolder.avatarImageView);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String stringDate = sdf.format(new Date(viewHolder.message.getTimeStamp() * 1000));
        viewHolder.date.setText(stringDate);
        viewHolder.text.setText(viewHolder.message.getText());

        if (viewHolder.message.getIsMediaMessage()){

            viewHolder.text.setVisibility(View.GONE);

            // If the user just sent the image, we don't have a download url
            // so we load the image directly from the file on the phone
            if (viewHolder.message.getDownloadUrl() == null) {
                if(viewHolder.message.getImage().exists()){
//                    Bitmap myBitmap = BitmapFactory.decodeFile(viewHolder.message.getImage().getAbsolutePath());
//                    viewHolder.attachment.setImageBitmap(myBitmap);

                    //test crop image
                    Bitmap original = BitmapFactory.decodeFile(viewHolder.message.getImage().getAbsolutePath());
                    Bitmap mask = BitmapFactory.decodeResource(listContext.getResources(),R.drawable.bubble_out);
                    Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas mCanvas = new Canvas(result);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                    mCanvas.drawBitmap(original, 0, 0, null);
                    mCanvas.drawBitmap(mask, 0, 0, paint);
                    paint.setXfermode(null);
                    viewHolder.attachment.setImageBitmap(result);
                    viewHolder.attachment.setScaleType(ImageView.ScaleType.CENTER);
                    //viewHolder.attachment.setBackgroundResource(R.drawable.background_frame);
                }
            }
            else {
//                Picasso.with(listContext)
//                        .load(viewHolder.message.getDownloadUrl())
//                        .noFade()
//                        .into(viewHolder.attachment);

                final Bitmap mask = BitmapFactory.decodeResource(listContext.getResources(), R.drawable.bubble_out);

//                Drawable drawMask = ContextCompat.getDrawable(listContext, R.drawable.bubble_out);
//                //drawMask.setBounds(0, 0, 200, 150);
//                Drawable drawable = new ScaleDrawable(drawMask, 0, 200, 150).getDrawable();
//                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//                final Bitmap mask = bitmapDrawable.getBitmap();

                //final Bitmap mask = ((BitmapDrawable) drawable).getBitmap();

                Glide.with(listContext).
                    load(viewHolder.message.getDownloadUrl())
                    .asBitmap()
                    .override(mask.getWidth(), mask.getHeight())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {

                                Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
                                Canvas mCanvas = new Canvas(result);
                                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                                mCanvas.drawBitmap(bitmap, 0, 0, null);
                                mCanvas.drawBitmap(mask, 0, 0, paint);
                                paint.setXfermode(null);
                                Drawable drawable = new BitmapDrawable(listContext.getResources(), result);
                                viewHolder.linearLayout.setBackground(drawable);
//                                viewHolder.attachment.setImageBitmap(result);
//                                viewHolder.attachment.setScaleType(ImageView.ScaleType.CENTER);
//                                viewHolder.attachment.setVisibility(View.VISIBLE);

                            }
                        });
            }
        } else {
            viewHolder.attachment.setVisibility(View.GONE);
            viewHolder.text.setVisibility(View.VISIBLE);
        }

        return  row;
    }


    public class ViewHolder {

        ModelMessage message;
        LinearLayout linearLayout;
        UICircularImage avatarImageView;
        TextView text;
        TextView date;
        TextView delivered;
        ImageView attachment;

    }
}
