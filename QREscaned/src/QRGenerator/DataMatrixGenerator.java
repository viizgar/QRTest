package QRGenerator;

import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.datamatrix.DataMatrixWriter;

public class DataMatrixGenerator {

    public static BufferedImage generateQR(final String xml) {
        final Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, CharacterSetECI.ISO8859_1);

        final int bigEnough = 144;
        final DataMatrixWriter writer = new DataMatrixWriter();
        final BitMatrix matrix = writer.encode(xml, BarcodeFormat.DATA_MATRIX, bigEnough, bigEnough, hints);

        final JFrame frame = new JFrame();
        frame.getContentPane()
             .setLayout(new FlowLayout());

        // BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
        //
        // frame.getContentPane()
        // .add(new JLabel(new ImageIcon(image)));

        final BufferedImage image = refactorImage(matrix, 4);

        frame.getContentPane()
             .add(new JLabel(new ImageIcon(image)));

        frame.pack();
        frame.setVisible(true);

        return image;
    }

    public static BufferedImage refactorImage(final BitMatrix bitMatrix, final int refactor) {
        final int BLACK = 0xFF000000;
        final int WHITE = 0xFFFFFFFF;

        // change the values to your needs
        final int requestedWidth = bitMatrix.getWidth() * refactor;
        final int requestedHeight = bitMatrix.getHeight() * refactor;

        final int width = bitMatrix.getWidth();
        final int height = bitMatrix.getHeight();

        // calculating the scaling factor
        int pixelsize = requestedWidth / width;
        if (pixelsize > requestedHeight / height) {
            pixelsize = requestedHeight / height;
        }

        final int[] pixels = new int[requestedWidth * requestedHeight];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * requestedWidth * pixelsize;

            // scaling pixel height
            for (int pixelsizeHeight = 0; pixelsizeHeight < pixelsize; pixelsizeHeight++, offset += requestedWidth) {
                for (int x = 0; x < width; x++) {
                    final int color = bitMatrix.get(x, y) ? BLACK : WHITE;

                    // scaling pixel width
                    for (int pixelsizeWidth = 0; pixelsizeWidth < pixelsize; pixelsizeWidth++) {
                        pixels[offset + x * pixelsize + pixelsizeWidth] = color;
                    }
                }
            }
        }

        final BufferedImage image = new BufferedImage(requestedWidth, requestedHeight, BufferedImage.TYPE_BYTE_BINARY);
        image.setRGB(0, 0, requestedWidth, requestedHeight, pixels, 0, requestedWidth);
        return image;

    }

    public static BufferedImage resizeImage(final BufferedImage img, final int newW, final int newH) {
        final Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        final BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        final Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
