package com.application.moveon.map;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.moveon.ProfilActivity;
import com.application.moveon.R;
import com.application.moveon.tools.ImageHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuItem;
import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuWidget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by damota on 10/12/2014.
 */
public class FragmentMap extends Fragment implements LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private MarkerOptions markerOptions;
    private LatLng locationMap;

    private Double finalLatitude = 0.0;
    private Double finalLongitude = 0.0;
    private String finalAdressString = "";

    private Button btn_find;

    private Address currentAddress;
    private List<Address> addresses;

    private SupportMapFragment supportMapFragment;

    private int cursor;
    private CircleOptions circle;
    private int radius;

    private Location myLocation = null;

    private RadialMenuWidget pieMenu;
    public RadialMenuItem menuItem, menuCloseItem, menuExpandItem, menuCacaItem;
    public RadialMenuItem firstChildItem, secondChildItem, thirdChildItem;
    private List<RadialMenuItem> children = new ArrayList<RadialMenuItem>();

    private FrameLayout containerMenu;

    private View fMap;

    private FragmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        containerMenu = (FrameLayout) view.findViewById(R.id.containerMenu);

        radius = 10;

        activity = (FragmentActivity)getActivity();

        // Recuperer le fragment de la map
        supportMapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Recuperer la map
        map = supportMapFragment.getMap();

        View.OnClickListener findClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cursor = 0;
                map.clear();

                EditText etLocation = (EditText) v.findViewById(R.id.et_location);

                // Recuperer l'adresse demandee
                String location = etLocation.getText().toString();

                if (location != null && !location.equals("")) {
                    new LocateTask().execute(location);
                }
            }
        };

        btn_find = (Button) view.findViewById(R.id.btn_find);
        btn_find.setOnClickListener(findClickListener);

        initMap();
        fMap = (View)view.findViewById(R.id.map);

        initMenu();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initMenu(){
        pieMenu = new RadialMenuWidget(activity);
        menuCloseItem = new RadialMenuItem("close", null);
        menuCloseItem
                .setDisplayIcon(android.R.drawable.ic_menu_close_clear_cancel);
        menuItem = new RadialMenuItem("normal","normal");
        menuItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                dismissMenu();
            }
        });

        firstChildItem = new RadialMenuItem("normal","normal");
        firstChildItem
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.;
                        dismissMenu();
                    }
                });

        secondChildItem = new RadialMenuItem("contact","contact");
        secondChildItem.setDisplayIcon(R.drawable.ic_launcher);
        secondChildItem
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu();
                    }
                });

        thirdChildItem = new RadialMenuItem("about", "about");
        thirdChildItem.setDisplayIcon(R.drawable.ic_launcher);
        thirdChildItem
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        dismissMenu();
                    }
                });

        menuExpandItem = new RadialMenuItem("caca", "caca");
        children.add(firstChildItem);
        children.add(secondChildItem);
        children.add(thirdChildItem);
        menuExpandItem.setMenuChildren(children);

        menuCacaItem = new RadialMenuItem("caca", "caca");
        menuCacaItem.setMenuChildren(children);

        menuCloseItem
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // menuLayout.removeAllsViews();
                        dismissMenu();
                    }
                });

        // pieMenu.setDismissOnOutsideClick(true, menuLayout);
        //pieMenu.setAnimationSpeed(1000L);
        pieMenu.setTextColor(Color.BLACK, 1000);
        //pieMenu.setSourceLocation(30, 60);
        pieMenu.setIconSize(15, 30);
        pieMenu.setTextSize(15);
        pieMenu.setOutlineColor(Color.BLACK, 225);
        pieMenu.setInnerRingColor(Color.RED, 220);
        pieMenu.setOuterRingColor(Color.RED, 220);
        pieMenu.setHeader("Anthony Da Mota", 20);
        pieMenu.setCenterCircle(menuCloseItem);
        pieMenu.addMenuEntry(new ArrayList<RadialMenuItem>() {
            {
                add(menuItem);
                add(menuExpandItem);
                add(menuCacaItem);
                add(menuCacaItem);
            }
        });
        //pieMenu.setVisibility(View.GONE);
        //pieMenu.show(containerMenu);
    }

    private void initMap(){
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

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
        //BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
        //        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        //markerOptions.icon(bitmapDescriptor);

        Bitmap b = BitmapFactory.decodeResource(getResources(),
                R.drawable.profile_test);
        Bitmap b_rounded = ImageHelper.getRoundedCornerBitmap(b, 1000, 0);
        Bitmap b_resized = Bitmap.createScaledBitmap(b_rounded, 60, 60, false);
        b.recycle();

        View marker_layout = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView profilePicture = (ImageView) marker_layout.findViewById(R.id.profile_picture);
        profilePicture.setImageBitmap(b_resized);

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(ImageHelper.createDrawableFromView(activity, marker_layout)));


        //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(b_resized));
        // Specifies the anchor to be at a particular point in the marker image.
        //markerOptions.anchor(0.5f, 1);

        markerOptions.title("Anthony Da Mota");
        map.addMarker(markerOptions);
        map.animateCamera(CameraUpdateFactory.newLatLng(myLocationLatlng));

        // TEST
        LatLng myLocationLatlng2 = new LatLng(myLocation.getLatitude()+1,
                myLocation.getLongitude()+5);

        markerOptions.position(myLocationLatlng2);
        markerOptions.title("Barrack Obama");

        map.addMarker(markerOptions);

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
        map.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //if(marker.getTitle().equals("MyHome")) // if marker source is clicked
        //    Toast.makeText(HomeActivity.this, marker.getTitle(),Toast.LENGTH_LONG).show();// display toast
        map.animateCamera(CameraUpdateFactory
                .newLatLng(marker.getPosition()),400,new GoogleMap.CancelableCallback()
        {
            @Override
            public void onFinish()
            {
                map.getUiSettings().setScrollGesturesEnabled(true);
                showMenu();
            }

            @Override
            public void onCancel()
            {
                map.getUiSettings().setAllGesturesEnabled(true);
            }
        });

        return true;
    }

    private void showMenu(){
        //pieMenu.setVisibility(View.GONE);
        pieMenu.setCenterLocation((int)fMap.getX()+ fMap.getWidth()/2, (int)fMap.getY()+ fMap.getHeight()/2);
        pieMenu.show(containerMenu);

        displayMenuAnimation(0, 1, View.VISIBLE);
        //pieMenu.show(containerMenu);
        //map.getUiSettings().setScrollGesturesEnabled(false);
    }

    private void displayMenuAnimation(int alpha1, int alpha2,
                                      final int visibility) {

        AlphaAnimation fadeAnimation = new AlphaAnimation(alpha1, alpha2); // start
        // alpha,
        // end
        // alpha
        fadeAnimation.setDuration(700); // time for animation in
        // milliseconds
        fadeAnimation.setFillAfter(false); // make the transformation persist
        fadeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                pieMenu.setVisibility(visibility);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });

        pieMenu.setAnimation(fadeAnimation);
    }

    private void dismissMenu() {
        displayMenuAnimation(1, 0, View.GONE);
        pieMenu.dismiss();
        pieMenu.setSelected(false);
        //pieMenu.invalidate();
        //map.getUiSettings().setScrollGesturesEnabled(true);
    }





    private class LocateTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {

            Geocoder geocoder = new Geocoder(activity.getBaseContext());
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
                Toast.makeText(activity.getBaseContext(), "Pas d'adresse trouvée",
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