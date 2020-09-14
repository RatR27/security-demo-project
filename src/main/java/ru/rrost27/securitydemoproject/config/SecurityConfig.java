package ru.rrost27.securitydemoproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.rrost27.securitydemoproject.service.UserService;

/**
 * Наследуемся от WebSecurityConfigurerAdapter для получения готовых настроек защиты
 * и кастомное переопределение
 *
 */

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(users());
//    }

    //данный метод настраивает защиту на уровне запроса
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //если запрос после корня приложения имеет кусок auth то ставим условие, что все запросы работают только для аутетифицированнных пользователей
                .antMatchers("/auth/**").authenticated()
                //защита запросов по Ролям
                .antMatchers("/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
                .antMatchers("/profile/**").hasAuthority("read_profile")
//                .antMatchers("/profile/**").authenticated()
                //таких настроек может быть много
                //чтобы перейти к следующему блоку настроек используем метод and()
                .and()
                /** Настройка авторизации
                 * можно настроить:
                 * loginPage(view) - указать свою страницу для авторизации
                 * loginProcessingUrl(url) - куда улетят Логин и Пароль со страницы авторизации
                 * successForwardUrl(url) - куда редиректит при успехе авторизации
                 * failureForwardUrl(url) - куда кинет при неуспешной
                 *
                 * При дефолтном режиме, как у нас, то выведется базовая view от самого спринга
                 */
                .formLogin()
                .and()
                /** Настройка выхода из УЗ
                 * можно настроить:
                 * logoutSuccessUrl() - куда редиректит при успешном выходе из УЗ
                 */
                .logout()
                .logoutSuccessUrl("/")
                .and()
                /** Перед тем как запрос попадет на контроллер он проходит цепочку из фильтров, которую мы можем кастомизировать, добавляя свои
                 *
                 */
                .addFilter(new UsernamePasswordAuthenticationFilter())
                .addFilterBefore(new UsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }


    //Три варианта хранения пользователей

    //InMemory - в памяти, при ребуте сервера, все наши пользователи умирают
    @Bean
    public UserDetailsService users(){
        //пока руками создали нового юзера в представлении Секьюра
        UserDetails user = User.builder()
                .username("User1_Bob")
                //префикс {bcrypt} указывает секьюру, что пароль сравнивается не как строки - символ к символу, а сравнить нужно хэши
                .password("{bcrypt}$2y$12$hWRINx7HZQ63py464HWz2esbvTWlB/zwhdcZeV.DaI7V/ncc2ETkm")
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("Admin")
                //префикс {bcrypt} указывает секьюру, что пароль сравнивается не как строки - символ к символу, а сравнить нужно хэши
                .password("{bcrypt}$2y$12$hWRINx7HZQ63py464HWz2esbvTWlB/zwhdcZeV.DaI7V/ncc2ETkm")
                .roles("ADMIN", "USER")
                .build();

        //просто помещаем наших пользователей в чистом виде в память и они там лежат
        return new InMemoryUserDetailsManager(user, admin);
    }

    //JdbcAuthentication - пользователи хранятся в БД, но таблицы должны соответсвовать структуре! Иначе не соберется даже
    //так как за БД отвечает bean DataSource то его и инжектим
//    @Bean
//    public UserDetailsService users(DataSource dataSource){
        // 1 - мы можем сами положить в БД пользователей
//        UserDetails user = User.builder()
//                .username("User1_Bob")
//                //префикс {bcrypt} указывает секьюру, что пароль сравнивается не как строки - символ к символу, а сравнить нужно хэши
//                .password("{bcrypt}$2y$12$hWRINx7HZQ63py464HWz2esbvTWlB/zwhdcZeV.DaI7V/ncc2ETkm")
//                .roles("USER")
//                .build();
//
//        UserDetails admin = User.builder()
//                .username("Admin")
//                //префикс {bcrypt} указывает секьюру, что пароль сравнивается не как строки - символ к символу, а сравнить нужно хэши
//                .password("{bcrypt}$2y$12$hWRINx7HZQ63py464HWz2esbvTWlB/zwhdcZeV.DaI7V/ncc2ETkm")
//                .roles("ADMIN", "USER")
//                .build();
//
//        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
//
//        if (jdbcUserDetailsManager.userExists(user.getUsername())){
//            jdbcUserDetailsManager.deleteUser(user.getUsername());
//        }
//        if (jdbcUserDetailsManager.userExists(admin.getUsername())){
//            jdbcUserDetailsManager.deleteUser(admin.getUsername());
//        }
//        //собственно само добавление пользователей в БД
//        jdbcUserDetailsManager.createUser(user);
//        jdbcUserDetailsManager.createUser(admin);
//        return jdbcUserDetailsManager;

        // 2 - простой вариант использования, когда все пользователи уже внесены в БД
//        return new JdbcUserDetailsManager(dataSource);
//    }

    //еще один вариант настройки аутентификации - взят из spring_security.doc
    //получается у Builder'а запрашиваем вариант с jdbc, отдаем туда наш dataSource
    //и тут же можем накидывать пользователей
//    @Bean
//    public void configureGlobal(AuthenticationManagerBuilder auth, DataSource dataSource) throws Exception {
//        auth.jdbcAuthentication()
//                .dataSource(dataSource)
//                .withDefaultSchema()
//                .withUser("user").password("pass1").roles("USER").and()
//                .withUser("admin").password("root").roles("USER", "ADMIN");
//    }

//    @Bean
//    public BCryptPasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//    //DaoAuthenticationProvider - пользователи хранятся в БД, при чем в любом виде, а мы руками их приведем к нужной форме для Секьюра
//    @Bean
//    public DaoAuthenticationProvider users(){
//        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
//        auth.setUserDetailsService(userService);
//        auth.setPasswordEncoder(passwordEncoder());
//        return auth;
//    }

}
