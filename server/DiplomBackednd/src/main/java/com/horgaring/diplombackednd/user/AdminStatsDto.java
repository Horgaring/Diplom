package com.horgaring.diplombackednd.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDto {
    private long totalUsers;
    private long activeUsers;
    private long verifiedUsers;
    private long bannedUsers;
    private long totalMatches;
    private long totalMessages;
    private long registrationsToday;
    private long registrationsThisWeek;
    private long registrationsThisMonth;
    private Map<String, Long> genderDistribution;
    private Map<String, Long> topCities;

    @Data
    @AllArgsConstructor
    public static class GenderEntry {
        private String key;
        private Long value;
    }

    @Data
    @AllArgsConstructor
    public static class CityEntry {
        private String key;
        private Long value;
    }

    public List<GenderEntry> getGenderEntries() {
        if (genderDistribution == null) return Collections.emptyList();
        return genderDistribution.entrySet().stream()
                .map(e -> new GenderEntry(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<CityEntry> getCityEntries() {
        if (topCities == null) return Collections.emptyList();
        return topCities.entrySet().stream()
                .map(e -> new CityEntry(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
