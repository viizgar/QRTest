package principal;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import QRGenerator.DataMatrixGenerator;
import QRReader.Scanner;

public class Principal {

    private static File dataMatrixImage;

    private static void escanear() {
        // writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/QRScanner/NuevoDocumento 2018-01-31.pdf", true));
        //writeCodes(Scanner.readQRCode(
        //        "C:/Users/Vicente Izquierdo/Desktop/tests_pdf_qrcode_import/C_test_scanned_mediplan/01_jürgen_wernersen_200dpi.pdf",
        //        true));
        writeCodes(Scanner.readQRCode(
                "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/test2.pdf",
                true));
        //writeCodes(Scanner.readQRCode(
       //         "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/test12.pdf",
       //         true));
        
//         writeCodes(Scanner.readQRCode("C:/Users/Vicente Izquierdo/Desktop/C_test_scanned_mediplan/Rudolf.pdf", true));
        //writeCodes(Scanner.readQRCode("C:/Users/Vicente Izquierdo/Desktop/Mediplan mit QR-Code.pdf", true));
         //writeCodes(Scanner.readQRCode(
         //"C:/Users/Vicente Izquierdo/Desktop/tests_pdf_qrcode_import/A_test_real_pdf/Team_Organisation.pdf", true));
         //writeCodes(Scanner.readQRCode(
         //        "C:/Users/Vicente Izquierdo/Desktop/tests_pdf_qrcode_import/B_test_suite_one_image_pdf/Alles.pdf", true));
         //writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/QRScanner/CCF31012018.pdf", true));
        // writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/QRScanner/Testfaelle_ukf201.pdf"));
        // writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/weirdImages/dataMatrixImage1.jpg"));
        // writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/image3.png"));
        // Scanner.readQRCode("C:/Users/Martinez/Desktop/QRScanner/images/Page_00001.jpg"); // 600 dpi -> works! (7016 x 4961)
        // Scanner.readQRCode("C:/Users/Martinez/Desktop/QRScanner/Page_00001_300.jpg"); // 300 dpi -> works! (7016 x 4961)
        // writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/QRScanner/Page_00001_96.jpg")); // 96 dpi -> works! (7016 x
        // 4961)
        // writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/QRScanner/test.jpeg"));
        // writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/QRScanner/Page_00001_72.jpg")); // 72 dpi -> works! (7016 x
        // 4961)
        // writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/QRScanner/Page_00001_200_2.jpg")); // 200 dpi -> NO! (2238 x
        // 1653)
        // writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/QRScanner/Testfaelle_ukf201-001.jpg")); // 96 dpi -> NO (2339
        // x
        // 1654)
        // System.out.println(Scanner.readQRCode(System.getProperty("user.home") + "/barcode2.png"));
        // System.out.println(Scanner.readQRCode(System.getProperty("user.home") + "/medikationplanExample2.png"));
        // writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/QRScanner/Testfaelle_ukf201.pdf"));
        // writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/QRScanner/EXT_ITA_VGEX_BMP_Anlage3_Spezifikation.pdf"));
        // final List<String> texts = new ArrayList<>();
        // int errors = 0;
        // for (int i = 1; i < 250; i++) {
        // String pagina = "";
        // if (i < 10) {
        // pagina = "0000" + i;
        // }
        // else if (i < 100) {
        // pagina = "000" + i;
        // }
        // else {
        // pagina = "00" + i;
        // }
        // System.out.println("Actual page: " + i);
        // final String text = Scanner.readQRCode("C:/Users/Martinez/Desktop/QRScanner/images/Page_" + pagina + ".jpg")
        // .get(0);
        // if (text != null) {
        // texts.add(text);
        // }
        // else {
        // errors++;
        // texts.add("The data matrix in the page " + i + " has not been able to interpret");
        // }
        // }
        // System.out.println(errors + " images couldn´t be read of " + 249);
        // writeCodes(texts);
    }

    private static void generar(final String resourceName) {
        String xml = "";
        final Path path = Paths.get("resources/" + resourceName + ".xml");
        final StringBuilder sb = new StringBuilder();
        Object[] lines;
        try {
            lines = Files.lines(path)
                         .toArray();
        } catch (final Exception e) {
            try {
                lines = Files.lines(path, Charset.forName("ISO-8859-1"))
                             .toArray();
            } catch (final Exception e2) {
                try {
                    lines = Files.lines(path, Charset.forName("Windows-1252"))
                                 .toArray();
                } catch (final IOException e1) {
                    lines = null;
                    e1.printStackTrace();
                }
            }
        }

        for (final Object line : lines) {
            sb.append(((String) line).replaceAll("^\\s+", "")
                                     .replaceAll("\\s+$", ""));
        }
        xml = sb.toString();

        System.out.println(xml);
        System.out.println("The xml have a size of: " + xml.length() + " characters");

        final BufferedImage img = DataMatrixGenerator.generateQR(xml);

        System.out.println(System.getProperty("user.home"));
        dataMatrixImage = new File(System.getProperty("user.home") + "/review.jpg");

        try {
            ImageIO.write(img, "jpg", dataMatrixImage);
        } catch (final IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(final String[] args) {

        escanear();

        // scanAndReading();

        // scanPdf();

        // scanImage();

        // scanGroup();
    }

    private static void scanAndReading() {
        // GENERATION + READING
        // generar("NewFile");
        // writeCodes(Scanner.readQRCode(dataMatrixImage, false));

        generar("bmp-0006a");
        writeCodes(Scanner.readQRCode(dataMatrixImage, false));

        // generar("mp-0001");
        // writeCodes(Scanner.readQRCode(dataMatrixImage, false));
        //
        // generar("mp-0002");
        // writeCodes(Scanner.readQRCode(dataMatrixImage, false));
    }

    private static void scanGroup() {
        // BAD QUALITY IMAGE FILTERING
        final List<String> texts = new ArrayList<>();
        int errors = 0;
        for (int i = 150; i < 161; i++) {
            String pagina = "";
            if (i < 10) {
                pagina = "00" + i;
            }
            else if (i < 100) {
                pagina = "0" + i;
            }
            else {
                pagina = "" + i;
            }

            System.out.println("Actual page: " + i);
            final String text = Scanner.readQRCode(
                    "C:/Users/Martinez/Desktop/review/medikationPlanImages/Testfaelle_ukf201-" + pagina + ".png", false)
                                       .get(0);
            if (text != null) {
                texts.add(text);
            }
            else {
                errors++;
                texts.add("The data matrix in the page " + i + " has not been able to interpret");
            }
        }
        System.out.println(errors + " images couldn´t be read of " + 10);
        // writeCodes(texts);
    }

    private static void scanImage() {
        // IMAGE READING
        writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/review/Page_00003.jpg", true));
    }

    private static void scanPdf() {
        // PDF READING
        writeCodes(Scanner.readQRCode("C:/Users/Martinez/Desktop/review/Page_00001.pdf", true));
    }

    private static void writeCodes(final List<String> codes) {
        if (codes.isEmpty()) {
            System.out.println("Haven´t found any data matrix to decode");
        }
        else {
            for (final String code : codes) {
                if (code != null) {
                    System.out.println(code);
                }
                else {
                    System.err.println("The data matrix has not been able to interpret");
                }
            }
        }
    }
}
