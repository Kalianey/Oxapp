package com.kalianey.oxapp.views.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.UICircularImage;
import com.kalianey.oxapp.utils.UIParallaxScroll;
import com.kalianey.oxapp.utils.UITabs;
import com.kalianey.oxapp.views.activities.ListItem;
import com.kalianey.oxapp.views.adapters.DetailListAdapter;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment {

    private ModelUser user;

    //Configuration
    public static final int DURATION = 500; // in ms
    public static final String PACKAGE = "IDENTIFY";

    //UI Elements
    private NetworkImageView cImageView;
    private UICircularImage mImageView;
    private TextView mTextView;
    private RelativeLayout mLayoutContainer;
    private FrameLayout mNavigationTop;
    private TextView mNavigationTitle;
    private Button mNavigationBackBtn;
    private TextView mTitleView;
    private UICircularImage mShare;

    //Vars
    private int delta_top;
    private int delta_left;
    private float scale_width;
    private float scale_height;
    String title;
    int imgId;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    String lor1 = "Pellentesque in luctus dui, non egestas nisl. Donec sapien ante, faucibus a sem at, tincidunt dictum quam. Sed vel blandit neque. Maecenas tincidunt at sem vel sodales. Nullam dignissim eros id tellus commodo, eu vulputate massa accumsan.<br><br>Ut eget volutpat turpis. Praesent ac auctor nisi, sed imperdiet augue. Aenean consequat est vel odio molestie pellentesque. Suspendisse rhoncus velit dolor, at ultrices nulla ullamcorper a. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Vivamus nec felis elit. Mauris at erat euismod leo sagittis gravida in id magna.";
    String lor2 = "Donec ornare eleifend turpis. Cras consectetur at neque sit amet bibendum. Nulla metus dui, porta vel mollis vitae, ornare sit amet lectus. Integer imperdiet quam eleifend nisl dictum vehicula. Suspendisse pharetra aliquet porttitor. Maecenas nec pharetra purus. Sed scelerisque suscipit faucibus. Etiam hendrerit tellus risus, et interdum tortor facilisis quis.";
    String lor3 = "Etiam tristique, sapien non rhoncus vestibulum, erat augue suscipit velit, vestibulum viverra justo nibh ut nibh. Vivamus pulvinar pharetra scelerisque. Curabitur ullamcorper tristique lacus.";
    String lor4 = "Maecenas id tortor sed purus ultricies tempor. In vulputate feugiat iaculis. Phasellus sem turpis, adipiscing sit amet lacus in, aliquet aliquet eros. Integer euismod, orci et tincidunt iaculis, odio odio vulputate lectus, sed rutrum sapien odio eget sem.";


    public ProfileFragment() {
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Get serialized object
        user = (ModelUser) getActivity().getIntent().getSerializableExtra("userObj");
        Log.v("User name: ", user.getName());


        //Get serialized object
        user = (ModelUser) getActivity().getIntent().getSerializableExtra("userObj");
        Log.v("User name: ", user.getName());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        getActivity().setContentView(R.layout.fragment_profile);

        ((UIParallaxScroll) view.findViewById(R.id.scroller)).setOnScrollChangedListener(mOnScrollChangedListener);

        cImageView = (NetworkImageView) view.findViewById(R.id.item_cover_image);
        mImageView = (UICircularImage) view.findViewById(R.id.image_view);
        mTextView = (TextView) view.findViewById(R.id.contact);
        mNavigationTop = (FrameLayout) view.findViewById(R.id.layout_top);
        mNavigationTitle = (TextView) view.findViewById(R.id.titleBar);
        mLayoutContainer = (RelativeLayout) view.findViewById(R.id.bg_layout);
        mTitleView = (TextView) view.findViewById(R.id.title);
        mNavigationBackBtn = (Button) view.findViewById(R.id.title_bar_left_menu);
        TextView mSum = (TextView) view.findViewById(R.id.sumary);
        mShare = (UICircularImage) view.findViewById(R.id.action1);
        UITabs tab = (UITabs) view.findViewById(R.id.toggle);

        mNavigationTop.getBackground().setAlpha(0);
        mNavigationTitle.setVisibility(View.INVISIBLE);

        mImageView.bringToFront();

        Bundle bundle = getActivity().getIntent().getExtras();

        final int top = bundle.getInt(PACKAGE + ".top");
        final int left = bundle.getInt(PACKAGE + ".left");
        final int width = bundle.getInt(PACKAGE + ".width");
        final int height = bundle.getInt(PACKAGE + ".height");
        title = user.getName();
        String sum = user.getAddress();
        imgId = 1;//bundle.getInt("img");

        //Our Animation initialization
        ViewTreeObserver observer = mImageView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                mImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                int[] screen_location = new int[2];
                mImageView.getLocationOnScreen(screen_location);

                delta_left = left - screen_location[0];
                delta_top = top - screen_location[1];

                scale_width = (float) width / mImageView.getWidth();
                scale_height = (float) height / mImageView.getHeight();

                runEnterAnimation();

                return true;
            }
        });

        final LinearLayout listView = (LinearLayout) view.findViewById(R.id.listView);
        ArrayList<ListItem> data = generateData();
        for (int i = 0; i < data.size(); i++) {
            View v = DetailListAdapter.getView(data.get(i), getActivity().getApplicationContext());
            listView.addView(v);
        }

        cImageView.setImageUrl(user.getCover_url(), imageLoader);

        mTitleView.setText(title);
        mSum.setText(sum);

        mImageView.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);

        Picasso.with(getActivity())
                .load(user.getAvatar_url())
                .noFade()
                .into(mImageView);


        mNavigationTitle.setText(title);

