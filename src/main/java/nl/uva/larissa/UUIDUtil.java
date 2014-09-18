package nl.uva.larissa;

import java.util.UUID;

public class UUIDUtil {
	public static boolean isUUID(String string) {
		try {
			return UUID.fromString(string).toString().equals(string);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
