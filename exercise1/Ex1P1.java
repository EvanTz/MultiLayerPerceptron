// Evangelos Tzortzis AM:3088

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Ex1P1 {

	public static void main(String[] args) {

		P1 nn1 = new P1(2,4,9,9,"tanh","tanh","logistic"); 
		nn1.loadData("s1data.csv");
		nn1.learningRate = 0.00005;
		nn1.threshold = 0.001; // difference of error between two consecutive epochs 
		
		nn1.train(10); // batch has to be a divider of the total amount of samples (4000) : 4000 serial, 1 group
		nn1.test();
		
		}

}


class P1 {
	int d;
	int k;
	int firstHiddenLayerNeurons;
	int secondHiddenLayerNeurons;
	String hiddenLayer1ActivationFunction;
	String hiddenLayer2ActivationFunction;
	String outputLayerActivationFunction;
	LoadData data;
	
	double learningRate;
	double threshold;
	
	ArrayList<ArrayList<Double>> weightsHL1;
	ArrayList<ArrayList<Double>> weightsHL2;
	ArrayList<ArrayList<Double>> weightsOutL;
	
	ArrayList<Double> inHL1 = new ArrayList<>();
	ArrayList<Double> outHL1 = new ArrayList<>();
	
	ArrayList<Double> inHL2 = new ArrayList<>();
	ArrayList<Double> outHL2 = new ArrayList<>();
	
	ArrayList<Double> inFL3 = new ArrayList<>();
	ArrayList<Double> outFL3 = new ArrayList<>();
	
	ArrayList<ArrayList<Double>> weights_der_hl1 = new ArrayList<>();
	ArrayList<ArrayList<Double>> weights_der_hl2 = new ArrayList<>();
	ArrayList<ArrayList<Double>> weights_der_fl = new ArrayList<>();
	
	int minEpochs = 700;
	double totalError =0;
	
	ArrayList<Double> errorList;
	
	
	ArrayList<DataType1> testSamplePredicted = new ArrayList<>();
	
	P1(int d, int k, int firstHiddenLayerNeurons, int secondHiddenLayerNeurons, String activationFunctionHL1,String activationFunctionHL2,String activationFunctionOutLayer){
		this.d = d;
		this.k = k;
		this.firstHiddenLayerNeurons = firstHiddenLayerNeurons;
		this.secondHiddenLayerNeurons = secondHiddenLayerNeurons;
		this.hiddenLayer1ActivationFunction = activationFunctionHL1;
		this.hiddenLayer2ActivationFunction = activationFunctionHL2;
		this.outputLayerActivationFunction =  activationFunctionOutLayer;

		
		build_MLP_Architecture();
		
	}
	
	void loadData(String file) {
		data = new LoadData(file);
		data.openFile();
		//data.samples.get(0).pr();
		//System.out.println(data.trainSet.get(23).encodedCats);
	}
	
	void build_MLP_Architecture() {
		totalError = 0;
		
		//weight lists for both hidden layers and output layer and random initialization 
		Random rando = new Random(System.currentTimeMillis());
		
		weightsHL1 = new ArrayList<>();
		
		for(int i=0;i<firstHiddenLayerNeurons;i++) {
			weightsHL1.add(new ArrayList<>());
			for (int j = 0; j < 3; j++) { // 3 size because of 2 sample inputs x1,x2 and bias b0
				weightsHL1.get(i).add(1.0 - rando.nextDouble() * 2);  // random scaled to (-1,1)
			}
		}

		
		weightsHL2 = new ArrayList<>();

		for(int i=0;i<secondHiddenLayerNeurons;i++) {
			weightsHL2.add(new ArrayList<>());
			for (int j = 0; j < (firstHiddenLayerNeurons+1); j++) { // input is the size of the previous neurons and bias b1
				weightsHL2.get(i).add(1.0 - rando.nextDouble() * 2);  // random scaled to (-1,1)
			}
		}
		
		
		weightsOutL = new ArrayList<>();
		
		for(int i=0;i<4;i++) {
			weightsOutL.add(new ArrayList<>());
			for (int j = 0; j < (secondHiddenLayerNeurons+1); j++) { // input is the size of the previous neurons and bias b2
				weightsOutL.get(i).add(1.0 - rando.nextDouble() * 2);  // random scaled to (-1,1)
			}
		}
		
		//System.out.println(" weightsHL1: "+weightsHL1);
		//System.out.println(" weightsHL2: "+weightsHL2);
		//System.out.println("weightsOutL: "+weightsOutL);
		
	}
	
