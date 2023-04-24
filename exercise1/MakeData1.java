// Evangelos Tzortzis AM:3088

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MakeData1 {

	public static void main(String[] args) throws IOException {
				
		try {
			File outS1 = new File("s1data.csv");
			if (outS1.createNewFile()) {
				System.out.println("File created: " + outS1.getName());
			} else {
				//System.out.println("File already exists.");
			}
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
		
		BufferedWriter fw = new BufferedWriter( new FileWriter("s1data.csv"));
		
		fw.write("x1,x2,category\n");
		
		Random rand = new Random();
		
		ArrayList<String> categories = new ArrayList<>(Arrays.asList("C1","C2","C3","C4"));
		ArrayList<String> tempCat = new ArrayList<>();
		
		ArrayList<Data> s1 = new ArrayList<>();
		
		for(int i=0; i < 8000; i++) {
			
			s1.add(new Data());
			
			s1.get(i).x1 = 1 - 2 * rand.nextDouble();
			s1.get(i).x2 = 1 - 2 * rand.nextDouble();
			
			
			if ((Math.pow(s1.get(i).x1 - 0.5,2) + Math.pow(s1.get(i).x2 - 0.5,2)) < 0.16) {
				s1.get(i).category = "C1";
			}
			else if ((Math.pow(s1.get(i).x1 + 0.5,2) + Math.pow(s1.get(i).x2 + 0.5,2)) < 0.16) {
				s1.get(i).category = "C1";			
			}
			else if ((Math.pow(s1.get(i).x1 - 0.5,2) + Math.pow(s1.get(i).x2 + 0.5,2)) < 0.16) {
				s1.get(i).category = "C2";
			}
			else if ((Math.pow(s1.get(i).x1 + 0.5,2) + Math.pow(s1.get(i).x2 - 0.5,2)) < 0.16) {
				s1.get(i).category = "C2";
			}
			else {
				if ((s1.get(i).x1 > 0 && s1.get(i).x2 > 0)||(s1.get(i).x1 < 0 && s1.get(i).x2 < 0)) {
					s1.get(i).category = "C3";
				}
				else if((s1.get(i).x1 > 0 && s1.get(i).x2 < 0)||(s1.get(i).x1 < 0 && s1.get(i).x2 > 0)) {
					s1.get(i).category = "C4";
				}
			}
			
			
			// noise with 0.1 probability for the first 4000 of the examples
			if (i < 4000) {
				if (rand.nextDouble() <= 0.1) {
					double prob = rand.nextDouble();
					
					tempCat.clear();
					tempCat.addAll(categories);
					tempCat.remove(s1.get(i).category);
					
					if(prob<0.33) {
						s1.get(i).category = tempCat.get(0);
					}
					else if (prob<0.66) {
						s1.get(i).category = tempCat.get(1);
					}
					else {
						s1.get(i).category = tempCat.get(2);
					}
				} 
			}
			
			//System.out.println("x1: "+s1.get(i).x1+" x2: "+s1.get(i).x1+" category: "+s1.get(i).category);
			fw.write(s1.get(i).x1+","+s1.get(i).x2+","+s1.get(i).category+"\n");
		}	
		fw.close();
	}

}

class Data{
	double x1;
	double x2;
	String category;
}