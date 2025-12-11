package com.example.smartbilling.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Service
public class BarcodeService {

    public void generateBarcodeImage(String barcodeText, String fileName) throws WriterException, IOException {
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.CODE_128, 300, 100);

        Path path = FileSystems.getDefault().getPath(fileName);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        System.out.println("Barcode image generated: " + path.toAbsolutePath());
    }
}