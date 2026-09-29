package com.lostfound.campus.controller;

import com.lostfound.campus.dto.FoundItemDto;
import com.lostfound.campus.entity.FoundStatus;
import com.lostfound.campus.service.FoundItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/found-items")
public class FoundItemController {

    private final FoundItemService foundItemService;

    public FoundItemController(FoundItemService foundItemService) {
        this.foundItemService = foundItemService;
    }

    @PostMapping
    public ResponseEntity<FoundItemDto> createFoundItem(@Valid @RequestBody FoundItemDto dto, java.security.Principal principal) {
        return new ResponseEntity<>(foundItemService.createFoundItem(dto, principal.getName()), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FoundItemDto>> getAllFoundItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) FoundStatus status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(foundItemService.getAllFoundItems(category, status, location, keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoundItemDto> getFoundItemById(@PathVariable Long id) {
        return ResponseEntity.ok(foundItemService.getFoundItemById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoundItemDto> updateFoundItem(@PathVariable Long id, @Valid @RequestBody FoundItemDto dto) {
        return ResponseEntity.ok(foundItemService.updateFoundItem(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoundItem(@PathVariable Long id) {
        foundItemService.deleteFoundItem(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/claim")
    public ResponseEntity<FoundItemDto> claimFoundItem(@PathVariable Long id, java.security.Principal principal) {
        return ResponseEntity.ok(foundItemService.claimItem(id, principal.getName()));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<FoundItemDto> returnFoundItem(@PathVariable Long id) {
        return ResponseEntity.ok(foundItemService.returnItem(id));
    }
}
