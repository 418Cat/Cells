package mainPkg;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Arguments {
	public String[] args;
	public Map<String, String> allArgs = new HashMap<>();
	
	public void setArgs(String[] tmpArgs) {
		args = tmpArgs;
		for (String string : tmpArgs) {
			if(string.toLowerCase().startsWith("--help")) {
				help();
			}
			allArgs.put(string.split("=")[0].substring(2), string.split("=")[1]);
		}
		verifyArgs();
	}
	
	private void verifyArgs() {
		if(!allArgs.containsKey("size")) {
			allArgs.put("size", "1080,1080");
		}
		if(!allArgs.containsKey("threshold")) {
			allArgs.put("threshold", "50,50,50");
		}
		allArgs.put("inputPath", getInput().getAbsolutePath());
		convert();
	};
	
	private void convert() {
		if(getInput().getAbsolutePath().endsWith(".czi")) {
			new FileConvert(getInput().getAbsolutePath(), getOutput().getAbsolutePath()).convert();
			System.out.println("conversion successful");
		}
	}
	
	public File getInput() {
		File res = null;
		if((allArgs.containsKey("input") && allArgs.containsKey("filePath")) || (allArgs.containsKey("folder") && allArgs.containsKey("filePath"))){
			System.err.println("Enter either a filePath or a folder & input");
			System.exit(0);
		}
		if(allArgs.containsKey("input") && allArgs.containsKey("folder")) {
			res = new File(allArgs.get("folder") + "/" + allArgs.get("input"));
		} else if((allArgs.containsKey("input") && !allArgs.containsKey("folder")) || (!allArgs.containsKey("input") && allArgs.containsKey("folder"))) {
			System.err.println("Enter a folder and an input file");
			System.exit(0);
		} else if(allArgs.containsKey("filePath")) {
			res = new File(allArgs.get("filePath"));
		} else {
			System.err.println("No file was given, cannot continue");
			System.exit(0);
		}
		if(!res.exists()) {
			System.err.println("The file " + res.getAbsolutePath() + " wasn't found");
			System.exit(0);
		}
		return(res);
	}
	
	public File getOutput() {
		File res = null;
		if((allArgs.containsKey("input") && allArgs.get("input").endsWith(".tiff")) || (allArgs.containsKey("filePath") && allArgs.get("filePath").endsWith(".tiff"))) {
			return(getInput());
		} 
		else if(allArgs.containsKey("output")) {
			res = new File(getInput().getParent() + "/" + allArgs.get("output"));
		}
		else if(allArgs.containsKey("outputPath")) {
			res = new File(allArgs.get("outputPath"));
			if(!new File(new File(allArgs.get("outputPath")).getParent()).exists()) {
				if(!new File(new File(allArgs.get("outputPath")).getParent()).mkdirs()) {
					System.err.println("Could not create the necessary directories for the output, create them manually or move the output folder");
				}
			}
		}
		else if(allArgs.containsKey("input")) {
			res = new File(allArgs.get("folder") + "/" + allArgs.get("input").substring(0, allArgs.get("input").length()-3) + "tiff");
		}
		else if(allArgs.containsKey("filePath")) {
			res = new File(allArgs.get("filePath").substring(0, allArgs.get("filePath").length()-3) + "tiff");
		}
		else {
			System.err.println("No input file was given, cannot continue");
			System.exit(0);
		}
		
		return(res);
	}
	
	private void help() {
		System.out.println("\nThis program was created by me, Benjamin Badina. The code is accessible on my github page under the name \"Cells\": github.com/418cat");
		System.out.println("You can modify and share this code freely, just don't forget the credits\n");
		System.out.println("\n/!\\ The options are case sensitive /!\\");
		System.out.println("---- INPUTS ----");
		System.out.println("The program can accept two types of files : .czi and .tiff files. In the case of a .czi file, the program will convert it to a .tiff file\n");
		System.out.println("--filePath   the whole absolute path to the input file\n");
		System.out.println("--folder     absolute path to the folder containing the file. Use it with --input\n");
		System.out.println("--input      input file name, can either be in .czi format or in .tiff format. Use it with --folder\n");
		System.out.println("\n---- OUTPUTS ----");
		System.out.println("In case the input file is in a .czi format, it will be converted to a .tiff format. These options allow you to choose the name of the file and its folder. If none is specified, it will take the name of the input file\n");
		System.out.println("--output     output file name. Do not use it specify the folder, only the name\n");
		System.out.println("--outputPath absolute path to the output file, you can change the folder and name\n");
		System.out.println("\n---- WINDOW ----");
		System.out.println("These options allow you to change settings of the window\n");
		System.out.println("--panel      specify a panel, it will be the only one shown. If the windows is too big, some black lines might appear.By default, all panels are shown. Not mandatory\n");
		System.out.println("--size       the desired size for the windows in width and height (in pixel). By default : --size=1080,1080\n");
		System.out.println("\n---- PROCESSING ----");
		System.out.println("To avoid bad results, use these options to tweak the image processing\n");
		System.out.println("--threshold  threshold values for the blue, red and green images (from 0 to 255, in this specific order). By default : --threshold=50,50,50\n");
		System.out.println("--radius     the radius (in percent) of the circle filter around the center of each panel. By default, there is no circle filter applied\n");
		System.out.println("\n---- OTHER ----");
		System.out.println();
		System.out.println("--help       list of all commands with a small description for each one\n");
		System.out.println("Examples :\nCellules.jar --folder=/home/benjamin/CellFolder --input=myFile.czi --output=myTiffFile.tiff --threshold=200,100,200 --radius=90\nCellules.jar --filePath=/home/benjamin/Desktop/aTiffFile.tiff --threshold=160,90,130 --panel=5");
		System.exit(0);
	}
	
}
