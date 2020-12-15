package com.safiyaakhtar.gifty.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.safiyaakhtar.gifty.Adapter.CustomGridViewHomeActivity;
import com.safiyaakhtar.gifty.Notification.Config;
import com.safiyaakhtar.gifty.Notification.NotificationUtils;
import com.safiyaakhtar.gifty.POJO.SharedPrefManager;
import com.safiyaakhtar.gifty.R;
import com.safiyaakhtar.gifty.connection.ConnectionClass;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import in.srain.cube.views.GridViewWithHeaderAndFooter;


public class Home_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    GridViewWithHeaderAndFooter androidGridView;
    private static final String TAG = Home_Activity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ConnectionClass connectionClass;
    ImageView img_trending;
    String[] gridViewString = {
            "Gifts For Men", "Gifts For Women", "Gifts For Kids",
            "Geeky Stuff", "Gifts For Couples", "Food & Drinks",
            "Home & Office", "Toys & Games", "Travel",
            "Wearables", "Random", "Music Instruments", "DIY & Customize", "Gifts For Festivals", "Funny Gifts", "More..."};

    String[] gridViewImageId = {
            "https://life-news.blog/wp-content/uploads/2019/04/giftsforman.jpg",
            "https://life-news.blog/wp-content/uploads/2019/04/giftsforwomen.jpeg",
            "https://life-news.blog/wp-content/uploads/2019/04/giftsforkids.jpg",
            "https://life-news.blog/wp-content/uploads/2019/04/geekygift.jpg",
            "https://life-news.blog/wp-content/uploads/2019/04/giftsforcouple.jpg",
            "https://life-news.blog/wp-content/uploads/2019/04/food.jpg",
            "https://life-news.blog/wp-content/uploads/2019/04/home.jpeg",
            "https://life-news.blog/wp-content/uploads/2019/04/toys.jpeg",
            "https://life-news.blog/wp-content/uploads/2019/04/travel.jpeg",
            "https://life-news.blog/wp-content/uploads/2019/04/wearables.jpeg",
            "https://life-news.blog/wp-content/uploads/2019/04/random.jpeg",
            "https://life-news.blog/wp-content/uploads/2019/04/music.jpeg",
            "https://life-news.blog/wp-content/uploads/2019/04/diy.jpg",
            "https://life-news.blog/wp-content/uploads/2019/04/festival.jpg",
            "https://life-news.blog/wp-content/uploads/2019/04/funny.jpg",
            "https://life-news.blog/wp-content/uploads/2019/04/more.jpeg"};
    SharedPrefManager sharedPrefManager;
    Context mContext;
    ImageView img_user;
    TextView txtusername, txtuseremail;
    String username, email, uri;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;
    String userId;
    NavigationView navigationView;
    private ProgressDialog mProgressDialog;
    int isupdated;

SliderLayout sliderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);
        mContext = this;
        androidGridView = (GridViewWithHeaderAndFooter) findViewById(R.id.grid_category);
        setGridViewHeaderAndFooter();
        CustomGridViewHomeActivity adapterViewAndroid = new CustomGridViewHomeActivity
                (Home_Activity.this, gridViewString, gridViewImageId);
         img_trending=findViewById(R.id.img_trending);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseMessaging.getInstance().subscribeToTopic("global");
        connectionClass = new ConnectionClass();
        sharedPrefManager = new SharedPrefManager(mContext);
        //getting User ID

        showProgressDialog();
        final UserId userid = new UserId();
        userid.execute();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        img_user = (ImageView) header.findViewById(R.id.img_profile);
        txtuseremail = (TextView) header.findViewById(R.id.txt_email);
        txtusername = (TextView) header.findViewById(R.id.txt_username);
        androidGridView.setAdapter(adapterViewAndroid);

        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int i, long id) {
                Intent intent = new Intent(Home_Activity.this, ItemsActivity.class);
                intent.putExtra("id", +i);
                //intent.putExtra("userid", wishArray);
                startActivity(intent);
//                Toast.makeText(Home_Activity.this, "GridView Item: " + i, Toast.LENGTH_LONG).show();
            }
        });


        username = sharedPrefManager.getName();
        email = sharedPrefManager.getUserEmail();
        uri = sharedPrefManager.getPhoto();
        Uri mPhotoUri = Uri.parse(uri);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        configureSignIn();


        Picasso.with(mContext)
                .load(mPhotoUri)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(img_user);

        txtusername.setText(username);
        txtuseremail.setText(email);

        img_trending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Home_Activity.this,ItemsActivity.class);
                i.putExtra("id", 100 );
                startActivity(i);
            }
        });
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

//                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

