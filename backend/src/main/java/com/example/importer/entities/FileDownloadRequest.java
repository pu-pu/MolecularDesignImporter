package com.example.importer.entities;

import java.util.List;

public class FileDownloadRequest {
    public List<Atom> atoms;
    public List<Corner> corners;
    public String name;
    public double angle;
    public double a;
    public double b;
    public double c;

    public FileDownloadRequest(List<Atom> atoms, List<Corner> corners, String name, double angle, double a, double b, double c) {
        this.name = name;
        this.atoms = atoms;
        this.corners = corners;
        this.angle = angle;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public FileDownloadRequest() { }
}
