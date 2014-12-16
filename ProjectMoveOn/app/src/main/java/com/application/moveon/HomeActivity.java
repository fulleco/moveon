package com.application.moveon;

import android.app.Fragment;
import android.content.Intent;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.application.moveon.map.FragmentMap;
import com.application.moveon.profil.FragmentViewProfil;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.*;
import com.application.moveon.tools.ToolBox;

public class HomeActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mDrawerArray;

    private FragmentMap fragmentMap = new FragmentMap();
    private FragmentViewProfil fragmentViewProfil = new FragmentViewProfil();
    private Fragment lastFragment;

    private FragmentManager fragmentManager;

    private ToolBox tools;

    private static final int MAP_INDEX = 0;
    private static final int VIEW_PROFIL_INDEX = 1;

    private int RESULT_LOAD_IMAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tools = new com.application.moveon.tools.ToolBox(this);

        //// Drawer declaration
        mTitle = mDrawerTitle = getTitle();
        mDrawerArray = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerArray));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //// End Drawer declaration

        fragmentMap = new FragmentMap();
        fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.content_frame, fragmentMap)
                .commit();
        lastFragment = fragmentMap;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {

            case R.id.action_disconnect:
                SessionManager session = new SessionManager(this);
                session.logoutUser();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                return true;
            case R.id.action_settings:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            fragmentViewProfil.setPicturePath(cursor.getString(columnIndex));
            String extension = fragmentViewProfil.getPicturePath().substring(fragmentViewProfil.getPicturePath().lastIndexOf("."));
            fragmentViewProfil.setNamePicture(tools.getFileName(selectedImage) + extension);
            cursor.close();

            BitmapFactory.Options options=new BitmapFactory.Options();
            options.outHeight = 8;
            //mainPicture.setImageBitmap(BitmapFactory.decodeFile(picturePath, options));

            Bitmap b_gallery = tools.decodeSampledBitmapFromResource(fragmentViewProfil.getPicturePath(), 60, 60);
            Bitmap b_rounded = ImageHelper.getRoundedCornerBitmap(b_gallery, 15, 0);

            fragmentViewProfil.getProfilePicture().setBackground(null);
            fragmentViewProfil.getProfilePicture().setImageBitmap(b_rounded);
        }

    }

    private void selectItem(int position) {
        setTitle(mDrawerArray[position]);
        switch (position){
            case MAP_INDEX :
                if(lastFragment.getClass() == fragmentMap.getClass())
                    break;

                // Insert the fragment by replacing any existing fragment
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .remove(lastFragment)
                        .show(fragmentMap)
                        .commit();

                lastFragment = fragmentMap;


                break;

            case VIEW_PROFIL_INDEX :
                //
                if(lastFragment.getClass() == fragmentViewProfil.getClass())
                    break;

                if(lastFragment.getClass() != fragmentMap.getClass()) {
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                            .remove(lastFragment)
                            .add(R.id.content_frame, fragmentViewProfil)
                            .commit();
                }
                else
                {
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .hide(fragmentMap)
                    .add(R.id.content_frame, fragmentViewProfil)
                    .commit();
                }
                lastFragment = fragmentViewProfil;
                break;

            default :
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .hide(fragmentMap)
                        .commit();
                break;

        }

        mDrawerLayout.closeDrawer(mDrawerList);
        return;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
