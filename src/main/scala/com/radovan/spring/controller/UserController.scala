package com.radovan.spring.controller

import java.util
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.radovan.spring.dto.UserDto
import com.radovan.spring.service.UserService


@RestController
@RequestMapping(value = Array("/api/users"))
class UserController {

  @Autowired
  private var userService: UserService = _

  @GetMapping(value = Array("/currentUser"))
  def getCurrentUser: ResponseEntity[UserDto] = {
    val currentUser = userService.getCurrentUser
    new ResponseEntity(currentUser, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(value = Array("/allUsers"))
  def getAllUsers: ResponseEntity[util.List[UserDto]] = {
    val allUsers = userService.listAll
    new ResponseEntity(allUsers, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @PutMapping(value = Array("/suspendUser/{userId}"))
  def suspendUser(@PathVariable("userId") userId: Integer): ResponseEntity[String] = {
    userService.suspendUser(userId)
    new ResponseEntity("User with id " + userId + " has been suspended!", HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @PutMapping(value = Array("/reactivateUser/{userId}"))
  def reactivateUser(@PathVariable("userId") userId: Integer): ResponseEntity[String] = {
    userService.reactivateUser(userId)
    new ResponseEntity("User with id " + userId + " has been reactivated!", HttpStatus.OK)
  }
}
