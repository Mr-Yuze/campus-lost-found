package com.lostfound.campus.service;

import com.lostfound.campus.dto.MatchDto;
import com.lostfound.campus.entity.FoundItem;
import com.lostfound.campus.entity.FoundStatus;
import com.lostfound.campus.entity.ItemMatch;
import com.lostfound.campus.entity.LostReport;
import com.lostfound.campus.entity.LostStatus;
import com.lostfound.campus.repository.FoundItemRepository;
import com.lostfound.campus.repository.ItemMatchRepository;
import com.lostfound.campus.repository.LostReportRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final ItemMatchRepository itemMatchRepository;
    private final LostReportRepository lostReportRepository;
    private final FoundItemRepository foundItemRepository;

    public MatchService(ItemMatchRepository itemMatchRepository,
                        @Lazy LostReportRepository lostReportRepository,
                        @Lazy FoundItemRepository foundItemRepository) {
        this.itemMatchRepository = itemMatchRepository;
        this.lostReportRepository = lostReportRepository;
        this.foundItemRepository = foundItemRepository;
    }

    public void findMatchesForLostReport(LostReport lostReport) {
        List<FoundItem> availableFoundItems = foundItemRepository.findByStatus(FoundStatus.AVAILABLE);
        for (FoundItem foundItem : availableFoundItems) {
            calculateAndSaveMatch(lostReport, foundItem);
        }
    }

    public void findMatchesForFoundItem(FoundItem foundItem) {
        List<LostReport> reportedLostItems = lostReportRepository.findByStatus(LostStatus.REPORTED);
        for (LostReport lostReport : reportedLostItems) {
            calculateAndSaveMatch(lostReport, foundItem);
        }
    }

    private void calculateAndSaveMatch(LostReport lostReport, FoundItem foundItem) {
        int score = 0;

        if (lostReport.getCategory() != null && lostReport.getCategory().equalsIgnoreCase(foundItem.getCategory())) {
            score += 30;
        }

        if (lostReport.getItemName() != null && foundItem.getItemName() != null &&
            (lostReport.getItemName().toLowerCase().contains(foundItem.getItemName().toLowerCase()) ||
             foundItem.getItemName().toLowerCase().contains(lostReport.getItemName().toLowerCase()))) {
            score += 30;
        }

        if (lostReport.getDescription() != null && foundItem.getDescription() != null &&
            !lostReport.getDescription().isEmpty() &&
            (lostReport.getDescription().toLowerCase().contains(foundItem.getDescription().toLowerCase()) ||
             foundItem.getDescription().toLowerCase().contains(lostReport.getDescription().toLowerCase()))) {
            score += 20;
        }

        if (lostReport.getLocationLost() != null && foundItem.getLocationFound() != null &&
            lostReport.getLocationLost().equalsIgnoreCase(foundItem.getLocationFound())) {
            score += 10;
        }

        if (lostReport.getDateLost() != null && foundItem.getDateFound() != null) {
            long daysBetween = Math.abs(ChronoUnit.DAYS.between(lostReport.getDateLost(), foundItem.getDateFound()));
            if (daysBetween <= 7) {
                score += 10;
            }
        }

        if (score >= 60) {
            ItemMatch match = new ItemMatch();
            match.setLostReport(lostReport);
            match.setFoundItem(foundItem);
            match.setMatchScore(score);
            match.setMatched(true);
            itemMatchRepository.save(match);
        }
    }

    public List<MatchDto> getAllMatches() {
        return itemMatchRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<MatchDto> getMatchesForLostReport(Long lostReportId) {
        return itemMatchRepository.findByLostReportId(lostReportId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<MatchDto> getMatchesForFoundItem(Long foundItemId) {
        return itemMatchRepository.findByFoundItemId(foundItemId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private MatchDto mapToDto(ItemMatch match) {
        MatchDto dto = new MatchDto();
        dto.setId(match.getId());
        dto.setLostReportId(match.getLostReport().getId());
        dto.setFoundItemId(match.getFoundItem().getId());
        dto.setMatchScore(match.getMatchScore());
        dto.setMatched(match.getMatched());
        dto.setLostItemName(match.getLostReport().getItemName());
        dto.setFoundItemName(match.getFoundItem().getItemName());
        return dto;
    }
}
