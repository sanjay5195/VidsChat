package com.sanju.chat.vidschat.Activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sanju.chat.vidschat.Adapters.PagerAdapter;
import com.sanju.chat.vidschat.R;
import com.sanju.chat.vidschat.Utiles.AppController;
import com.sanju.chat.vidschat.Utiles.RoundedNetworkImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    NetworkImageView ntwk_img_view;
    RoundedNetworkImageView rounded_img_view_profile;
    TextView txt_view_name;
    TextView txt_view_name2;
    ImageButton img_btn_profile_edit;
    TextView tv_image_name;
    TextView textEmail;
    public GoogleApiClient mGoogleApiClient;
    String providerId = "";
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ImageLoader imageLoader;

    SearchView searchView;

    ViewPager viewPager;
    TabLayout tabLayout;

    String[] tabs = { "Chat", "Contact"};

    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    private Bitmap thumbnail;
    private String selectedImagePath;
    BitmapFactory.Options options;

    StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageLoader = AppController.getInstance().getImageLoader();
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://vidschat-8f86b.appspot.com/");

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0)
                    setTitle(tabs[0]);
                else if (position == 1)
                    setTitle(tabs[1]);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hiii Mr.Sanjay...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        rounded_img_view_profile = (RoundedNetworkImageView) findViewById(R.id.rounded_img_view_profile);
        txt_view_name = (TextView) findViewById(R.id.txt_view_name);
        txt_view_name2 = (TextView) findViewById(R.id.txt_view_name2);
        img_btn_profile_edit = (ImageButton) findViewById(R.id.img_btn_profile_edit);

        img_btn_profile_edit.setOnClickListener(this);
        rounded_img_view_profile.setOnClickListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.inflateHeaderView(R.layout.nav_header_home);

        ntwk_img_view = (NetworkImageView) view.findViewById(R.id.ntwk_img_view);
        tv_image_name = (TextView) view.findViewById(R.id.tv_image_name);
        //tv_image_name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        textEmail= (TextView) view.findViewById(R.id.textEmail);
        //textEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

//        ntwk_img_view.setImageUrl(url, AppController.getInstance().getImageLoader());




        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            for (UserInfo profile : firebaseUser.getProviderData()) {

                if (profile.getProviderId() != null){
                    providerId = profile.getProviderId();
                }
                if (profile.getPhotoUrl() != null){
                    ntwk_img_view.setImageUrl(profile.getPhotoUrl().toString(), imageLoader);
                    rounded_img_view_profile.setImageUrl(profile.getPhotoUrl().toString(), imageLoader);
                }else {
                    ntwk_img_view.setDefaultImageResId(R.drawable.default_male);
                    rounded_img_view_profile.setDefaultImageResId(R.drawable.default_male);
                }
            }
        }else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        Query query = databaseReference;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    DataSnapshot snapshot = (DataSnapshot) iterator.next();
                    if (snapshot.getKey().equals("name")){
                        tv_image_name.setText(snapshot.getValue().toString());
                        txt_view_name.setText(snapshot.getValue().toString());
                    }else if (snapshot.getKey().equals("email")){
                        textEmail.setText(snapshot.getValue().toString());
                        txt_view_name2.setText(snapshot.getValue().toString());
                    }else if (snapshot.getKey().equals("photoUrl")){
                        ntwk_img_view.setImageUrl(snapshot.getValue().toString(), imageLoader);
                        rounded_img_view_profile.setImageUrl(snapshot.getValue().toString(), imageLoader);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(onQueryTextListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_profile_update) {

            startActivity(new Intent(this, UserProfileUpdateActivity.class));

        }else if (id == R.id.nav_log_out) {

            signOut();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            Intent intent = new Intent(HomeActivity.this, SearchResultActivity.class);
            intent.putExtra("query", query);
            startActivity(intent);
            HomeActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    public void signOut(){

        final Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        if (providerId.equals("google.com")){
            //Toast.makeText(HomeActivity.this, "Google", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    startActivity(intent);
                    finish();
                }
            });

        }else if (providerId.equals("facebook.com") && AccessToken.getCurrentAccessToken()!= null){
            //Toast.makeText(HomeActivity.this, "Facebook", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            startActivity(intent);
            finish();
        }else {
            //Toast.makeText(HomeActivity.this, "Email", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_btn_profile_edit:
                startActivity(new Intent(this, UserProfileUpdateActivity.class));
                break;
            case R.id.rounded_img_view_profile:
                selectImage();
                break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 90, bytes);
//                byte[] image = bytes.toByteArray(); // For the Upload to Parse Cloaud
//                user.put("profile_image", image);
                File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".PNG");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //rounded_img_view_profile.setImageBitmap(thumbnail);


                if(firebaseUser.getUid() != null && thumbnail != null){
                    // For Profile Image


                    Bitmap bitmap = thumbnail;

                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
                    uploadProfileImage(Uri.parse(path));

                } else {

                    //dialog.dismiss();
                }

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA };
                Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                selectedImagePath = cursor.getString(column_index);
                //Bitmap bm;
                options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                thumbnail = BitmapFactory.decodeFile(selectedImagePath, options);

                if(firebaseUser.getUid() != null && thumbnail != null){

                    Bitmap bitmap = thumbnail;
                    //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    //byte[] imageInByte = stream.toByteArray();

                    //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
                    uploadProfileImage(Uri.parse(path));

                } else {

                    //dialog.dismiss();
                }
            }
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {

                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                        }
                    }else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                } else if (items[item].equals("Choose from Library")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_FILE);
                        }
                    }else {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }else if (requestCode == SELECT_FILE){

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            }else {

            }
        }
    }

    public void uploadProfileImage(Uri file){

        //rlayout_progress_bar.setVisibility(View.VISIBLE);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        StorageReference storageReference = storageRef.child(firebaseUser.getUid()).child("profileImage");

        UploadTask uploadTask = storageReference.child("image_dp.jpg").putFile(file, metadata);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
                int prog = (int) progress;
                //progressBar.setProgress(prog);
                //txtProgress.setText(prog + " %");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //rlayout_progress_bar.setVisibility(View.INVISIBLE);
                //System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

//                dialog.dismiss();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //dialog.dismiss();

                databaseReference.child("photoUrl").setValue(taskSnapshot.getMetadata().getDownloadUrl().toString());

            }
        });
    }

}
