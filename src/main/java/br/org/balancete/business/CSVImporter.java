package br.org.balancete.business;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import br.org.balancete.domain.Transaction;
import br.org.balancete.domain.TransactionEn;
import br.org.balancete.util.DateUtil;
import br.org.balancete.util.NumberUtil;

public class CSVImporter {

	public Collection<Transaction> doImport(Collection<File> files) {

		Collection<Transaction> transactions = new ArrayList<Transaction>();
		files.forEach(file -> transactions.addAll(doImport(file)));
		return transactions;
	}

	public Collection<Transaction> doImport(File file) {

		Collection<Transaction> transactions = new ArrayList<>();

		try (Workbook workbook = WorkbookFactory.create(file)) {
			workbook.forEach(sheet -> {
				Row[] rows = IteratorUtils.toArray(sheet.iterator(), Row.class);
				for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {

					Row row = rows[rowIndex];

					Cell cellDate = row.getCell(0);
					Cell cellDescription = row.getCell(1);
					Cell cellAmount = row.getCell(2);

					Transaction transaction = new Transaction();
					transaction.setDate(DateUtil.convertToLocalDateTime(cellDate.getDateCellValue()));
					transaction.setDescription(getDescription(cellDescription));
					transaction.setType(getType(cellAmount));
					transaction.setAmount(getAmout(cellAmount));
					StringBuilder detail = new StringBuilder();

					for (rowIndex++; rowIndex < rows.length; rowIndex++) {
						row = rows[rowIndex];
						if (!isCellEmpty(row.getCell(0))) {
							rowIndex--;
							break;
						}
						detail.append(getDescription(row.getCell(1)));
						detail.append(StringUtils.SPACE);
					}
					transaction.setDetail(detail.toString());
					transactions.add(transaction);
				}
			});
		} catch (Exception e) {
			// TODO: log this
			e.printStackTrace();
		}

		return transactions;
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
