package codegenerator.readers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import codegenerator.strutsconcepts.Action;
import codegenerator.strutsconcepts.FormBean;
import codegenerator.strutsconcepts.Forward;
import codegenerator.strutsconcepts.FormProperty;

public class ConfigurationReader {

	List<FormBean> formBeanList = new ArrayList<FormBean>();
	List<Action> actionList = new ArrayList<Action>();

	public void readConfigFiles(String path, String filename) {
		System.out.println("Reading config file " + filename + "...");
		File strutsConfigFile = new File(path + "\\" + filename);
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(strutsConfigFile);
			doc.getDocumentElement().normalize();

			readActions(doc);
			readBeans(doc);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("could not read file");
		}
	}

	private void readBeans(Document doc) {
		// get form beans in struts file
		System.out.println("Reading form beans...");
		NodeList formBeanXMLList = doc.getElementsByTagName("form-bean");
		for (int i = 0; i < formBeanXMLList.getLength(); ++i) {
			Element formBeanXML = (Element) formBeanXMLList.item(i);
			if (formBeanXML.hasAttribute("name")) {
				FormBean formBean = new FormBean();
				ArrayList<FormProperty> formPropertyList = new ArrayList<FormProperty>();
				NodeList formPropertyXMLList = formBeanXML.getElementsByTagName("form-property");
				for (int j = 0; j < formPropertyXMLList.getLength(); ++j) {
					Element formPropertyXML = (Element) formPropertyXMLList.item(j);
					FormProperty formProperty = new FormProperty();
					formProperty.setName(formPropertyXML.getAttribute("name"));
					formProperty.setType(formPropertyXML.getAttribute("type"));
					if (formPropertyXML.hasAttribute("initial")) {
						formProperty.setInitial(formPropertyXML.getAttribute("initial"));
					}
					formPropertyList.add(formProperty);
				}
				formBean.setName(formBeanXML.getAttribute("name"));
				formBean.setPropertyList(formPropertyList);
				formBeanList.add(formBean);
			}
		}
	}

	private void readActions(Document doc) {
		// get actions in struts file
		System.out.println("Reading actions...");
		NodeList actionslist = doc.getElementsByTagName("action");
		for (int i = 0; i < actionslist.getLength(); ++i) {
			Element actionXML = (Element) actionslist.item(i);
			NodeList actionForwardsList = actionXML.getElementsByTagName("forward");
			if (actionXML.hasAttribute("path") && actionXML.hasAttribute("type")) {
				Action action = new Action();
				ArrayList<Forward> forwardsList = new ArrayList<Forward>();
				for (int j = 0; j < actionForwardsList.getLength(); ++j) {
					Element actionForward = (Element) actionForwardsList.item(j);
					Forward forward = new Forward();
					forward.setName(actionForward.getAttribute("name"));
					forward.setPath(actionForward.getAttribute("path"));
					forwardsList.add(forward);
				}
				action.setName(actionXML.getAttribute("name"));
				action.setPath(actionXML.getAttribute("path"));
				action.setType(actionXML.getAttribute("type"));
				action.setForwardsList(forwardsList);
				actionList.add(action);
			}
		}
	}

	public List<FormBean> getFormBeanList() {
		return formBeanList;
	}

	public void setFormBeanList(List<FormBean> formBeanList) {
		this.formBeanList = formBeanList;
	}

	public List<Action> getActionList() {
		return actionList;
	}

	public void setActionList(List<Action> actionList) {
		this.actionList = actionList;
	}

}
