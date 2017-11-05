package org.aimas.consert.ide.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.aimas.consert.ide.model.WorkspaceModel;

public class Utils {
	private static Utils instance;

	private Utils() {
	}

	public static Utils getInstance() {
		if (instance == null) {
			instance = new Utils();
		}
		return instance;
	}

	public List<ContextEntityModel> getAllEntities() {
		List<ContextEntityModel> allEntities = new ArrayList<>();
		Collection<ProjectModel> projects = WorkspaceModel.getInstance().getProjectModels().values();
		projects.forEach(project -> allEntities.addAll(project.getEntities()));
		return allEntities;
	}

	public String[] getAllEntitiesStringNames(List<ContextEntityModel> allEntities) {
		List<String> entityNames = new ArrayList<>();
		allEntities.forEach(entity -> entityNames.add(entity.getName()));
		return entityNames.toArray(new String[entityNames.size()]);
	}
	
	/**
	 * 
	 * @param projectName
	 * @param modelType
	 * @param modelName
	 * @return
	 * Method used to generate a unique ID for a certain model
	 */
	public String generateID(String projectName, String modelType, String modelName){
		String ID = projectName + "_"+ modelType +  "_" + modelName;
		return ID;
	}
}
