package com.autohire.flow.infrastructure.persistence.mapper;

import com.autohire.flow.domain.model.JobPosting;
import com.autohire.flow.infrastructure.persistence.entity.JobPostingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for JobPosting domain model and JobPostingEntity
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface JobPostingMapper {
    
    JobPostingEntity toEntity(JobPosting jobPosting);
    
    JobPosting toDomain(JobPostingEntity entity);
}
