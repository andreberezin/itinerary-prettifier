
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
// import java.io.BufferedWriter;
// import java.io.FileWriter;

public class Prettifier {
	// Main method to start the program
	public static void main(String[] args) {

		if (args.length > 0 && args[0].equals("-h")) { // Check if -h option is provided
			displayUsage();
			return;
		}

		readFile(args[0]);

	}

	// method to display the usage
	private static void displayUsage() {
		System.out.println("Itinerary usage:");
		System.out.println("$ java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv");
		// Add more options as needed
	}

	// method to read the file
	private static void readFile(String inputFilePath) {
		// Read the file and process the data

		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
			while ((reader.readLine()) != null) {
				System.out.println(reader.readLine()); // reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Input not found");
		}

	}
	// method to edit the file

	// method to write the result to a new file

}
