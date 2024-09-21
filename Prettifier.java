
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Prettifier {
	// Main method to start the program
	public static void main(String[] args) {

		if (args.length > 0 && args[0].equals("-h")) { // Check if -h option is provided
			displayUsage();
			return;
		}

		writeFile(args[1], modifyInput(readFile(args[0])));

	}

	// method to display the usage
	private static void displayUsage() {
		System.out.println("Itinerary usage:");
		System.out.println("$ java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv");
		// Add more options as needed
	}

	// method to read the file to an array list
	private static ArrayList<String> readFile(String inputFilePath) {
		// Read the file and process the data

		ArrayList<String> inputList = new ArrayList<>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
			String line;
			while ((line = reader.readLine()) != null) {
				inputList.addAll(Arrays.asList(line.split(" ")));
				// System.out.println(inputList); // reader.readLine();

				// inputList.add("new line");
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Input not found");
		}

		return inputList;

	}

	// method to convert the airport code using a CSV file
	private static ArrayList<String> modifyInput(ArrayList<String> inputList) {
		// String airpodCode = "";
		// String airportName = "";
		int index = 0;

		for (String word : inputList) {

			if (word.startsWith("#")) { // would be safer with regex
				inputList.set(index, "airportname1");
			}

			if (word.startsWith("##")) { // would be safer with regex
				inputList.set(index, "airportname2");
			}

			// if (inputList.get(index).matches("new line")) {
			// inputList.set(index, "/n");
			// }

			index++;
		}

		System.out.println(inputList);
		return inputList;
	}

	// method to write the result to a new file
	private static void writeFile(String outputFilePath, ArrayList<String> inputList) {
		// Read the file and process the data

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

			for (String word : inputList) {
				writer.write(word + " ");

			}
			writer.close();
		} catch (IOException e) {
			System.out.println("Input not found");
		}
	}

}
