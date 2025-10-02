package spring.apo.demotest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.TierConfigCreateRequest;
import spring.apo.demotest.dto.request.TierConfigUpdateRequest;
import spring.apo.demotest.dto.response.ApiResponse;
import spring.apo.demotest.dto.response.TierConfigResponse;
import spring.apo.demotest.entity.TierConfig;
import spring.apo.demotest.service.TierConfigService;

@RestController
@RequestMapping("/tier-config")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TierConfigController {

    TierConfigService tierConfigService;

    @PostMapping
    ApiResponse<TierConfigResponse> createTierConfig(@RequestBody @Valid TierConfigCreateRequest request) {
        log.info("createTierConfig");
        return ApiResponse.<TierConfigResponse>builder()
            .data(tierConfigService.createTierConfig(request))
            .build()
        ; 
    }
    @GetMapping
    ApiResponse<List<TierConfig>> getAllTierConfig() {
        return ApiResponse.<List<TierConfig>>builder()
            .data(tierConfigService.getAllTierConfig())
            .build()
        ; 
    }
    @GetMapping("{id}")
    ApiResponse<TierConfig> getTierConfig(@PathVariable int id) {
        return ApiResponse.<TierConfig>builder()
            .data(tierConfigService.getTierConfig(id))
            .build()
        ;
    }
    @DeleteMapping("{id}")
    ApiResponse<Void> deleteTierConfig(@PathVariable int id) {
        log.info("deleteTierConfig");
        
        boolean result =  tierConfigService.deleteTierConfig(id);

        return ApiResponse.<Void>builder()
            .code(result ? 1000 : 1001)
            .message(result ? "success" : "fail")
            .build()
        ;
       
    }
    @PostMapping("{id}")
    ApiResponse<TierConfigResponse> updateTierConfig(@PathVariable int id, @RequestBody @Valid TierConfigUpdateRequest request) {
        log.info("updateTierConfig");
        return ApiResponse.<TierConfigResponse>builder()
            .data(tierConfigService.updateTierConfig(id, request))
            .build()
        ;
    }
}

