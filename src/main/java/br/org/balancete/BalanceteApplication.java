package br.org.balancete;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import br.org.balancete.business.CSVWriter;
import br.org.balancete.business.reader.impl.CSVReader;
import br.org.balancete.domain.Transaction;

// @SpringBootApplication
public class BalanceteApplication {

	public static void main(String[] args) {
		// SpringApplication.run(BalanceteApplication.class, args);

		try {
			String path = System.getProperty("user.dir") + File.separator + "planilhas";
			System.out.println(path);
			// List<File> files = Files.walk(Paths.get("C:\\Users\\Zupper\\Desktop\\files"))
			List<File> files = Files.walk(Paths.get(path))
					.filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());

			CSVReader reader = new CSVReader();
			Collection<Transaction> transactions = reader.read(files);
			System.out.println(transactions.size());
			for (Transaction transaction : transactions) {
				System.out.println("tipo: " + transaction.getType().getValue());
				// System.out.println("id: " + transaction.getId());
				System.out.println("data: " + transaction.getDate());
				System.out.println("valor: " + transaction.getAmount());
				System.out.println("descricao: " + transaction.getDescription());
				System.out.println("detalhe: " + transaction.getDetail());
				System.out.println("");
			}
			CSVWriter writer = new CSVWriter();
			writer.write(transactions);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
