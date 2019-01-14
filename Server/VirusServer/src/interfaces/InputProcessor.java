package interfaces;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;

import javax.imageio.ImageIO;

import main.ClientData;
import main.Controller;
import main.Server;
import tools.Logger;
import tools.Logger.Level;

public class InputProcessor {
	
	private final Controller controller;
	private final Server server;
	
	private ClientData client;
	private boolean sendResponse = true;
	
	public InputProcessor(Controller controller, Server server, String input) {
		this.controller = controller;
		this.server = server;
		process(input);
	}
	
	private void process(String entireInput) {
		String[] inputs = entireInput.substring(1, entireInput.length()-1).split("\\]\\,\\[");
		
		for(String input : inputs) {
			String type = input.split(Server.SEPERATOR)[0];
			String data = input.split(Server.SEPERATOR)[1];
			
			try {
				this.getClass().getDeclaredMethod(type, data.getClass()).invoke(this, data);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				Logger.log(e.getMessage(), Level.ERROR);
			}
		}
		
		controller.getScreenPanel().repaint();
	}
	
	@SuppressWarnings("unused")
	private void id(String s) {
		this.server.loginClient(s);
		this.client = server.getClient(s);
	}
	
	@SuppressWarnings("unused")
	private void response(String s) {
		this.sendResponse = Boolean.valueOf(s);
	}
	
	@SuppressWarnings("unused")
	private void webcam(String s) {
		try {
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(s.getBytes())));
			controller.setWebcamImage(image);
		} catch (IOException e) {
			Logger.log(e.getMessage(), Level.ERROR);
		}
	}
	
	@SuppressWarnings("unused")
	private void screenshot(String s) {
		try {
			BufferedImage screenshot = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(s.getBytes())));
			controller.setScreenshot(screenshot);
		} catch (IOException e) {
			Logger.log(e.getMessage(), Level.ERROR);
		}
	}
	
	@SuppressWarnings("unused")
	private void msg(String s) {
		Logger.log(s.replace(Server.LINE_SEPERATOR, System.lineSeparator()));
	}
	
	public ClientData getClient() {
		return client;
	}
	
	public boolean sendResponse() {
		return sendResponse;
	}
	
}
