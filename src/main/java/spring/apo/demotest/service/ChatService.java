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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.ChatRequest;
import spring.apo.demotest.dto.response.BillItem;
// import spring.apo.demotest.dto.response.ChatFilmInfo;
import spring.apo.demotest.dto.response.ExpenseInfo;
import spring.apo.demotest.dto.response.TierConfigResponse;
import spring.apo.demotest.dto.response.UsageHistoryResponse;
import spring.apo.demotest.entity.TierConfig;
import spring.apo.demotest.entity.UsageHistory;
import spring.apo.demotest.mapper.TierConfigMapper;
import spring.apo.demotest.mapper.UsageHistoryMapper;
import spring.apo.demotest.repository.TierConfigRepository;
import spring.apo.demotest.repository.UsageHistroryRepository;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ChatService {
    ChatClient chatClient;
    JdbcChatMemoryRepository chatMemoryRepository;
    TierConfigRepository tierConfigRepository;
    TierConfigMapper tierConfigMapper;
    UsagerHistroryService usageHistoryService ;
    UsageHistoryMapper usageHistoryMapper;

    public ChatService(ChatClient.Builder chatClientBuilder , JdbcChatMemoryRepository chatMemoryRepository , TierConfigRepository tierConfigRepository, TierConfigMapper tierConfigMapper, UsagerHistroryService usageHistoryService, UsageHistoryMapper usageHistoryMapper) {
        this.chatMemoryRepository = chatMemoryRepository;
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(30)
                .build();
        this.chatClient = chatClientBuilder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                        .build();
        this.tierConfigRepository = tierConfigRepository;
        this.tierConfigMapper = tierConfigMapper;
        this.usageHistoryService = usageHistoryService;
        this.usageHistoryMapper = usageHistoryMapper;

    }
    public String chat(ChatRequest request){
        String conversationId = "conversation2";
        List<UsageHistory> histories = usageHistoryService.getMyUsageHistories();

        List<UsageHistoryResponse> usageData = usageHistoryMapper.toResponses(histories);

        List<TierConfig> entities = tierConfigRepository.findAll();

        List<TierConfigResponse> tierData = tierConfigMapper.toResponseList(entities);

        StringBuilder sb = new StringBuilder();
        sb.append("You are Quang Linh's assistant and your name is Quinix.\n");
        sb.append("You should respond with a short, professional answer.\n\n");

        sb.append("Tier Configurations:\n");
        for (TierConfigResponse p : tierData) {
            sb.append("- ").append(p.getTierName())
            .append(": Min=").append(p.getMinValue())
            .append(", Max=").append(p.getMaxValue())
            .append(", Price=").append(p.getPrice())
            .append("\n");
        }

        sb.append("\n");

        sb.append("Electricity Usage History:\n");
        for (UsageHistoryResponse u : usageData) {
            sb.append("- ").append(u.getUsageDate())
            .append(": ").append(u.getKwh()).append(" kWh")
            .append(", Total=").append(u.getAmount())
            .append("\n");
        }


        SystemMessage systemMessage = new SystemMessage(sb.toString());
        UserMessage userMessage = new UserMessage(request.getMessage());
        
        Prompt prompt = new Prompt(systemMessage, userMessage);
        
        // return chatClient.prompt(prompt).call()
        //         .entity( new ParameterizedTypeReference<List<ChatFilmInfo>>() {});
        return chatClient.prompt(prompt)
                .advisors(advisors -> advisors.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
    public List<BillItem> chatFile(MultipartFile image, String request){
        try {
            Media media = Media.builder()
                    .mimeType(MimeTypeUtils.parseMimeType(image.getContentType()))
                    .data(image.getBytes())
                    .build();
            ChatOptions options = ChatOptions.builder()
                    .temperature(0D)
                    .build();   
                
            ;

            // return chatClient.prompt()
            //         .options(options)
            //         .system("You are Quang Linh's assistant and your name is Quinix.")
            //         .user(user -> user
            //                 .media(media)    
            //                 .text(request))
            //         .call()
            //         .content();
            return chatClient.prompt()
                    .options(options)
                    .system("You are Quang Linh's assistant and your name is Quinix.")
                    .user(user -> user
                            .media(media)
                            .text(request))
                    .call()
                    .entity(new ParameterizedTypeReference<List<BillItem>>() {});

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file ảnh: " + e.getMessage(), e);
        }
    }
}
