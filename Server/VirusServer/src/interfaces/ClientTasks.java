package interfaces;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import main.Server;

public class ClientTasks {
	
	private String tasks = "";
	
	public ClientTasks() {
		
	}
	
	public void clear() {
		tasks = "";
	}
	
	public void runCommand(String cmd) {
		add("run", cmd);
	}
	
	public void moveMouse(float x, float y) {
		add("mousepos", x+","+y);
	}
	
	public void clickMouse(int button, float x, float y) {
		add("clickmouse", Integer.toString(button) + "," + x + "," + y);
	}
	
	public void blockInputs(boolean block) {
		add("blockinputs", String.valueOf(block));
	}
	
	public void pressMouse(int button) {
		add("pressmouse", Integer.toString(button));
	}
	
	public void copyFile(String file) throws IOException {
		add("copyfile", new File(file).getName() + "," + Base64.getEncoder().encodeToString(new String(Files.readAllBytes(new File(file).toPath())).getBytes()));
	}
	
	public void execute(Command c) {
		add(c.getType(), c.getData());
	}
	
	public void pressKey(String keys) {
		add("keys", keys);
	}
	
	public void pressKey(String... keys) {
		if(keys.length != 0) {
			String s = "";
			for(String key : keys) {
				s += key.toUpperCase() + ",";
			}
			s = s.substring(0, s.length()-1);
			add("keys", s);
		}
	}
	
	public void releaseMouse(int button) {
		add("releasemouse", Integer.toString(button));
	}
	
	public void requestScreenshot(boolean b) {
		add("screenshot", String.valueOf(b).toLowerCase());
	}
	
	private void add(String t, String c) {
		tasks += "[" + t + Server.SEPERATOR + c + "],";
	}
	
	@Override
	public String toString() {
		return tasks;
	}
	
}
