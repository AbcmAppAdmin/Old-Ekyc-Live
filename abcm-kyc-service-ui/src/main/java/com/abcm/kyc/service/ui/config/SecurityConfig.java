package com.abcm.kyc.service.ui.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final CustomUserDetailsService userDetailsService;
	private final LoginSuccessHandler successHandler;
	private final LoginFailureHandler failureHandler;
	
	@Value("${ContextPath}")
	private String contextPath;

	public SecurityConfig(CustomUserDetailsService userDetailsService,
			LoginSuccessHandler successHandler,
			LoginFailureHandler failureHandler) {
		this.userDetailsService = userDetailsService;
		this.successHandler = successHandler;
		this.failureHandler = failureHandler;
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		System.out.println("The context Path is"+contextPath);
		
		http
		.csrf(csrf -> csrf.disable())
		.headers(headers -> headers
			.frameOptions(frameOptions -> frameOptions.sameOrigin()) // ✅ Allow iframe from same origin
		)
		.authorizeHttpRequests(auth -> auth
				.requestMatchers(new AntPathRequestMatcher("/app/admin/**")).hasAnyRole("ADMIN")
				//.requestMatchers(new AntPathRequestMatcher("/app/user/**")).hasRole("USER")
				.requestMatchers(new AntPathRequestMatcher("/app/merchant/**")).hasAnyRole("MERCHANT")
	            .requestMatchers("/payment-response").permitAll()
	            .requestMatchers("/invalidError/**").permitAll()
	            .requestMatchers("/esign/webhook/**").permitAll()
	            
	            .requestMatchers("/response/**").permitAll()
				.requestMatchers("/WEB-INF/**").permitAll()
				.requestMatchers("/assets/**").permitAll()
				.requestMatchers("/Agreement/**").permitAll()
				.requestMatchers("/app/public/**").permitAll()
				.requestMatchers("/login/**").permitAll()
				.requestMatchers("/getRole/**").permitAll()
				.anyRequest().authenticated()
				)
		.formLogin(form -> form
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.successHandler(successHandler)
				.failureHandler(failureHandler)
				.permitAll()
				)
		.logout(logout -> logout
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.logoutSuccessUrl(contextPath+"/view-kyc/login?logout")
				.permitAll()
				)
		// ✅ SESSION MANAGEMENT STARTS HERE
		.sessionManagement(session -> session
				.invalidSessionUrl(contextPath+"/view-kyc/login?session=invalid")
				.maximumSessions(1)
				.maxSessionsPreventsLogin(false) 
				.expiredUrl(contextPath+"/view-kyc/login?session=expired")
				.and()
			    .sessionFixation().newSession() // force new session at login
				)
		// ✅ Handle unauthorized access nicely
		.exceptionHandling(handling -> handling
				.accessDeniedPage("/login")
				)
		.addFilterBefore(new AbsoluteSessionTimeoutFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authManager(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authBuilder.userDetailsService(userDetailsService)
		.passwordEncoder(passwordEncoder());
		return authBuilder.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
	
	
}
