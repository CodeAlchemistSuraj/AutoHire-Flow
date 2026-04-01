package com.autohire.flow.infrastructure.persistence.mapper;

import com.autohire.flow.application.dto.response.AuthResponse;
import com.autohire.flow.domain.model.User;
import com.autohire.flow.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.*;

/**
 * MapStruct mapper for User domain model and UserEntity
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    
    UserEntity toEntity(User user);
    
    User toDomain(UserEntity entity);
    
    AuthResponse toAuthResponse(User user);
}