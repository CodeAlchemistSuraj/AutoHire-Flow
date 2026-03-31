package com.autohire.flow.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Date and time utility methods.
 */
@Slf4j
@Component
public class DateUtil {
    
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    
    /**
     * Converts Instant to ISO date string.
     * @param instant instant to convert
     * @return ISO date string
     */
    public String formatInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        return date.format(ISO_DATE_FORMATTER);
    }
    
    /**
     * Checks if a date is in the past.
     * @param instant instant to check
     * @return true if in past, false otherwise
     */
    public boolean isPastDate(Instant instant) {
        return instant != null && instant.isBefore(Instant.now());
    }
    
    /**
     * Checks if a date is in the future.
     * @param instant instant to check
     * @return true if in future, false otherwise
     */
    public boolean isFutureDate(Instant instant) {
        return instant != null && instant.isAfter(Instant.now());
    }
    
    /**
     * Calculates days between two instants.
     * @param start start instant
     * @param end end instant
     * @return number of days
     */
    public long daysBetween(Instant start, Instant end) {
        return java.time.temporal.ChronoUnit.DAYS.between(start, end);
    }
}