//                    txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();
    }

    private void setSliderViews() {

        for (int i = 0; i <= 3; i++) {

            DefaultSliderView sliderView = new DefaultSliderView(this);

            switch (i) {
                case 0:
                    sliderView.setImageUrl("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=1260");
//                    sliderView.setImageDrawable(R.drawable.blog_icon);
                    break;
                case 1:
                    sliderView.setImageUrl("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=1260");
                    break;
                case 2:
                    sliderView.setImageUrl("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=650&w=1260");
                    break;
                case 3:
                    sliderView.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=1260");
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
//            sliderView.setDescription("The quick brown fox jumps over the lazy dog.\n" +
//                    "Jackdaws love my big sphinx of quartz. " + (i + 1));
            final int finalI = i;

            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    Toast.makeText(Home_Activity.this, "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show();

                }
            });

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }

    }

    private void setGridViewHeaderAndFooter() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View headerView = layoutInflater.inflate(R.layout.layout_header, null, false);
      //  View footerView = layoutInflater.inflate(R.layout.layout_footer, null, false);

        //locate views

        sliderLayout = headerView.findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.SWAP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        sliderLayout.setScrollTimeInSec(5); //set scroll delay in seconds :
        setSliderViews();
//        TextView headerText = (TextView)headerView.findViewById(R.id.text);
       // TextView footerText = (TextView)footerView.findViewById(R.id.text);

       // headerText.setText("GridView Header");
       // footerText.setText("GridView Footer");

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
      //  footerView.setOnClickListener(onClickListener(1));

        androidGridView.addHeaderView(headerView);
        //gridView.addFooterView(footerView);
    }
    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        FirebaseAuth mauth;
       Log.e("regid", String.valueOf(sharedPrefManager.regId())) ;
//        SharedPreferences pref = getApplicationContext().getSharedPreferences(sharedPrefManager, 0);
        String regId =sharedPrefManager.regId();
//
////        Log.e(TAG, "Firebase reg id: " + regId);
//
//        if (!TextUtils.isEmpty(regId))
////            Toast.makeText(mContext, ""+regId, Toast.LENGTH_SHORT).show();
////           // txtRegId.setText("Firebase Reg Id: " + regId);
//        else
//            Toast.makeText(mContext, "Firebase Reg Id is not received yet!", Toast.LENGTH_SHORT).show();
//           // txtRegId.setText("Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }




    @Override
    protected void onStop() {
        super.onStop();

//        new generatePictureStyleNotification(Home_Activity.this,"Empty Pocket", "New Gifts Available",
//                "https://life-news.blog/wp-content/uploads/2018/12/91pUTQEsvlL._SL1500_.jpg").execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            navigationView.getMenu().getItem(0).setChecked(true);
        } else if (id == R.id.nav_tranding) {
            Intent intent = new Intent(Home_Activity.this, ItemsActivity.class);
            intent.putExtra("id", 100);
            startActivity(intent);

        } else if (id == R.id.nav_wishlist) {
            Intent intent = new Intent(Home_Activity.this, WishListActivity.class);
            intent.putExtra("userid", userId);
            startActivity(intent);

        } else if (id == R.id.nav_submitproduct) {
            Intent intent = new Intent(Home_Activity.this, SubmitAProduct.class);
            intent.putExtra("userid", userId);
            startActivity(intent);

        } else if (id == R.id.nav_blog) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://life-news.blog/empty-pocket"));
            startActivity(browserIntent);

        } else if (id == R.id.nav_policy) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://life-news.blog/privacy-policy-empty-pocket"));
            startActivity(browserIntent);

        } else if (id == R.id.nav_dis) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://life-news.blog/affiliate-disclaimer"));
            startActivity(browserIntent);

        } else if (id == R.id.nav_logout) {
            signOut();

        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Insert Subject here");
            String app_url = "Download this app from PlayStore and Don't waste too much time picking unique gifts for" +
                    " your loved one's this time. Choose unique Gifts for your friends," +
                    " family, pets, kids, geeks, couple, Women, Men or any casual gifts. These Gifts are completely unique " +
                    "and you can buy them for yourself to have fun when you are alone or trying to spend some money on yourself " +
                    "rather than others and Empty Your Pockets! \n" +
                    "https://play.google.com/store/apps/details?id=" + mContext.getPackageName() + " Please Share this App From Friends or Family Members Too!";
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, app_url);
            startActivity(Intent.createChooser(shareIntent, "Share via"));

        } else if (id == R.id.nav_rate) {
            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } else if (id == R.id.nav_update) {
            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void configureSignIn() {
// Configure sign-in to request the user's basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();
    }

    //method to logout
    private void signOut() {
        showProgressDialoglog();
        new SharedPrefManager(mContext).clear();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Intent intent = new Intent(Home_Activity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
        );
        //   hideProgressDialog();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }
    public void showProgressDialoglog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Logging Out");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public class UserId extends AsyncTask {
        boolean isSuccess = false;

        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    isSuccess = false;
                } else {
                    String query = "select Srno,isUpdated from UserRegisteration where " +
                            "(REPLACE(REPLACE(EmailId,'.',''),'@',''))=(" +
                            "REPLACE(REPLACE('" + sharedPrefManager.getUserEmail() + "','.',''),'@',''))";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs.next()) {
                        isSuccess = true;
                        userId = rs.getString("Srno");
                        isupdated = rs.getByte("isUpdated");
                    }

                }
            } catch (Exception e) {
                isSuccess = false;
                Log.e("myerror", e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            hideProgressDialog();
            if (isSuccess) {
                sharedPrefManager.saveUserId(mContext, userId);
            }
        }
    }

}
