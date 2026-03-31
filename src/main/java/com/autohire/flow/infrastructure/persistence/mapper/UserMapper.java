package com.autohire.flow.infrastructure.persistence.mapper;

import com.autohire.flow.application.dto.response.AuthResponse;
import com.autohire.flow.domain.model.User;
import com.autohire.flow.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for User domain model and UserEntity
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    
    @Mapping(source = "email.value", target = "email")
    UserEntity toEntity(User user);
    
    @Mapping(source = "email", target = "email.value")
    User toDomain(UserEntity entity);
    
    @Mapping(source = "email.value", target = "email")
    AuthResponse toAuthResponse(User user);
}
