package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor

public class HomeController {
    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    //    @GetMapping("/")
    public String home() {
        return "home";
    }

//    @GetMapping("/")
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
        if (Objects.isNull(memberId)) {
            return "home";
        }
        /**
         * 로그인 :
         */
        Member loginMember = memberRepository.findById(memberId);
        if (Objects.isNull(loginMember)) {
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV2(HttpServletRequest req, Model model) {
        /**
         * 세션 관리자에 저장된 회원 정보 조회 :
         */

        Member member = (Member)sessionManager.getSession(req);

        /**
         * 로그인 :
         */
        if (Objects.isNull(member)) {
            return "home";
        }
        model.addAttribute("member", member);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV3(HttpServletRequest req, Model model) {
        /**
         * 세션 관리자에 저장된 회원 정보 조회 :
         */
        HttpSession session = req.getSession(false);
        if (Objects.isNull(session)) {
            return "home";
        }
        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        /**
         * 세션에 회원 데이터가 없다면
         */
        if (Objects.isNull(loginMember)) {
            return "home";
        }

        /**
         * 세션이 유지되면 로그인으로 이동
         */
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    @GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false)
            Member member, Model model) {
        /**
         * - 세션에 회원 데이터가 없으면 home
         */
        if (Objects.isNull(member)) {
            return "home";
        }

        /**
         * - 세션이 유지되면 로그인으로 이동
         */
        model.addAttribute("member", member);
        return "loginHome";
    }
}