package com.kalianey.oxapp.views.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.UICircularImage;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements ClusterManager.OnClusterItemClickListener<ModelUser>, ClusterManager.OnClusterItemInfoWindowClickListener<ModelUser> {

    private ClusterManager<ModelUser> mClusterManager;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private QueryAPI query = new QueryAPI();
    private List<ModelUser> users = new ArrayList<ModelUser>();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    //UI
    NetworkImageView avatarImageView;
    TextView name;
    TextView infos;
    TextView distance;
    RelativeLayout detailView;
    private FrameLayout mNavigationTop;
    private TextView mNavigationTitle;
    private Button mNavigationBackBtn;

    //Vars
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setUpMapIfNeeded();

        detailView = (RelativeLayout) findViewById(R.id.detailView);
        avatarImageView = (NetworkImageView) findViewById(R.id.imageView);
        name = (TextView) findViewById(R.id.name);
        infos = (TextView) findViewById(R.id.info);
        distance = (TextView) findViewById(R.id.distance);

        mNavigationTop = (FrameLayout) findViewById(R.id.layout_top);
        mNavigationTitle = (TextView) findViewById(R.id.titleBar);
        mNavigationBackBtn = (Button) findViewById(R.id.title_bar_left_menu);
        mNavigationTitle.setText("Near you");

        mNavigationBackBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        //Get User Location
        mLocationManager = AppController.getInstance().getLocationManager();
        AppController.getInstance().updateLocation();


        query.nearUsers(new QueryAPI.ApiResponse<List<ModelUser>>() {
            @Override
            public void onCompletion(List<ModelUser> result) {

                users = result;

                setUpClusterer();

                mNavigationTop.bringToFront();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        //Location : request updates every 10 minutes & 1km
        AppController.getInstance().updateLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.getInstance().stopUpdateLocation();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
    }

    private void setUpClusterer() {

        //Get User position
        Double lat = AppController.getInstance().getCurrentUserLat();
        Double lng = AppController.getInstance().getCurrentUserLng();

        // Position the map.
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 5));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<ModelUser>(this, getMap());

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        getMap().setOnCameraChangeListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        mClusterManager.setRenderer(new PersonRenderer());
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        for (int i = 0; i < users.size(); i++) {

            ModelUser user = users.get(i);

            mClusterManager.addItem(user);
        }

    }

    protected GoogleMap getMap() {
        setUpMapIfNeeded();
        return mMap;
    }

    @Override
    public boolean onClusterItemClick(final ModelUser item) {
        // Does nothing, but I could go into the user's profile page.
        Log.d("User clicked:", item.getName());
        avatarImageView.setImageUrl(item.getAvatar_url(), imageLoader);
        name.setText(item.getName());
        infos.setText(item.getAge());
        double d = Double.parseDouble(item.getDistance());
        distance.setText( String.format("%.1f", d) + "Km");
        detailView.setVisibility(View.VISIBLE);

        detailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Profile.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("userObj", item);
                i.putExtras(mBundle);
                startActivity(i);

            }
        });
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(ModelUser item) {
        // Does nothing, but you could go into the user's profile page, for example.
        Log.d("User Window clicked:", item.getName());
    }

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    private class PersonRenderer extends DefaultClusterRenderer<ModelUser> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final UICircularImage mImageView;
        //private final int mDimension;

        public PersonRenderer() {
            super(getApplicationContext(), getMap(), mClusterManager);

            View profile = getLayoutInflater().inflate(R.layout.map_marker_item, null);
            mIconGenerator.setContentView(profile);
            mImageView = (UICircularImage) profile.findViewById(R.id.avatarImageView);

//            mImageView = new UICircularImage(getApplicationContext());
//            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
//            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
//            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
//            mImageView.setPadding(padding, padding, padding, padding);
//            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(final ModelUser user, final MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            // mImageView.setImageUrl(user.getAvatar_url(), imageLoader);
            //Log.d("Avatar url:", user.getAvatar_url());
//            Picasso.with(getApplicationContext())
//                    .load(user.getAvatar_url())
//                    .noFade()
//                    .into(mImageView, new com.squareup.picasso.Callback() {
//                        @Override
//                        public void onSuccess() {
//                            mClusterManager.cluster(); //TODO: remove, doesn't do anything
//                            //http://stackoverflow.com/questions/22287207/clustermanager-repaint-markers-of-google-maps-v2-utils
//                        }
//
//                        @Override
//                        public void onError() {
//
//                        }
//                    });

//            Picasso.with(getApplicationContext())
//                    .load(user.getAvatar_url())
//                    .into(new com.squareup.picasso.Target() {
//                        @Override
//                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                            // let's find marker for this user
//                            Marker markerToChange = null;
//                            for (Marker marker : mClusterManager.getMarkerCollection().getMarkers()) {
//                                if (marker.getPosition().equals(user.getPosition())) {
//                                    markerToChange = marker;
//                                }
//                            }
//                            // if found - change icon
//                            if (markerToChange != null) {
//                                markerToChange.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
//                            }
//                        }
//                        @Override
//                        public void onBitmapFailed(Drawable errorDrawable) {
//                        }
//                        @Override
//                        public void onPrepareLoad(Drawable placeHolderDrawable) {
//                        }
//                    });

            //Works but size ugly and not round
//            Glide.with(getApplicationContext()).
//                    load(user.getAvatar_url())
//                    .asBitmap()
//                    .fitCenter()
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
//                            // let's find marker for this user
//                            Marker markerToChange = null;
//                            for (Marker marker : mClusterManager.getMarkerCollection().getMarkers()) {
//                                if (marker.getPosition().equals(user.getPosition())) {
//                                    markerToChange = marker;
//                                }
//                            }
//                            // if found - change icon
//                            if (markerToChange != null) {
//                                markerToChange.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
//                            }
//                        }
//                    });
//
//            Bitmap icon = mIconGenerator.makeIcon();
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(user.getName());

            Drawable drawable =getApplicationContext().getResources().getDrawable(R.drawable.annotation);
            mImageView.setImageDrawable(drawable);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }


    }
}
