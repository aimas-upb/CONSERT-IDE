package org.aimas.consert.ide.editor;

import java.io.IOException;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FormView extends FormPage {
	private MultiPageEditor editor;

	public FormView(MultiPageEditor editor) {
		super(editor, "first", "FormView");
		this.editor = editor;
	}

	private String getContent() {
		IDocument doc = editor.getTextEditor().getDocumentProvider()
				.getDocument(editor.getTextEditor().getEditorInput());
		String content = doc.get();
		System.out.println(content);
		return content;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		System.out.println("hai cu mata!!!");
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		form.setText(editor.getEditorInput().getName());
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		layout.numColumns = 2;

		String content = getContent();
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (!content.isEmpty()) {
				JsonNode node = mapper.readTree(content);
				System.out.println(node.toString());
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Label nameLabel = new Label(form.getBody(), SWT.NONE);
		nameLabel.setText(" Name: ");
		Text nameText = new Text(form.getBody(), SWT.BORDER | SWT.SINGLE);
		nameText.setText("");

		Label commentLabel = new Label(form.getBody(), SWT.NONE);
		commentLabel.setText(" Comment: ");
		Text commentText = new Text(form.getBody(), SWT.BORDER | SWT.SINGLE);
		commentText.setText("");

		form.reflow(true);
	}
}
