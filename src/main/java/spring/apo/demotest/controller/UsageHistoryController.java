package spring.apo.demotest.controller;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.UsageHistoryRequest;
import spring.apo.demotest.dto.response.ApiResponse;
import spring.apo.demotest.entity.UsageHistory;
import spring.apo.demotest.service.UsagerHistroryService;

@RestController
@RequestMapping("/usage-history")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UsageHistoryController {
    UsagerHistroryService usageService;
    @PostMapping("/usage")
    public ApiResponse<UsageHistory> addUsage(@RequestBody @Valid UsageHistoryRequest request) {
        log.info("addUsage");

        return ApiResponse.<UsageHistory>builder()
                .data(usageService.calculateAndSave(request))
                .build(); 
    }
    @GetMapping
    public ApiResponse<List<UsageHistory>> getAllUsage() {
        log.info("getAllUsage");
        return ApiResponse.<List<UsageHistory>>builder()
                .data(usageService.getAllUsageHistories())
                .build(); 
    }
    @GetMapping("/myUsage")
    public ApiResponse<List<UsageHistory>> getMyUsage() {
        log.info("getMyUsage");
        return ApiResponse.<List<UsageHistory>>builder()
                .data(usageService.getMyUsageHistories())
                .build(); 
    }
}
