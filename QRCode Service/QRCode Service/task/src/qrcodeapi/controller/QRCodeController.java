package qrcodeapi.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
public class QRCodeController {

    @GetMapping("/api/health")
    public String healthCheck() {
        return "OK";
    }

    @GetMapping(path = "/api/qrcode", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<byte[]> getQRCode(
            @RequestParam String contents,
            @RequestParam(defaultValue = "250") int size,
            @RequestParam(defaultValue = "L") String correction,
            @RequestParam(defaultValue = "png") String type) throws IOException {

        if (contents == null || contents.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("{\"error\": \"Contents cannot be null or blank\"}".getBytes());
        }

        if (size < 150 || size > 350) {
            return ResponseEntity
                    .badRequest()
                    .body("{\"error\": \"Image size must be between 150 and 350 pixels\"}".getBytes());
        }

        ErrorCorrectionLevel errorCorrectionLevel;
        switch (correction.toUpperCase()) {
            case "L":
                errorCorrectionLevel = ErrorCorrectionLevel.L;
                break;
            case "M":
                errorCorrectionLevel = ErrorCorrectionLevel.M;
                break;
            case "Q":
                errorCorrectionLevel = ErrorCorrectionLevel.Q;
                break;
            case "H":
                errorCorrectionLevel = ErrorCorrectionLevel.H;
                break;
            default:
                return ResponseEntity
                        .badRequest()
                        .body("{\"error\": \"Permitted error correction levels are L, M, Q, H\"}".getBytes());
        }

        if (!(type.equalsIgnoreCase("png") || type.equalsIgnoreCase("jpeg") || type.equalsIgnoreCase("gif"))) {
            return ResponseEntity
                    .badRequest()
                    .body("{\"error\": \"Only png, jpeg and gif image types are supported\"}".getBytes());
        }

        QRCodeWriter writer = new QRCodeWriter();
        try {
            Map<EncodeHintType, Object> hints = Map.of(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);

            BitMatrix bitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, size, size, hints);

            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                switch (type.toLowerCase()) {
                    case "png":
                        ImageIO.write(bufferedImage, "png", baos);
                        break;
                    case "jpeg":
                        ImageIO.write(bufferedImage, "jpeg", baos);
                        break;
                    case "gif":
                        ImageIO.write(bufferedImage, "gif", baos);
                        break;
                }

                byte[] imageBytes = baos.toByteArray();
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.parseMediaType("image/" + type))
                        .body(imageBytes);
            }
        } catch (WriterException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to generate QR code\"}".getBytes());
        }
    }
}
