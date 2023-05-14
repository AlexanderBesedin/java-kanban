package service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeDurationUtil {
    private int timePeriod;
    private DateTimeFormatter formatter;

    public TimeDurationUtil(int timePeriod, DateTimeFormatter formatter) {
        this.timePeriod = timePeriod;
        this.formatter = formatter;
    }

    public int getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(int timePeriod) {
        this.timePeriod = timePeriod;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public LocalDateTime getFormatStartTime(String start) {
        try {
            if (start == null) throw new NullPointerException("Дата и время не заданы.");
            LocalDateTime startTime = LocalDateTime.parse(start, formatter);
            if (startTime.getMinute() % timePeriod != 0) { // Проверяем кратность минут задачи периоду менеджера
                startTime = startTime.minusMinutes(startTime.getMinute()) //
                        .plusMinutes(roundByTimePeriod(startTime.getMinute()));
                System.out.printf("Время старта задачи округлено кратно интервалу планирования - %s минутам.%n" +
                        "Время старта: %s.%n", timePeriod, startTime.format(formatter));
            }
            return startTime;
        } catch (DateTimeParseException e) {
            System.out.printf("Недопустимый формат даты и времени: \"%s\"",
                    e.getParsedString());
            return null;
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Duration getFormatDuration(String duration) {
        try {
            if (duration == null) throw new NullPointerException("Длительность задачи не задана");
            int minutes = Integer.parseInt(duration);
            if (minutes < 0) {
                throw new IllegalArgumentException(String.format("Недопустимый формат ввода:'%s'," +
                        "значение должно быть положительным числом%n", duration));
            }
            if (minutes % timePeriod != 0) {
                minutes = roundByTimePeriod(minutes);
                System.out.printf("Длительность задачи округлена кратно интервалу планирования - %s минутам.%n" +
                        "Длительность: %s минут.%n", timePeriod, minutes);
            }

            return Duration.ofMinutes(minutes);
        } catch (NumberFormatException ex) {
            System.out.printf("Недопустимый формат ввода:'%s', введите количество минут%n", duration);
            return null;
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    private int roundByTimePeriod(int minutes) { // Метод округления минут даты до шага сетки timeOverlaps
        if (minutes < timePeriod) minutes = timePeriod;
        int c = minutes % timePeriod; // Находим остаток от де ления даты в минутах на период
        if (c <= timePeriod/2) { // Определяем в какую сторону проводить округление
            minutes -= c; // в меньшую сторону
        } else {
            minutes += (timePeriod - c); // в большую сторону
        }
        return minutes;
    }
}
