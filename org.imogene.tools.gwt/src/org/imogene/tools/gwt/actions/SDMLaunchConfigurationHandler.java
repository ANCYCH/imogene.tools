package org.imogene.tools.gwt.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.imogene.tools.gwt.ui.SDMLaunchConfigurationDialog;

import com.google.gdt.eclipse.core.ActiveProjectFinder;
import com.google.gwt.eclipse.core.runtime.GWTRuntime;

/**
 * This task creates the launch configuration for the SuperDevMode GWT debug mode for the generated project
 * 
 * @author MEDES-IMPS
 * 
 */
public class SDMLaunchConfigurationHandler extends AbstractHandler {

	private static final String EXTRA_ARGS = "-noprecompile";
	private static final String MAIN_CLASS = "com.google.gwt.dev.codeserver.CodeServer";

	@Override
	public Object execute(ExecutionEvent event) {
		try {
			buildLaunchConfiguration();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void buildLaunchConfiguration() throws CoreException {
		IProject initialProject = ActiveProjectFinder.getInstance().getProject();

		SDMLaunchConfigurationDialog dialog = new SDMLaunchConfigurationDialog(Display.getDefault().getActiveShell(),
				initialProject);
		dialog.setExtraArgs(EXTRA_ARGS);
		if (dialog.open() != Window.OK) {
			return;
		}

		IProject project = dialog.getProject();

		String projectName = project.getName();
		String launcher = dialog.getLauncher();
		String module = dialog.getModule();
		String extraArgs = dialog.getExtraArgs();
		String vmArgs = dialog.getVmArgs();

		ILaunchManager mgr = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = mgr
				.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
		ILaunchConfigurationWorkingCopy wc = type.newInstance(null, launcher);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, MAIN_CLASS);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, buildArgs(module, extraArgs));
		if (vmArgs != null && vmArgs.length() > 0) {
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs);
		}

		// Add the gwt-codeserver.jar to the classpath of the launch configuration
		IJavaProject javaProject = JavaCore.create(project);
		GWTRuntime runtime = GWTRuntime.findSdkFor(javaProject);
		IPath codeServerPath = runtime.getInstallationPath().append("gwt-codeserver.jar");
		IRuntimeClasspathEntry codeServerEnry = JavaRuntime.newArchiveRuntimeClasspathEntry(codeServerPath);
		codeServerEnry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

		// Add the default system JRE container classpath entry
		IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
		IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(systemLibsPath,
				IRuntimeClasspathEntry.STANDARD_CLASSES);

		// Add the project to the classpath entries list
		IRuntimeClasspathEntry projectEntry = JavaRuntime.newProjectRuntimeClasspathEntry(javaProject);
		projectEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

		List<String> classpath = new ArrayList<String>();
		classpath.add(systemLibsEntry.getMemento());
		classpath.add(projectEntry.getMemento());
		classpath.add(codeServerEnry.getMemento());

		// Add the source folders to the classpath entries list
		IClasspathEntry[] entries = javaProject.getRawClasspath();
		for (IClasspathEntry entry : entries) {
			if (entry.getContentKind() == IPackageFragmentRoot.K_SOURCE
					&& entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				IRuntimeClasspathEntry javaEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(entry.getPath());
				javaEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
				classpath.add(javaEntry.getMemento());
			}
		}

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);

		wc.doSave();
	}

	private static String buildArgs(String module, String extraArgs) {
		if (extraArgs == null || extraArgs.length() == 0) {
			return module;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(extraArgs);
		builder.append(' ');
		builder.append(module);
		return builder.toString();
	}

}
