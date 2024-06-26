package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.{UserDetails, UserDetailsService}
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsImpl extends UserDetailsService{

  private var userService:UserService = _
  private var tempConverter:TempConverter = _

  @Autowired
  private def injectAll(userService: UserService, tempConverter: TempConverter): Unit ={
    this.userService = userService
    this.tempConverter = tempConverter
  }

  override def loadUserByUsername(username: String): UserDetails = {
    val user = userService.getUserByEmail(username)
    tempConverter.userDtoToEntity(user)
  }
}
