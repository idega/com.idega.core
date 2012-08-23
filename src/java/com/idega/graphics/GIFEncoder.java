package com.idega.graphics;

import java.io.*;
import java.awt.*;
import java.awt.image.*;

/**
 * GIFEncoder is a class which takes an image and saves it to a stream
 * using the GIF file format (<A
 * HREF="http://www.dcs.ed.ac.uk/%7Emxr/gfx/">Graphics Interchange
 * Format</A>). A GIFEncoder
 * is constructed with either an AWT Image (which must be fully
 * loaded) or a set of RGB arrays. The image can be written out with a
 * call to <CODE>Write</CODE>.<P>
 *
 * Three caveats:
 * <UL>
 *   <LI>GIFEncoder will convert the image to indexed color upon
 *   construction. This will take some time, depending on the size of
 *   the image. Also, actually writing the image out (Write) will take
 *   time.<P>
 *
 *   <LI>The image cannot have more than 256 colors, since GIF is an 8
 *   bit format. For a 24 bit to 8 bit quantization algorithm, see
 *   Graphics Gems II III.2 by Xialoin Wu. Or check out his <A
 *   HREF="http://www.csd.uwo.ca/faculty/wu/cq.c">C source</A>.<P>
 *
 *   <LI>Since the image must be completely loaded into memory,
 *   GIFEncoder may have problems with large images. Attempting to
 *   encode an image which will not fit into memory will probably
 *   result in the following exception:<P>
 *   <CODE>java.awt.AWTException: Grabber returned false: 192</CODE><P>
 * </UL><P>
 *
 * GIFEncoder is based upon gifsave.c, which was written and released
 * by:<P>
 * <CENTER>
 *                                  Sverre H. Huseby<BR>
 *                                   Bjoelsengt. 17<BR>
 *                                     N-0468 Oslo<BR>
 *                                       Norway<P>
 *
 *                                 Phone: +47 2 230539<BR>
 *                                 sverrehu@ifi.uio.no<P>
 * </CENTER>
 * @version 0.90 21 Apr 1996
 * @author <A HREF="http://www.cs.brown.edu/people/amd/">Adam Doppelt</A> */
public class GIFEncoder {
  short width_;
  short height_;
  int numColors_;
  byte pixels_[];
  byte colors_[];

	/**
	 * 
	 * @uml.property name="sd_"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	ScreenDescriptor sd_;

	/**
	 * 
	 * @uml.property name="id_"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	ImageDescriptor id_;

  /**
   * Construct a GIFEncoder. The constructor will convert the image to
   * an indexed color array. <B>This may take some time.</B><P>
   *
   * @param image The image to encode. The image <B>must</B> be completely loaded.
   * @exception AWTException Will be thrown if the pixel grab fails. This
   * can happen if Java runs out of memory. It may also indicate that the image
   * contains more than 256 colors.
   **/

  public GIFEncoder(Image image) throws AWTException {
    this.width_ = (short)image.getWidth(null);
	  this.height_ = (short)image.getHeight(null);

	  int values[] = new int[this.width_ * this.height_];
	  PixelGrabber grabber = new PixelGrabber(image,0,0,this.width_,this.height_,values,0,this.width_);

	  try {
      if(grabber.grabPixels() != true) {
				throw new AWTException("Grabber returned false: " + grabber.status());
			}
	  }
	  catch (InterruptedException e) {
  	}

	  byte r[][] = new byte[this.width_][this.height_];
	  byte g[][] = new byte[this.width_][this.height_];
	  byte b[][] = new byte[this.width_][this.height_];
	  int index = 0;
	  for (int y = 0; y < this.height_; y++) {
			for (int x = 0; x < this.width_; x++) {
		    r[x][y] = (byte)((values[index] >> 16) & 0xFF);
		    g[x][y] = (byte)((values[index] >> 8) & 0xFF);
		    b[x][y] = (byte)((values[index]) & 0xFF);
		    index++;
	    }
		}
	    ToIndexedColor(r, g, b);
    }

