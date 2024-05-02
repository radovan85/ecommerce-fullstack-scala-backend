package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.CartItemDto
import com.radovan.spring.exceptions.{InstanceUndefinedException, OperationNotAllowedException, OutOfStockException}
import com.radovan.spring.repository.CartItemRepository
import com.radovan.spring.service.{CartItemService, CartService, CustomerService, ProductService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.stream.Collectors
import scala.collection.JavaConverters._

@Service
class CartItemServiceImpl extends CartItemService {

  private var itemRepository: CartItemRepository = _
  private var customerService: CustomerService = _
  private var cartService: CartService = _
  private var tempConverter: TempConverter = _
  private var productService: ProductService = _

  @Autowired
  private def injectAll(itemRepository: CartItemRepository, customerService: CustomerService, cartService: CartService,
                        tempConverter: TempConverter, productService: ProductService): Unit = {
    this.itemRepository = itemRepository
    this.customerService = customerService
    this.cartService = cartService
    this.tempConverter = tempConverter
    this.productService = productService
  }

  @Transactional
  override def addCartItem(productId: Integer): CartItemDto = {
    val customer = customerService.getCurrentCustomer
    val cart = cartService.getCartById(customer.getCartId)
    val product = productService.getProductById(productId)
    val existingItem = listAllByCartId(cart.getCartId).stream().filter(item => item.getProductId == productId).findFirst()
    val cartItem: CartItemDto = existingItem.orElseGet(() => {
      val newItem = new CartItemDto
      newItem.setProductId(productId)
      newItem.setCartId(customer.getCartId)
      newItem.setQuantity(0)
      newItem
    })
    cartItem.setQuantity(cartItem.getQuantity + 1)
    if (product.getUnitStock < cartItem.getQuantity) {
      throw new OutOfStockException(new Error(s"There is a shortage of ${product.getProductName} in stock!"))
    }
    val cartItemEntity = tempConverter.cartItemDtoToEntity(cartItem)
    cartItemEntity.setCart(tempConverter.cartDtoToEntity(cart))
    val storedItem = itemRepository.save(cartItemEntity)
    cartService.refreshCartState(cart.getCartId)
    tempConverter.cartItemEntityToDto(storedItem)
  }

  @Transactional
  override def removeCartItem(itemId: Integer): Unit = {
    val currentCustomer = customerService.getCurrentCustomer
    val cartItem = getItemById(itemId)
    if (currentCustomer.getCartId != cartItem.getCartId) {
      throw new OperationNotAllowedException(new Error("Operation not allowed!"))
    }
    itemRepository.removeItem(itemId)
    cartService.refreshCartState(currentCustomer.getCartId)
  }

  @Transactional
  override def removeAllByProductId(productId: Integer): Unit = {
    val cartItems = listAllByProductId(productId).asScala
    cartItems.foreach(item => {
      itemRepository.removeItem(item.getCartItemId)
      itemRepository.flush()
    })
  }

  @Transactional(readOnly = true)
  override def listAllByCartId(cartId: Integer): util.List[CartItemDto] = {
    val allItems = itemRepository.findAllByCartId(cartId)
    allItems.stream().map[CartItemDto](tempConverter.cartItemEntityToDto).collect(Collectors.toList())
  }

  @Transactional(readOnly = true)
  override def listAllByProductId(productId: Integer): util.List[CartItemDto] = {
    val allItems = itemRepository.findAllByProductId(productId)
    allItems.stream().map[CartItemDto](tempConverter.cartItemEntityToDto).collect(Collectors.toList())
  }

  @Transactional(readOnly = true)
  override def getItemById(itemId: Integer): CartItemDto = {
    val itemEntity = itemRepository.findById(itemId).orElseThrow(() => new InstanceUndefinedException(new Error("The item has not been found")))
    tempConverter.cartItemEntityToDto(itemEntity)
  }

  @Transactional
  override def removeAllByCartId(cartId: Integer): Unit = {
    val cartItems = listAllByCartId(cartId)
    cartItems.forEach(item => {
      itemRepository.removeItem(item.getCartItemId)
      itemRepository.flush()
    })
    cartService.refreshCartState(cartId)
  }
}
