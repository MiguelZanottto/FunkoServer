package develop.common.models;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
/**
 * Clase para formatear valores en formato de moneda de Espana, como fechas y valores monetarios.
 */
public class MyLocale {
    private static final Locale locale = new Locale("es","ES");

    private MyLocale(){}
    /**
     * Convierte una fecha en una representacion de fecha en estilo Espanol.
     *
     * @param date La fecha LocalDate que se formateara.
     * @return La representacion formateada de la fecha.
     */
    public static String toLocalDate(LocalDate date) {
        return date.format(
                DateTimeFormatter
                        .ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
        );
    }
    /**
     * Convierte el valor en una representacion en formato de moneda de Espana
     *
     * @param money El valor monetario que se formateara.
     * @return La representacion formateada en formato de moneda local.
     */
    public static String toLocalMoney(double money) {
        return NumberFormat.getCurrencyInstance(locale).format(money);
    }

}