package parsers;

import formatters.DateTimeFormats;

import java.time.LocalDateTime;

public class StringParsers {
	public static LocalDateTime parseDate(String toParse) {
		if (toParse == null) return null;
		LocalDateTime parsed;
		try {
			parsed = LocalDateTime.parse(toParse + " 00:00:00", DateTimeFormats.DATE_TIME_24);
		} catch (Exception e) {parsed = null;}
		return parsed;
	}
	public static long parseLong(String toParse) {
		long parsed;
		try {
			parsed = Integer.parseInt(toParse.trim());
		} catch (Exception e) {return 0;}
		return parsed;
	}
}
