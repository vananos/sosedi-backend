package io.github.vananos.sosedi.security.permission.evaluators;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.security.permission.TargetObjectInfo;
import org.springframework.stereotype.Component;

@Component
public class AvatarPermissionEvaluator implements CustomPermissionEvaluator {
    @Override
    public boolean isForObject(TargetObjectInfo targetObjectInfo, String permissionType) {
        return "saveAvatar".equals(permissionType) || "deleteAvatar".equals(permissionType);
    }

    @Override
    public boolean evaluate(TargetObjectInfo targetObjectInfo, User user, String permissionType) {
        if (permissionType.equals("deleteAvatar")) {
            return targetObjectInfo.targetObject.equals(user.getId());
        }
        if (permissionType.equals("saveAvatar")) {
            return targetObjectInfo.targetObject.equals(user.getId());
        }
        return false;
    }
}
