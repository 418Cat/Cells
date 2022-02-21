package mainPkg;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.image.BufferedImage;

public class Main {
	
	public static FileConvert fc;
	public static ImageInputStream is;
	public static Frame frame;
	public static Arguments arg;
	public static int imgNb = 0;
	
	public static void main(String[] args) {
		
		arg = new Arguments();
		arg.setArgs(args);
		ImageInputStream is = null;
		Iterator<ImageReader> iterator = null;
		try {
			is = ImageIO.createImageInputStream(arg.getOutput());
			if (is == null || is.length() == 0){
			}
			if (iterator == null || !iterator.hasNext()) {
				  throw new IOException("Image file format not supported by ImageIO: " + arg.allArgs.get("input"));
			}
		} catch(Exception e) {
			e.toString();
		}
		iterator = ImageIO.getImageReaders(is);
		ImageReader reader = (ImageReader) iterator.next();
		iterator = null;
		reader.setInput(is);
		try {
			reader.read(0);
			imgNb = reader.getNumImages(true);
		} catch(Exception e) {
			System.out.println(e.toString());
		}
		frame = new Frame();
		if(!arg.allArgs.containsKey("panel")) {
			try {
				frame.initialize(new int[] {Integer.parseInt(arg.allArgs.get("size").split(",")[0]), Integer.parseInt(arg.allArgs.get("size").split(",")[1])}, (int)(reader.getNumImages(true)/3), new int[] {0, 0});
				for(ArrayList<Panel> arrP : frame.panels) {
					for(Panel p : arrP) {
						p.img[0] = (BufferedImage)reader.read(p.pos*3);
						p.img[1] = (BufferedImage)reader.read((p.pos*3)+1);
						p.img[2] = (BufferedImage)reader.read((p.pos*3)+2);
					}
				}
			} catch(Exception e) {
				System.err.println("An error occured :\n" + e.toString());
				System.exit(0);
			}
		} else {
			try {
				frame.initialize(new int[] {Integer.parseInt(arg.allArgs.get("size").split(",")[0]), Integer.parseInt(arg.allArgs.get("size").split(",")[1])}, 1, new int[] {0, 0});
				frame.panels.get(0).get(0).img[0] = (BufferedImage)reader.read(Integer.parseInt(arg.allArgs.get("panel"))*3);
				frame.panels.get(0).get(0).img[1] = (BufferedImage)reader.read(Integer.parseInt(arg.allArgs.get("panel"))*3+1);
				frame.panels.get(0).get(0).img[2] = (BufferedImage)reader.read(Integer.parseInt(arg.allArgs.get("panel"))*3+2);
			} catch (Exception e) {
				System.err.println("An error occured :\n" + e.toString());
				System.exit(0);
			}
		}
		frame.allPanel();
	}
}
