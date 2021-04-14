/*
 * Java Color Thief
 * by Sven Woltmann, Fonpit AG
 * Ported to Minecraft NativeImage by Una
 * 
 * https://www.androidpit.com
 * https://www.androidpit.de
 *
 * License
 * -------
 * Creative Commons Attribution 2.5 License:
 * http://creativecommons.org/licenses/by/2.5/
 *
 * Thanks
 * ------
 * Lokesh Dhakar - for the original Color Thief JavaScript version
 * available at http://lokeshdhakar.com/projects/color-thief/
 */

package com.unascribed.yttr.util.math.colorthief;

import java.util.Arrays;

import com.unascribed.yttr.util.math.colorthief.MMCQ.CMap;

import net.minecraft.client.texture.NativeImage;

public class ColorThiefMC {

	private static final int DEFAULT_QUALITY = 10;
	private static final boolean DEFAULT_IGNORE_WHITE = true;

	/**
	 * Use the median cut algorithm to cluster similar colors and return the base color from the
	 * largest cluster.
	 *
	 * @param sourceImage
	 *            the source image
	 *
	 * @return the dominant color as RGB array
	 */
	public static int[] getColor(NativeImage sourceImage) {
		int[][] palette = getPalette(sourceImage, 5);
		if (palette == null) {
			return null;
		}
		int[] dominantColor = palette[0];
		return dominantColor;
	}

	/**
	 * Use the median cut algorithm to cluster similar colors and return the base color from the
	 * largest cluster.
	 *
	 * @param sourceImage
	 *            the source image
	 * @param quality
	 *            1 is the highest quality settings. 10 is the default. There is a trade-off between
	 *            quality and speed. The bigger the number, the faster a color will be returned but
	 *            the greater the likelihood that it will not be the visually most dominant color.
	 * @param ignoreWhite
	 *            if <code>true</code>, white pixels are ignored
	 *
	 * @return the dominant color as RGB array
	 * @throws IllegalArgumentException
	 *             if quality is &lt; 1
	 */
	public static int[] getColor(NativeImage sourceImage, int quality, boolean ignoreWhite) {
		int[][] palette = getPalette(sourceImage, 5, quality, ignoreWhite);
		if (palette == null) {
			return null;
		}
		int[] dominantColor = palette[0];
		return dominantColor;
	}

	/**
	 * Use the median cut algorithm to cluster similar colors.
	 * 
	 * @param sourceImage
	 *            the source image
	 * @param colorCount
	 *            the size of the palette; the number of colors returned
	 * 
	 * @return the palette as array of RGB arrays
	 */
	public static int[][] getPalette(NativeImage sourceImage, int colorCount) {
		CMap cmap = getColorMap(sourceImage, colorCount);
		if (cmap == null) {
			return null;
		}
		return cmap.palette();
	}

	/**
	 * Use the median cut algorithm to cluster similar colors.
	 * 
	 * @param sourceImage
	 *            the source image
	 * @param colorCount
	 *            the size of the palette; the number of colors returned
	 * @param quality
	 *            1 is the highest quality settings. 10 is the default. There is a trade-off between
	 *            quality and speed. The bigger the number, the faster the palette generation but
	 *            the greater the likelihood that colors will be missed.
	 * @param ignoreWhite
	 *            if <code>true</code>, white pixels are ignored
	 * 
	 * @return the palette as array of RGB arrays
	 * @throws IllegalArgumentException
	 *             if quality is &lt; 1
	 */
	public static int[][] getPalette(
			NativeImage sourceImage,
			int colorCount,
			int quality,
			boolean ignoreWhite) {
		CMap cmap = getColorMap(sourceImage, colorCount, quality, ignoreWhite);
		if (cmap == null) {
			return null;
		}
		return cmap.palette();
	}

