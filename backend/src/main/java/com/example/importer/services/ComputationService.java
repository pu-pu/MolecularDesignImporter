package com.example.importer.services;

import com.example.importer.entities.Atom;
import com.example.importer.entities.Box;
import com.example.importer.entities.Corner;
import com.example.importer.entities.Line;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ComputationService {
    public void DetectInclusion(List<Atom> atoms, Box box) {
        int[] xPoints = box.corners.stream().mapToInt(corner -> (int)corner.x).toArray();
        int[] yPoints = box.corners.stream().mapToInt(corner -> (int)corner.y).toArray();

        Polygon boundary = new Polygon(xPoints, yPoints, 4);
        atoms.forEach(atom -> {
            atom.outside = !boundary.contains(atom.x - 1, atom.y - 1) && !IsCloseToLine(atom, box);
        });
    }

    public void DetectLineAtoms(List<Atom> atoms, Box box) {
        atoms.forEach(atom -> {
            box.lines.forEach(line -> {
                double distance = Line2D.ptSegDist(line.startX, line.startY, line.endX, line.endY, atom.x, atom.y);

                if (distance < 10) {
                    // first, we need to make sure we haven't already marked this
                    // representative atom.
                    atom.line = line;
                }
            });
        });
    }

    public boolean IsCloseToLine(Atom atom, Box box) {
        double min = box.lines.stream()
                .mapToDouble(line -> Line2D.ptSegDist(line.startX, line.startY, line.endX, line.endY, atom.x, atom.y))
                .min()
                .getAsDouble();


        return min < 10;
    }

    public void DetectCornerAtoms(List<Atom> atoms, Box box) {
        // An atom is considered to be a corner atom if it is within some
        // small hardcoded tolerance of a corner's location.

        atoms.forEach(atom -> {
            box.corners.forEach(corner -> {
                if (Math.abs(atom.x - corner.x) < 20 && Math.abs(atom.y - corner.y) < 20) {
                    atom.isCorner = true;
                }
            });
        });
    }

    public List<Atom> CullDuplicates(List<Atom> atoms, Box box) {
        // Get one of each line type
        Line l1 = box.lines.get(0);
        Line l2 = box.lines.get(1);

        // Delete all atoms that fall on lines other than the selected
        return atoms.stream().filter(atom -> atom.line == l1 || atom.line == l2 || atom.line == null).collect(Collectors.toList());
    }

    public Box ConvertCornersToBox(List<Corner> corners) {
        SortCornersCounterClockwise(corners);
        List<Line> lines = new ArrayList<>();

        lines.add(new Line(corners.get(0), corners.get(1)));
        lines.add(new Line(corners.get(1), corners.get(2)));
        lines.add(new Line(corners.get(2), corners.get(3)));
        lines.add(new Line(corners.get(3), corners.get(0)));

        return new Box(lines, corners);
    }

    private void SortCornersCounterClockwise(List<Corner> corners) {
        // identify centre point
        double centreX = corners.stream().mapToDouble(corner -> corner.x).average().getAsDouble();
        double centreY = corners.stream().mapToDouble(corner -> corner.y).average().getAsDouble();

        // calculate angles
        corners.forEach(corner -> {
            corner.angleFromCentre = Math.atan2(corner.y - centreY, corner.x - centreX);
        });

        // sort by angle
        Collections.sort(corners, (Corner a, Corner b) -> {
            return (int)(b.angleFromCentre - a.angleFromCentre);
        });
    }
}
