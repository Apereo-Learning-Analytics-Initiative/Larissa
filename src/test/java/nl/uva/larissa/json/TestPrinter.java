package nl.uva.larissa.json;

import java.io.File;
import java.io.IOException;

import nl.uva.larissa.json.model.Statement;
import nl.uva.test.Util;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPrinter {

	private static final File BASEDIR = new File("src/test/resources");
	private static final File EXPECTED_DIR = new File(BASEDIR, "expectedIds");
	private static StatementParser parser;

	@BeforeClass
	public static void beforeClass() {
		parser = new StatementParserImpl();
	}

	@Test
	public void testPrintIds() throws IOException, ParseException {
		StatementPrinter printer = new StatementPrinterImpl();
		String file = "testLongExampleStatement.json";
		String input = Util.readFile(new File(BASEDIR, file));
		Statement statement = parser.parseStatement(input);

		String expectedOutput = Util.readFile(new File(EXPECTED_DIR, file));

		assertEquals(expectedOutput, printer.printIds(statement));
	}
}
