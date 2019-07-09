package io.github.vananos.sosedi.security.permission.evaluators;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.security.permission.TargetObjectInfo;
import org.springframework.stereotype.Component;

@Component
public class DeleteAccountPermissionEvaluator implements CustomPermissionEvaluator {
    @Override
    public boolean isForObject(TargetObjectInfo targetObjectInfo, String permissionType) {
        return "User".equals(targetObjectInfo.targetType);
    }

    @Override
    public boolean evaluate(TargetObjectInfo targetObjectInfo, User user, String permissionType) {
        return "delete".equals(permissionType) && targetObjectInfo.targetId.equals(user.getId());
    }
}
