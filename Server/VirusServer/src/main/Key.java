package main;

public class Key {
	
	private final int javaKeyCode;
	private final String cKeyCode;
	
	public Key(int javaKeyCode, String cKeyCode) {
		this.javaKeyCode = javaKeyCode;
		this.cKeyCode = cKeyCode.toUpperCase();
	}

	public int getJavaKeyCode() {
		return javaKeyCode;
	}
	public String getCKeyCode() {
		return cKeyCode;
	}
	
}
