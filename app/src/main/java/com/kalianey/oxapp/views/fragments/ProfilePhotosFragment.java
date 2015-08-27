package com.kalianey.oxapp.views.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelAttachment;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfilePhotosFragment extends Fragment {


    private ViewPager awesomePager;
    private Context cxt;
    private AwesomePagerAdapter awesomeAdapter;
    private List<ModelAttachment> photos;
    private ModelAttachment currentPhoto;

    /** Called when the activity is first created. */

    public ProfilePhotosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_photos, container, false);

        cxt = getActivity();

        ModelUser user = (ModelUser) getActivity().getIntent().getSerializableExtra("user");
        //photos = user.getPhotos();
        photos = AppController.getInstance().getLoggedInUser().getPhotos();
        //currentPhoto = (ModelAttachment) getActivity().getIntent().getSerializableExtra("photo");

        awesomeAdapter = new AwesomePagerAdapter();
        awesomePager = (ViewPager) view.findViewById(R.id.awesomepager);
        awesomePager.setAdapter(awesomeAdapter);

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
            LayoutInflater inflater = (LayoutInflater) cxt.getSystemService(cxt.LAYOUT_INFLATER_SERVICE);

//            TextView tv = new TextView(cxt);
//            tv.setText("Hello" + position);
//            tv.setTextColor(Color.WHITE);
//            tv.setTextSize(30);

            mImageView = new NetworkImageView(cxt);

            mImageView.setImageUrl(photos.get(position).getUrl(), imageLoader);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(500, 500));

            collection.addView(mImageView,0);

            return mImageView;
            //return tv;
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
            collection.removeView((TextView) view);
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
