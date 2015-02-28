package com.application.moveon.map;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.OrientationEventListener;
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
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.AddFriend_Callback;
import com.application.moveon.rest.callback.AddMessage_Callback;
import com.application.moveon.rest.callback.AddMessages_Callback;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.rest.modele.UserPojo;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ImageHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.application.moveon.menu.v1.RadialMenuItem;
import com.application.moveon.menu.v1.RadialMenuWidget;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by damota on 10/12/2014.
 */
public class FragmentMap extends Fragment implements LocationListener, GoogleMap.OnMarkerClickListener, SensorEventListener {

    private GoogleMap map;
    private MarkerOptions markerOptions;
    private LatLng locationMap;
    private HashMap<Marker, UserPojo> markers;

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

    private RadialMenuWidget pieMenu, pointMenu;
    public RadialMenuItem menuClosePoint, menuEditPoint, menuDeletePoint;
    public RadialMenuItem menuCloseItem, menuExpandInfo, menuExpandAlert, menuExpandQuestion, menuExpandSmiley;
    public RadialMenuItem childItemWhereAreYou, childItemHowAreYou, childItemIncoming, childItemLate,
                          childItemLost, childItemArrived, childItemLeaving, childItemJoinMe, childItemSos, childItemCigaret,
                          childrenEating, childrenCute, childrenSad, childrenDoubt, childrenBigSmile, childrenHaha,
                          childrenUpset, childrenCrying;
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

    private HomeActivity homeActivity;

    private boolean placePoint = false;

    private Marker selectedMarker = null;
    private Marker myMarker = null;
    private String selectedLogin = null;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;
    private boolean mapLoaded = false;

