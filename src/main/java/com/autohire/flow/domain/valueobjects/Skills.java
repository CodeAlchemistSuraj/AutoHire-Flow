package com.autohire.flow.domain.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Value object for skills collection
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skills {
    
    private String skillsList;  // Comma-separated or JSON serialized
    
    public static Skills of(Set<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return Skills.builder().skillsList("").build();
        }
        String serialized = String.join(",", skills);
        return Skills.builder().skillsList(serialized).build();
    }
    
    public Set<String> asSet() {
        if (skillsList == null || skillsList.trim().isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(List.of(skillsList.split(",")));
    }
    
    public List<String> asList() {
        if (skillsList == null || skillsList.trim().isEmpty()) {
            return List.of();
        }
        return List.of(skillsList.split(","));
    }
}
