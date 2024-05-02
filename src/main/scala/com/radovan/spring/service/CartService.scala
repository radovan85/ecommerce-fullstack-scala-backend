package com.radovan.spring.service

import com.radovan.spring.dto.CartDto

trait CartService {

  def getCartById(cartId: Integer): CartDto

  def validateCart(cartId: Integer): CartDto

  def calculateGrandTotal(cartId: Integer): Float

  def refreshCartState(cartId: Integer): Unit

  def refreshAllCarts(): Unit
}
