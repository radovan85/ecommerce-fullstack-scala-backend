package com.radovan.spring.service

import java.util
import org.springframework.security.core.Authentication
import com.radovan.spring.dto.UserDto

trait UserService {

  def listAll: util.List[UserDto]

  def getCurrentUser: UserDto

  def getUserById(userId: Integer): UserDto

  def getUserByEmail(email: String): UserDto

  def authenticateUser(username: String, password: String): Option[Authentication]

  def isAdmin: Boolean

  def suspendUser(userId: Integer): Unit

  def reactivateUser(userId: Integer): Unit
}
