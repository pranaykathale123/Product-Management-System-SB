
package com.product.configure;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource datasource;

	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder()).dataSource(datasource)
				.usersByUsernameQuery("select username,password,enabled from users where username = ?")
				.authoritiesByUsernameQuery("select username, role from users where username = ?");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception { //
		// TODO Auto-generated method stub //super.configure(http);
		http.authorizeRequests()

				.antMatchers("/login").hasAnyRole("ADMIN", "USER")
				.antMatchers("/load_form", "/edit_form/", "/save_products", "/update_products", "/delete/*")
				.hasRole("ADMIN").anyRequest().authenticated().and().formLogin().permitAll().and().logout().permitAll();

		http.formLogin().loginPage("/login");
		http.formLogin().defaultSuccessUrl("/viewindex")
		.and()
		.logout()
		.invalidateHttpSession(true)
		.deleteCookies("JSESSIONID")
		.logoutSuccessUrl("/login")
		.permitAll()
		.and()
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
		.invalidSessionUrl("/login")
		.maximumSessions(1);
		//.expiredUrl("/login");*/

	}

}
