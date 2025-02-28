**Description**

The Prettifier tool reads a text-based itinerary from a file, reformats the text to make it customer-friendly, and writes the results to a new file. It reformats dates from ISO 8601 standard to a more readable format and converts IATA and ICAO codes to either airport names or city names. It also removes unnecessary whitespace. The tool also needs a airport-lookup.csv file to find match IATA and ICAO codes to airport and city names.

**Usage**

```bash 
  java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv 
```

To display usage
```bash
  java Prettifier.java -h
```

To print the last output file
```bash
  java Prettifier.java -o
```

