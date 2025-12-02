package spring.apo.demotest.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.ChatRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBoot extends TelegramLongPollingBot {

    private static final String BOT_USERNAME = "De_QLinh_bot";
    private static final String BOT_TOKEN = "8528156781:AAGF2VstDoP4T7sKr3dYIk5lwGvQpNw277Y";
    private static final int MAX_MESSAGE_LENGTH = 4096; // Telegram limit

    private final ChatService chatService;

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) return;

        String response = "";

        try {
            // Handle TEXT message
            if (update.getMessage().hasText()) {
                String userText = update.getMessage().getText();
                log.info("Received text message: {}", userText);

                ChatRequest request = new ChatRequest(userText);
                response = chatService.chat(request);
            }
            // Handle PHOTO message
            else if (update.getMessage().hasPhoto()) {
                log.info("Received photo message");

                var photos = update.getMessage().getPhoto();
                String fileId = photos.get(photos.size() - 1).getFileId();

                byte[] imageBytes = downloadTelegramFile(fileId);
                MultipartFile image = new InMemoryMultipartFile(
                        "image.jpg", "image.jpg", "image/jpeg", imageBytes
                );

                String caption = update.getMessage().getCaption();
                String prompt = (caption != null && !caption.isEmpty())
                        ? caption
                        : "Phân tích ảnh này giúp tôi";

                response = chatService.chatFile(List.of(image), prompt);
            }

            // Clean and send response
            response = cleanMarkdownForTelegram(response);

        } catch (Exception e) {
            log.error("Bot error: ", e);
            response = "Bot gặp lỗi rồi đại ca 😭: " + e.getMessage();
        }

        sendToTelegram(update.getMessage().getChatId(), response);
    }

    /**
     * Clean markdown formatting that causes display issues in Telegram
     */
    private String cleanMarkdownForTelegram(String text) {
        if (text == null || text.isEmpty()) {
            return "Quinix đang suy nghĩ... 🤔";
        }

        // Remove or escape problematic characters
        return text
                // Remove asterisks used for markdown bold
                .replace("*", "")
                // Remove underscores used for markdown italic
                .replace("_", "")
                // Remove backticks used for code formatting
                .replace("`", "'")
                // Normalize multiple newlines
                .replaceAll("\n{3,}", "\n\n")
                // Trim whitespace
                .trim();
    }

    /**
     * Send message to Telegram with proper formatting and length handling
     */
    private void sendToTelegram(long chatId, String text) {
        try {
            // Split long messages
            if (text.length() > MAX_MESSAGE_LENGTH) {
                sendLongMessage(chatId, text);
                return;
            }

            SendMessage msg = new SendMessage();
            msg.setChatId(chatId);
            msg.setText(text);
            // Disable markdown parsing to avoid formatting issues
            msg.disableWebPagePreview();

            execute(msg);
            log.info("Message sent successfully to chat: {}", chatId);

        } catch (TelegramApiException e) {
            log.error("Failed to send message to Telegram: ", e);
        }
    }

    /**
     * Split and send long messages
     */
    private void sendLongMessage(long chatId, String text) {
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + MAX_MESSAGE_LENGTH, text.length());

            // Try to break at newline
            if (end < text.length()) {
                int lastNewline = text.lastIndexOf('\n', end);
                if (lastNewline > start) {
                    end = lastNewline;
                }
            }

            String chunk = text.substring(start, end);
            sendToTelegram(chatId, chunk);
            start = end;

            // Small delay between messages
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Download file from Telegram servers
     */
    private byte[] downloadTelegramFile(String fileId) throws Exception {
        log.info("Downloading file with ID: {}", fileId);

        var file = execute(new org.telegram.telegrambots.meta.api.methods.GetFile(fileId));
        return downloadFileAsByteArray(file.getFilePath());
    }

    /**
     * Download file as byte array from URL
     */
    private byte[] downloadFileAsByteArray(String filePath) throws Exception {
        String fileUrl = String.format("https://api.telegram.org/file/bot%s/%s",
                getBotToken(), filePath);

        log.info("Downloading from URL: {}", fileUrl);

        try (java.io.InputStream in = new java.net.URL(fileUrl).openStream()) {
            return in.readAllBytes();
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}