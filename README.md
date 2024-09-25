**Description**

The Prettifier tool reads a text-based itinerary from a file, reformats the text to make it customer-friendly, and writes the results to a new file. It reformats dates from ISO 8601 standard to a more readable format and converts IATA and ICAO codes to either airport names or city names. It also remvoes unnecessary whitespace. The tool also needs a airport-lookup.csv file to find match IATA and ICAO codes to airport and city names.

**Usage**

java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv

_To display usage_
java Prettifier.java -h

_To print the last output file_
java Prettifier.java -o
