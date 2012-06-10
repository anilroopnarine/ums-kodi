package net.pms.external.xbmc;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class ConfigPanel extends JPanel {
	private JTextField sqliteVideoTextField;
	private JTextField sqliteMusicTextField;

	/**
	 * Create the panel.
	 */
	public ConfigPanel() {
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JPanel sqlitePanel = new JPanel();
		tabbedPane.addTab("SQLite Config", null, sqlitePanel, null);
		
		JPanel sqliteVideoPanel = new JPanel();
		sqliteVideoPanel.setBorder(new TitledBorder(null, "Video", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel sqliteMusicPanel = new JPanel();
		sqliteMusicPanel.setBorder(new TitledBorder(null, "Music", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout gl_sqlitePanel = new GroupLayout(sqlitePanel);
		gl_sqlitePanel.setHorizontalGroup(
			gl_sqlitePanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_sqlitePanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_sqlitePanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(sqliteMusicPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
						.addComponent(sqliteVideoPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_sqlitePanel.setVerticalGroup(
			gl_sqlitePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_sqlitePanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(sqliteVideoPanel, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(sqliteMusicPanel, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(70, Short.MAX_VALUE))
		);
		
		sqliteMusicTextField = new JTextField();
		sqliteMusicTextField.setColumns(10);
		
		JButton btnBrowseMusicDb = new JButton("Browse Music DB File");
		GroupLayout gl_sqliteMusicPanel = new GroupLayout(sqliteMusicPanel);
		gl_sqliteMusicPanel.setHorizontalGroup(
			gl_sqliteMusicPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_sqliteMusicPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_sqliteMusicPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(sqliteMusicTextField, GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
						.addComponent(btnBrowseMusicDb, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_sqliteMusicPanel.setVerticalGroup(
			gl_sqliteMusicPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_sqliteMusicPanel.createSequentialGroup()
					.addComponent(sqliteMusicTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnBrowseMusicDb)
					.addContainerGap(43, Short.MAX_VALUE))
		);
		sqliteMusicPanel.setLayout(gl_sqliteMusicPanel);
		
		sqliteVideoTextField = new JTextField();
		sqliteVideoTextField.setColumns(10);
		
		JButton btnBrowseVideoDb = new JButton("Browse Video DB File");
		GroupLayout gl_sqliteVideoPanel = new GroupLayout(sqliteVideoPanel);
		gl_sqliteVideoPanel.setHorizontalGroup(
			gl_sqliteVideoPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_sqliteVideoPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_sqliteVideoPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(sqliteVideoTextField, GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
						.addComponent(btnBrowseVideoDb))
					.addContainerGap())
		);
		gl_sqliteVideoPanel.setVerticalGroup(
			gl_sqliteVideoPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_sqliteVideoPanel.createSequentialGroup()
					.addComponent(sqliteVideoTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnBrowseVideoDb)
					.addContainerGap(27, Short.MAX_VALUE))
		);
		sqliteVideoPanel.setLayout(gl_sqliteVideoPanel);
		sqlitePanel.setLayout(gl_sqlitePanel);
		
		JPanel mySqlPanel = new JPanel();
		tabbedPane.addTab("mySQL Config", null, mySqlPanel, null);
		GroupLayout gl_mySqlPanel = new GroupLayout(mySqlPanel);
		gl_mySqlPanel.setHorizontalGroup(
			gl_mySqlPanel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 425, Short.MAX_VALUE)
		);
		gl_mySqlPanel.setVerticalGroup(
			gl_mySqlPanel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 250, Short.MAX_VALUE)
		);
		mySqlPanel.setLayout(gl_mySqlPanel);
		setLayout(groupLayout);

	}
}
