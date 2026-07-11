package com.project.ongil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

public class MainActivity extends AppCompatActivity {

    private static final double MAP_LATITUDE = 37.50055;
    private static final double MAP_LONGITUDE = 127.06069;

    private String role;
    private TMapView tMapView;
    private TextView mapStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        role = getIntent().getStringExtra(LoginActivity.EXTRA_ROLE);
        if (role == null) {
            role = LoginActivity.ROLE_STUDENT;
        }

        initializeTmap();
        bindRoleContent();
        findViewById(R.id.locationShareButton).setOnClickListener(view -> openLocationSharing());
        findViewById(R.id.safeRouteButton).setOnClickListener(view -> openRoutePlans());
        findViewById(R.id.assistantButton).setOnClickListener(view ->
                startActivity(new Intent(this, AssistantActivity.class)));
        findViewById(R.id.tmapButton).setOnClickListener(view -> focusPickupZone());
        findViewById(R.id.placeSetupButton).setOnClickListener(view ->
                startActivity(new Intent(this, MapSetupActivity.class)));
    }

    private void initializeTmap() {
        mapStatusText = findViewById(R.id.mapStatusText);
        FrameLayout mapContainer = findViewById(R.id.mapContainer);
        tMapView = new TMapView(this);
        mapContainer.addView(tMapView, 0);
        tMapView.setOnTouchListener((view, event) -> {
            boolean mapGestureActive = event.getActionMasked() != MotionEvent.ACTION_UP
                    && event.getActionMasked() != MotionEvent.ACTION_CANCEL;
            view.getParent().requestDisallowInterceptTouchEvent(mapGestureActive);
            return false;
        });

        if (BuildConfig.TMAP_APP_KEY.isEmpty()) {
            mapStatusText.setText("TMAP 지도 키를 설정하면 실제 지도가 표시돼요.");
            mapStatusText.setVisibility(View.VISIBLE);
            return;
        }

        mapStatusText.setText("TMAP 지도를 불러오는 중이에요.");
        mapStatusText.setVisibility(View.VISIBLE);
        tMapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() {
                runOnUiThread(() -> mapStatusText.setVisibility(View.GONE));
            }

            @Override
            public void SKTMapApikeyFailed(String errorMessage) {
                runOnUiThread(() -> {
                    mapStatusText.setText("TMAP 지도 인증에 실패했어요. 키 설정을 확인해주세요.");
                    mapStatusText.setVisibility(View.VISIBLE);
                });
            }
        });
        tMapView.setSKTMapApiKey(BuildConfig.TMAP_APP_KEY);
        tMapView.setCenterPoint(MAP_LONGITUDE, MAP_LATITUDE);
        tMapView.setZoomLevel(16);
        addMarker("student", "학생", "학생 현재 위치", 37.50007, 127.06010, Color.rgb(46, 111, 242));
        addMarker("parent", "학부모", "학부모 현재 위치", 37.50078, 127.06120, Color.rgb(255, 145, 69));
        addMarker("pickup", "B", "B 픽업존 · 여유", MAP_LATITUDE, MAP_LONGITUDE, Color.rgb(76, 175, 107));
    }

    private void addMarker(String id, String letter, String title, double latitude,
                           double longitude, int color) {
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

    private void focusPickupZone() {
        if (BuildConfig.TMAP_APP_KEY.isEmpty()) {
            Toast.makeText(this, "local.properties에 TMAP_APP_KEY를 설정해주세요.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        tMapView.setCenterPoint(MAP_LONGITUDE, MAP_LATITUDE);
        tMapView.setZoomLevel(17);
        Toast.makeText(this, "B 픽업존을 지도 중심에 표시했어요.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TMAP 1.77 sets up its internal GL thread asynchronously after the API
        // key is validated, so onResume() can fire before that thread exists.
        if (tMapView != null) {
            try {
                tMapView.onResume();
            } catch (Exception ignored) {
                // GL surface not ready yet; nothing to resume.
            }
        }
    }

    @Override
    protected void onPause() {
        if (tMapView != null) {
            try {
                tMapView.onPause();
            } catch (Exception ignored) {
                // GL surface not ready yet; nothing to pause.
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

    private void bindRoleContent() {
        boolean isParent = LoginActivity.ROLE_PARENT.equals(role);
        TextView greeting = findViewById(R.id.greetingText);
        TextView roleBadge = findViewById(R.id.roleBadge);
        TextView statusTitle = findViewById(R.id.statusTitle);
        TextView statusBody = findViewById(R.id.statusBody);

        greeting.setText(isParent ? "오늘도 안전한 마중길이에요" : "오늘도 안전하게, 집으로");
        roleBadge.setText(isParent ? "학부모 모드" : "학생 모드");
        statusTitle.setText(isParent ? "B 픽업존이 가장 여유로워요" : "현재 위치를 확인했어요");
        statusBody.setText(isParent
                ? "학생 도보 4분 · 차량 혼잡도 낮음"
                : "픽업 요청을 기다리거나 안전 경로를 찾아보세요.");

        findViewById(R.id.tmapButton).setVisibility(isParent ? View.VISIBLE : View.GONE);
    }

    private void openLocationSharing() {
        Intent intent = new Intent(this, LocationShareActivity.class);
        intent.putExtra(LoginActivity.EXTRA_ROLE, role);
        startActivity(intent);
    }

    private void openRoutePlans() {
        startActivity(new Intent(this, RoutePlansActivity.class));
    }

}
