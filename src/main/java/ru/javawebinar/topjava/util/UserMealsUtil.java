package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
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
        final Map<LocalDate, Integer> caloriesSumByDate = new HashMap<>();
        for (UserMeal meal : meals) {
            caloriesSumByDate.merge(meal.getDateTime().toLocalDate(),
                    meal.getCalories(),
                    Integer::sum);
        }

        final List<UserMealWithExcess> mealsWithExceeded = new ArrayList<>();
        for (UserMeal m : meals) {
            if (TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                mealsWithExceeded.add(createWithExceed(m, caloriesSumByDate.get(m.getDateTime().toLocalDate()) > caloriesPerDay));
        }
        return mealsWithExceeded;
    }

    public static UserMealWithExcess createWithExceed(UserMeal meal, boolean exceeded) {
        return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), exceeded);
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(Collectors.groupingBy(m -> m.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories))
                );

        return meals.stream()
                .filter(m -> TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                .map(m -> createWithExceed(m, caloriesSumByDate.get(m.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }
}
