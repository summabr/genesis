/*
 * The Genesis Project
 * Copyright (C) 2005-2006  Summa Technologies do Brasil Ltda.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.java.dev.genesis.samples.useradmin.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

import net.java.dev.genesis.samples.useradmin.UserAdmin;
import net.java.dev.genesis.samples.useradmin.ui.UserListForm;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.swing.factory.SwingFactory;

/**
 * @ViewHandler
 */
public class UserListView extends JFrame {
   private final SwingBinder binder;
   private final UserListForm form;

   private JLabel nameLabel;
   private JLabel loginLabel;
   private JLabel emailLabel;
   private JTextField nameTextField;
   private JTextField emailTextField;
   private JTextField loginTextField;
   private JTable usersTable;
   private JButton searchButton;
   private JButton resetButton;
   private JButton previousButton;
   private JButton nextButton;
   private JButton newUserButton;
   private JButton updateUserButton;
   private JButton removeUserButton;

   public UserListView() {
      super(getMessage("UserListView.title"));

      binder = new SwingBinder(this, form = new UserListForm());
      initialize();
      binder.bind();
   }

   private void initialize() {
      JPanel panel = new JPanel();
      setContentPane(panel);
      panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

      GridBagConstraints c = new GridBagConstraints();
      c.insets = new Insets(5, 5, 5, 5);
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1.0;
      panel.setLayout(new GridBagLayout());

      JPanel upperPanel = new JPanel();
      upperPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 7, 5));

      nameLabel = new JLabel(getMessage("User.name"));
      nameLabel.setLabelFor(nameTextField);
      nameLabel.setDisplayedMnemonic('N');
      upperPanel.add(nameLabel);

      nameTextField = new JTextField();
      nameTextField.setName("name");
      nameTextField.setPreferredSize(new Dimension(170, 20));
      upperPanel.add(nameTextField);

      loginLabel = new JLabel(getMessage("User.login"));
      loginLabel.setLabelFor(loginTextField);
      upperPanel.add(loginLabel);

      loginTextField = new JTextField();
      loginTextField.setName("login");
      loginTextField.setPreferredSize(new Dimension(160, 20));
      upperPanel.add(loginTextField);

      emailLabel = new JLabel(getMessage("User.email"));
      emailLabel.setLabelFor(emailTextField);
      upperPanel.add(emailLabel);

      emailTextField = new JTextField();
      emailTextField.setName("email");
      emailTextField.setPreferredSize(new Dimension(170, 20));
      upperPanel.add(emailTextField);

      searchButton = new JButton(getMessage("button.search"));
      searchButton.setName("doSearch");
      getRootPane().setDefaultButton(searchButton);
      upperPanel.add(searchButton);

      resetButton = new JButton(getMessage("button.reset"));
      resetButton.setName("reset");
      upperPanel.add(resetButton);

      c.gridx = 0;
      c.gridy = 0;
      panel.add(upperPanel, c);

      JPanel middlePanel = new JPanel();
      middlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
      middlePanel.setBorder(new EtchedBorder());
      middlePanel.setPreferredSize(new Dimension(790, 300));

      JScrollPane tablePane = new JScrollPane();
      tablePane.setBorder(new EtchedBorder());
      tablePane.setPreferredSize(new Dimension(780, 230));

      usersTable = SwingFactory.createTable(binder, "users", new String[] {
            "name", "login", "email", "role", "country", "state" },
            new String[] { getMessage("User.name"), getMessage("User.login"),
                  getMessage("User.email"), getMessage("User.role"),
                  getMessage("User.country"), getMessage("User.state") });
      usersTable.setPreferredSize(new Dimension(300, 205));
      usersTable.setShowHorizontalLines(false);
      usersTable.setShowVerticalLines(false);
      usersTable.setColumnSelectionAllowed(false);
      usersTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
               if (event.getClickCount() == 2) {
                  binder.invokeAction("update");
               }
            }
         });

      tablePane.setViewportView(usersTable);
      middlePanel.add(tablePane);

      JPanel pagingPanel = new JPanel();
      pagingPanel.setLayout(new BorderLayout());
      pagingPanel.setPreferredSize(new Dimension(760, 20));

      previousButton = new JButton("<<");
      previousButton.setName("previousPage");
      pagingPanel.add(previousButton, BorderLayout.WEST);

      nextButton = new JButton(">>");
      nextButton.setName("nextPage");
      pagingPanel.add(nextButton, BorderLayout.EAST);

      middlePanel.add(pagingPanel);

      JPanel bottomPanel = new JPanel();
      bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
      bottomPanel.setPreferredSize(new Dimension(760, 35));

      newUserButton = new JButton(getMessage("button.newUser"));
      newUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               try {
                  create();
               } catch (Exception e) {
                  binder.handleException(e);
               }
            }
         });
      bottomPanel.add(newUserButton);

      updateUserButton = new JButton(getMessage("button.updateUser"));
      updateUserButton.setName("update");
      bottomPanel.add(updateUserButton);

      removeUserButton = new JButton(getMessage("button.removeUser"));
      removeUserButton.setName("remove");
      bottomPanel.add(removeUserButton);

      middlePanel.add(bottomPanel);

      setResizable(false);

      c.gridx = 0;
      c.gridy = 1;
      c.weighty = 1.0;
      c.fill = GridBagConstraints.BOTH;
      panel.add(middlePanel, c);

      addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent event) {
            try {
               UserAdmin.showMainWindow();
            } catch (Exception e) {
               binder.handleException(e);
            }
         }
      });

      pack();
      setLocationRelativeTo(null);
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
   }

   private static String getMessage(String key) {
      return UIUtils.getInstance().getBundle().getString(key);
   }
   
   public void display() throws Exception {
      setVisible(true);
   }

   public void create() throws Exception {
      final InsertUpdateView view = new InsertUpdateView(this);

      if (view.showView()) {
         runSearch();
         binder.refresh();
      }
   }

   /**
    * @BeforeAction
    */
   public void update() throws Exception {
      final InsertUpdateView view = new InsertUpdateView(this, form.getUser());

      if (view.showView()) {
         runSearch();
      }
   }

   /**
    * @BeforeAction("remove")
    */
   public boolean confirmRemove() {
      return JOptionPane.showConfirmDialog(this,
            getMessage("UserListView.deleteConfirmation"),
            getMessage("UserListView.deleteConfirmationTitle"),
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
   }

   /**
    * @AfterAction
    */
   public void remove() {
      runSearch();
   }

   private void runSearch() {
      form.reset();
      form.setResetSearch(true);
      form.setRunSearch(true);
   }
}
