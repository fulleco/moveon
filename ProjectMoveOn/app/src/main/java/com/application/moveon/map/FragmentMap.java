package com.application.moveon.map;

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
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.custom.CustomProgressDialog;
import com.application.moveon.menu.FragmentSlidingMenu;
import com.application.moveon.menu.v1.RadialMenuItem;
import com.application.moveon.menu.v1.RadialMenuWidget;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.AddMessage_Callback;
import com.application.moveon.rest.callback.AddMessages_Callback;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.rest.modele.UserPojo;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ImageHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by damota on 10/12/2014.
 */
public class FragmentMap extends Fragment implements GoogleMap.OnMarkerClickListener, SensorEventListener, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap map;
    private MarkerOptions markerOptions;
    private LatLng locationMap;
    private HashMap<Marker, UserPojo> markers = new HashMap<Marker, UserPojo>();

    private MapFragment supportMapFragment;

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

    private SlidingUpPanelLayout mSlidingPanel;

    private ProgressDialog progressDialog;

    private MoveOnService mainmos;

    private SessionManager session;

    private HomeActivity homeActivity;

    private Marker selectedMarker = null;
    private Marker myMarker = null;
    private String selectedLogin = null;

    // angle compas
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;
    private boolean mapLoaded = false;

    private ArrayList<Target> targetList;
    private Bitmap shaderBmp = null;
    private Bitmap shaderOuterBmp = null;

    private LocationClient locationclient;
    private LocationRequest locationrequest;

    private boolean updatingCircles = false;

    private CustomProgressDialog customProgress = null;

    private FragmentSlidingMenu fragmentSlidingMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

        // Recuperer le fragment de la map
        supportMapFragment = (MapFragment) activity.getFragmentManager()
                .findFragmentById(R.id.map);

        fragmentSlidingMenu = (FragmentSlidingMenu) activity.getSupportFragmentManager().findFragmentById(R.id.slidingContent);

        mSlidingPanel = (SlidingUpPanelLayout) view
                .findViewById(R.id.sliding_layout);
        mSlidingPanel.setAnchorPoint(0.45f);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recuperer la map
        map = supportMapFragment.getMap();
        fMap = view.findViewById(R.id.map);

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

        String receivers = "";
        for(UserPojo u : homeActivity.getCurrentCercle().getParticipants()){
            if(!String.valueOf(u.getId_client()).equals(idSender))
            receivers += u.getId_client() + " ";
        }

        if(idSender.equals(idReceiver)){
            mainmos.addMessages(idCircle, idSender,
                    receivers.trim(), content, new SimpleDateFormat("dd-MM-yyyy").format(new Date()),
                    0, new AddMessages_Callback(activity, progressDialog));
        }else {
            mainmos.addMessage(idCircle, idSender,
                    idReceiver, content, new SimpleDateFormat("dd-MM-yyyy").format(new Date()),
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin, "T'es où ?", "date");
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin, "Ça va ?", "date");
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
                        sendMessage("1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin, "J'arrive !", "J'arrive !");
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin, "Je suis en retard !", "date");
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin, "Je suis perdu...", "date");
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin, "Je suis perdu...", "date");
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin, "Je pars !", "date");
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin, "Rejoins-moi", "date");
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin, "J'ai besoin d'aide !", "date");
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin, "Pause clope ?", "date");
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin,
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin,
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin,
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin,
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin,
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
                        sendMessage( "1", session.getUserDetails().get(SessionManager.KEY_ID), selectedLogin,
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

        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(homeActivity);
        if(resp == ConnectionResult.SUCCESS){
            locationclient = new LocationClient(homeActivity,this,null);
            locationclient.connect();
        }
        else{
            Toast.makeText(homeActivity, "Google Play Service indisponible  " + resp, Toast.LENGTH_LONG).show();
        }
    }

    public void changeCircle(){
        customProgress = new CustomProgressDialog(homeActivity);
        customProgress.show();
        refresh();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    public void initCercle() {

        CerclePojo currentCercle = homeActivity.getCurrentCercle();
        if (currentCercle != null) {
            //HashMap<Marker, UserPojo> newMarkers = new HashMap<Marker, UserPojo>();
            for (UserPojo u : currentCercle.getParticipants()) {
                if (!(u.getLogin()).equals(session.getUserDetails().get(SessionManager.KEY_EMAIL))) {
                    loadBitmap(u, false);
                }
            }
        }else{
            return;
        }
        if (customProgress.isShowing()) {
            customProgress.dismiss();
            customProgress = null;
        }
    }

    public Marker removeMarker(UserPojo u, LatLng lastLngUser){
        Iterator it = markers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(((UserPojo)pair.getValue()).getLogin().equals(u.getLogin())){
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
        markerOptions.flat(true);

        if(isCurrentSession)
            markerOptions.title("Moi");
        else
            markerOptions.title(u.getFirstname() + " " + u.getLastname());

        markerOptions.visible(false);

        synchronized (markers) {
            Marker mTmp = removeMarker(u, lastLngUser);
            final Marker m = mTmp!=null?mTmp:map.addMarker(markerOptions);

            if(isCurrentSession){
                myMarker = m;

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(lastLngUser)      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                                //.bearing(90)                // Sets the orientation of the camera to east
                                //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mapLoaded = true;
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                        SensorManager.SENSOR_DELAY_GAME);
                //initCercle();
            }else if(mTmp==null){
                markers.put(m, u);
                //newMarkers.put(m, u);
            }

            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.i("ANTHO_EXC", "bitmap loaded" + u.getLogin());
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

            String image = "http://martinezhugo.com/pfe/images/"+ u.getId_client()+"/profile.jpg";
            Picasso.with(homeActivity).load(image).into(target);
        }
    }

    public void addMarker(Target target, UserPojo u, Bitmap b, Marker m){

        if(!isAdded()) {
            Log.i("ANTHO_EXC", "!Added");
            return;
        }

        synchronized (markers) {

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

            Log.i("ANTHO_EXC", "ADD MARKER " + m.getTitle());
            m.setIcon(BitmapDescriptorFactory.fromBitmap(ImageHelper.createDrawableFromView(activity, marker_layout)));
            m.setVisible(true);
            targetList.remove(target);

            //b_rounded.recycle();
            //b_rounded = null;
        }
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
                        synchronized (markers) {
                            UserPojo userSelected = getUserByMarker(marker);
                            if(userSelected==null)
                                return;
                            pieMenu.setHeader(userSelected.getFirstname()+ " " + userSelected.getLastname(), 20);
                            selectedLogin = String.valueOf(userSelected.getId_client());
                        }
                    }else{
                        pieMenu.setHeader("Moi", 20);
                        selectedLogin = session.getUserDetails().get(SessionManager.KEY_ID);
                    }
                    showMenu(pieMenu);
                }
            }

            public UserPojo getUserByMarker(Marker m){
                Iterator it = markers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    Marker mPair = ((Marker)pair.getKey());
                    if(m.getId().equals(mPair.getId()))
                        return (UserPojo)pair.getValue();

                    it.remove(); // avoids a ConcurrentModificationException
                }
                return null;
            }

            @Override
            public void onCancel()
            {
                map.getUiSettings().setAllGesturesEnabled(true);
            }
        });

        return true;
    }

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
    }

    private void showMenu(RadialMenuWidget m){
        //pieMenu.setVisibility(View.GONE);
        m.setAnimationSpeed(300L);
        m.show(containerMenu);
    }

    private void dismissMenu(RadialMenuWidget m) {
        //displayMenuAnimation(1, 0, View.GONE);
        m.dismiss();
        m.setSelected(false);
    }

    @Override
    public void onResume(){
        super.onResume();

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) homeActivity.getSystemService(homeActivity.SENSOR_SERVICE);

        initMap();
    }

    @Override
    public void onDestroy(){
        shaderBmp.recycle();
        shaderBmp = null;
        shaderOuterBmp.recycle();
        shaderOuterBmp = null;
        //map.clear();
        //map = null;
        if(locationclient!=null)
            locationclient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        homeActivity.stopRepeatingTask();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
        map.clear();
        synchronized (markers){
            markers.clear();
            myMarker = null;
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("ANTHO", "STOP FRAGMENT");
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

    @Override
    public void onConnected(Bundle bundle) {
        refresh();
    }

    public void refresh(){
        homeActivity.stopRepeatingTask();
        synchronized (markers) {

        if(myMarker!=null)
            map.clear();
        myMarker = null;

            markers.clear();
        }

        locationrequest = LocationRequest.create();
        locationrequest.setInterval(100);
        locationclient.requestLocationUpdates(locationrequest, this);

        UserPojo user = session.getUserPojo();
        myLocation =locationclient.getLastLocation();
        user.setLatitude(String.valueOf(myLocation.getLatitude()));
        user.setLongitude(String.valueOf(myLocation.getLongitude()));

        synchronized (markers) {
            loadBitmap(user, true);
        }

        //map.setOnMyLocationChangeListener();
        map.setOnMarkerClickListener(this);
        homeActivity.startUpdateUI();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
            myLocation = location;

            UserPojo user = session.getUserPojo();
            user.setLatitude(String.valueOf(myLocation.getLatitude()));
            user.setLongitude(String.valueOf(myLocation.getLongitude()));

            // Getting latitude of the current location
            double latitude = location.getLatitude();

            // Getting longitude of the current location
            double longitude = location.getLongitude();

            // Creating a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);
            if(myMarker!=null)
                myMarker.setPosition(latLng);
        }
    }

    @Override
    public void onDisconnected() {
        Log.i("ANTHO_EXC", "disconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(homeActivity, "GPS indisponible", Toast.LENGTH_LONG).show();
    }

    public boolean isUpdatingCircles() {
        return updatingCircles;
    }

    public void setUpdatingCircles(boolean updatingCircles) {
        this.updatingCircles = updatingCircles;
    }

    public HashMap<Marker, UserPojo> getMarkers() {
        return markers;
    }

    public void setMarkers(HashMap<Marker, UserPojo> markers) {
        this.markers = markers;
    }

    public SlidingUpPanelLayout getmSlidingPanel() {
        return mSlidingPanel;
    }

    public void updateSlidingUpDrawer() {
        fragmentSlidingMenu.updateContent();
    }
}
