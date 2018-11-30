package codegenerator.strutsconcepts;

import java.util.List;

public class Action {
	String name;
	String path;
	String type;
	List<Forward> forwardsList;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Forward> getForwardsList() {
		return forwardsList;
	}
	public void setForwardsList(List<Forward> forwardsList) {
		this.forwardsList = forwardsList;
	}

}
