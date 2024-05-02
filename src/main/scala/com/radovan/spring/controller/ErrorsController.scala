package com.radovan.spring.controller

import javax.security.auth.login.CredentialNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MultipartException
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.exceptions.ExistingInstanceException
import com.radovan.spring.exceptions.FileUploadException
import com.radovan.spring.exceptions.InstanceUndefinedException
import com.radovan.spring.exceptions.InvalidCartException
import com.radovan.spring.exceptions.InvalidUserException
import com.radovan.spring.exceptions.OutOfStockException
import com.radovan.spring.exceptions.SuspendedUserException
import jakarta.servlet.http.HttpServletRequest

@RestControllerAdvice
class ErrorsController {

  @ExceptionHandler(Array(classOf[DataNotValidatedException]))
  def handleDataNotValidatedException(error: Error) = new ResponseEntity[String](error.getMessage, HttpStatus.NOT_ACCEPTABLE)

  @ExceptionHandler(Array(classOf[InstanceUndefinedException]))
  def handleInstanceUndefinedException(error: Error) = new ResponseEntity[String](error.getMessage, HttpStatus.METHOD_NOT_ALLOWED)

  @ExceptionHandler(Array(classOf[CredentialNotFoundException]))
  def handleCredentialsNotFoundException(exc: CredentialNotFoundException) = new ResponseEntity[String](exc.getMessage, HttpStatus.UNPROCESSABLE_ENTITY)

  @ExceptionHandler(Array(classOf[InvalidUserException]))
  def handleInvalidUserException(error: Error) = new ResponseEntity[String](error.getMessage, HttpStatus.UNPROCESSABLE_ENTITY)

  @ExceptionHandler(Array(classOf[FileUploadException]))
  def handleFileUploadException(error: Error) = new ResponseEntity[String](error.getMessage, HttpStatus.NOT_ACCEPTABLE)

  @ExceptionHandler(Array(classOf[ExistingInstanceException]))
  def handleExistingInstanceException(error: Error) = new ResponseEntity[String](error.getMessage, HttpStatus.CONFLICT)

  @ExceptionHandler(Array(classOf[MultipartException]))
  def handleMultipartException(request: HttpServletRequest, e: Exception) = new ResponseEntity[String]("Error: " + e.getMessage, HttpStatus.NOT_ACCEPTABLE)

  @ExceptionHandler(Array(classOf[InvalidCartException]))
  def handleInvalidCartException(error: Error) = new ResponseEntity[String](error.getMessage, HttpStatus.NOT_ACCEPTABLE)

  @ExceptionHandler(Array(classOf[OutOfStockException]))
  def handleOutOfStockException(error: Error) = new ResponseEntity[String](error.getMessage, HttpStatus.NOT_ACCEPTABLE)

  @ExceptionHandler(Array(classOf[SuspendedUserException]))
  def handleSuspendedUserException(error: Error) = new ResponseEntity[String](error.getMessage, HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
}