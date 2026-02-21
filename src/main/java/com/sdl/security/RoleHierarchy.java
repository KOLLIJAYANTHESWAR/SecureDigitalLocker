package com.sdl.security;

import com.sdl.entity.User;

import java.util.EnumMap;
import java.util.Map;

public final class RoleHierarchy {

    private RoleHierarchy() {
    }

    private static final Map<User.Role, Integer> HIERARCHY = new EnumMap<>(User.Role.class);

    static {
        HIERARCHY.put(User.Role.ADMIN, 4);
        HIERARCHY.put(User.Role.HR, 3);
        HIERARCHY.put(User.Role.MANAGER, 2);
        HIERARCHY.put(User.Role.USER, 1);
    }

    public static int getLevel(User.Role role) {
        return HIERARCHY.getOrDefault(role, 0);
    }

    public static boolean isHigherOrEqual(User.Role higher, User.Role lower) {
        return getLevel(higher) >= getLevel(lower);
    }

    public static boolean isStrictlyHigher(User.Role higher, User.Role lower) {
        return getLevel(higher) > getLevel(lower);
    }

    public static boolean canCreate(User.Role creatorRole, User.Role targetRole) {
        return switch (creatorRole) {
            case ADMIN -> true;
            case HR -> targetRole == User.Role.MANAGER || targetRole == User.Role.USER;
            case MANAGER -> targetRole == User.Role.USER;
            default -> false;
        };
    }

    public static boolean canApprove(User.Role approverRole, User.Role targetRole) {
        return canCreate(approverRole, targetRole);
    }

    public static boolean canDeactivate(User.Role performerRole, User.Role targetRole) {
        return isStrictlyHigher(performerRole, targetRole);
    }
}