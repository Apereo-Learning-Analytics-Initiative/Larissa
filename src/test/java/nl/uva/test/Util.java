package nl.uva.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Util {
	public static String readJsonFile(File textFile) throws IOException {
		try (BufferedReader reader = new BufferedReader(
				new FileReader(textFile))) {
			final StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.replaceAll(" ", "");
				result.append(line);
			}
			return result.toString();
		}
	}
}
