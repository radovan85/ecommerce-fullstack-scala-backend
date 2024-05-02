package com.radovan.spring.service

import java.util
import com.radovan.spring.dto.CartItemDto

trait CartItemService {

  def addCartItem(productId: Integer): CartItemDto

  def removeCartItem(itemId: Integer): Unit

  def removeAllByCartId(cartId: Integer): Unit

  def removeAllByProductId(productId: Integer): Unit

  def listAllByCartId(cartId: Integer): util.List[CartItemDto]

  def listAllByProductId(productId: Integer): util.List[CartItemDto]

  def getItemById(itemId: Integer): CartItemDto
}
