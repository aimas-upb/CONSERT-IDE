package org.aimas.consert.ide.wizards;

import org.eclipse.osgi.util.NLS;

public class NewWizardMessages extends NLS {
	private static final String BUNDLE_NAME = "wizards.messages"; //$NON-NLS-1$
	public static String ScratchEntity;
	public static String ScratchAssertion;
	public static String ConsertProjectWizard_2;
	public static String ConsertProjectWizard_ConsertProjectNewWizard_Custom_Project;
	public static String ConsertProjectWizard_ConsertProjectWizard_Create_something_custom;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, NewWizardMessages.class);
	}

	private NewWizardMessages() {
	}
}
