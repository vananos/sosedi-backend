package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.models.Advertisement;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.AdRepository;
import io.github.vananos.sosedi.service.AdService;
import io.github.vananos.sosedi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdServiceImpl implements AdService {

    private UserService userService;
    private AdRepository adRepository;

    @Autowired
    public AdServiceImpl(AdRepository adRepository, UserService userService) {
        this.adRepository = adRepository;
        this.userService = userService;
    }

    @Override
    public Advertisement getAd(Long userId) {
        return userService.findUserById(userId).getAdvertisement();
    }

    public Advertisement saveAdForUser(Long userId, Advertisement ad) {
        User user = userService.findUserById(userId);
        user.setAdvertisement(ad);
        user = userService.updateUserInfo(user);
        return user.getAdvertisement();
    }
}