package org.aimas.consert.ide.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.ui.wizards.datatransfer.WizardExternalProjectImportPage;

public class ConsertProjectImportWizard extends Wizard implements IImportWizard {
	private IStructuredSelection selection;
	private IWorkbench workbench;
	private WizardExternalProjectImportPage _pageOne;

	@Override
	public void addPages() {
		_pageOne = new WizardExternalProjectImportPage();
		_pageOne.setDescription("this allows you to import an exisiting consert project");
		_pageOne.setTitle("import consert project");
		addPage(_pageOne);
	}

	public ConsertProjectImportWizard() {
		super();
		setWindowTitle(" Consert Project Import Wizard ");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {

		IProject project = _pageOne.getProjectHandle().getProject();
		IOverwriteQuery overwriteQuery = new IOverwriteQuery() {
			public String queryOverwrite(String file) {
				return ALL;
			}
		};

		ImportOperation importOperation = new ImportOperation(project.getFullPath(),
				new File(project.getFullPath().toString()), FileSystemStructureProvider.INSTANCE, overwriteQuery);
		importOperation.setCreateContainerStructure(false);
		try {
			importOperation.run(new NullProgressMonitor());
		} catch (InvocationTargetException | InterruptedException | NullPointerException e) {
			// TODO Auto-generated catch block
			System.err.println("throwed a null pointer exception, but hey, at least it works!");
		}

		return true;
	}

}
