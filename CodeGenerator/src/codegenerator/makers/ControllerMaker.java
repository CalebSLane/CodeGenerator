package codegenerator.makers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import codegenerator.strutsconcepts.Action;
import codegenerator.strutsconcepts.Forward;
import spring.mine.common.controller.BaseController;
import spring.mine.common.form.BaseForm;
import spring.mine.common.validator.BaseErrors;

public class ControllerMaker {

	// each action makes one controller, this can be changed so we put multiple
	// actions in one controller
	// this would complicate findlocalforward so it has not been done yet

	public void make(List<Action> actionList, String generatedPackagePrefix, String outputFilepath) {

		System.out.println("Making controllers...");
		Map<String, TypeSpec.Builder> classesList = new HashMap<String, Builder>();
		for (Action action : actionList) {
			// strip leading slash and append controller
			String ctrlClassName = action.getPath().replaceAll("^/+", "") + "Controller";
			// ctrlClassName = removeCommonPhrases(ctrlClassName);
			// strip unneccesary us.mn.state.health.lims. from classpath and add .controller
			// to classpath
			String ctrlClassPath = (action.getType().indexOf(".action") > 0)
					? action.getType().substring("us.mn.state.health.lims.".length(),
							action.getType().lastIndexOf(".action")) + ".controller"
					: action.getType().substring("us.mn.state.health.lims.".length(), action.getType().lastIndexOf("."))
							+ ".controller";

			// make methods for controller class
			MethodSpec mainMethod = createMainMethod(action);
			MethodSpec findForward = createFindLocalForward(action);
			TypeSpec.Builder jClassBuilder;
			if (classesList.containsKey(ctrlClassPath + ":" + ctrlClassName)) {
				jClassBuilder = classesList.get(ctrlClassPath + ":" + ctrlClassName);
				jClassBuilder.addMethod(mainMethod);
			} else {
				// make controller class
				jClassBuilder = TypeSpec.classBuilder(ctrlClassName)
						.addJavadoc("Status: generated")
						.addModifiers(Modifier.PUBLIC).addMethod(mainMethod)
						.addMethod(findForward).addAnnotation(Controller.class).superclass(BaseController.class)
						.addMethod(MethodSpec.methodBuilder("getPageTitleKey").addModifiers(Modifier.PROTECTED)
								.returns(String.class).addStatement("return null").build())
						.addMethod(MethodSpec.methodBuilder("getPageSubtitleKey").addModifiers(Modifier.PROTECTED)
								.returns(String.class).addStatement("return null").build());
				classesList.put(ctrlClassPath + ":" + ctrlClassName, jClassBuilder);
			}
		}
		writeClasses(classesList, generatedPackagePrefix, outputFilepath);
	}

	private MethodSpec createMainMethod(Action action) {
		String methodName = "show" + action.getPath().replaceAll("^/+", "");
		String newFormClassName = action.getName().substring(0, 1).toUpperCase() + action.getName().substring(1);
		ParameterSpec formParameter = ParameterSpec.builder(ClassName.get("spring.generated.forms", newFormClassName), "form")
				.addAnnotation(AnnotationSpec.builder(ModelAttribute.class).addMember("value", "\"form\"")
						.build()).build();
		
		// make RequestMapping method
		return MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(ModelAndView.class)
				.addParameter(HttpServletRequest.class, "request")
				.addParameter(formParameter)
				.addAnnotation(
						AnnotationSpec.builder(RequestMapping.class).addMember("value", "\"" + action.getPath() + "\"")
								.addMember("method", "$T.GET", RequestMethod.class).build())
				.addStatement("$T forward = FWD_SUCCESS", String.class)
				.addCode("if (form == null) {\n")
				.addStatement("\tform = new $T()", ClassName.get("spring.generated.forms", newFormClassName))
				.addCode("}\n")
				.addStatement("form.setFormName($S)", action.getName()).addStatement("form.setFormAction(\"\")")
				.addStatement("$T errors = new BaseErrors()", BaseErrors.class)
				.addCode("if (form.getErrors() != null) {\n")
				.addStatement("\terrors = (BaseErrors) form.getErrors()")
				.addCode("}\n")
				.addStatement("ModelAndView mv = checkUserAndSetup(form, errors, request)")
				.addCode("\nif (errors.hasErrors()) {\n" + "\treturn mv;\n" + "}\n\n")
				.addCode("return findForward(forward, form);").build();
	}

	private MethodSpec createFindLocalForward(Action action) {
		/// make findForward equivalent method
		MethodSpec.Builder jMethodBuilder = MethodSpec.methodBuilder("findLocalForward")
				.addModifiers(Modifier.PROTECTED).returns(ModelAndView.class).addParameter(String.class, "forward")
				.addParameter(BaseForm.class, "form");

		boolean firstTime = true;
		for (Forward forward : action.getForwardsList()) {
			if (firstTime) {
				jMethodBuilder.addCode("if (\"" + forward.getName() + "\".equals(forward)) {\n");
				firstTime = false;
			} else {
				jMethodBuilder.addCode(" else if (\"" + forward.getName() + "\".equals(forward)) {\n");
			}
			if (forward.getPath().startsWith("\\")) {
				jMethodBuilder.addCode("  return new ModelAndView(\"redirect:" + forward.getPath() + "\");\n}");
			} else {
				jMethodBuilder.addCode("  return new ModelAndView(\"" + forward.getPath() + "\", \"form\", form);\n}");
			}

		}
		jMethodBuilder.addCode(" else {\n" + "  return new ModelAndView(\"PageNotFound\");\n}\n");
		return jMethodBuilder.build();
	}

	// write all classes to files
	private void writeClasses(Map<String, TypeSpec.Builder> classesList, String generatedPackagePrefix,
			String outputFilepath) {
		// create java file with proper package name
		for (String key : classesList.keySet()) {
			TypeSpec jClass = classesList.get(key).build();
			JavaFile javaFile = JavaFile
					.builder(generatedPackagePrefix + "." + key.substring(0, key.lastIndexOf(':')), jClass).build();
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

	/*
	 * private String removeCommonPhrases(String classType) { classType =
	 * classType.replace("Update", ""); classType = classType.replace("Cancel", "");
	 * classType = classType.replace("Delete", ""); classType =
	 * classType.replace("Previous", ""); classType = classType.replace("Next", "");
	 * classType = classType.replace("Exit", ""); classType =
	 * classType.replace("Save", ""); return classType; }
	 */

}