  /**
   * Construct a GIFEncoder. The constructor will convert the image to
   * an indexed color array. <B>This may take some time.</B><P>
   *
   * Each array stores intensity values for the image. In other words,
   * r[x][y] refers to the red intensity of the pixel at column x, row
   * y.<P>
   *
   * @param r An array containing the red intensity values.
   * @param g An array containing the green intensity values.
   * @param b An array containing the blue intensity values.
   *
   * @exception AWTException Will be thrown if the image contains more than
   * 256 colors.
   **/
  public GIFEncoder(byte r[][], byte g[][], byte b[][]) throws AWTException {
	  this.width_ = (short)(r.length);
	  this.height_ = (short)(r[0].length);

	  ToIndexedColor(r, g, b);
  }

  /**
   * Writes the image out to a stream in the GIF file format. This will
   * be a single GIF87a image, non-interlaced, with no background color.
   * <B>This may take some time.</B><P>
   *
   * @param output The stream to output to. This should probably be a
   * buffered stream.
   *
   * @exception IOException Will be thrown if a write operation fails.
   **/
  public void Write(OutputStream output) throws IOException {
	  BitUtils.WriteString(output, "GIF87a");

	  ScreenDescriptor sd = new ScreenDescriptor(this.width_,this.height_,this.numColors_);
	  sd.Write(output);

	  output.write(this.colors_,0,this.colors_.length);

  	ImageDescriptor id = new ImageDescriptor(this.width_,this.height_,',');
	  id.Write(output);

  	byte codesize = BitUtils.BitsNeeded(this.numColors_);
	  if (codesize == 1) {
			codesize++;
		}
	  output.write(codesize);

	  LZWCompressor.LZWCompress(output,codesize,this.pixels_);
	  output.write(0);

	  id = new ImageDescriptor((byte)0,(byte)0,';');
	  id.Write(output);
	  output.flush();
  }

