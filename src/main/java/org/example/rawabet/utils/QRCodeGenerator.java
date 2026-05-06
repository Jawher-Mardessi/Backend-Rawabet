package org.example.rawabet.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class QRCodeGenerator {

    /**
     * Generate a QR code as a Base64-encoded PNG image
     * @param text The text to encode in the QR code
     * @param width The width of the QR code image
     * @param height The height of the QR code image
     * @return Base64-encoded PNG image
     */
    public static String generateQRCodeAsBase64(String text, int width, int height) {
        try {
            System.out.println("[QRCodeGenerator] Generating QR code for URL: " + text);
            
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    width,
                    height
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            
            System.out.println("[QRCodeGenerator] Generated QR code with " + imageBytes.length + " bytes");

            String base64String = Base64.getEncoder().encodeToString(imageBytes);
            System.out.println("[QRCodeGenerator] Base64 encoded string length: " + base64String.length());
            
            return base64String;
        } catch (Exception e) {
            System.err.println("[QRCodeGenerator] ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a QR code as raw PNG bytes
     * @param text The text to encode in the QR code
     * @param width The width of the QR code image
     * @param height The height of the QR code image
     * @return PNG image bytes
     */
    public static byte[] generateQRCodeAsBytes(String text, int width, int height) {
        try {
            System.out.println("[QRCodeGenerator] Generating QR code bytes for URL: " + text);
            
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    width,
                    height
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            
            System.out.println("[QRCodeGenerator] Generated QR code bytes with " + imageBytes.length + " bytes");
            
            return imageBytes;
        } catch (Exception e) {
            System.err.println("[QRCodeGenerator] ERROR generating bytes: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a QR code as raw PNG bytes with default size (300x300)
     */
    public static byte[] generateQRCodeAsBytes(String text) {
        return generateQRCodeAsBytes(text, 300, 300);
    }
}

