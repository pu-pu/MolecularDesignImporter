package com.example.importer.services;

import com.example.importer.entities.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class ImageProcessingService {

    private Map<String, String> labelMappings;

    public ImageProcessingService() {
        labelMappings = new HashMap<>();
        labelMappings.put("grey", "C");
        labelMappings.put("magenta", "K");
        labelMappings.put("red", "Li");
        labelMappings.put("blue", "N");
        labelMappings.put("yellow", "O");
        labelMappings.put("green", "Unknown");
        labelMappings.put("purple", "Unknown");
        labelMappings.put("#d4c136", "Na");
    }

    private Mat PreprocessImage(Mat image) {
        // Convert image to greyscale and apply blur
        Mat grey = new Mat();
        Imgproc.cvtColor(image, grey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(grey, grey, 5);

        return grey;
    }

    private Mat FindCentres(Mat image) {
        // Find centres
        Mat centres = new Mat();

        File config = new File("houghInputs.json");
        String configJson = "";

        try {
            configJson = FileUtils.readFileToString(config);
        } catch(Exception e) {

        }

        JSONObject obj = new JSONObject(configJson);
        int minRadius = obj.getInt("minRadius");
        int maxRadius = obj.getInt("maxRadius");
        int upperThreshold = obj.getInt("upperThreshold");
        int threshold = obj.getInt("threshold");
        double dp = obj.getDouble("dp");

//        int minRadius = 5;
//        int maxRadius = 16;
//        int upperThreshold = 100;
//        int threshold = 12;
        double minDist = (double)image.rows() / 16;


        Imgproc.HoughCircles(image, centres, Imgproc.HOUGH_GRADIENT, dp,
                minDist, // change this value to detect circles with different distances to each other
                upperThreshold, threshold, minRadius, maxRadius); // change the last two parameters

        return centres;
    }

    public List<Atom> GetAtoms(Mat image) {
        Mat preprocessedImage = PreprocessImage(image);

        Mat centres = FindCentres(preprocessedImage);

        Mat converted = new Mat();

        Imgproc.cvtColor(image, converted, Imgproc.COLOR_BGR2HLS);

        ArrayList<Atom> atoms = new ArrayList<>();

        for (int x = 0; x < centres.cols(); x++) {
            double[] centre = centres.get(0, x);
            int radius = (int) Math.round(centre[2]);

            List<double[]> circlePoints = GetCirclePixels(centre[0], centre[1], radius, converted);

            Map<String, Integer> colourCounts = new HashMap<String, Integer>();

            circlePoints.forEach(point -> {
                String colourString = Classify(point);
                if (colourCounts.get(colourString) == null) {
                    colourCounts.put(colourString, 0);
                }

                colourCounts.put(colourString, colourCounts.get(colourString) + 1);
            });

            boolean smarter = true;

            if (smarter) {
                List<Map.Entry<String, Integer>> sortedColours = colourCounts.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
                int totalCount = sortedColours.stream().mapToInt(entry -> entry.getValue()).sum();

                if (colourCounts.containsKey("white") && colourCounts.get("white") / (double)totalCount > 0.50) {
                    continue;
                }

                String atomColour = sortedColours.get(sortedColours.size() - 1).getKey();

                Atom atom = new Atom(centre[0], centre[1]);
                atom.colourString = atomColour;
                atom.label = this.labelMappings.get(atomColour);

                atoms.add(atom);
            } else {
                String maximumColour = "";
                int maximumColourCount = Integer.MIN_VALUE;

                for (Map.Entry<String, Integer> entry : colourCounts.entrySet()) {
                    if (entry.getValue() > maximumColourCount) {
                        maximumColourCount = entry.getValue();
                        maximumColour = entry.getKey();
                    }
                }

                if (maximumColour.equals("white")) {
                    continue; // these are false positives
                }

                Atom atom = new Atom(centre[0], centre[1]);
                atom.colourString = maximumColour;
                atom.label = this.labelMappings.get(maximumColour);
                atoms.add(atom);
            }

        }

        return atoms;
    }

    public String Classify(double[] c)
    {
        // H [0, 180] | 360
        // L,S [0, 255] | 100
        // opencv | common
        // see https://docs.opencv.org/2.4/modules/imgproc/doc/miscellaneous_transformations.html?#cvtcolor

        double hue = (c[0] / 180.0) * 360;
        double lgt = (c[1] / 255.0) * 100;
        double sat = (c[2] / 255.0) * 100;

        if (lgt < 20)  return "black";
        if (lgt > 90)  return "white";
        if (sat < 8) return "grey";
        if (hue < 30)   return "red";
        if (hue < 90)   return "#d4c136"; // a less bright yellow
        if (hue < 150)  return "green";
        if (hue < 210)  return "cyan";
        if (hue < 260)  return "blue";
        if (hue < 300)  return "purple";
        if (hue < 330)  return "magenta";

        return "red";
    }


    public List<double[]> GetCirclePixels(double centerX, double centerY, double radius, Mat converted) {
        List<double[]> circlePixels = new ArrayList<>();

        for (int x = 0; x < converted.cols(); x++)
        {
            for (int y = 0; y < converted.rows(); y++)
            {
                double dx = x - centerX;
                double dy = y - centerY;
                double distanceSquared = dx * dx + dy * dy;

                if (distanceSquared <= Math.pow(radius, 2.0))
                {
                    circlePixels.add(converted.get(y, x));
                }
            }
        }

        return circlePixels;
    }



    public void FitAtomsToBox(List<Atom> atoms, Box box, double boxHeight, double boxWidth) {
        List<Line> horizontalLineContainer = box.lines.stream().filter(line -> Math.abs(line.gradient) < 0.05).collect(Collectors.toList());
        List<Line> angledLineContainer = box.lines.stream().filter(line -> Math.abs(line.gradient) > 0.05).collect(Collectors.toList());

        if (horizontalLineContainer.size() == 0|| angledLineContainer.size() == 0) {
            return;
        }

        Line leftmostLine = GetLeftmostAngledLine(angledLineContainer);

        atoms.forEach(atom -> {
            double x = (atom.y - leftmostLine.intercept) / leftmostLine.gradient;
            double y = atom.y;

            double xDistance = CalculateDistanceBetweenPoints(x, y, atom.x, atom.y);
            double yDistance = CalculateDistanceBetweenPoints(x, y, leftmostLine.endX, leftmostLine.endY);

            double xRatio = xDistance / horizontalLineContainer.get(1).length;
            double yRatio = yDistance / leftmostLine.length;

            atom.scaledX = xRatio;
            atom.scaledY = yRatio;
        });
    }

    private Line GetLeftmostAngledLine(List<Line> lines) {
        // the leftmost line's start position is the furthest to the left.
        double leftmostStart = Double.MAX_VALUE;
        Line leftmostLine = lines.get(0);

        for (Line line : lines) {
            if (line.startX < leftmostStart) {
                leftmostStart = line.startX;
                leftmostLine = line;
            }
        }

        return leftmostLine;
    }

    private double CalculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}
