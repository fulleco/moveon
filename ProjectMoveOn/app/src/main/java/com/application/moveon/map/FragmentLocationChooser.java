package com.application.moveon.map;

import android.app.Fragment;
import android.content.Context;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.cercle.FragmentCreateCercle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by damota on 10/12/2014.
 */
public class FragmentLocationChooser extends Fragment implements LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private MarkerOptions markerOptions;
    private LatLng locationMap;

    private Double finalLatitude = 0.0;
    private Double finalLongitude = 0.0;
    private String finalAdressString = "";

    private Button btn_find;
    private Button btn_validate;

    private Address currentAddress;
    private List<Address> addresses;

    private SupportMapFragment supportMapFragment;

    private int cursor;
    private CircleOptions circle;
    private int radius;

    private Location myLocation = null;

    private View fMap;

    private FragmentActivity activity;

    private ArrayList<Marker> markersList;
    private Marker selectedMarker;

    private EditText etLocation;

    private ListView resultsList;
    private LocationResultsAdapter adapter;

    private TextView nbResultsText;
    private RelativeLayout layoutResults;

    private int nbResults;
    private static View view;

    private boolean canGetLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_location, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        activity = (FragmentActivity)getActivity();

        // Recuperer le fragment de la map
        supportMapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.location_map);

        // Recuperer la map
        map = supportMapFragment.getMap();

        supportMapFragment.getView().setVisibility(View.VISIBLE);

        //FragmentManager fm = view.getSupportFragmentManager();
        //FragmentTransaction ft = fm.beginTransaction();
        //ft.hide(supportMapFragment).commit();

        markersList = new ArrayList<Marker>();
        resultsList = (ListView) view.findViewById(R.id.results_list);
        etLocation = (EditText) view.findViewById(R.id.edit_location);

        layoutResults = (RelativeLayout) view.findViewById(R.id.relative_results);
        layoutResults.setVisibility(View.GONE);

        layoutResults.setOnTouchListener(new View.OnTouchListener() {
                                             public boolean onTouch(View view, MotionEvent event) {
                                                 if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                                                    //resultsList.setVisibility(View.GONE);
                                                 } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                                                     if(resultsList.getVisibility()==View.VISIBLE){
                                                         //resultsList.setVisibility(View.GONE);
                                                         collapse(resultsList);
                                                     }else{
                                                         //resultsList.setVisibility(View.VISIBLE);
                                                         expand(resultsList);
                                                     }
                                                 }
                                                 return true;
                                             }
                                         });

                resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        collapse(resultsList);

                        final Marker m = (Marker) resultsList.getItemAtPosition(position);

                        map.animateCamera(CameraUpdateFactory
                                .newLatLng(m.getPosition()), 400, new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                                map.getUiSettings().setScrollGesturesEnabled(true);
                                selectedMarker = m;
                                supportMapFragment.getView().setVisibility(View.VISIBLE);
                                resultsList.setVisibility(View.GONE);
                                //showMenu();
                            }

                            @Override
                            public void onCancel() {
                                map.getUiSettings().setAllGesturesEnabled(true);
                            }
                        });
                    }
                });

        View.OnClickListener findClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //supportMapFragment.getView().setVisibility(View.GONE);
                layoutResults.setVisibility(View.VISIBLE);

                cursor = 0;
                map.clear();

                // Recuperer l'adresse demandee
                String location = etLocation.getText().toString();

                if (location != null && !location.equals("")) {
                    new LocateTask().execute(location);
                }
            }
        };

        View.OnClickListener validateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCreateCercle fcc = (FragmentCreateCercle)getTargetFragment();
                fcc.setLatitude(selectedMarker.getPosition().latitude);
                fcc.setLongitude(selectedMarker.getPosition().longitude);
                map.clear();
                HomeActivity home = (HomeActivity)getActivity();
                home.switchFragment(fcc);
            }
        };

        btn_find = (Button) view.findViewById(R.id.btn_location_find);
        btn_find.setOnClickListener(findClickListener);

        btn_validate = (Button) view.findViewById(R.id.btn_location_validate);
        btn_validate.setOnClickListener(validateClickListener);

        nbResultsText = (TextView) view.findViewById(R.id.text_nb_results);

        initMap();
        fMap = view.findViewById(R.id.location_map);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initMap(){
        /*// Getting LocationManager object from System Service LOCATION_SERVICE
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

        //locationManager.requestLocationUpdates(provider, 20000, 0, this);

        // Location myLocation = map.getMyLocation();*/
        Location myLocation = getLocation();
        LatLng myLocationLatlng = new LatLng(myLocation.getLatitude(),
                myLocation.getLongitude());

        markerOptions = new MarkerOptions();
        markerOptions.position(myLocationLatlng);

        map.animateCamera(CameraUpdateFactory.newLatLng(myLocationLatlng));

        circle = new CircleOptions();

        // 55 represents percentage of transparency. For 100% transparency,
        // specify 00.
        // For 0% transparency ( ie, opaque ) , specify ff
        // The remaining 6 characters(00ff00) specify the fill color
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                //lstLatLngs.add(point);
                layoutResults.setVisibility(View.GONE);
                resultsList.setAdapter(null);
                MarkerOptions options = new MarkerOptions();
                map.clear();
                options.position(point);
                options.title("Point de rendez-vous");
                selectedMarker = map.addMarker(options);
            }
        });

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        //if(marker.getTitle().equals("MyHome")) // if marker source is clicked
        //    Toast.makeText(HomeActivity.this, marker.getTitle(),Toast.LENGTH_LONG).show();// display toast
        map.animateCamera(CameraUpdateFactory
                .newLatLng(marker.getPosition()),400,new GoogleMap.CancelableCallback()
        {
            @Override
            public void onFinish()
            {
                map.getUiSettings().setScrollGesturesEnabled(true);
                //MarkerOptions mOptions = new MarkerOptions();
                //mOptions.title(marker.getTitle());
                //mOptions.position(marker.getPosition());
                Log.i("ANTHO_EXC", "CHOOSER " + marker.getPosition().latitude
                        + " " + marker.getPosition().longitude);
                selectedMarker = marker;
                btn_validate.setEnabled(true);
                marker.showInfoWindow();
                //showMenu();
            }

            @Override
            public void onCancel()
            {
                map.getUiSettings().setAllGesturesEnabled(true);
            }
        });

        return true;
    }

    private class LocateTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {

            Geocoder geocoder = new Geocoder(activity.getBaseContext(), Locale.FRENCH);
            addresses = null;

            try {
                // Recuperer 10 adresses possibles
                addresses = geocoder.getFromLocationName(locationName[0], 20);
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

            // Vider la liste de résultats
            markersList.clear();

            nbResults = addresses.size();

            if(nbResults>1) {
                nbResultsText.setText(nbResults + " résultats trouvés.");
            }else{
                nbResultsText.setText(nbResults + " résultat trouvé.");
            }

            // Ajouter un nouveau marqueur par adresse
            for (int i = 0; i < addresses.size(); i++) {

                currentAddress = addresses.get(i);

                locationMap = new LatLng(currentAddress.getLatitude(),
                        currentAddress.getLongitude());

                /*finalAdressString = String
                        .format("%s, %s",
                                currentAddress.getMaxAddressLineIndex() > 0 ? currentAddress
                                        .getAddressLine(0) : "", currentAddress
                                        .getCountryName());*/

                finalAdressString = constructAddress(currentAddress);

                markerOptions = new MarkerOptions();
                markerOptions.position(locationMap);
                markerOptions.title(finalAdressString);

                Marker m = map.addMarker(markerOptions);
                markersList.add(m);

                // Deplacer la map a la premiere adresse
                if (i == 0)
                    map.animateCamera(CameraUpdateFactory
                            .newLatLng(locationMap));
            }

            adapter = new LocationResultsAdapter(getActivity(), getActivity()
                    .getApplicationContext(), R.layout.layout_item_location,
                    markersList);
            resultsList.setAdapter(adapter);
        }
    }

    private String constructAddress(Address a){
        String result = "";
        for(int i = 0; i < a.getMaxAddressLineIndex(); i++) {
            result += a.getAddressLine(i) + " ";
        }
        return result.trim();
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
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

    public void expand(final View v) {
        v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        final int targetHeight = v.getMeasuredHeight() * nbResults;

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(600);
        v.startAnimation(a);
    }

    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(600);
        v.startAnimation(a);
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
