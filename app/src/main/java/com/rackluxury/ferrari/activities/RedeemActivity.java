package com.rackluxury.ferrari.activities;

import android.animation.ArgbEvaluator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.rackluxury.ferrari.R;
import com.rackluxury.ferrari.blog.BlogActivity;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class RedeemActivity extends AppCompatActivity {
    private TextView coinsAvailable;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef, mRefStatus;

    private int usercoin;
    private float x;
    private Integer integer;
    private StorageReference storageReference;
    private float usermoney;
    private int usermoneyCoins, usercoins;
    private SharedPreferences coins;
    private String currentCoins;

    ViewPager viewPagerRedeem;
    AdapterRedeem adapterRedeem;
    List<Model> models;
    private RelativeLayout layout;


    final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    Integer[] colors = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        layout = findViewById(R.id.layRedeem);
        coins = getSharedPreferences("Rewards", MODE_PRIVATE);
        redeemChoice();


        ImageView imageView = findViewById(R.id.ivBackRedeem);
        Button buyMore = findViewById(R.id.btnBuyMoreRedeem);

        buyMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RedeemActivity.this, BuyCoinsActivity.class);
                startActivity(intent);
                Animatoo.animateSwipeLeft(RedeemActivity.this);

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        coinsAvailable = findViewById(R.id.tvCoinsRedeem);
        final Handler handler = new Handler();
        FirebaseDatabase database11 = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user11 = mAuth.getCurrentUser();
        String userId11 = user11.getUid();
        mRef = database11.getReference().child(userId11);
        mRef.child("RedeemCoins").removeValue();
        mRef.child("RedeemUSD").removeValue();
        mRef.child("Coins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usercoin = Integer.parseInt(dataSnapshot.getValue(String.class));
                coinsAvailable.setText(String.valueOf(usercoin));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        integer = Integer.valueOf(coinsAvailable.getText().toString());
        FirebaseDatabase database22 = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user22 = mAuth.getCurrentUser();
        String userId22 = user22.getUid();
        mRef = database22.getReference().child(userId22);

        Button button = findViewById(R.id.btnApplyRedeem);
        int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coinCount > 500000) {

                    int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
                    coinCount = coinCount - 500000;
                    SharedPreferences.Editor coinsEdit = coins.edit();
                    coinsEdit.putString("Coins", String.valueOf(coinCount));
                    coinsEdit.apply();
                    Toasty.info(RedeemActivity.this, "500,000 Coins have been Used.", Toast.LENGTH_LONG).show();
                    coinsAvailable.setText(String.valueOf(coinCount));
                    coins = getSharedPreferences("Rewards", MODE_PRIVATE);
                    currentCoins = coins.getString("Coins", "0");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference.child("Coins").setValue(currentCoins);


                    LayoutInflater inflater = LayoutInflater.from(RedeemActivity.this);
                    View view = inflater.inflate(R.layout.alert_dialog_redeem, null);
                    Button acceptButton = view.findViewById(R.id.btnAcceptAlertRedeem);
                    final AlertDialog alertDialog = new AlertDialog.Builder(RedeemActivity.this)
                            .setView(view)
                            .show();

                    acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();

                        }
                    });

                }else {
                    Toasty.info(RedeemActivity.this, "You don't have so many coins", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    private void redeemChoice() {
        models = new ArrayList<>();
        models.add(new Model(R.drawable.redeem_01, "1955 Ferrari 750 Monza", "$ 5 350 000"));
        models.add(new Model(R.drawable.redeem_02, "2016 Ferrari LaFerrari - UK Supplied", "$ 4 769 810"));
        models.add(new Model(R.drawable.redeem_03, "1958 Ferrari 250", "$ 4 350 000"));
        models.add(new Model(R.drawable.redeem_04, "2016 Ferrari J50", "$ 3 757 330"));
        models.add(new Model(R.drawable.redeem_05, "1958 Ferrari 250", "$ 3 750 000"));
        models.add(new Model(R.drawable.redeem_06, "1954 Ferrari 500 Mondial", "$ 3 250 000"));
        models.add(new Model(R.drawable.redeem_07, "2016 Ferrari LaFerrari", "$ 2 991 395"));
        models.add(new Model(R.drawable.redeem_08, "2014 Ferrari LaFerrari", "$ 2 991 395"));
        models.add(new Model(R.drawable.redeem_09, "1960 Ferrari 250 - GT Cabriolet", "$ 2 453 080"));
        models.add(new Model(R.drawable.redeem_10, "1967 Ferrari 330", "$ 2 450 000"));
        models.add(new Model(R.drawable.redeem_11, "2020 Ferrari Monza - SP2", "$ 2 410 664"));
        models.add(new Model(R.drawable.redeem_12, "1963 Ferrari 250 - GT Berlinetta Lusso", "$ 2 254 798"));




        adapterRedeem = new com.rackluxury.ferrari.activities.AdapterRedeem(models, this);

        viewPagerRedeem = findViewById(R.id.viewPagerRedeem);
        viewPagerRedeem.setAdapter(adapterRedeem);
        viewPagerRedeem.setPadding(130, 0, 130, 0);

        colors = new Integer[]{
                getResources().getColor(R.color.colorRedeem01),
                getResources().getColor(R.color.colorRedeem02),
                getResources().getColor(R.color.colorRedeem03),
                getResources().getColor(R.color.colorRedeem04),
                getResources().getColor(R.color.colorRedeem05),
                getResources().getColor(R.color.colorRedeem06),
                getResources().getColor(R.color.colorRedeem07),
                getResources().getColor(R.color.colorRedeem08),
                getResources().getColor(R.color.colorRedeem09),
                getResources().getColor(R.color.colorRedeem10),
                getResources().getColor(R.color.colorRedeem11),
                getResources().getColor(R.color.colorRedeem12)

        };

        viewPagerRedeem.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (adapterRedeem.getCount() - 1) && position < (colors.length - 1)) {
                    layout.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                } else {
                    layout.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void openBlog(View view) {
        Intent intent = new Intent(getApplicationContext(), BlogActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent view = new Intent(RedeemActivity.this, DashboardActivity.class);
        startActivity(view);
        Animatoo.animateSwipeLeft(RedeemActivity.this);
    }
}
