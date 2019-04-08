package com.david.scavengerhunt;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    public static String TroopName = "";

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    NavigationView navigationView;

    private MapView mapView;
    private GoogleMap gmap;
    private static final int RADIUS = 100; // radius in meters
    private static final int LOC_PERM_REQ_CODE = 1;
    private final int minimumTimeUpdate = 5000; // milliseconds
    private final int minimumDistanceUpdate = 5; // meters

    int targetLocationIndex = 0;
    boolean messageDisplayed = false;

    Marker marker;
    LocationManager locationManager;
    Circle circle;

    ArrayList<LatLng> allLocations = new ArrayList<LatLng>();
    ArrayList<String> allCodes = new ArrayList<String>();
    ArrayList<String> allMessages = new ArrayList<String>();

    ArrayList<String> fakeMessages = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TroopName = sharedPreferences.getString("TroopName", "");

        View v = navigationView.getHeaderView(0);
        TextView textViewTroopName = (TextView) v.findViewById(R.id.textViewTroopName);


        // adding locations according to the troop currently using the app
        if(TroopName.equals("taleat el asad")){
            textViewTroopName.setText("Asad");
            allLocations.add(new LatLng(30.100420, 31.342896)); // medan triumph
            allLocations.add(new LatLng(30.096478, 31.334512)); // medan el esma3lya
            allLocations.add(new LatLng(30.074894, 31.273494)); // el demrdash (masged ahl el kor2an)
        }
        else if(TroopName.equals("taleat el fahd")){
            textViewTroopName.setText("Fahd");
            allLocations.add(new LatLng(30.097651, 31.338400)); // medan safeer
            allLocations.add(new LatLng(30.093463, 31.325010)); // el korba (bazaleek)
            allLocations.add(new LatLng(30.075140, 31.283305)); // el 3abasseya (masged el noor)
        }
        else if(TroopName.equals("taleat el nesr")){
            textViewTroopName.setText("Nesr");
            allLocations.add(new LatLng(30.103719, 31.328444)); // medan el ma7kama
            allLocations.add(new LatLng(30.096782, 31.320709)); // merryland (mcdonalds)
            allLocations.add(new LatLng(30.061991, 31.248134)); // ramsis
        }
        else if(TroopName.equals("taleat el nemr")){
            textViewTroopName.setText("Nemr");
            allLocations.add(new LatLng(30.099949, 31.335222)); // mat7af suzan mubarak el tefl (united bank)
            allLocations.add(new LatLng(30.094296, 31.327743)); // salah el deen
            allLocations.add(new LatLng(30.071308, 31.269237)); // 3'amra
        }

        allLocations.add(new LatLng(30.052336, 31.246741)); // 3ataba
        allLocations.add(new LatLng(30.080382, 31.330727)); // genent vodafone

        allCodes.add("0");
        allCodes.add("189");

        if(TroopName.equals("taleat el asad")){
            allCodes.add("40");
        }
        else if(TroopName.equals("taleat el fahd")){
            allCodes.add("35");
        }
        else if(TroopName.equals("taleat el nesr")){
            allCodes.add("60");
        }
        else if(TroopName.equals("taleat el nemr")){
            allCodes.add("8");
        }

        allCodes.add("273");
        allCodes.add("469");


        allMessages.add("ابدأوا المشوار..الهدف هو الوصول بسرعة وبأقل فلوس ممكنة");

        if(TroopName.equals("taleat el asad")){
            allMessages.add("0128595**** it will help you."+"\n"+
                    "-٤ المركز العلمي \n" +
                    "-٩ دكتور سامح الامام \n" +
                    "-٤ دكتور ماجد ماهر صليب \n" +
                    "-٧ دكتور موريس فكري");
        }
        else if(TroopName.equals("taleat el fahd")){
            allMessages.add("0128595**** it will help you."+"\n"+
                    "-٧ فكهاني سفير \n" +
                    "-٣ مركز سفير للعيون والليزك \n" +
                    "-٣معامل رويال لاب \n" +
                    "-١ دلمار وعطالله");
        }
        else if(TroopName.equals("taleat el nesr")){
            allMessages.add("0128595**** it will help you."+"\n"+
                    "-٧ مطبعة العزازي \n" +
                    "-٣ رويال لاب \n" +
                    "-٤ صيدليات اورانج \n" +
                    "-٣ معامل الفا");
        }
        else if(TroopName.equals("taleat el nemr")){
            allMessages.add("0128595**** it will help you."+"\n"+
                    "-٤ رويال كلينيك \n" +
                    "-٧ دكتور مني رداميس \n" +
                    "-٤ تاون لاب \n" +
                    "-١ بريميير");
        }
        allMessages.add("A couple of messages will appear for 5 seconds each. Filter and find your code. Prepare yourself.");
        allMessages.add("Call 01286554730. The redhead girl will lead you.");
        allMessages.add("أحسب الوقت بين محطة العتبة ومحطة باب الشعرية. "+"\n"+"ملحوظة: متنزلش من المترو غير لما تعرف المفروض تروح على فين..");
        allMessages.add("Congratulations! You reached your destination.");



        fakeMessages.add("لا نستخدم علامات الترقيم "+"\n"+"خلى بالك من الأخطاء الإملائية "+"\n"+"الوقت مش كالسيف");
        fakeMessages.add("الله..الوطن..الحركة الكشفية، اول 3 ارقام من اشهر محل اكل");
        fakeMessages.add("عمرة رقم 23");
        fakeMessages.add("40 خطوة شمال قبل الساعة 5");
        fakeMessages.add("\"عدد افراد طليعتك\"");

        if(TroopName.equals("taleat el asad")){
            fakeMessages.add("عدد شبابيك عمارة رقم 40");
        }
        else if(TroopName.equals("taleat el fahd")){
            fakeMessages.add("عدد شبابيك عمارة رقم 38 بشارع نزيه خليفة");
        }
        else if(TroopName.equals("taleat el nesr")){
            fakeMessages.add("عدد شبابيك عمارة بنك الاتحاد الوطني");
        }
        else if(TroopName.equals("taleat el nemr")){
            fakeMessages.add("عدد شبابيك عمارة نور علي نور");
        }

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

