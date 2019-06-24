package io.github.vananos.sosedi;

import java.time.LocalDate;
import java.time.Period;

public class Utils {
    public static int calculateAge(LocalDate birthday) {
        return Period.between(birthday, LocalDate.now()).getYears();
    }
}
