package com.rackluxury.ferrari.activities;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rackluxury.ferrari.R;
import com.rackluxury.ferrari.adapters.CategoriesData;
import com.rackluxury.ferrari.adapters.MyCategoriesAdapter;
import com.rackluxury.ferrari.adapters.UserProfile;
import com.rackluxury.ferrari.blog.BlogActivity;
import com.rackluxury.ferrari.reddit.activities.RedditMainActivity;
import com.rackluxury.ferrari.youtube.YouTubeActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final List<CategoriesData> favouriteCategories = new ArrayList<>();
    final List<CategoriesData> deletedCategories = new ArrayList<>();
    RecyclerView categoriesRecyclerView;
    List<CategoriesData> myCategoriesList;
    CategoriesData mCategoriesData;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    boolean isOpen = false;

    private int soundLike;


    private SoundPool soundPool;
    private int soundFBShare;
    private int soundFBLike;
    private int soundTwitterShare;

    private long backPressedTime;
    private Toolbar toolbar;
    private MyCategoriesAdapter myCategoriesAdapter;
    final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();
            final CategoriesData categoriesItem = myCategoriesList.get(position);
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    deletedCategories.add(categoriesItem);
                    myCategoriesList.remove(position);
                    myCategoriesAdapter.notifyItemRemoved(position);
                    Snackbar.make(categoriesRecyclerView, "Deleted.", Snackbar.LENGTH_LONG).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    favouriteCategories.add(categoriesItem);
                    myCategoriesAdapter.notifyItemRemoved(position);
                    soundPool.play(soundLike, 1, 1, 0, 0, 1);
                    Snackbar.make(categoriesRecyclerView, "Added to favourites.", Snackbar.LENGTH_LONG).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView categoriesRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(HomeActivity.this, c, categoriesRecyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorRed))
                    .addSwipeLeftActionIcon(R.drawable.ic_deleted_swipe_main)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorGreen))
                    .addSwipeRightActionIcon(R.drawable.ic_favourite_swipe_main)
                    .setActionIconTint(ContextCompat.getColor(categoriesRecyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, categoriesRecyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private String ShareOnFacebookString;
    private String ShareOnTwitterString;
    private UserProfile userProfile;
    private ImageView navUserPhoto;
    private DrawerLayout drawer;
    private TextView navUsername, navUserMail;
    private int lastPosition;
    private FloatingActionButton fabMore, fabFav, fabVideos;
    private Animation fromBottom, toBottom, rotateOpen, rotateClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setUpUIViewsHomeActivity();
        initToolbar();
        setupNavigationDrawer();
        loadMainData();
        itemTouchCategories();

        fromBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.to_bottom_anim);
        rotateOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_close_anim);

        fabVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeVideos();

            }
        });
        fabFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoriesFav();

            }
        });
        fabMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    fabVideos.startAnimation(fromBottom);
                    fabFav.startAnimation(fromBottom);
                    fabMore.startAnimation(rotateOpen);

                    fabVideos.setClickable(true);
                    fabFav.setClickable(true);
                    isOpen = false;
                } else {
                    fabVideos.startAnimation(toBottom);
                    fabFav.startAnimation(toBottom);
                    fabMore.startAnimation(rotateClose);

                    fabVideos.setClickable(false);
                    fabFav.setClickable(false);
                    isOpen = true;
                }

            }
        });


        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        categoriesRecyclerView.setLayoutManager(layoutManager);
        categoriesRecyclerView.setHasFixedSize(true);
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        lastPosition = getPrefs.getInt("lastPosCategories", 0);
        categoriesRecyclerView.scrollToPosition(lastPosition);

        categoriesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastPosition = layoutManager.findFirstVisibleItemPosition();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putInt("lastPosCategories", lastPosition);
        e.apply();
    }

    private void setUpUIViewsHomeActivity() {

        toolbar = findViewById(R.id.toolbarHomeActivity);
        categoriesRecyclerView = findViewById(R.id.rvCategoriesRecycler);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.nav_username);
        navUserMail = headerView.findViewById(R.id.nav_user_mail);
        navUserPhoto = headerView.findViewById(R.id.nav_user_photo);
        fabMore = findViewById(R.id.fabMoreCategories);
        fabFav = findViewById(R.id.fabFavCategories);
        fabVideos = findViewById(R.id.fabVideosCategories);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        soundTwitterShare = soundPool.load(this, R.raw.sound_share_on_twitter, 1);
        soundFBShare = soundPool.load(this, R.raw.sound_share_on_facebook, 1);
        soundFBLike = soundPool.load(this, R.raw.sound_like_us_on_facebook, 1);
        soundLike = soundPool.load(this, R.raw.sound_like, 1);


    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

    }

    private void setupNavigationDrawer() {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open_home, R.string.navigation_drawer_close_home);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(HomeActivity.this);
        updateNavHeader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        getMenuInflater().inflate(R.menu.toolbar_search_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search_main);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_home_price_low_to_high) {
            sortViewPriceLowToHigh();
            return true;
        } else if (item.getItemId() == R.id.sort_home_price_high_to_low) {
            sortViewPriceHighToLow();
            return true;
        } else if (item.getItemId() == R.id.sort_home_name_a_to_z) {
            sortViewNameAToZ();
            return true;
        } else if (item.getItemId() == R.id.sort_home_name_z_to_a) {
            sortViewNameZtoA();
            return true;
        } else if (item.getItemId() == R.id.favourite_categories) {
            openCategoriesFav();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openCategoriesFav() {
        Intent view = new Intent(HomeActivity.this, FavouriteCategoriesActivity.class);
        startActivity(view);
        Animatoo.animateSplit(HomeActivity.this);
    }

    private void homeVideos() {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference.child(firebaseAuth.getUid()).child("Video Purchased").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                finish();
                Intent openVideoFromMain = new Intent(HomeActivity.this, VideoActivity.class);
                startActivity(openVideoFromMain);
                Animatoo.animateSplit(HomeActivity.this);

            }
        });
        storageReference.child(firebaseAuth.getUid()).child("Video Purchased").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseMessaging.getInstance().subscribeToTopic("purchase_video");
                finish();
                Intent openVideoCheckerFromMain = new Intent(HomeActivity.this, VideoCheckerActivity.class);
                startActivity(openVideoCheckerFromMain);
                Animatoo.animateSwipeRight(HomeActivity.this);

            }
        });


    }

    public void sortViewNameAToZ() {
        Collections.sort(myCategoriesList, CategoriesData.ByNameAToZ);
        myCategoriesAdapter.notifyDataSetChanged();
    }

    public void sortViewNameZtoA() {
        Collections.sort(myCategoriesList, CategoriesData.ByNameZToA);
        myCategoriesAdapter.notifyDataSetChanged();
    }

    public void sortViewPriceLowToHigh() {
        Collections.sort(myCategoriesList, CategoriesData.ByPriceLowToHigh);
        myCategoriesAdapter.notifyDataSetChanged();
    }

    public void sortViewPriceHighToLow() {
        Collections.sort(myCategoriesList, CategoriesData.ByPriceHighToLow);
        myCategoriesAdapter.notifyDataSetChanged();
    }

    private void filter(String text) {

        ArrayList<CategoriesData> filterList = new ArrayList<>();

        for (CategoriesData item : myCategoriesList) {

            if (item.getCategoriesName().toLowerCase().contains(text.toLowerCase())) {

                filterList.add(item);

            }

        }

        myCategoriesAdapter.filteredList(filterList);

    }

    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toasty.normal(HomeActivity.this, "Click Back again to Exit", Toast.LENGTH_SHORT, ContextCompat.getDrawable(HomeActivity.this, R.drawable.ic_main_exit_toast)).show();
        }
        backPressedTime = System.currentTimeMillis();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.expensiveNavigation) {
            ExpensiveWatches();

        } else if (id == R.id.imagesNavigation) {
            Images();

        } else if (id == R.id.videoNavigation) {
            homeVideos();

        } else if (id == R.id.youtubeVideoNavigation) {
            youtubeVideos();

        } else if (id == R.id.blogNavigation) {
            Blog();

        } else if (id == R.id.redditNavigation) {
            Reddit();

        } else if (id == R.id.profileNavigation) {
            ProfileDisplay();

        } else if (id == R.id.billingNavigation) {
            InAppPurchase();

        } else if (id == R.id.factsNavigation) {
            FactsAboutUs();

        } else if (id == R.id.signOutNavigation) {
            SignOut();


        } else if (id == R.id.shareNavigation) {
            GeneralShareMain();

        } else if (id == R.id.facebookLikeNavigation) {
            LikeFacebookPage();

        } else if (id == R.id.facebookShareNavigation) {
            ShareOnFacebook();

        } else if (id == R.id.twitterShareNavigation) {
            ShareOnTwitter();
        } else if (id == R.id.companyNavigation) {
            openCompanyInfo();
        } else if (id == R.id.aboutUsNavigation) {

            aboutUs();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Blog() {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference.child(firebaseAuth.getUid()).child("Blog Purchased").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                finish();
                Intent openExpensiveFromMain = new Intent(HomeActivity.this, BlogActivity.class);
                startActivity(openExpensiveFromMain);
                Animatoo.animateSwipeRight(HomeActivity.this);

            }
        });
        storageReference.child(firebaseAuth.getUid()).child("Blog Purchased").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseMessaging.getInstance().subscribeToTopic("purchase_blog");
                finish();

                Intent openBlogCheckerFromMain = new Intent(HomeActivity.this, BlogCheckerActivity.class);
                startActivity(openBlogCheckerFromMain);
                Animatoo.animateSwipeRight(HomeActivity.this);

            }
        });
    }

    private void Reddit() {
        Intent openBlogFromMain = new Intent(HomeActivity.this, RedditMainActivity.class);
        startActivity(openBlogFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    private void ExpensiveWatches() {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference.child(firebaseAuth.getUid()).child("Expensive Purchased").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                finish();
                Intent openExpensiveFromMain = new Intent(HomeActivity.this, ExpensiveActivity.class);
                startActivity(openExpensiveFromMain);
                Animatoo.animateSwipeRight(HomeActivity.this);

            }
        });
        storageReference.child(firebaseAuth.getUid()).child("Expensive Purchased").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseMessaging.getInstance().subscribeToTopic("purchase_expensive");
                finish();
                Intent openExpensiveCheckerFromMain = new Intent(HomeActivity.this, ExpensiveCheckerActivity.class);
                startActivity(openExpensiveCheckerFromMain);
                Animatoo.animateSwipeRight(HomeActivity.this);

            }
        });

    }

    private void Images() {
        Intent openImagesFromMain = new Intent(HomeActivity.this, ImagesActivity.class);
        startActivity(openImagesFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);

    }

    private void youtubeVideos() {
        Intent openYoutubeVideoFromMain = new Intent(HomeActivity.this, YouTubeActivity.class);
        startActivity(openYoutubeVideoFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    private void ProfileDisplay() {
        Intent openProfileActivityFromMain = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(openProfileActivityFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    private void InAppPurchase() {
        Intent openBillingFromMain = new Intent(HomeActivity.this, BillingActivity.class);
        startActivity(openBillingFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    private void FactsAboutUs() {
        Intent openFactsFromMain = new Intent(HomeActivity.this, FactsActivity.class);
        startActivity(openFactsFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    private void SignOut() {

        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View view = inflater.inflate(R.layout.alert_dialog_sign_out, null);
        Button acceptButton = view.findViewById(R.id.btnAcceptAlertSignOut);
        Button cancelButton = view.findViewById(R.id.btnRejectAlertSignOut);
        final AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this)
                .setView(view)
                .show();

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                finish();
                Intent openLoginActivityFromMain = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(openLoginActivityFromMain);
                Animatoo.animateSlideDown(HomeActivity.this);


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


    }

    private void GeneralShareMain() {

        setShareDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_LOADING_TIME = 3400;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent GeneralShareMainIntent = new Intent(Intent.ACTION_SEND);
                final String appPackageName = getApplicationContext().getPackageName();
                String appLink = "https://play.google.com/store/apps/details?id=" + appPackageName;


                GeneralShareMainIntent.setType("Text/plain");
                String generalMainShareBody = "Ferrari:We are the Competition      " +
                        "" +
                        appLink;
                String generalMainShareSub = "Ferrari App";
                GeneralShareMainIntent.putExtra(Intent.EXTRA_SUBJECT, generalMainShareSub);
                GeneralShareMainIntent.putExtra(Intent.EXTRA_TEXT, generalMainShareBody);
                startActivity(Intent.createChooser(GeneralShareMainIntent, "Share Via"));
                Animatoo.animateSpin(HomeActivity.this);
            }
        }, TRANSITION_SCREEN_LOADING_TIME);


    }

    private void setShareDialogue() {
        final ShareDialogue shareDialogue = new ShareDialogue(HomeActivity.this);
        shareDialogue.startShareDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 3000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shareDialogue.dismissDialogue();
            }
        }, TRANSITION_SCREEN_TIME);
    }

    private void LikeFacebookPage() {
        try {
            soundPool.play(soundFBLike, 1, 1, 0, 0, 1);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + "101799174956163"));
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + "101799174956163"));
            startActivity(intent);

        }

    }

    private void ShareOnFacebook() {
        setFacebookShareDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_LOADING_TIME = 4500;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {

                    soundPool.play(soundFBShare, 1, 1, 0, 0, 1);

                    Intent shareOnFacebookIntent = new Intent(Intent.ACTION_SEND);
                    shareOnFacebookIntent.setType("text/plain");
                    final String appPackageName = getApplicationContext().getPackageName();
                    ShareOnFacebookString = "https://play.google.com/store/apps/details?id=" +
                            "" + appPackageName;
                    shareOnFacebookIntent.putExtra(Intent.EXTRA_TEXT, ShareOnFacebookString);
                    shareOnFacebookIntent.setPackage("com.facebook.katana");
                    startActivity(shareOnFacebookIntent);
                    Animatoo.animateSpin(HomeActivity.this);


                } catch (Exception FacebookException) {

                    Toasty.warning(HomeActivity.this, "Install the Facebook App", Toast.LENGTH_LONG).show();

                }
            }
        }, TRANSITION_SCREEN_LOADING_TIME);
    }

    private void setFacebookShareDialogue() {
        final FacebookShareDialogue facebookShareDialogue = new FacebookShareDialogue(HomeActivity.this);
        facebookShareDialogue.startFacebookShareDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 4000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                facebookShareDialogue.dismissDialogue();
            }
        }, TRANSITION_SCREEN_TIME);
    }

    private void ShareOnTwitter() {
        setTwitterShareDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_LOADING_TIME = 4500;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    soundPool.play(soundTwitterShare, 1, 1, 0, 0, 1);

                    Intent shareOnTwitterIntent = new Intent(Intent.ACTION_SEND);
                    shareOnTwitterIntent.setType("text/plain");
                    final String appPackageName = getApplicationContext().getPackageName();
                    ShareOnTwitterString = "https://play.google.com/store/apps/details?id=" + appPackageName;
                    shareOnTwitterIntent.putExtra(Intent.EXTRA_TEXT, "Ferrari:We are the Competition      " +
                            "" + ShareOnTwitterString);
                    shareOnTwitterIntent.setPackage("com.twitter.android");
                    startActivity(shareOnTwitterIntent);
                    Animatoo.animateSpin(HomeActivity.this);


                } catch (Exception TwitterException) {

                    Toasty.warning(HomeActivity.this, "Install the Twitter App", Toast.LENGTH_LONG).show();

                }
            }
        }, TRANSITION_SCREEN_LOADING_TIME);
    }

    private void setTwitterShareDialogue() {
        final TwitterShareDialogue twitterShareDialogue = new TwitterShareDialogue(HomeActivity.this);
        twitterShareDialogue.startTwitterShareDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 4000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                twitterShareDialogue.dismissDialogue();
            }
        }, TRANSITION_SCREEN_TIME);
    }

    private void aboutUs() {
        Intent openAboutUsFromMain = new Intent(HomeActivity.this, AboutUsActivity.class);
        startActivity(openAboutUsFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);

    }

    private void openCompanyInfo() {
        Intent openCompanyInfoActivityFromMain = new Intent(HomeActivity.this, CompanyInfo.class);
        startActivity(openCompanyInfoActivityFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    public void updateNavHeader() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(navUserPhoto);
                final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
                displayDataEmailPassword(databaseReference);
            }
        });
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                navUserMail.setText(currentUser.getEmail());
                navUsername.setText(currentUser.getDisplayName());
                Glide.with(HomeActivity.this).load(currentUser.getPhotoUrl()).into(navUserPhoto);
            }
        });

    }

    private void displayDataEmailPassword(DatabaseReference databaseReference) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(UserProfile.class);


                navUsername.setText(userProfile.getUserName());
                navUserMail.setText(userProfile.getUserEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(HomeActivity.this, databaseError.getCode(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void itemTouchCategories() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(categoriesRecyclerView);
    }

    private void loadMainData() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(HomeActivity.this, 1);
        categoriesRecyclerView.setLayoutManager(gridLayoutManager);
        myCategoriesList = new ArrayList<>();
        mCategoriesData = new CategoriesData("2021 Ferrari 812 Superfast", "•\tBase price: US$340,712\n" +
                "•\tEngine: 6.5L naturally aspirated V12\n" +
                "•\tPower: 789 hp @ 8,500 rpm\n" +
                "•\tTorque: 530 lb-ft @ 7,000 rpm\n" +
                "•\t0-60 mph: 2.8 s\n" +
                "•\t0-100 mph: 5.8 s\n" +
                "•\tTop Speed: 211 mph\n" +
                "Updates for 2021: The Ferrari 812 now comes in GTS configuration, which in this case is a codeword for ‘convertible’. The otherwise mechanically identical GTS features an electronically controlled retractable hardtop.\n" +
                "A grand tourer with a whopping 789 hp, wrapped in a curvaceous Italian body, paired to one of the greatest chassis we have ever experienced. With its front-mid-mounted naturally aspirated 6.5L V12 engine and rear-wheel-drive layout, it is the latest iteration of Ferrari’s super-GT car.\n" +
                "It features a 7-speed dual-clutch gearbox, advanced active aerodynamics, and four-wheel steering. The Ferrari 812 Superfast is the successor to the Ferrari F12 and is now the company’s fastest front-engined-V12-powered grand tourer.\n" +
                "Despite the powerplant sitting in front of the driver, the 812 Superfast remains an engaging and soul-satisfying Ferrari experience, every single time you get into it. The perfect car. The perfect Ferrari.\n\n", "340000", R.drawable.first_categories, "0", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Ferrari SF90 Stradale", "•\tBase price: US$507,000\n" +
                "•\tEngine: 4.0L twin-turbocharged V8, plus 3 electric motors\n" +
                "•\tPower: 989 hp (combined)\n" +
                "•\tTorque: 590 lb-ft\n" +
                "•\t0-60 mph: 2.5 s\n" +
                "•\t0-124 mph: 6.7 s\n" +
                "•\tTop Speed: 211 mph\n" +
                "Ferrari’s SF90 Stradale is a stunning new hybrid supercar that produces 989 hp from a plug-in hybrid powertrain. This hybrid setup utilizes a twin-turbocharged 4.0L V8 combustion engine, paired with three electric motors.\n" +
                "Two of those electric motors are mounted on the front axle and one is mounted between the engine and the gearbox. The combined maximum output of the V8, together with the electric motors, makes this Ferrari good for 0-60 mph in just 2.5 seconds. This powertrain is the most powerful of any Ferrari and easily places the SF90 Stradale atop the Ferrari lineup.\n" +
                "The car also features an all-new chassis made of carbon fiber and aluminum. The sleek body panels and its aerodynamic shape help the model produce a whopping 860 pounds of downforce at speed; the whole profile of the car is extremely low, allowing it to slice through the air at high speeds.\n" +
                "It has a two-piece rear wing which is derived from the company’s participation in Formula 1 racing.\n\n", "570000", R.drawable.second_categories, "1", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Ferrari SF90 Spider", "•\tBase price: US$557,000\n" +
                "•\tEngine: 4.0L twin-turbocharged V8, plus 3 electric motors\n" +
                "•\tPower: 989 hp (combined)\n" +
                "•\tTorque: 590 lb-ft\n" +
                "•\t0-60 mph: 2.5 s\n" +
                "•\t0-124 mph: 6.7 s\n" +
                "•\tTop Speed: 211 mph\n" +
                "The Ferrari SF90 Spider has now been unveiled as Ferrari’s first plug-in hybrid roadster with close to 1,000 hp. The car is the open-top version of the SF90 Stradale. It maintains many of Stradale’s specs, including a 211 mph top speed. It will do a 0-60 mph time in just 2.5 seconds.\n" +
                "The SF90 Spider features a retractable hardtop that’s made of aluminum. This saves around 88 pounds over other traditional materials, according to Autoblog. However, the Spider still weighs 220 pounds more than the Stradale. The Spider’s roof can be lowered in 14 seconds and operated when the car is standing still or at low speeds.\n" +
                "There’s also a powered rear window that can be raised even when the top is down. This provides a bit more wind protection. Ferrari didn’t stop there in terms of airflow around the cockpit. The central trim piece between the seats also manages to channel air away from the occupants.\n\n", "557000", R.drawable.third_categories, "2", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Ferrari Portofino M", "•\tBase price: US$245,000\n" +
                "•\tEngine: 3.9L twin-turbocharged V8\n" +
                "•\tPower: 612 hp @ 7,500 rpm\n" +
                "•\tTorque: 560 lb-ft @ 3,000 rpm\n" +
                "•\t0-60 mph: 3.4 s\n" +
                "•\t0-124 mph: 9.3 s\n" +
                "•\tTop Speed: 199 mph\n" +
                "The Ferrari Portofino has been, for a couple of years, the Italian marque’s 2+2 grand touring cabriolet. It was, and still is, a powerhouse of comfort and technology, easily able to cross continents as much as drive a few blocks to the grocery store.\n" +
                "Now, however, it is getting its first refresh, thanks in large part to the success of the Ferrari Roma, which itself was a hardtop coupe evolution of the Portofino. Named the Portofino Modificata, it is shortened to Portofino M for branding purposes.\n" +
                "The highlight of this update has to be the newly developed eight-speed, dual-clutch automatic transmission. The everyday drop-top has also been refined on some other aspects which now makes it an even easier car to live with. A boatload of safety tech has been added and now the engine makes 20 hp more.\n\n", "245000", R.drawable.fourth_categories, "3", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Ferrari F8 Tributo", "•\tBase price: US$276,000\n" +
                "•\tEngine: 3.9L twin-turbocharged V8\n" +
                "•\tPower: 710 hp @ 8,000 rpm\n" +
                "•\tTorque: 568 lb-ft @ 3,250 rpm\n" +
                "•\t0-60 mph: 2.9 s\n" +
                "•\t0-124 mph: 7.8 s\n" +
                "•\tTop Speed: 211 mph\n" +
                "Billed as the replacement for the 488 GTB, the F8 Tributo inherits much of the outgoing model’s DNA. Mind you, this is large – if not entirely – a positive thing, as the F8 Tributo notably improves in areas where there was room to while keeping in the essence of what was working so well before.\n" +
                "The 2021 Ferrari F8 Tributo is the latest and greatest V8-powered Berlinetta to be produced by the prancing horse marque. Considered the ‘entry-level’ mid-engined car in the Ferrari model lineup, the F8 Tributo is nevertheless the greater of the sum of its parts; it is a highly capable all-rounder, standing out amongst an expanding club of ‘everyday supercars’.\n" +
                "Producing 710 hp at a screaming 8,000 rpm and 568 lb-ft of torque at an accessible 3,250 rpm, the F8 Tributo’s 3.9L twin-turbocharged V8 is nothing to balk at, despite being standard for the times.\n\n", "276000", R.drawable.fifth_categories, "4", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Ferrari F8 Spider", "•\tBase price: US$274,000\n" +
                "•\tEngine: 3.9L twin-turbocharged V8\n" +
                "•\tPower: 710 hp @ 8,000 rpm\n" +
                "•\tTorque: 568 lb-ft @ 3,250 rpm\n" +
                "•\t0-60 mph: 2.9 s\n" +
                "•\t0-124 mph: 7.8 s\n" +
                "•\tTop Speed: 211 mph\n" +
                "The F8 Spider replaces the 488 Spider and is officially on sale in Ferrari dealerships. It is powered by a twin-turbocharged 3.9-liter V-8 that produces 710 horsepower and 568 lb-ft of torque. It is rear-wheel drive, and a seven-speed automatic transmission changes the gears. Peak torque comes earlier in the rev range than the 488. The aero kit, headlights, taillights, and body look different than the 488 GTB.\n" +
                "We drove both the F8 Spider and Tributo back to back and our pick is the Spider. It is just as fast and dynamic as the coupe, but it feels faster, louder, and more involving thanks to its open top. It feels more visceral.\n" +
                "Like the F8 Tributo, the 2021 Spider accelerates from 0-60 mph in just 2.8 seconds, on its way to 124 mph in just 7.8 seconds, and a top speed of 211 mph. Fast enough I think.\n\n", "274000", R.drawable.sixth_categories, "5", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Ferrari Roma", "•\tBase price: US$222,630\n" +
                "•\tEngine: 3.9L twin-turbocharged V8\n" +
                "•\tPower: 612 hp @ 7,500 rom\n" +
                "•\tTorque: 560 lb-ft @ 3,000 rpm\n" +
                "•\t0-60 mph: 3.4 s\n" +
                "•\t0-124 mph: 9.3 s\n" +
                "•\tTop Speed: 199 mph\n" +
                "The vehicle is a stunning thing to look at, with a minimalist (by today’s standards) grille and a shark-nose front end. It’s long, lean, and so utterly Ferrari that it makes all the right places on a true car enthusiast ache with want.\n" +
                "Inside the car, you can see that it’s one of the most high-tech cabins of any Ferrari. There’s a large digital instrument cluster, a unique vertically oriented infotainment screen in the center with some controls in front of it and the passenger has their own small horizontally oriented infotainment screen.\n" +
                "Now onto even better stuff; the rear-wheel-drive Roma gets a 3.9L twin-turbocharged V8 engine with new cam profiles and a speed sensor that allows the maximum rpm to rise by 5,000 rpm. In other words, this is an Italian Stallion that can truly sing. The engine has a single-piece exhaust manifold that’s designed to make the most of the engine’s efforts. All told, it makes 612 hp and 560 lb-ft of torque.\n\n", "222000", R.drawable.seventh_categories, "6", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Ferrari 812 GTS", "•\tBase price: US$363,730\n" +
                "•\tEngine: 6.5L naturally aspirated V8\n" +
                "•\tPower: 789 hp @ 8,900 rpm\n" +
                "•\tTorque: 530 lb-ft @ 7,000 rpm\n" +
                "•\t0-60 mph: 2.9 s\n" +
                "•\t0-124 mph: TBD\n" +
                "•\t\n" +
                "Top Speed: 210 mph\n" +
                "The 2021 Ferrari 812 GTS Spider is a convertible variant of the 812 Superfast. It's the most powerful Spider in the world, with the performance to match.\n" +
                "What makes the 812 GTS so unique is the fact that it's powered by a 6.5-liter V12 that produces nearly 800 horsepower, and will do over 210 mph with the top down. The 812 GTS takes on more of a GT attitude rather than an all-out supercar attack and is surprisingly docile when it needs to be.\n" +
                "It is also the first series-production front-engined V12 Ferrari Spider in over 50 years. That is a huge deal. It is also a proper hard-top when it needs to be, letting you have your cake and eat it too. Add usable trunk space and that V12 noise, and this may be the best GT on the market.\n" +
                "Car and Driver said it best when they said: \"The Ferrari 812 GTS embodies the grand-tourer archetype with its comfortable driver-focused cabins, melodious V-12s, and incredible performance\"\n\n", "363000", R.drawable.eighth_categories, "7", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Ferrari Monza SP1", "•\tBase price: >US$1,000,000\n" +
                "•\tEngine: 6.5 liter naturally aspirated V12\n" +
                "•\tPower: 810 bhp @ 8,500 rpm\n" +
                "•\tTorque: 530 lb-ft @ 7000 rpm\n" +
                "•\t0-60 mph: < 3 sec (est)\n" +
                "•\t0-100 mph: < 8 sec (est)\n" +
                "•\tTop Speed: 186 mph\n" +
                "Think of classic Ferraris of the 1950s coupled with the most advanced sports car technology available today and you pretty much nailed it. The first iteration of the program is the Ferrari Monza SP1 and SP2. Both the Monza SP1 and SP2 are based on the Ferrari 812 Superfast and come with a 6.5-liter V12 engine with 810 hp to the rear wheels.\n\n", "1000000", R.drawable.ninth_categories, "8", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Ferrari Monza SP2", "•\tBase price: >US$1,000,000\n" +
                "•\tEngine: 6.5 liter naturally aspirated V12\n" +
                "•\tPower: 810 bhp @ 8,500 rpm\n" +
                "•\tTorque: 530 lb-ft @ 7000 rpm\n" +
                "•\t0-60 mph: < 3 sec (est)\n" +
                "•\t0-100 mph: < 8 sec (est)\n" +
                "•\tTop Speed: 186 mph\n" +
                "Think of classic Ferraris of the 1950s coupled with the most advanced sports car technology available today and you pretty much nailed it. The first iteration of the program is the Ferrari Monza SP1 and SP2. Both the Monza SP1 and SP2 are based on the Ferrari 812 Superfast and come with a 6.5-liter V12 engine with 810 hp to the rear wheels.\n\n", "1000000", R.drawable.tenth_categories, "9", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Ferrari 488 Pista", "•\tBase price: US$350,000\n" +
                "•\tEngine: 3.9 liter twin turbo V8\n" +
                "•\tPower: 710 bhp @ 7,500 rpm\n" +
                "•\tTorque: 568 lb-ft @ 5500 rpm\n" +
                "•\t0-60 mph: 2.85 sec\n" +
                "•\t0-100 mph: 5.4 sec\n" +
                "•\tTop Speed: 211 mph\n" +
                "The 488 Pista is the marque’s latest Special Series model and – following in the footsteps of its predecessors – epitomizes the pinnacle of Ferrari road cars. Ferrari’s naturally aspirated V8s shrieked and snarled into the redline. The Pista barks and roars its way there. A different special series animal for sure, but an animal nonetheless. Almost perfect.\n\n", "350000", R.drawable.eleventh_categories, "10", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Ferrari 488 Pista Spider", "•\tBase price: US$350,000\n" +
                "•\tEngine: 3.9-liter twin turbo V8\n" +
                "•\tPower: 710 bhp @ 7,500 rpm\n" +
                "•\tTorque: 568 lb-ft @ 5500 rpm\n" +
                "•\t0-60 mph: 2.85 sec\n" +
                "•\t0-100 mph: 5.4 sec\n" +
                "•\tTop Speed: 211 mph\n" +
                "The Ferrari 488 Pista Spider is powered by the same engine used in the coupe – a twin-turbocharged 3.9L V8 which produces a magnificent 711-horsepower and 568 lb-ft of torque. The Spider is a convertible with a removal hardtop, though some would argue it functions more closely to Targa top vehicle. The Spider weighs 200 pounds more than the coupe.\n\n", "350000", R.drawable.twelth_categories, "11", "0");
        myCategoriesList.add(mCategoriesData);

        myCategoriesAdapter = new MyCategoriesAdapter(HomeActivity.this, myCategoriesList);
        categoriesRecyclerView.setAdapter(myCategoriesAdapter);
    }


}