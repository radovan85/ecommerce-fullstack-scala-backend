package com.radovan.spring.service

import java.util
import com.radovan.spring.dto.OrderDto


trait OrderService {

  def addOrder: OrderDto

  def getOrderById(orderId: Integer): OrderDto

  def listAll: util.List[OrderDto]

  def listAllByCartId(cartId: Integer): util.List[OrderDto]

  def deleteOrder(orderId: Integer): Unit
}