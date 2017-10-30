package org.aimas.consert.ide.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.assertion.AssertionMultiPageEditor;
import org.aimas.consert.ide.editor.entity.EntityMultiPageEditor;
import org.aimas.consert.ide.model.ContextAssertionModel;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.aimas.consert.ide.views.TreeViewerNew.TreeParent;
import org.aimas.consert.ide.wizards.ContextAssertionWizard;
import org.aimas.consert.ide.wizards.ContextEntityWizard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class TreeViewerNew extends ViewPart implements Observer {
	public static final String ID = "org.aimas.consert.ide.views.TreeViewerNew";
	private static TreeViewerNew instance;
	private TreeViewer viewer;
	private TreeParent<?> invisibleRoot;

	class TreeParent<T> extends TreeObject<T> {
		private ArrayList<TreeObject<T>> children;

		public TreeParent(String name, String projectName) {
			super(name, projectName);
			children = new ArrayList<TreeObject<T>>();
		}

		public void addChild(TreeObject<T> child) {
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(TreeObject<T> child) {
			children.remove(child);
			child.setParent(null);
		}

		public TreeObject<T>[] getChildren() {
			return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
		}

		public boolean hasChildren() {
			return !children.isEmpty();
		}
	}

	/**
	 * 
	 * @param projectName
	 * @return
	 * On most operating systems, drawing to the screen is an operation that needs to be synchronized with other draw requests to prevent chaos. 
	 * A simple OS solution for this resource-contention problem is to allow drawing operations to occur only in a special thread. 
	 * Rather than drawing at will, an application sends in a request to the OS for a redraw, and the OS will, 
	 * at a time it deems appropriate, call back the application. An SWT application behaves in the same way.
	 * 
	 */
	public <T> boolean removeProjectfromTreeView(String projectName) {
		for (int i = 0; i < invisibleRoot.children.size(); i++) {
			TreeObject<T> child = (TreeObject<T>) invisibleRoot.children.get(i);
			if (child.getProjectName().equals(projectName)) {
				((TreeParent<T>) invisibleRoot).removeChild(child);
				
				new Thread(new Runnable() {
					public void run() {
						try {
							Thread.sleep(100);
						} catch (Exception e) {
						}
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								viewer.refresh();
							}
						});
					}
				}).start();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param projectName
	 * @param workspaceModel
	 */
	public void addProjectToTreeViewer(String projectName, WorkspaceModel workspaceModel){
		
		Map<String, ProjectModel> projects = workspaceModel.getProjectModels();
		
		ProjectModel project = projects.get(projectName);
		
		TreeParent root = new TreeParent(projectName, projectName);
		try {
			// create separate folders for ContextEntities and
			// ContextAssertions
			TreeParent<ContextEntityModel> entitiesParent = new TreeParent<ContextEntityModel>(
					"CONSERT ContextEntities", projectName);
			root.addChild(entitiesParent);
			TreeParent<ContextAssertionModel> assertionsParent = new TreeParent<ContextAssertionModel>(
					"CONSERT ContextAssertions", projectName);
			root.addChild(assertionsParent);

			/*
			 * get the list of entities and assertions from the
			 * projectWideModel instance
			 */
			List<ContextEntityModel> entities = project.getEntities();
			List<ContextAssertionModel> assertions = project.getAssertions();

			System.out.println(project.getEntities());

			/* add entities to the tree */
			for (ContextEntityModel ent : entities) {
				TreeObject<ContextEntityModel> obj = new TreeObject<ContextEntityModel>(ent.getName(),
						project.getName());
				obj.setResource(ent);
				entitiesParent.addChild(obj);
			}

			/* add assertions to the tree */
			for (ContextAssertionModel ass : assertions) {
				TreeObject<ContextAssertionModel> obj = new TreeObject<ContextAssertionModel>(ass.getName(),
						project.getName());
				obj.setResource(ass);
				assertionsParent.addChild(obj);
			}

		} catch (Exception e) {
			/* log exception */
			e.printStackTrace();
		}
		
		invisibleRoot.addChild(root);
		
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						viewer.refresh();
					}
				});
			}
		}).start();
	}
	
	/**
	 * 
	 * @param projectName
	 * @param workspaceModel
	 * Method which is called to add a NEW, empty Project to the Tree View
	 */
	public void addEmptyProjectToTreeViewer(String projectName, WorkspaceModel workspaceModel){
Map<String, ProjectModel> projects = workspaceModel.getProjectModels();
		
		ProjectModel project = projects.get(projectName);
		
		TreeParent root = new TreeParent(projectName, projectName);
		try {
			// create separate folders for ContextEntities and
			// ContextAssertions
			TreeParent<ContextEntityModel> entitiesParent = new TreeParent<ContextEntityModel>(
					"CONSERT ContextEntities", projectName);
			root.addChild(entitiesParent);
			TreeParent<ContextAssertionModel> assertionsParent = new TreeParent<ContextAssertionModel>(
					"CONSERT ContextAssertions", projectName);
			root.addChild(assertionsParent);

			

		} catch (Exception e) {
			/* log exception */
			e.printStackTrace();
		}
		
		invisibleRoot.addChild(root);
		
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						viewer.refresh();
					}
				});
			}
		}).start();
		
	}
	
	/**
	 * 
	 * @param projectName
	 * Method which is called to add a NEW, empty Project to the Tree View
	 * Method called from WorkspaceModel
	 */
	public void addProjectToTreeViewer(String projectName){
		WorkspaceModel workspaceModel = WorkspaceModel.getInstance();
		addEmptyProjectToTreeViewer(projectName, workspaceModel);
	}

	class ViewContentProvider implements ITreeContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot == null)
					initialize();

				return getChildren(invisibleRoot);
			}

			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			}

			return null;
		}

		public Object[] getChildren(Object parent) {

			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).getChildren();
			}

			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent) parent).hasChildren();
			return false;
		}

	}

	class ViewLabelProvider extends LabelProvider {
		public String getText(Object obj) {
			return obj.toString();
		}

		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;

			if (obj instanceof TreeParent)
				imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
	}

	public void initialize() {
		WorkspaceModel workspaceModel = WorkspaceModel.getInstance();
		workspaceModel.refreshWorkspace();
		Map<String, ProjectModel> projects = workspaceModel.getProjectModels();

		invisibleRoot = new TreeParent("", "");

		for (Map.Entry<String, ProjectModel> entry : projects.entrySet()) {
			addProjectToTreeViewer(entry.getKey(), workspaceModel);
		}
	}

	public TreeViewerNew() {
		instance = this;
	}

	public static TreeViewerNew getInstance() {
		if (instance == null) {
			instance = new TreeViewerNew();
		}
		return instance;
	}

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getViewSite());
		hookContextMenu();
		hookDoubleCLickAction();
	}

	private void hookDoubleCLickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				Object object = ((IStructuredSelection) selection).getFirstElement();

				/*
				 * in case of double clicking on parent or sub-parent, returns
				 */
				if (!(object instanceof TreeObject)) {
					return;
				}
				TreeObject treeObject = (TreeObject) object;
				Object model = treeObject.getResource();
				if (model == null) {
					return;
				}

				/* get the ProjectModel associated to this selection */
				ProjectModel projectModel = WorkspaceModel.getInstance()
						.getProjectModel(treeObject.getParent().getParent().getName());
				IWorkbenchPage page = getViewSite().getWorkbenchWindow().getActivePage();

				try {
					if (model instanceof ContextEntityModel) {
						EditorInputWrapper eiw = new EditorInputWrapper((ContextEntityModel) model);
						eiw.setProjectModel(projectModel);
						page.openEditor(eiw, EntityMultiPageEditor.ID);
					} else if (model instanceof ContextAssertionModel) {
						EditorInputWrapper eiw = new EditorInputWrapper((ContextAssertionModel) model);
						eiw.setProjectModel(projectModel);
						page.openEditor(eiw, AssertionMultiPageEditor.ID);
					} else {
						System.err.println("Model is nor Entity nor Assertion!");
					}
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			};
		});
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		addRefreshActionToMenu(menuMgr);
		addNewEntityActionToMenu(menuMgr);
		addNewAssertionActionToMenu(menuMgr);
		addDeleteActionToMenu(menuMgr);
	}

	/**
	 * adds a new entry in the menu for adding new assertions and refreshes the
	 * view on Finish
	 */
	private void addNewAssertionActionToMenu(MenuManager menuMgr) {
		Action addAssertion = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				String projectName = WorkspaceModel.getInstance()
						.getCurrentActiveProject((IStructuredSelection) selection);
				IWizard wizard = new ContextAssertionWizard(projectName);
				WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						wizard);
				if (dialog.open() == Window.OK) {
					initialize();
					viewer.refresh();
				}
			}
		};
		addAssertion.setText("NewContextAssertion");
		menuMgr.add(addAssertion);
	}

	/**
	 * adds a new entry in the menu for adding new entities and refreshes the
	 * view on Finish
	 */
	private void addNewEntityActionToMenu(MenuManager menuMgr) {
		Action addEntity = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				String projectName = WorkspaceModel.getInstance()
						.getCurrentActiveProject((IStructuredSelection) selection);
				IWizard wizard = new ContextEntityWizard(projectName);
				WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						wizard);
				if (dialog.open() == Window.OK) {
					initialize();
					viewer.refresh();
				}
			}
		};
		addEntity.setText("NewContextEntity");
		menuMgr.add(addEntity);
	}
	
	/**
	 * 
	 * @param menuMgr
	 * Deletes an Entity or an Assertion
	 */
	private void addDeleteActionToMenu(MenuManager menuMgr) {
		Action delete = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				String projectName = WorkspaceModel.getInstance()
						.getCurrentActiveProject((IStructuredSelection) selection);
				
				Object object = ((IStructuredSelection) selection).getFirstElement();

				/*
				 * in case of deleting on parent or sub-parent, returns
				 */
				if (!(object instanceof TreeObject)) {
					return;
				}
				
				TreeObject treeObject = (TreeObject) object;
				Object model = treeObject.getResource();
				if (model == null) {
					return;
				}
				if(model instanceof ContextAssertionModel){
					System.out.println("Delete Assertion");
					WorkspaceModel.getInstance().removeAssertionfromProjectModel(projectName, (ContextAssertionModel) model);
				}
				if(model instanceof ContextEntityModel){
					System.out.println("Delete Entity");
					WorkspaceModel.getInstance().removeEntityfromProjectModel(projectName, (ContextEntityModel) model);
				}
				
				initialize();
				viewer.refresh();
				
			}
		};
		delete.setText("delete");
		menuMgr.add(delete);
	}
	

	private void addRefreshActionToMenu(MenuManager menuMgr) {
		Action refresh = new Action() {
			public void run() {
				initialize();
				viewer.refresh();
			}
		};
		refresh.setText("Refresh");
		menuMgr.add(refresh);
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public TreeViewer getView() {
		return viewer;
	}

	/**
	 * Called indirectly by the observable object's notifyAll() method
	 */
	@Override
	public void update(Observable o, Object arg) {
		/* Refreshes the view on update */
		initialize();
		viewer.refresh();
	}
}
