package com.akmans.trade.web.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("test").password("123456").roles("USER");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/login", "/403").permitAll()
				.antMatchers("/resources/**").permitAll()
				.anyRequest().hasRole("USER")
				.and()
//			.exceptionHandling()
//				.accessDeniedPage("/login?authorization_error=true")
//				.and()
			.csrf()
				.disable()
			.logout()
				.logoutSuccessUrl("/login?logout")
				.logoutUrl("/logout")
				.and()
			.formLogin()
				.usernameParameter("j_username")
				.passwordParameter("j_password")
				.loginProcessingUrl("/authenticate")
				.failureUrl("/login?error")
				.loginPage("/login")
				.defaultSuccessUrl("/");

	}
}
