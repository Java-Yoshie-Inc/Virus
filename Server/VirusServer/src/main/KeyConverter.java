package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyConverter implements KeyListener {
	
	private StringBuilder sb = new StringBuilder();
	private final Key[] KEYS = new Key[] {
			new Key(KeyEvent.VK_SPACE, " "), 
			new Key(KeyEvent.VK_BACK_SPACE, "{bksp}")  , 
			new Key(KeyEvent.VK_CAPS_LOCK, "{capslock}"), 
			new Key(KeyEvent.VK_ENTER, "{enter}"), 
			new Key(KeyEvent.VK_END, "{ende}"), 
			new Key(KeyEvent.VK_ESCAPE, "{esc}"), 
			new Key(KeyEvent.VK_WINDOWS, "^{esc}"), 
			new Key(KeyEvent.VK_LEFT, "{left}"), 
			new Key(KeyEvent.VK_RIGHT, "{right}"), 
			new Key(KeyEvent.VK_UP, "{up}"), 
			new Key(KeyEvent.VK_DOWN, "{down}"), 
			new Key(KeyEvent.VK_TAB, "{tab}"), 
			new Key(KeyEvent.VK_NUM_LOCK, "{num}"), 
			new Key(KeyEvent.VK_DELETE, "{del}"), 
	};
	private final String CHAR_KEYS = "1234567890?!\"�$%&/()=?`�{[]}\\*+~'#_-:.;,�><|^�";
	
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
				return s.substring(0, i-1);
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
			for(Key key : this.KEYS) {
				System.out.println(e.getKeyCode() + " " + key.getJavaKeyCode());
				if(e.getKeyCode() == key.getJavaKeyCode()) {
					sb.append(key.getCKeyCode());
					return;
				}
			}
			
			if(Character.isAlphabetic(e.getKeyChar()) || CHAR_KEYS.contains(String.valueOf(e.getKeyChar()))) {
				sb.append(e.getKeyChar());
			} else if(KeyEvent.getKeyText(e.getKeyCode()).length() == 1 && Character.isAlphabetic(KeyEvent.getKeyText(e.getKeyCode()).charAt(0))) {
				sb.append(KeyEvent.getKeyText(e.getKeyCode()).toLowerCase().charAt(0));
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
