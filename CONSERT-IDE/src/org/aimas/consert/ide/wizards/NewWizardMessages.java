package org.aimas.consert.ide.wizards;

import org.eclipse.osgi.util.NLS;

public class NewWizardMessages extends NLS {
	/* BUNDLE_NAME is the path to messages.properties */
	private static final String BUNDLE_NAME = "org.aimas.consert.ide.wizards.messages";

	public static String NewConsertProjectWizard;
	public static String NewConsertProjectDescription;
	public static String NewConsertProjectTitle;

	public static String ImportConsertProjectDescription;
	public static String ImportConsertProjectTitle;

	public static String NewContextEntityWizard;
	public static String NewContextEntityDescription;
	public static String NewContextEntityTitle;

	public static String NewContextAssertionWizard;
	public static String NewContextAssertionDescription;
	public static String NewContextAssertionTitle;
	
	public static String NewEntityDescriptionWizard;
	public static String NewEntityDescriptionDescription;
	public static String NewEntityDescriptionTitle;

	public static String NewContextAnnotationWizard;
	public static String NewContextAnnotationDescription;
	public static String NewContextAnnotationTitle;

	static {
		/* initialize resource bundle */
		NLS.initializeMessages(BUNDLE_NAME, NewWizardMessages.class);
	}
}
