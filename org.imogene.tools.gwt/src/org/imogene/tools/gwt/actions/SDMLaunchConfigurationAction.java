package org.imogene.tools.gwt.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;

public class SDMLaunchConfigurationAction implements IActionDelegate {
	
	@Override
	public void run(IAction action) {
		new SDMLaunchConfigurationHandler().execute(null);
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
