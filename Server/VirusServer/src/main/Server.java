package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import interfaces.ClientTasks;
import interfaces.InputProcessor;
import tools.Logger;
import tools.Logger.Level;

public class Server {

	private HttpServer server;
	private Timer loop;
	private final Controller controller;

	private final int PORT = 1234;
	private final int HTTP_OK_STATUS = 200;
	private final int CLIENT_TIMEOUT = 8000;
	
	public static final String SEPERATOR = ":::";
	public static final String LINE_SEPERATOR = "%LS%";

	private ArrayList<ClientData> clients = new ArrayList<ClientData>();
	
	public Server(Controller controller) throws IOException {
		this.controller = controller;
		setup();
		loop();
	}

	public void setup() throws IOException {
		Logger.log("Initialize Server...");
		
		try {
			server = HttpServer.create(new InetSocketAddress(PORT), 0);
		} catch (BindException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), controller.getName(), JOptionPane.ERROR_MESSAGE);
		}
		
		HttpHandler login = new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				try {
					InputStream in = arg.getRequestBody();
					String input = readInputStream(in);
					new InputProcessor(controller, Server.this, input);
					
					String response = "OK";
					arg.sendResponseHeaders(HTTP_OK_STATUS, response.length());
					OutputStream output = arg.getResponseBody();
					output.write(response.getBytes());
					output.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		HttpHandler update = new HttpHandler() {
			public void handle(HttpExchange arg) throws IOException {
				try {
					InputStream in = arg.getRequestBody();
					String input = readInputStream(in);
					InputProcessor inputProcessor = new InputProcessor(controller, Server.this, input);
					ClientData client = inputProcessor.getClient();
					
					String response = "";
					if(inputProcessor.sendResponse()) {
						if (client != null) {
							client.update();
							
							ClientTasks tasks = client.getTasks();
							if(controller.getClientDropdown().getSelectedItem().equals(client)) {
								if(controller.getScreenshotButton().isSelected()) {
									tasks.requestScreenshot(true);
								} if(controller.getMouseMovingButton().isSelected()) {
									float x = controller.getClientScreenMousePositionPercentage().x/100f;
									float y = controller.getClientScreenMousePositionPercentage().y/100f;
									
									tasks.moveMouse(x, y);
									if(controller.isMousePressed()) {
										controller.setMousePressed(false);
										tasks.clickMouse(0, x, y);
									}
									if(!controller.getKeyListener().toString().isEmpty()) {
										tasks.pressKey(controller.getKeyListener().toString());
										controller.getKeyListener().reset();
									}
								} if(controller.getTransferFile() != null) {
									tasks.copyFile(controller.getTransferFile().getPath());
									controller.setTransferFile(null);
								}
							}
							response = tasks.toString();
							tasks.clear();
							
							//System.out.println(response);
						} else {
							Logger.log("Client not found", Level.ERROR);
						}
					}
					
					byte[] bytes = response.getBytes();
					arg.sendResponseHeaders(HTTP_OK_STATUS, bytes.length);
					OutputStream output = arg.getResponseBody();
					output.write(bytes);
					output.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		server.createContext("/login", login);
		server.createContext("/update", update);

		server.setExecutor(null);
	}

	public void start() {
		server.start();
		Logger.log("Server successfully started");
	}
	
	public void stop() {
		Logger.log("Stopping Server...");
		server.stop(0);
	}
	
	public void loginClient(String id) {
		String[] inputArgs = id.split(",");
		String name = inputArgs[0];
		String ip = inputArgs[1];
		String local_ip = inputArgs[2];
		ClientData client = new ClientData(name, ip, local_ip);
		if(!containsClient(client)) {
			clients.add(client);
			Logger.log(name + " logged in");
		}
	}

	private void update() {
		checkClientTimeout();
	}

	private void checkClientTimeout() {
		ArrayList<ClientData> toRemove = new ArrayList<ClientData>();
		for (ClientData client : clients) {
			if (client.getUpdateTimeDuration() >= CLIENT_TIMEOUT) {
				toRemove.add(client);
			}
		}
		for (ClientData client : toRemove) {
			Logger.log(client.getName() + " logged out");
			clients.remove(client);
		}
	}

	private void loop() {
		loop = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});
		loop.start();
	}

	private String readInputStream(InputStream in) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			StringBuffer response = new StringBuffer();
			String inputLine;
			while ((inputLine = reader.readLine()) != null) {
				response.append(inputLine);
			}
			reader.close();

			return response.toString();
		} catch (IOException e) {
			Logger.log(e.getMessage(), Level.ERROR);
			return "";
		}
	}

	public ClientData getClient(String id) {
		for (ClientData c : clients) {
			if (c.equals(id)) {
				return c;
			}
		}
		return null;
	}
	
	public boolean containsClient(ClientData client) {
		for(ClientData c : clients) {
			if(c.equals(client.toString())) {
				return true;
			}
		}
		return false;
	}

	public ClientData[] getClients() {
		return this.clients.toArray(new ClientData[0]);
	}

}
