package br.org.balancete.business.reader.impl;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import br.org.balancete.business.reader.FileReader;
import br.org.balancete.domain.Transaction;
import br.org.balancete.domain.TransactionEn;
import br.org.balancete.util.Constants;
import br.org.balancete.util.Constants.Category.Credit;
import br.org.balancete.util.Constants.Category.Debit;
import br.org.balancete.util.DateUtil;
import br.org.balancete.util.NumberUtil;

public class CSVReader implements FileReader<Transaction> {

	@Override
	public Collection<Transaction> read(Collection<File> files) {
		Collection<Transaction> transactions = new ArrayList<Transaction>();
		files.forEach(file -> transactions.addAll(read(file)));
		return transactions;
	}

	@Override
	public Collection<Transaction> read(File file) {
		Collection<Transaction> transactions = new ArrayList<>();

		try (Workbook workbook = WorkbookFactory.create(file)) {
			workbook.forEach(sheet -> {
				addRows(sheet.iterator(), transactions, new Transaction());
			});
		} catch (Exception e) {
			// TODO: log this
			e.printStackTrace();
		}

		return transactions;
	}

	private void addRows(Iterator<Row> rows, Collection<Transaction> transactions, Transaction transaction) {
		addRow(rows, rows.next(), transactions, transaction);
	}

	private void addRow(Iterator<Row> rows, Row row, Collection<Transaction> transactions, Transaction transaction) {

		if (!isCellEmpty(row.getCell(0))) {
			transaction.setDate(getDate(row.getCell(0)));
			transaction.setDescription(getDescription(row.getCell(1)));
			transaction.setType(getType(row.getCell(2)));
			transaction.setAmount(getAmout(row.getCell(2)));
		} else {
			StringBuilder detail = new StringBuilder(transaction.getDetail());
			detail.append(getDescription(row.getCell(1)));
			detail.append(StringUtils.SPACE);
			transaction.setDetail(detail.toString());
		}

		if (rows.hasNext()) {

			Row next = rows.next();

			if (!isCellEmpty(next.getCell(0))) {
				add(transactions, transaction);
				addRow(rows, next, transactions, new Transaction());
			} else {
				addRow(rows, next, transactions, transaction);
			}
		} else {
			add(transactions, transaction);
		}
	}

	private void add(Collection<Transaction> transactions, Transaction transaction) {

		fillCategory(transaction);

		transactions.add(transaction);
	}

	private void fillCategory(Transaction transaction) {
		if (TransactionEn.CREDIT.equals(transaction.getType())) {
			fillCreditCategory(transaction);
		} else {
			fillDebitCategory(transaction);
		}
	}

	private void fillCreditCategory(Transaction transaction) {
		if (StringUtils.startsWithAny(transaction.getDescription(), "732")) {
			transaction.setCategory(Credit.RECEIVED_CIELO);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "624")) {
			transaction.setCategory(Credit.BANK_TICKET);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "900")) {
			transaction.setCategory(Credit.RECEIVED_VINDI);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "870")) {
			if (StringUtils.containsIgnoreCase(transaction.getDetail(), "PREFACUL")) {
				transaction.setCategory(Credit.TRANSF_BETWEEN_ACCOUNTS);
			} else {
				transaction.setCategory(Credit.DEPOSIT);
			}
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "976", "623", "830")) {
			transaction.setCategory(Credit.DEPOSIT);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "848")) {
			transaction.setCategory(Credit.FINANCIAL_APPLICATION);
		} else {
			transaction.setCategory(Credit.OTHER);
		}
	}

	private void fillDebitCategory(Transaction transaction) {

		if (StringUtils.startsWithAny(transaction.getDescription(), "470")) {
			if (StringUtils.containsIgnoreCase(transaction.getDescription(), "INSTITUTO")) {
				transaction.setCategory(Debit.TRANSF_BETWEEN_ACCOUNTS);
			} else {
				transaction.setCategory(Debit.BILL_PAYMENT);
			}
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "393")) {
			if (StringUtils.containsIgnoreCase(transaction.getDescription(), "PREFACUL")) {
				transaction.setCategory(Debit.TRANSF_BETWEEN_ACCOUNTS);
			} else {
				transaction.setCategory(Debit.BILL_PAYMENT);
			}
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "109", "144", "362", "363", "375", "500")) {
			transaction.setCategory(Debit.BILL_PAYMENT);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "CP MAESTRO")) {
			transaction.setCategory(Debit.BILL_PAYMENT);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "DB CEST")) {
			transaction.setCategory(Debit.BILL_PAYMENT);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "DOC/TED INTERNET")) {
			transaction.setCategory(Debit.BILL_PAYMENT);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "ENVIO TEV")) {
			transaction.setCategory(Debit.BILL_PAYMENT);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "PG PREFEIT")) {
			transaction.setCategory(Debit.BILL_PAYMENT);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "234")) {
			transaction.setCategory(Debit.PURCHASE_IN_DEBT);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "435")) {
			transaction.setCategory(Debit.BANK_MAINTENANCE);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "MANUT CTA")) {
			transaction.setCategory(Debit.BANK_MAINTENANCE);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "124", "310")) {
			transaction.setCategory(Debit.BANK_RATE);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "TAR BCO24H")) {
			transaction.setCategory(Debit.BANK_RATE);
		} else if (StringUtils.startsWithAny(transaction.getDescription(), "TR TEV IBC")) {
			transaction.setCategory(Debit.BANK_RATE);
		} else {
			transaction.setCategory(Debit.OTHER);
		}
	}

	private Boolean isCellEmpty(final Cell cell) {
		if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return Boolean.TRUE;
		}

		if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().isEmpty()) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	private BigDecimal getAmout(Cell cell) {

		if (isCellEmpty(cell)) {
			return null;
		}

		String value = cell.getStringCellValue().replaceAll("[^0-9]", "");
		String integer = value.substring(0, value.length() - 2);
		String decimal = value.substring(value.length() - 2);
		StringBuilder formattedValue = new StringBuilder();
		formattedValue.append(integer);
		formattedValue.append(".");
		formattedValue.append(decimal);

		return NumberUtil.convertToBigDecimal(formattedValue.toString());
	}

	private TransactionEn getType(Cell cell) {

		return cell.getStringCellValue().contains("C") ? TransactionEn.CREDIT : TransactionEn.DEBIT;
	}

	private String getDescription(Cell cell) {
		return cell.getStringCellValue().toUpperCase();
	}

	private LocalDateTime getDate(Cell cell) {

		if (isCellEmpty(cell)) {
			return null;
		}

		return DateUtil.convertToLocalDateTime(cell.getDateCellValue());
	}

}
