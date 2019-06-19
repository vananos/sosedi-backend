package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.*;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

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
        if (!userId.equals(-1L)) {
            MatchResponseEntity matchResponseEntity = new MatchResponseEntity();
            matchResponseEntity.setMatchId(1L);
            UserProfileInfo userProfileInfo = new UserProfileInfo();
            userProfileInfo.setName("Ваня");
            userProfileInfo.setGender(Gender.ANY);
            userProfileInfo.setDescription("Лучше всех");
            userProfileInfo.setSurname("Петух");
            userProfileInfo.setAvatar("a4a0bdb0-4d47-48e4-8e3a-5e75dbb8a360.png");
            userProfileInfo.setInterests(asList(Interests.TV, Interests.BOOKS));
            userProfileInfo.setAge(25);
            matchResponseEntity.setUserProfileInfo(userProfileInfo);
            AdResponse adResponse = new AdResponse();
            adResponse.setConveniences(asList(Convenience.TV, Convenience.DISHWASHER));
            adResponse.setGender(Gender.ANY);
            adResponse.setRoomType(asList(RoomType.SINGLE));
            adResponse.setRentPay(15);
            adResponse.setMinAge(12);
            adResponse.setMaxAge(35);
            adResponse.setLandLord(true);
            adResponse.setDescription("Я петух");
            adResponse.setSmoking(Attitude.NEUTRAL);
            adResponse.setAnimals(Attitude.BAD);
            adResponse.setPlaceId(new GeoInfo());
            adResponse.getPlaceId().setAddress("SPB");
            matchResponseEntity.setAdInfo(adResponse);
            return ResponseEntity.ok(new BaseResponse<List<MatchResponseEntity>>().data(asList(matchResponseEntity)));
        }
        List<Match> matches = matchService.getMatchesForUser(userId);
        ModelMapper modelMapper = new ModelMapper();

        List<MatchResponseEntity> responseMatches = matches.stream()
                .map(m ->
                        modelMapper.map(m, MatchResponseEntity.class)
                )
                .collect(Collectors.toList());

        return ResponseEntity.ok(new BaseResponse<List<MatchResponseEntity>>().data(responseMatches));
    }

    @PostMapping("match")
    @PreAuthorize("hasPermission(#matchUpdateRequest, 'matches', 'write')")
    public ResponseEntity<BaseResponse> match(MatchUpdateRequest matchUpdateRequest) {
        matchService.updateMatchState(matchUpdateRequest);
        return ResponseEntity.ok(new BaseResponse());
    }
}