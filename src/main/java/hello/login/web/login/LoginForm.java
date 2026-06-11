package hello.login.web.login;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {
    @NotEmpty(message = "빈값금지")
    private String loginId;
    @NotEmpty(message = "빈값금지")
    private String password;
}