	void forwardPass(DataType1 sample) {
		inHL1.clear();
		inHL2.clear();
		inFL3.clear();
		outHL1.clear();
		outHL2.clear();
		outFL3.clear();
		
		double tempOut;
		
		
		
		//for each layer, for each neuron calculate the neuron sum of the inputs * weights  + bias_weight, and after that the output of the activation function of the layer
		inHL1.addAll(Arrays.asList(sample.x1,sample.x2));
		for (int i = 0; i < firstHiddenLayerNeurons; i++) {
			tempOut = 0.0;
			for (int j = 0; j < inHL1.size(); j++) {
				tempOut += inHL1.get(j)*weightsHL1.get(i).get(j);
			}
			tempOut += weightsHL1.get(i).get(weightsHL1.get(i).size()-1); // add bias to the sum 
			
			// calculate the activation function of the tempOut and add it to the outHL1 list
			if (hiddenLayer1ActivationFunction.equals("tanh")) {
				outHL1.add(Math.tanh(tempOut));
			}
			else if(hiddenLayer1ActivationFunction.equals("relu")){
				if (tempOut >= 0.0)
					outHL1.add(tempOut);
				else
				    outHL1.add(0.0);
			}
		}

		inHL2.addAll(outHL1);
		for (int i = 0; i < secondHiddenLayerNeurons; i++) {
			tempOut = 0.0;
			for (int j = 0; j < inHL2.size(); j++) {
				tempOut += inHL2.get(j)*weightsHL2.get(i).get(j);
			}
			tempOut += weightsHL2.get(i).get(weightsHL2.get(i).size()-1); // add bias to the sum 
			
			// calculate the activation function of the tempOut and add it to the outHL2 list
			if (hiddenLayer2ActivationFunction.equals("tanh")) {
				outHL2.add(Math.tanh(tempOut));
			}
			else if(hiddenLayer2ActivationFunction.equals("relu")){
				if (tempOut >= 0.0)
					outHL2.add(tempOut);
				else
				    outHL2.add(0.0);
			}
		}
		
		inFL3.addAll(outHL2);
		for (int i = 0; i < 4; i++) {
			tempOut = 0.0;
			for (int j = 0; j < inFL3.size(); j++) {
				tempOut += inFL3.get(j)*weightsOutL.get(i).get(j);
			}
			tempOut += weightsOutL.get(i).get(weightsOutL.get(i).size()-1); // add bias to the sum 
			//System.out.println("Math.tanh(tempOut): "+Math.tanh(tempOut));
			// calculate the activation function of the tempOut and add it to the outFL3 list
			if (outputLayerActivationFunction.equals("tanh")) {
				outFL3.add(Math.tanh(tempOut));
			}
			else if(outputLayerActivationFunction.equals("relu")){
				if (tempOut >= 0.0)
					outFL3.add(tempOut);
				else
					outFL3.add(0.0);
			}
			else if(outputLayerActivationFunction.equals("linear")){
				outFL3.add(tempOut);
			}
			else if(outputLayerActivationFunction.equals("logistic")){
				outFL3.add(1.0 / (1.0 + Math.exp(-tempOut)));
			}
		}
		
		//System.out.println("outHL1: "+outHL1);
		//System.out.println("outHL2: "+outHL2);
		//System.out.println("outFL3: "+outFL3);
		
		// calculate the sample error
		//double error = calcError(sample);
		// add the error to the total error for the epoch
		//totalError += error;
		
		//System.out.println("forward pass totalError: "+totalError);
	}
	
	double calcError(DataType1 sample) {
		double error = 0;
		for (int i = 0; i < 4; i++) {
			error += Math.pow(outFL3.get(i)-sample.encodedCats.get(i),2.0);
		}
		error = error * 0.5;
		return error;
	}
	
	void addTotalError(double error) {
		totalError += error;
	}

