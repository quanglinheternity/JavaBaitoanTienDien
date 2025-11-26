package spring.apo.demotest.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import lombok.RequiredArgsConstructor;
import spring.apo.demotest.dto.request.ChatRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramBoot extends TelegramLongPollingBot {

    private final ChatService chatService;   // <-- Inject AI ChatService

    @Override
    public void onUpdateReceived(Update update) {

        if (!update.hasMessage()) return;

        String response = "";

        try {
            // Nếu user gửi TEXT
            if (update.getMessage().hasText()) {
                String userText = update.getMessage().getText();
                ChatRequest request = new ChatRequest(userText);

                response = chatService.chat(request);
            }

            // Nếu user gửi ảnh
            else if (update.getMessage().hasPhoto()) {

                var photos = update.getMessage().getPhoto();

                // Lấy ảnh lớn nhất
                String fileId = photos.get(photos.size() - 1).getFileId();

                // Download ảnh từ Telegram API
                byte[] imageBytes = downloadTelegramFile(fileId);

                // Convert sang MultipartFile (tuỳ chọn)
                MultipartFile image = new InMemoryMultipartFile(
                        "image.jpg", "image.jpg", "image/jpeg", imageBytes
                );

                response = chatService.chatFile(List.of(image), "Phân tích ảnh này giúp tôi").toString();
            }

        } catch (Exception e) {
            response = "Bot gặp lỗi rồi đại ca 😭: " + e.getMessage();
        }

        // Gửi về Telegram
        sendToTelegram(update.getMessage().getChatId(), response);
    }

    private void sendToTelegram(long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Tải file từ Telegram
    private byte[] downloadTelegramFile(String fileId) throws Exception {
        var file = execute(new org.telegram.telegrambots.meta.api.methods.GetFile(fileId));
        return downloadFileAsByteArray(file.getFilePath());
    }
    private byte[] downloadFileAsByteArray(String filePath) throws Exception {
        String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;

        try (java.io.InputStream in = new java.net.URL(fileUrl).openStream()) {
            return in.readAllBytes();
        }
    }


    @Override
    public String getBotUsername() {
        return "th1_nhom3_bot";
    }

    @Override
    public String getBotToken() {
        return "8361925806:AAFPh6X3fR7ifzHQSCq_95ndTQ5GUar04S0";
    }
}
