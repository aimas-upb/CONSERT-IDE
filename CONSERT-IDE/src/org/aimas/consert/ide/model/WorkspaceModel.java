package org.aimas.consert.ide.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

public class WorkspaceModel {
	private static WorkspaceModel instance;
	private ArrayList<ProjectModel> projects;
	
	private WorkspaceModel() {
		projects = new ArrayList<ProjectModel>();
	}

	public static WorkspaceModel getInstance() {
		if (instance == null) {
			instance = new WorkspaceModel();
		}
		return instance;
	}
	
	public ArrayList<ProjectModel> getProjectModels(){
		return this.projects;
	}
	
	public ProjectModel getProjectModel(String projectName){
		for (ProjectModel project : this.projects){
			if (project.getName().equals(projectName)){
				return project;
			}
		}
		return null;
	}

	public ArrayList<String> refreshWorkspace() {
		ArrayList<String> result = new ArrayList<String>();
		this.projects.clear();
		
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject[] projects = workspace.getRoot().getProjects();
		
			for (int i = 0; i < projects.length; i++) {
				if ( projects[i].isOpen() && projects[i].hasNature("consertperspective.projectNature")){
					IProjectDescription description = projects[i].getDescription();
					result.add(description.getName());
					ProjectModel project = new ProjectModel(description.getName());
					this.projects.add(project);
				}
//				IResource[] folderResources = projects[i].members();
//				for (int j = 0; j < folderResources.length; j++) {
//					if (folderResources[j] instanceof IFolder) {
//						IFolder resource = (IFolder) folderResources[j];
//						if (resource.getName().equalsIgnoreCase("Property Files")) {
//							IResource[] fileResources = resource.members();
//							for (int k = 0; k < fileResources.length; k++) {
//								if (fileResources[k] instanceof IFile
//										&& fileResources[k].getName().endsWith(".properties")) {
//
//								}
//							}
//						}
//					}
//				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}

		return result;
	}

}
