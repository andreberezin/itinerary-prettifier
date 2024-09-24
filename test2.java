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

public class test2 {
	// Main method to start the program
	public static void main(String[] args) throws ParseException {

		if (args.length > 0 && args[0].equals("-h") || args.length < 3) { // Check if -h option is provided
			displayUsage();
			return;
		}
		airportCSVpath = args[2];

		if (isDataMalformed(airportCSVpath) == "Airport lookup file not found") {
			System.out.println(isDataMalformed(airportCSVpath));
			return;
		} else if (isDataMalformed(airportCSVpath) == "Airport lookup malformed") {
			System.out.println(isDataMalformed(airportCSVpath));
			return;
		} else {
			// Read the input file and process the data
			try (BufferedReader reader = new BufferedReader(new FileReader(args[0]));
					BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]))) {

				String line;
				String lastNotEmptyLine = "";
				while ((line = reader.readLine()) != null) {

					// if (line != null) {
					// String modifiedLine = modifyLine(line.strip()) + "\n";
					// // Write the modified line to the output file
					// writer.write(modifiedLine);
					// }
					String modifiedLine = modifyLine(line);

					if (!modifiedLine.isEmpty()) {
						lastNotEmptyLine = modifiedLine;
						writer.write(modifiedLine + "\n");
					}

					System.out.println("Current line: " + line);
					System.out.println("Previous line: " + lastNotEmptyLine);
				}
				writer.close();
			} catch (IOException e) {
				System.out.println("Input not found");
			}

		}

	}

	// Method to display the usage
	private static void displayUsage() {
		System.out.println("Itinerary usage:");
		System.out.println("$ java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv");
		// Add more options as needed
	}

	static String airportCSVpath;

	// Method to modify a line
	private static String modifyLine(String lineInput) {

		// find match for whitespace characters and replace with newline
		Pattern patternWhitespace = Pattern.compile("\f|\r|\n" + "\\x0B\\f\\r\\x85\\u2028\\u2029");
		Matcher matcherWhitespace = patternWhitespace.matcher(lineInput);

		if (matcherWhitespace.find()) {
			lineInput = lineInput.replace(matcherWhitespace.group(0), "\n");
		}

		lineInput = lineInput.replaceAll("\n{2,}", "\n");

		// lineInput = lineInput.replaceAll("\n+", "\n").trim(); // if a single line has
		// multiple new lines
		lineInput = modifyAirportNames(lineInput);
		lineInput = modifyDates(lineInput);

		lineInput = lineInput.trim();

		return lineInput;
	}

	public static String modifyAirportNames(String lineInput) {
		// find match for the IATA airport code in the input.txt file
		Pattern patternIATA = Pattern.compile("(#\\w{3})");
		Matcher matcherIATA = patternIATA.matcher(lineInput);
		// String airportCode = matcher.group(1);
		String airportCode = "";
		String[] airport;

		if (matcherIATA.find()) {
			airportCode = matcherIATA.group(1);
		} // read the CSV file only if a match has been found from input.txt to find
			// matching line from the CSV

		if (!airportCode.isEmpty())
			try {
				BufferedReader readerAirport = new BufferedReader(new FileReader(airportCSVpath));

				String airportLine = readerAirport.readLine();
				while ((airportLine = readerAirport.readLine()) != null) {
					if (airportLine.contains(airportCode.substring(1, airportCode.length()))) {
						airport = airportLine.split(",");
						String airportName = airport[airportCsvNameRow];
						lineInput = lineInput.replace(airportCode, airportName);
						readerAirport.close();
					}
				}
				readerAirport.close();
			} catch (IOException e) { // in case airport lookup file not found
				if (e.getMessage().contains("No such file or directory")) {
					// Handle the case when the airport lookup file is not found
					System.out.println("Airport lookup file not found");
				}
			}

		// find match for the ICAO airport code in the input.txt file
		Pattern patternICAO = Pattern.compile("(#{2}\\w{4})");
		Matcher matcherICAO = patternICAO.matcher(lineInput);

		if (matcherICAO.find()) {
			airportCode = matcherICAO.group(1);
		} // read the CSV file only if a match has been found from input.txt to find
			// matching line from the CSV

		if (!airportCode.isEmpty())
			try {
				BufferedReader readerAirport = new BufferedReader(new FileReader(airportCSVpath));

				String airportLine = readerAirport.readLine();
				while ((airportLine = readerAirport.readLine()) != null) {
					if (airportLine.contains(airportCode.substring(2, airportCode.length()))) {
						airport = airportLine.split(",");
						String airportName = airport[airportCsvNameRow];
						lineInput = lineInput.replace(airportCode, airportName);
					}
				}
				readerAirport.close();
			} catch (IOException e) { // in case airport lookup file not found
				if (e.getMessage().contains("No such file or directory")) {
					// Handle the case when the airport lookup file is not found
					// System.out.println("Airport lookup file not found");
				}
			}
		return lineInput;
	}

	public static String modifyDates(String lineInput) {
		// modify the date and time formats
		Pattern patternDate = Pattern.compile("(T12|D|T24)\\(([^)]+)\\)");
		Matcher matcherDate = patternDate.matcher(lineInput);
		// 22, 17
		if (matcherDate.find())

		{
			String date = matcherDate.group(2);
			String format = matcherDate.group(1);
			String formattedDate = date;

			// if date is malformed return inital value
			if (date.length() != 22 && date.length() != 17) {
				return lineInput;
			}

			if (format.equals("D")) { // If word is date and matches regex
										// of date format
				LocalDate localDate = LocalDate.parse(date.substring(0, 10));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
				formattedDate = localDate.format(formatter);
			}
			if (format.equals("T12")) { // If word is 12-hour time and
										// matches regex of date format
				// Convert to 12-hour time format
				LocalDateTime localDateTime = LocalDateTime.parse(date.substring(0, 16));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");
				String formattedTime = localDateTime.format(formatter);

				// Add timezone offset
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
			if (format.equals("T24")) { // If time is 24-hour and matches
										// regex of date format
				String time = date.substring(11, 16);
				// Add timezone offset
				if (date.endsWith("Z")) {
					formattedDate = time + " (+00:00)";
				} else {
					formattedDate = time + " (" + date.substring(16, date.length()) + ")";
				}
			}

			// Replace the original date and time format with the formatted date and time
			// format
			lineInput = lineInput.replace(matcherDate.group(0), formattedDate);
		}

		return lineInput;
	}

	static int airportCsvNameRow;

	// checks if airport lookup CSV file is found and if it's malformed or not
	public static String isDataMalformed(String airportCSVpath) {
		String[] data;

		try {
			BufferedReader readerCSV = new BufferedReader(new FileReader(airportCSVpath));

			String line = readerCSV.readLine();
			while ((line = readerCSV.readLine()) != null) {
				data = line.split(",");

				if (data.length < 6) {
					readerCSV.close();
					return "Airport lookup malformed";
				}

				int index = 0;
				for (String cell : data) {

					if (cell.trim() == "name") {
						airportCsvNameRow = index;
					}

					if (cell.trim().isEmpty()) {
						readerCSV.close();
						return "Airport lookup malformed";
					}
					index++;
				}
			}
			readerCSV.close();
		} catch (IOException e) { // in case airport lookup file not found
			if (e.getMessage().contains("No such file or directory")) {
				// Handle the case when the airport lookup file is not found
				return "Airport lookup file not found";
			}
		}
		return "";
	}
}