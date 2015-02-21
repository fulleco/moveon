package com.application.moveon.map;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.model.Cercle;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.AddFriend_Callback;
import com.application.moveon.rest.callback.AddMessage_Callback;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ImageHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.application.moveon.menu.v1.RadialMenuItem;
import com.application.moveon.menu.v1.RadialMenuWidget;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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
    public RadialMenuItem menuCloseItem, menuExpandInfo, menuExpandAlert, menuExpandQuestion, menuExpandSmiley;
    public RadialMenuItem childItemWhereAreYou, childItemHowAreYou, childItemIncoming, childItemLate,
                          childItemLost, childItemArrived, childItemLeaving, childItemJoinMe, childItemSos, childItemCigaret;
    private List<RadialMenuItem> childrenInfos = new ArrayList<RadialMenuItem>();
    private List<RadialMenuItem> childrenQuestions = new ArrayList<RadialMenuItem>();
    private List<RadialMenuItem> childrenAlert = new ArrayList<RadialMenuItem>();
    private List<RadialMenuItem> childrenSmiley = new ArrayList<RadialMenuItem>();

    private FrameLayout containerMenu;
    private RelativeLayout containerMap;

    private View fMap;

    private FragmentActivity activity;
    private static View view;

    private boolean canGetLocation;

    private SlidingUpPanelLayout mSlidingPanel;

    private ProgressDialog progressDialog;

    private MoveOnService mainmos;

    private SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_map, container, false);

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        containerMenu = (FrameLayout) view.findViewById(R.id.containerMenu);

        session = new SessionManager(getActivity());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);

        RestClient r = new RestClient(false);
        mainmos = (new RestClient(true)).getApiService();

        radius = 10;

        activity = (FragmentActivity)getActivity();

        // Recuperer le fragment de la map
        supportMapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Recuperer la map
        map = supportMapFragment.getMap();
        fMap = (View)view.findViewById(R.id.map);
        //new LoadMapTask().execute();

        mSlidingPanel = (SlidingUpPanelLayout) view
                .findViewById(R.id.sliding_layout);
        mSlidingPanel.setAnchorPoint(0.45f);

        // Cercle test
        initMap(null);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fMap.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                // gets called after layout has been done but before display.
                fMap.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                final int x = (int)fMap.getX()+ fMap.getWidth()/2;
                final int y = (int)fMap.getY()+ fMap.getHeight()/2;
                initMenu(x, y);
            }
        });
    }

    private void sendMessage(String idCircle, String idSender, String idReceiver, String content, String date){
        progressDialog.setMessage("Envoi du message...");
        progressDialog.show();
        mainmos.addMessage(idCircle, idSender,
               idReceiver, content, date,
                0, new AddMessage_Callback(activity, progressDialog));
    }

    private void initMenu(int x, int y){
        pieMenu = new RadialMenuWidget(activity);
        menuCloseItem = new RadialMenuItem("close", null);
        menuCloseItem
                .setDisplayIcon(android.R.drawable.ic_menu_close_clear_cancel);

        childItemWhereAreYou = new RadialMenuItem("T'es où ?","T'es où ?");
        childItemWhereAreYou
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.;
                        dismissMenu();
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_ID), "T'es où ?", "date");
                    }
                });

        childItemHowAreYou = new RadialMenuItem("Ça va ?","Ça va ?");
        childItemHowAreYou
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu();
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_ID), "Ça va ?", "date");
                    }
                });

        menuExpandQuestion = new RadialMenuItem("Question", "Question");
        childrenQuestions.add(childItemWhereAreYou);
        childrenQuestions.add(childItemHowAreYou);
        menuExpandQuestion.setMenuChildren(childrenQuestions);

        childItemIncoming = new RadialMenuItem("en route","en route");
        childItemIncoming.setDisplayIcon(R.drawable.rocket);
        childItemIncoming
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu();
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_ID), "J'arrive !", "J'arrive !");
                    }
                });

        childItemLate = new RadialMenuItem("en retard !","en retard !");
        childItemLate.setDisplayIcon(R.drawable.watch);
        childItemLate
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu();
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_ID), "Je suis en retard !", "date");
                    }
                });

        childItemLost = new RadialMenuItem("perdu","perdu");
        childItemLost.setDisplayIcon(R.drawable.crying);
        childItemLost
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu();
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_ID), "Je suis perdu...", "date");
                    }
                });

        childItemArrived = new RadialMenuItem("arrivé","arrivé");
        childItemArrived.setDisplayIcon(R.drawable.fire);
        childItemArrived
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu();
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_ID), "Je suis perdu...", "date");
                    }
                });

        menuExpandInfo = new RadialMenuItem("Je suis...", "Je suis...");
        childrenInfos.add(childItemIncoming);
        childrenInfos.add(childItemLost);
        childrenInfos.add(childItemLate);
        childrenInfos.add(childItemArrived);
        menuExpandInfo.setMenuChildren(childrenInfos);

        childItemLeaving = new RadialMenuItem("Je pars !","Je pars !");
        childItemLeaving.setDisplayIcon(R.drawable.door);
        childItemLeaving
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu();
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_ID), "Je pars !", "date");
                    }
                });

        childItemJoinMe = new RadialMenuItem("Rejoins-moi","Rejoins-moi");
        childItemJoinMe.setDisplayIcon(R.drawable.running);
        childItemJoinMe
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu();
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_ID), "Rejoins-moi", "date");
                    }
                });

        childItemSos = new RadialMenuItem("SOS","SOS");
        childItemSos.setDisplayIcon(R.drawable.help);
        childItemSos
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu();
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_ID), "J'ai besoin d'aide !", "date");
                    }
                });

        childItemCigaret = new RadialMenuItem("Clope","Clope");
        childItemCigaret.setDisplayIcon(R.drawable.cigarette);
        childItemCigaret
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu();
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_ID), "Pause clope ?", "date");
                    }
                });

        menuExpandAlert = new RadialMenuItem("Alerte", "Alerte");
        childrenAlert.add(childItemLeaving);
        childrenAlert.add(childItemSos);
        childrenAlert.add(childItemJoinMe);
        childrenAlert.add(childItemCigaret);
        menuExpandAlert.setMenuChildren(childrenAlert);

        menuExpandSmiley = new RadialMenuItem("Smiley", "Smiley");
        //menuSmiley.setMenuChildren(alertInfos);

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
        //pieMenu.setTextColor(Color.WHITE, 1000);
        //pieMenu.setSourceLocation(30, 60);
        pieMenu.setIconSize(15, 30);
        pieMenu.setTextSize(15);
        //pieMenu.setOutlineColor(Color.WHITE, 225);
        //pieMenu.setInnerRingColor(Color.RED, 220);

        Bitmap shaderBmp = BitmapFactory.decodeResource(getResources(), R.drawable.pattern);
        Bitmap shaderOuterBmp = BitmapFactory.decodeResource(getResources(), R.drawable.pattern_outer);
        pieMenu.setInnerRingShader(shaderBmp, 190);
        pieMenu.setOuterRingShader(shaderOuterBmp, 240);
        pieMenu.setTextColor(Color.WHITE, 255);
        //pieMenu.setOuterRingShader(shaderBmp, 160);
        //pieMenu.setOuterRingColor(Color.WHITE, 220);
        pieMenu.setHeader("Anthony Da Mota", 20);
        pieMenu.setCenterCircle(menuCloseItem);
        pieMenu.addMenuEntry(new ArrayList<RadialMenuItem>() {
            {
                add(menuExpandInfo);
                add(menuExpandAlert);
                add(menuExpandQuestion);
                add(menuExpandSmiley);
            }
        });
        pieMenu.setCenterLocation(x,y);
        //pieMenu.setSourceLocation((int)fMap.getX()+ fMap.getWidth()/2, (int)fMap.getY()+ fMap.getHeight()/2);
        //pieMenu.setVisibility(View.GONE);
        //pieMenu.show(containerMenu);
    }

    private void initMap(Cercle cercle){

        /*

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

        locationManager.requestLocationUpdates(provider, 20000, 0, this);*/

        // Location myLocation = map.getMyLocation();
        Location myLocation = getLocation();
        LatLng myLocationLatlng = new LatLng(myLocation.getLatitude(),
                myLocation.getLongitude());

        markerOptions = new MarkerOptions();
        markerOptions.position(myLocationLatlng);
        //BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
        //        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        //markerOptions.icon(bitmapDescriptor);

        HomeActivity h = (HomeActivity)getActivity();
        Bitmap b = h.getProfilePicture();

        if(b==null){
            b= BitmapFactory.decodeResource(getResources(),
                    R.drawable.profile_test);
        }

        Bitmap b_rounded = ImageHelper.getRoundedCornerBitmap(b, 1000, 0);
        Bitmap b_resized = Bitmap.createScaledBitmap(b_rounded, 60, 60, false);

        View marker_layout = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView profilePicture = (ImageView) marker_layout.findViewById(R.id.profile_picture);
        profilePicture.setImageBitmap(b_resized);

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(ImageHelper.createDrawableFromView(activity, marker_layout)));


        //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(b_resized));
        // Specifies the anchor to be at a particular point in the marker image.
        //markerOptions.anchor(0.5f, 1);

        markerOptions.title("Anthony Da Mota");
        map.addMarker(markerOptions);
        map.animateCamera(CameraUpdateFactory.newLatLng(myLocationLatlng), 200, null);

        // TEST
        LatLng myLocationLatlng2 = new LatLng(myLocation.getLatitude()+1,
                myLocation.getLongitude()+5);

        markerOptions.position(myLocationLatlng2);
        markerOptions.title("Anthony Da Mota");

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
        pieMenu.setAnimationSpeed(300L);
        pieMenu.show(containerMenu);

        //displayMenuAnimation(0, 1, View.VISIBLE);
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
        //displayMenuAnimation(1, 0, View.GONE);
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

        //map.animateCamera(CameraUpdateFactory.zoomTo(15));
        //map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

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

    public static long MIN_TIME_BW_UPDATES = 2000;
    public static float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    public Location getLocation() {

        Location location = null;
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        try {

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.i("ANTHO", "pas de gps ni network");
            } else {
                canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.i("ANTHO", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("ANTHO", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
}
