package QRReader;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.common.detector.WhiteRectangleDetector;
import com.google.zxing.datamatrix.DataMatrixReader;

public class Scanner {

    private static BufferedImage convert2DtoImage(final boolean[][] matrix) {
        final BufferedImage image2 = new BufferedImage(matrix[0].length, matrix.length, BufferedImage.TYPE_BYTE_GRAY);
        final WritableRaster raster = image2.getRaster();
        for (int y = 0; y < matrix.length; y++) {
            final int[] pixels = new int[matrix[0].length];
            for (int x = 0; x < pixels.length; x++) {
                if (matrix[y][x]) {
                    pixels[x] = 255;
                }
                else {
                    pixels[x] = 0;
                }
            }
            raster.setPixels(0, y, matrix[0].length, 1, pixels);
        }

        return image2;
    }

    private static boolean[][] convertImageTo2D(final BufferedImage image) {
        final BufferedImage image2 = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        image2.getGraphics()
              .drawImage(image, 0, 0, null);
        final WritableRaster raster = image2.getRaster();
        final boolean[][] result = new boolean[image.getHeight()][image.getWidth()];
        final int[] pixels = new int[image.getWidth()];
        for (int y = 0; y < image.getHeight(); y++) {
            raster.getPixels(0, y, image.getWidth(), 1, pixels);
            for (int x = 0; x < pixels.length; x++) {
                if (pixels[x] < 127) {
                    result[y][x] = false;
                }
                else {
                    result[y][x] = true;
                }
            }
        }
        return result;
    }

    // private static BufferedImage definitiveFilterDataMatrix(final BufferedImage image) {
    // final boolean[][] matrix = convertImageTo2D(image);
    // final int columns = matrix[0].length;
    // final int rows = matrix.length;
    // int x = 0;
    //
    // int changes = 0;
    // while (x < columns) {
    // int lenght = 1;
    // while (x + lenght < columns && matrix[0][x] == matrix[0][x + lenght]) {
    // lenght++;
    // }
    // changes++;
    // x = x + lenght;
    // }
    //
    // final int mediumLenght = columns / changes;
    // System.out.println("Medium quantity of pixels per square side: " + mediumLenght);
    //
    // x = 0;
    // while (x < columns) {
    // int lenghtx = mediumLenght - 1;
    // if (x + lenghtx >= columns) {
    // lenghtx = columns - x - 1;
    // }
    //
    // int y = rows - 1;
    // while (y >= 0) {
    // int lenghty = mediumLenght - 1;
    // if (y - lenghty < 0) {
    // lenghty = y;
    // }
    //
    // int blackCount = 0;
    // int whiteCount = 0;
    // for (int p = 0; p <= lenghtx; p++) {
    // for (int i = 0; i <= lenghty; i++) {
    // if (matrix[y - i][x + p]) {
    // whiteCount++;
    // }
    // else {
    // blackCount++;
    // }
    // }
    // }
    //
    // for (int p = 0; p <= lenghtx; p++) {
    // for (int i = 0; i <= lenghty; i++) {
    // if (whiteCount > blackCount) {
    // matrix[y - i][x + p] = true;
    // }
    // else {
    // matrix[y - i][x + p] = false;
    // }
    // }
    // }
    // y = y - (lenghty + 1);
    // }
    // x = x + lenghtx + 1;
    // }
    //
    // return convert2DtoImage(matrix);
    // }

    private static BufferedImage filterDataMatrix(final BufferedImage image) {
        final boolean[][] matrix = convertImageTo2D(image);
        final int columns = matrix[0].length;
        final int rows = matrix.length;
        int x = 0;

        int changes = 0;
        while (x < columns) {
            int lenght = 1;
            while (x + lenght < columns && matrix[0][x] == matrix[0][x + lenght]) {
                lenght++;
            }
            changes++;
            x = x + lenght;
        }

        final int mediumLenght = columns / changes;
        // System.out.println("Medium quantity of pixels per square side: " + mediumLenght);

        x = 0;
        while (x < columns) {
            int lenghtx = 1;
            while (x + lenghtx < columns && matrix[0][x] == matrix[0][x + lenghtx]) {
                lenghtx++;
            }

            lenghtx = lenghtx - 1;
            if (x + mediumLenght - 1 < columns && lenghtx < mediumLenght - 1) {
                lenghtx = mediumLenght - 1;
            }
            else if (lenghtx > mediumLenght + 1) {
                lenghtx = mediumLenght + 1;
            }

            int y = rows - 1;
            while (y >= 0) {
                int lenghty = 1;
                while (y - lenghty >= 0 && matrix[y][rows - 1] == matrix[y - lenghty][rows - 1]) {
                    lenghty++;
                }

                lenghty = lenghty - 1;
                if (y - mediumLenght - 1 >= 0 && lenghty < mediumLenght - 1) {
                    lenghty = mediumLenght - 1;
                }
                else if (lenghtx > mediumLenght + 1) {
                    lenghty = mediumLenght + 1;
                }

                int blackCount = 0;
                int whiteCount = 0;
                for (int p = 0; p <= lenghtx; p++) {
                    for (int i = 0; i <= lenghty; i++) {
                        if (matrix[y - i][x + p]) {
                            whiteCount++;
                        }
                        else {
                            blackCount++;
                        }
                    }
                }

                for (int p = 0; p <= lenghtx; p++) {
                    for (int i = 0; i <= lenghty; i++) {
                        if (whiteCount > blackCount) {
                            matrix[y - i][x + p] = true;
                        }
                        else {
                            matrix[y - i][x + p] = false;
                        }
                    }
                }
                y = y - (lenghty + 1);
            }
            x = x + lenghtx + 1;
        }

        return convert2DtoImage(matrix);
    }

