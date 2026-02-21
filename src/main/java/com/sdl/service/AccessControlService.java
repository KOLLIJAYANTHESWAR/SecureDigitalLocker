package com.sdl.service;

import com.sdl.entity.User;

public interface AccessControlService {

    boolean canCreateRole(User creator, User.Role targetRole);

    boolean canApproveRole(User approver, User.Role targetRole);

    boolean canDeactivateUser(User performer, User target);

    boolean isHigherOrEqualRole(User.Role higher, User.Role lower);

    boolean isHigherRole(User.Role higher, User.Role lower);
}