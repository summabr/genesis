package net.java.dev.genesis.samples.useradmin.ui.swt;

import net.java.dev.genesis.samples.useradmin.ui.InsertRoleForm;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.swt.SwtBinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @ViewHandler
 */
public class InsertRoleView extends Dialog {

   private SwtBinder binder;
   private boolean hasChanged;
   
   private Shell shell = null;
   private Composite composite = null;
   private Label codeLabel = null;
   private Text code = null;
   private Label labelLabel = null;
   private Text label = null;
   private SashForm buttonsContainer = null;
   private Button cancel = null;
   private Button save = null;
   
   public InsertRoleView(Shell parent) {
      super(parent);
      shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
      shell.setText(getMessage("InsertRoleView.title"));
      binder = new SwtBinder(shell, new InsertRoleForm(), this);
      createShell();
      binder.bind();
   }
   
   /**
    * This method initializes shell
    */
   private void createShell() {
      if (shell == null) {
         shell = new Shell();
         shell.setText("Shell");
      }
      shell.setLayout(new GridLayout());

      createComposite();
      createButtonsContainer();
      shell.setDefaultButton(save);
      
      shell.pack();
   }

   /**
    * This method initializes composite	
    *
    */
   private void createComposite() {
      GridData gridData2 = new GridData();
      gridData2.horizontalAlignment = GridData.FILL;
      gridData2.verticalAlignment = GridData.CENTER;
      GridData gridData1 = new GridData();
      gridData1.horizontalAlignment = GridData.FILL;
      gridData1.widthHint = 150;
      gridData1.verticalAlignment = GridData.CENTER;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      composite = new Composite(shell, SWT.BORDER);
      composite.setLayout(gridLayout);

      codeLabel = new Label(composite, SWT.NONE);
      codeLabel.setText("Code");

      code = new Text(composite, SWT.BORDER);
      code.setLayoutData(gridData1);
      binder.register("code", code);

      labelLabel = new Label(composite, SWT.NONE);
      labelLabel.setText("Label");

      label = new Text(composite, SWT.BORDER);
      label.setLayoutData(gridData2);
      binder.register("label", label);
   }

   /**
    * This method initializes buttonsContainer	
    *
    */
   private void createButtonsContainer() {
      GridData gridData = new GridData();
      gridData.horizontalAlignment = GridData.CENTER;
      gridData.verticalAlignment = GridData.CENTER;

      buttonsContainer = new SashForm(shell, SWT.NONE);
      buttonsContainer.setLayoutData(gridData);

      cancel = new Button(buttonsContainer, SWT.NONE);
      cancel.setText("Cancel");

      cancel.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            shell.dispose();
         }
      });
      save = new Button(buttonsContainer, SWT.NONE);
      save.setText("Save");
      binder.register("save", save);
   }

   private static String getMessage(String key) {
      return UIUtils.getInstance().getBundle().getString(key);
   }

   public boolean display() {
      Rectangle bounds = shell.getParent().getBounds();
      shell.setLocation(bounds.x + (bounds.width - shell.getBounds().width) / 2,
            bounds.y + (bounds.height - shell.getBounds().height) / 2);

      shell.open();
      while (!shell.isDisposed()) {
         if (!shell.getDisplay().readAndDispatch()) {
            shell.getDisplay().sleep();
         }
      }

      return hasChanged;
   }

   /**
    * @AfterAction
    */
   public void save() throws Exception {
      shell.dispose();
      hasChanged = true;
   }
}