package org.aimas.consert.ide.views;

import java.util.ArrayList;
import java.util.List;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.assertion.AssertionMultiPageEditor;
import org.aimas.consert.ide.editor.entity.EntityMultiPageEditor;
import org.aimas.consert.ide.model.ContextAssertionModel;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.aimas.consert.ide.model.WorkspaceModel;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class TreeViewerNew extends ViewPart {
	public static final String ID = "org.aimas.consert.ide.views.TreeViewerNew";
	private static TreeViewerNew instance;
	private TreeViewer viewer;
	private TreeParent invisibleRoot;

	class TreeObject<T> implements IAdaptable {

		private String name;
		private TreeParent<T> parent;
		private T resource;

		public TreeObject(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setParent(TreeParent<T> parent) {
			this.parent = parent;
		}

		public TreeParent<T> getParent() {
			return parent;
		}

		public String toString() {
			return getName();
		}

		public Object getAdapter(Class key) {
			return null;
		}

		protected T getResource() {
			return resource;
		}

		protected void setResource(T resource) {
			this.resource = resource;
		}
	}

	class TreeParent<T> extends TreeObject<T> {
		private ArrayList<TreeObject<T>> children;

		public TreeParent(String name) {
			super(name);
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
			return children.size() > 0;
		}

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
		ArrayList<ProjectModel> projects = workspaceModel.getProjectModels();
		
//		  IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//		    if (window != null)
//		    {
//		        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
//		        Object firstElement = selection.getFirstElement();
//		        if (firstElement instanceof IAdaptable)
//		        {
//		            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
//		            IPath path = project.getFullPath();
//		            System.out.println(path);
//		        }
//		    }
//		 IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//		 IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
//		 System.out.println("Got selection");
	
//		 System.out.println(selection.toString());
//		    IWorkbenchPage activePage = window.getActivePage();
//		    ISelection selection = activePage.getSelection();
//		    if (selection != null) {
//		        System.out.println("Got selection");
//		    }
		
		invisibleRoot = new TreeParent("");
		for (ProjectModel project : projects){
			System.out.println(project.getName());
			TreeParent root = new TreeParent(project.getName());
			try {
				// create separate folders for ContextEntities and ContextAssertions
				TreeParent<ContextEntityModel> entitiesParent = new TreeParent<ContextEntityModel>(
						"CONSERT ContextEntities");
				root.addChild(entitiesParent);
				TreeParent<ContextAssertionModel> assertionsParent = new TreeParent<ContextAssertionModel>(
						"CONSERT ContextAssertions");
				root.addChild(assertionsParent);

				// get the list of entities and assertions from the projectWideModel
				// instance
				List<ContextEntityModel> entities = project.getEntities();
				List<ContextAssertionModel> assertions = project.getAssertions();

				// add entities to the tree
				for (ContextEntityModel ent : entities) {
					TreeObject<ContextEntityModel> obj = new TreeObject<ContextEntityModel>(ent.getName());
					obj.setResource(ent);
					entitiesParent.addChild(obj);
				}

				// add assertions to the tree
				for (ContextAssertionModel ass : assertions) {
					TreeObject<ContextAssertionModel> obj = new TreeObject<ContextAssertionModel>(ass.getName());
					obj.setResource(ass);
					assertionsParent.addChild(obj);
				}

			} catch (Exception e) {
				// log exception
			}
			invisibleRoot.addChild(root);
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
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (!(obj instanceof TreeObject)) {
					return;
				}
				// get the page
				IWorkbenchPage page = TreeViewerNew.this.getViewSite().getWorkbenchWindow().getActivePage();
				try {
					Object model = ((TreeObject) obj).getResource();
					if (model instanceof ContextEntityModel) {
						page.openEditor(new EditorInputWrapper((ContextEntityModel) model), EntityMultiPageEditor.ID);
					} else if (model instanceof ContextAssertionModel) {
						page.openEditor(new EditorInputWrapper((ContextAssertionModel) model),
								AssertionMultiPageEditor.ID);
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
}