	void backprop(DataType1 sample) {
		// backpropagation
		
		
		// calculate the output error o-t
		ArrayList<Double> outputErrors = new ArrayList<>();
		
		ArrayList<Double> deltaOut = new ArrayList<>();
		ArrayList<Double> deltaHL2 = new ArrayList<>();
		ArrayList<Double> deltaHL1 = new ArrayList<>();
		
		
		// calculate the d for all neurons in outputlayer , hl2, hl1
		// calculate the partial derivatives of the weights for after calculating a layer
		
		// final layer
		
		
		for (int i = 0; i < 4; i++) {
			outputErrors.add(outFL3.get(i)-sample.encodedCats.get(i));
			if (outputLayerActivationFunction.equals("tanh")) {
				deltaOut.add((1-Math.pow(Math.tanh(outFL3.get(i)), 2))*outputErrors.get(i));
			}
			else if (outputLayerActivationFunction.equals("relu")) {
				if(outFL3.get(i) > 0) {
					deltaOut.add(outputErrors.get(i));
				}
				else {
					deltaOut.add(0.0);
				}
			}
			else if (outputLayerActivationFunction.equals("linear")) {
				deltaOut.add(outputErrors.get(i));
			}
			else if (outputLayerActivationFunction.equals("logistic")) {
				deltaOut.add(outFL3.get(i)*(1-outFL3.get(i))*outputErrors.get(i));
			}
		}

		//System.out.println("deltaOut: "+deltaOut);
		
		// weights derivatives for output layer
		weights_der_fl.clear();
		for (int i = 0; i < 4; i++) {
			weights_der_fl.add(new ArrayList<>());
			for(int j = 0; j < secondHiddenLayerNeurons ; j++) {
				weights_der_fl.get(i).add(deltaOut.get(i)*inFL3.get(j));
			}
			weights_der_fl.get(i).add(deltaOut.get(i)); //bias
		}
		
		//System.out.println("weights_der_fl: "+weights_der_fl+"\n");
		
		
		// second hidden layer
		
		
		for (int i = 0; i < secondHiddenLayerNeurons; i++) {
			double sum=0;
			if (hiddenLayer2ActivationFunction.equals("tanh")) {
				for (int j = 0; j < 4; j++) {
					sum += weightsOutL.get(j).get(i)*deltaOut.get(j);
				}
				deltaHL2.add((1-Math.pow(Math.tanh(outHL2.get(i)), 2))*sum);
			}
			else if (hiddenLayer2ActivationFunction.equals("relu")) {
				for (int j = 0; j < 4; j++) {
					sum += weightsOutL.get(j).get(i)*deltaOut.get(j);
				}
				deltaHL2.add((double) sum);
			}
		}
		
		//System.out.println("deltaHL2: "+deltaHL2);

		// weights derivatives for second hidden layer
		weights_der_hl2.clear();
		for (int i = 0; i < secondHiddenLayerNeurons; i++) {
			weights_der_hl2.add(new ArrayList<>());
			for(int j = 0; j < inHL2.size(); j++) { 
				weights_der_hl2.get(i).add(deltaHL2.get(i)*inHL2.get(j));
			}
			weights_der_hl2.get(i).add(deltaHL2.get(i)); //bias
		}
		
		//System.out.println("weights_der_hl2: "+weights_der_hl2+"\n");
		
		
		// first hidden layer
		

		for (int i = 0; i < firstHiddenLayerNeurons; i++) {
			double sum=0.0;
			if (hiddenLayer1ActivationFunction.equals("tanh")) {
				for (int j = 0; j < secondHiddenLayerNeurons; j++) {
					//System.out.println(deltaHL2.get(i)); 
					sum += weightsHL2.get(j).get(i)*deltaHL2.get(j);
				}
				deltaHL1.add((1-Math.pow(Math.tanh(outHL1.get(i)), 2))*sum);
			}
			else if (hiddenLayer1ActivationFunction.equals("relu")) {
				for (int j = 0; j < secondHiddenLayerNeurons; j++) {
					sum += weightsHL2.get(j).get(i)*deltaHL2.get(j);
				}
				deltaHL1.add((double) sum);
			}
		}
		
		//System.out.println("deltaHL1: "+deltaHL1);
		
		// weights derivatives for first hidden layer
		weights_der_hl1.clear();
		for (int i = 0; i < firstHiddenLayerNeurons; i++) {
			weights_der_hl1.add(new ArrayList<>());
			for(int j = 0; j < inHL1.size(); j++) {
				weights_der_hl1.get(i).add(deltaHL1.get(i)*inHL1.get(j));
			}
			weights_der_hl1.get(i).add(deltaHL1.get(i)); //bias
		}
		
		//System.out.println("weights_der_hl1: "+weights_der_hl1+"\n");
		
		//System.out.println("deltaOut: "+deltaOut);
		//System.out.println("deltaHL2: "+deltaHL2);
		//System.out.println("deltaHL1: "+deltaHL1);
		
		//System.out.println("weights_der_fl: "+weights_der_fl);
		//System.out.println("weights_der_hl2: "+weights_der_hl2);
		//System.out.println("weights_der_hl1: "+weights_der_hl1);

	}
	
