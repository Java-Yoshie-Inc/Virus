package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyConverter implements KeyListener {
	
	private StringBuilder sb = new StringBuilder();
	
	private static final String CONTROL = "^";
	private static final String SHIFT = "+";
	private static final String ALT = "%";
	
	private boolean pressControl, pressShift, pressAlt;
	
	
	public KeyConverter() {
		
	}
	
	@Override
	public String toString() {
		return this.getCompleteKeys();
	}
	
	private String getCompleteKeys() {
		String s = sb.toString();
		for(int i=s.length()-1; i>0; i--) {
			char c = s.charAt(i);
			if(c == ')') {
				break;
			} else if(c == '(') {
				return s.substring(0, i);
			}
		}
		return s;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_CONTROL && !pressControl) {
			sb.append(CONTROL + "(");
			pressControl = true;
		} else if(e.getKeyCode() == KeyEvent.VK_SHIFT && !pressShift) {
			sb.append(SHIFT + "(");
			pressShift = true;
		} else if(e.getKeyCode() == KeyEvent.VK_ALT && !pressAlt) {
			sb.append(ALT + "(");
			pressAlt = true;
		} else {
			System.out.println(e.getKeyChar());
			System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
			if(Character.isAlphabetic(e.getKeyChar()) || Character.isAlphabetic(KeyEvent.getKeyText(e.getKeyCode()))) {
				sb.append(e.getKeyChar());
			} else {
				System.out.println("Unknown Character");
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_CONTROL || e.getKeyCode() == KeyEvent.VK_ALT || e.getKeyCode() == KeyEvent.VK_SHIFT) {
			sb.append(")");
		} if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
			pressControl = false;
		} else if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
			pressShift = false;
		} else if(e.getKeyCode() == KeyEvent.VK_ALT) {
			pressAlt = false;
		}
	}
	
	public void reset() {
		String keys = sb.toString().substring(getCompleteKeys().length(), sb.length());
		sb = new StringBuilder();
		sb.append(keys);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

}
