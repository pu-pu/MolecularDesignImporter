package com.example.importer.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class Line {
    public double startX;
    public double startY;

    public double endX;
    public double endY;

    @JsonIgnore
    public double gradient;
    @JsonIgnore
    public double intercept;

    @JsonIgnore
    public double length;
    @JsonIgnore
    public UUID uuid;

    public Line(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.uuid = UUID.randomUUID();

        this.CalculateGradient();
        this.CalculateIntercept();
        this.CalculateLength();
    }

    public Line(Corner a, Corner b) {
        this.startX = a.x;
        this.startY = a.y;
        this.endX = b.x;
        this.endY = b.y;
        this.uuid = UUID.randomUUID();

        this.CalculateGradient();
        this.CalculateIntercept();
        this.CalculateLength();
    }

    public Line() { }

    public void CalculateGradient() {
        this.gradient = (endY - startY) / (endX - startX);
    }

    public void CalculateIntercept() {
        this.intercept = startY - (this.gradient * startX);
    }

    public void CalculateLength() {
        this.length = Math.sqrt(Math.pow(startX - endX, 2.0) + Math.pow(startY - endY, 2.0));
    }
}
