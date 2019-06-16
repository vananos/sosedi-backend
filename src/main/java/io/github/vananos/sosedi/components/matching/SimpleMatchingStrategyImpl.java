package io.github.vananos.sosedi.components.matching;

import io.github.vananos.sosedi.models.Advertisement;
import io.github.vananos.sosedi.models.Attitude;
import io.github.vananos.sosedi.models.Gender;
import io.github.vananos.sosedi.models.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.function.Predicate;

//TODO: improve me
@Component
public class SimpleMatchingStrategyImpl implements MatchingStrategy {

    private User targetUser;

    @Override
    public Predicate<User> matchWithUser(User user) {
        SimpleMatchingStrategyImpl strategy = new SimpleMatchingStrategyImpl();
        strategy.targetUser = user;

        return strategy::isSuitableFor;
    }

    private boolean isSuitableFor(User user) {
        Advertisement targetAd = targetUser.getAdvertisement();
        int targetUserAge = calculateAge(targetUser.getBirthday());
        int userAge = calculateAge(user.getBirthday());
        Advertisement userAd = user.getAdvertisement();
        Advertisement targetUserAd = targetUser.getAdvertisement();

        return isSuitableGender(userAd.getGender(), targetUser.getGender()) &&
                isSuitableGender(targetUserAd.getGender(), user.getGender()) &&

                (targetAd.getPlaceId().equals(userAd.getPlaceId())) &&

                isSuitableAttitude(targetAd.getSmoking(), userAd.getSmoking()) &&
                isSuitableAttitude(targetAd.getAnimals(), userAd.getAnimals()) &&

                (targetUserAge >= userAd.getMinAge() && targetUserAge <= userAd.getMaxAge()) &&

                (userAge >= targetUserAd.getMinAge() && userAge <= targetUserAd.getMaxAge()) &&

                !(targetAd.getLandlord() && userAd.getLandlord()) &&

                (userAd.getRoomType().stream().anyMatch(roomType -> targetAd.getRoomType().contains(roomType))) &&

                !user.getId().equals(targetUser.getId());
    }

    private boolean isSuitableGender(Gender adGender, Gender userGender) {
        return (adGender == Gender.ANY || adGender == userGender);
    }

    private boolean isSuitableAttitude(Attitude first, Attitude second) {
        return !((first == Attitude.GOOD && second == Attitude.BAD) || (first == Attitude.BAD && second == Attitude.GOOD));
    }

    private int calculateAge(LocalDate birthday) {
        return Period.between(birthday, LocalDate.now()).getYears();
    }
}