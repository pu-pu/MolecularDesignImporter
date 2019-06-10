package com.example.importer.entities;

import java.util.List;

public class AtomExclusionResponse {
    public List<Atom> atoms;
    public Box box;

    public AtomExclusionResponse(List<Atom> atoms, Box box) {
        this.atoms = atoms;
        this.box = box;
    }

    public AtomExclusionResponse() {}
}
