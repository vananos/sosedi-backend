package io.github.vananos.sosedi.security.permission;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.security.UserDetailsImpl;
import io.github.vananos.sosedi.security.permission.evaluators.CustomPermissionEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class PermissionEvaluatorImpl implements PermissionEvaluator {

    private List<CustomPermissionEvaluator> customPermissionEvaluators;

    @Autowired
    public PermissionEvaluatorImpl(List<CustomPermissionEvaluator> customPermissionEvaluators)
    {
        this.customPermissionEvaluators = customPermissionEvaluators;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return resolvePermission(authentication, permission, new TargetObjectInfo().targetObject(targetDomainObject));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
                                 Object permission)
    {
        return resolvePermission(authentication, permission,
                new TargetObjectInfo().targetType(targetType).targetId(targetId)
        );
    }

    private boolean resolvePermission(Authentication authentication, Object permission,
                                      TargetObjectInfo targetObjectInfo)
    {

        String permissionType = (String) permission;
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        if (user.getUserStatus() == User.UserStatus.EMAIL_UNCONFIRMED) {
            throw new AccessDeniedException("user email is not confirmed");
        }


        Optional<CustomPermissionEvaluator> permissionEvaluator = customPermissionEvaluators.stream()
                .filter(pe -> pe.isForObject(targetObjectInfo, permissionType))
                .findAny();

        if (permissionEvaluator.isPresent()) {
            return permissionEvaluator.get().evaluate(targetObjectInfo, user, permissionType);
        }

        log.error("Couldn't find permission evaluator for type: {} ", targetObjectInfo);
        return false;
    }
}
