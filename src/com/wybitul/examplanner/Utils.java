package com.wybitul.examplanner;

import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private Utils() { }

    public static Optional<LocalDate> parseDate(String str, int defaultYear) {
        Pattern p = Pattern.compile("(\\d{1,2})\\.\\s*(\\d{1,2})\\.(?:\\s*(\\d{4}))?");
        Matcher m = p.matcher(str);

        if (!m.find() || (m.group(3) == null && defaultYear == -1)) { return Optional.empty(); }

        int day = Integer.parseInt(m.group(1));
        int month = Integer.parseInt(m.group(2));
        int year = defaultYear;
        if (m.group(3) != null) {
            year = Integer.parseInt(m.group(3));
        }

        return Optional.of(LocalDate.of(year, month, day));
    }

    public static String formatDate(LocalDate date, int defaultYear, String defaultString) {
        if (date == null) {
            return defaultString;
        } else if (date.getYear() == defaultYear) {
            return String.format("%d. %d.", date.getDayOfMonth(), date.getMonthValue());
        } else {
            return String.format("%d. %d. %d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
        }
    }

    public static Integer safeParseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}