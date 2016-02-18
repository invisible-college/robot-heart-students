# Robot Map 2 (Robot Heart Session 6)

# Finishing up from Robot Session 1

We got a map working that plots the users current location and then we added a button to pan to the users location. It works when the users location is available but in the case that it is not it crashes. Lets first fix this bug and then move on to the new material.

# Goals

- Learn how to use SQLite to save data to the device
- Save geographical data to the device and render it on a map
- Get Google Maps SDK set up to display a Google map within the Android app
- *Stretch goal:* Change the Google Map baselayer to pull from a OpenStreetMap data source rather than Google


# Table of Contents

1. [Displaying saved data on the map](displaying-saved-data-on-the-map)
2. [Basic setup of Google Maps SDK](#basic-setup-for-google-maps-sdk)
3. [Map data](#map-data)
4. [Going further](#going-further)

### Displaying saved data on the map

We are going to enable the user to save pins to track their location as they move. So we will first create a button that, upon being tapped, drops a pin on the map at the users current location (the pin will also display the latitude and longitude of the given location). Since we will do this on the Mapbox Map, we add the button XML to `activity_mapbox_map.xml`:

```xml
<Button
    android:id="@+id/saveLocationButton"
    android:layout_height="wrap_content"
    android:layout_width="wrap_content"
    android:text="Save My Location" />
```

Next we add a listener in the onCreate method of `MapboxMapActivity.java`:

```java
final Button button = (Button) findViewById(R.id.saveLocationButton);
button.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v) {
        Location myLocation = mapView.getMyLocation();
        addPointToMap(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
    }
});
```

The code for the addPointToMap method, which adds a marker on the Mapbox map, is:

```java
private void addPointToMap(LatLng locationToAdd) {
    String latLngAsString = locationToAdd.getLatitude() + "," + locationToAdd.getLongitude();
    mapView.addMarker(new MarkerOptions()
            .position(new LatLng(locationToAdd.getLatitude(), locationToAdd.getLongitude()))
            .title(latLngAsString));
}
```

So far the user has the ability to drop pins as their location changes but if they quit the app all this data will be lost (the points are only stored in thus far RAM). We want to save these locations persistently so that means saving it to local storage on the device. There are [various options](http://developer.android.com/guide/topics/data/data-storage.html) to save data with Android, in this case we want our data to be structured so the best option is use to [SQLite](http://developer.android.com/training/basics/data-storage/databases.html).

To save user data persistently (and then retrieve at any time to display on a map) we will write lat/lng points when the user taps the 'Save My Location' button. To do this cleanly, we can create a helper called `SavedPointsHelper.java` which manages all the interaction with the database:

```java
public class SavedPointsHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "robotmap";

    private static final int DATABASE_VERSION = 1;
    private static final String POINTS_TABLE_NAME = "points";

    private static final String POINT_ID = "point_id";
    private static final int POINT_ID_INDEX = 0;
    private static final String POINT_LAT = "point_lat";
    private static final int POINT_LAT_INDEX = 1;
    private static final String POINT_LNG = "point_lng";
    private static final int POINT_LNG_INDEX = 2;

    private static final String POINTS_TABLE_CREATE =
            "CREATE TABLE " + POINTS_TABLE_NAME + "("
                    + POINT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + POINT_LAT + " REAL,"
                    + POINT_LNG + " REAL" + ")";

    public SavedPointsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(POINTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + POINTS_TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void saveNewPoint(final double lat, final double lng) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(POINT_LAT, lat);
        values.put(POINT_LNG, lng);
        db.insert(POINTS_TABLE_NAME, null, values);
        db.close();
    }

    public List<LatLng> getSavedPoints() {
        List<LatLng> points = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + POINTS_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LatLng point = getPointFromDbRow(cursor);
                points.add(point);
            } while (cursor.moveToNext());
        }
        db.close();
        return points;
    }

    private LatLng getPointFromDbRow(Cursor cursor) {
        double lat = cursor.getDouble(POINT_LAT_INDEX);
        double lng = cursor.getDouble(POINT_LNG_INDEX);
        return new LatLng(lat, lng);
    }
}
```

In the onCreate method of `MapboxMapActivity.java` we can initialize the helper with `savedPointsHelper = new SavedPointsHelper(this);`. Then to display all saved points we create this method, which we can call in onCreate or anywhere else we like:

```java
private void showAllSavedPoints() {
    // Implement this
}
```

Finally lets add a line to the button's onClickListener to actually save the lat/lng point of the user's current location to the SQLite database.


### Basic setup for Google Maps SDK

The process of getting a Google Map set up is similar to that described above for the Mapbox SDK. The first step is to use Android Studio to create a new Google Maps Activity by going to File > New > Activity > Google > Google Maps Activity. Next you must get a Google API Key by following these steps:

1. Copy the link provided in the `google_maps_api.xml` file and paste it into your browser. The link takes you to the Google Developers Console and supplies information via URL parameters, thus reducing the manual input required from you.
2. Follow the instructions to create a new project on the console or select an existing project.
3. Create an Android API key for your console project.
4. Copy the resulting API key, go back to Android Studio, and paste the API key into the <string> element in the google_maps_api.xml file. 

It is nifty that Android Studio automatically generates the code required for the Google Map. Noticed what code was changed:

1. Added Google Play Services as a dependency in `app/build.gradle`
2. READ_GSERVICES as a new user permission in the `AndroidManifest.xml`
3. `GoogleMapsActivity.java` and `activity_google_maps.xml`
4. Two `google_maps_api.xml` files (one storing the development/debug API Key and another for the release version which you dont yet need to fill in)

The code generated by Android Studio in `GoogleMapsActivity.java` should be replaced by this code:

```java
package org.invisiblecollege.robotmap;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Center the map on Seattle, WA
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(47.605967, -122.334539)));
    }
}
```

This set up the map centered on Seattle, WA.

More detailed information on getting set up with the Google Maps SDK is in the [Getting Started guide here](https://developers.google.com/maps/documentation/android-api/start). 

Now lets view the map! But first we must change the default Activity from the MapboxMapActivity to the GoogleMapActivity by moving the intent filter code from inside the MapboxMapActivity XML block to that for the GoogleMapsActivity. So now the code in `looks like this in `AndroidManifest.xml`.

```xml
<activity
    android:name=".GoogleMapsActivity"
    android:label="@string/title_activity_google_maps" >
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<activity
    android:name=".MapboxMapActivity"
    android:label="@string/app_name" >
</activity>
```

Run the application and you should see the Google Map that looks very similar to the Mapbox one but with the Google logo in the bottom left corner.


### Map data

So far there is only one data source powering each map in our app. It is the tile layer provided by the Mapbox API called Mapbox Streets in the Mapbox and Google Maps API for the Google Map. In the case of Mapbox Streets the data is stored as vector tiles meaning the map data is stored as a vectors rather than a rendered image (traditional web map format). You can read about this [open specification here](https://github.com/mapbox/vector-tile-spec). Map data is more limited in vector tile format but this is a more efficient means of displaying maps because less data is downloaded from the server (vector data is smaller than image data).  The latest Mapbox SDK (2.2) only allows using vector tile data. The Google Maps SDK, however, supports using image tiles so next we will show how to hook up the app to display Open Street Map (OSM) tiles hosted for free by the OSM community. 

The OSM community maintains a [list of tile servers here](http://wiki.openstreetmap.org/wiki/Tile_servers). To emphasize how much map styling can very lets hook up outdoor-focused map tiles that includes topographic and trail data from 'Thunderforest Outdoors'. You just need to copy one of the tile URLs from that column for the Thunderforest Outdoors (any of the servers a, b, or c will work), so lets use a: `http://a.tile.thunderforest.com/outdoors/${z}/${x}/${y}.png`. ${z}, ${x}, and ${y} each represent the coordinates of a given map tile which is just a normal raster image. To add this as a tile source for the Google Map we use a [TileOverlay](https://developers.google.com/maps/documentation/android-api/tileoverlay). 

Add this code to the onMapReady method in `GoogleMapActivity.java` to have the map render the OSM tiles:

```java
// Clears default Google Map tiles
map.setMapType(GoogleMap.MAP_TYPE_NONE);

TileProvider tileProvider = new UrlTileProvider(256, 256) {
    @Override
    public synchronized URL getTileUrl(int x, int y, int zoom) {
        String s = String.format("http://a.tile.thunderforest.com/outdoors/%d/%d/%d.png", zoom, x, y);
        URL url = null;
        try {
            url = new URL(s);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
        return url;
    }
};
// Adds the OSM map tiles
map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
```

Another cool set of map tiles are those maintained by the [Humanitarian OSM Team](https://hotosm.org/), also linked on the OSM wiki of tile sources. As you can see the community did a lot of work mapping important services in response to the catastrophic earthquake in Nepal. Preview [those tiles here](http://www.openstreetmap.org/relation/4583125#map=13/27.7013/85.3332&layers=H). 

Its worth noting that Google Map tiles are not fully open source, you are only allowed to use their tiles with their SDKs. Mapbox, on the other hand, provides direct access to all of their fully open source map tiles. You can also use tools like [Mapbox Studio](https://www.mapbox.com/mapbox-studio/) to create custom-styled map tiles.


### Going further

Allow the user to save an image from built-in camera and associate the picture with a point. Refer to this guide to use the [built-in camera](http://developer.android.com/training/camera/photobasics.html) to take photots and save them for display on the map. You will need to find a way to link the point data in the SQLite database with the saved image file (hint: each record in the SQLite database table is assigned a unique ID `point_id`).


