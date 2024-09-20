
public class Prettifier {
	// Main method to start the program
	public static void main(String[] args) {

		// System.out.println("Prettifier");

		if (args.length > 0 && args[0].equals("-h")) {
			displayUsage();
			return;
		}

	}

	// method to display the usage
	private static void displayUsage() {
		System.out.println("Itinerary usage:");
		System.out.println("$ java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv");
		// Add more options as needed
	}

	// method to read the file

	// method to edit the file

	// method to write the result to a new file

}