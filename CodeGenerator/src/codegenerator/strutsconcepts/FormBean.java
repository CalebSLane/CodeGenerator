package codegenerator.strutsconcepts;

import java.util.List;

public class FormBean {
	String name;
	List<FormProperty> propertyList;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<FormProperty> getPropertyList() {
		return propertyList;
	}
	public void setPropertyList(List<FormProperty> propertyList) {
		this.propertyList = propertyList;
	}

}
