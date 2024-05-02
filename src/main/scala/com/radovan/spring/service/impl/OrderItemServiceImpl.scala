package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.OrderItemDto
import com.radovan.spring.entity.OrderItemEntity
import com.radovan.spring.repository.OrderItemRepository
import com.radovan.spring.service.{OrderItemService, OrderService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.stream.Collectors

@Service
class OrderItemServiceImpl extends OrderItemService {

  private var itemRepository: OrderItemRepository = _
  private var tempConverter: TempConverter = _
  private var orderService: OrderService = _

  @Autowired
  private def injectAll(itemRepository: OrderItemRepository, tempConverter: TempConverter, orderService: OrderService): Unit = {
    this.itemRepository = itemRepository
    this.tempConverter = tempConverter
    this.orderService = orderService
  }

  @Transactional(readOnly = true)
  override def listAllByOrderId(orderId: Integer): util.List[OrderItemDto] = {
    orderService.getOrderById(orderId)
    val allItems: util.List[OrderItemEntity] = itemRepository.listAllByOrderId(orderId)
    allItems.stream.map[OrderItemDto](tempConverter.orderItemEntityToDto).collect(Collectors.toList[OrderItemDto])
  }
}
