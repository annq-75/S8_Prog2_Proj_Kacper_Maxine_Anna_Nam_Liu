package fr.uga.miashs.dciss.chatservice.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHistory {
    public static class MessageRecord {
        private final int fromId;      // 发送者ID（用户或群组）
        private final int destId;      // 接收者ID
        private final String content;  // 消息内容
        private final long timestamp;  // 时间戳

        public MessageRecord(int fromId, int destId, String content) {
            this.fromId = fromId;
            this.destId = destId;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }

        public int getFromId() { return fromId; }
        public int getDestId() { return destId; }
        public String getContent() { return content; }
        public long getTimestamp() { return timestamp; }
    }

    private final Map<Integer, List<MessageRecord>> historyMap = new ConcurrentHashMap<>();

    public void addMessage(int userId, MessageRecord record) {
        historyMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(record);
    }

    public List<MessageRecord> getUserHistory(int userId) {
        return historyMap.getOrDefault(userId, Collections.emptyList());
    }
}
