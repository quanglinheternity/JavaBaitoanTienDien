package spring.apo.demotest.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.TierConfigCreateRequest;
import spring.apo.demotest.dto.request.TierConfigUpdateRequest;
import spring.apo.demotest.dto.response.TierConfigResponse;
import spring.apo.demotest.entity.TierConfig;
import spring.apo.demotest.exception.AppException;
import spring.apo.demotest.exception.ErrorCode;
import spring.apo.demotest.mapper.TierConfigMapper;
import spring.apo.demotest.repository.TierConfigRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('ADMIN')") 
public class TierConfigService {
    TierConfigRepository tierConfigRepository;
    TierConfigMapper tierConfigMapper;
    public TierConfigResponse createTierConfig(TierConfigCreateRequest request) {
        TierConfig tierConfig = tierConfigMapper.toTierConfig(request);
        
       
         try {
             TierConfig savedTierConfig = tierConfigRepository.save(tierConfig);
            return tierConfigMapper.toTierConfigResponse(savedTierConfig);

        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.TIER_CONFIG_NAME_UNIQUE);
        }
        
    }
    public List<TierConfig> getAllTierConfig() {
        return tierConfigRepository.findAllByOrderByMinValueAsc();
    }
    public boolean deleteTierConfig(int id) {
        if (!tierConfigRepository.existsById(id)) return false;
        tierConfigRepository.deleteById(id);
        return true;
    }
    public TierConfig getTierConfig(int id) {
        return tierConfigRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.INVALID_ID_KEY));
    }
    public TierConfigResponse updateTierConfig(int id, TierConfigUpdateRequest request) {
        TierConfig tierConfig = tierConfigRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.INVALID_ID_KEY));
        log.info("Before save: {}", tierConfig);

        tierConfigMapper.updateTierConfig(tierConfig, request);
        TierConfig saved = tierConfigRepository.save(tierConfig);
        log.info("After save: {}", saved);
        return tierConfigMapper.toTierConfigResponse(saved);
    }
}
