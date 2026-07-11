package com.project.ongil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.project.ongil.ai.AiAssistantClient;
import com.project.ongil.ai.AssistantAction;
import com.project.ongil.ai.AssistantReply;
import com.project.ongil.ai.MockAiAssistantClient;

public class AssistantActivity extends AppCompatActivity {

    private final AiAssistantClient assistantClient = new MockAiAssistantClient();
    private AssistantAction currentAction = AssistantAction.NONE;
    private TextView userMessage;
    private TextView assistantMessage;
    private MaterialButton actionButton;
    private EditText messageInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);

        userMessage = findViewById(R.id.userMessage);
        assistantMessage = findViewById(R.id.assistantMessage);
        actionButton = findViewById(R.id.assistantActionButton);
        messageInput = findViewById(R.id.messageInput);

        findViewById(R.id.backButton).setOnClickListener(view -> finish());
        findViewById(R.id.pickupQuickButton).setOnClickListener(view -> askAssistant("오늘은 부모님이 픽업 와."));
        findViewById(R.id.soloQuickButton).setOnClickListener(view -> askAssistant("오늘은 혼자 집에 갈게."));
        findViewById(R.id.sendButton).setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                askAssistant(message);
                messageInput.setText("");
            }
        });
        actionButton.setOnClickListener(view -> runCurrentAction());
    }

    private void askAssistant(String message) {
        userMessage.setText(message);
        userMessage.setVisibility(View.VISIBLE);
        assistantClient.reply(message, this::renderReply);
    }

    private void renderReply(AssistantReply reply) {
        assistantMessage.setText(reply.getMessage());
        currentAction = reply.getAction();
        actionButton.setText(reply.getActionLabel());
        actionButton.setVisibility(currentAction == AssistantAction.NONE ? View.GONE : View.VISIBLE);
    }

    private void runCurrentAction() {
        if (currentAction == AssistantAction.OPEN_PICKUP_ZONES) {
            startActivity(new Intent(this, PickupZoneActivity.class));
        } else if (currentAction == AssistantAction.OPEN_SAFE_ROUTES) {
            startActivity(new Intent(this, RoutePlansActivity.class));
        } else if (currentAction == AssistantAction.REQUEST_LOCATION_SHARE) {
            Intent intent = new Intent(this, LocationShareActivity.class);
            intent.putExtra(LoginActivity.EXTRA_ROLE, LoginActivity.ROLE_PARENT);
            startActivity(intent);
        }
    }
}
