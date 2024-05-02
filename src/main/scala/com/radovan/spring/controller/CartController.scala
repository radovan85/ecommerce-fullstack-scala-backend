package com.radovan.spring.controller

import java.util
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{DeleteMapping, GetMapping, PathVariable, PostMapping, RequestMapping, RestController}
import com.radovan.spring.dto.{CartDto, CartItemDto}
import com.radovan.spring.service.{CartItemService, CartService, CustomerService}

@RestController
@RequestMapping(value = Array("/api/cart"))
class CartController @Autowired() (val cartService: CartService, val cartItemService: CartItemService, val customerService: CustomerService) {

  @GetMapping(value = Array("/getMyItems"))
  def listMyItems(): ResponseEntity[util.List[CartItemDto]] = {
    val customer = customerService.getCurrentCustomer
    val allItems = cartItemService.listAllByCartId(customer.getCartId())
    new ResponseEntity(allItems, HttpStatus.OK)
  }

  @PostMapping(value = Array("/addCartItem/{productId}"))
  def addItem(@PathVariable("productId") productId: Integer): ResponseEntity[String] = {
    cartItemService.addCartItem(productId)
    new ResponseEntity("The item has been added to the cart!",HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/deleteItem/{itemId}"))
  def deleteItem(@PathVariable("itemId") itemId: Integer): ResponseEntity[String] = {
    cartItemService.removeCartItem(itemId)
    new ResponseEntity("The item has been removed from the cart!", HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/clearCart"))
  def clearCart(): ResponseEntity[String] = {
    val customer = customerService.getCurrentCustomer
    cartItemService.removeAllByCartId(customer.getCartId())
    new ResponseEntity("All items have been removed from the cart!", HttpStatus.OK)
  }

  @GetMapping(value = Array("/getMyCart"))
  def getMyCart: ResponseEntity[CartDto] = {
    val customer = customerService.getCurrentCustomer
    val cart = cartService.getCartById(customer.getCartId)
    new ResponseEntity(cart, HttpStatus.OK)
  }

  @GetMapping(value = Array("/validateCart"))
  def validateCart(): ResponseEntity[String] = {
    val customer = customerService.getCurrentCustomer
    cartService.validateCart(customer.getCartId())
    new ResponseEntity("Your cart is validated!", HttpStatus.OK)
  }

}

