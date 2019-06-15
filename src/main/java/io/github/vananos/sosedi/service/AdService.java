package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.models.Advertisement;

public interface AdService {
    Advertisement getAd(Long userId);

    Advertisement saveAdForUser(Long userId, Advertisement ad);
}