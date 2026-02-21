package com.sdl.security;

import com.sdl.entity.User;

public final class RoleConstants {

    private RoleConstants() {
    }

    public static String role(User.Role role) {
        return "ROLE_" + role.name();
    }

    public static String plain(User.Role role) {
        return role.name();
    }
}