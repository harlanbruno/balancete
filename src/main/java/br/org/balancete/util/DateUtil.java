package br.org.balancete.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author harlan bruno
 * @since 19/03/2018
 * @version 1.0
 *
 */
public class DateUtil {

	public static LocalDateTime convertToLocalDateTime(Date date) {
		return new Timestamp(date.getTime()).toLocalDateTime();
	}
}
