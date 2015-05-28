import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringTokenizer;



public class MachineLearning_HW3 {
	
	//We are considering only binary attributes as of now.
	public static int MAX_ATTRIBUTES = 0;
	public static double learningRate = 0;
	public static int maxIteration = 0;
	public static ArrayList<Double> weights = new ArrayList<Double>();
	public static MachineLearning_HW3 hw3 = new MachineLearning_HW3();
	static ArrayList<Record> trainingRecords = new ArrayList<Record>();
	static ArrayList<Record> testRecords = new ArrayList<Record>();
//	static ArrayList<Feature> attributes = new ArrayList<Feature>();
	
	
	public MachineLearning_HW3() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		
		//Exactly 4 arguments are required
		if (args.length != 4) {
			System.out.println("You should give exactly 4 argumetns as input which are names of training data and test data files");
			System.exit(1);

		}
		//System.out.println("Programm in Execution");
		String trainingData = args[0];
		String testData = args[1];
		learningRate = Double.parseDouble(args[2]);
		maxIteration = Integer.parseInt(args[3]);
		double accuracy = 0.00;
		boolean readFlag = false;
		// Need the following flags while reading file
		boolean isTrainingrecord = true;
		boolean isTestrecord = true;
		readFlag = hw3
				.ReadFile(trainingData, trainingRecords, isTrainingrecord);
		if (readFlag == false) {
			System.out.println("Error reading File.");
			System.exit(1);
		}
		
		readFlag = hw3
				.ReadFile(testData, testRecords, isTestrecord);
		if (readFlag == false) {
			System.out.println("Error reading File.");
			System.exit(1);
		}
		
		//Initialize all the weights to 0
		for (int i = 0;  i< MAX_ATTRIBUTES; i++)
		{
			weights.add(0.00);
		}
		/*
		System.out.println("The weights before applying gradient Descent algorithm");
		for(int i = 0;i < MAX_ATTRIBUTES; i++)
		{
			System.out.print(" " +weights.get(i));
		}
		*/
		PerceptronLearning(trainingRecords, maxIteration);
		
