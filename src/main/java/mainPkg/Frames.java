package mainPkg;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Frames {

}

//What holds the r, g and b images and their informations
class Panel{
	int[][] bounds = {{0, 0}, {0, 0}};
	int pos;
	BufferedImage[] img = {null, null, null};
	int[] threshold = {0, 0, 0};
	int[] pixels = {0, 0, 0};
}

//The displayed frame
class Frame{
	JFrame frame;
	Graphics g;
	int nbPanel;
	int[] rows = {0, 0};
	int[] panelSize = {0, 0};
	int[] pixels = {0, 0, 0};
	ArrayList<ArrayList<Panel>> panels = new ArrayList<>();
	
	//Initialize the frame with a certain size, a certain panel number and a location
	public void initialize(int[] size, int nb, int[] location) {
		nbPanel = nb;
		int tmpPos = 0;
		
		if(Math.sqrt(nb)-(int)(Math.sqrt(nb)) >= 0.5) {
			rows[0] = (int)Math.ceil(Math.sqrt(nb));
			rows[1] = (int)Math.ceil(Math.sqrt(nb));
		} else {
			rows[0] = (int)Math.floor(Math.sqrt(nb));
			if(Math.sqrt(nb)-(int)(Math.sqrt(nb)) == 0){
				rows[1] = (int)Math.floor(Math.sqrt(nb));
			} else {
				rows[1] = (int)Math.ceil(Math.sqrt(nb));
			}
		}
		
		//for each y row, add an x panel row (under the form of an array)
		for(int i = 0; i < rows[1]; i++) {
			ArrayList<Panel> tmpPanels = new ArrayList<>();
			for(int j = 0; j < rows[0]; j++) {
				Panel tmp = new Panel();
				tmp.pos = tmpPos;
				tmp.bounds[0][0] = (int)(j%rows[0]);
				tmp.bounds[0][1] = (int)(i%rows[1]);
				tmp.bounds[1][0] = (int)(j%rows[0])+1;
				tmp.bounds[1][1] = (int)(i%rows[1])+1;
				tmp.threshold[0] = Integer.parseInt(Main.arg.allArgs.get("threshold").split(",")[0]);
				tmp.threshold[1] = Integer.parseInt(Main.arg.allArgs.get("threshold").split(",")[1]);
				tmp.threshold[2] = Integer.parseInt(Main.arg.allArgs.get("threshold").split(",")[2]);
				tmpPanels.add(tmp);
				tmpPos++;
			}
			panels.add(tmpPanels);
		}
		
		frame = new JFrame();
		frame.setSize(size[0], size[1]);
		frame.setLocation(location[0], location[1]);
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setBackground(Color.black);
		frame.setVisible(true);
		g = frame.getGraphics();
		
		panelSize[0] = frame.getWidth()/rows[0];
		panelSize[1] = frame.getHeight()/rows[1];
		
		//the frame needs a small time to initialize correctly
		try {
			Thread.sleep(50);
		} catch(Exception e) {}
		g.setColor(Color.black);
		g.fillRect(0, 0, size[0], size[1]);
	}
	
	//Display all panels
	public void allPanel() {
		for (ArrayList<Panel> p1 : panels) {
			for(Panel p2 : p1) {
				if(p2.img[0] != null) {
					panel(p2);
				}
			}
		}
		System.out.println("\nWhole grid");
		System.out.println("Total blue pixels : " + pixels[0] + "; Red on blue : " + 100*(float)pixels[1]/(float)pixels[0] + "%; Green on blue : " + 100*(float)pixels[2]/(float)pixels[0] + "%;");
		System.out.println("Grid depth :" + nbPanel*8 + "µm");
	}
	
	//Display the panel
	public void panel(Panel p) {
		
		//for each image, for each y & x row, if the pixel brightness is above the threshold, display it and add it to the pixel count (for the percentage)
		for(int i = 0; i < p.img.length; i++) {
			
			switch (i) {
			case 0:
				g.setColor(Color.blue);
				break;
			case 1:
				g.setColor(Color.red);
				break;
			case 2:
				g.setColor(Color.green);
				break;
			}
			
			for(int y = 0; y < p.img[i].getHeight(); y++) {
				
				for(int x = 0; x < p.img[i].getWidth(); x++) {
					if(new Color(p.img[i].getRGB(x, y)).getRed() > p.threshold[i] && (!Main.arg.allArgs.containsKey("radius") || (Main.arg.allArgs.containsKey("radius") && Math.sqrt(Math.pow((x - p.img[i].getWidth() / 2), 2) + Math.pow((y - p.img[i].getHeight() / 2), 2)) < Math.sqrt((p.img[i].getHeight()*p.img[i].getWidth()))*Integer.parseInt(Main.arg.allArgs.get("radius"))/2/100))) {
						g.fillRect((x*panelSize[0]/p.img[i].getWidth())+p.bounds[0][0]*panelSize[0], y*panelSize[1]/p.img[i].getHeight()+p.bounds[0][1]*panelSize[1], 1, 1);
						switch(i) {
						case 0:
							p.pixels[0]++;
							pixels[0]++;
							break;
						case 1:
							if(new Color(p.img[0].getRGB(x, y)).getRed() > p.threshold[0]) {
								p.pixels[1]++;
								pixels[1]++;
							}
							break;
						case 2:
							if(new Color(p.img[0].getRGB(x, y)).getRed() > p.threshold[0]) {
								p.pixels[2]++;
								pixels[2]++;
							}
						}
					}
				}
			}
		}
		System.out.println("\nPanel " + p.pos);
		System.out.println("Total blue pixels : " + p.pixels[0] + "; Red on blue : " + 100*(float)p.pixels[1]/(float)p.pixels[0] + "%; Green on blue : " + 100*(float)p.pixels[2]/(float)p.pixels[0] + "%;");
		g.setColor(Color.white);
		if (Main.arg.allArgs.containsKey("radius")) {
			int r = Integer.parseInt(Main.arg.allArgs.get("radius"));
			g.drawOval(p.bounds[0][0]*panelSize[0]+(100-r)*panelSize[0]/2/100, p.bounds[0][1]*panelSize[1]+(100-r)*panelSize[1]/2/100, r*panelSize[0]/100, r*panelSize[1]/100);
		} 
		//draw a white rectangle around the image
		g.drawRect(p.bounds[0][0]*panelSize[0], p.bounds[0][1]*panelSize[1], panelSize[0]-1, panelSize[1]-1);
	}
}
