package com.sanju.chat.vidschat.Activities;

/**
 * Created by Sanju on 02-Jan-17.
 */


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sanju.chat.vidschat.R;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN =9001 ;
    private static final int FB_SIGN_IN =64206 ;

    FirebaseAuth fbauth;
    FirebaseAuth.AuthStateListener fbautListener;
    EditText edt_username;
    EditText edt_password;
    Button btn_login;
    Button btn_signup;
    TextView txt_btn_forgotpswd;
    Button btn_fb_login;
    SignInButton signInButton;
    GoogleSignInOptions gso;
    ProgressDialog progressDialog;

    private CallbackManager mCallbackManager;
    FirebaseAuth.AuthStateListener mAuthListener;
    GoogleApiClient mGoogleApiClient;
    String login_user,login_password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AppEventsLogger.activateApp(this);
        mCallbackManager = CallbackManager.Factory.create();
        if (AccessToken.getCurrentAccessToken() != null){
            LoginManager.getInstance().logOut();
        }


        setContentView(R.layout.activity_login);

        fbauth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //dialog.dismiss();
                //user = firebaseAuth.getCurrentUser();
            }
        };

        edt_username = (EditText)findViewById(R.id.et_username);
        edt_password = (EditText)findViewById(R.id.et_password);
        btn_login = (Button)findViewById(R.id.bt_login);
        btn_signup = (Button)findViewById(R.id.bt_sign);
        txt_btn_forgotpswd = (TextView)findViewById(R.id.tv_forgot);
        btn_fb_login = (Button) findViewById(R.id.login_button);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);


        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
        txt_btn_forgotpswd.setOnClickListener(this);
        btn_fb_login.setOnClickListener(this);
        signInButton.setOnClickListener(this);





        //Google Signin Integration..............
         gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



    }

    @Override
    protected void onStart() {
        super.onStart();
        fbauth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            fbauth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view)
    {
        login_user= edt_username.getText().toString().trim();
        login_password= edt_password.getText().toString().trim();
        switch (view.getId())
        {
            case R.id.bt_login:

                final ProgressDialog progressDialog1 = new ProgressDialog(LoginActivity.this);

                
                if(login_user.isEmpty())
                    edt_username.setError("Empty Field");
                else if(login_password.isEmpty())
                    edt_password.setError("password Emmpty");
                else
                {
                    fbauth.signInWithEmailAndPassword(login_user,login_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("","SignInWithEmail:on Complete" + task.isSuccessful());

                            if(!task.isSuccessful())
                            {
                                Log.v("","signWithEmail: failed",task.getException());
                                Toast.makeText(LoginActivity.this, " Something Wrong ", Toast.LENGTH_SHORT).show();
                            progressDialog1.dismiss();
                                edt_password.setError("Wrong Password");
                            }
                            else
                            {
                                progressDialog1.setMessage("Logingggg.....");
                                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                Toast.makeText(LoginActivity.this, "Welcome to Home", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }

                break;
            case R.id.bt_sign:
                progressDialog();
                startActivity(new Intent(this,SignupActivity.class));

                break;
            case R.id.tv_forgot:
                Toast.makeText(this, "forgot Password", Toast.LENGTH_SHORT).show();
                break;
            case R.id.login_button:
                Toast.makeText(this, "FB Login", Toast.LENGTH_SHORT).show();

                facebook_login();

                break;
            case R.id.sign_in_button:

                    signIn();

                break;
        }
    }

    public void progressDialog()
    {
        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Registering....");
        progressDialog.show();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void facebook_login()
    {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //dialog.dismiss();
                //dialog = ProgressDialog.show(LoginActivity.this, "","Please Wait...", true);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                //dialog.dismiss();
                Toast.makeText(LoginActivity.this, "User Cancel", Toast.LENGTH_LONG).show();
//                        rlayout_parent.setVisibility(View.VISIBLE);

            }

            @Override
            public void onError(FacebookException error) {
                //dialog.dismiss();
                Log.d("error:", error.toString());
                Toast.makeText(LoginActivity.this, "Something Wrong", Toast.LENGTH_LONG).show();
//                        rlayout_parent.setVisibility(View.VISIBLE);

            }
        });
    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("fbbbbb", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fbauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("fbbbbb", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("fbbbbb", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed."+task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            progressDialog();
                            Toast.makeText(LoginActivity.this, "Fb Loging Successful", Toast.LENGTH_SHORT).show();

                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            final DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(uid);

                            databaseReference.child("name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        //databaseReference.child("Contact").setValue(phone);
                                        //databaseReference.child("Country").setValue(country);
                                        databaseReference.child("email").setValue((FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                                        Toast.makeText(LoginActivity.this,"Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(LoginActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }


                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                //dialog.dismiss();
                Toast.makeText(this, "Login Failed. Please Re-try", Toast.LENGTH_LONG).show();

            }
        }
        else if(requestCode==FB_SIGN_IN)
        {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Google", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
//        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fbauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d("Google", "signInWithCredential:onComplete:" + task.isSuccessful());
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
                        final DatabaseReference databaseReference= firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());


                      databaseReference.child("name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    databaseReference.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    task.getResult();
                                    Toast.makeText(LoginActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Google", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}

