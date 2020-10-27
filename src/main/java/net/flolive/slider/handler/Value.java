package net.flolive.slider.handler;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Value {

    private int red;

    private int green;

    private int blue;

    @JsonProperty("red")
    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    @JsonProperty("green")
    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    @JsonProperty("blue")
    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public String toRGB() {
        return String.format("#%1$02X%2$02X%3$02X", red, green, blue);
    }
}
