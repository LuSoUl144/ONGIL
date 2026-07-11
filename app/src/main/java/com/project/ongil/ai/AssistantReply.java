package com.project.ongil.ai;

public class AssistantReply {

    private final String message;
    private final AssistantAction action;
    private final String actionLabel;

    public AssistantReply(String message, AssistantAction action, String actionLabel) {
        this.message = message;
        this.action = action;
        this.actionLabel = actionLabel;
    }

    public String getMessage() {
        return message;
    }

    public AssistantAction getAction() {
        return action;
    }

    public String getActionLabel() {
        return actionLabel;
    }
}
