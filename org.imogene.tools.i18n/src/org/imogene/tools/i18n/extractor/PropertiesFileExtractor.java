package org.imogene.tools.i18n.extractor;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class PropertiesFileExtractor implements Extractor {

	@Override
	public Properties extract(IFile selection) {
		Properties p = new Properties();
		try {
			p.load(selection.getContents());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return p;
	}

}
