package br.org.balancete.facade;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.webcohesion.ofx4j.domain.data.MessageSetType;
import com.webcohesion.ofx4j.domain.data.ResponseEnvelope;
import com.webcohesion.ofx4j.domain.data.ResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import com.webcohesion.ofx4j.domain.data.banking.BankingResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.common.Transaction;
import com.webcohesion.ofx4j.io.AggregateUnmarshaller;
import com.webcohesion.ofx4j.io.OFXParseException;

import br.org.balancete.domain.TransactionEn;
import br.org.balancete.util.DateUtil;
import br.org.balancete.util.NumberUtil;

/**
 * @author harlan bruno
 * @since 19/03/2018
 * @version 1.0
 *
 */
public class OFX4JFacade {

	public static Collection<br.org.balancete.domain.Transaction> getTransactions(BufferedReader bf) {

		Collection<br.org.balancete.domain.Transaction> transactions = new ArrayList<>();

		try {

			AggregateUnmarshaller<ResponseEnvelope> a = new AggregateUnmarshaller<ResponseEnvelope>(
					ResponseEnvelope.class);
			ResponseEnvelope re = (ResponseEnvelope) a.unmarshal(bf);
			ResponseMessageSet message = re.getMessageSet(MessageSetType.banking);

			if (message != null) {
				List<BankStatementResponseTransaction> bank = ((BankingResponseMessageSet) message)
						.getStatementResponses();
				for (BankStatementResponseTransaction b : bank) {
					List<Transaction> list = b.getMessage().getTransactionList().getTransactions();
					for (Transaction OFXTransaction : list) {
						br.org.balancete.domain.Transaction transaction = new br.org.balancete.domain.Transaction();
						transaction.setType(TransactionEn.get(OFXTransaction.getTransactionType().name()));
						// transacao.setId(Long.parseLong(OFXTransaction.getId()));
						transaction.setDate(DateUtil.convertToLocalDateTime(OFXTransaction.getDatePosted()));
						transaction.setAmount(NumberUtil.convertToBigDecimal(Math.abs(OFXTransaction.getAmount())));
						transaction.setDescription(OFXTransaction.getMemo());

						transactions.add(transaction);
					}
				}
			}
		} catch (IOException | OFXParseException e) {
			// TODO: Log this
			e.printStackTrace();
		}

		return transactions;
	}
}