	void train(int batches) {
		double startTime = System.currentTimeMillis();
		
		int epoch = 0;
		double previousError = 0;
		double errorDif = -11111111;
		errorList = new ArrayList<>();

		ArrayList<ArrayList<Double>> temp_weights_der_hl1 = new ArrayList<>();
		ArrayList<ArrayList<Double>> temp_weights_der_hl2 = new ArrayList<>();
		ArrayList<ArrayList<Double>> temp_weights_der_fl = new ArrayList<>();
		
		//System.out.println("weightsHL1 before: "+ weightsHL1+"\n");
		do {
			totalError = 0;

			// initialize all weights to 0 
			
			temp_weights_der_fl.clear();
			temp_weights_der_hl2.clear();
			temp_weights_der_hl1.clear();

			
			for (int i = 0; i < weightsHL1.size(); i++) {
				temp_weights_der_hl1.add(new ArrayList<>());
				for (int j = 0; j < weightsHL1.get(i).size(); j++) {
					temp_weights_der_hl1.get(i).add(j, 0.0);
				}
			}

			for (int i = 0; i < weightsHL2.size(); i++) {
				temp_weights_der_hl2.add(new ArrayList<>());
				for (int j = 0; j < weightsHL2.get(i).size(); j++) {
					temp_weights_der_hl2.get(i).add(j, 0.0);
				}
			}
			
			for (int i = 0; i < weightsOutL.size(); i++) {
				temp_weights_der_fl.add(new ArrayList<>());
				for (int j = 0; j < weightsOutL.get(i).size(); j++) {
					temp_weights_der_fl.get(i).add(j, 0.0);
				}
			}
			
			//System.out.println("temp_weights_der_hl1: "+temp_weights_der_hl1.get(0).size());
			//System.out.println("temp_weights_der_hl2: "+temp_weights_der_hl2.get(0).size());
			//System.out.println("temp_weights_der_fl: "+temp_weights_der_fl.get(0).size());
			
			
			int iter = 0;
			int counter = 0;
			double tempSum;
			
			for(int i=0; i < 4000 ; i +=4000/batches) {
				if(iter + 4000/batches > 4000) {
					iter += 4000 - iter;
				}
				else iter += 4000/batches;
				
				// calc each batch in here
				for(int j=i; j < iter; j++) {
					counter ++;
					
					forwardPass(data.trainSet.get(j));
					
					backprop(data.trainSet.get(j));
					
					// calculate the weights sum
					for (int z = 0; z < temp_weights_der_hl1.size(); z++) {
						for (int w = 0; w < temp_weights_der_hl1.get(z).size(); w++) {
							tempSum = temp_weights_der_hl1.get(z).get(w) + weights_der_hl1.get(z).get(w);
							temp_weights_der_hl1.get(z).set(w, tempSum);
						}
					}
					for (int z = 0; z < temp_weights_der_hl2.size(); z++) {
						for (int w = 0; w < temp_weights_der_hl2.get(z).size(); w++) {
							tempSum = temp_weights_der_hl2.get(z).get(w) + weights_der_hl2.get(z).get(w);
							temp_weights_der_hl2.get(z).set(w, tempSum);
						}
					}
					for (int z = 0; z < temp_weights_der_fl.size(); z++) {
						for (int w = 0; w < temp_weights_der_fl.get(z).size(); w++) {
							tempSum = temp_weights_der_fl.get(z).get(w) + weights_der_fl.get(z).get(w);
							temp_weights_der_fl.get(z).set(w, tempSum);
						}
					}
				}
				
				// update the weights
				for (int z = 0; z < weights_der_hl1.size(); z++) {
					for (int w = 0; w < weights_der_hl1.get(z).size(); w++) {
						tempSum = weightsHL1.get(z).get(w) - learningRate * temp_weights_der_hl1.get(z).get(w);
						weightsHL1.get(z).set(w, tempSum);
					}
				}
				for (int z = 0; z < weights_der_hl2.size(); z++) {
					for (int w = 0; w < weights_der_hl2.get(z).size(); w++) {
						tempSum = weightsHL2.get(z).get(w) - learningRate * temp_weights_der_hl2.get(z).get(w);
						weightsHL2.get(z).set(w, tempSum);
					}
				}
				for (int z = 0; z < weights_der_fl.size(); z++) {
					for (int w = 0; w < weights_der_fl.get(z).size(); w++) {
						tempSum = weightsOutL.get(z).get(w) - learningRate * temp_weights_der_fl.get(z).get(w);
						weightsOutL.get(z).set(w, tempSum);
					}
				}
				
				// add the batch error to the total epoch error
				for(int j=i; j < iter; j++) {
					forwardPass(data.trainSet.get(j));
					addTotalError(calcError(data.trainSet.get(j)));
				}
				
				//System.out.println("i: "+i+" iter: "+iter+" counter: "+counter);

			}

			errorDif = totalError - previousError;
			
			previousError = totalError;
			
			errorList.add(totalError);

			System.out.println("totalError: "+totalError+", error difference: "+errorDif+" epoch: "+epoch+"\n");
			epoch +=1;

		} while (epoch<700 || Math.abs(errorDif) > threshold);
		
		//System.out.println("weightsHL1 after: "+ weightsHL1);
		
		double endTime = (System.currentTimeMillis()-startTime)/1000.0;
		
		System.out.println("###### P1 results ######\n");
		
		System.out.printf("Time taken for training: %.3f seconds.",endTime);
		
		System.out.println("\nLearning Rate: "+ learningRate
				+"\nError threshold: "+threshold
				+"\n\nFirst hidden layer neurons: "+firstHiddenLayerNeurons+", First HL activation f: "+hiddenLayer1ActivationFunction
				+"\nSecond hidden layer neurons: "+secondHiddenLayerNeurons+", Second HL activation f: "+hiddenLayer2ActivationFunction
				+"\nOutput activation f: "+outputLayerActivationFunction);

		
		System.out.println("\nerrorList: "+errorList);
		
		//saveStatsTrain(endTime, errorList);  // save training stats to file
	}
	
