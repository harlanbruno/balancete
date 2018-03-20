package br.org.balancete.domain;

/**
 * @author harlan bruno
 * @since 19/03/2018
 * @version 1.0
 *
 */
public enum TransactionEn {

	CREDIT("Credito"), DEBIT("Debito");

	private String value;

	private TransactionEn(String value) {
		this.value = value;
	}

	public static TransactionEn get(String value) {

		return (value == null || "DEP".equalsIgnoreCase(value)) ? CREDIT : DEBIT;
	}

	public String getValue() {
		return value;
	}

}
