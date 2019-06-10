package com.example.importer.services;

import com.example.importer.entities.Atom;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class FileService {
    /**
     * Deletes the file found at the given path.
     * @param path the path to the file to be deleted
     */
    public void DeleteFile(String path) {
        File fileToDelete = new File(path);
        fileToDelete.delete();
    }

    /**
     * Takes all required structure data and generates a CIF file. The file
     * is saved to the project's root directory with a randomly generated filename.
     * The filename is returned. If there is a problem during file generation or saving,
     * an exception is thrown.
     * @param atoms
     * @param name the name of the structure
     * @param angle
     * @param a
     * @param b
     * @param c
     * @return
     */
    public String OutputToCif(List<Atom> atoms, String name, double angle, double a, double b, double c) throws IOException {
        String filename = UUID.randomUUID().toString();

        NumberFormat formatter = new DecimalFormat("#0.000000");
        FileWriter out = null;

        try {
            out = new FileWriter(filename);

            out.write("data_global\n");
            out.write("_chemical_name " + name + "\n");
            out.write("_cell_length_a " + formatter.format(a) + "\n");
            out.write("_cell_length_b " + formatter.format(b) + "\n");
            out.write("_cell_length_c " + formatter.format(c) + "\n");
            out.write("_cell_angle_alpha 90\n");
            out.write("_cell_angle_beta 90\n");
            out.write("_cell_angle_gamma " + formatter.format(angle) + "\n");

            out.write("loop_\n");
            out.write("_atom_site_label\n");
            out.write("_atom_site_fract_x\n");
            out.write("_atom_site_fract_y\n");
            out.write("_atom_site_fract_z\n");

            for (Atom atom : atoms) {
                if (atom.outside) {
                    continue;
                }
                out.write(atom.label);
                out.write(" ");
                out.write(Double.toString(atom.scaledX));
                out.write(" ");
                out.write(Double.toString(atom.scaledY));
                out.write(" ");
                out.write("0\n");
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }

        return filename;
    }

    public Mat LoadImage(MultipartFile file, int flag) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        byte[] imageBytes = file.getBytes();

        // Load image into openCV's preferred format
        return Imgcodecs.imdecode(new MatOfByte(imageBytes), flag);
    }
}
