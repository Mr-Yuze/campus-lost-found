package com.lostfound.campus.config;

import com.lostfound.campus.entity.*;
import com.lostfound.campus.repository.FoundItemRepository;
import com.lostfound.campus.repository.LostReportRepository;
import com.lostfound.campus.repository.UserRepository;
import com.lostfound.campus.service.MatchService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Optional;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      LostReportRepository lostReportRepository,
                                      FoundItemRepository foundItemRepository,
                                      MatchService matchService,
                                      org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@campus.edu").isEmpty()) {
                // Users
                User admin = new User();
                admin.setName("Admin User");
                admin.setEmail("admin@campus.edu");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setPhone("1112223333");
                userRepository.save(admin);

                User student = new User();
                student.setName("Student Alice");
                student.setEmail("alice@student.campus.edu");
                student.setPassword(passwordEncoder.encode("pass123"));
                student.setRole(Role.STUDENT);
                student.setPhone("2223334444");
                User savedStudent = userRepository.save(student);

                User staff = new User();
                staff.setName("Staff Bob");
                staff.setEmail("bob@staff.campus.edu");
                staff.setPassword(passwordEncoder.encode("pass123"));
                staff.setRole(Role.STAFF);
                staff.setPhone("3334445555");
                User savedStaff = userRepository.save(staff);

                // Lost Reports
                LostReport lost1 = new LostReport();
                lost1.setItemName("Blue ID Card");
                lost1.setCategory("ID/Wallet");
                lost1.setDescription("Student ID card with name Alice");
                lost1.setLocationLost("Library");
                lost1.setDateLost(LocalDate.now().minusDays(2));
                lost1.setStatus(LostStatus.REPORTED);
                lost1.setReportedBy(savedStudent);
                lostReportRepository.save(lost1);

                LostReport lost2 = new LostReport();
                lost2.setItemName("Black MacBook Pro");
                lost2.setCategory("Electronics");
                lost2.setDescription("14-inch MacBook with stickers");
                lost2.setLocationLost("Cafeteria");
                lost2.setDateLost(LocalDate.now().minusDays(1));
                lost2.setStatus(LostStatus.REPORTED);
                lost2.setReportedBy(savedStudent);
                lostReportRepository.save(lost2);

                LostReport lost3 = new LostReport();
                lost3.setItemName("Silver Water Bottle");
                lost3.setCategory("Accessories");
                lost3.setDescription("Hydroflask 32oz");
                lost3.setLocationLost("Gym");
                lost3.setDateLost(LocalDate.now());
                lost3.setStatus(LostStatus.REPORTED);
                lost3.setReportedBy(savedStudent);
                lostReportRepository.save(lost3);

                // Found Items
                FoundItem found1 = new FoundItem();
                found1.setItemName("ID Card");
                found1.setCategory("ID/Wallet");
                found1.setDescription("Student ID found near the front desk");
                found1.setLocationFound("Library");
                found1.setDateFound(LocalDate.now().minusDays(1));
                found1.setStatus(FoundStatus.AVAILABLE);
                found1.setReportedBy(savedStaff);
                FoundItem savedFound1 = foundItemRepository.save(found1);
                matchService.findMatchesForFoundItem(savedFound1);

                FoundItem found2 = new FoundItem();
                found2.setItemName("MacBook Pro");
                found2.setCategory("Electronics");
                found2.setDescription("Black laptop found on table");
                found2.setLocationFound("Cafeteria");
                found2.setDateFound(LocalDate.now());
                found2.setStatus(FoundStatus.AVAILABLE);
                found2.setReportedBy(savedStaff);
                FoundItem savedFound2 = foundItemRepository.save(found2);
                matchService.findMatchesForFoundItem(savedFound2);
                
                FoundItem found3 = new FoundItem();
                found3.setItemName("Airpods");
                found3.setCategory("Electronics");
                found3.setDescription("White airpods pro");
                found3.setLocationFound("Science Building");
                found3.setDateFound(LocalDate.now().minusDays(3));
                found3.setStatus(FoundStatus.AVAILABLE);
                found3.setReportedBy(savedStaff);
                FoundItem savedFound3 = foundItemRepository.save(found3);
                matchService.findMatchesForFoundItem(savedFound3);
                
                System.out.println("Sample data initialized!");
            }
        };
    }
}
