package ru.rrost27.securitydemoproject.security_doc;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sun.jvm.hotspot.utilities.Assert;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration       //вспомнить бы зачем это
@SpringBootTest
public class HelloMessageServiceTest {

    /**
     * Я хз почему, все же это отдельная тема - тестирование, но нормально срабатывает только при полном подъеме контекста
     * Видимо ток в этом случае загружаются конфиг, который включает защиту на уровне методов!?
     */

    @Autowired
    private MessageService messageService;

    //Метод только для аутентифицированных пользователей, поэтому в данном случае мы получаем Исключение
    @Test
    public void getMessageUnauthenticated(){
        assertThrows(AuthenticationCredentialsNotFoundException.class, () ->
                messageService.getMessage());
    }

    /**
     * WithMockUser - создаст пользовтаеля user-password-ROLE_USER
     * WithMockUser("customUsername) - customUsername-password-ROLE_USER (то есть мы переопределяем только имя)
     * WithMockUser (username = "admin", sizes = {"ADMIN", "USER"}) - полностью касстомный пользователь (без префикса ROLE_)
     * WithMockUser (username = "admin", roles = {"USER", "ADMIN"}) - аналогично, но с ROLE_USER etc
     *
     * Чтобы не прописывать так над каждым методом, где должен быть замоканный юзер, можно вынести аннотацию на уровень класса
     *
     */
    @Test
    @WithMockUser
    public void getMessageWithMockUserCustomerName(){
        String message = messageService.getMessage();
        assertTrue(message.contains("Principal"));
    }

    /**
     * Если мы вынесли @WithMockUser на уровень класса, а у нас есть тест, который должен использовать аннонимного
     * На помощь приходит аннотация @WithAnonymousUser
     * тогда именно в таком тесте Мока не срабоатет
     */
    @Ignore
    @Test
    @WithAnonymousUser
    public void anonymous(){
        //что-то проверяем
    }

    /**
     * Если нам нужно проверить более детально пользователей, а не Authentication из коробки
     * То вместо WithUserMock подключаем @WithUserDetails(опять несколько вариантов!)
     * при использовании данной аннотации подразумевается, что у нас создан бин UserDetailsService и пользователь существует!!!
     */
    @Ignore
    @Test
    @WithUserDetails
    public  void getMessageWithUserDetailsCustomUsername () {

        //не работает, потому что что-то с посиком пользователя в бд ((
        String message = messageService.getMessage ();
        System.out.println(message);
    }
}