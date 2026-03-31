package com.autohire.flow.infrastructure.persistence.mapper;

import com.autohire.flow.application.dto.response.ResumeUploadResponse;
import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.infrastructure.persistence.entity.ResumeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Resume domain model and ResumeEntity
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ResumeMapper {
    
    ResumeEntity toEntity(Resume resume);
    
    Resume toDomain(ResumeEntity entity);
    
    @Mapping(target = "resumeId", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "parsedSkillsCount", expression = "java(resume.getSkills() != null ? resume.getSkills().size() : 0)")
    ResumeUploadResponse toUploadResponse(Resume resume);
}
