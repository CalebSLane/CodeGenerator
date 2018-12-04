package codegenerator.main;

import codegenerator.makers.BeanMaker;
import codegenerator.makers.ControllerMaker;
import codegenerator.readers.ConfigurationReader;

public class App {
	public static void main(String[] args) {
		
		String generatedPackagePrefix = "spring.generated";
		String outputFilepath = ".\\src\\";
		ConfigurationReader configReader = new ConfigurationReader();
		if (args.length == 0 ) {
			System.out.println("Require command line argument path to openelis top level directory");
		}
		configReader.readConfigFiles(args[0] + "\\app\\WebContent\\WEB-INF", "struts-globalOpenELIS.xml");
		configReader.readConfigFiles(args[0] + "\\app\\WebContent\\WEB-INF", "struts-config.xml");

		BeanMaker beanMaker = new BeanMaker();
		beanMaker.make(configReader.getFormBeanList(), generatedPackagePrefix, outputFilepath);

		ControllerMaker controllerMaker = new ControllerMaker();
		controllerMaker.make(configReader.getActionList(), generatedPackagePrefix, outputFilepath);

		System.out.println("Done");
	}

}
