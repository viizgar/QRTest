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

    private static void escanear(Scanner s) {
       writeCodes(s.readQRCode(
                "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/01.pdf",
                true));
       /**writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/02.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/03.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/04.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/05-06.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/07-08.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/09.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/10.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/11.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/12.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/13.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/14.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/15.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/16-17.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/18.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/19.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/20-21.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/22.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/23.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/24-25.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/26.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/27.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/28.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/29.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/30.pdf",
               true));
       writeCodes(s.readQRCode(
               "C:/Users/Vicente Izquierdo/Pictures/ControlCenter4/Scan/original/31.pdf",
               true));**/
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

    	Scanner s = new Scanner();
    	
        escanear(s);

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
        //writeCodes(Scanner.readQRCode(dataMatrixImage, false));

        // generar("mp-0001");
        // writeCodes(Scanner.readQRCode(dataMatrixImage, false));
        //
        // generar("mp-0002");
        // writeCodes(Scanner.readQRCode(dataMatrixImage, false));
    }

    /**private static void scanGroup() {
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
**/
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