	void test() {		
		int correct = 0;

		int argMax = -1;
		double max = -1.0;
		for (int i = 0; i < data.testSet.size(); i++) {
			argMax = -1;
			max = -1.0;
			
			forwardPass(data.testSet.get(i));
			
			for (int j = 0; j < outFL3.size(); j++) {
				if (outFL3.get(j) > max) {
					argMax = j;
					max = outFL3.get(j);
				}
			}
			
			addToPredicted(argMax, i);
			
			if (argMax == data.testSet.get(i).encodedCats.indexOf(1)) {
				correct +=1;
			}
		}
		System.out.println("\n### Testing ###");
		System.out.println("\nCorrect: "+ correct + ", correct percentage out of 4000 test samples: "+ (float)correct/(float) 4000+"\n");
		
		savePredicted();  // save predicted categories to file for plotting
		
		//saveStatsTest(correct);  // save testing stat to file
	}

	void addToPredicted(int argMax,int i) {
		testSamplePredicted.add(new DataType1());
		testSamplePredicted.get(testSamplePredicted.size()-1).x1 = data.testSet.get(i).x1;
		testSamplePredicted.get(testSamplePredicted.size()-1).x2 = data.testSet.get(i).x2;
		
		if (argMax == data.testSet.get(i).encodedCats.indexOf(1)) {
			testSamplePredicted.get(testSamplePredicted.size()-1).category = "+";
		}
		else {
			testSamplePredicted.get(testSamplePredicted.size()-1).category = "-";
		}
	}
	
