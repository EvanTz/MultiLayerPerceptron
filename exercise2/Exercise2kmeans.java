// Evangelos Tzortzis AM:3088

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;


public class Exercise2kmeans {
	ArrayList<ArrayList<Double>> centroids;
	
	public static void main(String[] args) {
		
		Kmeans algok9 = new Kmeans(9); //  run for k = 3,5,7,9,11,13
		//algok9.runKmeans();
		algok9.run20times();
	}

	
	
	
	
}

class Kmeans{
	ArrayList<ArrayList<Double>> centroids;
	ArrayList<ArrayList<Integer>> assignedClusterIndeces;
	int k;
	LoadData2 data;
	double error;
	
	public Kmeans(int k) {
		this.k = k;
		data = new LoadData2("s2data.csv");
		data.openFile();
	}
	
	
	void initializeCentroids() {
		Random rand = new Random();
		double x1;
		double x2;
		centroids = new ArrayList<>();
		assignedClusterIndeces = new ArrayList<>();
		
		for (int i = 0; i < k; i++) {
			centroids.add(new ArrayList<>());
			x1 = data.samples.get(rand.nextInt(1200)).get(0);
			x2 = data.samples.get(rand.nextInt(1200)).get(1);
			
			centroids.get(i).add(x1);
			centroids.get(i).add(x2);
			
			assignedClusterIndeces.add(new ArrayList<>());
		}
		//System.out.println(centroids);
		//System.out.println(assignedClusterIndeces);
	}
	
	void runKmeans() {
		initializeCentroids();
		ArrayList<ArrayList<Double>> oldCentroids;
		error = 0;
		// while centroids keep changing position
		do {
			error = 0;
			oldCentroids = new ArrayList<>();
			for (int i = 0; i < k; i++) {
				oldCentroids.add(new ArrayList<>());
				for (int j = 0; j < 2; j++) {
					oldCentroids.get(i).add(centroids.get(i).get(j));
				}
			}
			//System.out.println("oldCentroids "+oldCentroids);
			
			
			// assign the samples to the closest centroid
			for (int i = 0; i < data.samples.size(); i++) {

				double dist = 0;
				double minDist = 10000000;
				int minDistClusterIndex = -1;

				for (int j = 0; j < k; j++) {
					dist = distance(data.samples.get(i), centroids.get(j));
					if (dist < minDist) {
						minDist = dist;
						minDistClusterIndex = j;
					}
				}
				assignedClusterIndeces.get(minDistClusterIndex).add(i); // i sample belongs in minDistClusterIndex cluster
				//System.out.println(assignedClusterIndeces);
				
				error += minDist;
			}
			// update centroids
			for (int i = 0; i < k; i++) {
				float x1;
				float x2;
				float tempSum1 = 0;
				float tempSum2 = 0;
				for (int j = 0; j < assignedClusterIndeces.get(i).size(); j++) {
					tempSum1 += data.samples.get(assignedClusterIndeces.get(i).get(j)).get(0);
					tempSum2 += data.samples.get(assignedClusterIndeces.get(i).get(j)).get(1);
				}
				x1 = Math.round(10000 * (tempSum1 * (float) 1 / (assignedClusterIndeces.get(i).size())));
				x2 = Math.round(10000 * (tempSum2 * (float) 1 / (assignedClusterIndeces.get(i).size())));

				centroids.get(i).set(0,(double) x1/(double)10000);
				centroids.get(i).set(1,(double) x2/(double)10000);

			}
			//System.out.println("centroids    "+centroids);
		} while (!centroids.equals(oldCentroids));
		
		System.out.println("Final centroids: "+centroids);
		System.out.println("Total clustering error: "+error +" for k="+k);
		
	}
	
	void run20times() {
		ArrayList<ArrayList<Double>> minCentroids = new ArrayList<>();
		int minIndex = -1;
		double minError = 100000000;
		for (int i = 0; i < 20; i++) {
			System.out.println("Run: "+i);
			initializeCentroids();
			runKmeans();
			if (error< minError) {
				minError = error;
				minIndex = i;
				minCentroids = centroids;
			}
		}
		System.out.println("\nMin error: "+minError+", at min run: "+minIndex);
		System.out.println("Best centroids: "+minCentroids);
	}
	
	// euclidean distance
 	double distance(ArrayList<Double> sample1, ArrayList<Double> sample2) {
		double dist;
		double sum = 0;
		for (int i = 0; i < sample1.size(); i++) {
			sum += Math.pow((sample1.get(i) - sample2.get(i)), 2);
		}
		dist = Math.pow(sum, 0.5);
		
		return dist;
	}
}

class LoadData2{
	String file;
	ArrayList<String> lines = new ArrayList<>();
	ArrayList<ArrayList<Double>> samples = new ArrayList<>();
	ArrayList<String> lineContents;
	
	
	public LoadData2(String fileName) {
		this.file = fileName;
	}
	
	void openFile() {
		lines.clear();
		samples.clear();
		
		try {
		      File myObj = new File(file);
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				//System.out.println(data);
				lines.add(data);
		        
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		lines.remove(0);
		
		for(int i=0;i<lines.size();i++) {
			lineContents = new ArrayList<>(Arrays.asList(lines.get(i).split("\\s*,\\s*")));
			samples.add(new ArrayList<>(Arrays.asList(Double.parseDouble(lineContents.get(0)),Double.parseDouble(lineContents.get(1)))));
			
		}
		for(int i=0;i<lines.size();i++) {
			//System.out.println(samples.get(i));
		}
		//System.out.println(samples.size());


	}
}
