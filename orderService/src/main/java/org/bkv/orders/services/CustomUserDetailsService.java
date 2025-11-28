package org.bkv.orders.services;

import org.bkv.orders.entity.UserEntity;
import org.bkv.orders.repo.UserRepository;
import org.bkv.orders.utils.Roles;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        Optional<UserEntity> user = userRepository.findByUserName(userName);

        if (user.isPresent()) {
            List<GrantedAuthority> authorities =
                    user.get().getRole().equals("admin")
                            ? List.of(new SimpleGrantedAuthority(Roles.ROLE_ADMIN.name()))
                            : List.of(new SimpleGrantedAuthority(Roles.ROLE_USER.name()));

            return new User(
                    user.get().getUserName(),
                    user.get().getPassword(),
                    authorities
            );
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
