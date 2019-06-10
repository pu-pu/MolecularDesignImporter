package com.example.importer.controllers;

import com.example.importer.entities.*;
import com.example.importer.services.ComputationService;
import com.example.importer.services.FileService;
import com.example.importer.services.ImageProcessingService;
import org.apache.commons.io.IOUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ImportController {

    private ImageProcessingService imageProcessingService;
    private FileService fileService;
    private ComputationService computationService;

    public ImportController() {
        this.imageProcessingService = new ImageProcessingService();
        this.fileService = new FileService();
        this.computationService = new ComputationService();
    }

    /**
     * Takes all values required to generate a CIF file and creates a CIF file. Serves the file
     * in response.
     * @param request: the data required to create a CIF file for a given structure
     * @return response entity that contains the CIF file as bytes
     */
    @RequestMapping(value = "/files", method = RequestMethod.POST)
    public ResponseEntity<Object> getFileFromData(@RequestBody FileDownloadRequest request) {

        Box box = this.computationService.ConvertCornersToBox(request.corners);

        imageProcessingService.FitAtomsToBox(request.atoms, box, request.a, request.b);

        String filename;

        try {
            filename = fileService.OutputToCif(request.atoms, request.name, request.angle, request.a, request.b, request.c);
        } catch (IOException e) {
            return new ResponseEntity<>("Could not generate CIF file", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        byte[] fileAsBytes;
        try(FileInputStream fileInputStream = new FileInputStream(filename)) {
            fileAsBytes = IOUtils.toByteArray(fileInputStream);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not generate CIF file", HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            fileService.DeleteFile(filename);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        headers.add("Content-Disposition", "attachment; filename=" + filename);
        return new ResponseEntity<>(fileAsBytes, headers, HttpStatus.OK);
    }

    /**
     * Takes an image as input and extracts atom data (mainly location) from it. Returns this
     * structured data as response.
     * @param file the input image
     * @return structured atom data
     */
    @PostMapping("/upload")
    public ResponseEntity<Object> imageUpload(@RequestParam("file") MultipartFile file) {
        List<Atom> atoms;

        try {
            atoms = ExtractAtoms(file);
        } catch (IOException e) {
            return new ResponseEntity<>("Error processing file", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // No atoms could be extracted from the file
        if (atoms.size() == 0) {
            return new ResponseEntity<>("No atoms found in file", HttpStatus.BAD_REQUEST);
        }

        ImageUploadResponse response = new ImageUploadResponse(atoms);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Takes a set of atoms and box boundaries and determines some properties about the atoms.
     * Marks atoms that are outside the box, marks atoms that are on box lines and marks atoms
     * the are on corners. Can also cull duplicates on lines. This data is useful later.
     * @param request the atom and corner data
     * @return the marked atoms and box
     */
    @PostMapping("/exclusion")
    public ResponseEntity<Object> exclusion(@RequestBody AtomExclusionRequest request) {
        if (request.atoms == null || request.atoms.size() == 0) {
            return new ResponseEntity<>("Atoms is a required field", HttpStatus.BAD_REQUEST);
        }
        if (request.corners == null || request.corners.size() == 0) {
            return new ResponseEntity<>("Corners is a required field", HttpStatus.BAD_REQUEST);
        }

        Box box = computationService.ConvertCornersToBox(request.corners);

        this.computationService.DetectInclusion(request.atoms, box);
        this.computationService.DetectCornerAtoms(request.atoms, box);
        this.computationService.DetectLineAtoms(request.atoms, box);
        //List<Atom> culledAtoms = this.computationService.CullDuplicates(request.atoms, box);

        return new ResponseEntity<>(new AtomExclusionResponse(request.atoms, box), HttpStatus.OK);
    }

    private List<Atom> ExtractAtoms(MultipartFile file) throws IOException {
        Mat colourImage = fileService.LoadImage(file, Imgcodecs.IMREAD_COLOR);

        return imageProcessingService.GetAtoms(colourImage);
    }
}