package org.aimas.consert.ide.editor;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FormView extends FormPage {
	private MultiPageEditor editor;
	private ScrolledForm form;
	private boolean isDirty;

	public FormView(MultiPageEditor editor) {
		super(editor, "first", "FormView");
		this.editor = editor;
		isDirty = false;
	}

	private String getContent() {
		IDocument doc = editor.getTextEditor().getDocumentProvider()
				.getDocument(editor.getTextEditor().getEditorInput());
		String content = doc.get();
		System.out.println(content);
		return content;
	}

	public void createLabelAndText(String labelName, String textName) {
		Label nameLabel = new Label(form.getBody(), SWT.NONE);
		nameLabel.setText(labelName);
		Text nameText = new Text(form.getBody(), SWT.BORDER | SWT.SINGLE);
		nameText.setText(textName);
		nameText.setLayoutData(new GridData(100, 10));
		nameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (nameText.getText().equals(textName))
					isDirty = false;
				else {
					isDirty = true;
					firePropertyChange(IEditorPart.PROP_DIRTY);
					editor.editorDirtyStateChanged();
				}
			}
		});

		nameText.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// isDirty = true;
				// firePropertyChange(IEditorPart.PROP_DIRTY);
				// } else
				// isDirty = false;
			}

		});
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		System.out.println("needs doSave functionality on formView and reload editor!");
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
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (!content.isEmpty()) {
				JsonNode rootNode = mapper.readTree(content);
				System.out.println("Form View parsed: " + rootNode.toString());
				// if (rootNode.has("ContextAssertions")) {
				// ArrayNode assertions = (ArrayNode)
				// rootNode.get("ContextAssertions");
				// for (JsonNode assertion : assertions) {
				// String name = assertion.get("name").asText();
				if (rootNode.has("ContextEntities")) {
					JsonNode entities = (JsonNode) rootNode.get("ContextEntities");
					if (entities.isArray()) {
						for (JsonNode entity : entities) {
							String name = entity.get("name").asText();
							String comment = entity.get("comment").asText();
							Label nameLabel = new Label(form.getBody(), SWT.NONE);
							nameLabel.setText(" ContextEntitity: ");
							new Label(form.getBody(), SWT.NONE);
							createLabelAndText(" Name: ", name);
							createLabelAndText(" Comment: ", comment);
						}
					}
				}
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		form.reflow(true);
	}

}
