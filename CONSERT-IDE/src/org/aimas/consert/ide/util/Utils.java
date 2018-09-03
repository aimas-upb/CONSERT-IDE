package org.aimas.consert.ide.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.eclipse.swt.widgets.Combo;

public class Utils {

	public enum ModelType {
		ENTITY, ASSERTION, ANNOTATION, ENTITY_DESCRIPTION
	}

	private Utils() {
	}

	public static List<ContextEntityModel> getAllEntities() {
		Collection<ProjectModel> projects = WorkspaceModel.getInstance().getProjectModels().values();

		return projects.stream().map(project -> project.getEntities()).flatMap(List::stream)
				.collect(Collectors.toList());
	}

	public static String[] getAllEntitiesStringNames() {
		return getAllEntities().stream().map(entity -> entity.getName()).toArray(String[]::new);
	}

	public static String generateID(String projectName, ModelType modelType, String modelName) {
		return projectName + "_" + modelType.name() + "_" + modelName;
	}

	public static ContextEntityModel getEntityModelFromCombo(Combo entityCombo) {
		int index = entityCombo.getSelectionIndex();
		String subjectEntityName = entityCombo.getItem(index == -1 ? 0 : index);

		for (ContextEntityModel cem : getAllEntities()) {
			if (cem.getName().equals(subjectEntityName)) {
				return cem;
			}
		}

		// TODO: returning null is bad idea
		return null;
	}
}
