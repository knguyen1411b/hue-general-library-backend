package org.app.backend.security;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.usersubscription.UserSubscription;
import org.app.backend.modules.usersubscription.UserSubscriptionRepository;
import org.app.backend.modules.usersubscription.exception.UserSubscriptionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service cung cấp các phương thức kiểm tra quyền (Permission) cho Spring Security Expression.
 *
 * Lưu ý: Tên bean "securityService" phải khớp với tên được gọi trong annotation @PreAuthorize
 * Ví dụ: @PreAuthorize("@securityService.isUserSubscriptionOwner(#id)")
 */
@Service("securityService")
public class SecurityService {

    private final UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    public SecurityService(
        UserSubscriptionRepository userSubscriptionRepository
    ) {
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    /**
     * Kiểm tra xem UserSubscription có thuộc về User đang đăng nhập hay không.
     *
     * @param subscriptionId ID của UserSubscription cần kiểm tra
     * @return true nếu user đang đăng nhập là chủ sở hữu của subscription, false nếu không
     */
    public boolean isUserSubscriptionOwner(UUID subscriptionId) {
        // 1. Lấy thông tin Authentication từ Security Context
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        // 2. Kiểm tra xem user đã đăng nhập chưa (tránh lỗi null)
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // 3. Lấy User ID của người đang đăng nhập từ CustomUserDetails
        // (Giả sử bạn đã lưu CustomUserDetails trong Authentication Principal)
        CustomUserDetails currentUserDetails;
        try {
            currentUserDetails =
                (CustomUserDetails) authentication.getPrincipal();
        } catch (ClassCastException e) {
            // Trường hợp principal không phải CustomUserDetails (ví dụ: anonymous user)
            return false;
        }

        UUID currentUserId = currentUserDetails.getId();

        // 4. Lấy UserSubscription từ database
        UserSubscription userSubscription = userSubscriptionRepository
            .findById(subscriptionId)
            .orElseThrow(() ->
                new UserSubscriptionNotFoundException(
                    "UserSubscription not found with id: " + subscriptionId
                )
            );

        // 5. So sánh ID: User đăng nhập có trùng với User trong UserSubscription không?
        UUID subscriptionOwnerId = userSubscription.getUser().getId();

        return currentUserId.equals(subscriptionOwnerId);
    }
}
