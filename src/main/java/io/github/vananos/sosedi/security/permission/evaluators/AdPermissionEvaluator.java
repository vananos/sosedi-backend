package io.github.vananos.sosedi.security.permission.evaluators;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.ad.AdRequest;
import io.github.vananos.sosedi.security.permission.TargetObjectInfo;
import io.github.vananos.sosedi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdPermissionEvaluator implements CustomPermissionEvaluator {

    private UserService userService;

    @Autowired
    public AdPermissionEvaluator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isForObject(TargetObjectInfo targetObjectInfo, String permissionType) {
        return targetObjectInfo.targetObject instanceof AdRequest || "AdInfo".equals(targetObjectInfo.targetType);
    }

    @Override
    public boolean evaluate(TargetObjectInfo targetObjectInfo, User user, String permissionType) {
        if (permissionType.equals("write")) {
            AdRequest adRequest = (AdRequest) targetObjectInfo.targetObject;
            return adRequest.getId() == null ||
                    userService.findUserById(adRequest.getUserId())
                            .getAdvertisement()
                            .getId()
                            .equals(adRequest.getUserId());
        }
        if (permissionType.equals("read")) {
            return (targetObjectInfo.targetId()).equals(user.getId());
        }

        return false;
    }
}
