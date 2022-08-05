package com.esri.arcgisruntime.sample.displaymap.location.avalanchewarningsystem;

public enum AvalancheWarningLevel {

    SIMPLE,
    CHALLENGING,
    COMPLEX;

    public static AvalancheWarningLevel getRespectiveWarningLevel(String warningLevel) {

        switch (warningLevel) {

            case "Simple":
                return SIMPLE;

            case "Challenging":
                return CHALLENGING;

            case "Complex":
                return COMPLEX;

            default:
                return SIMPLE;
        }
    }
}
