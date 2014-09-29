package org.imogene.tools.i18n.wdgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class EnhancedComposite extends Composite {

	public EnhancedComposite(final Composite parent, final int style) {
		super(parent, style);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (Control child : getChildren()) {
			child.setEnabled(enabled);
		}
	}
}