package org.aimas.consert.ide.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EditorInputWrapper implements IFileEditorInput {
	private final String name;

	class StringStorage implements IStorage {
		  @Override
		  public InputStream getContents() throws CoreException {
			ContextEntityModel cem = ProjectModel.getInstance().getEntityByName(name);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode entity = mapper.valueToTree(cem);
			return new ByteArrayInputStream(entity.toString().getBytes(StandardCharsets.UTF_8));
		  }

		@Override
		public <T> T getAdapter(Class<T> adapter) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IPath getFullPath() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isReadOnly() {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	public EditorInputWrapper(String name) {
		this.name = name;
	}

	@Override
	public IStorage getStorage() throws CoreException {
		return new StringStorage();
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		return "Displays a task";
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFile getFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EditorInputWrapper other = (EditorInputWrapper) obj;
		if (name != other.name)
			return false;
		return true;
	}
}