	void savePredicted() {
		try {
			File outS1 = new File("P1predictions.csv");
			if (outS1.createNewFile()) {
				System.out.println("File created: " + outS1.getName());
			} else {
				//System.out.println("File already exists.");
			}
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
		
		BufferedWriter fw = null;
		try {
			fw = new BufferedWriter( new FileWriter("P1predictions.csv"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			fw.write("x1,x2,category\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < testSamplePredicted.size(); i++) {
			//System.out.println(testSamplePredicted.get(i).x1+","+testSamplePredicted.get(i).x2+","+testSamplePredicted.get(i).category);
			
			try {
				fw.write(testSamplePredicted.get(i).x1+","+testSamplePredicted.get(i).x2+","+testSamplePredicted.get(i).category+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void saveStatsTrain(double endTime,ArrayList<Double> errList) {
		String errorsString = errList.stream().map(Object::toString).collect(Collectors.joining(", "));
		
		try {
			File outS1 = new File("P1_statistics.txt");
			if (outS1.createNewFile()) {
				System.out.println("File created: " + outS1.getName());
			} else {
				//System.out.println("File already exists.");
			}
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
		
		BufferedWriter fw = null;
		try {
			fw = new BufferedWriter( new FileWriter("P1_statistics.txt",true));
			fw.write("\n###### P1 results ######\n");
			fw.write(String.format("\nTime taken for training: %.3f seconds.",endTime));
			fw.write("\n\nLearning Rate: "+ learningRate
				+"\nError threshold: "+threshold
				+"\n\nFirst hidden layer neurons: "+firstHiddenLayerNeurons+", First HL activation f: "+hiddenLayer1ActivationFunction
				+"\nSecond hidden layer neurons: "+secondHiddenLayerNeurons+", Second HL activation f: "+hiddenLayer2ActivationFunction
				+"\nOutput activation f: "+outputLayerActivationFunction+"\n");
			fw.write("\nerrorList:\n[");
			fw.write(errorsString);
			fw.write("]\n");
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	void saveStatsTest(int correct) {
		BufferedWriter fw = null;
		try {
			fw = new BufferedWriter( new FileWriter("P1_statistics.txt",true));
			fw.write("\n### Testing ###\n");
			fw.write("\nCorrect: "+ correct + ", correct percentage out of 4000 test samples: "+ (float)correct/(float) 4000+"\n");
			fw.write("\n");
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}





class LoadData{
	String fileName;
	ArrayList<String> lines = new ArrayList<>();
	ArrayList<DataType1> samples = new ArrayList<>();
	ArrayList<DataType1> trainSet = new ArrayList<>();
	ArrayList<DataType1> testSet = new ArrayList<>();
	ArrayList<String> lineContents;

	LoadData(String fileName){
		this.fileName = fileName;
	}
	
	void openFile(){
		lines.clear();
		samples.clear();
		trainSet.clear();
		testSet.clear();
		try {
		      File myObj = new File(fileName);
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
			samples.add(new DataType1());
			samples.get(i).x1 = Double.parseDouble(lineContents.get(0));
			samples.get(i).x2 = Double.parseDouble(lineContents.get(1));
			samples.get(i).category = lineContents.get(2);
			
			switch (lineContents.get(2)) {
			case "C1": {
				samples.get(i).encodedCats = new ArrayList<>(Arrays.asList(1,0,0,0));
				break;
			}
			case "C2": {
				samples.get(i).encodedCats = new ArrayList<>(Arrays.asList(0,1,0,0));			
				break;
						}
			case "C3": {
				samples.get(i).encodedCats = new ArrayList<>(Arrays.asList(0,0,1,0));
				break;
			}
			case "C4": {
				samples.get(i).encodedCats = new ArrayList<>(Arrays.asList(0,0,0,1));
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + lineContents.get(2));
			}
			
			//samples.get(i).pr();
			
			if(i<4000) {
				trainSet.add(samples.get(i));
			}
			else {
				testSet.add(samples.get(i));
			}
			
			Collections.shuffle(trainSet);
		}
	}
}


class DataType1{
	double x1;
	double x2;
	String category;
	ArrayList<Integer> encodedCats;
	
	void pr() {
		System.out.println("x1: "+x1+" x2: "+x2+" category: "+category+" encoded category: "+encodedCats);
	}
}



