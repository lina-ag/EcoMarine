package tn.edu.esprit.services;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FaceRecognitionService {
    
    private CascadeClassifier faceDetector;
    private static final String HAAR_CASCADE_PATH = "/haarcascade_frontalface_default.xml";
    
    public FaceRecognitionService() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        loadFaceDetector();
    }
    
    private void loadFaceDetector() {
        try {
            InputStream is = getClass().getResourceAsStream(HAAR_CASCADE_PATH);
            
            if (is == null) {
                System.err.println("Erreur: Fichier de détection non trouvé!");
                return;
            }
            
            File tempFile = File.createTempFile("haarcascade", ".xml");
            Files.copy(is, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            faceDetector = new CascadeClassifier(tempFile.getAbsolutePath());
            tempFile.deleteOnExit();
            is.close();
            
            if (faceDetector.empty()) {
                System.err.println("Erreur: Cascade classifier invalide!");
            } else {
                System.out.println("✅ Cascade classifier chargé avec succès!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 🔥 Méthode detectFace ajoutée
    public Mat detectFace(Mat image) {
        if (faceDetector == null || faceDetector.empty()) {
            System.err.println("Face detector not initialized");
            return null;
        }
        
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(grayImage, faceDetections);
        
        Rect[] faces = faceDetections.toArray();
        if (faces.length == 0) {
            return null;
        }
        
        // Prendre le premier visage détecté
        Rect face = faces[0];
        return new Mat(image, face);
    }
    
    // 🔥 Méthode generateFaceEncoding ajoutée
    public byte[] generateFaceEncoding(Mat faceImage) {
        if (faceImage == null || faceImage.empty()) {
            return null;
        }
        
        // Redimensionner pour standardiser
        Mat resized = new Mat();
        Imgproc.resize(faceImage, resized, new Size(100, 100));
        
        // Convertir en niveaux de gris
        Mat gray = new Mat();
        Imgproc.cvtColor(resized, gray, Imgproc.COLOR_BGR2GRAY);
        
        // Créer un histogramme simplifié comme encodage
        Mat hist = new Mat();
        int histSize = 256;
        float[] ranges = {0, 256};
        Imgproc.calcHist(Arrays.asList(gray), new MatOfInt(0), new Mat(), hist, 
                        new MatOfInt(histSize), new MatOfFloat(ranges));
        
        // Convertir l'histogramme en bytes
        double[] histData = new double[histSize];
        for (int i = 0; i < histSize; i++) {
            histData[i] = hist.get(i, 0)[0];
        }
        
        // Normaliser
        double max = 0;
        for (double val : histData) {
            if (val > max) max = val;
        }
        if (max > 0) {
            for (int i = 0; i < histData.length; i++) {
                histData[i] /= max;
            }
        }
        
        // Convertir en bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(histData);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
        return baos.toByteArray();
    }
    
    // 🔥 Méthode compareFaceEncodings ajoutée
    public double compareFaceEncodings(byte[] encoding1, byte[] encoding2) {
        if (encoding1 == null || encoding2 == null) {
            return 0;
        }
        
        try {
            double[] hist1 = deserializeHistogram(encoding1);
            double[] hist2 = deserializeHistogram(encoding2);
            
            if (hist1 == null || hist2 == null || hist1.length != hist2.length) {
                return 0;
            }
            
            // Calculer la similarité cosinus
            double dotProduct = 0;
            double norm1 = 0;
            double norm2 = 0;
            
            for (int i = 0; i < hist1.length; i++) {
                dotProduct += hist1[i] * hist2[i];
                norm1 += hist1[i] * hist1[i];
                norm2 += hist2[i] * hist2[i];
            }
            
            if (norm1 == 0 || norm2 == 0) {
                return 0;
            }
            
            double similarity = dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
            return similarity * 100; // Convertir en pourcentage
            
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    // 🔥 Méthode calculateConfidence ajoutée
    public double calculateConfidence(double similarityScore) {
        if (similarityScore >= 70) {
            return Math.min(100, similarityScore);
        } else if (similarityScore >= 50) {
            return 50 + (similarityScore - 50) / 2;
        } else {
            return similarityScore / 2;
        }
    }
    
    private double[] deserializeHistogram(byte[] data) {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (double[]) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String saveFaceImage(Mat faceImage, String email) {
        try {
            String dir = "faces/";
            File directory = new File(dir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            String filename = dir + email + "_" + System.currentTimeMillis() + ".jpg";
            Imgcodecs.imwrite(filename, faceImage);
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}