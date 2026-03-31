package com.autohire.flow.infrastructure.persistence.mapper;

import com.autohire.flow.application.dto.response.MatchScoreResponse;
import com.autohire.flow.domain.model.MatchResult;
import com.autohire.flow.infrastructure.persistence.entity.MatchResultEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for MatchResult domain model and MatchResultEntity
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MatchResultMapper {
    
    MatchResultEntity toEntity(MatchResult matchResult);
    
    MatchResult toDomain(MatchResultEntity entity);
    
    MatchScoreResponse toScoreResponse(MatchResult matchResult);
}
