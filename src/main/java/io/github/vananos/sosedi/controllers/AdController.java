package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.Advertisement;
import io.github.vananos.sosedi.models.dto.ad.AdRequest;
import io.github.vananos.sosedi.models.dto.ad.AdResponse;
import io.github.vananos.sosedi.models.dto.registration.BaseResponse;
import io.github.vananos.sosedi.service.AdService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static io.github.vananos.sosedi.components.validation.ErrorProcessingUtils.assertHasNoErrors;

@RestController
public class AdController {

    private AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    @GetMapping("/ad")
    @PreAuthorize("hasPermission(#id, 'AdInfo', 'read')")
    public ResponseEntity<BaseResponse<AdResponse>> getAd(@RequestParam("id") Long id) {
        AdResponse adResponse = new ModelMapper().map(adService.getAd(id), AdResponse.class);
        return ResponseEntity.ok(new BaseResponse().data(adResponse));
    }

    @PostMapping("ad")
    @PreAuthorize("hasPermission(#adRequest, 'write')")
    public ResponseEntity<BaseResponse<AdResponse>> postAd(@Valid @RequestBody AdRequest adRequest, BindingResult bindingResult) {
        assertHasNoErrors(bindingResult);
        ModelMapper modelMapper = new ModelMapper();
        Advertisement advertisement = modelMapper.map(adRequest, Advertisement.class);
        advertisement = adService.saveAdForUser(adRequest.getUserId(), advertisement);

        AdResponse adResponse = modelMapper.map(advertisement, AdResponse.class);
        adResponse.setUserId(adRequest.getUserId());

        return ResponseEntity.ok(new BaseResponse<AdResponse>().data(adResponse));
    }
}