//        displayMessage(allMessages.get(0) + " \nCode: 0");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

//        LatLng startLocation = new LatLng(30.100088, 31.373563); // kinisa
//        marker = gmap.addMarker(new MarkerOptions().position(startLocation).title("Start"));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        showCurrentLocationOnMap();


//        CircleOptions circleOptions = new CircleOptions()
//                .center(new LatLng(startLocation.latitude, startLocation.longitude))
//                .radius(RADIUS)
//                .fillColor(0x40ff0000)
//                .strokeColor(Color.TRANSPARENT)
//                .strokeWidth(2);
//
//        // Get back the mutable Circle
//        circle = gmap.addCircle(circleOptions);

//        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 15));
    }

    @Override
    public void onLocationChanged(Location currentLocation) {
//        Log.v("GPS", "location changed");
        if(circle != null){
            Location circleLocation = new Location("");
            circleLocation.setLongitude(circle.getCenter().longitude);
            circleLocation.setLatitude(circle.getCenter().latitude);
            double distanceToCircle = currentLocation.distanceTo(circleLocation);
            if (distanceToCircle <= RADIUS) {
                // change location geofence color
                circle.setFillColor(Color.GREEN);
                circle.setStrokeColor(Color.TRANSPARENT);
                Toast.makeText(MainActivity.this, "Entered zone", Toast.LENGTH_LONG).show();

                // call location action
                if(targetLocationIndex == 1 && !messageDisplayed){
                    // display the normal location message and the fake messages
                    messageDisplayed = true;
                    displayFakeMessages(allMessages.get(targetLocationIndex+1));
                }
                else if(targetLocationIndex == 3 && !messageDisplayed){
                    messageDisplayed = true;
                    displayTimeInputDialog(allMessages.get(targetLocationIndex+1));
                }
//                else if(targetLocationIndex == 4 && !messageDisplayed){
//                    // last location, do nothing
//                    messageDisplayed = true;
//                }
                else if(!messageDisplayed){
                    messageDisplayed = true;
                    displayMessage(allMessages.get(targetLocationIndex+1));
                }
            }
        }
    }

    private void displayFakeMessages(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Message!");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                autoDismissibleDialog("Rules", fakeMessages.get(0), 10000);
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void autoDismissibleDialog(String messageTitle, String message, final long messageTime){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle(messageTitle);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        CountDownTimer counter1 = new CountDownTimer(35000, 5000) {
            int interval = 0;
            int msgNum = 1;
            @Override
            public void onTick(long millisUntilFinished) {
                interval++;
                if(interval >= 3 && msgNum < fakeMessages.size()){
                    alertDialog.setTitle("Message "+msgNum);
                    alertDialog.setMessage(fakeMessages.get(msgNum));
                    msgNum++;
                }

            }

            @Override
            public void onFinish() {
                alertDialog.dismiss();
            }
        }.start();

    }

    private void displayTimeInputDialog(String message){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextTime = (EditText) dialogView.findViewById(R.id.editTextCode);

        dialogBuilder.setTitle("Insert time in seconds");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Insert", null);

        final AlertDialog timeDialog = dialogBuilder.create();

        timeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnInsert = timeDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnInsert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = editTextTime.getText().toString().toLowerCase();
                        boolean correctTime = false;

                        try{
                            int time = Integer.parseInt(text);
                            if(time > 0){ // TODO: time between 3ataba and bab el she3rya in Seconds
                                correctTime = true;
                            }
                        }
                        catch (NumberFormatException e){

                        }

                        if (!correctTime){
                            Toast.makeText(MainActivity.this, "Wrong time.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            timeDialog.dismiss();
                            displayMessage("Thank you for reaching this point. Please ask your leader about your final destination and DO NOT close the app.");
                        }
                    }
                });
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Message!");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                timeDialog.show();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void insertCodeDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextCode = (EditText) dialogView.findViewById(R.id.editTextCode);

        dialogBuilder.setTitle("Insert code");
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton("Insert", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String code = editTextCode.getText().toString().toLowerCase();
                boolean correctCode = false;

                for(int i=0; i<allCodes.size(); i++){
                    if (code.equals(allCodes.get(i))){
                       setupNewLocation(i);
                       correctCode = true;
                       if(i == 0){
                           displayMessage(allMessages.get(0));
                       }
                       break;
                    }
                }

                if (!correctCode){
                    Toast.makeText(MainActivity.this, "Wrong code.", Toast.LENGTH_LONG).show();
                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void setupNewLocation(int locationIndex){
        if(marker != null)
            marker.remove();

        if(circle != null)
            circle.remove();

        int locationNum = locationIndex+1;
        LatLng newLocation = allLocations.get(locationIndex);

        marker = gmap.addMarker(new MarkerOptions().position(newLocation).title("Location "+locationNum));
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(newLocation.latitude, newLocation.longitude))
                .radius(RADIUS)
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);

        // Get back the mutable Circle
        circle = gmap.addCircle(circleOptions);

        targetLocationIndex = locationIndex;
        messageDisplayed = false;

        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 17));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minimumTimeUpdate, minimumDistanceUpdate, (LocationListener) MainActivity.this);
    }

    private void showCurrentLocationOnMap() {
        // check if location services is enabled on the device
        if(!isLocationEnabled(this)) {
            Toast.makeText(MainActivity.this, "Enable device location", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        // check for app permission to access location
        if (!isLocationAccessPermitted()) {
            requestLocationAccessPermission();
        }

        // show current location on map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        gmap.setMyLocationEnabled(true);
    }

    private boolean isLocationAccessPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOC_PERM_REQ_CODE);
    }

    private boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void displayMessage(String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Message!");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Exit application?");
            alertDialogBuilder
//                    .setMessage("Click yes to exit!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // save application state


                                    // exit
                                    moveTaskToBack(true);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                }
                            })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
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
        if (id == R.id.action_about) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("About");
            alertDialogBuilder
                    .setMessage(getString(R.string.about_info))
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_insert_code) {
            insertCodeDialog();
        } else if (id == R.id.nav_log_out) {
            // TODO: save application state

            // log out
            LoginActivity.saveLoginState(false, "");
            MainActivity.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