    private ArrayList<Target> targetList;
    private Bitmap shaderBmp = null;
    private Bitmap shaderOuterBmp = null;

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
            Log.d("INFLATE EXCEPTION : ",e.toString());
        }

        homeActivity = (HomeActivity) getActivity();
        activity = (FragmentActivity) getActivity();

        targetList = new ArrayList<Target>();

        containerMenu = (FrameLayout) view.findViewById(R.id.containerMenu);

        session = new SessionManager(homeActivity);

        progressDialog = new ProgressDialog(homeActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);

        RestClient r = new RestClient(false);
        mainmos = (new RestClient(true)).getApiService();

        radius = 10;

        setHasOptionsMenu(true);

        markers = new HashMap<Marker, UserPojo>();

        // Recuperer le fragment de la map
        supportMapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Recuperer la map
        map = supportMapFragment.getMap();
        fMap = view.findViewById(R.id.map);

        mSlidingPanel = (SlidingUpPanelLayout) view
                .findViewById(R.id.sliding_layout);
        mSlidingPanel.setAnchorPoint(0.45f);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) homeActivity.getSystemService(homeActivity.SENSOR_SERVICE);

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

        initMap();
    }

    private void sendMessage(String idCircle, String idSender, String idReceiver, String content, String date){
        progressDialog.setMessage("Envoi du message...");
        progressDialog.show();

        String receivers = "";
        for(UserPojo u : homeActivity.getCurrentCercle().getParticipants()){
            receivers += u.getLogin() + " ";
        }

        if(idSender.equals(idReceiver)){
            mainmos.addMessages(idCircle, idSender,
                    receivers.trim(), content, date,
                    0, new AddMessages_Callback(activity, progressDialog));
        }else {
            mainmos.addMessage(idCircle, idSender,
                    idReceiver, content, date,
                    0, new AddMessage_Callback(activity, progressDialog));
        }
    }

    private void initMenu(int x, int y){
        pointMenu = new RadialMenuWidget(activity);

        menuEditPoint = new RadialMenuItem("edit_point", null);
        menuEditPoint
                .setDisplayIcon(android.R.drawable.edit_text);
        menuEditPoint
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {

                        dismissMenu(pointMenu);

                    }
                });

        menuDeletePoint = new RadialMenuItem("delete_point", null);
        menuDeletePoint
                .setDisplayIcon(android.R.drawable.ic_menu_close_clear_cancel);
        menuDeletePoint
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        selectedMarker.remove();
                        dismissMenu(pointMenu);
                    }
                });

        pointMenu.setIconSize(15, 30);
        pointMenu.setTextSize(15);

        shaderBmp = BitmapFactory.decodeResource(getResources(), R.drawable.pattern);
        shaderOuterBmp = BitmapFactory.decodeResource(getResources(), R.drawable.pattern_outer);
        pointMenu.setInnerRingShader(shaderBmp, 190);
        pointMenu.setOuterRingShader(shaderOuterBmp, 240);
        pointMenu.setTextColor(Color.WHITE, 255);
        //pieMenu.setOuterRingShader(shaderBmp, 160);
        //pieMenu.setOuterRingColor(Color.WHITE, 220);
        pointMenu.setHeader("Point de rencontre", 20);
        pointMenu.setCenterCircle(menuCloseItem);
        pointMenu.addMenuEntry(new ArrayList<RadialMenuItem>() {
            {
                add(menuDeletePoint);
                add(menuEditPoint);
            }
        });

        pointMenu.setCenterLocation(x,y);

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
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin, "T'es où ?", "date");
                    }
                });

        childItemHowAreYou = new RadialMenuItem("Ça va ?","Ça va ?");
        childItemHowAreYou
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin, "Ça va ?", "date");
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
                        dismissMenu(pieMenu);
                        sendMessage("1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin, "J'arrive !", "J'arrive !");
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
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin, "Je suis en retard !", "date");
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
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin, "Je suis perdu...", "date");
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
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin, "Je suis perdu...", "date");
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
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin, "Je pars !", "date");
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
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin, "Rejoins-moi", "date");
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
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin, "J'ai besoin d'aide !", "date");
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
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin, "Pause clope ?", "date");
                    }
                });

        menuExpandAlert = new RadialMenuItem("Alerte", "Alerte");
        childrenAlert.add(childItemLeaving);
        childrenAlert.add(childItemSos);
        childrenAlert.add(childItemJoinMe);
        childrenAlert.add(childItemCigaret);
        menuExpandAlert.setMenuChildren(childrenAlert);

        childrenEating = new RadialMenuItem("","");
        childrenEating.setDisplayIcon(R.drawable.eating);
        childrenEating
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin,
                                session.getUserDetails().get(SessionManager.KEY_FIRSTNAME) + " vous envoie un smiley", "date");
                    }
                });

        childrenCute = new RadialMenuItem("","");
        childrenCute.setDisplayIcon(R.drawable.cute);
        childrenCute
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin,
                                session.getUserDetails().get(SessionManager.KEY_FIRSTNAME) + " vous envoie un smiley", "date");
                    }
                });

        childrenBigSmile = new RadialMenuItem("","");
        childrenBigSmile.setDisplayIcon(R.drawable.big_smile);
        childrenBigSmile
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin,
                                session.getUserDetails().get(SessionManager.KEY_FIRSTNAME) + " vous envoie un smiley", "date");
                    }
                });

        childrenHaha = new RadialMenuItem("","");
        childrenHaha.setDisplayIcon(R.drawable.haha);
        childrenHaha
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin,
                                session.getUserDetails().get(SessionManager.KEY_FIRSTNAME) + " vous envoie un smiley", "date");
                    }
                });

        childrenUpset = new RadialMenuItem("","");
        childrenUpset.setDisplayIcon(R.drawable.upset);
        childrenUpset
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin,
                                session.getUserDetails().get(SessionManager.KEY_FIRSTNAME) + " vous envoie un smiley", "date");
                    }
                });

        childrenCrying = new RadialMenuItem("","");
        childrenCrying.setDisplayIcon(R.drawable.crying);
        childrenCrying
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        // Can edit based on preference. Also can add animations
                        // here.
                        dismissMenu(pieMenu);
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_EMAIL), selectedLogin,
                                session.getUserDetails().get(SessionManager.KEY_FIRSTNAME) + " vous envoie un smiley", "date");
                    }
                });

        menuExpandSmiley = new RadialMenuItem("Smiley", "Smiley");
        childrenSmiley.add(childrenBigSmile);
        childrenSmiley.add(childrenCute);
        childrenSmiley.add(childrenEating);
        childrenSmiley.add(childrenHaha);
        childrenSmiley.add(childrenUpset);
        childrenSmiley.add(childrenCrying);
        menuExpandSmiley.setMenuChildren(childrenSmiley);

        menuCloseItem
                .setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
                    @Override
                    public void execute() {
                        dismissMenu(pieMenu);
                    }
                });

        pieMenu.setIconSize(15, 30);
        pieMenu.setTextSize(15);
        pieMenu.setInnerRingShader(shaderBmp, 190);
        pieMenu.setOuterRingShader(shaderOuterBmp, 240);
        pieMenu.setTextColor(Color.WHITE, 255);
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
    }

    private void initMap(){

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

        Location myLocation = getLocation();
        LatLng myLocationLatlng = new LatLng(myLocation.getLatitude(),
                myLocation.getLongitude());

        UserPojo user = session.getUserPojo();
        user.setLatitude(String.valueOf(myLocation.getLatitude()));
        user.setLongitude(String.valueOf(myLocation.getLongitude()));

        loadBitmap(user, true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                if(placePoint) {
                    //lstLatLngs.add(point);
                    MarkerOptions options = new MarkerOptions();
                    options.position(point);
                    options.title("Point de rencontre");
                    map.addMarker(options);
                }
            }
        });
        //map.setOnMyLocationChangeListener();
        map.setOnMarkerClickListener(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        homeActivity.startUpdateUI();
    }

    private HashMap<Marker, UserPojo> newMarkers;

    public void initCercle() {

        CerclePojo cercle = homeActivity.getCurrentCercle();
        if(cercle != null){

            newMarkers = new HashMap<Marker, UserPojo>();
            //markers.clear();

            for(UserPojo u : cercle.getParticipants()) {

                if (!(u.getLogin()).equals(session.getUserDetails().get(SessionManager.KEY_EMAIL))) {
                    loadBitmap(u, false);
                }
            }

            markers = newMarkers;
            //map.setOnMarkerClickListener(this);
        }
    }

    public Marker removeMarker(UserPojo u, LatLng lastLngUser){
        Iterator it = markers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(pair.getValue().equals(u)){
                Marker m = (Marker)pair.getKey();
                m.setPosition(lastLngUser);
                return m;
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return null;
    }

    public void loadBitmap(final UserPojo u, final boolean isCurrentSession){

        MarkerOptions markerOptions = new MarkerOptions();

        LatLng lastLngUser = new LatLng(Double.parseDouble(u.getLatitude()),
                Double.parseDouble(u.getLongitude()));
        markerOptions.position(lastLngUser);

        if(isCurrentSession)
            markerOptions.title("Moi");
        else
            markerOptions.title(u.getFirstname() + " " + u.getLastname());

        markerOptions.visible(false);

        Marker mTmp = removeMarker(u, lastLngUser);

        final Marker m = mTmp!=null?mTmp:map.addMarker(markerOptions);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                addMarker(this, u, bitmap, m);
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {
                addMarker(this, u, null, m);
            }

            @Override
            public void onPrepareLoad(Drawable drawable) {
            }
        };

        targetList.add(target);

        if(isCurrentSession){
            myMarker = m;
            map.animateCamera(CameraUpdateFactory.newLatLng(lastLngUser), 200, null);
            mapLoaded = true;
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                    SensorManager.SENSOR_DELAY_GAME);
            //initCercle();
        }else{
            newMarkers.put(m, u);
        }

        String image = "http://martinezhugo.com/pfe/images/"+ u.getId_client()+"/profile.jpg";
        Picasso.with(homeActivity).load(image).into(target);
    }

    public void addMarker(Target target, UserPojo u, Bitmap b, Marker m){

        if(!isAdded()) {
            return;
        }

        //LatLng lastLngUser = new LatLng(Double.parseDouble(u.getLatitude()),
        //        Double.parseDouble(u.getLongitude()));

        Bitmap b_rounded;
        Bitmap b_resized;

        if (b == null) {
            b = BitmapFactory.decodeResource(getResources(),
                    R.drawable.profile_test);
        }

        b_rounded = ImageHelper.getRoundedCornerBitmap(b, 1000, 0);
        b_resized = Bitmap.createScaledBitmap(b_rounded, 60, 60, false);

        View marker_layout = ((LayoutInflater) homeActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView profilePicture = (ImageView) marker_layout.findViewById(R.id.profile_picture);
        profilePicture.setImageBitmap(b_resized);

        m.setIcon(BitmapDescriptorFactory.fromBitmap(ImageHelper.createDrawableFromView(activity, marker_layout)));
        m.setVisible(true);
        targetList.remove(target);

        b_rounded.recycle();
        b_rounded = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==(R.id.action_point)){
            placePoint = !placePoint;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        map.animateCamera(CameraUpdateFactory
                .newLatLng(marker.getPosition()),400,new GoogleMap.CancelableCallback()
        {
            @Override
            public void onFinish()
            {
                map.getUiSettings().setScrollGesturesEnabled(true);
                selectedMarker = marker;
                if(marker.getTitle().equals("Point de rencontre")) {
                    showMenu(pointMenu);
                }else{
                    if(!selectedMarker.equals(myMarker)){
                        UserPojo userSelected = markers.get(marker);
                        pieMenu.setHeader(userSelected.getFirstname()+ " " + userSelected.getLastname(), 20);
                        selectedLogin = userSelected.getLogin();
                    }else{
                        pieMenu.setHeader("Moi", 20);
                        selectedLogin = session.getUserDetails().get(SessionManager.KEY_EMAIL);
                    }
                    showMenu(pieMenu);
                }
            }

            @Override
            public void onCancel()
            {
                map.getUiSettings().setAllGesturesEnabled(true);
            }
        });

        return true;
    }

    private void showMenu(RadialMenuWidget m){
        //pieMenu.setVisibility(View.GONE);
        m.setAnimationSpeed(300L);
        m.show(containerMenu);
    }

    private void displayMenuAnimation(final RadialMenuWidget m, int alpha1, int alpha2,
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
                m.setVisibility(visibility);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });

        m.setAnimation(fadeAnimation);
    }

    private void dismissMenu(RadialMenuWidget m) {
        //displayMenuAnimation(1, 0, View.GONE);
        m.dismiss();
        m.setSelected(false);
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
        if(myMarker!=null)
            myMarker.setPosition(latLng);

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
        LocationManager locationManager = (LocationManager) homeActivity.getSystemService(Context.LOCATION_SERVICE);

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

    @Override
    public void onResume(){
        super.onResume();
        if(mapLoaded)
        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onDestroy(){
        Log.i("ANTHO", "destroy");

        shaderBmp.recycle();
        shaderBmp = null;
        shaderOuterBmp.recycle();
        shaderOuterBmp = null;
        map.clear();
        map = null;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        myMarker.setRotation(degree);
        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
