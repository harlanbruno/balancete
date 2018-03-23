package br.org.balancete.business;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.org.balancete.domain.Transaction;
import br.org.balancete.domain.TransactionEn;
import br.org.balancete.util.DateUtil;

public class CSVWriter {

	private static String[] columns = { "Data", "Descricao", "Detalhe", "Tipo", "Categoria", "Valor" };

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
		sheet.setDisplayGridlines(false);

		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.WHITE.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setFillForegroundColor(HSSFColorPredefined.ROYAL_BLUE.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);

		// Create a Row
		Row headerRow = sheet.createRow(0);

		// Creating cells
		for (int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(headerCellStyle);
		}

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);

		// Create Cell Style for formatting Date
		CellStyle dateCellStyle = workbook.createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
		dateCellStyle.setBorderBottom(BorderStyle.THIN);
		dateCellStyle.setBorderLeft(BorderStyle.THIN);
		dateCellStyle.setBorderTop(BorderStyle.THIN);
		dateCellStyle.setBorderRight(BorderStyle.THIN);

		CellStyle currencyCellStyle = workbook.createCellStyle();
		currencyCellStyle.setDataFormat(createHelper.createDataFormat()
				.getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* \\\"-\\\"??_);_(@_)"));
		currencyCellStyle.setBorderBottom(BorderStyle.THIN);
		currencyCellStyle.setBorderLeft(BorderStyle.THIN);
		currencyCellStyle.setBorderTop(BorderStyle.THIN);
		currencyCellStyle.setBorderRight(BorderStyle.THIN);

		// Create Other rows and cells with employees data
		int rowNum = 1;
		for (Transaction transaction : transactions) {
			Row row = sheet.createRow(rowNum++);

			Cell dateCell = row.createCell(0);
			dateCell.setCellValue(DateUtil.convertToDate(transaction.getDate()));
			dateCell.setCellStyle(dateCellStyle);

			Cell cell = row.createCell(1);
			cell.setCellValue(transaction.getDescription());
			cell.setCellStyle(dateCellStyle);

			cell = row.createCell(2);
			cell.setCellValue(transaction.getDetail());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(3);
			cell.setCellValue(transaction.getType().getValue());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(4);
			cell.setCellValue(transaction.getCategory());
			cell.setCellStyle(cellStyle);

			Cell amountCell = row.createCell(5);
			amountCell.setCellValue(transaction.getAmount().doubleValue());
			amountCell.setCellStyle(currencyCellStyle);
		}

		// Resize all columns to fit the content size
		for (int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn(i);
		}

		Sheet sheet2 = workbook.createSheet("Details");
		sheet2.setDisplayGridlines(false);

		BigDecimal creditAmount = BigDecimal.ZERO;
		BigDecimal debitAmount = BigDecimal.ZERO;

		for (Transaction transaction : transactions) {
			if (TransactionEn.CREDIT.equals(transaction.getType()))
				creditAmount = creditAmount.add(transaction.getAmount());
			else
				debitAmount = debitAmount.add(transaction.getAmount());
		}

		Row creditRow = sheet2.createRow(0);
		Cell descCell = creditRow.createCell(0);
		descCell.setCellValue("Receita");
		Cell creditCell = creditRow.createCell(1);
		creditCell.setCellValue(creditAmount.doubleValue());
		creditCell.setCellStyle(currencyCellStyle);

		Row debitRow = sheet2.createRow(1);
		descCell = debitRow.createCell(0);
		descCell.setCellValue("Despesas");
		Cell debitCell = debitRow.createCell(1);
		debitCell.setCellValue(debitAmount.doubleValue());
		debitCell.setCellStyle(currencyCellStyle);

		// Resize all columns to fit the content size
		for (int i = 0; i < 2; i++) {
			sheet2.autoSizeColumn(i);
		}

		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("poi-generated-file.xlsx");
		workbook.write(fileOut);
		fileOut.close();

		// Closing the workbook
		workbook.close();
	}
}
