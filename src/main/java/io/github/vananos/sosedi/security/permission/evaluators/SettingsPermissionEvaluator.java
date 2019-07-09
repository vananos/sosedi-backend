package io.github.vananos.sosedi.security.permission.evaluators;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.security.permission.TargetObjectInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SettingsPermissionEvaluator implements CustomPermissionEvaluator {
    @Override
    public boolean isForObject(TargetObjectInfo targetObjectInfo, String permissionType) {
        return "UserSettings".equals(targetObjectInfo.targetType);
    }

    @Override
    public boolean evaluate(TargetObjectInfo targetObjectInfo, User user, String permissionType) {
        return targetObjectInfo.targetId.equals(user.getId());
    }
}
