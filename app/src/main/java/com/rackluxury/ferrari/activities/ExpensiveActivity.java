package com.rackluxury.ferrari.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.rackluxury.ferrari.R;
import com.rackluxury.ferrari.adapters.ExpensiveData;
import com.rackluxury.ferrari.adapters.MyExpensiveAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ExpensiveActivity extends AppCompatActivity {
    RecyclerView expensiveRecyclerView;
    List<ExpensiveData> myExpensiveList;
    ExpensiveData mExpensiveData;

    private SharedPreferences prefs;

    private Toolbar toolbar;
    private ImageView backIcon;

    private MyExpensiveAdapter myExpensiveAdapter;

    private int soundLike;


    private SoundPool soundPool;

    final List<ExpensiveData> favouriteExpensive = new ArrayList<>();
    final List<ExpensiveData> deletedExpensive = new ArrayList<>();
    private int lastPosition;
    private ShimmerFrameLayout shimmerFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expensive);
        setUpUIViewsExpensiveActivity();
        setExpensiveDialogue();
        initToolbar();
        loadMainData();
        itemTouchExpensive();


        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("expensiveFirst", true);
        if (firstStart) {
            setPurchaseSuccessDialogue();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setShimmer(null);

            }
        },1500);







        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        expensiveRecyclerView.setLayoutManager(layoutManager);
        expensiveRecyclerView.setHasFixedSize(true);
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        lastPosition = getPrefs.getInt("lastPosExpensive", 0);
        expensiveRecyclerView.scrollToPosition(lastPosition);

        expensiveRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        e.putInt("lastPosExpensive", lastPosition);
        e.apply();
    }

    private void setUpUIViewsExpensiveActivity() {
        toolbar = findViewById(R.id.toolbarExpensivePage);
        backIcon = findViewById(R.id.backIconExpensive);
        expensiveRecyclerView = findViewById(R.id.rvExpensiveRecycler);
        shimmerFrameLayout =  findViewById(R.id.sflExpensive);

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

        soundLike = soundPool.load(this, R.raw.sound_like, 1);

    }
    private void setPurchaseSuccessDialogue() {
        final PurchaseSuccessDialogue purchaseSuccessDialogue = new PurchaseSuccessDialogue(ExpensiveActivity.this);
        purchaseSuccessDialogue.startPurchaseSuccessDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 4000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                purchaseSuccessDialogue.dismissDialogue();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("expensiveFirst", false);
                editor.apply();
            }
        }, TRANSITION_SCREEN_TIME);
    }

    private void setExpensiveDialogue() {
        final ExpensiveDialogue expensiveDialogue = new ExpensiveDialogue(ExpensiveActivity.this);
        expensiveDialogue.startExpensiveDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 1500;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                expensiveDialogue.dismissDialogue();
            }
        }, TRANSITION_SCREEN_TIME);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent openHomeFromExpensive = new Intent(ExpensiveActivity.this,HomeActivity.class);
                startActivity(openHomeFromExpensive);
                Animatoo.animateSwipeLeft(ExpensiveActivity.this);
            }
        });
    }

    private void loadMainData() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ExpensiveActivity.this, 1);
        expensiveRecyclerView.setLayoutManager(gridLayoutManager);
        myExpensiveList = new ArrayList<>();
        mExpensiveData = new ExpensiveData("2017 LaFerrari Aperta", "In 2017, Ferrari LaFerrari Aperta became the most expensive car sold at auction. The car was auctioned at RM Sothebys for €8,300,000 (approx. $9,980,000). There are four LaFerrari Aperta cars for sale on JamesEdtion with prices above US$4 million, and six other LaFerraris with prices starting from US$2.7 million."
                ,"4733000", R.drawable.first_expensive,"0","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("2017 Ferrari FXX-K2017 Ferrari FXX-K", "FXX-K was the brand’s first, limited-production, track day hybrid; the model is based on the LaFerrari, a street-legal hybrid sports car. The K in the car’s name refers to the kinetic energy recovery system (KERS) which is used to maximize performance. Like the previous FXX and 599XX, the cars are kept and maintained by Ferrari and are available to the owners on track day events."
                ,"4170000", R.drawable.second_expensive,"1","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("2003 Enzo", "The Enzo was designed by Ken Okuyama, Pininfarina’s head of design. Before production began, the limited run of 399 units was sold to customers who previously had bought the F40 and F50 models. With an initial price of US$659,330, one of the cars was later sold at a Sotheby’s auction for US$1.1 million. And now, in 2021, this iconic car is the third most expensive Ferrari in our stock."
                ,"2407000", R.drawable.third_expensive,"2","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("1984 Ferrari 288 GTO", "With GT for Grand Turismo and O for Omologato, the 280 GTO is an exotic homologation of Ferrari sports cars. This model, equipped with a GTO Evoluzione kit, was considered high-tech and avant-garde for its times. Its modifications included the use of Kevlar and carbon fiber as well as engine, chassis, and safety system upgrades."
                ,"2265000", R.drawable.fourth_expensive,"3","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("1991 Ferrari F40", "The closer we get to the US$2-million line, the less diversity we see. Only iconic classic models, sports car legends, and supercars rise to this level. Both the first and the second descriptors are true of the F40: the successor to the 288 GTO (mentioned below) and the final Ferrari automobile personally approved by Enzo Ferrari. This mid-engine, the rear-wheel-drive sports car was designed to celebrate Ferrari’s 40th anniversary."
                ,"1873000", R.drawable.fifth_expensive,"4","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("2011 Ferrari 599 Pininfarina", "With only 80 examples, this limited-edition model was created to commemorate the 80th anniversary of Pininfarina. The 599 Pininfarina officially is a member of the most limited Ferrari series, and one of the finest sports cars Ferrari has ever built. Its powerful, 6.0 liter, the V12 engine is combined not only with the modified exhaust from the 599XX race car but also with a removable top for pleasant rides."
                ,"1600000", R.drawable.sixth_expensive,"5","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("2017 Ferrari F12", "The F12tdf pays homage to the Tour de France which was regularly won by Ferrari in the 1950s and 1960s. The model boasts excellent performance and sharp looks, being the last Ferrari model designed by Pininfarina. On top of that, the version put up for sale via JamesEdition is finished in the historical colorway Grigio Ferro with a unique two-tone racing stripe (Nero center; Rosso Scuderia sides)."
                ,"1350000", R.drawable.seventh_expensive,"6","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData(" 1957 Ferrari 250 GT Coupe Boano", "Four classic Ferrari cars are on our list, and one of them is a beautiful 250 GT: Ferrari’s first, true production model. The Coupe Boano version was manufactured by Pininfarina’s partner, Carrozzeria Boano coachbuilding company. Although the car is not so widely known as the 250 GTO, its price both on car auctions and on the open market has risen above US$1 million. With that said, 250 GT is one of the best Ferraris to buy for investment"
                ,"1195000", R.drawable.eighth_expensive,"7","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData(" Ferrari 599 GTO", "The 599 GTO (for Gran Turismo Omologato) is a road-legal version of the 599XX track day car.  The 6-liter, V12 engine of the GTO produces 660 hp and comes from the legendary Ferrari Enzo. The power is transmitted via an F1 six-speed gearbox. This GTO scored the best time in the history of Ferrari on the racetrack in Fiorano."
                ,"879000", R.drawable.ninth_expensive,"8","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData(" 2015 Ferrari 458 Speciale Aperta", "A rare 458 Speciale Aperta is up for sale in Japan. Among other features, the car boasts a light-beige interior with special stitching; 20” forged gold, diamond rims; and gold brake calipers. The model was unveiled at the 2014 Paris Motor Show and led the list of fastest, street-legal, convertible Ferraris until LaFerrari Aperta took over."
                ,"786000", R.drawable.tenth_expensive,"9","0");
        myExpensiveList.add(mExpensiveData);

        myExpensiveAdapter = new MyExpensiveAdapter(ExpensiveActivity.this, myExpensiveList);
        expensiveRecyclerView.setAdapter(myExpensiveAdapter);
    }

    private void itemTouchExpensive() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(expensiveRecyclerView);
    }

    final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();
            final ExpensiveData ExpensiveItem = myExpensiveList.get(position);
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    deletedExpensive.add(ExpensiveItem);
                    myExpensiveList.remove(position);
                    myExpensiveAdapter.notifyItemRemoved(position);
                    Snackbar.make(expensiveRecyclerView, "Deleted.", Snackbar.LENGTH_LONG).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    favouriteExpensive.add(ExpensiveItem);
                    myExpensiveAdapter.notifyItemRemoved(position);
                    soundPool.play(soundLike, 1, 1, 0, 0, 1);
                    Snackbar.make(expensiveRecyclerView, "Added to favourites.", Snackbar.LENGTH_LONG).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView expensiveRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(ExpensiveActivity.this, c, expensiveRecyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ExpensiveActivity.this, R.color.colorLightRed))
                    .addSwipeLeftActionIcon(R.drawable.ic_deleted_swipe_main)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(ExpensiveActivity.this, R.color.colorFavourite))
                    .addSwipeRightActionIcon(R.drawable.ic_favourite_swipe_main)
                    .setActionIconTint(ContextCompat.getColor(expensiveRecyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, expensiveRecyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.expensive_menu, menu);
        getMenuInflater().inflate(R.menu.toolbar_search_expensive, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search_expensive);

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
        if (item.getItemId() == R.id.sort_expensive_price_low_to_high) {
            sortViewPriceLowToHigh();
            return true;
        } else if (item.getItemId() == R.id.sort_expensive_price_high_to_low) {
            sortViewPriceHighToLow();
            return true;
        } else if (item.getItemId() == R.id.sort_expensive_name_a_to_z) {
            sortViewNameAToZ();
            return true;
        } else if (item.getItemId() == R.id.sort_expensive_name_z_to_a) {
            sortViewNameZtoA();
            return true;
        } else if (item.getItemId() == R.id.favourite_expensive) {
            openExpensiveFav();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openExpensiveFav() {
        Intent view = new Intent(ExpensiveActivity.this, FavouriteExpensiveActivity.class);
        startActivity(view);
        Animatoo.animateSplit(ExpensiveActivity.this);
    }

    public void sortViewNameAToZ() {
        Collections.sort(myExpensiveList, ExpensiveData.ByNameAToZ);
        myExpensiveAdapter.notifyDataSetChanged();
    }

    public void sortViewNameZtoA() {
        Collections.sort(myExpensiveList, ExpensiveData.ByNameZToA);
        myExpensiveAdapter.notifyDataSetChanged();
    }

    public void sortViewPriceLowToHigh() {
        Collections.sort(myExpensiveList, ExpensiveData.ByPriceLowToHigh);
        myExpensiveAdapter.notifyDataSetChanged();
    }

    public void sortViewPriceHighToLow() {
        Collections.sort(myExpensiveList, ExpensiveData.ByPriceHighToLow);
        myExpensiveAdapter.notifyDataSetChanged();
    }


    private void filter(String text) {

        ArrayList<ExpensiveData> filterList = new ArrayList<>();

        for (ExpensiveData item : myExpensiveList) {

            if (item.getExpensiveName().toLowerCase().contains(text.toLowerCase())) {

                filterList.add(item);

            }

        }

        myExpensiveAdapter.filteredList(filterList);

    }


    @Override
    public void onBackPressed() {
        finish();
        Intent openHomeFromExpensive = new Intent(ExpensiveActivity.this,HomeActivity.class);
        startActivity(openHomeFromExpensive);
        Animatoo.animateSwipeLeft(ExpensiveActivity.this);
    }
}