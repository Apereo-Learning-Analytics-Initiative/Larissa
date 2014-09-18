package nl.uva.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Util {
	public static String readFile(File textFile) throws IOException {
		try (BufferedReader reader = new BufferedReader(
				new FileReader(textFile))) {
			final StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line).append('\n');
			}
			if (result.length() > 0) {
				result.setLength(result.length() - 1);
			}
			return result.toString();
		}
	}
}
