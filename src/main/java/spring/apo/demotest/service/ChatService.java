package spring.apo.demotest.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.ChatRequest;
import spring.apo.demotest.dto.response.TierConfigResponse;
import spring.apo.demotest.entity.TierConfig;
import spring.apo.demotest.exception.ChatServiceException;
import spring.apo.demotest.mapper.TierConfigMapper;
import spring.apo.demotest.repository.TierConfigRepository;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ChatService {

    // Constants
    private static final String DEFAULT_CONVERSATION_ID = "de_QLinh_bot";
    private static final String FILE_CONVERSATION_ID = "conversation2";
    private static final int MAX_CHAT_MESSAGES = 30;
    private static final double CHAT_TEMPERATURE = 0.0;
    private static final String SYSTEM_PROMPT_BASE = "You are Quang Linh's assistant and your name is Quinix.\nYou should answer humorously.\n\n";

    ChatClient chatClient;
    JdbcChatMemoryRepository chatMemoryRepository;
    TierConfigRepository tierConfigRepository;
    TierConfigMapper tierConfigMapper;

    public ChatService(
            ChatClient.Builder chatClientBuilder,
            JdbcChatMemoryRepository chatMemoryRepository,
            TierConfigRepository tierConfigRepository,
            TierConfigMapper tierConfigMapper) {
        this.chatMemoryRepository = chatMemoryRepository;

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(MAX_CHAT_MESSAGES)
                .build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

        this.tierConfigRepository = tierConfigRepository;
        this.tierConfigMapper = tierConfigMapper;
    }

    /**
     * Handle text-based chat with tier configuration context
     */
    public String chat(ChatRequest request) {
        try {
            String systemPrompt = buildSystemPrompt();
            SystemMessage systemMessage = new SystemMessage(systemPrompt);
            UserMessage userMessage = new UserMessage(request.getMessage());
            Prompt prompt = new Prompt(systemMessage, userMessage);

            return chatClient
                    .prompt(prompt)
                    .advisors(advisors -> advisors.param(ChatMemory.CONVERSATION_ID, DEFAULT_CONVERSATION_ID))
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("Error in chat method: {}", e.getMessage(), e);
            throw new ChatServiceException("Failed to process chat request", e);
        }
    }

    /**
     * Handle chat with image files
     */
    public String chatFile(List<MultipartFile> images, String request) {
        try {
            ChatOptions options = ChatOptions.builder()
                    .temperature(CHAT_TEMPERATURE)
                    .build();

            // Handle text-only case
            if (images == null || images.isEmpty()) {
                log.info("Processing text-only request: {}", request);
                return processTextOnlyChat(request, options);
            }

            // Handle images + text case
            log.info("Processing {} image(s) with text request", images.size());
            List<Media> medias = convertImagesToMedia(images);
            return processChatWithImages(medias, request, options);

        } catch (IOException e) {
            log.error("Error reading image files: {}", e.getMessage(), e);
            throw new ChatServiceException("Failed to read image files", e);
        } catch (Exception e) {
            log.error("Error in chatFile method: {}", e.getMessage(), e);
            throw new ChatServiceException("Failed to process chat with files", e);
        }
    }

    /**
     * Build system prompt with tier configurations
     */
    @Cacheable(value = "systemPrompts", key = "'tierConfig'")
    public String buildSystemPrompt() {
        List<TierConfig> entities = tierConfigRepository.findAll();
        List<TierConfigResponse> tierData = tierConfigMapper.toResponseList(entities);

        StringBuilder sb = new StringBuilder(SYSTEM_PROMPT_BASE);
        sb.append("Tier Configurations:\n");

        tierData.forEach(tier ->
                sb.append(String.format("- %s: Min=%d, Max=%d, Price=%.2f\n",
                        tier.getTierName(),
                        tier.getMinValue(),
                        tier.getMaxValue(),
                        tier.getPrice()))
        );

        return sb.toString();
    }

    /**
     * Process text-only chat request
     */
    private String processTextOnlyChat(String request, ChatOptions options) {
        return chatClient
                .prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, FILE_CONVERSATION_ID))
                .options(options)
                .system(SYSTEM_PROMPT_BASE)
                .user(request)
                .call()
                .content();
    }

    /**
     * Process chat with images
     */
    private String processChatWithImages(List<Media> medias, String request, ChatOptions options) {
        return chatClient
                .prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, FILE_CONVERSATION_ID))
                .options(options)
                .system(SYSTEM_PROMPT_BASE)
                .user(user -> {
                    medias.forEach(user::media);
                    user.text(request);
                })
                .call()
                .content();
    }

    /**
     * Convert multipart files to Media objects
     */
    private List<Media> convertImagesToMedia(List<MultipartFile> images) throws IOException {
        return images.stream()
                .filter(image -> image != null && !image.isEmpty())
                .map(image -> {
                    try {
                        return Media.builder()
                                .mimeType(MimeTypeUtils.parseMimeType(image.getContentType()))
                                .data(image.getBytes())
                                .build();
                    } catch (IOException e) {
                        log.error("Failed to convert image: {}", image.getOriginalFilename(), e);
                        throw new RuntimeException("Failed to convert image to media", e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Scheduled task to clear old chat history
     * Runs every 6 seconds (consider adjusting based on actual needs)
     */
    @Scheduled(fixedRate = 6000)
    public void clearOldChatHistory() {
        try {
            chatMemoryRepository.deleteByConversationId(FILE_CONVERSATION_ID);
            log.info("Chat history cleared for conversation: {}", FILE_CONVERSATION_ID);
        } catch (Exception e) {
            log.error("Failed to clear chat history: {}", e.getMessage(), e);
        }
    }
}