//        mNavigationBackBtn.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View arg0) {
//                onBackPressed();
//            }
//
//        });

        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(getActivity().getApplicationContext(), "Clicked Share", Toast.LENGTH_SHORT).show();
            }
        });

//        tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
//        {
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//
//                    case R.id.toggle1:
//                        mTextView.setVisibility(View.GONE);
//                        listView.setVisibility(LinearLayout.VISIBLE);
//                        return;
//                    case R.id.toggle2:
//                        mTextView.setVisibility(View.VISIBLE);
//                        listView.setVisibility(LinearLayout.GONE);
//                        return;
//                }
//            }
//        });

        return view;
    }

//    @Override
//    public void onBackPressed() {
//
//        runExitAnimation(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        });
//
//    }

//    @Override
//    public void finish() {
//
//        super.finish();
//        overridePendingTransition(0, 0);
//    }

    private ArrayList<ListItem> generateData(){
        ArrayList<ListItem> items = new ArrayList<ListItem>();
        items.add(new ListItem(imgId, title, lor2, null, null));
        items.add(new ListItem(imgId, title, lor1, null, null));
        if (imgId != R.drawable.ph_1)
            items.add(new ListItem(R.drawable.ph_1, "Henry Smith", "<font color='"+ getResources().getColor(R.color.theme_color) +"'>@" + title + "</font> " + lor3, null, null));
        else
            items.add(new ListItem(R.drawable.ph_12, "Ella Smith", "<font color='"+ getResources().getColor(R.color.theme_color) +"'>@" + title + "</font> " + lor3, null, null));
        items.add(new ListItem(imgId, title, lor2, null, null));
        if (imgId != R.drawable.ph_3)
            items.add(new ListItem(R.drawable.ph_3, "Olivier Smith", "<font color='"+ getResources().getColor(R.color.theme_color) +"'>@" + title + "</font> " + lor4, null, null));
        else
            items.add(new ListItem(R.drawable.ph_12, "Ella Smith", "<font color='"+ getResources().getColor(R.color.theme_color) +"'>@" + title + "</font> " + lor3, null, null));
        items.add(new ListItem(imgId, title, lor3, null, null));
        items.add(new ListItem(imgId, title, lor4, null, null));

        return items;
    }

    private void runEnterAnimation() {

        ViewHelper.setPivotX(mImageView, 0.f);
        ViewHelper.setPivotY(mImageView, 0.f);
        ViewHelper.setScaleX(mImageView, scale_width);
        ViewHelper.setScaleY(mImageView, scale_height);
        ViewHelper.setTranslationX(mImageView, delta_left);
        ViewHelper.setTranslationY(mImageView, delta_top);

        animate(mImageView).
                setDuration(DURATION).
                scaleX(1.f).
                scaleY(1.f).
                translationX(0.f).
                translationY(0.f).
                setInterpolator(new DecelerateInterpolator()).
                setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }
                });

        ObjectAnimator bg_anim = ObjectAnimator.ofFloat(mLayoutContainer, "alpha", 0f, 1f);
        bg_anim.setDuration(DURATION);
        bg_anim.start();

    }

    private void runExitAnimation(final Runnable end_action) {

        ViewHelper.setPivotX(mImageView, 0.f);
        ViewHelper.setPivotY(mImageView, 0.f);
        ViewHelper.setScaleX(mImageView, 1.f);
        ViewHelper.setScaleY(mImageView, 1.f);
        ViewHelper.setTranslationX(mImageView, 0.f);
        ViewHelper.setTranslationY(mImageView, 0.f);

        animate(mImageView).
                setDuration(DURATION).
                scaleX(scale_width).
                scaleY(scale_height).
                translationX(delta_left).
                translationY(delta_top).
                setInterpolator(new DecelerateInterpolator()).
                setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        end_action.run();
                    }
                });

        ObjectAnimator bg_anim = ObjectAnimator.ofFloat(mLayoutContainer, "alpha", 1f, 0f);
        bg_anim.setDuration(DURATION);
        bg_anim.start();

    }

    private UIParallaxScroll.OnScrollChangedListener mOnScrollChangedListener = new UIParallaxScroll.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            //Difference between the heights, important to not add margin or remove mNavigationTitle.
            final float headerHeight = ViewHelper.getY(mTitleView) - (mNavigationTop.getHeight() - mTitleView.getHeight());
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mNavigationTop.getBackground().setAlpha(newAlpha);

            Animation animationFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fadein);
            Animation animationFadeOut = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.fadeout);

            if (newAlpha == 255 && mNavigationTitle.getVisibility() != View.VISIBLE && !animationFadeIn.hasStarted()){
                mNavigationTitle.setVisibility(View.VISIBLE);
                mNavigationTitle.startAnimation(animationFadeIn);
            } else if (newAlpha < 255 && !animationFadeOut.hasStarted() && mNavigationTitle.getVisibility() != View.INVISIBLE)  {
                mNavigationTitle.startAnimation(animationFadeOut);
                mNavigationTitle.setVisibility(View.INVISIBLE);

            }

        }
    };
}
