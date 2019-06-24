package io.github.vananos.sosedi.components.matching;

import io.github.vananos.sosedi.models.Advertisement;
import io.github.vananos.sosedi.models.Attitude;
import io.github.vananos.sosedi.models.Gender;
import io.github.vananos.sosedi.models.User;
import org.springframework.stereotype.Component;

import static io.github.vananos.sosedi.Utils.calculateAge;

//TODO: improve me
@Component
public class SimpleMatchingStrategyImpl implements MatchingStrategy {


    @Override
    public boolean matches(User targetUser, User secondUser) {
        int targetUserAge = calculateAge(targetUser.getBirthday());
        int userAge = calculateAge(secondUser.getBirthday());
        Advertisement userAd = secondUser.getAdvertisement();
        Advertisement targetUserAd = targetUser.getAdvertisement();

        return !secondUser.getId().equals(targetUser.getId()) &&
                isSuitableGender(userAd.getGender(), targetUser.getGender()) &&
                isSuitableGender(targetUserAd.getGender(), secondUser.getGender()) &&

                (targetUserAd.getPlaceId().equals(userAd.getPlaceId())) &&

                isSuitableAttitude(targetUserAd.getSmoking(), userAd.getSmoking()) &&
                isSuitableAttitude(targetUserAd.getAnimals(), userAd.getAnimals()) &&

                (targetUserAge >= userAd.getMinAge() && targetUserAge <= userAd.getMaxAge()) &&

                (userAge >= targetUserAd.getMinAge() && userAge <= targetUserAd.getMaxAge()) &&
                (userAd.getRoomType().stream().anyMatch(roomType -> targetUserAd.getRoomType().contains(roomType))) &&
                !(targetUserAd.getLandlord() && userAd.getLandlord());
    }

    private boolean isSuitableGender(Gender adGender, Gender userGender) {
        return (adGender == Gender.ANY || adGender == userGender);
    }

    private boolean isSuitableAttitude(Attitude first, Attitude second) {
        return !((first == Attitude.GOOD && second == Attitude.BAD) || (first == Attitude.BAD && second == Attitude.GOOD));
    }
}