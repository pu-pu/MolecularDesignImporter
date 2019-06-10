package com.example.importer.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Corner {
    public double x;
    public double y;
    @JsonIgnore
    public double angleFromCentre;

    public Corner(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Corner() { }
}
