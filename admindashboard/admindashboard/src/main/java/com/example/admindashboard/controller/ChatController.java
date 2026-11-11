package com.example.admindashboard.controller;

import com.example.admindashboard.model.ChatMessage;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.ChatMessageRepository;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // The "Mailman" for WebSockets

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. LOAD THE CHAT PAGE & CONTACTS
    @GetMapping("/chat")
    public String viewChatPage(Principal principal, Model model) {
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username).orElseThrow();
        
        System.out.println("USER = " + currentUser.getFullName());

        if(currentUser.getEmployeeProfile() != null){
            System.out.println("PROFILE DESIGNATION = " +
                    currentUser.getEmployeeProfile().getDesignation());
        }

        // Fetch internal colleagues (exclude self and external clients)
        List<User> empUsers = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .filter(u -> u.getUsername() != null && (u.getRole() == null || !"CLIENT".equalsIgnoreCase(u.getRole().getRoleName())))
                .collect(Collectors.toList());

        List<ContactInfo> colleagues = new ArrayList<>();
        // Variable to track the actual timestamp for sorting
        final Map<Long, LocalDateTime> lastTimestamps = new java.util.HashMap<>();

        // CRUNCH RICH DATA FOR EACH CONTACT
        for (User u : empUsers) {
            List<ChatMessage> history = chatMessageRepository.findConversationHistory(currentUser.getId(), u.getId());

            int unread = (int) history.stream()
                    .filter(m -> m.getRecipientId().equals(currentUser.getId()) && !m.isRead())
                    .count();

            String snippet = "New match! Say hello.";
            LocalDateTime rawTime = null;

            if (!history.isEmpty()) {
                ChatMessage lastMsg = history.get(history.size() - 1);
                rawTime = lastMsg.getTimestamp();

                // Truncate long snippets (e.g., limit to 30 chars)
                snippet = lastMsg.getContent().length() > 30 ?
                        lastMsg.getContent().substring(0, 28) + "..." :
                        lastMsg.getContent();
            }

            lastTimestamps.put(u.getId(), rawTime);
            String formattedTime = formatLastMessageTime(rawTime);

            colleagues.add(new ContactInfo(
                    u.getId(),
                    u.getUsername(),
                    u.getFullName(),
                    unread,
                    snippet,
                    formattedTime
            ));
        }

        // Sort colleagues by most recent message timestamp descending
        colleagues.sort((c1, c2) -> {
            LocalDateTime t1 = lastTimestamps.get(c1.getId());
            LocalDateTime t2 = lastTimestamps.get(c2.getId());

            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return t2.compareTo(t1);
        });

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("colleagues", colleagues);

        return "chat";
    }

    // 2. RECEIVE AND ROUTE WEBSOCKET MESSAGES
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setRead(false);

        ChatMessage saved = chatMessageRepository.save(chatMessage);

        // Forward to recipient
        messagingTemplate.convertAndSendToUser(
                String.valueOf(saved.getRecipientId()),
                "/queue/messages",
                saved
        );
    }

    // 3. FETCH HISTORICAL MESSAGES VIA REST
    @GetMapping("/chat/history/{contactId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable Long contactId, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username).orElseThrow();

        List<ChatMessage> history = chatMessageRepository.findConversationHistory(currentUser.getId(), contactId);

        // Mark incoming messages as read
        boolean needsSave = false;
        for (ChatMessage msg : history) {
            if (msg.getRecipientId().equals(currentUser.getId()) && !msg.isRead()) {
                msg.setRead(true);
                needsSave = true;
            }
        }
        if (needsSave) {
            chatMessageRepository.saveAll(history);
        }
        return ResponseEntity.ok(history);
    }

    // 4. SEARCH FOR COLLEAGUES
    @GetMapping("/chat/search")
    @ResponseBody
    public ResponseEntity<List<User>> searchColleagues(@RequestParam String query, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username).orElseThrow();

        // Search applies to internal colleagues
        List<User> searchResults = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .filter(u -> u.getUsername() != null && (u.getRole() == null || !"CLIENT".equalsIgnoreCase(u.getRole().getRoleName())))
                .filter(u -> {

                    String searchText = query.toLowerCase();

                    boolean usernameMatch =
                            u.getUsername() != null &&
                            u.getUsername().toLowerCase().contains(searchText);

                    boolean fullNameMatch =
                            u.getFullName() != null &&
                            u.getFullName().toLowerCase().contains(searchText);

                    return usernameMatch || fullNameMatch;
                })                
                .collect(Collectors.toList());

        return ResponseEntity.ok(searchResults);
    }

    // HELPER CLASS FOR THE SIDEBAR
    public static class ContactInfo {
        private Long id;
        private String username;
        private String fullName;
        private int unreadCount;
        private String lastMessageSnippet;
        private String formattedLastTime;

        public ContactInfo(Long id, String username, String fullName, int unreadCount, String lastMessageSnippet, String formattedLastTime) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.unreadCount = unreadCount;
            this.lastMessageSnippet = lastMessageSnippet;
            this.formattedLastTime = formattedLastTime;
        }

        // Standard Getters for all fields
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public int getUnreadCount() { return unreadCount; }
        public String getLastMessageSnippet() { return lastMessageSnippet; }
        public String getFormattedLastTime() { return formattedLastTime; }
    }

    private String formatLastMessageTime(LocalDateTime timestamp) {
        if (timestamp == null) return "";
        LocalDate msgDate = timestamp.toLocalDate();
        LocalDate today = LocalDate.now();

        if (msgDate.equals(today)) {
            // If today, show time: "10:30 AM"
            return timestamp.format(DateTimeFormatter.ofPattern("hh:mm a"));
        } else if (msgDate.equals(today.minusDays(1))) {
            return "Yesterday";
        } else if (msgDate.isAfter(today.minusWeeks(1))) {
            // Within a week, show day: "Mon"
            return timestamp.format(DateTimeFormatter.ofPattern("E"));
        } else {
            // Older, show full date: "28 Feb"
            return timestamp.format(DateTimeFormatter.ofPattern("dd MMM"));
        }
    }

}
