package br.org.balancete.business.reader;

import java.io.File;
import java.util.Collection;

public interface FileReader<T> {

	public Collection<T> read(Collection<File> files);

	public Collection<T> read(File file);
}
