/* Decompiled by Mocha from Thumbnail.class */
/* Originally compiled from Thumbnail.java */

package com.idega.util;

import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import com.idega.io.MemoryFileBuffer;
import com.idega.io.MemoryOutputStream;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public  class Thumbnail
{
    public Thumbnail()
    {
    }

    public static void main(String args[])
        throws Exception
    {
        long start = System.currentTimeMillis();
        if (args.length < 5)
        {
            System.err.println("Usage: java Thumbnail INFILE OUTFILE WIDTH HEIGHT QUALITY");
            System.exit(1);
        }
        String fileName = args[0];
        String thumbName = args[1];
        int thumbWidth = Integer.parseInt(args[2]);
        int thumbHeight = Integer.parseInt(args[3]);
        int quality = Integer.parseInt(args[4]);
        createThumbnail(fileName, thumbName, thumbWidth, thumbHeight, quality);
        long end = System.currentTimeMillis();
        System.out.println("scaling took "+((end-start)/1000)+" seconds");
        System.exit(0);
    }

    public static void createThumbnail(String fileName, String thumbName, int thumbWidth, int thumbHeight, int quality)
        throws Exception
    {
        Image image = Toolkit.getDefaultToolkit().getImage(fileName);
        MediaTracker mediaTracker = new MediaTracker(new Frame());
        mediaTracker.addImage(image, 0);
        mediaTracker.waitForID(0);
        double thumbRatio = (double)thumbWidth / thumbHeight;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double imageRatio = (double)imageWidth / imageHeight;
        if (thumbRatio < imageRatio) {
			thumbHeight = (int)(thumbWidth / imageRatio);
		}
		else {
			thumbWidth = (int)(thumbHeight * imageRatio);
		}
        BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, 1);
        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(thumbName));
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(thumbImage);
        quality = Math.max(0, Math.min(quality, 100));
        //param.setQuality()
        param.setQuality((float)quality / 100, false);
        encoder.setJPEGEncodeParam(param);
        encoder.encode(thumbImage);
    }

    public static MemoryFileBuffer createMemoryJPEG(MemoryFileBuffer buf, int thumbWidth, int thumbHeight, int quality)
        throws Exception
    {
        Image image = Toolkit.getDefaultToolkit().createImage(buf.buffer());
        MediaTracker mediaTracker = new MediaTracker(new Frame());
        mediaTracker.addImage(image, 0);
        mediaTracker.waitForID(0);
        double thumbRatio = (double)thumbWidth / thumbHeight;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double imageRatio = (double)imageWidth / imageHeight;
        if (thumbRatio < imageRatio) {
			thumbHeight = (int)(thumbWidth / imageRatio);
		}
		else {
			thumbWidth = (int)(thumbHeight * imageRatio);
		}
        BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, 1);
        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
        MemoryFileBuffer buffer = new MemoryFileBuffer();
        MemoryOutputStream out = new MemoryOutputStream(buffer);
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(thumbImage);
        quality = Math.max(0, Math.min(quality, 100));
        param.setQuality((float)quality / 100, false);
        encoder.setJPEGEncodeParam(param);
        encoder.encode(thumbImage);
        return buffer;
    }
}
