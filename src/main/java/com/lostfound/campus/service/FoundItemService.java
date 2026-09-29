package com.lostfound.campus.service;

import com.lostfound.campus.dto.FoundItemDto;
import com.lostfound.campus.entity.FoundItem;
import com.lostfound.campus.entity.FoundStatus;
import com.lostfound.campus.entity.LostReport;
import com.lostfound.campus.entity.LostStatus;
import com.lostfound.campus.entity.Role;
import com.lostfound.campus.entity.User;
import com.lostfound.campus.exception.BusinessRuleException;
import com.lostfound.campus.exception.ResourceNotFoundException;
import com.lostfound.campus.repository.FoundItemRepository;
import com.lostfound.campus.repository.LostReportRepository;
import com.lostfound.campus.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoundItemService {

    private final FoundItemRepository foundItemRepository;
    private final UserRepository userRepository;
    private final MatchService matchService;
    private final LostReportRepository lostReportRepository;

    public FoundItemService(FoundItemRepository foundItemRepository, UserRepository userRepository, MatchService matchService, LostReportRepository lostReportRepository) {
        this.foundItemRepository = foundItemRepository;
        this.userRepository = userRepository;
        this.matchService = matchService;
        this.lostReportRepository = lostReportRepository;
    }

    @Transactional
    public FoundItemDto createFoundItem(FoundItemDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        FoundItem item = new FoundItem();
        item.setItemName(dto.getItemName());
        item.setCategory(dto.getCategory());
        item.setDescription(dto.getDescription());
        item.setLocationFound(dto.getLocationFound());
        item.setDateFound(dto.getDateFound());
        item.setReportedBy(user);
        
        if (dto.getLinkedLostReportId() != null) {
            LostReport lr = lostReportRepository.findById(dto.getLinkedLostReportId()).orElse(null);
            item.setLinkedLostReport(lr);
        }
        
        FoundItem savedItem = foundItemRepository.save(item);
        
        matchService.findMatchesForFoundItem(savedItem);
        
        return mapToDto(savedItem);
    }

    public FoundItemDto getFoundItemById(Long id) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + id));
        return mapToDto(item);
    }

    public List<FoundItemDto> getAllFoundItems(String category, FoundStatus status, String location, String keyword) {
        List<FoundItem> items = foundItemRepository.searchFoundItems(category, status, location, keyword);
        return items.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public FoundItemDto updateFoundItem(Long id, FoundItemDto dto) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + id));
        
        item.setItemName(dto.getItemName());
        item.setCategory(dto.getCategory());
        item.setDescription(dto.getDescription());
        item.setLocationFound(dto.getLocationFound());
        item.setDateFound(dto.getDateFound());
        
        if (dto.getStatus() != null && dto.getStatus() != item.getStatus()) {
            if (item.getStatus() == FoundStatus.AVAILABLE && dto.getStatus() == FoundStatus.CLAIMED) {
                // allow
            } else if (item.getStatus() == FoundStatus.CLAIMED && dto.getStatus() == FoundStatus.RETURNED) {
                // allow
            } else {
                throw new BusinessRuleException("Invalid status transition from " + item.getStatus() + " to " + dto.getStatus());
            }
            item.setStatus(dto.getStatus());
        }
        
        FoundItem updatedItem = foundItemRepository.save(item);
        return mapToDto(updatedItem);
    }

    public void deleteFoundItem(Long id) {
        if (!foundItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Found item not found with id: " + id);
        }
        foundItemRepository.deleteById(id);
    }

    @Transactional
    public FoundItemDto claimItem(Long itemId, String email) {
        FoundItem item = foundItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + itemId));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (item.getStatus() != FoundStatus.AVAILABLE) {
            throw new BusinessRuleException("Only AVAILABLE items can be claimed.");
        }

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.STAFF && !item.getReportedBy().getId().equals(user.getId())) {
            throw new BusinessRuleException("Only an admin, staff or the reporting user can claim this found item.");
        }

        item.setStatus(FoundStatus.CLAIMED);
        if (item.getLinkedLostReport() != null) {
            item.getLinkedLostReport().setStatus(LostStatus.CLAIMED);
            lostReportRepository.save(item.getLinkedLostReport());
        }
        return mapToDto(foundItemRepository.save(item));
    }

    @Transactional
    public FoundItemDto returnItem(Long itemId) {
        FoundItem item = foundItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + itemId));

        if (item.getStatus() != FoundStatus.CLAIMED) {
            throw new BusinessRuleException("A found item must be claimed before it can be returned.");
        }

        item.setStatus(FoundStatus.RETURNED);
        if (item.getLinkedLostReport() != null) {
            item.getLinkedLostReport().setStatus(LostStatus.RESOLVED);
            lostReportRepository.save(item.getLinkedLostReport());
        }
        return mapToDto(foundItemRepository.save(item));
    }

    public FoundItemDto mapToDto(FoundItem item) {
        FoundItemDto dto = new FoundItemDto();
        dto.setId(item.getId());
        dto.setItemName(item.getItemName());
        dto.setCategory(item.getCategory());
        dto.setDescription(item.getDescription());
        dto.setLocationFound(item.getLocationFound());
        dto.setDateFound(item.getDateFound());
        dto.setStatus(item.getStatus());
        dto.setReportedById(item.getReportedBy().getId());
        dto.setReportedByEmail(item.getReportedBy().getEmail());
        dto.setCreatedAt(item.getCreatedAt());
        if (item.getLinkedLostReport() != null) {
            dto.setLinkedLostReportId(item.getLinkedLostReport().getId());
        }
        return dto;
    }
}
