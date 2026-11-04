package br.com.restaurante.config;

import br.com.restaurante.repository.FuncionarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Controle de acesso por perfil (RNF002). Funcionários entram com CPF e senha;
 * cardápio e avaliação são públicos para os clientes.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/cardapio", "/avaliacao/**", "/login", "/css/**", "/webjars/**", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("GERENTE")
                .requestMatchers("/cozinha/**").hasAnyRole("COZINHEIRO", "GERENTE")
                .requestMatchers("/pedidos/**", "/clientes/**").hasAnyRole("GARCOM", "GERENTE")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("cpf")
                .passwordParameter("senha")
                .defaultSuccessUrl("/painel", true)
                .permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/?saiu").permitAll());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(FuncionarioRepository funcionarioRepository) {
        return cpf -> funcionarioRepository.findById(cpf.replaceAll("\\D", ""))
                .map(f -> User.withUsername(f.getCpf())
                        .password(f.getSenha())
                        .roles(f.getFuncao().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("CPF não cadastrado"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
