package org.aimas.consert.ide.wizards;

import java.io.IOException;
import java.net.URI;

import org.aimas.consert.ide.projects.CustomProjectSupport;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class ConsertProjectWizard extends Wizard implements INewWizard, IExecutableExtension {
	private IConfigurationElement _configurationElement;
	private WizardNewProjectCreationPage _pageOne;
	private IStructuredSelection selection;
	private IWorkbench workbench;

	public ConsertProjectWizard() {
		super();
		setWindowTitle(NewWizardMessages.NewConsertProjectTitle);
	}

	@Override
	public void addPages() {
		super.addPages();
		_pageOne = new WizardNewProjectCreationPage(NewWizardMessages.NewConsertProjectWizard);
		_pageOne.setDescription(NewWizardMessages.NewConsertProjectDescription);
		_pageOne.setTitle(NewWizardMessages.NewConsertProjectTitle);
		addPage(_pageOne);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		String name = _pageOne.getProjectName();
		URI location = null;
		if (!_pageOne.useDefaults()) {
			location = _pageOne.getLocationURI();
			System.err.println("location: " + location.toString());
		}
		// else location == null

		try {
			CustomProjectSupport.createProject(name, location);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BasicNewProjectResourceWizard.updatePerspective(_configurationElement);

		return true;
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		_configurationElement = config;
	}
}
