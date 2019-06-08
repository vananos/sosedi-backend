package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.components.validation.ErrorProcessor;
import io.github.vananos.sosedi.models.dto.ad.AdResponse;
import io.github.vananos.sosedi.models.dto.registration.BaseResponse;
import io.github.vananos.sosedi.service.AdService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdController {

    private AdService adService;
    private ErrorProcessor errorProcessor;

    public AdController(AdService adService, ErrorProcessor errorProcessor) {
        this.adService = adService;
        this.errorProcessor = errorProcessor;
    }

    @GetMapping("/ad")
    public ResponseEntity<BaseResponse<AdResponse>> getAd(@RequestParam("id") Long id) {
        AdResponse adResponse = new ModelMapper().map(adService.getAd(id), AdResponse.class);
        return ResponseEntity.ok(new BaseResponse().data(adResponse));
    }
}