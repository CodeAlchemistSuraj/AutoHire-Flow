package com.autohire.flow.infrastructure.persistence.mapper;

import com.autohire.flow.application.dto.response.ResumeUploadResponse;
import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.infrastructure.persistence.entity.ResumeEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * MapStruct mapper for Resume domain model and ResumeEntity
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ResumeMapper {
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    @Mapping(target = "skills", source = "skills", qualifiedByName = "listToString")
    @Mapping(target = "experiences", source = "experiences", qualifiedByName = "experiencesToString")
    @Mapping(target = "educations", source = "educations", qualifiedByName = "educationsToString")
    @Mapping(target = "projects", source = "projects", qualifiedByName = "listToString")
    @Mapping(target = "embedding", source = "embedding", qualifiedByName = "floatArrayToString")
    public abstract ResumeEntity toEntity(Resume resume);
    
    @Mapping(target = "skills", source = "skills", qualifiedByName = "stringToList")
    @Mapping(target = "experiences", source = "experiences", qualifiedByName = "stringToExperiences")
    @Mapping(target = "educations", source = "educations", qualifiedByName = "stringToEducations")
    @Mapping(target = "projects", source = "projects", qualifiedByName = "stringToList")
    @Mapping(target = "embedding", source = "embedding", qualifiedByName = "stringToFloatArray")
    public abstract Resume toDomain(ResumeEntity entity);
    
    @Mapping(target = "resumeId", source = "id")
    @Mapping(target = "parsedSkillsCount", expression = "java(resume.getSkills() != null ? resume.getSkills().size() : 0)")
    @Mapping(target = "status", expression = "java(\"SUCCESS\")")
    public abstract ResumeUploadResponse toUploadResponse(Resume resume);
    
    @Named("listToString")
    protected String listToString(List<String> list) {
        if (list == null) return "[]";
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
    
    @Named("stringToList")
    protected List<String> stringToList(String json) {
        if (json == null || json.isEmpty()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
    
    @Named("experiencesToString")
    protected String experiencesToString(List<Resume.Experience> experiences) {
        if (experiences == null) return "[]";
        try {
            return objectMapper.writeValueAsString(experiences);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
    
    @Named("stringToExperiences")
    protected List<Resume.Experience> stringToExperiences(String json) {
        if (json == null || json.isEmpty()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<Resume.Experience>>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
    
    @Named("educationsToString")
    protected String educationsToString(List<Resume.Education> educations) {
        if (educations == null) return "[]";
        try {
            return objectMapper.writeValueAsString(educations);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
    
    @Named("stringToEducations")
    protected List<Resume.Education> stringToEducations(String json) {
        if (json == null || json.isEmpty()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<Resume.Education>>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
    
    @Named("floatArrayToString")
    protected String floatArrayToString(float[] array) {
        if (array == null) return "[]";
        try {
            return objectMapper.writeValueAsString(array);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
    
    @Named("stringToFloatArray")
    protected float[] stringToFloatArray(String json) {
        if (json == null || json.isEmpty()) return new float[0];
        try {
            return objectMapper.readValue(json, float[].class);
        } catch (JsonProcessingException e) {
            return new float[0];
        }
    }
}