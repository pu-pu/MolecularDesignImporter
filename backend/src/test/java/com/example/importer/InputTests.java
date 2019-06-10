package com.example.importer;

import com.example.importer.controllers.ImportController;
import com.example.importer.entities.Atom;
import com.example.importer.services.ImageProcessingService;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.runner.RunWith;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InputTests {

    ImportController controller = new ImportController();
    ImageProcessingService service = new ImageProcessingService();
    List<String> testFilenames;

    public InputTests() {
        testFilenames = new ArrayList<>();
        testFilenames.add("face");
        testFilenames.add("1");
        testFilenames.add("b1");
        testFilenames.add("b2");
        testFilenames.add("b3");
        testFilenames.add("b4");
        testFilenames.add("b5");
        testFilenames.add("b6");
        testFilenames.add("example");
        testFilenames.add("cropped");
    }


    private byte[] loadFile(String path) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        byte[] fileAsBytes = null;

        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            fileAsBytes = IOUtils.toByteArray(fileInputStream);
        } catch (Exception e) {
        }

        return fileAsBytes;
    }

    @TestFactory
    Collection<DynamicTest> fileInputTests() {
        return testFilenames
                .stream()
                .map(filename -> DynamicTest.dynamicTest(filename, () -> testFile(filename)))
                .collect(Collectors.toList());
    }


    public boolean essentiallyEquals(Atom atom, Atom b) {
        double x = b.x;
        double y = b.y;
        double tolerance = 5;
        String colourString = b.colourString;
        return (Math.abs(x - atom.x) < tolerance && Math.abs(y - atom.y) < tolerance && Objects.equals(colourString, atom.colourString));
    }


    public void testFile(String filename) {
        byte[] imageBytes = loadFile("testInputs/" + filename + ".png");
        Mat colourImage = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_COLOR);
        List<Atom> atoms = service.GetAtoms(colourImage);

        List<Atom> known = null;

        try {
            FileInputStream fis = new FileInputStream("testInputs/" + filename + ".ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            known = (List<Atom>) ois.readObject();
            ois.close();
        } catch (Exception e) {
        }


        for (int i = 0; i < atoms.size(); i++) {
            Atom a = atoms.get(i);
            boolean has = known.stream().anyMatch(l -> essentiallyEquals(a, l));

            if (!has) {
                assert(false);
            }
        }

        assert(true);
    }

    public void b1() {
        byte[] imageBytes = loadFile("testInputs/b1.png");
        Mat colourImage = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_COLOR);
        List<Atom> atoms = service.GetAtoms(colourImage);

        List<Atom> known = null;

        try {
            FileInputStream fis = new FileInputStream("b1.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            known = (List<Atom>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            System.out.println("A");
        }

        assert(new HashSet<>(atoms).equals(new HashSet<>(known)));
    }

    @Test
    public void point() {
        assert(true);
    }

    public void createBaselineFile() {
        String filename = "";
        byte[] imageBytes = loadFile("testInputs/" + filename + ".png");
        Mat colourImage = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_COLOR);
        List<Atom> atoms = service.GetAtoms(colourImage);

        try {
            FileOutputStream fos = new FileOutputStream("testInputs/" + filename + ".ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(atoms);
            oos.close();

        } catch (Exception e) {
            System.out.println("A");
        }
    }

}
