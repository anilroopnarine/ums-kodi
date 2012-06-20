package net.pms.external;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
class MySqlOptionPane extends JOptionPane {

	private PMSXBMCConfig callback;

	public MySqlOptionPane(PMSXBMCConfig callback) {
		this.callback = callback;
	}

	public String[] showInputDialog() {
		String[] data = null;
		class GetData extends JDialog implements ActionListener {
			JTextField host = new JTextField(callback.getMySqlDetails()[0]);
			JTextField port = new JTextField(callback.getMySqlDetails()[1]);
			JTextField db = new JTextField(callback.getMySqlDetails()[2]);
			JTextField user = new JTextField(callback.getMySqlDetails()[3]);
			JTextField pass = new JTextField(callback.getMySqlDetails()[4]);
			JButton btnOK = new JButton("OK");
			JButton btnCancel = new JButton("Cancel");
			String[] str = null;

			public GetData() {
				setModal(true);
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				setLocation(400, 300);

				getContentPane().setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;

				c.gridx = 0;
				c.gridy = 0;
				getContentPane().add(new JLabel("Host"), c);

				c.gridx = 1;
				getContentPane().add(host, c);

				c.gridx = 0;
				c.gridy = 1;
				getContentPane().add(new JLabel("Port"), c);
				c.gridx = 1;
				getContentPane().add(port, c);

				c.gridx = 0;
				c.gridy = 2;
				getContentPane().add(new JLabel("Database"), c);
				c.gridx = 1;
				getContentPane().add(db, c);

				c.gridx = 0;
				c.gridy = 3;
				getContentPane().add(new JLabel("Username"), c);
				c.gridx = 1;
				getContentPane().add(user, c);

				c.gridx = 0;
				c.gridy = 4;
				getContentPane().add(new JLabel("Password"), c);
				c.gridx = 1;
				getContentPane().add(pass, c);

				JPanel jp = new JPanel();
				btnOK.addActionListener(this);
				btnCancel.addActionListener(this);
				jp.add(btnOK);
				jp.add(btnCancel);

				c.weightx = 0.0;
				c.gridwidth = 3;
				c.gridx = 0;
				c.gridy = 5;
				getContentPane().add(jp, c);
				pack();
				setVisible(true);
			}

			public void actionPerformed(ActionEvent ae) {
				if (ae.getSource() == btnOK) {
					str = new String[] { host.getText(), port.getText(), db.getText(), user.getText(), pass.getText() };
				}
				dispose();
			}

			public String[] getData() {
				return str;
			}
		}
		data = new GetData().getData();
		return data;
	}
}