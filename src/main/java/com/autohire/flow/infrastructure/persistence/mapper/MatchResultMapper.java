package com.autohire.flow.infrastructure.persistence.mapper;

import com.autohire.flow.application.dto.response.MatchScoreResponse;
import com.autohire.flow.domain.model.MatchResult;
import com.autohire.flow.infrastructure.persistence.entity.MatchResultEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for MatchResult domain model and MatchResultEntity
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MatchResultMapper {
    
    @Mapping(target = "matchingSkills", expression = "java(listToString(matchResult.getMatchingSkills()))")
    @Mapping(target = "missingSkills", expression = "java(listToString(matchResult.getMissingSkills()))")
    MatchResultEntity toEntity(MatchResult matchResult);
    
    @Mapping(target = "matchingSkills", expression = "java(stringToList(entity.getMatchingSkills()))")
    @Mapping(target = "missingSkills", expression = "java(stringToList(entity.getMissingSkills()))")
    MatchResult toDomain(MatchResultEntity entity);
    
    MatchScoreResponse toScoreResponse(MatchResult matchResult);
    
    // Custom mapping methods
    default String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(",", list);
    }
    
    default List<String> stringToList(String str) {
        if (str == null || str.isBlank()) {
            return List.of();
        }
        return Arrays.stream(str.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
    }
}