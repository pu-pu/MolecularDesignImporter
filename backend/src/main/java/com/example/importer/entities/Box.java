package com.example.importer.entities;

import java.util.List;

public class Box {

    public List<Line> lines;
    public List<Corner> corners;

    public Box(List<Line> lines, List<Corner> corners) {
        this.lines = lines;
        this.corners = corners;
    }
    public Box(List<Line> lines) {
        this.lines = lines;
    }
}
