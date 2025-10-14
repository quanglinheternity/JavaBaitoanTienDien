package spring.apo.demotest.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import spring.apo.demotest.dto.request.UserCreateRequest;
import spring.apo.demotest.dto.request.UserUpdateRequest;
import spring.apo.demotest.dto.response.UserResponse;
import spring.apo.demotest.dto.response.UserResponseHaspassword;
import spring.apo.demotest.entity.AppUser;


@Mapper(componentModel = "spring", uses = {UsageHistoryMapper.class})
public interface UserMapper {
    @Mapping(target = "usageHistories", source = "usageHistories")
    UserResponse toUserResponse(AppUser user);

    @Mapping(target = "usageHistories", source = "usageHistories")
    UserResponseHaspassword toUserResponseHaspassword(AppUser user);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usageHistories", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "verified", ignore = true)
    AppUser toUser(UserCreateRequest request);  

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usageHistories", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "verified", ignore = true)
    void updateAppUserFromRequest(UserUpdateRequest request, @MappingTarget AppUser user);

    
}
