package io.github.vananos.sosedi.security.permission.evaluators;

import io.github.vananos.sosedi.models.Match;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.matching.MatchUpdateRequest;
import io.github.vananos.sosedi.security.permission.TargetObjectInfo;
import io.github.vananos.sosedi.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MatchesPermissionEvaluator implements CustomPermissionEvaluator {

    private MatchService matchService;

    @Autowired
    public MatchesPermissionEvaluator(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public boolean isForObject(TargetObjectInfo targetObjectInfo, String permissionType) {
        return targetObjectInfo.targetObject instanceof MatchUpdateRequest || "matches".equals(targetObjectInfo.targetType);
    }

    @Override
    public boolean evaluate(TargetObjectInfo targetObjectInfo, User user, String permissionType) {
        if (permissionType.equals("write")) {
            MatchUpdateRequest matchUpdateRequest = (MatchUpdateRequest) targetObjectInfo.targetObject;
            Optional<Match> match = matchService
                    .getMatch(matchUpdateRequest.getMatchId());

            return match.isPresent() && (
                    match.map(m ->
                            m.getFirstUser().getId().equals(user.getId()) || m.getSecondUser().getId().equals(user.getId()))
                            .get());
        }

        if (permissionType.equals("read")) {
            return (targetObjectInfo.targetId.equals(user.getId()));
        }

        return false;
    }
}
