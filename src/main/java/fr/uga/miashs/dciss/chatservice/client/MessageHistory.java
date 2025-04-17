package fr.uga.miashs.dciss.chatservice.client;

import java.io.*;
import java.util.*;

public class MessageHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class MessageRecord implements Serializable {
        private static final long serialVersionUID = 1L;
        int senderId;
        int receiverId;
        String message;
        Date timestamp;

        public MessageRecord(int senderId, int receiverId, String message) {
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.message = message;
            this.timestamp = new Date();  // 当前时间
        }

        @Override
        public String toString() {
            return "[" + timestamp + "] " + senderId + " -> " + receiverId + ": " + message;
        }
    }

    private List<MessageRecord> allMessages = new ArrayList<>();

    public void addMessage(int senderId, int receiverId, String message) {
        allMessages.add(new MessageRecord(senderId, receiverId, message));
    }

    public List<MessageRecord> getConversation(int userId) {
        List<MessageRecord> conversation = new ArrayList<>();
        for (MessageRecord record : allMessages) {
            if (record.senderId == userId || record.receiverId == userId) {
                conversation.add(record);
            }
        }
        // 排序：时间升序
        conversation.sort(Comparator.comparing(r -> r.timestamp));
        return conversation;
    }

    public void saveToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MessageHistory loadFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return new MessageHistory();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (MessageHistory) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new MessageHistory();
        }
    }
}

//package fr.uga.miashs.dciss.chatservice.client;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class MessageHistory {
//    public static class MessageRecord {
//        private final int fromId;      // 发送者ID（用户或群组）
//        private final int destId;      // 接收者ID
//        private final String content;  // 消息内容
//        private final long timestamp;  // 时间戳
//
//        public MessageRecord(int fromId, int destId, String content) {
//            this.fromId = fromId;
//            this.destId = destId;
//            this.content = content;
//            this.timestamp = System.currentTimeMillis();
//        }
//
//        public int getFromId() { return fromId; }
//        public int getDestId() { return destId; }
//        public String getContent() { return content; }
//        public long getTimestamp() { return timestamp; }
//    }
//
//    private final Map<Integer, List<MessageRecord>> historyMap = new ConcurrentHashMap<>();
//
//    public void addMessage(int userId, MessageRecord record) {
//        historyMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(record);
//    }
//
//    public List<MessageRecord> getUserHistory(int userId) {
//        return historyMap.getOrDefault(userId, Collections.emptyList());
//    }
//}
