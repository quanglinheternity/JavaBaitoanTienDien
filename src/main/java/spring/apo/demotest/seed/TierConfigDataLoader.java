package spring.apo.demotest.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import spring.apo.demotest.entity.TierConfig;
import spring.apo.demotest.repository.TierConfigRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
public class TierConfigDataLoader implements CommandLineRunner {

    private final TierConfigRepository tierConfigRepository;

    public TierConfigDataLoader(TierConfigRepository tierConfigRepository) {
        this.tierConfigRepository = tierConfigRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (tierConfigRepository.count() == 0) { // chỉ seed khi bảng trống
            List<TierConfig> tiers = List.of(
                    createTier("Bậc 1", 0, 50, new BigDecimal("1500")),
                    createTier("Bậc 2", 51, 100, new BigDecimal("1800")),
                    createTier("Bậc 3", 101, 200, new BigDecimal("2500")),
                    createTier("Bậc 4", 201, 300, new BigDecimal("2700")),
                    createTier("Bậc 5", 301, 400, new BigDecimal("2900")),
                    createTier("Bậc 6", 401, null, new BigDecimal("3200")) // null = không giới hạn max
            );
            tierConfigRepository.saveAll(tiers);
            System.out.println("✅ Đã seed dữ liệu mẫu cho bảng TierConfig");
        }
    }
    private TierConfig createTier(String name, Integer min, Integer max, BigDecimal price) {
        TierConfig tier = new TierConfig();
        tier.setTierName(name);
        tier.setMinValue(min);
        tier.setMaxValue(max);
        tier.setPrice(price);
        return tier;
    }
}
