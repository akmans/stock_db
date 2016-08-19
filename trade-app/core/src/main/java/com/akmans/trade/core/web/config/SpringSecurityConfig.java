package com.akmans.trade.core.web.config;

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
		auth.inMemoryAuthentication().withUser("akamans").password("123456").roles("USER");
		auth.inMemoryAuthentication().withUser("manager").password("123456").roles("USER");
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
			.csrf()
				.disable()
			.logout()
				.logoutSuccessUrl("/login?logout")
				.logoutUrl("/logout")
				.and()
			.formLogin()
				.usernameParameter("username")
				.passwordParameter("password")
				.loginProcessingUrl("/authenticate")
				.failureUrl("/login?error")
				.loginPage("/login")
				.defaultSuccessUrl("/");

	}
}
