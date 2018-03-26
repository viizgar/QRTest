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
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.common.detector.WhiteRectangleDetector;
import com.google.zxing.datamatrix.DataMatrixReader;

public class Scanner {


	private static Rectangle getDataMatrixPosition(final BufferedImage image, int c) {

		try {

			final BufferedImageLuminanceSource bils = new BufferedImageLuminanceSource(image);
			final HybridBinarizer hb = new HybridBinarizer(bils);// bils is BufferedImageLuminanceSource object
			BitMatrix bm;
			WhiteRectangleDetector wrd;

			bm = hb.getBlackMatrix();
			wrd = new WhiteRectangleDetector(bm);

			final ResultPoint[] rp = wrd.detect();

			ResultPoint pointA = correctPoints(rp[1], Vertices.TOPLEFT, c);
			ResultPoint pointB = correctPoints(rp[0], Vertices.BOTTOMLEFT, c);
			ResultPoint pointC = correctPoints(rp[3], Vertices.TOPRIGHT, c);
			ResultPoint pointD = correctPoints(rp[2], Vertices.BOTTOMRIGHT, c);

			final int x = (int) pointB.getX();
			final int y = (int) pointB.getY();

			final int w = (int) (pointD.getX() - pointB.getX());
			final int h = (int) (pointA.getY() - pointB.getY());

			return new Rectangle(x, y, w, h);

		} catch (final Exception e) {
			return null;
		}

	}

	private static String getTextByImage(BufferedImage image, int i) {
		String text = null;
		if (image != null) {
			int angle = 90;
			Rectangle rec = null;

			while (angle < 360) {

				final BufferedImage transformedImage = image.getSubimage(image.getWidth() * 8 / 11, 0,
						image.getWidth() * 3 / 11, image.getHeight() / 4);

				
				for (int c = 10; c > -1; c--) {

					rec = getDataMatrixPosition(transformedImage, c);

					if (rec != null) {
						BufferedImage dataMatrix = transformedImage.getSubimage((int) rec.getX(), (int) rec.getY(),
								(int) rec.getWidth(), (int) rec.getHeight());

						for (int rot = -10; rot < 10; rot++) {
							text = imageReader(dataMatrix, rot);
							if (text != null) {
								System.out.println(text);
								return text;
							}
						}

					}
				}
				image = rotateImageByDegrees(image, angle);

				angle += 90;
			}

		}
		if (text == null) {
			System.out.println("Page fail: " + (i + 1));

			 
		}

		else {
			System.out.println("The image cannot be readed");
		}
		return text;
	}

	public static String imageReader(BufferedImage image, final int i) {
		String text = imageReader(image, i, false);
		if (text == null) {
			text = imageReader(image, i, true);
		}

		return text;
	}

	public static String imageReader(BufferedImage image, final int i, boolean filter) {

		String text = null;
		BinaryBitmap bitmap = null;
		Result result = null;

		image = rotateImageByDegrees(image, i);

		LuminanceSource tmpSource = new BufferedImageLuminanceSource(image);
		bitmap = new BinaryBitmap(new HybridBinarizer(tmpSource));
		DataMatrixReader reader = new DataMatrixReader();

		
		  
		 

		final Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<>();
		decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		decodeHints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.DATA_MATRIX);
		decodeHints.put(DecodeHintType.CHARACTER_SET, CharacterSetECI.ISO8859_1);
		try {
			result = reader.decode(bitmap, decodeHints);
			text = result.getText();

		} catch (final Exception e) {
			}

		return text;
	}

	private enum Vertices {
		TOPLEFT, BOTTOMLEFT, TOPRIGHT, BOTTOMRIGHT
	}

	private static ResultPoint correctPoints(ResultPoint point, Vertices vertice, int correction) {

		if (vertice.equals(Vertices.TOPLEFT))
			return new ResultPoint(point.getX() - correction, point.getY() + correction);
		else if (vertice.equals(Vertices.BOTTOMLEFT)) {
			return new ResultPoint(point.getX() - correction, point.getY() - correction);
		} else if (vertice.equals(Vertices.TOPRIGHT)) {
			return new ResultPoint(point.getX() + correction, point.getY() + correction);
		} else {
			return new ResultPoint(point.getX() + correction, point.getY() - correction);
		}

	}

	public static List<String> readQRCode(final File file, final boolean printQR) {
		final List<String> codes = new ArrayList<>();

		if (FilenameUtils.getExtension(file.getName()).equals("pdf")) {
			PDDocument document;
			try {
				document = PDDocument.load(file);
				final PDFRenderer pdfRenderer = new PDFRenderer(document);
				int i = 0;
				for (final PDPage page : document.getPages()) {
					String text = null;
					BufferedImage image = null;
					for (final COSName name : page.getResources().getXObjectNames()) {
						final PDXObject resource = page.getResources().getXObject(name);

					}

					if (image == null) {
						int resolution = 500;
						image = pdfRenderer.renderImageWithDPI(i, resolution, ImageType.RGB);
						text = getTextByImage(image, i);
						
					}

					if (text == null) {
						System.out.println("Error happens on : " + (i + 1) + " page");
					}

					codes.add(text);
					i++;
				}
				document.close();
				System.out.println(i + " pages/images charged");
			} catch (final Exception e) {
				e.printStackTrace();
			}
		} else

		{
			BufferedImage image = null;
			try {
				image = ImageIO.read(file);
				String text = getTextByImage(image, 0);

				if (image != null && printQR) {
					final JFrame frame = new JFrame();
					frame.getContentPane().setLayout(new FlowLayout());
					frame.getContentPane().add(new JLabel(new ImageIcon(image)));
					frame.pack();
					frame.setVisible(true);
				}

				if (image != null && text == null) {
					if (image.getWidth() > image.getHeight()) {
						BufferedImage transformedImage = image.getSubimage(image.getWidth() * 5 / 6, 0,
								image.getWidth() / 6, image.getHeight() / 4);
						text = imageReader(transformedImage, 0);
						if (text == null) {
							transformedImage = image.getSubimage(0, image.getHeight() * 3 / 4, image.getWidth() / 6,
									image.getHeight() / 4);
							text = imageReader(transformedImage, 0);
						}
					} else {
						BufferedImage transformedImage = image.getSubimage(0, 0, image.getWidth() / 6,
								image.getHeight() / 4);
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

}
