package io.github.vananos.sosedi.security;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.ad.AdRequest;
import io.github.vananos.sosedi.models.dto.userprofile.UserProfileInfo;
import io.github.vananos.sosedi.service.UserService;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.Serializable;

public class CustomPermissionEvaluator implements PermissionEvaluator {

    private UserService userService;

    @Autowired
    public CustomPermissionEvaluator(UserService userService) {
        this.userService = userService;
    }

    @Data
    @Accessors(fluent = true)
    private static class TargetObjectInfo {
        public Object targetObject;
        public String targetType;
        public Serializable targetId;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return resolvePermission(authentication, permission, new TargetObjectInfo().targetObject(targetDomainObject));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return resolvePermission(authentication, permission, new TargetObjectInfo().targetType(targetType).targetId(targetId));
    }

    private boolean resolvePermission(Authentication authentication, Object permission, TargetObjectInfo targetObjectInfo) {
        if (!(permission instanceof String)) {
            throw new IllegalArgumentException("Only permissions of type String are supported, permission type: " + permission.getClass());
        }

        String permissionType = (String) permission;
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        if (user.getUserStatus() == User.UserStatus.EMAIL_UNCONFIRMED) {
            throw new AccessDeniedException("user email is not confirmed");
        }

        if (permissionType.equals("write") && targetObjectInfo.targetObject instanceof UserProfileInfo) {
            return ((UserProfileInfo) targetObjectInfo.targetObject).getId().equals(user.getId());
        }
        if (permissionType.equals("write") && targetObjectInfo.targetObject instanceof AdRequest) {
            AdRequest adRequest = (AdRequest) targetObjectInfo.targetObject;
            return adRequest.getId() == null ||
                    userService.findUserById(adRequest.getUserId())
                            .getAdvertisement()
                            .getId()
                            .equals(adRequest.getUserId());
        }

        if (permissionType.equals("read") && targetObjectInfo.targetType != null) {
            String type = targetObjectInfo.targetType;
            if (type.equals("UserProfile")) {
                return targetObjectInfo.targetId.equals(user.getId());
            }

            if (type.equals("AdInfo")) {
                return (targetObjectInfo.targetId()).equals(user.getId());
            }

            if (type.equals("matches")) {
                return (targetObjectInfo.targetId.equals(user.getId()));
            }
        }

        if (permissionType.equals("savePhoto")) {
            MultipartHttpServletRequest request = (MultipartHttpServletRequest) targetObjectInfo.targetObject;
            return request.getParameter("userId").equals(user.getId().toString());
        }

        if (permissionType.equals("deletePhoto")) {
            return targetObjectInfo.targetId.equals(user.getAvatar());
        }

        throw new UnsupportedOperationException("Couldn't resolve permission of type: " + permissionType);
    }
}
