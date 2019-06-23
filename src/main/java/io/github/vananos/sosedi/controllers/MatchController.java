package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.Match;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.ad.AdResponse;
import io.github.vananos.sosedi.models.dto.matching.MatchResponseEntity;
import io.github.vananos.sosedi.models.dto.matching.MatchUpdateRequest;
import io.github.vananos.sosedi.models.dto.registration.BaseResponse;
import io.github.vananos.sosedi.models.dto.userprofile.UserProfileInfo;
import io.github.vananos.sosedi.service.MatchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MatchController {
    private MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/matches")
    @PreAuthorize("hasPermission(#userId, 'matches', 'read')")
    public ResponseEntity<BaseResponse<List<MatchResponseEntity>>> getMatchesForUser(@RequestParam("userid") Long userId) {
        List<Match> matches = matchService.getMatchesForUser(userId);
        ModelMapper modelMapper = new ModelMapper();

        List<MatchResponseEntity> responseMatches = matches.stream()
                .map(m -> {
                    User otherUser = m.getFirstUser().getId().equals(userId) ? m.getSecondUser() : m.getFirstUser();
                    MatchResponseEntity matchResponseEntity = new MatchResponseEntity();
                    matchResponseEntity.setMatchId(m.getMatchId());
                    AdResponse adResponse = modelMapper.map(otherUser.getAdvertisement(), AdResponse.class);
                    matchResponseEntity.setAdInfo(adResponse);
                    UserProfileInfo userProfileInfo = modelMapper.map(otherUser, UserProfileInfo.class);
                    matchResponseEntity.setUserProfileInfo(userProfileInfo);
                    return matchResponseEntity;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new BaseResponse<List<MatchResponseEntity>>().data(responseMatches));
    }

    @PostMapping("/match")
    @PreAuthorize("hasPermission(#matchUpdateRequest, 'write')")
    public ResponseEntity<BaseResponse> match(@RequestBody MatchUpdateRequest matchUpdateRequest) {
        matchService.updateMatchState(matchUpdateRequest);
        return ResponseEntity.ok(new BaseResponse());
    }
}