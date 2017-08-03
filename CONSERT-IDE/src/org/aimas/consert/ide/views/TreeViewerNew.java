package org.aimas.consert.ide.views;

import java.util.ArrayList;
import java.util.List;

import org.aimas.consert.ide.model.ContextAssertionModel;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class TreeViewerNew extends ViewPart{
	public static final String ID = "org.aimas.consert.ide.views.TreeViewerNew";
	private static TreeViewerNew instance;
	private TreeViewer viewer;
	private TreeParent invisibleRoot;

	class TreeObject implements IAdaptable {

		private String name;
		private TreeParent parent;
		private Object resource;

		public TreeObject(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setParent(TreeParent parent) {
			this.parent = parent;
		}

		public TreeParent getParent() {
			return parent;
		}

		public String toString() {
			return getName();
		}

		public Object getAdapter(Class key) {
			return null;
		}

		protected Object getResource() {
			return resource;
		}

		protected void setResource(Object resource) {
			this.resource = resource;
		}
	}

	class TreeParent extends TreeObject {
		private ArrayList<TreeObject> children;

		public TreeParent(String name) {
			super(name);
			children = new ArrayList<TreeObject>();
		}

		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}

		public TreeObject[] getChildren() {
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
		ProjectModel projectWideModel = ProjectModel.getInstance();
		TreeParent root = new TreeParent("CONSERT Model elements");
		try {
			// create separate folders for ContextEntities and ContextAssertions
			TreeParent entitiesParent = new TreeParent("CONSERT ContextEntities");
			root.addChild(entitiesParent);
			TreeParent assertionsParent = new TreeParent("CONSERT ContextAssertions");
			root.addChild(assertionsParent);
			
			// get the list of entities and assertions from the projectWideModel instance
			List<ContextEntityModel> entities = projectWideModel.getEntities();
			List<ContextAssertionModel> assertions = projectWideModel.getAssertions();
						
			//add entities to the tree
			for (ContextEntityModel ent : entities) {
				TreeObject obj = new TreeObject(ent.getName());
				obj.setResource(ent);
				entitiesParent.addChild(obj);
			}
			
			//add assertions to the tree
			for (ContextAssertionModel ass : assertions) {
				TreeObject obj = new TreeObject(ass.getName());
				obj.setResource(ass);
				assertionsParent.addChild(obj);
			}

		} catch (Exception e) {
			// log exception
		}
		invisibleRoot = new TreeParent("");
		invisibleRoot.addChild(root);
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
		// hookDoubleCLickAction();
	}

	// private void hookDoubleCLickAction() {
	// viewer.addDoubleClickListener(new IDoubleClickListener() {
	// public void doubleClick(DoubleClickEvent event) {
	// ISelection selection = event.getSelection();
	// Object obj = ((IStructuredSelection) selection).getFirstElement();
	// if (!(obj instanceof TreeObject)) {
	// return;
	// } else {
	// TreeObject tempObj = (TreeObject) obj;
	// IFile ifile =
	// ResourcesPlugin.getWorkspace().getRoot().getFile(tempObj.getResource().getFullPath());
	// IWorkbenchPage dpage =
	// TreeViewerNew.this.getViewSite().getWorkbenchWindow().getActivePage();
	// if (dpage != null) {
	// try {
	// IDE.openEditor(dpage, ifile, true);
	// } catch (Exception e) {
	// // log exception
	// }
	// }
	// }
	// };
	// });
	// }

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
