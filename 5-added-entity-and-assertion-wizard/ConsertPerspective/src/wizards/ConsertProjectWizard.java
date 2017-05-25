package wizards;

import java.io.IOException;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import projects.CustomProjectSupport;

public class ConsertProjectWizard extends Wizard implements INewWizard, IExecutableExtension {
	private IConfigurationElement _configurationElement;
	private WizardNewProjectCreationPage _pageOne;
	private IStructuredSelection selection;
	private IWorkbench workbench;
	private static final String WIZARD_NAME = "New CONSERT Project"; //$NON-NLS-1$

	@Override
	public void addPages() {
		super.addPages();

		_pageOne = new WizardNewProjectCreationPage(
				wizards.NewWizardMessages.ConsertProjectWizard_ConsertProjectNewWizard_Custom_Project);
		_pageOne.setDescription(
				wizards.NewWizardMessages.ConsertProjectWizard_ConsertProjectWizard_Create_something_custom);
		_pageOne.setTitle(wizards.NewWizardMessages.ConsertProjectWizard_ConsertProjectNewWizard_Custom_Project);
		addPage(_pageOne);
	}

	public ConsertProjectWizard() {
		// TODO Auto-generated constructor stub
		super();
		setWindowTitle(wizards.NewWizardMessages.ConsertProjectWizard_2);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		this.workbench = workbench;
	
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		String name = _pageOne.getProjectName();
		URI location = null;
		if (!_pageOne.useDefaults()) {
			location = _pageOne.getLocationURI();
			System.err.println("location: " + location.toString()); //$NON-NLS-1$
		} // else location == null

		try {
			CustomProjectSupport.createProject(name, location);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Add this
		BasicNewProjectResourceWizard.updatePerspective(_configurationElement);

		return true;

	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		// TODO Auto-generated method stub
		_configurationElement = config;

	}

}
