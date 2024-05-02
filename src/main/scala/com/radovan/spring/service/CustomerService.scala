package com.radovan.spring.service

import java.util
import com.radovan.spring.dto.CustomerDto
import com.radovan.spring.utils.RegistrationForm


trait CustomerService {

  def addCustomer(form: RegistrationForm): CustomerDto

  def getCustomerById(customerId: Integer): CustomerDto

  def getCustomerByUserId(userId: Integer): CustomerDto

  def listAll: util.List[CustomerDto]

  def getCurrentCustomer: CustomerDto

  def updateCustomer(customer: CustomerDto): CustomerDto

  def removeCustomer(customerId: Integer): Unit
}
