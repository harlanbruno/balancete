package br.org.balancete.business;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.org.balancete.domain.Transaction;
import br.org.balancete.util.DateUtil;

public class CSVWriter {

	private static String[] columns = { "Data", "Descricao", "Detalhe", "Tipo", "Valor" };

	public void write(Collection<Transaction> transactions) throws IOException {

		// Create a Workbook
		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

		/*
		 * CreationHelper helps us create instances for various things like DataFormat,
		 * Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way
		 */
		CreationHelper createHelper = workbook.getCreationHelper();

		// Create a Sheet
		Sheet sheet = workbook.createSheet("Report");

		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		// Create a Row
		Row headerRow = sheet.createRow(0);

		// Creating cells
		for (int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Cell Style for formatting Date
		CellStyle dateCellStyle = workbook.createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

		CellStyle currencyCellStyle = workbook.createCellStyle();
		currencyCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* \\\"-\\\"??_);_(@_)"));

		// Create Other rows and cells with employees data
		int rowNum = 1;
		for (Transaction transaction : transactions) {
			Row row = sheet.createRow(rowNum++);

			Cell dateCell = row.createCell(0);
			dateCell.setCellValue(DateUtil.convertToDate(transaction.getDate()));
			dateCell.setCellStyle(dateCellStyle);

			row.createCell(1).setCellValue(transaction.getDescription());
			row.createCell(2).setCellValue(transaction.getDetail());
			row.createCell(3).setCellValue(transaction.getType().getValue());

			Cell amountCell = row.createCell(4);
			amountCell.setCellValue(transaction.getAmount().doubleValue());
			amountCell.setCellStyle(currencyCellStyle);
		}

		// Resize all columns to fit the content size
		for (int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn(i);
		}

		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("poi-generated-file.xlsx");
		workbook.write(fileOut);
		fileOut.close();

		// Closing the workbook
		workbook.close();
	}
}
