package com.davidhampgonsalves.identicon;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class IdenticonGenerator {

	private static final int SIZE_IN_PX = 8;
	private static final int HEIGHT = SIZE_IN_PX;
	private static final int WIDTH = SIZE_IN_PX;
	private static final int SCALE = 48;

	private static final int SIZE_MINUS_ONE = SIZE_IN_PX - 1;
	private static final int SIZE_MINUS_TWO = SIZE_IN_PX - 2;
	private static final int ZERO = 0;
	private static final int ONE = 1;
	private static final int TWO_FIVE_FIVE = 255;

	public static BufferedImage generate(String userName, HashGeneratorInterface hashGenerator) {
		byte[] hash = hashGenerator.generate(userName);

		BufferedImage identicon = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = identicon.getRaster();

		//get byte values as unsigned ints
		int r = hash[0] & TWO_FIVE_FIVE;
		int g = hash[1] & TWO_FIVE_FIVE;
		int b = hash[2] & TWO_FIVE_FIVE;

		int[] background = new int[]{TWO_FIVE_FIVE, TWO_FIVE_FIVE, TWO_FIVE_FIVE, ZERO};
		int[] foreground = new int[]{r, g, b, TWO_FIVE_FIVE};

		for (int x = ZERO; x < WIDTH; x++) {
			int i = x < SIZE_MINUS_TWO ? x : SIZE_MINUS_ONE - x;
			for (int y = ZERO; y < HEIGHT; y++) {
				int[] pixelColor;
				if ((hash[i] >> y & ONE) == ONE) {
					pixelColor = foreground;
				} else {
					pixelColor = background;
				}
				raster.setPixel(x, y, pixelColor);
			}
		}

		return identicon;
	}

	public static WritableImage generateWithoutSmoothing(String userName, HashGeneratorInterface hashGenerator) {
		BufferedImage generated = generate(userName, hashGenerator);
		WritableImage input = new WritableImage(generated.getWidth(), generated.getHeight());
		SwingFXUtils.toFXImage(generated, input);

		final int width = (int) input.getWidth();
		final int height = (int) input.getHeight();
		final int scale = SCALE;

		WritableImage output = new WritableImage(width * scale, height * scale);

		PixelReader reader = input.getPixelReader();
		PixelWriter writer = output.getPixelWriter();

		for (int y = ZERO; y < height; y++) {
			for (int x = ZERO; x < width; x++) {
				final int argb = reader.getArgb(x, y);
				for (int dy = ZERO; dy < scale; dy++) {
					for (int dx = ZERO; dx < scale; dx++) {
						writer.setArgb(x * scale + dx, y * scale + dy, argb);
					}
				}
			}
		}
		return output;
	}

}
