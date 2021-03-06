package org.jamocha.gui.editor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.jamocha.gui.icons.IconLoader;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Module;
import org.jamocha.rete.MultiSlot;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.Template;

public class FactEditor extends AbstractJamochaEditor implements
		ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 6037731034903564707L;

	private int step = 0;

	private JPanel contentPanel;

	private JButton cancelButton;

	private JButton assertButton;

	private JButton backButton;

	private JButton nextButton;

	private JButton reloadButtondumpAreaFact;

	private JList<String> moduleList;

	private JList<String> templateList;

	private JTextArea dumpAreaTemplate = new JTextArea();

	private JTextArea dumpAreaFact = new JTextArea();

	private DefaultListModel<String> moduleListModel = new DefaultListModel<String>();

	private DefaultListModel<String> templateListModel = new DefaultListModel<String>();

	private StringChannel channel;

	private Map<Slot, JComponent> factComponents = new HashMap<Slot, JComponent>();

	public FactEditor(Rete engine) {
		super(engine);
		setLayout(new BorderLayout());
		setTitle("Assert new Fact");
		contentPanel = new JPanel(new CardLayout());
		add(contentPanel, BorderLayout.CENTER);
		cancelButton = new JButton("Cancel", IconLoader.getImageIcon("cancel"));
		cancelButton.addActionListener(this);
		assertButton = new JButton("Assert Fact", IconLoader
				.getImageIcon("database_add"));
		assertButton.addActionListener(this);
		assertButton.setVisible(false);
		backButton = new JButton("Back", IconLoader
				.getImageIcon("resultset_previous"));
		backButton.addActionListener(this);
		backButton.setVisible(false);
		nextButton = new JButton("Next", IconLoader
				.getImageIcon("resultset_next"));
		nextButton.setHorizontalTextPosition(SwingConstants.LEFT);
		nextButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		buttonPanel.add(cancelButton);
		buttonPanel.add(backButton);
		buttonPanel.add(nextButton);
		buttonPanel.add(assertButton);
		add(buttonPanel, BorderLayout.PAGE_END);

		dumpAreaTemplate.setEditable(false);
		dumpAreaTemplate.setFont(new Font("Courier", Font.PLAIN, 12));
		dumpAreaTemplate.setRows(5);
		dumpAreaFact.setEditable(false);
		dumpAreaFact.setFont(new Font("Courier", Font.PLAIN, 12));
		dumpAreaFact.setRows(5);

	}

	public void setStringChannel(StringChannel channel) {
		this.channel = channel;
	}

	public void init() {
		// initialize the Panels
		initPreselectionPanel();
		initFactEditPanel();

		showCurrentStep();
		setVisible(true);
	}

	private void initPreselectionPanel() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel preselectionPanel = new JPanel(gridbag);
		preselectionPanel.setBorder(BorderFactory
				.createTitledBorder("Module and Template Selection"));
		c.fill = GridBagConstraints.BOTH;
		moduleList = new JList<String>(moduleListModel);
		moduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		moduleList.getSelectionModel().addListSelectionListener(this);
		Collection<?> modules = engine.getWorkingMemory().getModules();
		for (Object obj : modules) {
			Module mod = (Module) obj;
			moduleListModel.addElement(mod.getModuleName());
		}
		JPanel modulePanel = new JPanel();
		modulePanel.setLayout(new BoxLayout(modulePanel, BoxLayout.Y_AXIS));
		modulePanel.add(new JLabel("Select a Module:"));
		modulePanel.add(new JScrollPane(moduleList));
		c.weightx = 0.5;
		c.gridx = c.gridy = 0;
		c.weighty = 1.0;
		// c.gridwidth = GridBagConstraints.RELATIVE;
		gridbag.setConstraints(modulePanel, c);
		preselectionPanel.add(modulePanel);
		templateList = new JList<String>(templateListModel);
		templateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		templateList.getSelectionModel().addListSelectionListener(this);
		initTemplateList();
		JPanel templatePanel = new JPanel();
		templatePanel.setLayout(new BoxLayout(templatePanel, BoxLayout.Y_AXIS));
		templatePanel.add(new JLabel("Select a Template:"));
		templatePanel.add(new JScrollPane(templateList));
		// c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		gridbag.setConstraints(templatePanel, c);
		preselectionPanel.add(templatePanel);
		JPanel dumpAreaPanel = new JPanel();
		dumpAreaPanel.setLayout(new BoxLayout(dumpAreaPanel, BoxLayout.Y_AXIS));
		dumpAreaPanel.add(new JLabel("Template Definition:"));
		dumpAreaPanel.add(new JScrollPane(dumpAreaTemplate));
		c.weightx = 0.0;
		c.gridwidth = 2;
		c.gridy = 1;
		c.gridx = 0;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(dumpAreaPanel, c);
		preselectionPanel.add(dumpAreaPanel);
		contentPanel.add(preselectionPanel, "preselection");

	}

	private void initTemplateList() {
		templateListModel.clear();
		Module module = engine.getWorkingMemory().findModule(
				String.valueOf(moduleList.getSelectedValue()));
		if (module != null) {
			Collection<?> templates = module.getTemplates();
			for (Object obj : templates) {
				Template tmp = (Template) obj;
				if (!module.getModuleName().equals("MAIN")
						|| !tmp.getName().equals("_initialFact")) {
					templateListModel.addElement(tmp.getName());
				}
			}
		}
	}

	private void initFactEditPanel() {
		factComponents.clear();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel factEditPanel = new JPanel(new BorderLayout());
		JPanel innerPanel = new JPanel(gridbag);
		factEditPanel.setBorder(BorderFactory
				.createTitledBorder("Set the Slots for the Fact"));
		if (templateList.getSelectedIndex() > -1) {
			Module module = engine.getWorkingMemory().findModule(
					String.valueOf(moduleList.getSelectedValue()));

			Template tmp = module.getTemplate(String.valueOf(templateList
					.getSelectedValue()));

			c.weightx = 1.0;
			Slot[] slots = (Slot[])tmp.getAllSlots();
			for (int i = 0; i < slots.length; ++i) {
				c.gridx = 0;
				c.gridy = i;
				c.fill = GridBagConstraints.VERTICAL;
				c.anchor = GridBagConstraints.EAST;
				JLabel label = new JLabel(slots[i].getName() + ": ");
				gridbag.setConstraints(label, c);
				innerPanel.add(label);
				c.gridx = 1;
				c.fill = GridBagConstraints.BOTH;
				c.anchor = GridBagConstraints.WEST;
				if (slots[i] instanceof MultiSlot) {
					MultiSlotEditor multislotEditor = new MultiSlotEditor();
					JScrollPane scrollPane = new JScrollPane(multislotEditor
							.getList());
					gridbag.setConstraints(scrollPane, c);
					innerPanel.add(scrollPane);
					factComponents.put(slots[i], multislotEditor.getList());
				} else if (slots[i].getValueType() == Constants.FACT_TYPE) {
					// TODO Fact-Selector

					JComboBox<?> factBox = new JComboBox<Object>();
					factComponents.put(slots[i], factBox);
				} else {
					JTextField textField = new JTextField();
					gridbag.setConstraints(textField, c);
					innerPanel.add(textField);
					factComponents.put(slots[i], textField);
				}
			}
		}
		factEditPanel.add(new JScrollPane(innerPanel), BorderLayout.CENTER);
		JPanel dumpAreaPanel = new JPanel();
		dumpAreaPanel.setLayout(new BoxLayout(dumpAreaPanel, BoxLayout.Y_AXIS));
		dumpAreaPanel.add(new JLabel("Fact Preview:"));
		dumpAreaPanel.add(new JScrollPane(dumpAreaFact));
		reloadButtondumpAreaFact = new JButton("Reload Fact Preview",
				IconLoader.getImageIcon("arrow_refresh"));
		reloadButtondumpAreaFact.addActionListener(this);
		dumpAreaPanel.add(reloadButtondumpAreaFact);
		c.weightx = 0.0;
		c.gridwidth = 2;
		c.gridy = 1;
		c.gridx = 0;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(dumpAreaPanel, c);
		factEditPanel.add(dumpAreaPanel, BorderLayout.SOUTH);
		contentPanel.add("factEdit", factEditPanel);
	}

	private void showCurrentStep() {
		switch (step) {
		case 0:
			((CardLayout) contentPanel.getLayout()).show(contentPanel,
					"preselection");
			if (templateList.getSelectedIndex() > -1) {
				nextButton.setEnabled(true);
			} else {
				nextButton.setEnabled(false);
			}
			nextButton.setVisible(true);
			backButton.setVisible(false);
			assertButton.setVisible(false);
			nextButton.requestFocus();
			break;
		case 1:
			initFactEditPanel();
			((CardLayout) contentPanel.getLayout()).show(contentPanel,
					"factEdit");
			nextButton.setVisible(false);
			backButton.setVisible(true);
			assertButton.setVisible(true);
			assertButton.requestFocus();
			break;
		}
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == assertButton) {
			channel.executeCommand(getCurrentFactAssertionString(false));
			JOptionPane.showMessageDialog(this, "Assertion done.\nPlease check the log for Messages.");
		} else if (event.getSource() == backButton) {
			if (step > 0) {
				step--;
				showCurrentStep();
			}
		} else if (event.getSource() == nextButton) {
			if (step < 1) {
				step++;
				showCurrentStep();
			}
		} else if (event.getSource() == cancelButton) {
			close();
		} else if (event.getSource() == reloadButtondumpAreaFact) {
			dumpAreaFact.setText(getCurrentFactAssertionString(true));
		}
	}

	public void valueChanged(ListSelectionEvent event) {
		if (event.getSource() == moduleList.getSelectionModel()) {
			initTemplateList();
		} else if (event.getSource() == templateList.getSelectionModel()) {
			if (templateList.getSelectedIndex() > -1) {
				nextButton.setEnabled(true);
				Module module = engine.getWorkingMemory().findModule(
						String.valueOf(moduleList.getSelectedValue()));
				Template tmp = module.getTemplate(String.valueOf(templateList
						.getSelectedValue()));

				dumpAreaTemplate
						.setText("(deftemplate " + tmp.getName() + "\n");
				Slot[] slots = (Slot[])tmp.getAllSlots();
				for (Slot slot : slots) {
					dumpAreaTemplate.append("    (");
					if (slot instanceof MultiSlot) {
						dumpAreaTemplate.append("multislot");
					} else {
						dumpAreaTemplate.append("slot");
					}
					dumpAreaTemplate.append(" " + slot.getName() + ")\n");
				}
				dumpAreaTemplate.append(")");
			} else {
				nextButton.setEnabled(false);
			}
		}
	}

	private String getCurrentFactAssertionString(boolean print) {
		Module module = engine.getWorkingMemory().findModule(
				String.valueOf(moduleList.getSelectedValue()));
		Template tmp = module.getTemplate(String.valueOf(templateList
				.getSelectedValue()));
		StringBuilder res = new StringBuilder("(assert (" + tmp.getName());
		JComponent currComponent;
		for (Slot slot : factComponents.keySet()) {
			currComponent = factComponents.get(slot);
			if (print)
				res.append("\n\t");
			res.append("(" + slot.getName() + " ");
			if (slot instanceof MultiSlot) {
				Object[] values = ((DefaultListModel<?>) ((JList<?>) currComponent)
						.getModel()).toArray();
				for (int i = 0; i < values.length; ++i) {
					if (i > 0)
						res.append(" ");
					res.append("\"" + values[i].toString() + "\"");
				}
			} else if (slot.getValueType() == Constants.FACT_TYPE) {
				// TODO Fact-Selector
			} else {
				res
						.append("\"" + ((JTextField) currComponent).getText()
								+ "\"");
			}
			res.append(")");
		}
		if (print)
			res.append("\n");
		res.append("))");
		return res.toString();
	}

	private final class MultiSlotEditor implements ActionListener,
			PopupMenuListener {

		private JList<String> list;

		private DefaultListModel<String> listModel = new DefaultListModel<String>();

		private JPopupMenu popupMenu;

		private JMenuItem addMenuItem;

		private JMenuItem editMenuItem;

		private JMenuItem deleteMenuItem;

		@SuppressWarnings({ })
		private MultiSlotEditor() {
			popupMenu = new JPopupMenu();
			addMenuItem = new JMenuItem("add value", IconLoader
					.getImageIcon("add"));
			addMenuItem.addActionListener(this);
			editMenuItem = new JMenuItem("edit value", IconLoader
					.getImageIcon("pencil"));
			editMenuItem.addActionListener(this);
			deleteMenuItem = new JMenuItem("remove value", IconLoader
					.getImageIcon("delete"));
			deleteMenuItem.addActionListener(this);
			popupMenu.add(addMenuItem);
			popupMenu.add(editMenuItem);
			popupMenu.add(deleteMenuItem);
			popupMenu.addPopupMenuListener(this);
			list = new JList<String>(listModel);
			list.setVisibleRowCount(4);
			list.setComponentPopupMenu(popupMenu);
		}

		private JList<String> getList() {
			return list;
		}

		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == addMenuItem) {
				String value = JOptionPane.showInputDialog("Enter the value:");
				listModel.addElement(value);
			} else if (event.getSource() == editMenuItem) {
				String value = JOptionPane.showInputDialog("Enter the value:",
						list.getSelectedValue());
				listModel.set(list.getSelectedIndex(), value);
			} else if (event.getSource() == deleteMenuItem) {
				int[] indices = list.getSelectedIndices();
				// run backwards to delete the right indices
				for (int i = indices.length - 1; i >= 0; --i) {
					listModel.remove(indices[i]);
				}
			}
		}

		public void popupMenuCanceled(PopupMenuEvent arg0) {

		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {

		}

		public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
			addMenuItem.setVisible(true);
			if (list.getSelectedIndices().length > 1) {
				editMenuItem.setVisible(false);
				deleteMenuItem.setVisible(true);
			} else if (list.getSelectedIndices().length == 1) {
				editMenuItem.setVisible(true);
				deleteMenuItem.setVisible(true);
			} else {
				editMenuItem.setVisible(false);
				deleteMenuItem.setVisible(false);
			}
		}

	}
}
