package com.radovan.spring.controller

import javax.security.auth.login.CredentialNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.UserDto
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.service.CustomerService
import com.radovan.spring.service.UserService
import com.radovan.spring.utils.{AuthenticationRequest, JwtUtil, RegistrationForm}

@RestController
class MainController {

  private var userService: UserService = _
  private var customerService: CustomerService = _
  private var jwtTokenUtil: JwtUtil = _
  private var tempConverter: TempConverter = _

  @Autowired
  private def injectAll(userService: UserService, customerService: CustomerService, jwtUtil: JwtUtil,
                        tempConverter: TempConverter): Unit = {
    this.userService = userService
    this.customerService = customerService
    this.jwtTokenUtil = jwtUtil
    this.tempConverter = tempConverter
  }

  @PostMapping(value = Array("/register"))
  def registerUser(@Validated @RequestBody form: RegistrationForm, errors: Errors): ResponseEntity[String] = {
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The data has not been validated"))
    }

    customerService.addCustomer(form)
    new ResponseEntity("Registration completed!", HttpStatus.OK)
  }

  @PostMapping(Array("/login"))
  @throws[Exception]
  def createAuthenticationToken(@RequestBody authenticationRequest: AuthenticationRequest, errors: Errors): ResponseEntity[UserDto] = {
    val authOptional = userService.authenticateUser(authenticationRequest.getUsername, authenticationRequest.getPassword)
    if (authOptional.isEmpty) throw new CredentialNotFoundException
    val userDto = userService.getUserByEmail(authenticationRequest.getUsername)
    val userDetails = tempConverter.userDtoToEntity(userDto)
    val jwt = jwtTokenUtil.generateToken(userDetails)
    val authUser = tempConverter.userEntityToDto(userDetails)
    authUser.setAuthToken(jwt)
    new ResponseEntity(authUser, HttpStatus.OK)
  }
}
