package com.example.searchproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class City {
    private String name;
    private int x;
    private int y;

    public Map<City, Integer> adjacentCities;
    public Map<City, Integer> airDistance;

    public City(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.adjacentCities = new HashMap<>();
        this.airDistance = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
