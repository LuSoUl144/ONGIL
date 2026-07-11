package com.project.ongil.ai;

public interface AiAssistantClient {

    void reply(String userMessage, Callback callback);

    interface Callback {
        void onReply(AssistantReply reply);
    }
}
