package org.aimas.consert.ide.editor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.aimas.consert.ide.model.AbstractContextModel;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.FileEditorInput;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FormView extends FormPage implements IResourceChangeListener {
	private MultiPageEditor editor;
	private ScrolledForm form;
	private boolean isDirty;
	private JsonNode rootNode;
	private ObjectMapper mapper;
	private Map<JsonNode, AbstractContextModel> map;

	public FormView(MultiPageEditor editor) {
		super(editor, "first", "FormView");
		this.editor = editor;
		isDirty = false;
		map = new HashMap<JsonNode, AbstractContextModel>();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	private String getContent() {
		IDocument doc = editor.getTextEditor().getDocumentProvider()
				.getDocument(editor.getTextEditor().getEditorInput());
		String content = doc.get();
		System.out.println(content);
		return content;
	}

	public void createLabelAndText(String labelName, String textName, JsonNode entity) {
		Label nameLabel = new Label(form.getBody(), SWT.NONE);
		nameLabel.setText(labelName);
		Text nameText = new Text(form.getBody(), SWT.BORDER | SWT.SINGLE);
		nameText.setText(textName);
		nameText.setLayoutData(new GridData(100, 10));
		nameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				isDirty = true;
				firePropertyChange(IEditorPart.PROP_DIRTY);
				editor.editorDirtyStateChanged();

				ContextEntityModel cem = (ContextEntityModel) map.get(entity);

				if (labelName.equals(" Name: ")) {
					cem.setName(nameText.getText());
				} else if (labelName.equals(" Comment: "))
					cem.setComment(nameText.getText());
				map.put(entity, cem);
			}
		});
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		IPath path = ((FileEditorInput) editor.getEditorInput()).getPath();
		try {
			((ObjectNode) rootNode).withArray("ContextEntities").removeAll();
			for (AbstractContextModel cem : map.values()) {
				System.out.println("[doSave] new map values: " + map.values());
				((ObjectNode) rootNode).withArray("ContextEntities").add(mapper.valueToTree((ContextEntityModel) cem));
			}
			mapper.writeValue(new File(path.toString()), rootNode);
		} catch (IOException e) {
			e.printStackTrace();
		}
		isDirty = false;
		firePropertyChange(PROP_DIRTY);
		editor.editorDirtyStateChanged();
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		form.setText(editor.getTextEditor().getEditorInput().getName());
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		layout.numColumns = 2;

		String content = getContent();
		if (content.isEmpty()) {
			System.err.println("File is empty!");
			return;
		}

		mapper = new ObjectMapper();
		try {
			rootNode = mapper.readTree(content);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Form View parsed: " + rootNode.toString());
		// if (rootNode.has("ContextAssertions")) {
		// ArrayNode assertions = (ArrayNode)
		// rootNode.get("ContextAssertions");
		// for (JsonNode assertion : assertions) {
		// String name = assertion.get("name").asText();
		String nodeName = "ContextEntities";
		if (rootNode.has(nodeName)) {
			JsonNode entities = (JsonNode) rootNode.get(nodeName);
			if (entities.isArray()) {
				for (JsonNode entity : entities) {
					try {
						map.put(entity, mapper.treeToValue(entity, ContextEntityModel.class));
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
					String name = entity.get("name").asText();
					String comment = entity.get("comment").asText();
					Label nameLabel = new Label(form.getBody(), SWT.NONE);
					nameLabel.setText(" ContextEntitity: ");
					new Label(form.getBody(), SWT.NONE);
					createLabelAndText(" Name: ", name, entity);
					createLabelAndText(" Comment: ", comment, entity);
				}
			}
		}
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Reload formView");
	}

}
