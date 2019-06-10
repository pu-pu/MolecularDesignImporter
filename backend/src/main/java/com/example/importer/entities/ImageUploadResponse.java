package com.example.importer.entities;

import java.util.List;

public class ImageUploadResponse {
    public List<Atom> atoms;

    public ImageUploadResponse(List<Atom> atoms) {
        this.atoms = atoms;
    }
}