    private static Rectangle getDataMatrixPosition(final BufferedImage image) {

        try {

            final BufferedImageLuminanceSource bils = new BufferedImageLuminanceSource(image);
            final HybridBinarizer hb = new HybridBinarizer(bils);// bils is BufferedImageLuminanceSource object
            BitMatrix bm;
            WhiteRectangleDetector wrd;

            bm = hb.getBlackMatrix();
            wrd = new WhiteRectangleDetector(bm);

            final ResultPoint[] rp = wrd.detect();

            final int x = (int) rp[0].getX() - 15;
            final int y = (int) (rp[0].getY() - 15);

            final int w = (int) (rp[2].getX() - rp[0].getX()) + 30;
            final int h = (int) (rp[3].getY() - rp[2].getY()) + 30;

            return new Rectangle(x, y, w, h);

        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String getTextByImage(BufferedImage image, final int i) {
        String text = null;
        if (image != null) {
            int angle = 90;
            Rectangle rec = null;
            image = rotateImageByDegrees(image, angle);
            final BufferedImage transformedImage = image.getSubimage(image.getWidth() * 8 / 11, 0, image.getWidth() * 3 / 11,
                    image.getHeight() / 4);

            // while (angle < 360) {

            JFrame frame;
            frame = new JFrame();
            frame.getContentPane()
                 .setLayout(new FlowLayout());
            frame.getContentPane()
                 .add(new JLabel(new ImageIcon(image)));
            frame.pack();
            frame.setVisible(true);

            rec = getDataMatrixPosition(transformedImage);
            angle += 90;

            // }
            if (rec != null) {
                BufferedImage dataMatrix = transformedImage.getSubimage((int) rec.getX(), (int) rec.getY(), (int) rec.getWidth(),
                        (int) rec.getHeight());

                frame = new JFrame();
                frame.getContentPane()
                     .setLayout(new FlowLayout());
                frame.getContentPane()
                     .add(new JLabel(new ImageIcon(dataMatrix)));
                frame.pack();
                frame.setVisible(true);

                text = imageReader(dataMatrix, 0);
                if (text == null) {
                    dataMatrix = filterDataMatrix(resizeImage(dataMatrix, dataMatrix.getWidth() * 2, dataMatrix.getHeight() * 2));
                    text = imageReader(dataMatrix, 0);

                }

                if (text == null) {

                    System.err.println("Unreadable image");

                    // final JFrame frame = new JFrame();
                    // frame.getContentPane()
                    // .setLayout(new FlowLayout());
                    // frame.getContentPane()
                    // .add(new JLabel(new ImageIcon(dataMatrix)));
                    // frame.pack();
                    // frame.setVisible(true);
                }

            }
        }
        else {
            System.out.println("The image cannot be readed");
        }
        return text;
    }

    public static String imageReader(final BufferedImage image, final int i) {

        String text = null;
        BinaryBitmap bitmap = null;
        Result result = null;

        final int padding = 10;
        final BufferedImage newImage = new BufferedImage(image.getWidth() + 2 * padding, image.getHeight() + 2 * padding,
                image.getType());
        final Graphics g = newImage.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, image.getWidth() + 2 * padding, image.getHeight() + 2 * padding);
        g.drawImage(image, padding, padding, null);
        g.dispose();

        final int[] pixels = newImage.getRGB(0, 0, newImage.getWidth(), newImage.getHeight(), null, 0, newImage.getWidth());
        final RGBLuminanceSource source = new RGBLuminanceSource(newImage.getWidth(), newImage.getHeight(), pixels);

        bitmap = new BinaryBitmap(new HybridBinarizer(source));

        final DataMatrixReader reader = new DataMatrixReader();
        final Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<>();
        decodeHints.put(DecodeHintType.TRY_HARDER, BarcodeFormat.DATA_MATRIX);
        decodeHints.put(DecodeHintType.CHARACTER_SET, CharacterSetECI.ISO8859_1);

        try {
            result = reader.decode(bitmap, decodeHints);
            text = result.getText();
        } catch (final Exception e) {
            // System.out.println("Page: " + (i + 1));

            // final JFrame frame = new JFrame();
            // frame.getContentPane()
            // .setLayout(new FlowLayout());
            // frame.getContentPane()
            // .add(new JLabel(new ImageIcon(image)));
            // frame.pack();
            // frame.setVisible(true);
        }
        return text;
    }

    public static List<String> readQRCode(final File file, final boolean printQR) {
        final List<String> codes = new ArrayList<>();

        if (FilenameUtils.getExtension(file.getName())
                         .equals("pdf")) {
            PDDocument document;
            try {
                document = PDDocument.load(file);
                final PDFRenderer pdfRenderer = new PDFRenderer(document);
                int i = 0;
                for (final PDPage page : document.getPages()) {
                    String text = null;
                    BufferedImage image = null;
                    for (final COSName name : page.getResources()
                                                  .getXObjectNames()) {
                        final PDXObject resource = page.getResources()
                                                       .getXObject(name);

                        /**
                         * if (resource instanceof PDImageXObject) { image = ((PDImageXObject) resource).getImage(); text =
                         * getTextByImage(image, i); }
                         **/

                    }

                    if (image == null) {
                        int resolution = 200;
                        // while (text == null && resolution < 1000) {
                        image = pdfRenderer.renderImageWithDPI(i, resolution, ImageType.RGB);
                        text = getTextByImage(image, i);
                        resolution = resolution + 100;
                        // }
                    }

                    if (text == null) {
                        System.out.println("Error happens on : " + (i + 1) + " page");
                    }

                    // if (image != null && printQR) {
                    // final JFrame frame = new JFrame();
                    // frame.getContentPane()
                    // .setLayout(new FlowLayout());
                    // frame.getContentPane()
                    // .add(new JLabel(new ImageIcon(image)));
                    // frame.pack();
                    // frame.setVisible(true);
                    // }

                    codes.add(text);
                    i++;
                }
                document.close();
                System.out.println(i + " pages/images charged");
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        else

        {
            BufferedImage image = null;
            try {
                image = ImageIO.read(file);
                String text = getTextByImage(image, 0);

                if (image != null && printQR) {
                    final JFrame frame = new JFrame();
                    frame.getContentPane()
                         .setLayout(new FlowLayout());
                    frame.getContentPane()
                         .add(new JLabel(new ImageIcon(image)));
                    frame.pack();
                    frame.setVisible(true);
                }

                if (image != null && text == null) {
                    if (image.getWidth() > image.getHeight()) {
                        BufferedImage transformedImage = image.getSubimage(image.getWidth() * 5 / 6, 0, image.getWidth() / 6,
                                image.getHeight() / 4);
                        text = imageReader(transformedImage, 0);
                        if (text == null) {
                            transformedImage = image.getSubimage(0, image.getHeight() * 3 / 4, image.getWidth() / 6,
                                    image.getHeight() / 4);
                            text = imageReader(transformedImage, 0);
                        }
                    }
                    else {
                        BufferedImage transformedImage = image.getSubimage(0, 0, image.getWidth() / 6, image.getHeight() / 4);
                        text = imageReader(transformedImage, 0);
                        if (text == null) {
                            transformedImage = image.getSubimage(image.getWidth() * 5 / 6, image.getHeight() * 3 / 4,
                                    image.getWidth() / 6, image.getHeight() / 4);
                            text = imageReader(transformedImage, 0);
                        }
                    }
                }
                codes.add(text);

            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return codes;
    }

    public static List<String> readQRCode(final String fileName, final boolean printQR) {

        final File file = new File(fileName);
        return readQRCode(file, printQR);
    }

    private static BufferedImage resizeImage(final BufferedImage img, final int newWidth, final int newHeight) {
        final Image tmp = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        final BufferedImage dimg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        final Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    private static BufferedImage rotateImageByDegrees(final BufferedImage img, final double angle) {
        final double rads = Math.toRadians(angle);
        final double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        final int w = img.getWidth();
        final int h = img.getHeight();
        final int newWidth = (int) Math.floor(w * cos + h * sin);
        final int newHeight = (int) Math.floor(h * cos + w * sin);

        final BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = rotated.createGraphics();
        final AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        final int x = w / 2;
        final int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    private static BufferedImage thresholdImage(final BufferedImage image, final int threshold) {
        final BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        result.getGraphics()
              .drawImage(image, 0, 0, null);
        final WritableRaster raster = result.getRaster();
        final int[] pixels = new int[image.getWidth()];
        for (int y = 0; y < image.getHeight(); y++) {
            raster.getPixels(0, y, image.getWidth(), 1, pixels);
            for (int i = 0; i < pixels.length; i++) {
                if (pixels[i] < threshold) {
                    pixels[i] = 0;
                }
                else {
                    pixels[i] = 255;
                }
            }
            raster.setPixels(0, y, image.getWidth(), 1, pixels);
        }
        return result;
    }
}
