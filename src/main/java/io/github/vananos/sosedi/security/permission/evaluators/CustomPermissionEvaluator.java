package io.github.vananos.sosedi.security.permission.evaluators;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.security.permission.TargetObjectInfo;

public interface CustomPermissionEvaluator {

    boolean isForObject(TargetObjectInfo targetObjectInfo, String permissionType);

    boolean evaluate(TargetObjectInfo targetObjectInfo, User user, String permissionType);
}
