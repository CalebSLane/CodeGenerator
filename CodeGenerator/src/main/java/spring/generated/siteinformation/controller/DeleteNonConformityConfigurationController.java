package spring.generated.siteinformation.controller;

import java.lang.String;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import spring.generated.forms.NonConformityConfigurationMenuForm;
import spring.mine.common.controller.BaseController;
import spring.mine.common.form.BaseForm;
import spring.mine.common.validator.BaseErrors;

@Controller
public class DeleteNonConformityConfigurationController extends BaseController {
  @RequestMapping(
      value = "/DeleteNonConformityConfiguration",
      method = RequestMethod.GET
  )
  public ModelAndView showDeleteNonConformityConfiguration(HttpServletRequest request) {
    String forward = FWD_SUCCESS;
    NonConformityConfigurationMenuForm form = new NonConformityConfigurationMenuForm();
    form.setFormName("NonConformityConfigurationMenuForm");
    form.setFormAction("");
    BaseErrors errors = new BaseErrors();
    ModelAndView mv = checkUserAndSetup(form, errors, request);

    if (errors.hasErrors()) {
    	return mv;
    }

    return findForward(forward, form);}

  protected ModelAndView findLocalForward(String forward, BaseForm form) {
    if ("success".equals(forward)) {
      return new ModelAndView("/NonConformityConfiguration.do", "form", form);
    } else if ("fail".equals(forward)) {
      return new ModelAndView("/NonConformityConfiguration.do", "form", form);
    } else {
      return new ModelAndView("PageNotFound");
    }
  }

  protected String getPageTitleKey() {
    return null;
  }

  protected String getPageSubtitleKey() {
    return null;
  }
}
