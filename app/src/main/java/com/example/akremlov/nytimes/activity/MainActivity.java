package com.example.akremlov.nytimes.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akremlov.nytimes.R;
import com.example.akremlov.nytimes.content.DrawerItem;
import com.example.akremlov.nytimes.content.NYCategoriesAdapter;
import com.example.akremlov.nytimes.content.NYFragmentPagerAdapter;
import com.example.akremlov.nytimes.database.UserDb;
import com.example.akremlov.nytimes.utils.AssetsReader;
import com.example.akremlov.nytimes.utils.Constants;
import com.example.akremlov.nytimes.utils.InternetChangeReceiver;
import com.example.akremlov.nytimes.utils.NYSharedPreferences;
import com.example.akremlov.nytimes.utils.UsersContract;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NYCategoriesAdapter.ViewPagerCategoryListener {


    private File mPhotoFile;
    private ImageView mUserImage;
    private ViewPager mPager;
    private ArrayList<String> mQueries;
    private DrawerLayout mDrawer;
    private ListView mCategoriesList;
    private NYCategoriesAdapter mAdapter;
    private int mClickedPosition;
    private Bitmap imageBitmap;
    private final String TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (!InternetChangeReceiver.isNetworkAvailable()) {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mQueries = generateCategoryList();
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        TextView mUserName = (TextView) headerView.findViewById(R.id.textView);
        String username = NYSharedPreferences.getsInstance().getUsername();

        mUserName.setText(username);
        mUserImage = (ImageView) headerView.findViewById(R.id.imageView);

        tryToSetImageFromDb();

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
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(intent, Constants.SELECT_PICTURE);
                            }
                        })
                        .show();
            }
        });

        findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        PagerTabStrip tabStrip = (PagerTabStrip) mPager.findViewById(R.id.pagerTabStrip);
        tabStrip.setDrawFullUnderline(true);
        tabStrip.setTabIndicatorColor(ContextCompat.getColor(this, R.color.background_main));
        NYFragmentPagerAdapter fragmentPagerAdapter = new NYFragmentPagerAdapter(getSupportFragmentManager(), mQueries);
        mPager.setAdapter(fragmentPagerAdapter);
        mCategoriesList = (ListView) mDrawer.findViewById(R.id.categories_list);
        if (savedInstanceState != null) {
            mClickedPosition = savedInstanceState.getInt(Constants.CLICKED_POSITION);
        }
        List<DrawerItem> drawerItemList = generateDrawerList(mClickedPosition);
        mAdapter = new NYCategoriesAdapter(drawerItemList, this);
        mAdapter.setListener(this);
        mCategoriesList.setAdapter(mAdapter);
    }


    private List<DrawerItem> generateDrawerList(int clickedPosition) {
        List<DrawerItem> itemList = new ArrayList<>(mQueries.size());
        for (int i = 0; i < mQueries.size(); i++) {
            DrawerItem drawerItem = new DrawerItem(mQueries.get(i), i == clickedPosition);
            itemList.add(drawerItem);
        }
        return itemList;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.CATEGORIES_LIST_ON_SAVE, mCategoriesList.onSaveInstanceState());
        if (imageBitmap != null) {
            outState.putParcelable(Constants.USER_IMAGE, imageBitmap);
        }
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
        Bitmap tempBitmap = savedInstanceState.getParcelable(Constants.USER_IMAGE);
        if (tempBitmap != null) {
            imageBitmap = tempBitmap;
            mUserImage.setImageBitmap(tempBitmap);
        }
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
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                        if (inputStream != null) {
                            File savedImage = new File(getFilesDir(), new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()));
                            FileOutputStream outputStream = new FileOutputStream(savedImage);
                            outputStream.write(IOUtils.toByteArray(inputStream));
                            inputStream.close();
                            outputStream.close();
                            setPic(savedImage.getPath());
                            putImageToDB(savedImage.getPath());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Constants.CAPTURE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    setPic(mPhotoFile.getAbsolutePath());
                    putImageToDB(mPhotoFile.getAbsolutePath());
                }
                break;
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

    private void putImageToDB(final String path) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Intent intent = getIntent();
                String userName = intent.getStringExtra(Constants.USERNAME);
                ContentResolver resolver = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(UserDb.DBColumns.PATH_TO_IMAGE, path);
                String where = UserDb.DBColumns.USERNAME + "=?";
                resolver.update(UsersContract.TABLE_URI, values, where, new String[]{userName});
            }
        }).start();
    }

    private void setPic(String photoFilePath) {
        Picasso.with(this).load(new File(photoFilePath))
                .resize(Constants.BITMAP_WIDTH, Constants.BITMAP_HEIGHT).centerCrop().into(mUserImage);
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_.jpg";
        return new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            mPhotoFile = null;
            try {
                mPhotoFile = createImageFile();
            } catch (IOException ex) {
                Log.d(TAG, ex.toString());
            }
            if (mPhotoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, Constants.PHOTO_FILE_PROVIDER_AUTHORITY, mPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.CAPTURE_PICTURE);
            }
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

    private ArrayList<String> generateCategoryList() {
        ArrayList<String> arrayList = AssetsReader.readAssets();
        Iterator<String> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            String category = iterator.next();
            boolean isChecked = NYSharedPreferences.getsInstance().getCategoryPreference(category);
            if (!isChecked) {
                iterator.remove();
            }
        }
        return arrayList;
    }

    private void tryToSetImageFromDb() {

        Intent intent = getIntent();
        final String userName = intent.getStringExtra(Constants.USERNAME);
        getLoaderManager().initLoader(1, null, new android.app.LoaderManager.LoaderCallbacks<Cursor>() {

            @Override
            public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new android.content.CursorLoader(MainActivity.this, UsersContract.TABLE_URI, new String[]{UserDb.DBColumns.PATH_TO_IMAGE},
                        UserDb.DBColumns.USERNAME + "=?", new String[]{userName}, null);
            }

            @Override
            public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
                String pathToImage;
                if (cursor != null) {
                    cursor.moveToFirst();
                    pathToImage = cursor.getString(cursor.getColumnIndex(UserDb.DBColumns.PATH_TO_IMAGE));
                    if (!TextUtils.isEmpty(pathToImage)) {
                        setPic(pathToImage);
                    }
                }
            }

            @Override
            public void onLoaderReset(android.content.Loader<Cursor> loader) {

            }
        }).forceLoad();
    }

}
