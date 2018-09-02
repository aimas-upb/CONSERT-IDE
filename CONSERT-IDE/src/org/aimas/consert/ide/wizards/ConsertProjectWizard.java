package org.aimas.consert.ide.wizards;

import java.io.IOException;
import java.net.URI;

import org.aimas.consert.ide.model.WorkspaceModel;
import org.aimas.consert.ide.projects.CustomProjectSupport;
import org.eclipse.core.resources.IProject;
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

	public ConsertProjectWizard() {
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
	}

	@Override
	public boolean performFinish() {
		String projectName = _pageOne.getProjectName();
		URI location = _pageOne.getLocationURI();

		if (location == null) {
			System.err.println("location for newly created Consert Project is null");
			return false;
		}

		try {
			IProject iProject = CustomProjectSupport.createProject(projectName, location);
			WorkspaceModel.getInstance().addProjectModel(projectName, iProject);
		} catch (IOException | CoreException e) {
			e.printStackTrace();
			return false;
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
