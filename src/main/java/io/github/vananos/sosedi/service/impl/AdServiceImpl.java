package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.models.Advertisement;
import io.github.vananos.sosedi.repository.AdRepository;
import io.github.vananos.sosedi.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;

public class AdServiceImpl implements AdService {

    private AdRepository adRepository;

    @Autowired
    public AdServiceImpl(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    @Override
    public Advertisement getAd(Long id) {
        return adRepository.getOne(id);
    }
}