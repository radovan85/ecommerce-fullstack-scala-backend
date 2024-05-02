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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.radovan.spring.dto.OrderAddressDto
import com.radovan.spring.dto.OrderDto
import com.radovan.spring.dto.OrderItemDto
import com.radovan.spring.dto.ShippingAddressDto
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.service.CustomerService
import com.radovan.spring.service.OrderAddressService
import com.radovan.spring.service.OrderItemService
import com.radovan.spring.service.OrderService
import com.radovan.spring.service.ShippingAddressService

@RestController
@RequestMapping(value = Array("/api/order"))
class OrderController {

  private var shippingAddressService: ShippingAddressService = _
  private var customerService: CustomerService = _
  private var orderService: OrderService = _
  private var orderAddressService: OrderAddressService = _
  private var orderItemService: OrderItemService = _

  @Autowired
  private def injectAll(shippingAddressService: ShippingAddressService, customerService: CustomerService,
                        orderService: OrderService, orderAddressService: OrderAddressService, orderItemService: OrderItemService): Unit = {

    this.shippingAddressService = shippingAddressService
    this.customerService = customerService
    this.orderService = orderService
    this.orderAddressService = orderAddressService
    this.orderItemService = orderItemService

  }

  @PreAuthorize(value = "hasAuthority('ROLE_USER')")
  @GetMapping(value = Array("/provideMyAddress"))
  def provideMyAddress: ResponseEntity[ShippingAddressDto] = {
    val customer = customerService.getCurrentCustomer
    val address = shippingAddressService.getAddressById(customer.getShippingAddressId)
    new ResponseEntity(address, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ROLE_USER')")
  @PutMapping(value = Array("/confirmShippingAddress"))
  def confirmShipping(@Validated @RequestBody address: ShippingAddressDto, errors: Errors): ResponseEntity[String] = {
    if (errors.hasErrors) throw new DataNotValidatedException(new Error("The address has not been validated!"))
    val customer = customerService.getCurrentCustomer
    shippingAddressService.updateAddress(address, customer.getShippingAddressId)
    new ResponseEntity("The address has been updated!", HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ROLE_USER')")
  @PostMapping(value = Array("/placeOrder"))
  def placeOrder: ResponseEntity[String] = {
    orderService.addOrder
    new ResponseEntity("Your order has been submitted without any problems.", HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(value = Array("/allOrders"))
  def getAllOrders: ResponseEntity[util.List[OrderDto]] = {
    val allOrders = orderService.listAll
    new ResponseEntity(allOrders, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(value = Array("/orderDetails/{orderId}"))
  def orderDetails(@PathVariable("orderId") orderId: Integer): ResponseEntity[OrderDto] = {
    val order = orderService.getOrderById(orderId)
    new ResponseEntity(order, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(value = Array("/allAddresses"))
  def getAllAddresses: ResponseEntity[util.List[OrderAddressDto]] = {
    val allAddresses = orderAddressService.listAll
    new ResponseEntity(allAddresses, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(value = Array("/allItems/{orderId}"))
  def getAllItems(@PathVariable("orderId") orderId: Integer): ResponseEntity[util.List[OrderItemDto]] = {
    val allItems = orderItemService.listAllByOrderId(orderId)
    new ResponseEntity(allItems, HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @DeleteMapping(value = Array("/deleteOrder/{orderId}"))
  def deleteOrder(@PathVariable("orderId") orderId: Integer): ResponseEntity[String] = {
    orderService.deleteOrder(orderId)
    new ResponseEntity("The order with id " + orderId + " has been permanently deleted!", HttpStatus.OK)
  }
}
