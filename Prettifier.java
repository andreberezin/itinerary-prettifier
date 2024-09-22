
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Prettifier {
	// Main method to start the program
	public static void main(String[] args) throws ParseException {

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
			System.out.println("Airport lookup not found");
		}

		// System.out.println(airportList);
		return airportList;
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

	private static ArrayList<String> modifyDates(ArrayList<String> inputList) throws ParseException {
		int index = 0;

		for (String word : inputList) {

			// Pattern pattern = Pattern.compile("(T12|D|T24)(([^)]+))");
			Pattern pattern = Pattern.compile("(T12|D|T24)(\\(([^)]+)\\))");
			Matcher matcher = pattern.matcher(word);

			if (matcher.find()) {
				String date = (String) matcher.group(3);
				String format = matcher.group(1);
				String formattedDate = date;

				if (format.equals("D")) { // if word is date && word matches

					LocalDate localDate = LocalDate.parse(date.substring(0, 10));
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
					formattedDate = localDate.format(formatter);

				}
				if (format.equals("T12")) { // if word is 12 hour time && word matches regex of date format
					// convert to 12-hour time format
					LocalDateTime localDateTime = LocalDateTime.parse(date.substring(0, 16));
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");
					String formattedTime = localDateTime.format(formatter);

					// add timezone offset
					if (date.endsWith("Z")) {
						formattedDate = formattedTime + " (+00:00)";

					} else {
						String timezoneOffset = date.substring(16);
						ZoneOffset zoneOffsetObj = ZoneOffset.ofHoursMinutes(
								Integer.parseInt(timezoneOffset.substring(0, 3)),
								Integer.parseInt(timezoneOffset.substring(4)));
						formattedDate = formattedTime + " (" + zoneOffsetObj.toString() + ")";
					}
				}
				if (format.equals("T24")) { // if word is 24 hour time && word matches regex of date format
					String time = date.substring(11, 16);
					// add timezone offset
					if (date.endsWith("Z")) {
						formattedDate = time + " (+00:00)";

					} else {
						formattedDate = time + " (" + date.substring(16, date.length()) + ")";
					}
				}

				inputList.set(index, formattedDate);
			}

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
