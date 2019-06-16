package io.github.vananos.sosedi.components;

import io.github.vananos.sosedi.components.matching.MatchingStrategy;
import io.github.vananos.sosedi.components.matching.SimpleMatchingStrategyImpl;
import io.github.vananos.sosedi.models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static io.github.vananos.Utils.getValidAdvertisement;
import static io.github.vananos.Utils.getValidUser;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class SimpleMatchingStrategyTest {
    public static final int START_INDEX = 2;

    private MatchingStrategy matchingStrategy = new SimpleMatchingStrategyImpl();


    @Test
    @DisplayName("filter male only")
    public void maleOnlyFilterTest() {
        User targetUser = getValidUser();
        Advertisement ad = getValidAdvertisement();
        ad.setGender(Gender.MALE);
        targetUser.setAdvertisement(ad);

        List<User> allGenderUsers = getUsers(3, user -> {
            user.setGender(Gender.values()[user.getId().intValue() - START_INDEX]);
        });

        check(targetUser, allGenderUsers, allGenderUsers.stream().filter(u -> u.getGender() == Gender.MALE));
    }

    @Test
    @DisplayName("filter female only")
    public void femaleOnlyFilterTest() {
        User targetUser = getValidUser();
        Advertisement ad = getValidAdvertisement();
        ad.setGender(Gender.FEMALE);
        targetUser.setAdvertisement(ad);

        List<User> allGenderUsers = getUsers(3, user -> {
            user.setGender(Gender.values()[user.getId().intValue() - START_INDEX]);
        });

        check(targetUser, allGenderUsers,
                allGenderUsers.stream().filter(u -> u.getGender() == Gender.FEMALE));
    }

    @Test
    @DisplayName("other users don't want to be with user gender")
    public void otherUsersDoNotWantToBeWithUserGenderTest() {
        User targetUser = getValidUser();
        targetUser.setGender(Gender.MALE);
        Advertisement ad = getValidAdvertisement();
        ad.setGender(Gender.ANY);
        targetUser.setAdvertisement(ad);

        List<User> allGenderUsers = getUsers(3, user -> {
            user.setGender(Gender.values()[user.getId().intValue() - START_INDEX]);
            user.getAdvertisement().setGender(Gender.FEMALE);
        });

        check(targetUser, allGenderUsers, Stream.empty());
    }

    @Test
    @DisplayName("same gender preferences")
    public void samePreferencesGenderTest() {
        User targetUser = getValidUser();
        targetUser.setGender(Gender.MALE);
        Advertisement ad = getValidAdvertisement();
        ad.setGender(Gender.FEMALE);
        targetUser.setAdvertisement(ad);

        List<User> allGenderUsers = getUsers(3, user -> {
            Gender gender = Gender.values()[user.getId().intValue() - START_INDEX];
            user.setGender(gender);
            if (gender == Gender.FEMALE) {
                user.getAdvertisement().setGender(Gender.MALE);
            }
        });

        check(targetUser, allGenderUsers, allGenderUsers.stream().filter(u -> u.getGender() == Gender.FEMALE));
    }

    @Test
    @DisplayName("filter any gender")
    public void anyGenderFilterTest() {
        User targetUser = getValidUser();
        Advertisement ad = getValidAdvertisement();
        ad.setGender(Gender.ANY);
        targetUser.setAdvertisement(ad);

        List<User> allGenderUsers = getUsers(3, user -> {
            user.setGender(Gender.values()[user.getId().intValue() - START_INDEX]);
        });

        check(targetUser, allGenderUsers, allGenderUsers.stream());
    }

    @Test
    @DisplayName("user from SPB match with SPB users only")
    public void samePlaceFilterTest() {
        User spbUser = getValidUser();
        spbUser.setAdvertisement(getValidAdvertisement());
        spbUser.getAdvertisement().setPlaceId("SPB");

        List<User> usersFromDifferentPlaces = getUsers(3, user -> {
            if (user.getId() == 2) {
                user.getAdvertisement().setPlaceId("SPB");
            } else {
                user.getAdvertisement().setPlaceId("MOSCOW");
            }
        });

        check(spbUser, usersFromDifferentPlaces,
                usersFromDifferentPlaces.stream().filter(u -> u.getId().equals(2L)));
    }

    @Test
    @DisplayName("no smoker match only with no smokers")
    public void userDoNotPreferSmokingOnlyNoSmokersMatchTest() {
        User notSmokerUser = getValidUser();
        notSmokerUser.setAdvertisement(getValidAdvertisement());
        notSmokerUser.getAdvertisement().setSmoking(Attitude.BAD);

        List<User> usersWithDifferentSmokingAttitude = getUsers(3, user -> {
            user.getAdvertisement().setSmoking(Attitude.values()[user.getId().intValue() - START_INDEX]);
        });

        check(notSmokerUser, usersWithDifferentSmokingAttitude,
                usersWithDifferentSmokingAttitude.stream().filter(u -> u.getAdvertisement().getSmoking() != Attitude.GOOD));
    }

    @Test
    @DisplayName("user don't like animals, only without animals")
    public void userDoNotPreferAnimalsOnlyWithoutAnimalsMatchTest() {
        User userDontLikeAnimals = getValidUser();
        userDontLikeAnimals.setAdvertisement(getValidAdvertisement());
        userDontLikeAnimals.getAdvertisement().setAnimals(Attitude.BAD);

        List<User> usersWithDifferentAnimalAttitude = getUsers(3, user -> {
            user.getAdvertisement().setAnimals(Attitude.values()[user.getId().intValue() - START_INDEX]);
        });

        check(userDontLikeAnimals, usersWithDifferentAnimalAttitude,
                usersWithDifferentAnimalAttitude.stream().filter(u -> u.getAdvertisement().getAnimals() != Attitude.GOOD));
    }

    @Test
    @DisplayName("user don't like animals, only without animals")
    public void neutralAttitudeSuitableForAllAttitudeTest() {
        User neutralUser = getValidUser();
        neutralUser.setAdvertisement(getValidAdvertisement());
        neutralUser.getAdvertisement().setSmoking(Attitude.NEUTRAL);
        neutralUser.getAdvertisement().setAnimals(Attitude.NEUTRAL);

        List<User> usersWithDifferentAttitudes = getUsers(9, user -> {
            user.getAdvertisement().setAnimals(Attitude.values()[(user.getId().intValue() - START_INDEX) / Attitude.values().length]);
            user.getAdvertisement().setSmoking(Attitude.values()[(user.getId().intValue() - START_INDEX) % Attitude.values().length]);
        });

        check(neutralUser, usersWithDifferentAttitudes, usersWithDifferentAttitudes.stream());
    }

    @Test
    @DisplayName("users age preferences must be same")
    public void userAgePreferencesMustIntersectTest() {
        User youngUser = getValidUser();
        youngUser.setBirthday(LocalDate.of(2000, 12, 1));
        youngUser.setAdvertisement(getValidAdvertisement());
        youngUser.getAdvertisement().setMinAge(15);
        youngUser.getAdvertisement().setMaxAge(30);

        int id = START_INDEX;

        User userWithSamePreferencesAndAge = getValidUser();
        userWithSamePreferencesAndAge.setId((long) id++);
        userWithSamePreferencesAndAge.setAdvertisement(youngUser.getAdvertisement());
        userWithSamePreferencesAndAge.setBirthday(LocalDate.of(2002, 1, 1));

        User userWithSamePreferencesUnsuitableAge = getValidUser();
        userWithSamePreferencesUnsuitableAge.setId((long) id++);
        userWithSamePreferencesUnsuitableAge.setAdvertisement(getValidAdvertisement());
        userWithSamePreferencesUnsuitableAge.setBirthday(LocalDate.of(1950, 12, 1));
        userWithSamePreferencesUnsuitableAge.setAdvertisement(youngUser.getAdvertisement());

        User userWithSuitableAgeButUnsuitablePreferences = getValidUser();
        userWithSuitableAgeButUnsuitablePreferences.setId((long) id++);
        userWithSuitableAgeButUnsuitablePreferences.setBirthday(LocalDate.of(2002, 1, 1));
        userWithSuitableAgeButUnsuitablePreferences.setAdvertisement(getValidAdvertisement());
        userWithSuitableAgeButUnsuitablePreferences.getAdvertisement().setMinAge(30);
        userWithSuitableAgeButUnsuitablePreferences.getAdvertisement().setMaxAge(50);

        List<User> usersWithDifferentAgeAndAgePreferences = asList(userWithSamePreferencesAndAge,
                userWithSamePreferencesUnsuitableAge, userWithSuitableAgeButUnsuitablePreferences);

        check(youngUser, usersWithDifferentAgeAndAgePreferences,
                Stream.of(userWithSamePreferencesAndAge));
    }

    @Test
    @DisplayName("landlord must match only with no landlords")
    public void landlordMustMatchOnlyWithNoLandlordTest() {
        User landlordUser = getValidUser();
        landlordUser.setAdvertisement(getValidAdvertisement());
        landlordUser.getAdvertisement().setLandlord(true);

        List<User> differentUsers = getUsers(2, user -> {
            if (user.getId() % 2 == 0) {
                user.getAdvertisement().setLandlord(true);
            } else {
                user.getAdvertisement().setLandlord(false);
            }
        });

        check(landlordUser, differentUsers, differentUsers.stream().filter(u -> !u.getAdvertisement().getLandlord()));
    }

    @Test
    @DisplayName("room type must intersect")
    public void roomTypeMustIntersectTest() {
        User targetUser = getValidUser();
        targetUser.setAdvertisement(getValidAdvertisement());
        targetUser.getAdvertisement().setRoomType(asList(RoomType.SINGLE, RoomType.STUDIO));

        List<User> differentUsers = getUsers(5, user -> {
        });
        differentUsers.get(0).getAdvertisement().setRoomType(asList(RoomType.STUDIO, RoomType.SINGLE));
        differentUsers.get(1).getAdvertisement().setRoomType(asList(RoomType.SINGLE, RoomType.DOUBLE));
        differentUsers.get(2).getAdvertisement().setRoomType(asList(RoomType.DOUBLE, RoomType.THREE));

        check(targetUser, differentUsers,
                differentUsers.stream()
                        .filter(
                                u -> u.getAdvertisement()
                                        .getRoomType()
                                        .stream()
                                        .anyMatch(r -> targetUser.getAdvertisement().getRoomType().contains(r))));
    }

    private void check(User targetUser, List<User> users, Stream<User> expectedResult)
    {
        assertThat(
                users.stream()
                        .filter(matchingStrategy.matchWithUser(targetUser))
                        .collect(toList())
        ).containsOnlyElementsOf(expectedResult.collect(toList()));
    }


    private static List<User> getUsers(int count, Consumer<User> userConfigurer) {
        return LongStream.range(START_INDEX, count + START_INDEX)
                .mapToObj(i -> {
                    User user = getValidUser();
                    user.setId(i);
                    user.setAdvertisement(getValidAdvertisement());
                    user.getAdvertisement().setLandlord(false);
                    userConfigurer.accept(user);
                    return user;
                }).collect(toList());
    }
}