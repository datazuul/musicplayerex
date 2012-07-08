/**
 * 						About Me
 * 
 * Copyright © 2004 Jérôme JOUVIE (Jouvieje)
 * 
 * @author Jérôme JOUVIE (Jouvieje)
 * 
 * WANT TO CONTACT ME ?
 * E-mail :
 * 		jerome.jouvie@gmail.com
 * My web sites :
 * 		http://topresult.tomato.co.uk/~jerome/
 * 		http://jerome.jouvie.free.fr/
 * 
 * DISTRIBUTION
 * You can use all this class or only the part you want.
 * All this code is made to learn how to use NativeFmod
 *                                    :-)
 */

package jouvieje.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.net.URI;

public class AboutMeDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private final String info;
	private final String help;
	private final int yOffset;

	private JPanel jContentPane = null;
	private JLabel jPicture = null;
	private JLabel jInfo = null;
	private JLabel jAuteur = null;
	private JLabel jEmail = null;
	private JLabel jEmail2 = null;
	private JLabel jSite = null;
	private JLabel jSite1 = null;
	private JPanel jHelpP = null;
	private JLabel jHelp = null;

	public AboutMeDialog(String infoApp, String[] help) {
		this((Dialog)null, false, infoApp, help);
	}

	public AboutMeDialog(Frame owner, boolean modal, String infoApp, String[] help) {
		super(owner, modal);
		this.info = infoApp;
		if(help != null) this.yOffset = 45 + help.length * 13;
		else this.yOffset = 0;
		this.help = toHelpText(help);
		initialize();
	}

	public AboutMeDialog(Dialog owner, boolean modal, String infoApp, String[] help) {
		super(owner, modal);
		this.info = infoApp;
		if(help != null) this.yOffset = 45 + help.length * 13;
		else this.yOffset = 0;
		this.help = toHelpText(help);
		initialize();
	}

	private void endInitialize() {
		this.setPreferredSize(new Dimension(354, 260 + yOffset));
		this.setSize(new Dimension(354, 260 + yOffset));
	}

	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("About Me");
		this.setPreferredSize(new Dimension(354, 260));
		this.setSize(new Dimension(354, 260));
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE) AboutMeDialog.this.dispose();
			}
		});
		endInitialize();
	}

	private JPanel getJContentPane() {
		if(jContentPane == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridwidth = 2;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridwidth = 2;
			gridBagConstraints2.gridy = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridwidth = 2;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 3;
			GridBagConstraints gridBagConstraints4_ = new GridBagConstraints();
			gridBagConstraints4_.gridx = 1;
			gridBagConstraints4_.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4_.gridy = 3;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.weightx = 1.0D;
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 4;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.weightx = 1.0D;
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 4;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridwidth = 2;
			gridBagConstraints7.fill = GridBagConstraints.BOTH;
			gridBagConstraints7.insets = new Insets(0, 20, 10, 20);
			gridBagConstraints7.weighty = 1.0D;
			gridBagConstraints7.gridy = 5;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.setBackground(Color.black);
			jContentPane.setForeground(Color.yellow);
			jContentPane.add(getJPicture(), gridBagConstraints1);
			jContentPane.add(getJInfo(), gridBagConstraints2);
			jContentPane.add(getJAuteur(), gridBagConstraints3);
			jContentPane.add(getJEmail(), gridBagConstraints4);
			jContentPane.add(getJEmail2(), gridBagConstraints4_);
			jContentPane.add(getJSite(), gridBagConstraints5);
			jContentPane.add(getJSite1(), gridBagConstraints6);
			if(help != null && !help.equals("")) {
				jContentPane.add(getJHelpP(), gridBagConstraints7);
			}
		}
		return jContentPane;
	}

	private JLabel getJPicture() {
		if(jPicture == null) {
			jPicture = new JLabel();
			jPicture.setHorizontalAlignment(SwingConstants.LEFT);
			try {
				jPicture.setIcon(new ImageIcon(getClass().getResource("/jouvieje/util/Autor.gif")));
			} catch(Throwable t1) {
				try {
					jPicture.setIcon(new ImageIcon(getClass().getResource("/jouvieje/util/Autor.jpg")));
				} catch(Throwable t2) {}
			}
		}
		return jPicture;
	}

	private JLabel getJInfo() {
		if(jInfo == null) {
			jInfo = new JLabel();
			jInfo.setBackground(new Color(0, 0, 0));
			jInfo.setForeground(new Color(255, 255, 0));
			jInfo.setHorizontalAlignment(SwingConstants.CENTER);
			jInfo.setText(info);
			jInfo.setBounds(0, 155, 346, 16);
		}
		return jInfo;
	}

	private JLabel getJAuteur() {
		if(jAuteur == null) {
			jAuteur = new JLabel();
			jAuteur.setBackground(new Color(0, 0, 0));
			jAuteur.setForeground(new Color(255, 255, 0));
			jAuteur.setHorizontalAlignment(SwingConstants.CENTER);
			jAuteur.setText("Author : Jérôme JOUVIE");
			jAuteur.setBounds(0, 174, 346, 16);
		}
		return jAuteur;
	}

	private JLabel getJEmail() {
		if(jEmail == null) {
			jEmail = new JLabel();
			jEmail.setBackground(new Color(0, 0, 0));
			jEmail.setForeground(new Color(255, 255, 0));
			jEmail.setHorizontalAlignment(SwingConstants.RIGHT);
			jEmail.setText("E-mail : ");
			jEmail.setBounds(0, 193, 346, 16);
		}
		return jEmail;
	}

	private JLabel getJEmail2() {
		if(jEmail2 == null) {
			jEmail2 = new JLabel();
			jEmail2.setBackground(new Color(0, 0, 0));
			jEmail2.setForeground(new Color(255, 255, 0));
			jEmail2.setHorizontalAlignment(SwingConstants.LEFT);
			jEmail2.setText("<html><body><font color=\"#639ACE\"><a href=\"mailto:jerome.jouvie@gmail.com\">jerome.jouvie@gmail.com</a></body></html>");
			jEmail2.setCursor(new Cursor(Cursor.HAND_CURSOR));
			jEmail2.addMouseListener(new java.awt.event.MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					try {
						if(Desktop.isDesktopSupported()) {
							Desktop desktop = Desktop.getDesktop();
							if(desktop.isSupported(Desktop.Action.MAIL)) {
								desktop.mail(new URI("mailto", "jerome.jouvie@gmail.com", null));
							}
						}
					} catch(Throwable t) {}
				}
			});
		}
		return jEmail2;
	}

	private JLabel getJSite() {
		if(jSite == null) {
			jSite = new JLabel();
			jSite.setText("Web site : ");
			jSite.setBackground(Color.black);
			jSite.setForeground(Color.yellow);
			jSite.setHorizontalAlignment(SwingConstants.RIGHT);
			jSite.setBounds(25, 212, 60, 16);
		}
		return jSite;
	}

	private JLabel getJSite1() {
		if(jSite1 == null) {
			jSite1 = new JLabel();
			jSite1.setText("<html><font color=\"#639ACE\"><a href=\"http://jerome.jouvie.free.fr/\">http://jerome.jouvie.free.fr/</a></font></html>");
			jSite1.setBackground(Color.black);
			jSite1.setForeground(Color.yellow);
			jSite1.setHorizontalAlignment(SwingConstants.LEFT);
			jSite1.setCursor(new Cursor(Cursor.HAND_CURSOR));
			jSite1.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					try {
						if(Desktop.isDesktopSupported()) {
							Desktop desktop = Desktop.getDesktop();
							if(desktop.isSupported(Desktop.Action.BROWSE)) {
								desktop.browse(new URI("http://jerome.jouvie.free.fr/index.php"));
							}
						}
					} catch(Throwable t) {}
				}
			});
		}
		return jSite1;
	}

	private JPanel getJHelpP() {
		if(jHelpP == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridy = 1;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.insets = new Insets(0, 10, 0, 10);
			gridBagConstraints.gridx = 0;
			jHelpP = new JPanel();
			jHelpP.setLayout(new GridBagLayout());
			TitledBorder ivjTitledBorder = BorderFactory.createTitledBorder(null, "Menu d\'aide",
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
					new Font("Dialog", Font.BOLD, 13), Color.yellow);
			ivjTitledBorder.setTitle("Help Menu");
			jHelpP.setBorder(ivjTitledBorder);
			jHelpP.setBackground(new Color(0, 0, 0));
			jHelpP.setForeground(new Color(255, 255, 0));
			jHelpP.add(getJHelp(), gridBagConstraints);
			jHelpP.setBounds(30, 251, 286, 50 + yOffset);
		}
		return jHelpP;
	}

	private JLabel getJHelp() {
		if(jHelp == null) {
			jHelp = new JLabel();
			jHelp.setText(help);
			jHelp.setVerticalAlignment(SwingConstants.TOP);
			jHelp.setBackground(new Color(0, 0, 0));
			jHelp.setForeground(new Color(255, 255, 0));
		}
		return jHelp;
	}

	private String toHelpText(String[] helps) {
		if(helps == null) return "";

		String s = "<html><body>";
		for(int i = 0; i <= helps.length - 1; i++) {
			s += helps[i] + "<br>";
		}
		s += "</body></html>";
		return s;
	}
} //  @jve:visual-info  decl-index=0 visual-constraint="10,10"