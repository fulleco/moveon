package com.application.moveon;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.application.moveon.cercle.FragmentCreateCercle;
import com.application.moveon.cercle.FragmentPickFriends;
import com.application.moveon.friends.FragmentFriendDemands;
import com.application.moveon.friends.FragmentFriends;
import com.application.moveon.ftp.FtpDownloadTask;
import com.application.moveon.map.FragmentLocationChooser;
import com.application.moveon.map.FragmentMap;
import com.application.moveon.menu.FragmentSettings;
import com.application.moveon.profil.FragmentEditProfil;
import com.application.moveon.profil.FragmentViewProfil;
import com.application.moveon.provider.UpdaterService;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.application.moveon.tools.ImageHelper;
import com.application.moveon.tools.ToolBox;
import com.google.android.gms.maps.model.Marker;
import com.heinrichreimersoftware.materialdrawer.DrawerFrameLayout;
import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerHeaderItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends FragmentActivity {

    private DrawerFrameLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mDrawerArray;
    private Calendar c = Calendar.getInstance();
    private MoveOnDB db;
    private DrawerView drawer;
    private Target target;



    private FragmentMap fragmentMap;
    private FragmentViewProfil fragmentViewProfil;
    private FragmentEditProfil fragmentEditProfil;
    private FragmentLocationChooser fragmentLocationChooser;
    private FragmentCreateCercle fragmentCreateCercle;
    private FragmentFriends fragmentFriends;
    private FragmentFriendDemands fragmentFriendDemands;
    private FragmentSettings fragmentSettings;
    private FragmentPickFriends fragmentPickFriends;


    private Fragment currentFragment;

    private FragmentManager fragmentManager;

    private ToolBox tools;

    private static final int MAP_INDEX = 0;
    private static final int VIEW_PROFIL_INDEX = 1;
    private static final int CREATE_CERCLE_INDEX = 2;
    private static final int FRIENDS = 3;
    private static final int DEMANDS = 4;
    private static final int SETTINGS = 5;

    private int RESULT_LOAD_IMAGE = 0;

    private Bitmap profilePicture;


    private SessionManager session;

    private CerclePojo currentCercle;

    public CerclePojo getCurrentCercle() {
        return currentCercle;
    }

    public void setCurrentCercle(CerclePojo currentCercle) {
        this.currentCercle = currentCercle;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    public FragmentViewProfil getFragmentViewProfil(){
        return fragmentViewProfil;
    }
    public FragmentFriends getFragmentFriends() {
        return fragmentFriends;
    }

    public void setFragmentEditProfil(FragmentFriends fragmentFriends) {
        this.fragmentFriends = fragmentFriends;
    }

    public FragmentEditProfil getFragmentEditProfil() {
        return fragmentEditProfil;
    }

    public void setFragmentEditProfil(FragmentEditProfil fragmentEditProfil) {
        this.fragmentEditProfil = fragmentEditProfil;
    }

    public FragmentLocationChooser getFragmentLocationChooser() {
        return fragmentLocationChooser;
    }

    public void setFragmentLocationChooser(FragmentLocationChooser fragmentLocationChooser) {
        this.fragmentLocationChooser = fragmentLocationChooser;
    }

    public Bitmap getProfilePicture(){
        File cacheDir = getBaseContext().getCacheDir();
        if(profilePicture == null){
            Bitmap bitmapDownloaded = ToolBox.decodeSampledBitmapFromResource(
                    cacheDir.getAbsolutePath() + "/profile.jpg", 100, 100);
            this.profilePicture = bitmapDownloaded;
        }
        return this.profilePicture;
    }

    public void setProfilePicture(Bitmap profilePicture){
        this.profilePicture = profilePicture;
    }

    private int mInterval = 10000;
    private Handler mHandler;
    private PendingIntent piUI;
    private AlarmManager amUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragmentViewProfil = new FragmentViewProfil();
        fragmentEditProfil = new FragmentEditProfil();
        fragmentLocationChooser = new FragmentLocationChooser();
        fragmentCreateCercle = new FragmentCreateCercle();
        fragmentFriends = new FragmentFriends();
        fragmentFriendDemands = new FragmentFriendDemands();
        fragmentSettings = new FragmentSettings();
        fragmentPickFriends = new FragmentPickFriends();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/BebasNeue.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        session = new SessionManager(this);
        mDrawerLayout = (DrawerFrameLayout) findViewById(R.id.drawer_layout);

        final DrawerProfile profile = new DrawerProfile()
                .setBackground(getResources().getDrawable(R.drawable.background))
                .setName(session.getUserDetails().get(SessionManager.KEY_EMAIL))
                .setDescription(session.getUserDetails().get(SessionManager.KEY_FIRSTNAME) + " " + session.getUserDetails().get(SessionManager.KEY_LASTNAME))
                .setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
                    @Override
                    public void onClick(DrawerProfile drawerProfile) {
                        mDrawerLayout.closeDrawer();
                        switchFragment(fragmentViewProfil);
                        setTitle("Profil");
                        mDrawerLayout.selectItem(0);
                    }
                });
        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.i("LOADPHOTO", "CHARGE");
                profile.setAvatar(new BitmapDrawable(bitmap));
                mDrawerLayout.setProfile(profile);
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {
                Log.i("LOADPHOTO", "PAS CHARGE");

            }

            @Override
            public void onPrepareLoad(Drawable drawable) {
                Log.i("LOADPHOTO", "Onprepare");
            }
        };


        String image = "http://martinezhugo.com/pfe/images/"+ session.getUserDetails().get(SessionManager.KEY_ID)+"/profile.jpg";
        Picasso.with(this).load(image).resize(100, 100).into(target);

        tools = new com.application.moveon.tools.ToolBox(this);

        HashMap<String, String> userInfos = session.getUserDetails();
        String idUser = userInfos.get(SessionManager.KEY_ID);

        File cacheDir = getBaseContext().getCacheDir();

        new FtpDownloadTask("www/pfe/images/"+idUser+"/profile.jpg",
                cacheDir.getAbsolutePath() + "/profile.jpg", profilePicture).execute();

        updateCurrentCercle();

        //// Drawer declaration


        mTitle = mDrawerTitle = getTitle();



        // set up the drawer's list view with items and click listener



        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        fragmentMap = new FragmentMap();

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
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(15)
                .oval(false)
                .build();



        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark_material_dark));
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setProfile(profile);

        mDrawerLayout.addItem(new DrawerHeaderItem()
                .setTitle("Cercles"));

        mDrawerLayout.addItem(new DrawerItem()
                .setTextPrimary("Carte")
                .setImage(getResources().getDrawable(R.drawable.map_icon))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, int i, int i2) {
                        mDrawerLayout.selectItem(i2);
                        mDrawerLayout.closeDrawer();
                        switchFragment(fragmentMap);
                        setTitle("Carte");
                    }
                }));

        mDrawerLayout.addItem(new DrawerItem()
                .setTextPrimary("Création de cercle")
                .setImage(getResources().getDrawable(R.drawable.cercle_icon))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, int i, int i2) {
                        mDrawerLayout.selectItem(i2);
                        mDrawerLayout.closeDrawer();
                        switchFragment(fragmentCreateCercle);
                        setTitle("Création de cercle");
                    }
                }));

        mDrawerLayout.addItem(new DrawerHeaderItem()
                .setTitle("Amis"));
        mDrawerLayout.addItem(new DrawerItem()
                .setTextPrimary("Mes amis")
                .setImage(getResources().getDrawable(R.drawable.friend_icon))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, int i, int i2) {
                        mDrawerLayout.selectItem(i2);
                        mDrawerLayout.closeDrawer();
                        switchFragment(fragmentFriends);
                        setTitle("Mes amis");

                    }
                }));

        mDrawerLayout.addItem(new DrawerItem()
        .setTextPrimary("Mes demandes d'amis")
         .setImage(getResources().getDrawable(R.drawable.dem_icon))
        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem drawerItem, int i, int i2) {
                mDrawerLayout.selectItem(i2);
                mDrawerLayout.closeDrawer();
                switchFragment(fragmentFriendDemands);
                setTitle("Mes demandes d'amis");
            }
        }));
        mDrawerLayout.addDivider();
        mDrawerLayout.addItem(new DrawerItem()
                .setTextPrimary("Paramètres")
                .setImage(getResources().getDrawable(R.drawable.setting_icon))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, int i, int i2) {
                        mDrawerLayout.selectItem(i2);
                        mDrawerLayout.closeDrawer();
                        switchFragment(fragmentSettings);
                        setTitle("Paramètres");
                    }
                }));

        mDrawerLayout.setDrawerMaxWidth(800);
        //// End Drawer declaration

        fragmentPickFriends.setTargetFragment(fragmentCreateCercle,10);


        setFragmentMap(new FragmentMap());
        fragmentLocationChooser = new FragmentLocationChooser();
        fragmentLocationChooser.setTargetFragment(getFragmentCreateCercle(),1);
        fragmentManager = getFragmentManager();



        fragmentManager.beginTransaction()
                .add(R.id.content_frame, getFragmentMap())
                .commit();
        currentFragment = getFragmentMap();
        mDrawerLayout.selectItem(1);
        setTitle("Carte");
    }

    public void initCurrentCercle() {

        MoveOnDB moveOnDB = new MoveOnDB(getBaseContext(), session.getUserDetails().get(SessionManager.KEY_EMAIL));
        moveOnDB.open();
        ArrayList<CerclePojo> cercles = moveOnDB.getCircles();
        moveOnDB.close();

        if(cercles.size()==0)
        {
            currentCercle=null;
            return;
        }


        currentCercle = cercles.get(0);
        currentCercle.setAllInfo(session, getBaseContext());

    }

    public void updateCurrentCercle()
    {

        if(currentCercle==null)
        {
            initCurrentCercle();
            return;
        }

        MoveOnDB moveOnDB = new MoveOnDB(getBaseContext(), session.getUserDetails().get(SessionManager.KEY_EMAIL));
        moveOnDB.open();
        currentCercle = moveOnDB.getCircle(String.valueOf(currentCercle.getId_cercle()));
        moveOnDB.close();

        if(currentCercle==null)
            return;

        currentCercle.setAllInfo(session, getBaseContext());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intentUpdate = new Intent(
                this,
                UpdaterService.class);
        changeNotificationFrequency();
        mHandler = new Handler();
        amUI = (AlarmManager) getSystemService(ALARM_SERVICE);
        piUI = PendingIntent.getService(this, 0, intentUpdate, 0);
        amUI.cancel(piUI);
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void startUpdateUI(){
        int secondUpdateUI = 10;

        amUI.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + secondUpdateUI * 1000,
                secondUpdateUI * 1000, piUI);
        startRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            updateCurrentCercle();
            fragmentMap.initCercle();
            fragmentMap.updateSlidingUpDrawer();
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    private boolean updateActivated = false;
    public void startRepeatingTask() {
        if(!updateActivated){
            updateActivated = true;
            mStatusChecker.run();
        }
    }

    public void stopRepeatingTask() {
        if(updateActivated){
            mHandler.removeCallbacks(mStatusChecker);
            amUI.cancel(piUI);
            updateActivated = false;
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        session.checkLogin(false);
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    /* Called whenever we call invalidateOptionsMenu() */


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
                setTitle("Paramètres");
                switchFragment(fragmentSettings);
                mDrawerLayout.selectItem(7);
                mDrawerLayout.closeDrawer();
                return true;
            case R.id.action_add_friend:
                setTitle("Amis");
                switchFragment(fragmentFriends);
                mDrawerLayout.selectItem(4);
                mDrawerLayout.closeDrawer();
                return true;
            case R.id.action_create_circle:
                setTitle("Cercle");
                switchFragment(fragmentCreateCercle);
                mDrawerLayout.selectItem(2);
                mDrawerLayout.closeDrawer();

                return true;
            case R.id.action_map:
                setTitle("Carte");
                switchFragment(fragmentMap);
                mDrawerLayout.selectItem(1);
                mDrawerLayout.closeDrawer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public FragmentFriendDemands getFragmentFriendDemands() {
        return fragmentFriendDemands;
    }

    public void setFragmentFriendDemands(FragmentFriendDemands fragmentFriendDemands) {
        this.fragmentFriendDemands = fragmentFriendDemands;
    }

    public FragmentMap getFragmentMap() {
        return fragmentMap;
    }

    public void setFragmentMap(FragmentMap fragmentMap) {
        this.fragmentMap = fragmentMap;
    }

    public FragmentCreateCercle getFragmentCreateCercle() {
        return fragmentCreateCercle;
    }

    public void setFragmentCreateCercle(FragmentCreateCercle fragmentCreateCercle) {
        this.fragmentCreateCercle = fragmentCreateCercle;
    }

    public FragmentPickFriends getFragmentPickFriends() {
        return fragmentPickFriends;
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
                switchFragment(getFragmentMap());
                break;

            case VIEW_PROFIL_INDEX :
                switchFragment(fragmentViewProfil);
                break;

            case CREATE_CERCLE_INDEX :
                switchFragment(getFragmentCreateCercle());
                break;

            case FRIENDS :
                switchFragment(fragmentFriends);
                break;
            case DEMANDS :
                switchFragment(fragmentFriendDemands);
                break;
            case SETTINGS :
                switchFragment(fragmentSettings);
                break;

            default :
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .hide(getFragmentMap())
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

    public void switchFragment(Fragment newFragment)
    {
        if(currentFragment.getClass() == newFragment.getClass())
            return;

        if(currentFragment.getClass() == getFragmentMap().getClass()) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .hide(currentFragment)
                    .add(R.id.content_frame, newFragment)
                    .commit();
        }
        else {
            if(newFragment.getClass() == getFragmentMap().getClass())
            {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .remove(currentFragment)
                        .show(getFragmentMap())
                        .commit();
            }
            else {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .remove(currentFragment)
                        .add(R.id.content_frame, newFragment)
                        .commit();
            }
        }

        currentFragment = newFragment;

    }

    public void dateOnClick(final View textField){

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){
            @Override
            public  void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH,monthOfYear);
                c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                tools.setCurrentDateOnView((EditText)textField,c);
            }
        };

        new DatePickerDialog(HomeActivity.this,date,
                c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void timeOnClick(final View textField){
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener(){
            @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE,minute);
                tools.setCurrentTimeOnView((EditText)textField,c);
            }
        };
                new TimePickerDialog(HomeActivity.this, time, c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
    }

    public void changeNotificationFrequency()
    {
        String sync_key_value = getResources().getString(R.string.pref_sync_key);
        boolean isNotifOn = session.getPref().getBoolean(sync_key_value, true);

        if(!isNotifOn)
            return;

        String freq_key_value = getResources().getString(R.string.pref_freq_key);
        int second = Integer.valueOf(session.getPref().getString(freq_key_value, "15"));

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intentNotifications = new Intent(
                this,
                com.application.moveon.provider.ProviderService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, intentNotifications, 0);
        am.cancel(pi);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + second * 1000,
                second * 1000, pi);
    }

    public void stopNotification()
    {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intentNotifications = new Intent(
                this,
                com.application.moveon.provider.ProviderService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, intentNotifications, 0);
        am.cancel(pi);
    }

    public void startNotification()
    {

        String key_value = getResources().getString(R.string.pref_freq_key);
        int second = Integer.valueOf(session.getPref().getString(key_value, "15"));

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intentNotifications = new Intent(
                this,
                com.application.moveon.provider.ProviderService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, intentNotifications, 0);
        am.cancel(pi);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + second * 1000,
                second * 1000, pi);
    }
}
