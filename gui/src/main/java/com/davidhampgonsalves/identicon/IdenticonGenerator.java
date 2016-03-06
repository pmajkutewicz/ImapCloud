package com.davidhampgonsalves.identicon;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class IdenticonGenerator {
	private static final int ZERO = 0;
	private static final int ONE = 1;
	private static final int SIX = 6;
	private static final int SEVEN = 7;
	private static final int EIGTH = 8;
	private static final int TWO_FIVE_FIVE = 255;

	private static int height = EIGTH;
	private static int width = EIGTH;

	public static BufferedImage generate(String userName, HashGeneratorInterface hashGenerator) {
		byte[] hash = hashGenerator.generate(userName);

		BufferedImage identicon = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = identicon.getRaster();

		//get byte values as unsigned ints
		int r = hash[0] & TWO_FIVE_FIVE;
		int g = hash[1] & TWO_FIVE_FIVE;
		int b = hash[2] & TWO_FIVE_FIVE;

		int[] background = new int[]{TWO_FIVE_FIVE, TWO_FIVE_FIVE, TWO_FIVE_FIVE, ZERO};
		int[] foreground = new int[]{r, g, b, TWO_FIVE_FIVE};

		for (int x = ZERO; x < width; x++) {
			int i = x < SIX ? x : SEVEN - x;
			for (int y = ZERO; y < height; y++) {
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
}
