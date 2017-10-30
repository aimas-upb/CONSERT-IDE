package org.aimas.consert.ide.model;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.aimas.consert.ide.views.TreeObject;
import org.aimas.consert.ide.views.TreeViewerNew;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class WorkspaceModel {
	public static final String CONTEXT_ENTITY_NODE_NAME = "ContextEntities";
	public static final String CONTEXT_ASSERTION_NODE_NAME = "ContextAssertions";
	
	private static WorkspaceModel instance;
	private Map<String, ProjectModel> projects = new HashMap<>();

	private WorkspaceModel() {
		reactOnAddDelete();
		projects = new HashMap<String, ProjectModel>();
	}

	public static WorkspaceModel getInstance() {
		if (instance == null) {
			instance = new WorkspaceModel();
		}
		return instance;
	}

	public Map<String, ProjectModel> getProjectModels() {
		return projects;
	}

	public ProjectModel getProjectModel(String projectName) {
		return projects.get(projectName);
	}

	public ProjectModel removeProjectModel(String projectName) {
		return projects.remove(projectName);
	}

	public boolean removeEntityfromProjectModel(String projectName, ContextEntityModel cem) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		ProjectModel projectModel = getProjectModel(projectName);
		IProject projectInWorkspace = workspace.getRoot().getProject(projectName);
		String nodeName = CONTEXT_ENTITY_NODE_NAME;
		auxDeleteFromFile(projectInWorkspace, projectModel, cem.getName(), nodeName);

		return projects.get(projectName).removeEntity(cem);
	}

	public boolean removeAssertionfromProjectModel(String projectName, ContextAssertionModel cam) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		ProjectModel projectModel = getProjectModel(projectName);
		IProject projectInWorkspace = workspace.getRoot().getProject(projectName);
		String nodeName = CONTEXT_ASSERTION_NODE_NAME;
		auxDeleteFromFile(projectInWorkspace, projectModel, cam.getName(), nodeName);
		
		return projects.get(projectName).removeAssertions(cam);
	}
	
	public void auxDeleteFromFile(IProject projectInWorkspace, ProjectModel projectModel, String modelName, String nodeName){
		IResource[] folderResources;
		try {
			folderResources = projectInWorkspace.members();
			for (int j = 0; j < folderResources.length; j++) {
				try {
					if (folderResources[j] instanceof IFolder) {
						IFolder resource = (IFolder) folderResources[j];
						if (resource.getName().equalsIgnoreCase("origin")) {
							IResource[] fileResources = resource.members();
							for (int k = 0; k < fileResources.length; k++) {
								if (fileResources[k] instanceof IFile
										&& fileResources[k].getName().equals("consert.txt")) {
									TextFileDocumentProvider provider = new TextFileDocumentProvider();
									IDocument document = provider.getDocument((IFile) fileResources[k]);

									deleteFromFile(projectModel, document, (IFile) fileResources[k], modelName, nodeName);
								}
							}

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * 
	 * @param projectName
	 * @param projectInWorkspace
	 * @throws CoreException
	 *             Method used to add a new Project to the WorkspaceModel
	 */
	public void addProjectModel(String projectName, IProject projectInWorkspace) throws CoreException {
		ProjectModel project = new ProjectModel(projectName);
		projects.put(projectName, project);
		/* Update on the current ProjectModel */
		IResource[] folderResources = projectInWorkspace.members();
		updateProjectModels(projectName, folderResources);
	}

	/**
	 * 
	 * @param projectName
	 * @return Not used method Not working properly
	 */
	public boolean addProjectModel(String projectName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IProject projectInWorkspace = workspace.getRoot().getProject(projectName);

		/*
		 * if the project is open and has the nature "consertperspective"
		 */
		try {
			if (projectInWorkspace.isOpen() && projectInWorkspace.hasNature("consertperspective.projectNature")) {
				addProjectModel(projectName, projectInWorkspace);
				return true;
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Initialize Workspace Model
	 */
	public void initializeWorkspace() {
		projects = new HashMap<String, ProjectModel>();
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject[] projectsInWorkspace = workspace.getRoot().getProjects();

			/* iterate through all projects in Workspace */
			for (int i = 0; i < projectsInWorkspace.length; i++) {
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
					IProject projectInWorkspace = projectsInWorkspace[i];
					addProjectModel(projectName, projectInWorkspace);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(projects.toString());
	}

	/*
	 * Method for updating project model for the project with the name received
	 * as parameter
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
								populateProjectModel(projects.get(projectName), document, (IFile) fileResources[k]);
							}
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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
					updateProjectModels(projectName, folderResources);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateProjectModel(ProjectModel projectModel, IDocument document, IFile file)
			throws JsonProcessingException, IOException, CoreException {

		/* get string content from file, first refreshed on disk */
		file.refreshLocal(IFile.DEPTH_INFINITE, null);
		InputStream is = file.getContents();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(is);

		projectModel.setRootNode(rootNode);
		/* set path to file consert.txt in ProjectModel */
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
	 * @param projectModel
	 * @param document
	 * @param file
	 * @param modelName
	 * @param nodeName
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws CoreException
	 * Method used to delete an Entity or assertion from the file in which all elements defined by the user are stored
	 */
	public void deleteFromFile(ProjectModel projectModel, IDocument document, IFile file, String modelName, String nodeName)
			throws JsonProcessingException, IOException, CoreException {
		/* get string content from file, first refreshed on disk */
		file.refreshLocal(IFile.DEPTH_INFINITE, null);
		InputStream is = file.getContents();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(is);


		if (rootNode.has(nodeName)) {
			JsonNode entities = (JsonNode) rootNode.get(nodeName);

			Iterator<JsonNode> itr = entities.iterator();

			while (itr.hasNext()) {
				JsonNode entity = itr.next();
				System.out.println(entity.get("name"));
				String entityName = entity.get("name").textValue();
				System.out.println(entityName);
				if (entityName.equals(modelName)) {
					/*
					 * Remove the found object
					 */
					itr.remove();
					break;
				}
			};
			
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));
			
			/*
			 * Write to file
			 */
			InputStream isf = new ByteArrayInputStream(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode).getBytes());
			file.setContents(isf, IResource.FORCE, null);

		}
	}

	/**
	 * 
	 * @param selection
	 * @return projectName Method used in ContextModel element creation wizards
	 */
	public String getCurrentActiveProject(IStructuredSelection selection) {
		Object element = selection.getFirstElement();

		if (element instanceof TreeObject) {
			return ((TreeObject<?>) element).getProjectName();
		}

		/* Every IResource is an IAdaptable */
		if (element instanceof IAdaptable) {
			return ((IProject) ((IResource) element).getProject()).getName();
		}

		return "";
	}

	/**
	 * 
	 * @param file
	 * @return ProjectModel Method used when opening a file from ProjectExplorer
	 *         to determine the ProjectModel corresponding to the opened file
	 */
	public ProjectModel getCurrentActiveProject(IFile file) {
		if (file != null) {
			String projectName = file.getProject().getName();
			return WorkspaceModel.getInstance().getProjectModel(projectName);
		}
		return null;
	}

	/**
	 * Method used for reflecting the changes in Package Explorer (ADD, DELETE) 
	 * in the Tree View
	 */
	public void reactOnAddDelete() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResourceChangeListener rcl = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				IResource res = event.getResource();

				switch (event.getType()) {
				// case IResourceChangeEvent.PRE_CLOSE:
				// System.out.print("Project ");
				// System.out.print(res.getFullPath());
				// System.out.println(" is about to close.");
				// break;
				// case IResourceChangeEvent.PRE_DELETE:
				// System.out.print("Project ");
				// System.out.print(res.getFullPath());
				// System.out.println(" is about to be deleted.");
				// break;
				case IResourceChangeEvent.POST_CHANGE:
					System.out.println("Resources have changed.");
					try {
						event.getDelta().accept(new DeltaPrinter());
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				// case IResourceChangeEvent.PRE_BUILD:
				// System.out.println("Build about to run.");
				// try {
				// event.getDelta().accept(new DeltaPrinter());
				// } catch (CoreException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// break;
				// case IResourceChangeEvent.POST_BUILD:
				// System.out.println("Build complete.");
				// try {
				// event.getDelta().accept(new DeltaPrinter());
				// } catch (CoreException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// break;

				}
			}
		};

		workspace.addResourceChangeListener(rcl);
	}

	class DeltaPrinter implements IResourceDeltaVisitor {

		public boolean visit(IResourceDelta delta) {
			IResource res = delta.getResource();
			TreeViewerNew tv = TreeViewerNew.getInstance();
			if (res instanceof IProject) { // If the changed resource is a
											// Project
				String projectName = ((IProject) res).getName();
				switch (delta.getKind()) {
				case IResourceDelta.ADDED:
					System.out.print("Resource ");
					System.out.print(res.getFullPath());
					System.out.println(" was added.");

					// Add ProjectModel to WorkspaceModel and Tree View

					tv.addProjectToTreeViewer(projectName);

					break;
				case IResourceDelta.REMOVED:
					System.out.print("Resource ");
					System.out.print(res.getFullPath());
					System.out.println(" was removed.");

					// Remove ProjectModel from WorkspaceModel and Tree View
					removeProjectModel(projectName);
					tv.removeProjectfromTreeView(projectName);

					break;
				// case IResourceDelta.CHANGED:
				// System.out.print("Resource ");
				// System.out.print(res.getFullPath());
				// System.out.println(" has changed.");
				// break;
				}

			}

			return true; // visit the children
		}
	}

}
