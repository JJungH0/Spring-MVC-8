package hello.login.web.session;

import hello.login.domain.member.Member;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class SessionManagerTest {
    SessionManager sessionManager = new SessionManager();

    @Test
    void sessionTest() {

        MockHttpServletResponse resp = new MockHttpServletResponse();

        /**
         * 세션 생성 :
         * - 서버 담당
         */
        Member member = new Member();

        sessionManager.createSession(member, resp);

        /**
         * 요청에 응답 쿠기 저장 :
         * - 클라이언트 쿠기 생성
         */
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(resp.getCookies());

        /**
         * 세션 조회 :
         */
        Object result = sessionManager.getSession(req);
        Assertions.assertThat(result).isSameAs(member);


        /**
         * 세션 만료 :
         */
        sessionManager.expire(req);
        Object expired = sessionManager.getSession(req);
        Assertions.assertThat(expired).isEqualTo(null);
    }
}
