package com.kalianey.oxapp.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.commit451.nativestackblur.NativeStackBlur;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.menu.ResideMenu;
import com.kalianey.oxapp.models.ModelAttachment;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.models.ModelUser;
import com.kalianey.oxapp.utils.AppController;
import com.kalianey.oxapp.utils.QueryAPI;
import com.kalianey.oxapp.utils.UICircularImage;
import com.kalianey.oxapp.utils.UIParallaxScroll;
import com.kalianey.oxapp.utils.UITabs;
import com.kalianey.oxapp.utils.Utility;
import com.kalianey.oxapp.views.activities.MainActivity;
import com.kalianey.oxapp.views.activities.Message;
import com.kalianey.oxapp.views.activities.ProfilePhotos;
import com.kalianey.oxapp.views.adapters.ProfileFriendListViewAdapter;
import com.kalianey.oxapp.views.adapters.ProfilePhotoRecyclerViewAdapter;
import com.kalianey.oxapp.views.adapters.ProfileQuestionListAdapter;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

//import org.lucasr.twowayview.ItemClickSupport.OnItemClickListener;
//import org.lucasr.twowayview.ItemClickSupport.OnItemLongClickListener;

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
    private UIParallaxScroll scrollView;
    private RelativeLayout mCoverContainer;
    private RecyclerView gridView;
    private UICircularImage mAvatarImageView;
    private RelativeLayout mLayoutContainer;
    private FrameLayout mNavigationTop;
    private TextView mNavigationTitle;
    private Button mNavigationBackBtn;
    private TextView mTitleView;
    private UICircularImage mChatButton;
    private LinearLayout mLinearLayoutButtonHolder;
    private ImageButton mAddFriend;
    private ImageButton mAddFavorite;
    private ProfilePhotoRecyclerViewAdapter profilePhotoRecyclerViewAdapter;
    private TwoWayView friendsListView;
    private ProfileFriendListViewAdapter friendsAdapter;
    private TextView profileFriendText;
    private TextView noFriendText;
    private TextView profilePhotoText;
    private TextView noPhotoText;
    private TextView mSum;
    private UITabs tab;
    private StickyListHeadersListView stickyList;

    //Vars
    private ArrayList<ModelAttachment> photoList = new ArrayList<ModelAttachment>();
    private Boolean isLoggedInUser = true;
    private int delta_top;
    private int delta_left;
    private float scale_width;
    private float scale_height;
    private String title;
    private Integer isFriend;
    private Boolean isFav;
    private Integer photoCount = 0;
    private Integer friendCount = 0;
    int imgId;


    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


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

        getUI();

        //initializeRecyclerView();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


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
                photoCount = user.getPhotos().size();
                friendCount = user.getFriends().size();

                //Staggered grid view for the photos
                photoList.clear();
                photoList.addAll(0, user.getPhotos());
                initializeRecyclerView();
                profilePhotoRecyclerViewAdapter.setPhotos(photoList);
                profilePhotoRecyclerViewAdapter.setUser(user);
                profilePhotoRecyclerViewAdapter.notifyDataSetChanged();

                if (photoCount == 0) {
                    noPhotoText.setVisibility(view.VISIBLE);
                    gridView.setVisibility(view.GONE);
                }

                //Horizontal ListView for friends
                if (friendCount > 0) {
                    friendsAdapter = new ProfileFriendListViewAdapter(getActivity(), R.layout.profile_friend_list_item, user.getFriends());
                    friendsListView.setAdapter(friendsAdapter);
                } else {
                    noFriendText.setVisibility(view.VISIBLE);
                    friendsListView.setVisibility(view.GONE);
                }

                //Profile Questions
                stickyList = (StickyListHeadersListView) view.findViewById(R.id.questions_list);
                ProfileQuestionListAdapter adapter = new ProfileQuestionListAdapter(getActivity(), user);
                stickyList.setAdapter(adapter);


            }
        });

        //Set up animation for the user avatar image
        if (!isLoggedInUser) {
            Bundle bundle = getActivity().getIntent().getExtras();

            final int top = bundle.getInt(PACKAGE + ".top");
            final int left = bundle.getInt(PACKAGE + ".left");
            final int width = bundle.getInt(PACKAGE + ".width");
            final int height = bundle.getInt(PACKAGE + ".height");

            imgId = 1;//bundle.getInt("img");

            //Our Animation initialization
            ViewTreeObserver observer = mAvatarImageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {

                    mAvatarImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screen_location = new int[2];
                    mAvatarImageView.getLocationOnScreen(screen_location);

                    delta_left = left - screen_location[0];
                    delta_top = top - screen_location[1];

                    scale_width = (float) width / mAvatarImageView.getWidth();
                    scale_height = (float) height / mAvatarImageView.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });

            //As the user is different from the LoggedIn User, we get friend and fav status
            //Get Friend Status
            query.isFriend(user.getUserId(), new QueryAPI.ApiResponse<Integer>() {
                @Override
                public void onCompletion(Integer result) {
                    Log.i("isFriend: ", result.toString());
                    isFriend = result;
                    if (isFriend == 1) {
                        mAddFriend.setImageResource(R.drawable.user_ok);
                    } else if (isFriend == 2) {
                        mAddFriend.setImageResource(R.drawable.user_ok_tick);
                    }
                }
            });

            //Get Favorite Status
            query.isFavorite(user.getUserId(), new QueryAPI.ApiResponse<Boolean>() {
                @Override
                public void onCompletion(Boolean result) {
                    isFav = result;
                    if (isFav) {
                        mAddFavorite.setImageResource(R.drawable.star_ok);
                    }
                }
            });


        } else {
            //If user is the logged in user
            // we set the back button to slidemenu button
            mNavigationBackBtn.setBackgroundResource(R.drawable.titlebar_menu_selector);
            mNavigationBackBtn.setPadding(0, 10, 0, 0);
            //Add a bit of margin to the menu button
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mNavigationBackBtn.getLayoutParams();
            params.topMargin = 50;
            //Hide msg, friend and favorite buttons
            mChatButton.setVisibility(view.GONE);
            mLinearLayoutButtonHolder.setVisibility(view.INVISIBLE);
            mAddFriend.setVisibility(view.INVISIBLE);
            mAddFavorite.setVisibility(view.INVISIBLE);
        }

        //Set up all the elements and bind actions
        setUpUI();

        //Return the View
        return view;

    }

    public void getUI() {
        //Set up UI elements
        mCoverContainer = (RelativeLayout) view.findViewById(R.id.cover_container);
        gridView = (RecyclerView) view.findViewById(R.id.grid_view);
        gridView.setFocusable(false); //prevent the scrollview to scroll to bottom onCreate
        friendsListView = (TwoWayView) view.findViewById(R.id.friends_list);
        friendsListView.setFocusable(false); //prevent the scrollview to scroll to bottom onCreate
        scrollView = (UIParallaxScroll) view.findViewById(R.id.scroller);
        scrollView.setOnScrollChangedListener(mOnScrollChangedListener);
        mAvatarImageView = (UICircularImage) view.findViewById(R.id.image_view);
        // mTextView = (TextView) view.findViewById(R.id.contact);
        mNavigationTop = (FrameLayout) view.findViewById(R.id.layout_top);
        mNavigationTitle = (TextView) view.findViewById(R.id.titleBar);
        mLayoutContainer = (RelativeLayout) view.findViewById(R.id.bg_layout);
        mTitleView = (TextView) view.findViewById(R.id.title);
        mNavigationBackBtn = (Button) view.findViewById(R.id.title_bar_left_menu);
        mSum = (TextView) view.findViewById(R.id.sumary);
        mLinearLayoutButtonHolder = (LinearLayout) view.findViewById(R.id.statistics);
        mChatButton = (UICircularImage) view.findViewById(R.id.action1);
        mAddFriend = (ImageButton) view.findViewById(R.id.imageButtonFriend);
        mAddFavorite = (ImageButton) view.findViewById(R.id.imageButtonFavorite);
        tab = (UITabs) view.findViewById(R.id.toggle);
        profilePhotoText = (TextView) view.findViewById(R.id.profile_photo_text);
        noPhotoText = (TextView) view.findViewById(R.id.noPhotos);
        profileFriendText = (TextView) view.findViewById(R.id.profile_friend_text);
        noFriendText = (TextView) view.findViewById(R.id.noFriends);

        mNavigationTop.getBackground().setAlpha(0);
        mNavigationTitle.setVisibility(View.INVISIBLE);

        mAvatarImageView.bringToFront();
    }

    public void setUpUI(){

        title = user.getName();
        String sum = user.getAddress();
        mTitleView.setText(title);
        mSum.setText(sum);

        mAvatarImageView.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);

        Picasso.with(getActivity())
                .load(user.getAvatar_url())
                .noFade()
                .into(mAvatarImageView);

        mAvatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create the array to hold our photo
                ArrayList<ModelAttachment> photos = new ArrayList<ModelAttachment>();
                ModelAttachment attachment = new ModelAttachment();
                attachment.setId("0");
                attachment.setUrl(user.getAvatar_url());
                photos.add(0, attachment);
                //Create the intent
                Intent intent = new Intent(getActivity(), ProfilePhotos.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("photoList", photos);
                mBundle.putInt("photoIndex", 0);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });


        //Set up the navigation
        mNavigationTitle.setText(title);
        mNavigationBackBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isLoggedInUser) {
                    ResideMenu resideMenu = ((MainActivity) getActivity()).getResideMenu();
                    if (resideMenu.isOpened()) {
                        resideMenu.closeMenu();
                    } else {
                        resideMenu.openMenu();
                    }

                } else {
                    onBackPressed();
                }
                //onBackPressed();
            }

        });

        //Set up the Chat Button
        mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                query.conversationGet(user.getUserId(), new QueryAPI.ApiResponse<String>() {
                    @Override
                    public void onCompletion(String result) {

                        //Conv doesn't exist
                        if (result.equals("") || result.equals("0")) {

                            query.conversationCreate(AppController.getInstance().getLoggedInUser().getUserId(), user.getUserId(), new QueryAPI.ApiResponse<ModelConversation>() {
                                @Override
                                public void onCompletion(ModelConversation conversation) {

                                    conversation.setAvatarUrl(user.getAvatar_url());
                                    conversation.setName(user.getName());
                                    Intent i = new Intent(getActivity(), Message.class);
                                    Bundle mBundle = new Bundle();
                                    mBundle.putSerializable("convObj", conversation);
                                    i.putExtras(mBundle);
                                    startActivity(i);

                                }
                            });
                        }
                        //Conv already exist
                        else {
                            ModelConversation conversation = new ModelConversation();
                            conversation.setId(result);
                            conversation.setName(user.getName());
                            conversation.setOpponentId(user.getUserId());
                            //conv.setAvatarUrl(user.getAvatar_url());

                            Intent i = new Intent(getActivity(), Message.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putSerializable("convObj", conversation);
                            i.putExtras(mBundle);
                            startActivity(i);
                        }
                    }
                });

            }
        });

        //Set up the tabs (General / Infos)
        tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {

                    case R.id.toggle1:
                        //mTextView.setVisibility(View.GONE);
                        gridView.setVisibility(LinearLayout.VISIBLE);
                        friendsListView.setVisibility(LinearLayout.VISIBLE);
                        profilePhotoText.setVisibility(LinearLayout.VISIBLE);
                        profileFriendText.setVisibility(LinearLayout.VISIBLE);
                        stickyList.setVisibility(View.GONE);
                        if (photoCount == 0) {
                            noPhotoText.setVisibility(view.VISIBLE);
                            gridView.setVisibility(view.GONE);
                        }
                        if (friendCount == 0) {
                            noFriendText.setVisibility(view.VISIBLE);
                            friendsListView.setVisibility(view.GONE);
                        }
                        return;
                    case R.id.toggle2:
                        // mTextView.setVisibility(View.VISIBLE);
                        stickyList.setVisibility(View.VISIBLE);
                        gridView.setVisibility(LinearLayout.GONE);
                        friendsListView.setVisibility(LinearLayout.GONE);
                        profilePhotoText.setVisibility(LinearLayout.GONE);
                        noPhotoText.setVisibility(view.GONE);
                        profileFriendText.setVisibility(LinearLayout.GONE);
                        noFriendText.setVisibility(view.GONE);
                        return;
                }
            }
        });


        //Set up the Add Friend and Favorite Buttons
        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendWasTapped();
            }
        });
        mAddFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteWasTapped();
            }
        });

        //Set up cover Image as background of Relative layout
        Bitmap bm = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(),
                R.drawable.default_bg);
        Bitmap bg = NativeStackBlur.process(bm, 100);
        Drawable defaultBackground = new BitmapDrawable(getActivity().getResources(), bg);
        mCoverContainer.setBackground(defaultBackground);

        //If user has a cover, we set it
        if (user.getCover_url() != null) {

            Target target = new Target() {
                @Override
                public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                        Drawable drawable = new BitmapDrawable(getActivity().getResources(), bitmap);
                        mCoverContainer.setBackground(drawable);
                    }
                @Override
                public void onBitmapFailed(Drawable errorDrawable) {}
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {}
                };
            Picasso.with(getActivity().getApplicationContext())
                    .load(user.getCover_url())
                    .into(target);

        }

    }

    //http://blog.ashwanik.in/2015/05/handling-adapter-error-while-using-recyclerview.html
    public void initializeRecyclerView() {

        Integer photoRows = 1;
        Integer gridHeight = 100;
        if (photoCount > 4 && photoCount < 10) {
            photoRows = 2;
            gridHeight = 200;
        } else if (photoCount > 10){
            photoRows = 3;
            gridHeight = 400;
        }

        gridView = (RecyclerView) view.findViewById(R.id.grid_view);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(photoRows,StaggeredGridLayoutManager.HORIZONTAL );
        gridLayoutManager.setOrientation(gridLayoutManager.HORIZONTAL);
        profilePhotoRecyclerViewAdapter = new ProfilePhotoRecyclerViewAdapter(getActivity());
        gridView.setAdapter(profilePhotoRecyclerViewAdapter);
        gridView.setLayoutManager(gridLayoutManager);
        gridView.setHasFixedSize(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, gridHeight);
        params.height= gridHeight;
        gridView.setLayoutParams(params);

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

        ViewHelper.setPivotX(mAvatarImageView, 0.f);
        ViewHelper.setPivotY(mAvatarImageView, 0.f);
        ViewHelper.setScaleX(mAvatarImageView, scale_width);
        ViewHelper.setScaleY(mAvatarImageView, scale_height);
        ViewHelper.setTranslationX(mAvatarImageView, delta_left);
        ViewHelper.setTranslationY(mAvatarImageView, delta_top);

        animate(mAvatarImageView).
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

        ViewHelper.setPivotX(mAvatarImageView, 0.f);
        ViewHelper.setPivotY(mAvatarImageView, 0.f);
        ViewHelper.setScaleX(mAvatarImageView, 1.f);
        ViewHelper.setScaleY(mAvatarImageView, 1.f);
        ViewHelper.setTranslationX(mAvatarImageView, 0.f);
        ViewHelper.setTranslationY(mAvatarImageView, 0.f);

        animate(mAvatarImageView).
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


    private void friendWasTapped(){

        mAddFriend.setEnabled(false);

        if (isFriend == 0){
            query.friend(user.getUserId(), "add", new QueryAPI.ApiResponse<Boolean>() {
                @Override
                public void onCompletion(Boolean result) {
                    if (result) {
                        isFriend = 1;
                        mAddFriend.setImageResource(R.drawable.user_ok);
                        Toast.makeText(getActivity().getApplicationContext(), "Friend request sent", Toast.LENGTH_SHORT).show();
                    }
                    mAddFriend.setEnabled(true);
                }
            });
        } else if (isFriend == 1) {
            query.friend(user.getUserId(), "cancel", new QueryAPI.ApiResponse<Boolean>() {
                @Override
                public void onCompletion(Boolean result) {
                    if (result) {
                        isFriend = 0;
                        mAddFriend.setImageResource(R.drawable.user);
                        Toast.makeText(getActivity().getApplicationContext(), "Friend removed", Toast.LENGTH_SHORT).show();
                    }
                    mAddFriend.setEnabled(true);
                }
            });
        } else {
            mAddFriend.setEnabled(true);
        }
    }


    private void favoriteWasTapped(){

        mAddFavorite.setEnabled(false);

        if (!isFav){
            query.favorite(user.getUserId(), "add", new QueryAPI.ApiResponse<Boolean>() {
                @Override
                public void onCompletion(Boolean result) {
                    if (result) {
                        isFav = true;
                        mAddFavorite.setImageResource(R.drawable.star_ok);
                        Toast.makeText(getActivity().getApplicationContext(), "Favorite added", Toast.LENGTH_SHORT).show();
                    }
                    mAddFavorite.setEnabled(true);
                }
            });
        } else {
            query.favorite(user.getUserId(), "remove", new QueryAPI.ApiResponse<Boolean>() {
                @Override
                public void onCompletion(Boolean result) {
                    if (result) {
                        isFav = false;
                        mAddFavorite.setImageResource(R.drawable.star);
                        Toast.makeText(getActivity().getApplicationContext(), "Favorite removed", Toast.LENGTH_SHORT).show();
                    }
                    mAddFavorite.setEnabled(true);
                }
            });
        }
    }

}
