package com.esri.arcgisruntime.sample.displaymap.location.avalanchewarningsystem;

public enum AvalancheWarningLevel {

    LOW("Ниска"),
    MODERATE("Средна"),
    SIGNIFICANT("Значителна"),
    HIGH("Висока"),
    EXTREME("Много висока");

    private String avalacheDangerText;

    AvalancheWarningLevel(String avalacheDangerText) {

        this.avalacheDangerText = avalacheDangerText;
    }

    public String getAvalacheDangerText() {

        return avalacheDangerText;
    }

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
