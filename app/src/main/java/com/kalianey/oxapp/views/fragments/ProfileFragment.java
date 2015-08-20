package com.kalianey.oxapp.views.fragments;

import android.annotation.SuppressLint;
//import android.app.Fragment;
import android.content.Intent;
import android.graphics.AvoidXfermode;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.etsy.android.grid.StaggeredGridView;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelAttachment;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.UICircularImage;
import com.kalianey.oxapp.utils.UIParallaxScroll;
import com.kalianey.oxapp.utils.UITabs;
import com.kalianey.oxapp.views.activities.Message;
import com.kalianey.oxapp.views.activities.Profile;
import com.kalianey.oxapp.views.adapters.ProfileFriendListViewAdapter;
import com.kalianey.oxapp.views.adapters.ProfilePhotoListViewAdapter;
import com.kalianey.oxapp.views.adapters.ProfilePhotoRecyclerViewAdapter;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;

import org.lucasr.twowayview.TwoWayView;
//import org.lucasr.twowayview.ItemClickSupport.OnItemClickListener;
//import org.lucasr.twowayview.ItemClickSupport.OnItemLongClickListener;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment {

    private ModelUser user;

    //Configuration
    public static final int DURATION = 500; // in ms
    public static final String PACKAGE = "IDENTIFY";
    QueryAPI query = new QueryAPI();

    //UI Elements
    private View view;
    //private TwoWayView gridView;
    private RecyclerView gridView;
    private NetworkImageView cImageView;
    private UICircularImage mImageView;
    private TextView mTextView;
    private RelativeLayout mLayoutContainer;
    private FrameLayout mNavigationTop;
    private TextView mNavigationTitle;
    private Button mNavigationBackBtn;
    private TextView mTitleView;
    private UICircularImage mShare;
    //private ProfilePhotoListViewAdapter adapter;
    private ProfilePhotoRecyclerViewAdapter adapter;
    private TwoWayView friendsListView;
    private ProfileFriendListViewAdapter friendsAdapter;
    private TextView profileFriendText;
    private TextView profilePhotoText;

    //Vars
    private List<ModelAttachment> photoList = new ArrayList<ModelAttachment>();
    private Boolean isLoggedInUser = true;
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

    public ModelUser getUser() {
        return user;
    }

    public void setUser(ModelUser user) {
        this.user = user;
    }


    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeRecyclerView();

        gridView = (RecyclerView) view.findViewById(R.id.grid_view);

        friendsListView = (TwoWayView) view.findViewById(R.id.friends_list);

        //Get logged in user
        if (user == null) {
            //If coming from intent, get serialized object
            user = (ModelUser) getActivity().getIntent().getSerializableExtra("userObj");
            isLoggedInUser = false;
        }

        //Get extra info for user
        query.userExtra(user, new QueryAPI.ApiResponse<ModelUser>() {
            @Override
            public void onCompletion(ModelUser result) {
                user = result;

                //Staggered grid view for the photos
                photoList.clear();
                photoList.addAll(0, user.getPhotos());
                adapter.setPhotos(photoList);
                adapter.notifyDataSetChanged();

                //Horizontal ListView for friends
                friendsAdapter = new ProfileFriendListViewAdapter(getActivity(), R.layout.profile_friend_list_item, user.getFriends());
                friendsListView.setAdapter(friendsAdapter);

//                final ItemClickSupport itemClick = ItemClickSupport.addTo(friendsAdapter);
//
//                friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                        Intent i = new Intent(getActivity(), Profile.class);
//                        Bundle mBundle = new Bundle();
//                        mBundle.putSerializable("userObj", user.getFriends().get(position));
//
//                        int[] screen_location = new int[2];
//                        friendsListView.getLocationOnScreen(screen_location);
//
//                        mBundle.putInt(PACKAGE + ".left", screen_location[0]);
//                        mBundle.putInt(PACKAGE + ".top", screen_location[1]);
//                        mBundle.putInt(PACKAGE + ".width", 90);
//                        mBundle.putInt(PACKAGE + ".height", 90);
//
//                        i.putExtras(mBundle);
//                        getActivity().startActivity(i);
//                    }
//                });

            }
        });


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


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
        profilePhotoText = (TextView) view.findViewById(R.id.profile_photo_text);
        profileFriendText = (TextView) view.findViewById(R.id.profile_friend_text);

        mNavigationTop.getBackground().setAlpha(0);
        mNavigationTitle.setVisibility(View.INVISIBLE);

        mImageView.bringToFront();


        title = user.getName();
        String sum = user.getAddress();

        //Here to wrap if coming from intent
        if (!isLoggedInUser) {
            Bundle bundle = getActivity().getIntent().getExtras();

            final int top = bundle.getInt(PACKAGE + ".top");
            final int left = bundle.getInt(PACKAGE + ".left");
            final int width = bundle.getInt(PACKAGE + ".width");
            final int height = bundle.getInt(PACKAGE + ".height");

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

        mNavigationBackBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }

        });

        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                query.conversationGet(user.getUserId(), new QueryAPI.ApiResponse<String>() {
                    @Override
                    public void onCompletion(String result) {

                        //Conv doesn't exist
                        if (result.equals("") || result.equals("0")) {

                        }
                        //Conv already exist
                        else {
                            ModelConversation conv = new ModelConversation();
                            conv.setId(result);
                            conv.setName(user.getName());
                            conv.setOpponentId(user.getUserId());
                            //conv.setAvatarUrl(user.getAvatar_url());

                            Intent i = new Intent(getActivity(), Message.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putSerializable("convObj", conv);
                            i.putExtras(mBundle);
                            startActivity(i);
                        }
                    }
                });

                /*   else {

                        //if not we create a new one //TODO: check why the conversation is displayed but not created (cannot send messages)
                        let query = queryAPI()
                        query.conversationCreate(KYController.sharedInstance.getUser()!.userId!, interlocutorId: self.currentUser.userId, completion: { (conv: ModelConversation) -> () in

                            //pass the returned conversation model to var conversation
                            self.openConversation = conv
                            self.openConversation!.displayName = self.currentUser.name
                            self.performSegueWithIdentifier("profileToMessage", sender: nil)
                        })

                    }

                })
                */
            }
        });

        tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {

                    case R.id.toggle1:
                        mTextView.setVisibility(View.GONE);
                        gridView.setVisibility(LinearLayout.VISIBLE);
                        friendsListView.setVisibility(LinearLayout.VISIBLE);
                        profilePhotoText.setVisibility(LinearLayout.VISIBLE);
                        profileFriendText.setVisibility(LinearLayout.VISIBLE);
                        return;
                    case R.id.toggle2:
                        mTextView.setVisibility(View.VISIBLE);
                        gridView.setVisibility(LinearLayout.GONE);
                        friendsListView.setVisibility(LinearLayout.GONE);
                        profilePhotoText.setVisibility(LinearLayout.GONE);
                        profileFriendText.setVisibility(LinearLayout.GONE);
                        return;
                }
            }
        });

        return view;
    }


    //http://blog.ashwanik.in/2015/05/handling-adapter-error-while-using-recyclerview.html
    void initializeRecyclerView() {
        gridView = (RecyclerView) view.findViewById(R.id.grid_view);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.HORIZONTAL );
        gridLayoutManager.setOrientation(gridLayoutManager.HORIZONTAL);
        adapter = new ProfilePhotoRecyclerViewAdapter(getActivity());
        gridView.setAdapter(adapter);
        gridView.setLayoutManager(gridLayoutManager);
        gridView.setHasFixedSize(true);
        //gridView.setVisibility(View.GONE);
    }


    public void onBackPressed() {

        runExitAnimation(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });

    }

    public void finish() {

        getActivity().finish();
        getActivity().overridePendingTransition(0, 0);
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
