package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;
import ru.javawebinar.topjava.util.TimeUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 2, 10, 0), "Завтрак", 100),
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 2, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 2, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 28, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 28, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 28, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 28, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(6, 0), LocalTime.of(23, 0), 2000);
        mealsTo.forEach(System.out::println);


       System.out.println(filteredByStreams(meals, LocalTime.of(6, 0), LocalTime.of(23, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

//-------------------- https://vertex-academy.com/tutorials/ru/java-8-novye-metody-map/?doing_wp_cron=1611781810.9929790496826171875000

        Map<LocalDate, Integer> caloriesSumByDate = new HashMap<>();
        meals.forEach(m -> caloriesSumByDate.merge(m.getDateTime().toLocalDate(), m.getCalories(), (oldCal, newCal) -> oldCal + newCal));


        List<UserMealWithExcess> caloriesSumExceedDays = new ArrayList<>();
        meals.forEach(m ->
        {
            if (TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime)) {
                caloriesSumExceedDays.add(new UserMealWithExcess(m.getDateTime(), m.getDescription(), caloriesSumByDate.getOrDefault(m.getDateTime(),0),
                        caloriesSumByDate.getOrDefault(m.getDateTime().toLocalDate(),0) > caloriesPerDay ));
            }
        });

        return caloriesSumExceedDays;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        final Map<Object, Integer> caloriesSumByDate = meals.stream().collect(Collectors.groupingBy(m -> m.getDateTime().toLocalDate(),
                Collectors.summingInt(UserMeal::getCalories)));

        Stream<UserMeal> userMealStream = meals.stream().filter(m -> TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime));
        List<UserMealWithExcess> caloriesSumExceedDays = userMealStream.map(m-> new UserMealWithExcess(m.getDateTime(), m.getDescription(), caloriesSumByDate.getOrDefault(m.getDateTime(),0),
                caloriesSumByDate.getOrDefault(m.getDateTime().toLocalDate(),0) > caloriesPerDay )).collect(Collectors.toList());

        return caloriesSumExceedDays;
    }
}
