package hello.login.web.filter;

import hello.login.domain.member.Member;
import hello.login.web.session.SessionManager;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("LogFilter.init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("LogFilter.doFilter");

        HttpServletRequest req = (HttpServletRequest) request;

        String requestURI = req.getRequestURI();

        String uuid = UUID.randomUUID().toString();

        try {
            log.info("REQUEST [{}][{}]",uuid,requestURI);
            chain.doFilter(req,response);
        } catch (Exception e) {
            throw e;
        }finally {
            log.info("RESPONSE [{}}[{}]",uuid,requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("LogFilter.destroy");
    }
}
