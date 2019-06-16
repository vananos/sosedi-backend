package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.Match;
import io.github.vananos.sosedi.models.dto.matching.MatchResponseEntity;
import io.github.vananos.sosedi.models.dto.registration.BaseResponse;
import io.github.vananos.sosedi.service.MatchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                .map(m ->
                        modelMapper.map(m, MatchResponseEntity.class)
                )
                .collect(Collectors.toList());

        return ResponseEntity.ok(new BaseResponse<List<MatchResponseEntity>>().data(responseMatches));
    }
}