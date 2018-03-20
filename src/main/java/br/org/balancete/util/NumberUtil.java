package br.org.balancete.util;

import java.math.BigDecimal;

/**
 * @author harlan bruno
 * @since 19/03/2018
 * @version 1.0
 *
 */
public class NumberUtil {

	public static BigDecimal convertToBigDecimal(Double value) {
		return BigDecimal.valueOf(value);
	}
}
