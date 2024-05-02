package com.radovan.spring.converter

import com.radovan.spring.dto._
import com.radovan.spring.entity._

trait TempConverter {

  def cartEntityToDto(cart: CartEntity): CartDto
  def cartDtoToEntity(cart: CartDto): CartEntity

  def cartItemEntityToDto(cartItem: CartItemEntity): CartItemDto
  def cartItemDtoToEntity(cartItem: CartItemDto): CartItemEntity

  def customerEntityToDto(customer: CustomerEntity): CustomerDto
  def customerDtoToEntity(customer: CustomerDto): CustomerEntity

  def shippingAddressEntityToDto(address: ShippingAddressEntity): ShippingAddressDto
  def shippingAddressDtoToEntity(address: ShippingAddressDto): ShippingAddressEntity

  def roleEntityToDto(role: RoleEntity): RoleDto
  def roleDtoToEntity(role: RoleDto): RoleEntity

  def userEntityToDto(user: UserEntity): UserDto
  def userDtoToEntity(user: UserDto): UserEntity

  def categoryEntityToDto(category: ProductCategoryEntity): ProductCategoryDto
  def categoryDtoToEntity(category: ProductCategoryDto): ProductCategoryEntity

  def productEntityToDto(product: ProductEntity): ProductDto
  def productDtoToEntity(product: ProductDto): ProductEntity

  def productImageEntityToDto(image: ProductImageEntity): ProductImageDto
  def productImageDtoToEntity(image: ProductImageDto): ProductImageEntity

  def shippingAddressToOrderAddress(shippingAddress: ShippingAddressDto): OrderAddressDto
  def orderAddressDtoToEntity(address: OrderAddressDto): OrderAddressEntity
  def orderAddressEntityToDto(address: OrderAddressEntity): OrderAddressDto

  def orderItemEntityToDto(orderItem: OrderItemEntity): OrderItemDto
  def orderItemDtoToEntity(orderItem: OrderItemDto): OrderItemEntity

  def orderEntityToDto(order: OrderEntity): OrderDto
  def orderDtoToEntity(order: OrderDto): OrderEntity

  def cartItemToOrderItemDto(cartItem: CartItemDto): OrderItemDto
}

