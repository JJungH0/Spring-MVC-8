package hello.login.web.filter;

import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whiteList = {"/", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String requestURI = req.getRequestURI();

        try {

            log.info("인증 체크 필터 시작 : {}", requestURI);

            if (isLoginCheckPath(requestURI)) {

                log.info("인증 체크 로직 실행: {}",requestURI);

                 HttpSession session = req.getSession(false);

                 if (Objects.isNull(session) || Objects.isNull(session.getAttribute(SessionConst.LOGIN_MEMBER))) {

                    log.info("미인증 사용자 요청: {}", requestURI);
                    /**
                     * 로그인 페이지로 redirect
                     */
                    resp.sendRedirect("/login?redirectURL=" + requestURI);
                    return;
                }
            }
            chain.doFilter(request,response);
        } catch (Exception e) {
            throw e; // 에외 로깅 가능, 톰캣까지 예외를 보내주어야 함
        }finally {
            log.info("인증 체크 필터 종료: {}",requestURI);
        }

    }

    /**
     * White List인 경우 인증 체크 X
     */
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }
}