  void ToIndexedColor(byte r[][], byte g[][], byte b[][]) throws AWTException {
	  this.pixels_ = new byte[this.width_ * this.height_];
	  this.colors_ = new byte[256 * 3];
	  int colornum = 0;
	  for (int x = 0; x < this.width_; x++) {
	    for (int y = 0; y < this.height_; y++) {
		    int search;
		    for (search = 0; search < colornum; search++) {
					if (this.colors_[search * 3] == r[x][y] &&
			        this.colors_[search * 3 + 1] == g[x][y] &&
              this.colors_[search * 3 + 2] == b[x][y]) {
						break;
					}
				}

		      if (search > 255) {
						throw new AWTException("Too many colors.");
					}

		      this.pixels_[y * this.width_ + x] = (byte)search;

		      if (search == colornum) {
		        this.colors_[search * 3] = r[x][y];
		        this.colors_[search * 3 + 1] = g[x][y];
		        this.colors_[search * 3 + 2] = b[x][y];
		        colornum++;
		      }
	      }
	    }
	    this.numColors_ = 1 << BitUtils.BitsNeeded(colornum);
	    byte copy[] = new byte[this.numColors_ * 3];
	    System.arraycopy(this.colors_,0,copy,0,this.numColors_ * 3);
	    this.colors_ = copy;
    }
  }

  class BitFile {
    OutputStream output_;
    byte buffer_[];
    int index_;
    int bitsLeft_;

    public BitFile(OutputStream output) {
	    this.output_ = output;
	    this.buffer_ = new byte[256];
	    this.index_ = 0;
	    this.bitsLeft_ = 8;
    }

    public void Flush() throws IOException {
	    int numBytes = this.index_ + (this.bitsLeft_ == 8 ? 0 : 1);
	    if (numBytes > 0) {
	      this.output_.write(numBytes);
	      this.output_.write(this.buffer_, 0, numBytes);
	      this.buffer_[0] = 0;
	      this.index_ = 0;
	      this.bitsLeft_ = 8;
	    }
    }

    @SuppressWarnings("unused")
	public void WriteBits(int bits, int numbits) throws IOException {
	    int bitsWritten = 0;
	    int numBytes = 255;
	    do {
	      if ((this.index_ == 254 && this.bitsLeft_ == 0) || this.index_ > 254) {
		      this.output_.write(numBytes);
		      this.output_.write(this.buffer_, 0, numBytes);

		      this.buffer_[0] = 0;
		      this.index_ = 0;
		      this.bitsLeft_ = 8;
	      }

	      if (numbits <= this.bitsLeft_) {
		      this.buffer_[this.index_] |= (bits & ((1 << numbits) - 1)) << (8 - this.bitsLeft_);
		      bitsWritten += numbits;
		      this.bitsLeft_ -= numbits;
		      numbits = 0;
	      }
	      else {
		      this.buffer_[this.index_] |= (bits & ((1 << this.bitsLeft_) - 1)) << (8 - this.bitsLeft_);
		      bitsWritten += this.bitsLeft_;
		      bits >>= this.bitsLeft_;
		      numbits -= this.bitsLeft_;
		      this.buffer_[++this.index_] = 0;
		      this.bitsLeft_ = 8;
        }
	    }
      while (numbits != 0);
    }
  }

  class LZWStringTable {
    private final static int RES_CODES = 2;
    private final static short HASH_FREE = (short)0xFFFF;
    private final static short NEXT_FIRST = (short)0xFFFF;
    private final static int MAXBITS = 12;
    private final static int MAXSTR = (1 << MAXBITS);
    private final static short HASHSIZE = 9973;
    private final static short HASHSTEP = 2039;

    byte strChr_[];
    short strNxt_[];
    short strHsh_[];
    short numStrings_;

    public LZWStringTable() {
	    this.strChr_ = new byte[MAXSTR];
	    this.strNxt_ = new short[MAXSTR];
	    this.strHsh_ = new short[HASHSIZE];
    }

    public int AddCharString(short index, byte b) {
	    int hshidx;

	    if (this.numStrings_ >= MAXSTR) {
				return 0xFFFF;
			}

	    hshidx = Hash(index, b);
	    while (this.strHsh_[hshidx] != HASH_FREE) {
				hshidx = (hshidx + HASHSTEP) % HASHSIZE;
			}

	    this.strHsh_[hshidx] = this.numStrings_;
	    this.strChr_[this.numStrings_] = b;
	    this.strNxt_[this.numStrings_] = (index != HASH_FREE) ? index : NEXT_FIRST;

	    return this.numStrings_++;
    }

    public short FindCharString(short index, byte b) {
	    int hshidx, nxtidx;

	    if (index == HASH_FREE) {
				return b;
			}

	    hshidx = Hash(index, b);
	    while ((nxtidx = this.strHsh_[hshidx]) != HASH_FREE) {
	      if (this.strNxt_[nxtidx] == index && this.strChr_[nxtidx] == b) {
					return (short)nxtidx;
				}
	      hshidx = (hshidx + HASHSTEP) % HASHSIZE;
	    }

    	return (short)0xFFFF;
    }

    public void ClearTable(int codesize) {
	    this.numStrings_ = 0;

	    for (int q = 0; q < HASHSIZE; q++) {
	      this.strHsh_[q] = HASH_FREE;
	    }

	    int w = (1 << codesize) + RES_CODES;
	    for (int q = 0; q < w; q++) {
				AddCharString((short)0xFFFF, (byte)q);
			}
    }

    static public int Hash(short index, byte lastbyte) {
	    return (((short)(lastbyte << 8) ^ index) & 0xFFFF) % HASHSIZE;
    }
  }

  class LZWCompressor {
    public static void LZWCompress(OutputStream output, int codesize, byte toCompress[]) throws IOException {
	    byte c;
	    short index;
	    int clearcode, endofinfo, numbits, limit;
	    short prefix = (short)0xFFFF;

	    BitFile bitFile = new BitFile(output);
	    LZWStringTable strings = new LZWStringTable();

	    clearcode = 1 << codesize;
	    endofinfo = clearcode + 1;

	    numbits = codesize + 1;
	    limit = (1 << numbits) - 1;

	    strings.ClearTable(codesize);
	    bitFile.WriteBits(clearcode, numbits);

	    for (int loop = 0; loop < toCompress.length; ++loop) {
	      c = toCompress[loop];
	      if ((index = strings.FindCharString(prefix, c)) != -1) {
					prefix = index;
				}
				else {
		      bitFile.WriteBits(prefix, numbits);
		      if (strings.AddCharString(prefix, c) > limit) {
		        if (++numbits > 12) {
			        bitFile.WriteBits(clearcode, numbits - 1);
			        strings.ClearTable(codesize);
			        numbits = codesize + 1;
    		    }
		        limit = (1 << numbits) - 1;
		      }

		      prefix = (short)(c & 0xFF);
	      }
	    }

	    if (prefix != -1) {
				bitFile.WriteBits(prefix, numbits);
			}

	    bitFile.WriteBits(endofinfo, numbits);
	    bitFile.Flush();
    }
  }

  class ScreenDescriptor {
    public short localScreenWidth_;
    public short localScreenHeight_;
    private byte byte_;
    public byte backgroundColorIndex_;
    public byte pixelAspectRatio_;

    public ScreenDescriptor(short width, short height, int numColors) {
	    this.localScreenWidth_ = width;
	    this.localScreenHeight_ = height;
	    SetGlobalColorTableSize((byte)(BitUtils.BitsNeeded(numColors) - 1));
	    SetGlobalColorTableFlag((byte)1);
	    SetSortFlag((byte)0);
	    SetColorResolution((byte)7);
	    this.backgroundColorIndex_ = 0;
	    this.pixelAspectRatio_ = 0;
    }

    public void Write(OutputStream output) throws IOException {
	    BitUtils.WriteWord(output, this.localScreenWidth_);
	    BitUtils.WriteWord(output, this.localScreenHeight_);
	    output.write(this.byte_);
	    output.write(this.backgroundColorIndex_);
	    output.write(this.pixelAspectRatio_);
    }

    public void SetGlobalColorTableSize(byte num) {
	    this.byte_ |= (num & 7);
    }

    public void SetSortFlag(byte num) {
	    this.byte_ |= (num & 1) << 3;
    }

    public void SetColorResolution(byte num) {
	    this.byte_ |= (num & 7) << 4;
    }

    public void SetGlobalColorTableFlag(byte num) {
	    this.byte_ |= (num & 1) << 7;
    }
  }

  class ImageDescriptor {
    public byte separator_;
    public short leftPosition_;
    public short topPosition_;
    public short width_;
    public short height_;
    private byte byte_;

    public ImageDescriptor(short width, short height, char separator) {
	    this.separator_ = (byte)separator;
	    this.leftPosition_ = 0;
	    this.topPosition_ = 0;
	    this.width_ = width;
	    this.height_ = height;
	    SetLocalColorTableSize((byte)0);
	    SetReserved((byte)0);
	    SetSortFlag((byte)0);
	    SetInterlaceFlag((byte)0);
	    SetLocalColorTableFlag((byte)0);
    }

    public void Write(OutputStream output) throws IOException {
	    output.write(this.separator_);
	    BitUtils.WriteWord(output, this.leftPosition_);
	    BitUtils.WriteWord(output, this.topPosition_);
	    BitUtils.WriteWord(output, this.width_);
	    BitUtils.WriteWord(output, this.height_);
	    output.write(this.byte_);
    }

    public void SetLocalColorTableSize(byte num) {
	    this.byte_ |= (num & 7);
    }

    public void SetReserved(byte num) {
	    this.byte_ |= (num & 3) << 3;
    }

    public void SetSortFlag(byte num) {
	    this.byte_ |= (num & 1) << 5;
    }

    public void SetInterlaceFlag(byte num) {
	    this.byte_ |= (num & 1) << 6;
    }

    public void SetLocalColorTableFlag(byte num) {
	    this.byte_ |= (num & 1) << 7;
    }
  }

  class BitUtils {
    public static byte BitsNeeded(int n) {
	    byte ret = 1;

	    if (n-- == 0) {
				return 0;
			}

	    while ((n >>= 1) != 0) {
				ret++;
			}

    	return ret;
    }

    public static void WriteWord(OutputStream output, short w) throws IOException {
	    output.write(w & 0xFF);
	    output.write((w >> 8) & 0xFF);
    }

    static void WriteString(OutputStream output, String string) throws IOException {
      for (int loop = 0; loop < string.length(); ++loop) {
				output.write((byte)(string.charAt(loop)));
			}
    }
  }
