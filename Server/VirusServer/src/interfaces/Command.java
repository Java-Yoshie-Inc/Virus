package interfaces;

public class Command {
	
	private final String type;
	private final ParameterGetter parameterGetter;
	
	public Command(String type, String data) {
		this.type = type;
		this.parameterGetter = new ParameterGetter() {
			@Override
			public String get() {
				return data;
			}
		};
	}
	
	public Command(String type, ParameterGetter parameterGetter) {
		this.type = type;
		this.parameterGetter = parameterGetter;
	}
	
	public String getType() {
		return type;
	}
	public String getData() {
		return parameterGetter.get();
	}
	
	@Override
	public String toString() {
		return type;
	}
	
}
