package hello.login.web.session;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 세션 관리 :
 */
@Component
public class SessionManager {

    public static final String SESSION_COOKIE_NAME = "mySessionId";
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    /**
     * 세션 생성 :
     * - sessionId 생성 (= 임의의 추정 불가능한 랜덤 값)
     * - 세션 저장소에 sessionId와 보관할 값 저장
     * - sessionId로 응답 쿠키를 생성해서 클라이언트에 전
     */
    public void createSession(Object value, HttpServletResponse resp) {
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);

        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        resp.addCookie(cookie);
    }

    /**
     * 세션 조회 :
     */
    public Object getSession(HttpServletRequest req) {
        Cookie sessionCookie = findCookie(req, SESSION_COOKIE_NAME);
        if (Objects.isNull(sessionCookie)) {
            return null;
        }
        return sessionStore.get(sessionCookie.getValue());
    }

    /**
     * 세션 만료
     */
    public void expire(HttpServletRequest req) {
        Cookie cookie = findCookie(req, SESSION_COOKIE_NAME);
        if (Objects.nonNull(cookie)) {
            sessionStore.remove(cookie.getValue());
        }
    }

    public Cookie findCookie(HttpServletRequest req, String cookieName) {
        if (Objects.isNull(req.getCookies())) {
            return null;
        }
        /**
         * 여기서 getCookies()가 NULL이면 NPE 방지를 위해 getCookies()을 먼저 검사
         */
        return Arrays.stream(req.getCookies())
                .filter(c -> c.getName().equals(cookieName))
                .findFirst()
                .orElse(null);
    }
}
