package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.OrderDto
import com.radovan.spring.entity.{OrderEntity, OrderItemEntity}
import com.radovan.spring.exceptions.{InstanceUndefinedException, OutOfStockException}
import com.radovan.spring.repository.{OrderAddressRepository, OrderItemRepository, OrderRepository}
import com.radovan.spring.service.{CartItemService, CartService, CustomerService, OrderService, ProductService, ShippingAddressService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.sql.Timestamp
import java.time.{Instant, ZoneId}
import java.util
import java.util.stream.Collectors
import scala.collection.JavaConverters._

@Service
class OrderServiceImpl extends OrderService{

  private var orderRepository: OrderRepository = _
  private var orderAddressRepository: OrderAddressRepository = _
  private var orderItemRepository: OrderItemRepository = _
  private var customerService: CustomerService = _
  private var tempConverter: TempConverter = _
  private var cartService: CartService = _
  private var shippingAddressService: ShippingAddressService = _
  private var cartItemService: CartItemService = _
  private var productService: ProductService = _
  private val zoneId: ZoneId = ZoneId.of("UTC")

  @Autowired
  private def injectAll(orderRepository: OrderRepository, orderAddressRepository: OrderAddressRepository, orderItemRepository: OrderItemRepository,
                        customerService: CustomerService, tempConverter: TempConverter, cartService: CartService,
                        shippingAddressService: ShippingAddressService, cartItemService: CartItemService, productService: ProductService): Unit = {

    this.orderRepository = orderRepository
    this.orderAddressRepository = orderAddressRepository
    this.orderItemRepository = orderItemRepository
    this.customerService = customerService
    this.tempConverter = tempConverter
    this.cartService = cartService
    this.shippingAddressService = shippingAddressService
    this.cartItemService = cartItemService
    this.productService = productService
  }


  @Transactional
  override def addOrder: OrderDto = {
    val customer = customerService.getCurrentCustomer
    val cart = cartService.getCartById(customer.getCartId)
    cartService.validateCart(cart.getCartId)
    val orderDto = new OrderDto
    orderDto.setCartId(cart.getCartId)
    orderDto.setOrderPrice(cart.getCartPrice)
    val shippingAddress = shippingAddressService.getAddressById(customer.getShippingAddressId)
    val orderAddress = tempConverter.shippingAddressToOrderAddress(shippingAddress)
    val storedAddress = orderAddressRepository.save(tempConverter.orderAddressDtoToEntity(orderAddress))
    val orderEntity = tempConverter.orderDtoToEntity(orderDto)
    orderEntity.setAddress(storedAddress)
    val currentTime = Instant.now().atZone(zoneId)
    orderEntity.setCreatedAt(Timestamp.valueOf(currentTime.toLocalDateTime))
    var storedOrder = orderRepository.save(orderEntity)
    val orderedItems = new util.ArrayList[OrderItemEntity]
    val cartItems = cartItemService.listAllByCartId(cart.getCartId)

    cartItems.forEach(cartItemDto => {
      val product = productService.getProductById(cartItemDto.getProductId)
      if (cartItemDto.getQuantity > product.getUnitStock) throw new OutOfStockException(new Error(s"There is a shortage of ${product.getProductName} in stock"))
      else {
        product.setUnitStock(product.getUnitStock - cartItemDto.getQuantity)
        productService.updateProduct(product, product.getProductId)
      }
      val orderItemDto = tempConverter.cartItemToOrderItemDto(cartItemDto)
      val orderItemEntity = tempConverter.orderItemDtoToEntity(orderItemDto)
      orderItemEntity.setOrder(storedOrder)
      orderedItems.add(orderItemRepository.save(orderItemEntity))
    })

    storedOrder.getOrderedItems.addAll(orderedItems)
    storedOrder = orderRepository.saveAndFlush(storedOrder)
    cartItemService.removeAllByCartId(cart.getCartId)
    cartService.refreshCartState(cart.getCartId)
    tempConverter.orderEntityToDto(storedOrder)
  }

  @Transactional(readOnly = true)
  override def getOrderById(orderId: Integer): OrderDto = {
    val orderEntity = orderRepository.findById(orderId).orElseThrow(() => new InstanceUndefinedException(new Error("The order has not been found!")))
    tempConverter.orderEntityToDto(orderEntity)
  }

  @Transactional(readOnly = true)
  override def listAll: util.List[OrderDto] = {
    val allOrders: util.List[OrderEntity] = orderRepository.findAll
    allOrders.stream.map[OrderDto](tempConverter.orderEntityToDto).collect(Collectors.toList[OrderDto])
  }

  @Transactional(readOnly = true)
  override def listAllByCartId(cartId: Integer): util.List[OrderDto] = {
    val allOrders = listAll.asScala
    allOrders.filter(order => order.getCartId == cartId).asJava
  }

  @Transactional
  override def deleteOrder(orderId: Integer): Unit = {
    getOrderById(orderId)
    orderRepository.deleteById(orderId)
    orderRepository.flush()
  }
}
