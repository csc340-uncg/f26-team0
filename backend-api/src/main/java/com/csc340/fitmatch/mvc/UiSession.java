package com.csc340.fitmatch.mvc;

import com.csc340.fitmatch.service.BadRequestException;
import jakarta.servlet.http.HttpSession;

final class UiSession {
    private UiSession() {
    }

    static Long requireId(HttpSession session, String role) {
        Object id = session.getAttribute("userId");
        Object currentRole = session.getAttribute("userRole");
        if (!(id instanceof Long userId) || !role.equals(currentRole)) {
            throw new BadRequestException("Please log in to continue.");
        }
        return userId;
    }
}
