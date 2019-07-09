package io.github.vananos.sosedi.security.permission.evaluators;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.settings.ChangeNotificationSettingsRequest;
import io.github.vananos.sosedi.security.permission.TargetObjectInfo;
import org.springframework.stereotype.Component;

@Component
public class ChangeNotificationSettingsPermissionEvaluator implements CustomPermissionEvaluator {
    @Override
    public boolean isForObject(TargetObjectInfo targetObjectInfo, String permissionType) {
        return targetObjectInfo.targetObject instanceof ChangeNotificationSettingsRequest;
    }

    @Override
    public boolean evaluate(TargetObjectInfo targetObjectInfo, User user, String permissionType) {
        if (permissionType.equals("write")) {
            ChangeNotificationSettingsRequest changeNotificationSettingsRequest =
                    (ChangeNotificationSettingsRequest) targetObjectInfo.targetObject;

            return changeNotificationSettingsRequest.getUserId().equals(user.getId());
        }

        return false;
    }
}