	/**
	 * Use the median cut algorithm to cluster similar colors.
	 * 
	 * @param sourceImage
	 *            the source image
	 * @param colorCount
	 *            the size of the palette; the number of colors returned (minimum 2, maximum 256)
	 * 
	 * @return the color map
	 */
	public static CMap getColorMap(NativeImage sourceImage, int colorCount) {
		return getColorMap(sourceImage, colorCount, DEFAULT_QUALITY, DEFAULT_IGNORE_WHITE);
	}

	/**
	 * Use the median cut algorithm to cluster similar colors.
	 * 
	 * @param sourceImage
	 *            the source image
	 * @param colorCount
	 *            the size of the palette; the number of colors returned (minimum 2, maximum 256)
	 * @param quality
	 *            1 is the highest quality settings. 10 is the default. There is a trade-off between
	 *            quality and speed. The bigger the number, the faster the palette generation but
	 *            the greater the likelihood that colors will be missed.
	 * @param ignoreWhite
	 *            if <code>true</code>, white pixels are ignored
	 * 
	 * @return the color map
	 * @throws IllegalArgumentException
	 *             if quality is &lt; 1
	 */
	public static CMap getColorMap(
			NativeImage sourceImage,
			int colorCount,
			int quality,
			boolean ignoreWhite) {
		if (colorCount < 2 || colorCount > 256) {
			throw new IllegalArgumentException("Specified colorCount must be between 2 and 256.");
		}
		if (quality < 1) {
			throw new IllegalArgumentException("Specified quality should be greater then 0.");
		}

		int[][] pixelArray = getPixels(sourceImage, quality, ignoreWhite);

		// Send array to quantize function which clusters values using median cut algorithm
		CMap cmap = MMCQ.quantize(pixelArray, colorCount);
		return cmap;
	}

	/**
	 * Gets the image's pixels via BufferedImage.getRaster().getDataBuffer(). Fast, but doesn't work
	 * for all color models.
	 * 
	 * @param sourceImage
	 *            the source image
	 * @param quality
	 *            1 is the highest quality settings. 10 is the default. There is a trade-off between
	 *            quality and speed. The bigger the number, the faster the palette generation but
	 *            the greater the likelihood that colors will be missed.
	 * @param ignoreWhite
	 *            if <code>true</code>, white pixels are ignored
	 * 
	 * @return an array of pixels (each an RGB int array)
	 */
	private static int[][] getPixels(
			NativeImage sourceImage,
			int quality,
			boolean ignoreWhite) {
		int[] pixels = sourceImage.makePixelArray();
		int pixelCount = sourceImage.getWidth() * sourceImage.getHeight();

		int expectedDataLength = pixelCount;
		if (expectedDataLength != pixels.length) {
			throw new IllegalArgumentException(
					"(expectedDataLength = " + expectedDataLength + ") != (pixels.length = "
							+ pixels.length + ")");
		}

		// Store the RGB values in an array format suitable for quantize function

		// numRegardedPixels must be rounded up to avoid an ArrayIndexOutOfBoundsException if all
		// pixels are good.
		int numRegardedPixels = (pixelCount + quality - 1) / quality;

		int numUsedPixels = 0;
		int[][] pixelArray = new int[numRegardedPixels][];
		int offset, r, g, b, a;

		for (int i = 0; i < pixelCount; i += quality) {
			offset = i;
			a = NativeImage.getAlpha(pixels[offset]);
			b = NativeImage.getBlue(pixels[offset]);
			g = NativeImage.getGreen(pixels[offset]);
			r = NativeImage.getRed(pixels[offset]);

			// If pixel is mostly opaque and not white
			if (a >= 125 && !(ignoreWhite && r > 250 && g > 250 && b > 250)) {
				pixelArray[numUsedPixels] = new int[] {r, g, b};
				numUsedPixels++;
			}
		}

		// Remove unused pixels from the array
		return Arrays.copyOfRange(pixelArray, 0, numUsedPixels);
	}

}
