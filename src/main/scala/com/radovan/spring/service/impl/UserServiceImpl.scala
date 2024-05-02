package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.UserDto
import com.radovan.spring.entity.UserEntity
import com.radovan.spring.exceptions.InvalidUserException
import com.radovan.spring.repository.{RoleRepository, UserRepository}
import com.radovan.spring.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.{AuthenticationManager, UsernamePasswordAuthenticationToken}
import org.springframework.security.core.{Authentication, AuthenticationException}
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.stream.Collectors

@Service
class UserServiceImpl extends UserService{

  private var userRepository: UserRepository = _
  private var tempConverter: TempConverter = _
  private var authenticationManager: AuthenticationManager = _
  private var roleRepository: RoleRepository = _

  @Autowired
  private def injectAll(userRepository: UserRepository, tempConverter: TempConverter, authenticationManager: AuthenticationManager,
                        roleRepository: RoleRepository): Unit = {
    this.userRepository = userRepository
    this.tempConverter = tempConverter
    this.authenticationManager = authenticationManager
    this.roleRepository = roleRepository
  }

  @Transactional(readOnly = true)
  override def listAll: util.List[UserDto] = {
    val allUsers: util.List[UserEntity] = userRepository.findAll
    allUsers.stream.map[UserDto](tempConverter.userEntityToDto).collect(Collectors.toList[UserDto])
  }

  @Transactional(readOnly = true)
  override def getCurrentUser: UserDto = {
    val authentication = SecurityContextHolder.getContext.getAuthentication
    if (authentication.isAuthenticated) {
      val currentUsername = authentication.getName
      userRepository.findByEmail(currentUsername)
        .map(tempConverter.userEntityToDto)
        .getOrElse(throw new InvalidUserException(new Error("Invalid user!")))
    } else {
      throw new InvalidUserException(new Error("Invalid user!"))
    }
  }

  @Transactional(readOnly = true)
  override def getUserById(userId: Integer): UserDto =
    userRepository.findById(userId).map[UserDto](tempConverter.userEntityToDto)
      .orElseThrow(() => new InvalidUserException(new Error("Invalid user!")))

  @Transactional(readOnly = true)
  override def getUserByEmail(email: String): UserDto = {
    userRepository.findByEmail(email)
      .map(tempConverter.userEntityToDto)
      .fold(throw new InvalidUserException(new Error("Invalid user!")))(identity)
  }

  @Transactional(readOnly = true)
  override def authenticateUser(username: String, password: String): Option[Authentication] = {
    val authReq = new UsernamePasswordAuthenticationToken(username, password)
    val userOptional = userRepository.findByEmail(username)
    userOptional.flatMap { user =>
      try {
        val auth = authenticationManager.authenticate(authReq)
        Some(auth)
      } catch {
        case _: AuthenticationException => None
      }
    }
  }

  @Transactional(readOnly = true)
  override def isAdmin: Boolean = {
    val authUser = getCurrentUser
    roleRepository.findByRole("ADMIN") match {
      case Some(role) => authUser.getRolesIds.contains(role.getId)
      case None => false
    }
  }

  @Transactional
  override def suspendUser(userId: Integer): Unit = {
    val user = getUserById(userId)
    user.setEnabled(0)
    userRepository.saveAndFlush(tempConverter.userDtoToEntity(user))
  }

  @Transactional
  override def reactivateUser(userId: Integer): Unit = {
    val user = getUserById(userId)
    user.setEnabled(1)
    userRepository.saveAndFlush(tempConverter.userDtoToEntity(user))
  }
}
