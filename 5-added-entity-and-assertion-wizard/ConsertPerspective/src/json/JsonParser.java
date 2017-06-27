package json;

import java.io.FileOutputStream;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

public class JsonParser {
	private static JsonParser instance;
	private ObjectMapper mapper;

	private JsonParser() {
		mapper = new ObjectMapper();
	}

	public static JsonParser getInstance() {
		if (instance == null) {
			instance = new JsonParser();
		}
		return instance;
	}

	public boolean appendToFile(String projectName, Object model) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IFolder folder = project.getFolder("origin");
		if (!project.exists()) {
			System.out.println("project does not exist");
			return false;
		}

		// Convert object to JSON string and save into file directly
		try {
			FileOutputStream out = new FileOutputStream(folder.getFile("consert.txt").getLocation().toFile(), true);
			// JsonGenerator g =
			// mapper.getJsonFactory().createJsonGenerator(out);
			// mapper.writeValue(folder.getFile("consert.txt").getLocation().toFile(),
			// model);
			mapper.writeValue(out, model);
			// Convert object to JSON string
			// String jsonInString = mapper.writeValueAsString(model);
			// System.out.println(jsonInString);
			System.out.println(model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}

}
