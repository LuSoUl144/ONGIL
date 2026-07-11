package com.project.ongil.ai;

import java.util.Locale;

public class MockAiAssistantClient implements AiAssistantClient {

    @Override
    public void reply(String userMessage, Callback callback) {
        String normalized = userMessage.toLowerCase(Locale.KOREAN);

        if (containsAny(normalized, "픽업", "데리러", "부모님", "엄마", "아빠")) {
            callback.onReply(new AssistantReply(
                    "좋아, 학원 앞보다 덜 붐비는 픽업존을 찾아볼게. B존이 지금 가장 여유로워.",
                    AssistantAction.OPEN_PICKUP_ZONES,
                    "픽업존 비교하기"));
        } else if (containsAny(normalized, "혼자", "걸어", "귀가", "집에 갈")) {
            callback.onReply(new AssistantReply(
                    "혼자 가는 날이구나. 조금 더 걸리더라도 밝은 길과 안전 거점이 있는 경로를 비교해줄게.",
                    AssistantAction.OPEN_SAFE_ROUTES,
                    "안전 경로 보기"));
        } else if (containsAny(normalized, "위치", "공유", "어디")) {
            callback.onReply(new AssistantReply(
                    "상대방이 수락한 경우에만 귀가 시간 동안 위치를 공유할 수 있어.",
                    AssistantAction.REQUEST_LOCATION_SHARE,
                    "위치 공유 요청"));
        } else {
            callback.onReply(new AssistantReply(
                    "오늘은 부모님과 픽업존에서 만날지, 혼자 안전 경로로 갈지 알려줘.",
                    AssistantAction.NONE,
                    ""));
        }
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
