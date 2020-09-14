package ru.rrost27.securitydemoproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.rrost27.securitydemoproject.entity.Role;
import ru.rrost27.securitydemoproject.entity.MyUser;
import ru.rrost27.securitydemoproject.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Обертка для обращения в БД
    public MyUser findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    //Метод находит нам по имени в БД пользователя и создает по нему объект типа UserDetails кооторый понятен Секьюру
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser myUser = findUserByUsername(username);
        if(myUser == null){
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }
        return new org.springframework.security.core.userdetails.User(myUser.getUsername(), myUser.getPassword(),
                mapRolesToAuthorities(myUser.getRoles()));
    }

    private Collection< ? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles){
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }
}
