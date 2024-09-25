package se233.projectadpro.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFileManager {

    public ZipFileManager() {}

    public ArrayList<File> extractImage(File zipFile) throws IOException {
        ArrayList<File> imageFiles = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile.toPath()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) {
                    // Extract the image file
                    File newFile = new File(zipFile.getParent(), fileName);
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    imageFiles.add(newFile);
                }
                zis.closeEntry();
            }
        }
        return imageFiles;
    }

    public ArrayList<File> replaceZipWithImages(ArrayList<File> fileList) throws IOException {
        ArrayList<File> result = new ArrayList<>();

        // Using a normal for loop instead of an iterator
        for (int i = 0; i < fileList.size(); i++) {
            File file = fileList.get(i);
            if (file.getName().endsWith(".zip")) {
                // Extract images from the zip and replace the zip entry with the images
                result.addAll(extractImage(file));
            } else {
                result.add(file); // Keep the original image files
            }
        }
        return result;
    }
}