package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.CartDto
import com.radovan.spring.exceptions.{InstanceUndefinedException, InvalidCartException}
import com.radovan.spring.repository.CartRepository
import com.radovan.spring.service.{CartItemService, CartService, ProductService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._

@Service
class CartServiceImpl extends CartService{

  private var cartRepository: CartRepository = _
  private var tempConverter: TempConverter = _
  private var cartItemService: CartItemService = _
  private var productService: ProductService = _

  @Autowired
  private def injectAll(cartRepository: CartRepository, tempConverter: TempConverter, cartItemService: CartItemService,
                        productService: ProductService): Unit = {
    this.cartRepository = cartRepository
    this.tempConverter = tempConverter
    this.cartItemService = cartItemService
    this.productService = productService
  }

  @Transactional(readOnly = true)
  override def getCartById(cartId: Integer): CartDto = {
    val cartEntity = cartRepository.findById(cartId).orElseThrow(() => new InstanceUndefinedException(new Error("The cart has not been found")))
    tempConverter.cartEntityToDto(cartEntity)
  }

  @Transactional(readOnly = true)
  override def validateCart(cartId: Integer): CartDto = {
    val cart = getCartById(cartId)
    if (cart.getCartItemsIds.isEmpty) throw new InvalidCartException(new Error("Your cart is currently empty!"))
    cart
  }

  @Transactional(readOnly = true)
  override def calculateGrandTotal(cartId: Integer): Float = {
    val cartItems = cartItemService.listAllByCartId(cartId).asScala
    val grandTotal = cartItems.map(_.getPrice).sum
    (grandTotal * 100).round / 100f
  }

  @Transactional
  override def refreshCartState(cartId: Integer): Unit = {
    val cart = getCartById(cartId)
    val cartPrice = cartRepository.calculateCartPrice(cartId).getOrElse(0f)
    cart.setCartPrice(cartPrice)
    cartRepository.saveAndFlush(tempConverter.cartDtoToEntity(cart))
  }

  @Transactional
  override def refreshAllCarts(): Unit = {
    val allCarts = cartRepository.findAll().asScala
    allCarts.foreach(cart => refreshCartState(cart.getCartId))
  }
}
