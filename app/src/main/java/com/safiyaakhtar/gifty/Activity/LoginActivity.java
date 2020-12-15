package com.safiyaakhtar.gifty.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ServerValue;
import com.safiyaakhtar.gifty.CustomDesign.Constants;
import com.safiyaakhtar.gifty.CustomDesign.Utils;
import com.safiyaakhtar.gifty.POJO.SharedPrefManager;
import com.safiyaakhtar.gifty.POJO.User;
import com.safiyaakhtar.gifty.R;
import com.safiyaakhtar.gifty.connection.ConnectionClass;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class LoginActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";
    private final Context mContext = this;
    public SharedPrefManager sharedPrefManager;
    ConnectionClass connectionClass;
    ImageView imglogo;
    TextView txthead;
    private GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String idToken;
    private String name, email;
    private String photo;
    private Uri photoUri;
    private SignInButton mSignInButton;


    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignInButton = (SignInButton) findViewById(R.id.login_with_google);
        imglogo = (ImageView) findViewById(R.id.imglogo);
        txthead = (TextView) findViewById(R.id.txt);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);
        sharedPrefManager = new SharedPrefManager(mContext);
        if (sharedPrefManager.getISLogged_IN()) {
            Intent intent = new Intent(LoginActivity.this, Home_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        connectionClass = new ConnectionClass();
        mSignInButton.setOnClickListener(this);
//        FirebaseApp.initializeApp(this);


        configureSignIn();
//        ViewAnimator
//                .animate(imglogo,txthead)
//                .rotation(360)
//                .start();
        mAuth = FirebaseAuth.getInstance();
        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get signedIn user
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //if user is signed in, we call a helper method to save the user details to Firebase
                if (user != null) {
                    // User is signed in
                    createUserInFirebaseHelper();
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void createUserInFirebaseHelper() {

        final String encodedEmail = Utils.encodeEmail(email.toLowerCase());
        final Firebase userLocation = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail);
        userLocation.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    // Insert into Firebase database
                    User newUser = new User(name, photo, encodedEmail, timestampJoined);
                    userLocation.setValue(newUser);

//                    Toast.makeText(LoginActivity.this, "Account created!", Toast.LENGTH_SHORT).show();

                    // After saving data to Firebase, goto next activity
                    Intent intent = new Intent(LoginActivity.this, Home_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

//                Log.d(TAG, getString(R.string.log_error_occurred) + firebaseError.getMessage());
                //hideProgressDialog();
                if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
                } else {
//                    Toast.makeText(LoginActivity.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // This method configures Google SignIn
    public void configureSignIn() {
// Configure sign-in to request the user's basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(LoginActivity.this.getResources().getString(R.string.web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();
    }

    // This method is called when the signIn button is clicked on the layout
    // It prompts the user to select a Google account.
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // This IS the method where the result of clicking the signIn button will be handled
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //  Log.e(TAG,data.getDataString());
            if (result.isSuccess()) {

                // Google Sign In was successful, save Token and a state then authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();

                idToken = account.getIdToken();

                name = account.getDisplayName();
                email = account.getEmail();
                photoUri = account.getPhotoUrl();
                photo = photoUri.toString();

                // Save Data to SharedPreference
                sharedPrefManager = new SharedPrefManager(mContext);
                sharedPrefManager.saveIsLoggedIn(mContext, true);

                sharedPrefManager.saveEmail(mContext, email);
                sharedPrefManager.saveName(mContext, name);
                sharedPrefManager.savePhoto(mContext, photo);

                sharedPrefManager.saveToken(mContext, idToken);
                //sharedPrefManager.saveIsLoggedIn(mContext, true);

                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuthWithGoogle(credential);
            } else {
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //After a successful sign into Google, this method now authenticates the user with Firebase
    private void firebaseAuthWithGoogle(AuthCredential credential) {
        showProgressDialog();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
//                            Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                        } else {
                            createUserInFirebaseHelper();
                            CreateUser createUser = new CreateUser();
                            createUser.execute();

//                            Toast.makeText(LoginActivity.this, "Login successful",
//                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, Home_Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        }

                        hideProgressDialog();

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().signOut();
        }
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View view) {

        Utils utils = new Utils(this);
        int id = view.getId();

        if (id == R.id.login_with_google) {
            if (utils.isNetworkAvailable()) {
                signIn();
            } else {
//                Toast.makeText(LoginActivity.this, "Oops! no internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public class CreateUser extends AsyncTask {
        boolean isSuccess;
        boolean z;
        String back;

        @Override
        protected Object doInBackground(Object[] objects) {
            Connection con = connectionClass.CONN();
            try {
                if (con == null) {
                    isSuccess = false;
                } else {
                    String queryw = "select * from UserRegisteration where EmailId='" + email + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(queryw);
                    if (rs.next()) {
                        z = true;
                    }
                    if (!z) {
                        String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                                .format(Calendar.getInstance().getTime());
                        String query = " INSERT INTO UserRegisteration" +
                                "           (Username\n" +
                                "           ,EmailId\n" +
                                "           ,MobileNo\n" +
                                "           ,CreatedAt" +
                                "           ,Active)\n" +
                                "     VALUES ('" + name + "','" + email + "',' ','" + dates + "','1')";
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        isSuccess = true;

                    } else {
                        back = "yes";
                    }
                }
            } catch (Exception e) {
                isSuccess = false;
//                Log.e("safiya", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            hideProgressDialog();
            if (isSuccess) {
                Toast.makeText(mContext, "You have Been Successfully Logged In", Toast.LENGTH_SHORT).show();
            } else if (back == "yes") {
                Toast.makeText(mContext, "Welcome Back!", Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(mContext, "We Are Having Trouble Please Come Back Later", Toast.LENGTH_SHORT).show();
            }
        }

    }

}