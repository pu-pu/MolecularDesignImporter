package com.example.importer.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Atom implements Serializable {
    public UUID id;
    public String colourString;
    public String label;
    public double x;
    public double y;
    public boolean outside = false;
    public boolean isCorner = false;
    @JsonIgnore
    public Line line;

    @JsonIgnore
    public double scaledX = 0;
    @JsonIgnore
    public double scaledY = 0;


    public Atom(double x, double y) {
        this.id = UUID.randomUUID();
        this.x = x;
        this.y = y;
    }


    public Atom() { }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Atom)) {
            return false;
        }

        Atom atom = (Atom)o;

        return x == atom.x && y == atom.y && Objects.equals(colourString, atom.colourString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, colourString);
    }
}
