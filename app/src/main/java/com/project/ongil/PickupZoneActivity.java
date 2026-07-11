package com.project.ongil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PickupZoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_zone);

        findViewById(R.id.backButton).setOnClickListener(view -> finish());
        findViewById(R.id.shareFromPickupButton).setOnClickListener(view -> {
            Intent intent = new Intent(this, LocationShareActivity.class);
            intent.putExtra(LoginActivity.EXTRA_ROLE, LoginActivity.ROLE_PARENT);
            startActivity(intent);
        });
        findViewById(R.id.startTmapButton).setOnClickListener(view ->
                Toast.makeText(this, "B 픽업존 좌표를 TMAP으로 전달할 예정이에요.", Toast.LENGTH_SHORT).show());
        findViewById(R.id.zoneAButton).setOnClickListener(view -> showZoneMessage("A존은 현재 혼잡해요."));
        findViewById(R.id.zoneBButton).setOnClickListener(view -> showZoneMessage("B존을 만남 장소로 선택했어요."));
        findViewById(R.id.zoneCButton).setOnClickListener(view -> showZoneMessage("C존은 보통 혼잡도예요."));
    }

    private void showZoneMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
