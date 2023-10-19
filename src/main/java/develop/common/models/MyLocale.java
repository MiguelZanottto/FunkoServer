package develop.common.models;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class MyLocale {
    private static final Locale locale = new Locale("es","ES");

    private MyLocale(){}

    public static String toLocalDate(LocalDate date) {
        return date.format(
                DateTimeFormatter
                        .ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
        );
    }

    public static String toLocalMoney(double money) {
        return NumberFormat.getCurrencyInstance(locale).format(money);
    }

}