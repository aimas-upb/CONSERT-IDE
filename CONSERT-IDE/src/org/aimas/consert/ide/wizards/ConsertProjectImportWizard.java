package org.aimas.consert.ide.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IPath;
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

	public ConsertProjectImportWizard() {
		super();
		setWindowTitle(NewWizardMessages.ImportConsertProjectTitle);
	}

	@Override
	public void addPages() {
		_pageOne = new WizardExternalProjectImportPage();
		_pageOne.setDescription(NewWizardMessages.ImportConsertProjectDescription);
		_pageOne.setTitle(NewWizardMessages.ImportConsertProjectTitle);
		addPage(_pageOne);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		IPath projectPath = _pageOne.getLocationPath();
		File directory = new File(projectPath.toString());
		for (File file : directory.listFiles()) {
			System.out.println(file.getName());
		}
		IOverwriteQuery overwriteQuery = new IOverwriteQuery() {
			public String queryOverwrite(String file) {
				return ALL;
			}
		};

		ImportOperation importOperation = new ImportOperation(_pageOne.getProjectHandle().getFullPath(),
				new File(projectPath.toString()), FileSystemStructureProvider.INSTANCE, overwriteQuery);
		importOperation.setCreateContainerStructure(false);
		try {
			importOperation.run(new NullProgressMonitor());
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
}
