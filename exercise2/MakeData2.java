// Evangelos Tzortzis AM:3088

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MakeData2 {

	public static void main(String[] args) throws IOException {
				
		try {
			File outS1 = new File("s2data.csv");
			if (outS1.createNewFile()) {
				System.out.println("File created: " + outS1.getName());
			} else {
				//System.out.println("File already exists.");
			}
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
		
		BufferedWriter fw = new BufferedWriter( new FileWriter("s2data.csv"));
		
		fw.write("x1,x2\n");
		
		Random rand = new Random();
		
		ArrayList<ArrayList<Double>> s2 = new ArrayList<>();
		
		
		// random in range formula: rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
		for(int i = 0; i<150*6; i+=6) {
			
			s2.add(new ArrayList<>());
			s2.get(i).add(0,0.75 + 0.5 * rand.nextDouble()); // x1:[0.75,1.25] max - min = 0.5
			s2.get(i).add(1,0.75 + 0.5 * rand.nextDouble()); // x2:[0.75,1.25] max - min = 0.5
			
			s2.add(new ArrayList<>());
			s2.get(i+1).add(0,0 + 0.5 * rand.nextDouble()); // x1:[0, 0.5]  max - min = 0.5
			s2.get(i+1).add(1,0 + 0.5 * rand.nextDouble()); // x2:[0, 0.5]  max - min = 0.5
			
			s2.add(new ArrayList<>());
			s2.get(i+2).add(0,0   + 0.5 * rand.nextDouble()); // x1:[0, 0.5]  max - min = 0.5
			s2.get(i+2).add(1,1.5 + 0.5 * rand.nextDouble()); // x2:[1.5, 2]  max - min = 0.5

			s2.add(new ArrayList<>());
			s2.get(i+3).add(0,1.5 + 0.5 * rand.nextDouble()); // x1:[1.5, 2]  max - min = 0.5
			s2.get(i+3).add(1,0   + 0.5 * rand.nextDouble()); // x2:[0, 0.5]  max - min = 0.5
			
			s2.add(new ArrayList<>());
			s2.get(i+4).add(0,1.5 + 0.5 * rand.nextDouble()); // x1:[1.5, 2]  max - min = 0.5
			s2.get(i+4).add(1,1.5 + 0.5 * rand.nextDouble()); // x2:[1.5, 2]  max - min = 0.5
			
			s2.add(new ArrayList<>());
			s2.get(i+5).add(0,0 + 2 * rand.nextDouble()); // x1:[0, 2]  max - min = 2
			s2.get(i+5).add(1,0 + 2 * rand.nextDouble()); // x2:[0, 2]  max - min = 2
			
			System.out.println("x1: "+s2.get(i).get(0)+" x2: "+s2.get(i).get(1));
			System.out.println("x1: "+s2.get(i+1).get(0)+" x2: "+s2.get(i+1).get(0));
			System.out.println("x1: "+s2.get(i+2).get(0)+" x2: "+s2.get(i+2).get(0));
			System.out.println("x1: "+s2.get(i+3).get(0)+" x2: "+s2.get(i+3).get(0));
			System.out.println("x1: "+s2.get(i+4).get(0)+" x2: "+s2.get(i+4).get(0));
			System.out.println("x1: "+s2.get(i+5).get(0)+" x2: "+s2.get(i+5).get(0));

		}
		
		for(int i = 900; i<(900+75*4); i+=4) {
			
			s2.add(new ArrayList<>());
			s2.get(i).add(0,0.6 + 0.2 * rand.nextDouble()); // x1:[0.6,0.8] max - min = 0.2
			s2.get(i).add(1,0   + 0.4 * rand.nextDouble()); // x2:[0,  0.4] max - min = 0.4
			
			s2.add(new ArrayList<>());
			s2.get(i+1).add(0,0.6 + 0.2 * rand.nextDouble()); // x1:[0.6,0.8]  max - min = 0.2
			s2.get(i+1).add(1,1.6 + 0.4 * rand.nextDouble()); // x2:[1.6,  2]  max - min = 0.4
			
			s2.add(new ArrayList<>());
			s2.get(i+2).add(0,1.2 + 0.2 * rand.nextDouble()); // x1:[1.2,1.4]  max - min = 0.2
			s2.get(i+2).add(1,0   + 0.4 * rand.nextDouble()); // x2:[0,  0.4]  max - min = 0.4

			s2.add(new ArrayList<>());
			s2.get(i+3).add(0,1.2 + 0.2 * rand.nextDouble()); // x1:[1.2,1.4]  max - min = 0.2
			s2.get(i+3).add(1,1.6 + 0.4 * rand.nextDouble()); // x2:[1.6,  2]  max - min = 0.4
			
			System.out.println("x1: "+s2.get(i).get(0)+" x2: "+s2.get(i).get(1));
			System.out.println("x1: "+s2.get(i+1).get(0)+" x2: "+s2.get(i+1).get(0));
			System.out.println("x1: "+s2.get(i+2).get(0)+" x2: "+s2.get(i+2).get(0));
			System.out.println("x1: "+s2.get(i+3).get(0)+" x2: "+s2.get(i+3).get(0));

		}
		for(ArrayList<Double> i :s2) {
			fw.write(i.get(0)+","+i.get(1)+"\n");
		}
		fw.close();
	}

}
