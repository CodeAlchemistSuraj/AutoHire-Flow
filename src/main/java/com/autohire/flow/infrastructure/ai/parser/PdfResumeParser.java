package com.autohire.flow.infrastructure.ai.parser;

import com.autohire.flow.domain.exception.ResumeParseException;
import com.autohire.flow.domain.model.Resume;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PDF resume parser - extracts text and structured data
 */
@Component
@Slf4j
public class PdfResumeParser {
    
    private static final Set<String> COMMON_SKILLS = Set.of(
        "java", "python", "javascript", "typescript", "c++", "c#", "go", "rust", "kotlin", "scala",
        "spring", "spring-boot", "spring-cloud", "hibernate", "jpa",
        "react", "angular", "vue", "node.js", "express", "nestjs",
        "sql", "postgresql", "mysql", "mongodb", "redis", "elasticsearch",
        "docker", "kubernetes", "jenkins", "git", "github", "gitlab",
        "aws", "azure", "gcp", "terraform", "cloudformation",
        "microservices", "rest", "grpc", "graphql", "soap",
        "junit", "mockito", "testng", "selenium", "cypress",
        "maven", "gradle", "npm", "gradle", "ant",
        "agile", "scrum", "kanban", "jira", "confluence"
    );
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "\\b(?:\\+?1[-.]?)?\\(?([0-9]{3})\\)?[-.]?([0-9]{3})[-.]?([0-9]{4})\\b"
    );
    
    /**
     * Parse PDF resume and extract text content
     */
    public ParsedResumeData parse(byte[] pdfContent) {
        try {
            String extractedText = extractTextFromPdf(pdfContent);
            log.debug("Successfully extracted text from PDF, length: {}", extractedText.length());
            
            ParsedResumeData.ParsedResumeDataBuilder builder = ParsedResumeData.builder()
                .rawText(extractedText);
            
            // Extract contact information
            extractContactInfo(extractedText, builder);
            
            // Extract skills
            Set<String> skills = extractSkills(extractedText);
            builder.skills(new ArrayList<>(skills));
            
            // Extract experiences
            List<Resume.Experience> experiences = extractExperiences(extractedText);
            builder.experiences(experiences);
            
            // Extract education
            String education = extractEducation(extractedText);
            builder.education(education);
            
            return builder.build();
            
        } catch (IOException e) {
            log.error("Failed to parse PDF resume: {}", e.getMessage(), e);
            throw new ResumeParseException("Failed to parse PDF: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error parsing resume: {}", e.getMessage(), e);
            throw new ResumeParseException("Unexpected parsing error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract text from PDF using PDFBox
     */
    private String extractTextFromPdf(byte[] pdfContent) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(pdfContent);
        PDDocument document = PDDocument.load(bais);
        
        try {
            if (document.isEncrypted()) {
                log.warn("PDF is encrypted, attempting to read anyway");
            }
            
            PDFTextStripper textStripper = new PDFTextStripper();
            String text = textStripper.getText(document);
            
            if (text.trim().isEmpty()) {
                throw new ResumeParseException("PDF contains no extractable text");
            }
            
            return text;
        } finally {
            document.close();
        }
    }
    
    /**
     * Extract contact information (email, phone)
     */
    private void extractContactInfo(String text, ParsedResumeData.ParsedResumeDataBuilder builder) {
        // Extract email
        Matcher emailMatcher = EMAIL_PATTERN.matcher(text);
        if (emailMatcher.find()) {
            builder.email(emailMatcher.group());
        }
        
        // Extract phone
        Matcher phoneMatcher = PHONE_PATTERN.matcher(text);
        if (phoneMatcher.find()) {
            builder.phone(phoneMatcher.group());
        }
    }
    
    /**
     * Extract technical skills from resume text
     */
    private Set<String> extractSkills(String text) {
        Set<String> foundSkills = new HashSet<>();
        String lowerText = text.toLowerCase();
        
        for (String skill : COMMON_SKILLS) {
            if (lowerText.contains(skill)) {
                foundSkills.add(formatSkill(skill));
            }
        }
        
        log.debug("Extracted {} skills from resume", foundSkills.size());
        return foundSkills;
    }
    
    /**
     * Extract work experience information
     */
    private List<Resume.Experience> extractExperiences(String text) {
        List<Resume.Experience> experiences = new ArrayList<>();
        
        // Simple pattern matching for common job markers
        String[] lines = text.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            // Look for common experience indicators
            if (isJobTitleLine(line)) {
                Resume.Experience.ExperienceBuilder expBuilder = Resume.Experience.builder()
                    .title(line.replaceAll("(?i)(at|@|-)", "").trim());
                
                // Look for company name in next lines
                if (i + 1 < lines.length) {
                    String nextLine = lines[i + 1].trim();
                    if (!isDateLine(nextLine)) {
                        expBuilder.company(nextLine);
                    }
                }
                
                experiences.add(expBuilder.build());
            }
        }
        
        log.debug("Extracted {} work experiences from resume", experiences.size());
        return experiences;
    }
    
    /**
     * Extract education information
     */
    private String extractEducation(String text) {
        String[] lines = text.split("\n");
        StringBuilder education = new StringBuilder();
        boolean inEducation = false;
        
        for (String line : lines) {
            String lowerLine = line.toLowerCase();
            
            // Detect education section
            if (lowerLine.contains("education") || lowerLine.contains("academic")) {
                inEducation = true;
                continue;
            }
            
            // Stop at next major section
            if (inEducation && (lowerLine.contains("experience") || lowerLine.contains("skills"))) {
                break;
            }
            
            if (inEducation && !line.trim().isEmpty()) {
                education.append(line).append(" ");
            }
        }
        
        String result = education.toString().trim();
        log.debug("Extracted education: {}", result.substring(0, Math.min(100, result.length())));
        return result;
    }
    
    /**
     * Check if line appears to be a job title
     */
    private boolean isJobTitleLine(String line) {
        String lowerLine = line.toLowerCase();
        String[] jobTitleKeywords = {
            "engineer", "developer", "architect", "manager", "analyst", "specialist",
            "consultant", "lead", "senior", "junior", "principal", "director"
        };
        
        return Arrays.stream(jobTitleKeywords).anyMatch(lowerLine::contains);
    }
    
    /**
     * Check if line appears to be a date
     */
    private boolean isDateLine(String line) {
        return line.matches(".*\\d{4}.*") || 
               line.toLowerCase().contains("present") ||
               line.toLowerCase().matches(".*(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
    }
    
    /**
     * Format skill name
     */
    private String formatSkill(String skill) {
        return Arrays.stream(skill.split("-|_"))
            .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
            .reduce((a, b) -> a + " " + b)
            .orElse(skill);
    }
}
