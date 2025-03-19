package com.MiniProject.Drive.Controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;

public class Preview {
	public byte[] imagePreview(byte[] imageByte, int width) throws IOException {
		InputStream in = new ByteArrayInputStream(imageByte);
		BufferedImage originalImage = ImageIO.read(in);

		if(originalImage == null) {
			throw new IOException("포멧에 맞지않다.");
		}

		int height = (int)(width / ((double)originalImage.getWidth() / originalImage.getHeight()));

		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		Graphics2D g = resizedImage.createGraphics();

		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(resizedImage, "jpg", baos);
		return  baos.toByteArray();
	}

	public String textPreview(byte[] stringByte) {
		String text = new String(stringByte, StandardCharsets.UTF_8);

		return text.length() > 100 ? text.substring(0, 500) + "..." : text;
	}
}
