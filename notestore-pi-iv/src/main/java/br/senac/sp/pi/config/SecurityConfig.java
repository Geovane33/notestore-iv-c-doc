//package br.senac.sp.pi.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().authorizeRequests()
//                .antMatchers(HttpMethod.GET, "/produtos", "/","/notestore").permitAll()
//                .antMatchers(HttpMethod.POST, "/produtos").hasRole("ADMIN")
//                .antMatchers("/css/**","/icons/**","/logo/**", "/index.js").permitAll()
//                .antMatchers("/funcionarios/**").hasRole("ADMIN")
//                .anyRequest().authenticated()
//                .and().formLogin().loginPage("/notestore").permitAll()
//                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("logout"));
//        //.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);;
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("geovane@usuario").password("{noop}123456").roles("USER")
//                .and()
//                .withUser("admin@admin").password("{noop}123456").roles("USER","ADMIN");
//    }
//
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring()
//                .antMatchers("/templates/index.html", "/templates/produtos/detalhes.html");
//    }
//
////    @Bean
////    public PasswordEncoder passwordEncoder() {
////        return NoOpPasswordEncoder.getInstance();
////    }
//
//
//}
