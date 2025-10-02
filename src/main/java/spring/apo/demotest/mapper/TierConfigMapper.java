package spring.apo.demotest.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import spring.apo.demotest.dto.request.TierConfigCreateRequest;
import spring.apo.demotest.dto.request.TierConfigUpdateRequest;
import spring.apo.demotest.dto.response.TierConfigResponse;
import spring.apo.demotest.entity.TierConfig;

@Mapper(componentModel = "spring")
public interface TierConfigMapper {
    @Mapping(target = "id", ignore = true)
    TierConfig toTierConfig(TierConfigCreateRequest request);

    TierConfigResponse toTierConfigResponse(TierConfig tierConfig);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tierName", ignore = true)
    void updateTierConfig(@MappingTarget TierConfig target,  TierConfigUpdateRequest request);
}
