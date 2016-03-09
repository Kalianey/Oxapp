package com.kalianey.oxapp.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelAttachment;
import com.kalianey.oxapp.utils.AppController;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfilePhotosFragment extends Fragment {


    private ViewPager awesomePager;
    private Context cxt;
    private AwesomePagerAdapter awesomeAdapter;
    private ArrayList<ModelAttachment> photos;
    private int currentIndex;
    private Boolean toggleCloseBtn = false;

    /** Called when the activity is first created. */

    public ProfilePhotosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_photos, container, false);

        cxt = getActivity();

        photos = (ArrayList<ModelAttachment>) getActivity().getIntent().getSerializableExtra("photoList");
        currentIndex = (int) getActivity().getIntent().getSerializableExtra("photoIndex");

        awesomeAdapter = new AwesomePagerAdapter();
        awesomePager = (ViewPager) view.findViewById(R.id.awesomepager);
        awesomePager.setAdapter(awesomeAdapter);
        awesomePager.setCurrentItem(currentIndex);

        return view;
    }



    private class AwesomePagerAdapter extends PagerAdapter {

        private NetworkImageView mImageView;
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();


        @Override
        public int getCount() {
            return photos.size();
        }

        /**
         * Create the page for the given position.  The adapter is responsible
         * for adding the view to the container given here, although it only
         * must ensure this is done by the time it returns from
         * {@link #finishUpdate(android.view.ViewGroup)}.
         *
         * @param collection The containing View in which the page will be shown.
         * @param position The page position to be instantiated.
         * @return Returns an Object representing the new page.  This does not
         * need to be a View, but can be some other container of the page.
         */
        @Override
        public Object instantiateItem(ViewGroup collection, int position) {

            LayoutInflater inflater = LayoutInflater.from(cxt);
            View parentView = inflater.inflate(R.layout.fragment_profile_photos_item, collection, false) ; //resource, viewGroup, attachToGroup

            final NetworkImageView mImageView  = (NetworkImageView) parentView.findViewById(R.id.imageView);
            final ImageButton closeBtn = (ImageButton) parentView.findViewById(R.id.closeBtn);

            mImageView.setImageUrl(photos.get(position).getUrl(), imageLoader);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (toggleCloseBtn) {
                        closeBtn.setVisibility(View.INVISIBLE);
                    } else {
                        closeBtn.setVisibility(View.VISIBLE);
                    }
                    toggleCloseBtn = !toggleCloseBtn;
                }
            });

            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            collection.addView(parentView);

            return parentView;
        }

        /**
         * Remove a page for the given position.  The adapter is responsible
         * for removing the view from its container, although it only must ensure
         * this is done by the time it returns from {@link #finishUpdate(android.view.ViewGroup)}.
         *
         * @param collection The containing View from which the page will be removed.
         * @param position The page position to be removed.
         * @param view The same object that was returned by
         * {@link #instantiateItem(android.view.View, int)}.
         */
        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }


        /**
         * Determines whether a page View is associated with a specific key object
         * as returned by instantiateItem(ViewGroup, int). This method is required
         * for a PagerAdapter to function properly.
         * @param view Page View to check for association with object
         * @param object Object to check for association with view
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view==object);
        }


        /**
         * Called when the a change in the shown pages has been completed.  At this
         * point you must ensure that all of the pages have actually been added or
         * removed from the container as appropriate.
         * @param arg0 The containing View which is displaying this adapter's
         * page views.
         */
        @Override
        public void finishUpdate(ViewGroup arg0) {}


        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {}

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(ViewGroup arg0) {}

    }

}
