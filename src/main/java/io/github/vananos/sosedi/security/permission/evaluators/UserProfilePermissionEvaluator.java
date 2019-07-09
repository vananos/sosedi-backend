package io.github.vananos.sosedi.security.permission.evaluators;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.userprofile.UserProfileInfo;
import io.github.vananos.sosedi.security.permission.TargetObjectInfo;
import org.springframework.stereotype.Component;

@Component
public class UserProfilePermissionEvaluator implements CustomPermissionEvaluator {
    @Override
    public boolean isForObject(TargetObjectInfo targetObjectInfo, String permissionType) {
        return targetObjectInfo.targetObject instanceof UserProfileInfo || "UserProfile".equals(targetObjectInfo.targetType);
    }

    @Override
    public boolean evaluate(TargetObjectInfo targetObjectInfo, User user, String permissionType) {
        if (permissionType.equals("write")) {
            return ((UserProfileInfo) targetObjectInfo.targetObject).getId().equals(user.getId());
        }
        if (permissionType.equals("read")) {
            return targetObjectInfo.targetId.equals(user.getId());
        }
        return false;
    }
}