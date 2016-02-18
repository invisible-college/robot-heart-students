package college.invisible.robothello;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    MapView mMapView;
    SavedPointsHelper mSavedPointsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSavedPointsHelper = new SavedPointsHelper(this);
        mSavedPointsHelper.saveNewPoint(47.6097, -122.3331);
        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.setStyleUrl(Style.MAPBOX_STREETS);
        mMapView.setCenterCoordinate(new LatLng(-33.856898, 151.2130919));
        mMapView.setZoomLevel(11);
        mMapView.onCreate(savedInstanceState);
        mMapView.setMyLocationEnabled(true);
        showAllSavedPoints();
        Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Location location =  mMapView.getMyLocation();
            mMapView.setCenterCoordinate(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        });

        final Button buttonSave = (Button) findViewById(R.id.saveLocationButton);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location myLocation = mMapView.getMyLocation();
                addPointToMap(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                mSavedPointsHelper.saveNewPoint(myLocation.getLatitude(), myLocation.getLongitude());
            }
        });

    }

    private void addPointToMap(LatLng locationToAdd) {
        String latLngAsString = locationToAdd.getLatitude() + "," + locationToAdd.getLongitude();
        mMapView.addMarker(new MarkerOptions()
                .position(new LatLng(locationToAdd.getLatitude(), locationToAdd.getLongitude()))
                .title(latLngAsString));
    }

    private void showAllSavedPoints() {
        List<LatLng> savedPoints = mSavedPointsHelper.getSavedPoints();
        for (LatLng place : savedPoints ) {
            addPointToMap(place);
        }
    }

}
