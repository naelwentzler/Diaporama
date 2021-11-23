package diaporama;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

// class extends JFrame 
class affichage extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Borders variables for highlight element
	protected BevelBorder bevel;
	protected EmptyBorder empty;

	// Control default time
	protected int time = 3000;

	// Forward (1) and backward (0)
	protected int sens = 1;

	// Element selected with mouse
	protected Component selection;

	// List with all icons with the size for the thumbnail
	protected ImageIcon[] listIconThumb;

	// List with all icons with the size for the Diapo
	protected ImageIcon[] listIconDiapo;

	// Label selected by mouseClick
	protected JLabel previous;

	// Label accessible for everyone
	protected JLabel label = new JLabel();

	// Timer changing the image of the diapo
	protected final Timer timer = new Timer(time, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (sens == 1) {
				nextImageDiapo(listIconDiapo, label);
			} else {
				precImageDiapo(listIconDiapo, label);

			}

		}
	});

	// Creating Object of Jpanel class
	protected JPanel panel1 = new JPanel(); // Border Layout / Thumbnail
	protected JPanel panel2 = new JPanel(); // Contain my images
	protected JPanel panel3 = new JPanel(); // All my buttons for Thumbnail (panel1)
	protected JPanel panel4 = new JPanel(); // Diaporama
	protected JPanel panel5 = new JPanel(); // All my buttons for Diaporama (panel4)
	protected JPanel panel6 = new JPanel(); // Diaporama Paused
	protected JPanel panel7 = new JPanel(); // All my buttons for Diaporama Paused(panel6)

	// CardLayout to make it easier to change between panel to show ( Panel1, Panel4
	// and Panel6)
	protected CardLayout card = new CardLayout();
	protected JPanel deck = new JPanel(card);

	protected JButton slow = new JButton("<<");
	protected JButton start = new JButton("Start");
	protected JButton stop = new JButton("Stop");
	protected JButton fast = new JButton(">>");
	protected JButton pause = new JButton("Pause");

	// Allow me to know if im in the Diaporama Panel
	protected int inDiapo = 0;

	affichage() {

		super();

		int preferedSize = 500;

		bevel = new BevelBorder(BevelBorder.RAISED);
		empty = new EmptyBorder(5, 5, 5, 5);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		// Numbers of valide image
		int count = 0;

		// Path to the folder containing pictures
		File path = new File("images");
		// Contain all files in the folder
		File[] file = path.listFiles();

		
		// Loop to count how many pictures with the extensions desired
		for (int i = 0; i < file.length; i++) {
			if (isAPicture(file[i])) {
				count++;
			}
		}

		setSize(preferedSize, preferedSize);
		setLocationRelativeTo(null);
		this.setTitle("Diaporama");

		// set the layout
		panel1.setLayout(new BorderLayout());
		panel2.setLayout(new GridLayout(count / 3, 3));
		panel4.setLayout(new BorderLayout());
		panel6.setLayout(new BorderLayout());

		panel1.add(panel3, BorderLayout.SOUTH);
		panel4.add(panel5, BorderLayout.SOUTH);
		panel6.add(panel7, BorderLayout.SOUTH);

		listIconThumb = new ImageIcon[count];
		listIconDiapo = new ImageIcon[count];
		
		//Fill all my list with icons
		for (int i = 0; i < file.length; i++) {
			if (isAPicture(file[i])) {
				ImageIcon imageIcon = new ImageIcon(file[i].getPath());

				Image image = imageIcon.getImage();
				Image newimg = image.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH);
				imageIcon = new ImageIcon(newimg);
				listIconThumb[i] = imageIcon;

				Image imageDiapo = image.getScaledInstance(500, 450, java.awt.Image.SCALE_SMOOTH);
				listIconDiapo[i] = new ImageIcon(imageDiapo);

				JLabel label = new JLabel(imageIcon, JLabel.CENTER);
				label.setIcon(imageIcon);
				label.setToolTipText(file[i].getName());
				label.addMouseListener(new myMouseListener());
				label.setBorder(empty);
				panel2.add(label);

			}
		}

		// Adding components to labels
		panel1.add(panel2, BorderLayout.CENTER);
		panel3.add(slow);
		panel3.add(start);
		panel3.add(stop);
		panel3.add(fast);

		// Initialisation buttons
		slow.setEnabled(false);
		stop.setEnabled(false);
		fast.setEnabled(false);

		// Initialisation CardLayout
		deck.add(panel1, "Thumbnail");
		deck.add(panel4, "Diaporama");
		deck.add(panel6, "Pause");
		add(deck);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		// Button <<
		slow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((time == 1000 && sens == 1) || (time == 2000 && sens == 1)) {
					time = time + 1000;
				} else if (time == 3000 && sens == 1) {
					sens = 0;
				} else if (time > 1000 && sens == 0) {
					time = time - 1000;
				}
				timer.setDelay(time);
				timer.start();
			}
		});

		// Button Start
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				if (selection != null) {
					JLabel temp = (JLabel) selection;
					findImageDiapoFromThumb(listIconThumb, listIconDiapo, label, temp);
					selection = null;

				} else if (inDiapo > 0) {
					Component cp = panel6.getComponent(1);
					JLabel temp = (JLabel) cp;
					Icon ii = temp.getIcon();
					label.setIcon(ii);

				} else {
					ImageIcon imageIcon = listIconDiapo[0];
					label.setIcon(imageIcon);
				}

				inDiapo++;
				panel4.add(label, BorderLayout.CENTER);
				panel5.add(slow);
				panel5.add(pause);
				panel5.add(stop);
				panel5.add(fast);
				slow.setEnabled(true);
				stop.setEnabled(true);
				fast.setEnabled(true);
				pause.setEnabled(true);
				card.show(deck, "Diaporama");
				timer.start();

			}
		});

		// Button Stop
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				if (previous != null) {
					previous.setBackground(null);
					previous.setForeground(null);
					previous.setOpaque(false);
				}
				time = 3000;
				sens = 1;
				inDiapo = 0;
				panel3.add(slow);
				panel3.add(start);
				panel3.add(stop);
				panel3.add(fast);
				slow.setEnabled(false);
				stop.setEnabled(false);
				fast.setEnabled(false);
				card.show(deck, "Thumbnail");
			}
		});

		// Button >>
		fast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				if (time > 1000 && sens == 1) {
					time = time - 1000;
				}
				if ((time == 1000 && sens == 0) || (time == 2000 && sens == 0)) {
					time = time + 1000;
				}
				timer.setDelay(time);
				timer.start();
			}
		});

		// Button Pause
		pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				panel6.add(label, BorderLayout.CENTER);
				panel7.add(slow);
				panel7.add(start);
				panel7.add(stop);
				panel7.add(fast);
				slow.setEnabled(false);
				stop.setEnabled(false);
				fast.setEnabled(false);
				card.show(deck, "Pause");
			}
		});

	}

	// Function to choose the next image
	public void nextImageDiapo(ImageIcon[] listIconDiapo, JLabel label) {
		int index = 0;
		Boolean bool = true;
		while (bool) {
			if (label.getIcon() == listIconDiapo[index]) {
				if (index + 1 == listIconDiapo.length) {
					label.setIcon(listIconDiapo[0]);
					bool = false;
				} else {
					label.setIcon(listIconDiapo[index + 1]);
					bool = false;
				}
			}
			index++;
		}
	}

	// Function to choose the precedent image if going backward
	public void precImageDiapo(ImageIcon[] listIconDiapo, JLabel label) {
		int index = listIconDiapo.length - 1;
		Boolean bool = true;
		while (bool) {
			if (label.getIcon() == listIconDiapo[index]) {
				if (index == 0) {
					label.setIcon(listIconDiapo[listIconDiapo.length - 1]);
					bool = false;
				} else {
					label.setIcon(listIconDiapo[index - 1]);
					bool = false;
				}
			}
			index--;
		}
	}

	//Search the corresponding IconDiapo of IconThumb and store it in label;
	public void findImageDiapoFromThumb(ImageIcon[] listIconThumb, ImageIcon[] listIconDiapo, JLabel label,
			JLabel temp) {
		int index = 0;
		Boolean bool = true;
		while (bool) {
			if (temp.getIcon() == listIconThumb[index]) {
				label.setIcon(listIconDiapo[index]);
				bool = false;
			}
			index++;
		}
	}

	//Verify if the file is a picture. You can add any extension desired
	public Boolean isAPicture(File file) {
		Boolean validate = false;
		if (file.isFile() && (file.getName().endsWith(".png") || file.getName().endsWith(".jpg")
				|| file.getName().endsWith(".svg"))) {
			validate = true;
		}
		return validate;
	}

	class myMouseListener implements MouseListener {
		private boolean doubleClick;
		JLabel label = new JLabel();;

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				selection = null;
				Component source = e.getComponent();
				selection = e.getComponent();
				if (e.getClickCount() == 2) {
					doubleClick = true;
				}

				Timer timeClick = new Timer(50, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (doubleClick) {
							doubleClick = false;
							start.doClick();

						} else {
							// 1 click -> Highlight component
							if (!(source instanceof JLabel)) {
								return;
							}
							label = (JLabel) source;
							if (previous != null) {
								previous.setBackground(null);
								previous.setForeground(null);
								previous.setOpaque(false);
							}

							previous = label;
							label.setForeground(Color.WHITE);
							label.setBackground(Color.lightGray);
							label.setOpaque(true);
						}
					}

				});
				timeClick.setRepeats(false);
				timeClick.start();
				if (e.getID() == MouseEvent.MOUSE_RELEASED)
					timeClick.stop();
			}

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	// Driver code
	public static void main(String[] args) {

		// calling the constructor
		affichage cardLayoutTest = new affichage();
		cardLayoutTest.setVisible(true);
	}
}
