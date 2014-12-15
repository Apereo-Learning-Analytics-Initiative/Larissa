package nl.uva.larissa.json;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import nl.uva.larissa.json.model.Statement;
import nl.uva.test.Util;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestStatementParser {

	private static StatementParser parser;
	private static StatementPrinter printer;

	private static File testFileDir = new File("src/test/resources/");
	private static File expectedOutputDir = new File(
			"src/test/resources/expected/");
	private static Validator validator;

	@BeforeClass
	public static void beforeClass() {
		parser = new StatementParserImpl();
		printer = new StatementPrinterImpl();

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

		validator = factory.getValidator();
	}

	@Test
	public void testParser() throws IOException {
		File[] testFiles = testFileDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith("json");
			}
		});

		for (File testFile : testFiles) {
			String jsonInput;

			jsonInput = Util.readJsonFile(testFile);

			Statement statement;
			System.out.println("TestStatementParser testing " + testFile);
			try {
				statement = parser.parseStatement(jsonInput);
				assertNotNull("read statement for " + testFile.getName()
						+ " should not be null", statement);

				Set<ConstraintViolation<Statement>> violations = validator
						.validate(statement);
				assertEquals(String.format(
						"%s should validate but has violations: %s",
						testFile.getName(), violations), 0, violations.size());

			} catch (ParseException e) {
				e.printStackTrace();
				assertTrue(
						"no ParseException should occur for "
								+ testFile.getName(), false);
				break;
			} catch (ValidationException e2) {
				e2.printStackTrace();
				assertTrue("no ValidationException should occur for "
						+ testFile.getName(), false);
				break;
			}
			String expectedOutput;
			String message;
			File expectedFile = new File(expectedOutputDir, testFile.getName());
			if (expectedFile.exists()) {

				expectedOutput = Util.readJsonFile(expectedFile);

				message = "printing " + testFile.getName()
						+ " should match expected file";
			} else {
				expectedOutput = jsonInput;
				message = "printing " + testFile.getName()
						+ " should match itself";
			}
			try {
				assertEquals(message, expectedOutput,
						printer.printStatement(statement));
			} catch (IOException e) {
				e.printStackTrace();
				assertTrue("no IOException while printing should occur for "
						+ testFile.getName(), false);
			}

		}
	}

}
