package com.project.ongil;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.Locale;

public class MapSetupActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ongil_places";
    private static final String HOME_LAT = "home_lat";
    private static final String HOME_LON = "home_lon";
    private static final String ACADEMY_LAT = "academy_lat";
    private static final String ACADEMY_LON = "academy_lon";
    private static final double DEFAULT_LATITUDE = 37.50055;
    private static final double DEFAULT_LONGITUDE = 127.06069;

    private TMapView tMapView;
    private TextView mapStatusText;
    private TextView savedPlacesText;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_setup);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        mapStatusText = findViewById(R.id.placeMapStatusText);
        savedPlacesText = findViewById(R.id.savedPlacesText);

        configureBottomSheet();
        initializeMap();
        refreshSavedPlaces();

        findViewById(R.id.placeBackButton).setOnClickListener(view -> finish());
        findViewById(R.id.saveHomeButton).setOnClickListener(view -> saveCurrentCenter(true));
        findViewById(R.id.saveAcademyButton).setOnClickListener(view -> saveCurrentCenter(false));
    }

    private void configureBottomSheet() {
        LinearLayout bottomSheet = findViewById(R.id.placeBottomSheet);
        BottomSheetBehavior<LinearLayout> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setHideable(false);
        behavior.setFitToContents(true);
        behavior.setPeekHeight(dpToPx(132));
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void initializeMap() {
        FrameLayout mapContainer = findViewById(R.id.placeMapContainer);
        tMapView = new TMapView(this);
        mapContainer.addView(tMapView, 0);

        if (BuildConfig.TMAP_APP_KEY.isEmpty()) {
            showMapStatus("TMAP 지도 키를 설정하면 장소를 지정할 수 있어요.");
            return;
        }

        showMapStatus("TMAP 지도를 불러오는 중이에요.");
        tMapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() {
                runOnUiThread(() -> mapStatusText.setVisibility(View.GONE));
            }

            @Override
            public void SKTMapApikeyFailed(String errorMessage) {
                runOnUiThread(() -> showMapStatus("TMAP 지도 인증에 실패했어요. 네트워크와 키를 확인해주세요."));
            }
        });
        tMapView.setSKTMapApiKey(BuildConfig.TMAP_APP_KEY);
        tMapView.setCenterPoint(DEFAULT_LONGITUDE, DEFAULT_LATITUDE);
        tMapView.setZoomLevel(16);
        showSavedMarkers();
    }

    private void saveCurrentCenter(boolean home) {
        if (BuildConfig.TMAP_APP_KEY.isEmpty()) {
            Toast.makeText(this, "TMAP 키를 먼저 설정해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        TMapPoint center = tMapView.getCenterPoint();
        if (center == null) {
            Toast.makeText(this, "지도가 준비될 때까지 잠시 기다려주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String latitudeKey = home ? HOME_LAT : ACADEMY_LAT;
        String longitudeKey = home ? HOME_LON : ACADEMY_LON;
        preferences.edit()
                .putString(latitudeKey, Double.toString(center.getLatitude()))
                .putString(longitudeKey, Double.toString(center.getLongitude()))
                .apply();

        addPlaceMarker(home ? "saved_home" : "saved_academy",
                home ? "집" : "학원",
                home ? "H" : "A",
                center.getLatitude(),
                center.getLongitude(),
                home ? Color.rgb(46, 111, 242) : Color.rgb(255, 145, 69));
        refreshSavedPlaces();
        Toast.makeText(this, home ? "집 위치를 저장했어요." : "학원 위치를 저장했어요.",
                Toast.LENGTH_SHORT).show();
    }

    private void showSavedMarkers() {
        if (preferences.contains(HOME_LAT) && preferences.contains(HOME_LON)) {
            addPlaceMarker("saved_home", "집", "H",
                    readDouble(HOME_LAT), readDouble(HOME_LON), Color.rgb(46, 111, 242));
        }
        if (preferences.contains(ACADEMY_LAT) && preferences.contains(ACADEMY_LON)) {
            addPlaceMarker("saved_academy", "학원", "A",
                    readDouble(ACADEMY_LAT), readDouble(ACADEMY_LON), Color.rgb(255, 145, 69));
        }
    }

    private void addPlaceMarker(String id, String title, String letter,
                                double latitude, double longitude, int color) {
        try {
            tMapView.removeMarkerItem(id);
        } catch (Exception ignored) {
            // The marker has not been added yet.
        }
        TMapMarkerItem marker = new TMapMarkerItem();
        marker.setTMapPoint(new TMapPoint(latitude, longitude));
        marker.setName(title);
        marker.setCalloutTitle(title);
        marker.setCanShowCallout(true);
        marker.setIcon(createMarkerBitmap(letter, color));
        tMapView.addMarkerItem(id, marker);
    }

    private Bitmap createMarkerBitmap(String letter, int color) {
        int size = 76;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4f, paint);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(30f);
        paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float baseline = size / 2f - (metrics.ascent + metrics.descent) / 2f;
        canvas.drawText(letter, size / 2f, baseline, paint);
        return bitmap;
    }

    private void refreshSavedPlaces() {
        String home = preferences.contains(HOME_LAT)
                ? String.format(Locale.KOREA, "집  %.5f, %.5f", readDouble(HOME_LAT), readDouble(HOME_LON))
                : "집  아직 지정하지 않음";
        String academy = preferences.contains(ACADEMY_LAT)
                ? String.format(Locale.KOREA, "학원  %.5f, %.5f", readDouble(ACADEMY_LAT), readDouble(ACADEMY_LON))
                : "학원  아직 지정하지 않음";
        savedPlacesText.setText(home + "\n" + academy);
    }

    private double readDouble(String key) {
        return Double.parseDouble(preferences.getString(key, "0"));
    }

    private void showMapStatus(String message) {
        mapStatusText.setText(message);
        mapStatusText.setVisibility(View.VISIBLE);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tMapView != null) {
            try {
                tMapView.onResume();
            } catch (Exception ignored) {
                // TMAP 1.77 GL renderer is initialized asynchronously.
            }
        }
    }

    @Override
    protected void onPause() {
        if (tMapView != null) {
            try {
                tMapView.onPause();
            } catch (Exception ignored) {
                // GL renderer is not ready yet.
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (tMapView != null) {
            try {
                tMapView.destroy();
            } catch (Exception ignored) {
                // Nothing to release.
            }
        }
        super.onDestroy();
    }
}