		accuracy = hw3.findAccuracy(trainingRecords);
		System.out.println("Accuracy of training data :" +accuracy);
		accuracy = hw3.findAccuracy(testRecords);
		System.out.println("Accuracy of testing data :" +accuracy);
		
	}
	
	
	class Record {

		public String datasetName = "";
		// An array of the parsed features in the data.
		public String[] attributeValue = new String[MAX_ATTRIBUTES];

		// A binary feature representing the output label of the dataset.
		public String outputLabel;

		// Define constructor

		public Record() {

		}

	}
	
	//Function to read the data from the file
	public boolean ReadFile(String fileName, ArrayList<Record> records,
			boolean isTrainingRecords) {

		// Read the input file.
		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			return false;
		}

		// If the file was successfully opened, parse the file
		this.parse(fileScanner, records, isTrainingRecords);
		return true;
	}

	public void parse(Scanner fileScanner, ArrayList<Record> records,boolean trainingRecords) {
		String line = fileScanner.nextLine().trim();
		int count = 0;
		int i =0;
		String token;
		String attributeName;
		StringTokenizer st = new StringTokenizer(line);
		ArrayList<String> attributeNames = new ArrayList<String>();
		
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (trainingRecords) {
				attributeName = token;
				attributeNames.add(attributeName);
			}
		}
		// Set MAX_ATTRIBUTES while scanning training Records only
		if (trainingRecords)
			MAX_ATTRIBUTES = attributeNames.size();

		// Update the records till the end of the file
		while (fileScanner.hasNextLine()) {
		
			line = fileScanner.nextLine().trim();
			
			//ignore blank lines
			if (line.length() == 0)
			{
				continue;
			}
			st = new StringTokenizer(line);
			 i = 0;
			String tempAttribute = null;
			Record r = hw3.new Record();
			count = 0;
			while (count < MAX_ATTRIBUTES) {
				tempAttribute = st.nextToken();
				// System.out.print(attribute + " ");
				r.attributeValue[count] = (tempAttribute);
				// System.out.print("testing");
				// System.out.print(r.attributeValue[i]+ "  ");
				count++;
			}
			// Update the records classLabel
			r.outputLabel = st.nextToken();

			// Add the record to the arraylist of Records
			records.add(r);
		}				
	}
	
	public static void PerceptronLearning(ArrayList<Record> records, int maxIteration)
	{
		int count = 0;
		double sigmoidInput = 0.00;
		double sigmoidOutput = 0.00;
		double derivative = 0.00;
		double error = 0.00;
		double actualOutput = 0.00;
		do
		{
		
		//Execute the perceptron Algorithm till the end of the iteration count provided by User
		for (int i =0; i < trainingRecords.size(); i++)
		{
			sigmoidInput = CalculateSigmoidInput(i, trainingRecords);
			sigmoidOutput = CalculateSigmoidOutput(i,sigmoidInput);
			derivative = CalculateSigmoidDerivative(sigmoidOutput);
			actualOutput = Double.parseDouble(records.get(i).outputLabel);
			//System.out.println("Perceptron label :" + sigmoidOutput);
			error = CalculateError(sigmoidOutput,actualOutput);
			UpdateWeights(i,error, derivative);
			
			//increament count after reading each record set
			count++;
			if(count == maxIteration)
			{
				break;
			}
		}
		
	
		//Stop if count has reached maxIteration
		}while (count != maxIteration);
	}
	
	public static double CalculateSigmoidInput(int recordNumber, ArrayList<Record> records)
	{
		double calculatedInput = 0.00;
		for (int i =0; i < MAX_ATTRIBUTES; i++)
		{
			calculatedInput += (Double.parseDouble(records.get(recordNumber).attributeValue[i]) * weights.get(i) );

		}
		return calculatedInput;
	}
	
	public static double CalculateSigmoidOutput(int recordNumber, double sigmoidInput)
	{
		double calculatedSigmoidOutput = 0.00;
		
		calculatedSigmoidOutput = 1 / (1 + Math.exp(-(sigmoidInput)));
		return calculatedSigmoidOutput;
		
	}
	
	public static double CalculateSigmoidDerivative(double sigmoidOutput)
	{
		double calculatedDerivative = 0.00;
		
		calculatedDerivative = sigmoidOutput * (1- sigmoidOutput);
		
		return calculatedDerivative;
	}
	
	public static double CalculateError(double sigmoidOutput, double actualOutput)
	{
		double error = 0.00;
		error = (actualOutput - sigmoidOutput);
		return error;
	}
	
	public static void UpdateWeights(int recordNumber, double error, double derivative)
	{
		double newWeight = 0.00;
		for (int i = 0; i < MAX_ATTRIBUTES; i++)
		{
			newWeight = weights.get(i) + (learningRate * error * derivative * (Double.parseDouble(trainingRecords.get(recordNumber).attributeValue[i])));
			
			//Update the new weight 
			weights.set(i, newWeight);
		}
	}
	
	public double findAccuracy(ArrayList<Record> records) {
		double accuracy = 0.00;
		int count = 0;
		int size = records.size();
		count = PerceptronLabelling(records);
		//System.out.println("Count records after traversing " + count);
		accuracy = ((double) count / (double) size) * 100;

		return accuracy;
	}
	
	public static int PerceptronLabelling(ArrayList<Record> records)
	{
		int count = 0;
		double sigmoidInput = 0.00;
		double sigmoidOutput = 0.00;
		double perceptronLabel = 0.00;
		double actualLabel = 0.00;
		//Execute the perceptron Algorithm till the end of the iteration count provided by User
		for (int i =0; i < records.size(); i++)
		{
			sigmoidInput = CalculateSigmoidInput(i,records);
			sigmoidOutput = CalculateSigmoidOutput(i,sigmoidInput);
			
			//Use 0.5 as a classification threshhold i.e if perceptron o/p is > 0.5 assign class label as 1 else 0
			//System.out.println("Sigmoid Output :" + sigmoidOutput);
			if (sigmoidOutput >=0.5)
			{
				perceptronLabel = 1.00;
			}
			else
			{
				perceptronLabel = 0.00;
			}
			//System.out.println("Perceptron label :" + perceptronLabel);
			actualLabel = Double.parseDouble(records.get(i).outputLabel);
			
			//If perceptron classifies correctly increment count
			if (actualLabel == perceptronLabel)
			{
				count++;
			}
			
		}
		
		return count;
		
	}
}