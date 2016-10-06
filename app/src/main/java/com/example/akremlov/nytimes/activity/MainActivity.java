package com.example.akremlov.nytimes.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.akremlov.nytimes.R;
import com.example.akremlov.nytimes.content.DrawerItem;
import com.example.akremlov.nytimes.content.NYCategoriesAdapter;
import com.example.akremlov.nytimes.database.UserDb;
import com.example.akremlov.nytimes.fragment.NYFragment;
import com.example.akremlov.nytimes.utils.Constants;
import com.example.akremlov.nytimes.utils.LogInSharedPreferences;
import com.example.akremlov.nytimes.utils.UsersContract;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NYCategoriesAdapter.ViewPagerCategoryListener {


    private String mCurrentPhotoPath;
    private ImageView mUserImage;
    private ViewPager mPager;
    private LinkedList<String> mQueries;
    private DrawerLayout mDrawer;
    private ListView mCategoriesList;
    private NYCategoriesAdapter mAdapter;
    private int mClickedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        TextView mUserName = (TextView) headerView.findViewById(R.id.textView);
        mUserName.setText(LogInSharedPreferences.getUsername(this));
        mUserImage = (ImageView) headerView.findViewById(R.id.imageView);
        mUserImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.set_image_title)
                        .setMessage(R.string.set_image_text)
                        .setPositiveButton(R.string.take_new, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    dispatchTakePictureIntent();
                                } else {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.CAMERA},
                                            Constants.PERMISSION_REQUEST_CAMERA);
                                }
                            }
                        })
                        .setNegativeButton(R.string.gallery, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, Constants.SELECT_PICTURE);
                            }
                        })
                        .show();
            }
        });

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        PagerTabStrip tabStrip = (PagerTabStrip) mPager.findViewById(R.id.pagerTabStrip);
        tabStrip.setDrawFullUnderline(true);
        tabStrip.setTabIndicatorColor(ContextCompat.getColor(this, R.color.background_main));
        NYFragmentPagerAdapter fragmentPagerAdapter = new NYFragmentPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(fragmentPagerAdapter);

        mCategoriesList = (ListView) mDrawer.findViewById(R.id.categories_list);
        mCategoriesList.setDivider(null);
        mCategoriesList.setDividerHeight(0);
        if (savedInstanceState != null) {
            mClickedPosition = savedInstanceState.getInt(Constants.CLICKED_POSITION);
        }
        List<DrawerItem> drawerItemList = generateDrawerList(mClickedPosition);
        mAdapter = new NYCategoriesAdapter(drawerItemList, this);
        mAdapter.setListener(this);
        mCategoriesList.setAdapter(mAdapter);
    }

    private List<DrawerItem> generateDrawerList(int mClickedPosition) {
        List<DrawerItem> itemList = new ArrayList<>(mQueries.size());
        for (int i = 0; i < mQueries.size(); i++) {
            DrawerItem drawerItem = new DrawerItem(mQueries.get(i), i == mClickedPosition);
            itemList.add(drawerItem);
        }
        return itemList;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.CATEGORIES_LIST_ON_SAVE, mCategoriesList.onSaveInstanceState());
        if (mAdapter.getClickedPosition() == 0) {
            outState.putInt(Constants.CLICKED_POSITION, mClickedPosition);
        } else {
            outState.putInt(Constants.CLICKED_POSITION, mAdapter.getClickedPosition());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCategoriesList.onRestoreInstanceState(savedInstanceState.getParcelable(Constants.CATEGORIES_LIST_ON_SAVE));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.SELECT_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    Bitmap imageBitmap;
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        mUserImage.setImageBitmap(imageBitmap);
                        File f = new File(selectedImage.getPath());
                        putImageToDB(f.getAbsolutePath());
                        Toast.makeText(this, f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Constants.CAPTURE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    setPic();
                    putImageToDB(mCurrentPhotoPath);
                    Toast.makeText(this, mCurrentPhotoPath, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void putImageToDB(String path) {
        Intent intent = getIntent();
        String userName = intent.getStringExtra(getString(R.string.username));
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(UserDb.DBColumns.PATH_TO_IMAGE, path);
        String where = UserDb.DBColumns.USERNAME + " = " + userName;
        resolver.update(UsersContract.TABLE_URI, values, where, null);
    }

    private void setPic() {
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        mUserImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 500, 500, true));
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_.jpg";
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName);
        mCurrentPhotoPath = file.getAbsolutePath();
        return file;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.akremlov.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.CAPTURE_PICTURE);
            }
        }
    }

    private class NYFragmentPagerAdapter extends FragmentStatePagerAdapter {

        public NYFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            mQueries = readAssets();
        }

        @Override
        public Fragment getItem(int position) {
            return NYFragment.newInstance(mQueries.get(position));
        }

        @Override
        public int getCount() {
            return mQueries.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mQueries.get(position).trim();
        }

        public LinkedList<String> readAssets() {
            LinkedList<String> list = new LinkedList<>();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("articles.txt")));
                while (true) {
                    String articleName = bufferedReader.readLine();
                    if (articleName == null) {
                        break;
                    }
                    list.add(articleName.trim());
                }
                bufferedReader.close();
                return list;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new LinkedList<>();
        }
    }

    @Override
    public void setFocus(int position) {
        mPager.setCurrentItem(position);
        mDrawer.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void scrollTo(int position) {
        mCategoriesList.setSelection(position);
    }
}
