package nl.uva.larissa.json;

import java.text.FieldPosition;
import java.util.Date;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.databind.util.ISO8601Utils;

/**
 * Alternative to ISO8601DateFormat that preserves milliseconds
 **/
public class ISO8601VerboseDateFormat extends ISO8601DateFormat {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public StringBuffer format(Date date, StringBuffer stringbuffer,
			FieldPosition fieldposition) {
		String s = ISO8601Utils.format(date, true);
		stringbuffer.append(s);
		return stringbuffer;
	}
}
