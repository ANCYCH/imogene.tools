package org.imogene.tools.i18n.extractor;

import java.util.Properties;

import org.eclipse.core.resources.IFile;

public interface Extractor {

	public Properties extract(IFile selection);
}
