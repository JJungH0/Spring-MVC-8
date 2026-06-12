package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login/loginForm";
    }

//    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm loginForm,
                        BindingResult bindingResult,
                        HttpServletResponse resp) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if (Objects.isNull(loginMember)) {
            bindingResult.reject("loginFail", "ID 또는 PW가 일치하지 않습니다.");
            return "login/loginForm";
        }

        /**
         * 로그인 성공 처리 :
         * - 쿠키에 시간 정보를 주지 않으면 자동으로 세션쿠키 (= 브라우저 종료 시 소멸)
         */
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        resp.addCookie(idCookie);

        return "redirect:/";
    }

//    @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute("loginForm") LoginForm loginForm,
                        BindingResult bindingResult,
                        HttpServletResponse resp) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if (Objects.isNull(loginMember)) {
            bindingResult.reject("loginFail", "ID 또는 PW가 일치하지 않습니다.");
            return "login/loginForm";
        }

        /**
         * 로그인 성공 처리 :
         * - 쿠키에 시간 정보를 주지 않으면 자동으로 세션쿠키 (= 브라우저 종료 시 소멸)
         * - 세션 관리자를 통해 세션을 생성하고, 회원 데이터 보관
         */
        sessionManager.createSession(loginMember, resp);

        return "redirect:/";
    }

//    @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute("loginForm") LoginForm loginForm,
                          BindingResult bindingResult,
                          HttpServletRequest req) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if (Objects.isNull(loginMember)) {
            bindingResult.reject("loginFail", "ID 또는 PW가 일치하지 않습니다.");
            return "login/loginForm";
        }

        /**
         * 로그인 성공 처리 :
         * - 세션이 존재한다면 존재하는 세션을 반환, 없다면 신규 생성
         * - getSession(default=true)
         */
        HttpSession session = req.getSession();

        /**
         * 세션에 로그인 회원 정보 보관
         */
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginV4(@Valid @ModelAttribute("loginForm") LoginForm loginForm,
                          BindingResult bindingResult,
                          @RequestParam(defaultValue = "/") String redirectURL,
                          HttpServletRequest req) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if (Objects.isNull(loginMember)) {
            bindingResult.reject("loginFail", "ID 또는 PW가 일치하지 않습니다.");
            return "login/loginForm";
        }

        /**
         * 로그인 성공 처리 :
         * - 세션이 존재한다면 존재하는 세션을 반환, 없다면 신규 생성
         * - getSession(default=true)
         */
        HttpSession session = req.getSession();

        /**
         * 세션에 로그인 회원 정보 보관
         */
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:" + redirectURL;
    }

//    @PostMapping("/logout")
    public String logout(HttpServletResponse resp) {
        expireCookie(resp, "memberId");
        return "redirect:/";
    }

//    @PostMapping("/logout")
    public String logoutV2(HttpServletRequest req, HttpServletResponse resp) {
        sessionManager.expire(req);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession(false);
        if (Objects.nonNull(session)) {
            session.invalidate();
        }
        return "redirect:/";
    }

    private static void expireCookie(HttpServletResponse resp, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }
}
