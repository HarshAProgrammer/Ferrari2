package com.rackluxury.ferrari.activities;

import android.animation.ArgbEvaluator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rackluxury.ferrari.R;
import com.rackluxury.ferrari.blog.BlogActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RedeemActivity extends AppCompatActivity {
    private TextView coins2;
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

    ViewPager viewPagerRedeem;
    com.rackluxury.ferrari.activities.AdapterRedeem adapterRedeem;
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

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        if (dpHeight > 700) {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        ImageView imageView = findViewById(R.id.imageView4);
        Button buyMore = findViewById(R.id.btnBuyMoreRedeem);

        buyMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RedeemActivity.this, com.rackluxury.ferrari.activities.BuyCoinsActivity.class);
                        startActivity(intent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        coins2 = (TextView) findViewById(R.id.textViewCoins);
        final TextView calcmoney = (TextView) findViewById(R.id.textView6);
        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds
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
                coins2.setText(String.valueOf(usercoin));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        integer = Integer.valueOf(coins2.getText().toString());
        FirebaseDatabase database22 = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user22 = mAuth.getCurrentUser();
        String userId22 = user22.getUid();
        mRef = database22.getReference().child(userId22);

        Button button = findViewById(R.id.button7);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (integer < usercoin && integer > 10) {

                    mRef.child("RedeemCoins").setValue(String.valueOf(integer));
                    StorageReference imageReference1 = storageReference.child(firebaseAuth.getUid()).child("Blog Purchased");
                    Uri uri1 = Uri.parse("android.resource://com.rackluxury.ferrari/drawable/img_blog_checker");
                    UploadTask uploadTask = imageReference1.putFile(uri1);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(RedeemActivity.this, "Please Check Your Internet Connectivity", Toast.LENGTH_LONG).show();

                        }
                    });
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            Toasty.success(RedeemActivity.this, "Purchase Successful", Toast.LENGTH_LONG).show();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            mAuth = FirebaseAuth.getInstance();
                            FirebaseUser user1 = mAuth.getCurrentUser();
                            String userId = user1.getUid();
                            mRef = database.getReference().child(userId);
                            mRef.child("RedeemUSD").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    usermoney = Float.parseFloat(dataSnapshot.getValue(String.class));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                            mRef.child("RedeemCoins").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    usermoneyCoins = Integer.parseInt(dataSnapshot.getValue(String.class));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                            mRef.child("Coins").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    usercoins = Integer.parseInt(dataSnapshot.getValue(String.class));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                            final ProgressDialog dialog = new ProgressDialog(RedeemActivity.this);
                            dialog.setTitle("Sending Email");
                            dialog.setMessage("Please wait");
                            dialog.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Thread sender = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                dialog.dismiss();
                                                int result = usercoins - usermoneyCoins;
                                                mRef.child("RedeemCoins").removeValue();
                                                mRef.child("RedeemUSD").removeValue();

                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                mAuth = FirebaseAuth.getInstance();
                                                FirebaseUser user1 = mAuth.getCurrentUser();
                                                String userId = user1.getUid();
                                                mRefStatus = database.getReference().child("Redeem").push();
                                                mRefStatus.child("Status").setValue("Review");

                                                mRefStatus.child("MoneyUSD").setValue(String.valueOf(usermoney));

                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(userId).child("Redeem").push();
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("id", databaseReference.getKey());
                                                map.put("Redeem", usermoney);
                                                Calendar c = Calendar.getInstance();

                                                int day = c.get(Calendar.DAY_OF_MONTH);
                                                int month = c.get(Calendar.MONTH);
                                                int year = c.get(Calendar.YEAR);
                                                String date = day + ". " + month + ". " + year;
                                                map.put("Date", date);
                                                databaseReference.setValue(map);

                                                SharedPreferences.Editor coinsEdit = coins.edit();
                                                coinsEdit.putString("Coins", String.valueOf(result));
                                                coinsEdit.apply();

                                                Intent intent = new Intent(RedeemActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();

                                            } catch (Exception e) {
                                                Log.e("mylog", "Error: " + e.getMessage());
                                            }
                                        }
                                    });
                                    sender.start();
                                }
                            }, 2500);


                        }
                    });

                } else {
                    Toasty.info(RedeemActivity.this, "You don't have so many coins", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    private void redeemChoice() {
        models = new ArrayList<>();
        models.add(new Model(R.drawable.redeem_1, "1955 Ferrari 750 Monza", "$ 5 350 000"));
        models.add(new Model(R.drawable.redeem_2, "2016 Ferrari LaFerrari - UK Supplied", "$ 4 769 810"));
        models.add(new Model(R.drawable.redeem_3, "1958 Ferrari 250", "$ 4 350 000"));
        models.add(new Model(R.drawable.redeem_4, "2016 Ferrari J50", "$ 3 757 330"));
        models.add(new Model(R.drawable.redeem_5, "1958 Ferrari 250", "$ 3 750 000"));
        models.add(new Model(R.drawable.redeem_6, "1954 Ferrari 500 Mondial", "$ 3 250 000"));
        models.add(new Model(R.drawable.redeem_7, "2016 Ferrari LaFerrari", "$ 2 991 395"));
        models.add(new Model(R.drawable.redeem_8, "2014 Ferrari LaFerrari", "$ 2 991 395"));
        models.add(new Model(R.drawable.redeem_9, "1960 Ferrari 250 - GT Cabriolet", "$ 2 453 080"));
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
        Animatoo.animateSwipeLeft(RedeemActivity.this);
    }
}
