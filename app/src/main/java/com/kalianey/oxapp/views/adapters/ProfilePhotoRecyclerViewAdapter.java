package com.kalianey.oxapp.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelAttachment;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.views.activities.ProfilePhotos;

import java.util.ArrayList;

/**
 * Created by kalianey on 18/08/2015.
 */
public class ProfilePhotoRecyclerViewAdapter extends  RecyclerView.Adapter<ProfilePhotoRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<ModelAttachment> photos; //data
    private ModelUser user;
    private Activity listContext;
    //private Context listContext;
    private int listRowLayoutId;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public ProfilePhotoRecyclerViewAdapter(Activity context) {
        photos = new ArrayList<>();
        listContext = context;
        //this.notifyDataSetChanged();
    }

    public void setPhotos(ArrayList< ModelAttachment> photoList) {
        photos.clear();
        photos.addAll(photoList);
        this.notifyItemRangeInserted(0, photos.size() - 1);
    }

    public void setUser(ModelUser user) {
        this.user = user;
    }

    @Override
    public ProfilePhotoRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int i) {

        final View itemView = LayoutInflater.from(listContext).inflate(R.layout.profile_photogrid_item, parent, false);



        return new ProfilePhotoRecyclerViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProfilePhotoRecyclerViewAdapter.ViewHolder viewHolder, final int i) {

        ModelAttachment photo = photos.get(i);
        viewHolder.imageView.setImageUrl(photo.getUrl(), imageLoader);

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(listContext, ProfilePhotos.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("photoList", photos);
                mBundle.putInt("photoIndex", viewHolder.getAdapterPosition());
                intent.putExtras(mBundle);
                listContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        ModelAttachment photo;
        public NetworkImageView imageView;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (NetworkImageView) itemView.findViewById(R.id.imageView);
        }
    }


}
