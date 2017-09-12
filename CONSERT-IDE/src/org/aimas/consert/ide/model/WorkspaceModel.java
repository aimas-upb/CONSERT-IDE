package org.aimas.consert.ide.model;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;

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
		this.projects = new HashMap<String, ProjectModel>();
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject[] projects = workspace.getRoot().getProjects();

			for (int i = 0; i < projects.length; i++) {
				if (projects[i].isOpen() && projects[i].hasNature("consertperspective.projectNature")) {
					IProjectDescription description = projects[i].getDescription();
					String projectName = description.getName();
					System.out.println("am gasit " + description.getName());
					ProjectModel project = new ProjectModel(projectName);
					this.projects.put(projectName, project);
				}

			}

		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println(projects.toString());

	}

	/**
	 * Metoda parcurge toate proiectele din workspace, adauga noile proiecte aparute, daca este cazul,
	 *  si reincarca modelul pentru fiecare din proiecte
	 */
	public void refreshWorkspace() {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject[] projects = workspace.getRoot().getProjects();

			for (int i = 0; i < projects.length; i++) { //Parcurgem toate proiectele din Workspace
				//Daca proiectul este deschis si are natura consertperspective
				if (projects[i].isOpen() && projects[i].hasNature("consertperspective.projectNature")) {
					IProjectDescription description = projects[i].getDescription();
					System.out.println("am gasit refresh" + description.getName());
					String projectName = description.getName();
					//Daca proiectul cu curent nu se afla deja in WorkspaceModel, adaugam proiectul
					if(!this.projects.containsKey(projectName)){
						ProjectModel project = new ProjectModel(projectName);
						this.projects.put(projectName, project);
					}
					//Update pe model proiect curent
					
					 IResource[] folderResources = projects[i].members();
					 for (int j = 0; j < folderResources.length; j++) {
						 if (folderResources[j] instanceof IFolder) {
							 IFolder resource = (IFolder) folderResources[j];
							 System.out.println("INAINTE!!!!");
							 if (resource.getName().equalsIgnoreCase("origin")) {
								 IResource[] fileResources = resource.members();
								 for (int k = 0; k < fileResources.length; k++) {
									 if (fileResources[k] instanceof IFile && fileResources[k].getName().equals("consert.txt")) {
										TextFileDocumentProvider provider = new TextFileDocumentProvider();
										IDocument document = provider.getDocument((IFile)fileResources[k]);
										System.out.println("POPULARE MODEL!!!!");
										this.populateProjectModel(this.projects.get(projectName), document, (IFile)fileResources[k]);
									 }
								 }
						
							 }
						 }
					 }
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println(projects.toString());

	}

	private void populateProjectModel(ProjectModel projectModel, IDocument document, IFile file)
			throws JsonProcessingException, IOException, CoreException {
		
		InputStream is = file.getContents();
		
		String content = CharStreams.toString(new InputStreamReader(is, "UTF-8"));

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

}
