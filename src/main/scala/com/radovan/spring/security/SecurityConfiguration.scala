package com.radovan.spring.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

import com.radovan.spring.service.impl.UserDetailsImpl

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfiguration {

  private var jwtRequestFilter: JwtRequestFilter = _
  private var jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint = _
  private var corsHandler: CorsHandler = _
  private val unSecuredPaths = Array[String]("/login", "/register")
  private val userPaths = Array[String]("/api/cart/**")

  @Autowired
  private def injectAll(jwtRequestFilter: JwtRequestFilter, jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint, corsHandler: CorsHandler): Unit = {
    this.jwtRequestFilter = jwtRequestFilter
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint
    this.corsHandler = corsHandler
  }

  private def getAntPathRequestMatchers = {
    val requestMatchers = new Array[AntPathRequestMatcher](unSecuredPaths.length)
    for (i <- unSecuredPaths.indices) {
      requestMatchers(i) = new AntPathRequestMatcher(unSecuredPaths(i))
    }
    requestMatchers
  }

  private def getUserPathRequestMatchers = {
    val returnValue = new Array[AntPathRequestMatcher](userPaths.length)
    for (x <- userPaths.indices) {
      returnValue(x) = new AntPathRequestMatcher(userPaths(x))
    }
    returnValue
  }

  @Bean
  @throws[Exception]
  def securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain = {
    val requestMatchers = getAntPathRequestMatchers
    val requestUserMatchers = getUserPathRequestMatchers
    httpSecurity
      .csrf(csrf => csrf.disable)
      .cors(cors => cors.configurationSource(corsHandler))
      .sessionManagement(session => session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(exception => exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
      .authorizeHttpRequests(authorize => authorize
        .requestMatchers(requestMatchers: _*).permitAll()
        .requestMatchers(requestUserMatchers: _*).hasAuthority("ROLE_USER")
        .requestMatchers(HttpMethod.GET, "/api/users/currentUser").permitAll()
        .anyRequest.authenticated()
      )
      .addFilterBefore(jwtRequestFilter, classOf[UsernamePasswordAuthenticationFilter])
      .build()
  }

  @Bean def authenticationManager: AuthenticationManager = {
    val authProvider = new DaoAuthenticationProvider
    authProvider.setUserDetailsService(userDetailsService)
    authProvider.setPasswordEncoder(passwordEncoder)
    new ProviderManager(authProvider)
  }

  @Bean def userDetailsService = new UserDetailsImpl

  @Bean def passwordEncoder = new BCryptPasswordEncoder
}
