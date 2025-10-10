package spring.apo.demotest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.ChatRequest;
import spring.apo.demotest.dto.response.BillItem;
import spring.apo.demotest.dto.response.ChatFilmInfo;
import spring.apo.demotest.dto.response.ExpenseInfo;
import spring.apo.demotest.service.ChatService;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ChatController {
    ChatService chatService;
    @PostMapping
    String chat(@RequestBody ChatRequest request){
        return chatService.chat(request);
    }
    @PostMapping("/image")
    List<BillItem> chatImage(@RequestParam(value = "image") MultipartFile image, @RequestParam(value = "message") String request){
        return chatService.chatFile(image,request);
    }

}
