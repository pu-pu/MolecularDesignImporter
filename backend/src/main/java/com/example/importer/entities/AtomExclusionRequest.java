package com.example.importer.entities;

import java.util.List;

public class AtomExclusionRequest {
    public List<Corner> corners;
    public List<Atom> atoms;

    public AtomExclusionRequest(List<Corner> corners, List<Atom> atoms) {
        this.corners = corners;
        this.atoms = atoms;
    }

    public AtomExclusionRequest() { }
}
