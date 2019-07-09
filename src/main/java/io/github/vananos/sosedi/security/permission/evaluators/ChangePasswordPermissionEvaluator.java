package io.github.vananos.sosedi.security.permission.evaluators;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.settings.ChangePasswordRequest;
import io.github.vananos.sosedi.security.permission.TargetObjectInfo;
import org.springframework.stereotype.Component;

@Component
public class ChangePasswordPermissionEvaluator implements CustomPermissionEvaluator {
    @Override
    public boolean isForObject(TargetObjectInfo targetObjectInfo, String permissionType) {
        return targetObjectInfo.targetObject instanceof ChangePasswordRequest;
    }

    @Override
    public boolean evaluate(TargetObjectInfo targetObjectInfo, User user, String permissionType) {
        if (permissionType.equals("write")) {
            ChangePasswordRequest changePasswordRequest = (ChangePasswordRequest) targetObjectInfo.targetObject;
            return changePasswordRequest.getUserId().equals(user.getId());
        }
        return false;
    }
}
