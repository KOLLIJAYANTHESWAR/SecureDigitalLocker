package com.sdl.serviceImpl;

import com.sdl.entity.User;
import com.sdl.security.RoleHierarchy;
import com.sdl.service.AccessControlService;
import org.springframework.stereotype.Service;

@Service
public class AccessControlServiceImpl implements AccessControlService {

    @Override
    public boolean canCreateRole(User creator, User.Role targetRole) {

        if (creator == null || targetRole == null) {
            return false;
        }

        return RoleHierarchy.canCreate(creator.getRole(), targetRole);
    }

    @Override
    public boolean canApproveRole(User approver, User.Role targetRole) {

        if (approver == null || targetRole == null) {
            return false;
        }

        return RoleHierarchy.canApprove(approver.getRole(), targetRole);
    }

    @Override
    public boolean canDeactivateUser(User performer, User target) {

        if (performer == null || target == null) {
            return false;
        }

        return RoleHierarchy.canDeactivate(
                performer.getRole(),
                target.getRole()
        );
    }

    @Override
    public boolean isHigherOrEqualRole(User.Role higher, User.Role lower) {

        if (higher == null || lower == null) {
            return false;
        }

        return RoleHierarchy.isHigherOrEqual(higher, lower);
    }

    @Override
    public boolean isHigherRole(User.Role higher, User.Role lower) {

        if (higher == null || lower == null) {
            return false;
        }

        return RoleHierarchy.isStrictlyHigher(higher, lower);
    }
}