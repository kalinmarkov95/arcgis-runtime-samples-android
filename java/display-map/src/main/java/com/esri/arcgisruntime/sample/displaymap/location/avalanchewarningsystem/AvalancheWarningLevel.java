package com.esri.arcgisruntime.sample.displaymap.location.avalanchewarningsystem;

public enum AvalancheWarningLevel {

    LOW,
    MODERATE,
    SIGNIFICANT,
    HIGH,
    EXTREME;

    public static AvalancheWarningLevel getRespectiveWarningLevel(String warningLevel) {

        switch (warningLevel) {

            case "Безопасно":
                return LOW;

            case "Ниска":
                return LOW;

            case "Средна":
                return MODERATE;

            case "Значителна":
                return SIGNIFICANT;

            case "Висока":
                return HIGH;

            case "Много висока":
                return EXTREME;

            default:
                return LOW;
        }
    }
}
