package spring.generated.login.controller;

import java.lang.String;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import spring.generated.forms.LoginValidateForm;
import spring.mine.common.controller.BaseController;
import spring.mine.common.form.BaseForm;
import spring.mine.common.validator.BaseErrors;

@Controller
public class ValidateLoginController extends BaseController {
  @RequestMapping(
      value = "/ValidateLogin",
      method = RequestMethod.GET
  )
  public ModelAndView showValidateLogin(HttpServletRequest request) {
    String forward = FWD_SUCCESS;
    LoginValidateForm form = new LoginValidateForm();
    form.setFormName("loginValidateForm");
    form.setFormAction("");
    BaseErrors errors = new BaseErrors();
    ModelAndView mv = checkUserAndSetup(form, errors, request);

    if (errors.hasErrors()) {
    	return mv;
    }

    return findForward(forward, form);}

  protected ModelAndView findLocalForward(String forward, BaseForm form) {
    if ("success".equals(forward)) {
      return new ModelAndView("/HomePage.do", "form", form);
    } else if ("fail".equals(forward)) {
      return new ModelAndView("/LoginPage.do", "form", form);
    } else if ("changePassword".equals(forward)) {
      return new ModelAndView("/ChangePasswordLogin.do", "form", form);
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
