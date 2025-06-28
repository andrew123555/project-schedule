package com.example.demo.service;

import com.example.demo.model.entity.UserActivity;
import com.example.demo.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.demo.service.UserDetailsImpl; // 確保導入這個類

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserActivityService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordActivity(UserActivity.ActionType actionType, String actionDescription, String details, String ipAddress) {
        UserActivity activity = new UserActivity();
        activity.setActionType(actionType);
        activity.setAction(actionDescription);
        activity.setDetails(details);
        activity.setIpAddress(ipAddress);
        activity.setTimestamp(LocalDateTime.now());

        // 獲取當前登入用戶的資訊
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            activity.setUserId(userDetails.getId()); // 確保 UserActivity 有 userId 欄位
            activity.setUsername(userDetails.getUsername());
        } else {
            activity.setUsername("匿名/系統"); // 對於未登入或系統活動
        }

        userActivityRepository.save(activity);
    }

    public Page<UserActivity> searchActivities(
            String username, String actionType, String actionDescription,
            String details, String ipAddress,
            LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable) {

        Specification<UserActivity> spec = Specification.where(null);

        if (username != null && !username.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
        }
        if (actionType != null && !actionType.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("actionType"), UserActivity.ActionType.valueOf(actionType)));
        }
        if (actionDescription != null && !actionDescription.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("action")), "%" + actionDescription.toLowerCase() + "%"));
        }
        if (details != null && !details.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("details")), "%" + details.toLowerCase() + "%"));
        }
        if (ipAddress != null && !ipAddress.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("ipAddress")), "%" + ipAddress.toLowerCase() + "%"));
        }
        if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("timestamp"), startDate));
        }
        if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("timestamp"), endDate));
        }

        return userActivityRepository.findAll(spec, pageable);
    }

    public void deleteAllActivities() {
        userActivityRepository.deleteAll();
    }
}