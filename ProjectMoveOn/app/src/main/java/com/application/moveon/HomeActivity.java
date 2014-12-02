package com.application.moveon;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.application.moveon.session.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.LocationListener;

import java.io.IOException;
import java.util.List;


public class HomeActivity extends FragmentActivity implements LocationListener {


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    private GoogleMap map;
    private MarkerOptions markerOptions;
    private LatLng locationMap;

    private Double finalLatitude = 0.0;
    private Double finalLongitude = 0.0;
    private String finalAdressString = "";

    private Button btn_find;

    private Address currentAddress;
    private List<Address> addresses;

    private ToolBox tools;

    private SupportMapFragment supportMapFragment;

    private int cursor;
    private CircleOptions circle;
    private int radius;

    private Location myLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button editProfilButton = (Button) findViewById(R.id.buttonEditProfil);
        editProfilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ProfilActivity.class);
                startActivity(i);
            }
        });

        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

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

        radius = 10;

        // Recuperer le fragment de la map
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Recuperer la map
        map = supportMapFragment.getMap();

        OnClickListener findClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                cursor = 0;
                map.clear();

                EditText etLocation = (EditText) findViewById(R.id.et_location);

                // Recuperer l'adresse demandee
                String location = etLocation.getText().toString();

                if (location != null && !location.equals("")) {
                    new LocateTask().execute(location);
                }
            }
        };

        btn_find = (Button) findViewById(R.id.btn_find);
        btn_find.setOnClickListener(findClickListener);

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        while(myLocation==null)
            myLocation = locationManager.getLastKnownLocation(provider);

        if (myLocation != null) {
            onLocationChanged(myLocation);
        }

        locationManager.requestLocationUpdates(provider, 20000, 0, this);

        // Location myLocation = map.getMyLocation();
        LatLng myLocationLatlng = new LatLng(myLocation.getLatitude(),
                myLocation.getLongitude());

        markerOptions = new MarkerOptions();
        markerOptions.position(myLocationLatlng);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        markerOptions.icon(bitmapDescriptor);

        map.addMarker(markerOptions);
        map.animateCamera(CameraUpdateFactory.newLatLng(myLocationLatlng));

        circle = new CircleOptions();

        // 55 represents percentage of transparency. For 100% transparency,
        // specify 00.
        // For 0% transparency ( ie, opaque ) , specify ff
        // The remaining 6 characters(00ff00) specify the fill color
        circle.fillColor(0x5500ff00);
        circle.strokeWidth(2);
        circle.center(myLocationLatlng);
        circle.radius(radius*1000);

        map.addCircle(circle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        int id = item.getItemId();
        if (id == R.id.action_disconnect) {
            SessionManager session = new SessionManager(this);
            session.logoutUser();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_settings) {
        }
        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        setTitle(mPlanetTitles[position]);
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

    private class LocateTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {

            Geocoder geocoder = new Geocoder(getBaseContext());
            addresses = null;

            try {
                // Recuperer 5 adresses possibles
                addresses = geocoder.getFromLocationName(locationName[0], 5);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getBaseContext(), "Pas d'adresse trouvée",
                        Toast.LENGTH_SHORT).show();
            }

            // Faire disparaitre les marqueurs de la map
            map.clear();

            // Ajouter un nouveau marqueur par adresse
            for (int i = 0; i < addresses.size(); i++) {

                currentAddress = (Address) addresses.get(i);

                locationMap = new LatLng(currentAddress.getLatitude(),
                        currentAddress.getLongitude());

                finalAdressString = String
                        .format("%s, %s",
                                currentAddress.getMaxAddressLineIndex() > 0 ? currentAddress
                                        .getAddressLine(0) : "", currentAddress
                                        .getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(locationMap);
                markerOptions.title(finalAdressString);

                map.addMarker(markerOptions);

                // Deplacer la map a la premiere adresse
                if (i == 0)
                    map.animateCamera(CameraUpdateFactory
                            .newLatLng(locationMap));
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        myLocation = location;

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        // map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

//		CircleOptions c = new CircleOptions();
//		c.center(latLng);
//		// 55 represents percentage of transparency. For 100% transparency,
//		// specify 00.
//		// For 0% transparency ( ie, opaque ) , specify ff
//		// The remaining 6 characters(00ff00) specify the fill color
//		c.fillColor(0x5500ff00);
//		c.strokeWidth(2);
//		c.radius(radius*1000);
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//		map.addCircle(c);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

}
