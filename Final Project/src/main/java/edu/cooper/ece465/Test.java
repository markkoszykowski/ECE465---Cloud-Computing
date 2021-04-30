package edu.cooper.ece465;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.cooper.ece465.Complex;
import edu.cooper.ece465.IterativeFFT;
import edu.cooper.ece465.Image;

public class Test {

	public static void main(String args[]) throws IOException {
		System.out.println("Compressing...");
		Image image = new Image();
		
		final long startTime = System.currentTimeMillis();
		
		String inputPath = "sample4.jpg";
		String outputPath = "test.jpg";
		
		image.readImage(inputPath);
		System.out.println("Dimensions: " + image.width + "x" + image.height);
		image.writeImage("test2.jpg");
		image.rgbCompress((float) 0.01);

		
		image.writeImage(outputPath);
		
		final long endTime = System.currentTimeMillis();
		
        Path filePath = Paths.get(inputPath);
		
		FileChannel fileChannel = FileChannel.open(filePath);
        long inSize = fileChannel.size();
        fileChannel.close();
        
        
        filePath = Paths.get(outputPath);
        fileChannel = FileChannel.open(filePath);
        long outSize = fileChannel.size();
        fileChannel.close();
		
        System.out.println("File size reduction: " + (float) (inSize-outSize)/inSize);
		System.out.println("Total execution time: " + (float) (endTime - startTime)/1000 + " seconds");		
		
	}
}
