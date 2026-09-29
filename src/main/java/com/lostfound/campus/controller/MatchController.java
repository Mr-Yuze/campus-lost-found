package com.lostfound.campus.controller;

import com.lostfound.campus.dto.MatchDto;
import com.lostfound.campus.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<List<MatchDto>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @GetMapping("/lost/{lostReportId}")
    public ResponseEntity<List<MatchDto>> getMatchesForLostReport(@PathVariable Long lostReportId) {
        return ResponseEntity.ok(matchService.getMatchesForLostReport(lostReportId));
    }

    @GetMapping("/found/{foundItemId}")
    public ResponseEntity<List<MatchDto>> getMatchesForFoundItem(@PathVariable Long foundItemId) {
        return ResponseEntity.ok(matchService.getMatchesForFoundItem(foundItemId));
    }
}
