package ru.rrost27.securitydemoproject.security_doc;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationExample {

    private static AuthenticationManager am = new SampleAuthenticationManager();

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {

            System.out.println("Please enter your login");
            String login = reader.readLine();
            System.out.println("Please enter your password");
            String password = reader.readLine();
            try {
                Authentication request = new UsernamePasswordAuthenticationToken(login, password);        //из введенной информации создали Token
                Authentication result = am.authenticate(request);                                         //отдали его в AM
                SecurityContextHolder.getContext().setAuthentication(result);                   //при успешном ответе, мы получаем полностью готовый Authentication и сохраняем его в контексте
                break;
            } catch (AuthenticationException e) {
                System.out.println("Authentication failed: " + e);
            }
        }

        System.out.println("Successfully authenticated. Security context contains: " +
                SecurityContextHolder.getContext().getAuthentication());
    }
}

    //AuthenticationManager - проверяет переданный ему токен
    //в сложных примерах, он сравнивает то что пришло с тем, что лежит в базе (форматируется к Security формату через UserDetailsService)
    //если все ок, то он возвращает Authentication, иначе бросает исключение (в реальном проекте его бы перехватил один из фильтров)
    //в данном примере мы сравниваем имя c паролем (bob-bob успешно, bob-pass ошибка)
    //и при успешном прохождении добавляем права нашему пользователю
    class SampleAuthenticationManager implements AuthenticationManager {

    static final List<GrantedAuthority> AUTHORITIES = new ArrayList<>();

    static {
        AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        //для простоты сравниваем логин и пароль, который сами же и ввели
        //успешная пара bob-bob
        if (auth.getName().equals(auth.getCredentials())){
            return new UsernamePasswordAuthenticationToken(auth.getName(),
                    auth.getCredentials(), AUTHORITIES);
        }else {
            throw new BadCredentialsException("Bad Credentials");
        }
    }
}
