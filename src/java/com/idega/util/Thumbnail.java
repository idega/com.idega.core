/* Decompiled by Mocha from Thumbnail.class */
/* Originally compiled from Thumbnail.java */

package com.idega.util;

import com.sun.image.codec.jpeg.*;
import java.awt.*;
import java.io.*;
import com.idega.io.MemoryFileBuffer;
import com.idega.io.MemoryOutputStream;
import java.awt.image.BufferedImage;

public  class Thumbnail
{
    public Thumbnail()
    {
    }

    public static void main(String args[])
        throws Exception
    {
        if (args.length < 5)
        {
            System.err.println("Usage: java Thumbnail INFILE OUTFILE WIDTH HEIGHT QUALITY");
            System.exit(1);
        }
        long start = System.currentTimeMillis();
        String fileName = args[0];
        String thumbName = args[1];
        int thumbWidth = Integer.parseInt(args[2]);
        int thumbHeight = Integer.parseInt(args[3]);
        int quality = Integer.parseInt(args[4]);
        createThumbnail(fileName, thumbName, thumbWidth, thumbHeight, quality);
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
        if (thumbRatio < imageRatio)
            thumbHeight = (int)((double)thumbWidth / imageRatio);
        else
            thumbWidth = (int)((double)thumbHeight * imageRatio);
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
        if (thumbRatio < imageRatio)
            thumbHeight = (int)((double)thumbWidth / imageRatio);
        else
            thumbWidth = (int)((double)thumbHeight * imageRatio);
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
