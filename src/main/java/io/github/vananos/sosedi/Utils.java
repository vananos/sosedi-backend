package io.github.vananos.sosedi;

import java.time.LocalDate;
import java.time.Period;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static int calculateAge(LocalDate birthday) {
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    public static String getRandomPincode() {
        return String.format("%05d", ThreadLocalRandom.current().nextInt(0, 999999));
    }
}
