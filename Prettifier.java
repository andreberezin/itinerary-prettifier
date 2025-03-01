import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Prettifier {
	// Main method to start the program
	public static void main(String[] args) throws ParseException {

		if (args.length == 0) {
			displayUsage();
			return;
		}

		if (args[0].equals("-o") && args.length == 1) { // -o flag to display output
			displayOutput();
			return;
		}

		if (args[0].equals("-h") || args.length != 3) { // -h flag to display usage
			displayUsage();
			return;
		}

		airportCSVpath = args[2];

		if (isDataMalformed(airportCSVpath) == "Airport lookup not found") {
			System.out.println("\u001B[31m" + isDataMalformed(airportCSVpath) + "\u001B[0m");
			return;
		} else if (isDataMalformed(airportCSVpath) == "Airport lookup malformed") {
			System.out.println("\u001B[31m" + isDataMalformed(airportCSVpath) + "\u001B[0m");
			return;
		} else {
			// Read the input file and write into a new file
			try (BufferedReader reader = new BufferedReader(new FileReader(args[0]));
					BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]))) {

				String line;
				boolean previousLineIsEmpty = true;
				while ((line = reader.readLine()) != null) {

					String modifiedLine = modifyLine(line);

					// in case multiple empty lines are in a row
					if (previousLineIsEmpty == true && modifiedLine.isEmpty()) {
						previousLineIsEmpty = true;
					}
					if (modifiedLine.isEmpty() && previousLineIsEmpty == false) {
						previousLineIsEmpty = true;
						writer.write("\n" + modifiedLine);
					}
					if (!modifiedLine.isEmpty()) {
						previousLineIsEmpty = false;
						writer.write(modifiedLine + "\n");
					}
				}
				writer.close();
			} catch (IOException e) {
				System.out.println("\u001B[31m" + "Input not found" + "\u001B[0m");
			}

		}

	}

	// Method to display the usage
	private static void displayUsage() {
		System.out.println("\u001B[34m" + "Itinerary usage:" + "\u001B[0m");
		System.out.println(
				"\u001B[34m" + "$ java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv" + "\u001B[0m");
		// Add more options as needed
	}

	static String outputPath;

	private static void displayOutput() {
		try {
			BufferedReader readerOutput = new BufferedReader(new FileReader("output.txt"));

			String lineOutput;
			while ((lineOutput = readerOutput.readLine()) != null) {
				System.out.println("\u001B[32m" + lineOutput + "\u001B[0m");
			}
			readerOutput.close();
		} catch (IOException e) { // in case airport lookup file not found
			System.out.println("\u001B[31m" + "Output not found" + "\u001B[0m");
		}
	}

	static String airportCSVpath;

	// Method to modify a line
	private static String modifyLine(String lineInput) {

		lineInput = modifyWhitespace(lineInput);
		lineInput = modifyAirportNames(lineInput);
		lineInput = modifyDates(lineInput);

		return lineInput.trim();
	}

	public static String modifyWhitespace(String lineInput) {

		lineInput = lineInput.replaceAll("\\\\f", "\n")
				.replaceAll("\n{2,}", "\n")
				.replaceAll("\\\\r", "\n")
				.replaceAll("\\\\v", "\n");

		return lineInput;
	}

	// Method to convert IATA and ICAO codes to airport names
	public static String modifyAirportNames(String lineInput) {
		// find match for the IATA airport code in the input.txt file

		String airportCode = "";
		String airportName = "";
		String cityName = "";
		String[] airport;

		String[] lineInputArray = lineInput.split(" ");

		for (String word : lineInputArray) {
			word = word.replace(".", "").trim();
			word = word.replace(",", "").trim();

			if (word.matches("#{1}[A-Z]{3}") || word.matches("#{2}[A-Z]{4}") || word.matches("[*]#[A-Z]{3}")
					|| word.matches("[*]#{2}[A-Z]{4}")) {
				airportCode = word;
				try {
					BufferedReader readerAirport = new BufferedReader(new FileReader(airportCSVpath));
					String airportLine = readerAirport.readLine();
					while ((airportLine = readerAirport.readLine()) != null) {
						airportLine = airportLine.replaceAll(", ", ":");
						airport = airportLine.split(",");
						for (String airportCell : airport) {
							if (airportCode.startsWith("*")) {
								if (airportCell.equals(airportCode.substring(2, airportCode.length()))) {
									cityName = airport[airportCsvCityNameRow];
									lineInput = lineInput.replace(airportCode, cityName);
								}
								if (airportCell.equals(airportCode.substring(4, airportCode.length()))) {
									cityName = airport[airportCsvCityNameRow];
									lineInput = lineInput.replace(airportCode, cityName);
								}
							} else {
								if (airportCell.equals(airportCode.substring(1, airportCode.length()))) {
									airportName = airport[airportCsvAirportNameRow];
									lineInput = lineInput.replace(airportCode, airportName);
								}
								if (airportCell.equals(airportCode.substring(2, airportCode.length()))) {
									airportName = airport[airportCsvAirportNameRow];
									lineInput = lineInput.replace(airportCode, airportName);
								}
							}
						}
					}
					readerAirport.close();
				} catch (IOException e) { // in case airport lookup file not found
					if (e.getMessage().contains("\u001B[31m" + "No such file or directory" + "\u001B[0m")) {
						System.out.println("\u001B[31m" + "Airport lookup file not found" + "\u001B[0m");
					}
				}
			}

		}

		return lineInput;
	}

	// Method to modify the date and time formats
	public static String modifyDates(String lineInput) {
		Pattern patternDate = Pattern.compile("(T12|D|T24)\\(([^)]+)\\)");
		Matcher matcherDate = patternDate.matcher(lineInput);
		if (matcherDate.find()) {
			String date = matcherDate.group(2);
			String format = matcherDate.group(1);
			String formattedDate = date;

			// if date is malformed return inital value
			if (date.length() != 22 && date.length() != 17) {
				return lineInput;
			}

			if (format.equals("D")) { // if date format
				LocalDate localDate = LocalDate.parse(date.substring(0, 10));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
				formattedDate = localDate.format(formatter);
			}
			if (format.equals("T12")) { // if 12-hour format
				LocalDateTime localDateTime = LocalDateTime.parse(date.substring(0, 16));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");
				String formattedTime = localDateTime.format(formatter);

				// Add timezone offset
				if (date.endsWith("Z")) { // in case of Zulu time
					formattedDate = formattedTime + " (+00:00)";
				} else {
					String timezoneOffset = date.substring(16);
					ZoneOffset zoneOffsetObj = ZoneOffset.ofHoursMinutes(
							Integer.parseInt(timezoneOffset.substring(0, 3)),
							Integer.parseInt(timezoneOffset.substring(4)));
					formattedDate = formattedTime + " (" + zoneOffsetObj.toString() + ")";
				}
			}
			if (format.equals("T24")) { // if 24-hour format
				String time = date.substring(11, 16);
				// Add timezone offset
				if (date.endsWith("Z")) { // in case of Zulu time
					formattedDate = time + " (+00:00)";
				} else {
					formattedDate = time + " (" + date.substring(16, date.length()) + ")";
				}
			}
			lineInput = lineInput.replace(matcherDate.group(0), formattedDate);
		}
		return lineInput;
	}

	static int airportCsvAirportNameRow; // save row number for airport names in airport lookup csv file
	static int airportCsvCityNameRow; // save row number for city names in airport lookup csv file

	// Method to check if airport lookup CSV file is found and if it's malformed or
	// not
	public static String isDataMalformed(String airportCSVpath) {
		String[] data;

		try {
			BufferedReader readerCSV = new BufferedReader(new FileReader(airportCSVpath));
			String[] header = readerCSV.readLine().split(",");

			// find the row that contains airport names
			for (int i = 0; i < header.length; i++) {

				if (header[i].equals("name")) {
					airportCsvAirportNameRow = i;
				}
				if (header[i].equals("municipality")) {
					airportCsvCityNameRow = i;
				}
				// check if data is malformed by checking if number of rows matches or not
				if (header.length != 6) {
					readerCSV.close();
					return "Airport lookup malformed";
				}
			}

			String line;
			while ((line = readerCSV.readLine()) != null) {
				line = line.replaceAll(", ", ":");
				data = line.split(",");

				for (String cell : data) { // iterate over every cell to check if data is malformed by checking if a
											// cell is empty
					if (cell.trim().isEmpty() || data.length != 6) {
						readerCSV.close();
						return "Airport lookup malformed";
					}

				}
			}
			readerCSV.close();
		} catch (IOException e) { // in case airport lookup file not found
			if (e.getMessage().contains("No such file or directory")) {
				return "Airport lookup not found";
			}
		}
		return "";
	}
}