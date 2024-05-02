package com.radovan.spring.controller

import java.util
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.radovan.spring.dto.CustomerDto
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.service.CustomerService


@RestController
@RequestMapping(value = Array("/api/customers"))
class CustomerController {

  @Autowired
  private var customerService: CustomerService = _

  @PreAuthorize(value = "hasAuthority('ROLE_USER')")
  @GetMapping(value = Array("/getCurrentCustomer"))
  def getCurrentCustomer: ResponseEntity[CustomerDto] = {
    val customer = customerService.getCurrentCustomer
    new ResponseEntity(customer, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ROLE_USER')")
  @PutMapping(value = Array("/updateCustomer"))
  def updateCustomer(@Validated @RequestBody customer: CustomerDto, errors: Errors): ResponseEntity[String] = {
    if (errors.hasErrors) throw new DataNotValidatedException(new Error("The customer has not been validated!"))
    customerService.updateCustomer(customer)
    new ResponseEntity("The customer has been updated!", HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(value = Array("/allCustomers"))
  def getAllCustomers: ResponseEntity[util.List[CustomerDto]] = {
    val allCustomers = customerService.listAll
    new ResponseEntity(allCustomers, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(value = Array("/customerDetails/{customerId}"))
  def getCustomerDetails(@PathVariable("customerId") customerId: Integer): ResponseEntity[CustomerDto] = {
    val customer = customerService.getCustomerById(customerId)
    new ResponseEntity(customer, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @DeleteMapping(value = Array("/deleteCustomer/{customerId}"))
  def deleteCustomer(@PathVariable("customerId") customerId: Integer): ResponseEntity[String] = {
    customerService.removeCustomer(customerId)
    new ResponseEntity("The customer with id " + customerId + " has been permanently deleted!", HttpStatus.OK)
  }
}
