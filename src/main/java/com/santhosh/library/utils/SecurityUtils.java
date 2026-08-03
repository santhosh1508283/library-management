package com.santhosh.library.utils;

import com.santhosh.library.entity.User;
import com.santhosh.library.exception.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotFoundException("User not authenticated");
        }

        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new UserNotFoundException("User not authenticated");
        }

        return user;
    }
}
