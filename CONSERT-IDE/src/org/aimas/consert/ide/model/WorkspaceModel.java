package org.aimas.consert.ide.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.aimas.consert.ide.views.TreeObject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkspaceModel {
	private static WorkspaceModel instance;
	private HashMap<String, ProjectModel> projects = new HashMap<String, ProjectModel>();

	private WorkspaceModel() {
		projects = new HashMap<String, ProjectModel>();
	}

	public static WorkspaceModel getInstance() {
		if (instance == null) {
			instance = new WorkspaceModel();
		}
		return instance;
	}

	public HashMap<String, ProjectModel> getProjectModels() {
		return this.projects;
	}

	public ProjectModel getProjectModel(String projectName) {
		return this.projects.get(projectName);
	}

	/**
	 * Metoda pentru initializare workspace
	 */
	public void initializeWorkspace() {
		projects = new HashMap<String, ProjectModel>();
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject[] projectsInWorkspace = workspace.getRoot().getProjects();

			for (int i = 0; i < projectsInWorkspace.length; i++) {/*
																	 * iterate
																	 * through
																	 * all
																	 * projects
																	 * in
																	 * Workspace
																	 */
				/*
				 * if the project is open and has the nature
				 * "consertperspective"
				 */
				if (projectsInWorkspace[i].isOpen()
						&& projectsInWorkspace[i].hasNature("consertperspective.projectNature")) {
					IProjectDescription description = projectsInWorkspace[i].getDescription();
					String projectName = description.getName();
					ProjectModel project = new ProjectModel(projectName);
					projects.put(projectName, project);
					/* Update on the current ProjectModel */
					IResource[] folderResources = projectsInWorkspace[i].members();
					this.updateProjectModels(projectName, folderResources);
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println(projects.toString());

	}

	/*
	 * Method for updating project model for the project with the name received as parameter
	 */
	public void updateProjectModels(String projectName, IResource[] folderResources) {
		for (int j = 0; j < folderResources.length; j++) {
			try {
				if (folderResources[j] instanceof IFolder) {
					IFolder resource = (IFolder) folderResources[j];
					if (resource.getName().equalsIgnoreCase("origin")) {
						IResource[] fileResources = resource.members();
						for (int k = 0; k < fileResources.length; k++) {
							if (fileResources[k] instanceof IFile && fileResources[k].getName().equals("consert.txt")) {
								TextFileDocumentProvider provider = new TextFileDocumentProvider();
								IDocument document = provider.getDocument((IFile) fileResources[k]);
								this.populateProjectModel(projects.get(projectName), document,
										(IFile) fileResources[k]);
							}
						}

					}
				}
			} catch (Exception e) {
				System.out.println("Excepetion at updating ProjectModel");
			}
		}

	}

	/*
	 * This method iterates through all projects in Workspace, adds new projects
	 * if it is the case, and refreshes the model for each of the projects in
	 * WorkspaceModel.
	 */
	public void refreshWorkspace() {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject[] projectsInWorkspace = workspace.getRoot().getProjects();

			for (int i = 0; i < projectsInWorkspace.length; i++) { 
				/*
				 * if the project is open and has the nature
				 * "consertperspective"
				 */
				if (projectsInWorkspace[i].isOpen()
						&& projectsInWorkspace[i].hasNature("consertperspective.projectNature")) {
					IProjectDescription description = projectsInWorkspace[i].getDescription();
					String projectName = description.getName();
					/*
					 * if the current project is not already in WorkspaceModel,
					 * we add the project
					 */
					if (!projects.containsKey(projectName)) {
						ProjectModel project = new ProjectModel(projectName);
						projects.put(projectName, project);
					}
					/* Update on the current ProjectModel */
					IResource[] folderResources = projectsInWorkspace[i].members();
					this.updateProjectModels(projectName, folderResources);
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	private void populateProjectModel(ProjectModel projectModel, IDocument document, IFile file)
			throws JsonProcessingException, IOException, CoreException {

		/* get string content from file */
		InputStream is = file.getContents();
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		String content = result.toString("UTF-8");

		if (content.isEmpty()) {
			System.err.println("File is completely empty!");
			return;
		}

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(content);

		System.out.println("Form View parsed: " + rootNode.toString());

		projectModel.setRootNode(rootNode);
		/* set path to file consert.txt in ProjectModel */

		System.out.println(file.getName());
		projectModel.setPath(file.getLocation());

		/*
		 * This code populates the view and the model with the found ENTITIES
		 * from the rootNode
		 */
		String nodeName = "ContextEntities";
		projectModel.getEntities().clear();
		if (rootNode.has(nodeName)) {
			JsonNode entities = (JsonNode) rootNode.get(nodeName);
			if (entities.isArray() && entities.size() > 0) {
				for (JsonNode entity : entities) {
					try {
						/* Populate model with entities */
						ContextEntityModel cem = mapper.treeToValue(entity, ContextEntityModel.class);
						projectModel.addEntity(cem);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.err.println("ContextEntities JsonNode has no entities");
			}
		} else {
			System.err.println("File does not have a ContextEntities JsonNode");
		}

		/*
		 * This code populates the view and the model with the found ASSERTIONS
		 * from the rootNode
		 */
		nodeName = "ContextAssertions";
		projectModel.getAssertions().clear();
		if (rootNode.has(nodeName)) {
			JsonNode assertions = (JsonNode) rootNode.get(nodeName);
			if (assertions.isArray() && assertions.size() > 0) {
				for (JsonNode assertion : assertions) {
					try {
						/* Populate model with assertions */
						ContextAssertionModel cam = mapper.treeToValue(assertion, ContextAssertionModel.class);
						projectModel.addAssertion(cam);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.err.println("ContextAssertions JsonNode has no assertions");
			}
		} else {
			System.err.println("File does not have a ContextAssertions JsonNode");
		}
	}

	/**
	 * 
	 * @param selection
	 * @return
	 */
	public String extractSelection(IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		Object selected;
		if (element instanceof IResource){
			IProject project = ((IResource) element).getProject();
			return project.getName();
		}
		
		if (element instanceof TreeObject){
			TreeObject treeObject = (TreeObject) element;
			return ((TreeObject) element).getProjectName();
			
		}
		
		if (element instanceof IAdaptable){
			IAdaptable adaptable = (IAdaptable) element;
			Object adapter = adaptable.getAdapter(IResource.class);
			IProject project = ((IResource) element).getProject();
			return project.getName();
		}
	
			return null;	
		
	}

	/**
	 * 
	 * @param selection
	 * @return projectName
	 * Method used in ContextModel element creation wizards
	 */
	public String getCurrentActiveProject(IStructuredSelection selection) {
		return this.extractSelection(selection);
	}
	
	/**
	 * 
	 * @param file
	 * @return ProjectModel 
	 * Method used when opening a file from ProjectExplorer to determine the ProjectModel 
	 * corresponding to the opened file 
	 */
	public ProjectModel getCurrentActiveProject(IFile file) {
		if(file != null){
			String projectName = file.getProject().getName();
			System.out.println(projectName);
			return WorkspaceModel.getInstance().getProjectModel(projectName);
		}
		return null;
	}

}
