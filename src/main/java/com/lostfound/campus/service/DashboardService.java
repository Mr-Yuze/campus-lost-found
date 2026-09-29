package com.lostfound.campus.service;

import com.lostfound.campus.dto.DashboardDto;
import com.lostfound.campus.entity.FoundStatus;
import com.lostfound.campus.entity.Role;
import com.lostfound.campus.repository.FoundItemRepository;
import com.lostfound.campus.repository.ItemMatchRepository;
import com.lostfound.campus.repository.LostReportRepository;
import com.lostfound.campus.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final LostReportRepository lostReportRepository;
    private final FoundItemRepository foundItemRepository;
    private final ItemMatchRepository itemMatchRepository;

    public DashboardService(UserRepository userRepository, 
                            LostReportRepository lostReportRepository, 
                            FoundItemRepository foundItemRepository,
                            ItemMatchRepository itemMatchRepository) {
        this.userRepository = userRepository;
        this.lostReportRepository = lostReportRepository;
        this.foundItemRepository = foundItemRepository;
        this.itemMatchRepository = itemMatchRepository;
    }

    public DashboardDto getDashboardStats() {
        DashboardDto dto = new DashboardDto();
        dto.setTotalUsers(userRepository.count());
        dto.setTotalStudents(userRepository.countByRole(Role.STUDENT));
        dto.setTotalStaff(userRepository.countByRole(Role.STAFF));
        dto.setTotalAdmins(userRepository.countByRole(Role.ADMIN));
        
        dto.setTotalLostReports(lostReportRepository.count());
        dto.setTotalFoundItems(foundItemRepository.count());
        
        dto.setAvailableItems(foundItemRepository.findByStatus(FoundStatus.AVAILABLE).size());
        dto.setClaimedItems(foundItemRepository.findByStatus(FoundStatus.CLAIMED).size());
        dto.setReturnedItems(foundItemRepository.findByStatus(FoundStatus.RETURNED).size());
        
        dto.setTotalMatches(itemMatchRepository.count());
        
        return dto;
    }
}
