package br.org.balancete.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.stereotype.Component;

import br.org.balancete.domain.Transaction;
import br.org.balancete.facade.OFX4JFacade;

/**
 * @author harlan bruno
 * @since 19/03/2018
 * @version 1.0
 *
 */
@Component
public class OFXImporter {

	public Collection<Transaction> doImport(Collection<File> files) {

		Collection<Transaction> transactions = new ArrayList<Transaction>();

		try {
			for (File file : files) {
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
				transactions.addAll(OFX4JFacade.getTransactions(in));
			}
		} catch (FileNotFoundException e) {
			// TODO: Log this
			e.printStackTrace();
		}

		return transactions;
	}
}
