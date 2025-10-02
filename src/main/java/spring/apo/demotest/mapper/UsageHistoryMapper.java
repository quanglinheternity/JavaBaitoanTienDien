package spring.apo.demotest.mapper;

import org.mapstruct.Mapper;

import spring.apo.demotest.dto.response.UsageHistoryResponse;
import spring.apo.demotest.entity.UsageHistory;

@Mapper(componentModel = "spring")
public interface UsageHistoryMapper {
    UsageHistoryResponse toResponse(UsageHistory usageHistory);
}