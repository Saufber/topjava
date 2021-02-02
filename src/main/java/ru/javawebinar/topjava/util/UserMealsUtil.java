package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;
import ru.javawebinar.topjava.util.TimeUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.LocalDate;
import java.util.*;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 2, 10, 0), "Завтрак", 5000),
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 2, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 2, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 28, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 28, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 28, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2021, Month.FEBRUARY, 28, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(6, 0), LocalTime.of(23, 0), 2000);
        mealsTo.forEach(System.out::println);


//        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

//-------------------- https://vertex-academy.com/tutorials/ru/java-8-novye-metody-map/?doing_wp_cron=1611781810.9929790496826171875000
        Map<LocalDate, Integer> caloriesSumByDate = new HashMap<>();
        meals.forEach(m -> caloriesSumByDate.merge(m.getDateTime().toLocalDate(), m.getCalories(), (oldCal, newCal) -> oldCal + newCal));


        final List<UserMealWithExcess> mealsWithExcess = new ArrayList<>();
        meals.forEach(m ->
        {
            if (TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime)) {
                mealsWithExcess.add(new UserMealWithExcess(m.getDateTime(), m.getDescription(), caloriesSumByDate.getOrDefault(m.getDateTime(),0),
                        caloriesSumByDate.getOrDefault(m.getDateTime(),0) > caloriesPerDay ));
            }
        });
        return mealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        return null;
    }
}
