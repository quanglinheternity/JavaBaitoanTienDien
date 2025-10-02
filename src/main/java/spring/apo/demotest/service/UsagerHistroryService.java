package spring.apo.demotest.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import spring.apo.demotest.dto.request.UsageHistoryRequest;
import spring.apo.demotest.entity.AppUser;
import spring.apo.demotest.entity.TierConfig;
import spring.apo.demotest.entity.UsageHistory;
import spring.apo.demotest.exception.AppException;
import spring.apo.demotest.exception.ErrorCode;
import spring.apo.demotest.repository.TierConfigRepository;
import spring.apo.demotest.repository.UsageHistroryRepository;
import spring.apo.demotest.repository.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UsagerHistroryService {
    UsageHistroryRepository usageHistroryRepository;
    TierConfigRepository tierConfigRepository;
    UserRepository appUserRepository;
    @PreAuthorize("hasRole('ADMIN')") 
    public UsageHistory calculateAndSave(UsageHistoryRequest request) {
        AppUser user = appUserRepository.findById(request.getUserID())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<TierConfig> tierConfigs = tierConfigRepository.findAllByOrderByMinValueAsc();
        int kwh = request.getKwh();
        LocalDate date = LocalDate.parse(request.getDate()) ;
        BigDecimal total = BigDecimal.ZERO;
        int remaining = kwh;
         for (TierConfig tier : tierConfigs) {
            int tierMin = tier.getMinValue();
            int tierMax = tier.getMaxValue() != null ? tier.getMaxValue() : Integer.MAX_VALUE;
            int usageInTier = Math.min(remaining, tierMax - tierMin + 1);
            if (usageInTier > 0) {
                total = total.add(tier.getPrice().multiply(BigDecimal.valueOf(usageInTier)));
                remaining -= usageInTier;
            }
            if (remaining <= 0) break;
        }
        UsageHistory usage = UsageHistory.builder()
                .usageDate(date)
                .kwh(kwh)
                .amount(total)
                .user(user)
                .build();
        user.getUsageHistories().add(usage); 
        return usageHistroryRepository.save(usage);
    }
    @PreAuthorize("hasRole('ADMIN')") 
    public List<UsageHistory> getAllUsageHistories() {
        
        return usageHistroryRepository.findAll();
    }
    public List<UsageHistory> getMyUsageHistories() {
        // Lấy username hiện tại từ token
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Lấy AppUser từ DB
        AppUser currentUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return usageHistroryRepository.findAllByUserId(currentUser.getId());
    }
}
