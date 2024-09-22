
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

		// ArrayList<String> airportList = readCSV(args[2]);

		writeFile(args[1], modifyDates(modifyAirportNames(readFile(args[0]))));

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

	// method to convert the airport code
	private static ArrayList<String> modifyAirportNames(ArrayList<String> inputList) {
		int index = 0;
		ArrayList<String> airportList = readCSV("airport-lookup.csv");

		for (String word : inputList) {

			if (word.startsWith("#")) { // would be safer with regex
				word = word.replaceAll("\\W+", "");

				// find the match from airportList and replace
				for (String airport : airportList) {
					// find the match from airportList and replace

					if (airport.contains(word)) {
						String[] airportArray = airport.split(",");
						// String airportName = airport.substring(0, airport.indexOf(","));
						String destinationAirport = airportArray[0];
						inputList.set(index, destinationAirport);
						break;
					}
				}
			}

			index++;
		}

		return inputList;
	}

	// method to modify the dates

	private static ArrayList<String> modifyDates(ArrayList<String> inputList) {
		int index = 0;

		for (String word : inputList) {
			if (word.startsWith("D")) { // if word is date && word matches regex of date format
				// Dates must be displayed in the output as DD-Mmm-YYYY format. E.g. "05 Apr
				// 2007"
				String date = "DATE";
				inputList.set(index, date);
			}
			if (word.startsWith("T12")) { // if word is 12 hour time && word matches regex of date format
				// must be displayed as "12:30PM (-02:00)".
				String date = "T12";
				inputList.set(index, date);
			}
			if (word.startsWith("T24")) { // if word is 24 hour time && word matches regex of date format
				// must be displayed as "12:30 (-02:00)"
				String date = "T24";
				inputList.set(index, date);
			}

			index++;
		}

		System.out.println(inputList);
		return inputList;
	}

	// method to read the csv file to an array list
	private static ArrayList<String> readCSV(String airportCSV) {

		ArrayList<String> airportList = new ArrayList<>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(airportCSV));
			String line;
			while ((line = reader.readLine()) != null) {
				// airportList.addAll(Arrays.asList(line.split(" ")));
				airportList.add(line);
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("File not found");
		}

		// System.out.println(airportList);
		return airportList;
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
