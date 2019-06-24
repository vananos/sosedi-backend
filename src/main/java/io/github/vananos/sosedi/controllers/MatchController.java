package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.*;
import io.github.vananos.sosedi.models.Match.MatchState;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.vananos.sosedi.Utils.calculateAge;
import static java.util.Arrays.asList;

@RestController
public class MatchController {
    private MatchService matchService;
    private int callCounter = 0;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/matches")
    @PreAuthorize("hasPermission(#userId, 'matches', 'read')")
    public ResponseEntity<BaseResponse<List<MatchResponseEntity>>> getMatchesForUser(@RequestParam("userid") Long userId) {
        List<Match> matches = matchStub();//callCounter++ < 1 ? matchStub() : Collections.emptyList();//matchService
        // .getMatchesForUser(userId);
        ModelMapper modelMapper = new ModelMapper();

        List<MatchResponseEntity> responseEntities = matches.stream()
                .map(match -> {
                    MatchResponseEntity matchResponseEntity = matchToMatchResponseEntity(userId, match, modelMapper);
                    matchResponseEntity.getUserProfileInfo().setPhone(null);
                    return matchResponseEntity;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new BaseResponse<List<MatchResponseEntity>>().data(responseEntities));
    }

    @PostMapping("/match")
//    @PreAuthorize("hasPermission(#matchUpdateRequest, 'write')")
    public ResponseEntity<BaseResponse> match(@RequestBody MatchUpdateRequest matchUpdateRequest) {
//        matchService.updateMatchState(matchUpdateRequest);
        return ResponseEntity.ok(new BaseResponse());
    }

    @GetMapping("/mutualmatches")
    @PreAuthorize("hasPermission(#userId, 'matches', 'read')")
    public ResponseEntity<BaseResponse<List<MatchResponseEntity>>> getMutualMatches(@RequestParam("userid") Long userId) {
        List<Match> matches = matchService.getMutualMatches(userId);

        ModelMapper modelMapper = new ModelMapper();
        List<MatchResponseEntity> responseEntities = matches.stream()
                .map(match -> matchToMatchResponseEntity(userId, match, modelMapper))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new BaseResponse<List<MatchResponseEntity>>().data(responseEntities));
    }

    private MatchResponseEntity matchToMatchResponseEntity(Long userId, Match match, ModelMapper modelMapper) {
        User otherUser = match.getFirstUser().getId().equals(userId) ? match.getSecondUser() : match.getFirstUser();
        MatchResponseEntity matchResponseEntity = new MatchResponseEntity();
        matchResponseEntity.setMatchId(match.getMatchId());
        AdResponse adResponse = modelMapper.map(otherUser.getAdvertisement(), AdResponse.class);
        matchResponseEntity.setAdInfo(adResponse);
        UserProfileInfo userProfileInfo = modelMapper.map(otherUser, UserProfileInfo.class);
        userProfileInfo.setAge(calculateAge(otherUser.getBirthday()));
        matchResponseEntity.setUserProfileInfo(userProfileInfo);
        return matchResponseEntity;
    }

    private List<Match> matchStub() {
        User user = new User();
        user.setId(2L);
        user.setName("Ваня " + callCounter++);
        user.setSurname("Заяц");
        user.setBirthday(LocalDate.of(1998, 11, 1));
        user.setGender(Gender.MALE);
        user.setPhone("+7921 928 35 46");
        user.setDescription("Норм парень");
        user.setInterests(asList(Interests.SPORT, Interests.BOOKS));
        user.setAvatar("0db964bb-ea9b-4e8a-aec2-0afdc3974573.png");

        Advertisement advertisement = new Advertisement();
        advertisement.setRentPay(25);
        advertisement.setRoomType(asList(RoomType.SINGLE, RoomType.DOUBLE));
        advertisement.setLandlord(true);
        advertisement.setMinAge(12);
        advertisement.setMaxAge(45);
        advertisement.setSmoking(Attitude.NEUTRAL);
        advertisement.setAnimals(Attitude.NEUTRAL);
        advertisement.setDescription("Ищю чистюлю");
        advertisement.setConveniences(asList(Convenience.TV, Convenience.FRIDGE));
        advertisement.setPlaceId(new GeoInfo());
        advertisement.getPlaceId().setAddress("Санкт-Петербург");
        user.setAdvertisement(advertisement);

        User user1 = new User();
        user1.setId(1L);


        Match match = new Match();
        match.setFirstUser(user);
        match.setFirstUserState(MatchState.ACCEPTED);
        match.setSecondUser(user1);
        match.setSecondUserState(MatchState.ACCEPTED);
        return asList(match, match, match, match, match, match, match);
    }
}