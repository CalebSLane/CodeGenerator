package codegenerator.makers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import codegenerator.strutsconcepts.FormBean;
import codegenerator.strutsconcepts.FormProperty;
import spring.mine.common.form.BaseForm;

// beans may be unnecessary for spring. This was chosen to be included for
// greater similarity betweent he two frameworks
public class BeanMaker {

	public void make(List<FormBean> beanList, String generatedPackagePrefix, String outputFilepath) {
		System.out.println("Making form beans...");
		Map<String, TypeSpec.Builder> classesList = new HashMap<String, Builder>();
		for (FormBean formBean : beanList) {
			String name = formBean.getName();
			String formClassName = name.substring(0, 1).toUpperCase() + name.substring(1);
			TypeSpec.Builder jClassBuilder;
			if (classesList.containsKey(formClassName)) {
				jClassBuilder = classesList.get(formClassName);
			} else {
				jClassBuilder = TypeSpec.classBuilder(formClassName).addModifiers(Modifier.PUBLIC);
				jClassBuilder.addMethod(createConstructor(name));
			}
			for (FormProperty property : formBean.getPropertyList()) {
				try {
					FieldSpec jField = createField(property);
					MethodSpec jMethodGet = createGetter(property); // can omit if we use lombok
					MethodSpec jMethodSet = createSetter(property); // can omit if we use lombok
					jClassBuilder.addField(jField).addMethod(jMethodGet).addMethod(jMethodSet);
				} catch (ClassNotFoundException e) {
					System.out.println("Failure: could not process formProperty " + property.getName() + ": "
							+ property.getType() + " ClassNotFound");
					System.out.println("Skipping...");
				}
			}
			classesList.put(formClassName, jClassBuilder);
		}
		writeClasses(classesList, generatedPackagePrefix, outputFilepath);
	}

	private FieldSpec createField(FormProperty property) throws ClassNotFoundException {
		String baseType = property.getType().replace("[]", "");
		FieldSpec jField;
		if (!property.getType().equals("java.lang.String[]")) {
			if (baseType.equals("boolean")) {// might cause issue
				jField = FieldSpec.builder(TypeName.BOOLEAN, property.getName(), Modifier.PRIVATE).build();
			}else if (property.getInitial() == null || property.getType().contains("Timestamp")) {
				jField = FieldSpec.builder(Class.forName(baseType), property.getName(), Modifier.PRIVATE).build();
			} else {
				if (Class.forName(baseType).equals(String.class)) {
					jField = FieldSpec.builder(Class.forName(baseType), property.getName(), Modifier.PRIVATE)
							.initializer("$S", property.getInitial()).build();
				} else {
					jField = FieldSpec.builder(Class.forName(baseType), property.getName(), Modifier.PRIVATE)
							.initializer(property.getInitial()).build();
				}
			}
		} else {
			if (property.getInitial() == null) {
				jField = FieldSpec.builder(String[].class, property.getName(), Modifier.PRIVATE).build();
			} else {
				jField = FieldSpec.builder(String[].class, property.getName(), Modifier.PRIVATE)
						.initializer(property.getInitial()).build();
			}
		}
		return jField;
	}

	private MethodSpec createGetter(FormProperty property) throws ClassNotFoundException {
		String baseType = property.getType().replace("[]", "");
		String upperName = property.getName().substring(0, 1).toUpperCase() + property.getName().substring(1);
		MethodSpec jMethod;
		if (baseType.equals(property.getType())) {
			if (baseType.equals("boolean")) {
				jMethod = MethodSpec.methodBuilder("is" + upperName).addModifiers(Modifier.PUBLIC)
						.returns(TypeName.BOOLEAN).addCode("return this." + property.getName() + ";\n").build();
			} else {
				jMethod = MethodSpec.methodBuilder("get" + upperName).addModifiers(Modifier.PUBLIC)
						.returns(Class.forName(baseType)).addCode("return this." + property.getName() + ";\n").build();
			}
		} else {
			jMethod = MethodSpec.methodBuilder("get" + upperName).addModifiers(Modifier.PUBLIC).returns(String[].class)
					.addCode("return this." + property.getName() + ";\n").build();
		}
		return jMethod;
	}

	private MethodSpec createSetter(FormProperty property) throws ClassNotFoundException {
		String baseType = property.getType().replace("[]", "");
		String upperName = property.getName().substring(0, 1).toUpperCase() + property.getName().substring(1);
		MethodSpec jMethod;
		if (baseType.equals(property.getType())) {
			if (baseType.equals("boolean")) {
				jMethod = MethodSpec.methodBuilder("set" + upperName).addModifiers(Modifier.PUBLIC)
						.addParameter(ParameterSpec.builder(TypeName.BOOLEAN, property.getName()).build())
						.addCode("this." + property.getName() + " = " + property.getName() + ";\n").build();
			} else {
				jMethod = MethodSpec.methodBuilder("set" + upperName).addModifiers(Modifier.PUBLIC)
						.addParameter(ParameterSpec.builder(Class.forName(baseType), property.getName()).build())
						.addCode("this." + property.getName() + " = " + property.getName() + ";\n").build();
			}
		} else {
			jMethod = MethodSpec.methodBuilder("set" + upperName).addModifiers(Modifier.PUBLIC)
					.addParameter(ParameterSpec.builder(String[].class, property.getName()).build())
					.addCode("this." + property.getName() + " = " + property.getName() + ";\n").build();
		}
		return jMethod;
	}
	
	private MethodSpec createConstructor(String name) {
		MethodSpec constructor = MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC)
				.addStatement("setFormName(\"" + name + "\")")
				.build();
		return constructor;
		
	}

	private void writeClasses(Map<String, TypeSpec.Builder> classesList, String generatedPackagePrefix,
			String outputFilepath) {
		for (String key : classesList.keySet()) {
			TypeSpec jClass = classesList.get(key).superclass(BaseForm.class).build();
			JavaFile javaFile = JavaFile.builder(generatedPackagePrefix + ".forms", jClass).build();
			try {
				File file = new File(outputFilepath);
				javaFile.writeTo(file);
			} catch (IOException e) {
				// e.printStackTrace();
				System.out.println("Failure: writing " + key);
			}
			System.out.println("Success: writing " + key);
		}
	}
}
