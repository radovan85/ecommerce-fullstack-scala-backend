package com.radovan.spring.converter

import java.text.DecimalFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util
import java.util.Optional
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.radovan.spring.dto.CartDto
import com.radovan.spring.dto.CartItemDto
import com.radovan.spring.dto.CustomerDto
import com.radovan.spring.dto.OrderAddressDto
import com.radovan.spring.dto.OrderDto
import com.radovan.spring.dto.OrderItemDto
import com.radovan.spring.dto.ProductCategoryDto
import com.radovan.spring.dto.ProductDto
import com.radovan.spring.dto.ProductImageDto
import com.radovan.spring.dto.RoleDto
import com.radovan.spring.dto.ShippingAddressDto
import com.radovan.spring.dto.UserDto
import com.radovan.spring.entity.CartEntity
import com.radovan.spring.entity.CartItemEntity
import com.radovan.spring.entity.CustomerEntity
import com.radovan.spring.entity.OrderAddressEntity
import com.radovan.spring.entity.OrderEntity
import com.radovan.spring.entity.OrderItemEntity
import com.radovan.spring.entity.ProductCategoryEntity
import com.radovan.spring.entity.ProductEntity
import com.radovan.spring.entity.ProductImageEntity
import com.radovan.spring.entity.RoleEntity
import com.radovan.spring.entity.ShippingAddressEntity
import com.radovan.spring.entity.UserEntity
import com.radovan.spring.repository.CartItemRepository
import com.radovan.spring.repository.CartRepository
import com.radovan.spring.repository.CustomerRepository
import com.radovan.spring.repository.OrderItemRepository
import com.radovan.spring.repository.OrderRepository
import com.radovan.spring.repository.ProductCategoryRepository
import com.radovan.spring.repository.ProductImageRepository
import com.radovan.spring.repository.ProductRepository
import com.radovan.spring.repository.RoleRepository
import com.radovan.spring.repository.ShippingAddressRepository
import com.radovan.spring.repository.UserRepository

import scala.collection.JavaConverters._
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

@Component
class TempConverterExtension extends TempConverter {

  private var mapper: ModelMapper = _
  private var customerRepository: CustomerRepository = _
  private var cartItemRepository: CartItemRepository = _
  private var userRepository: UserRepository = _
  private var roleRepository: RoleRepository = _
  private var shippingAddressRepository: ShippingAddressRepository = _
  private var cartRepository: CartRepository = _
  private var imageRepository: ProductImageRepository = _
  private var categoryRepository: ProductCategoryRepository = _
  private var productRepository: ProductRepository = _
  private var orderRepository: OrderRepository = _
  private var orderItemRepository: OrderItemRepository = _
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
  private val decfor = new DecimalFormat("0.00")
  private val zoneId = ZoneId.of("UTC")

  override def cartEntityToDto(cart: CartEntity): CartDto = {
    val returnValue = mapper.map(cart, classOf[CartDto])
    returnValue.setCustomerId(Option(cart.getCustomer).map(_.getCustomerId).orNull)
    returnValue.setCartItemsIds(Option(cart.getCartItems).map(_.map(_.getCartItemId).toList.asJava).getOrElse(new util.ArrayList()))
    returnValue.setCartPrice(decfor.format(returnValue.getCartPrice).toFloat)
    returnValue
  }


  override def cartDtoToEntity(cart: CartDto): CartEntity = {
    val returnValue = mapper.map(cart, classOf[CartEntity])
    val customerIdOptional = Optional.ofNullable(cart.getCartId)
    if (customerIdOptional.isPresent) {
      val customerId = customerIdOptional.get
      val customerEntity = customerRepository.findById(customerId).orElse(null)
      returnValue.setCustomer(customerEntity)
    }
    val cartItemsIdsOptional = Optional.ofNullable(cart.getCartItemsIds)
    val cartItems = new util.ArrayList[CartItemEntity]
    if (cartItemsIdsOptional.isPresent) {
      cartItemsIdsOptional.get.asScala.foreach { itemId =>
        val itemOptional = cartItemRepository.findById(itemId)
        if (itemOptional.isPresent) cartItems.add(itemOptional.get)
      }
    }
    returnValue.setCartItems(cartItems)
    returnValue.setCartPrice(decfor.format(returnValue.getCartPrice).toFloat)
    returnValue
  }


  override def roleEntityToDto(role: RoleEntity): RoleDto = {
    val returnValue = mapper.map(role, classOf[RoleDto])
    val usersIds = new util.ArrayList[Integer]
    val usersOptional = Optional.ofNullable(role.getUsers)
    if (usersOptional.isPresent) {
      val users = usersOptional.get
      for (userEntity <- users.asScala) {
        usersIds.add(userEntity.getId)
      }
    }
    returnValue.setUsersIds(usersIds)
    returnValue
  }


  override def roleDtoToEntity(role: RoleDto): RoleEntity = {
    val returnValue = mapper.map(role, classOf[RoleEntity])
    val usersIdsOptional = Optional.ofNullable(role.getUsersIds)
    val users = new util.ArrayList[UserEntity]
    if (usersIdsOptional.isPresent) {
      val usersIds = usersIdsOptional.get
      usersIds.forEach(userId => {
        val userEntity = userRepository.findById(userId).orElse(null)
        if (userEntity != null) {
          users.add(userEntity)
        }
      })
    }
    returnValue.setUsers(users)
    returnValue
  }


  def userEntityToDto(userEntity: UserEntity): UserDto = {
    val returnValue = mapper.map(userEntity, classOf[UserDto])
    returnValue.setEnabled(userEntity.getEnabled)
    val rolesOpt = Optional.ofNullable(userEntity.getRoles)
    val rolesIds = new util.ArrayList[Integer]
    if (!rolesOpt.isEmpty) rolesOpt.get.forEach((roleEntity: RoleEntity) => {
      rolesIds.add(roleEntity.getId)
    })
    returnValue.setRolesIds(rolesIds)
    returnValue
  }

  def userDtoToEntity(userDto: UserDto): UserEntity = {
    val returnValue = mapper.map(userDto, classOf[UserEntity])
    val roles = new util.ArrayList[RoleEntity]
    val rolesIdsOpt = Optional.ofNullable(userDto.getRolesIds)
    if (!rolesIdsOpt.isEmpty) rolesIdsOpt.get.forEach((roleId: Integer) => {
      val role = roleRepository.findById(roleId).get
      roles.add(role)
    })
    returnValue.setRoles(roles)
    returnValue
  }


  def customerEntityToDto(customer: CustomerEntity): CustomerDto = {
    val returnValue = mapper.map(customer, classOf[CustomerDto])
    val addressOptional = Optional.ofNullable(customer.getShippingAddress)
    if (addressOptional.isPresent) returnValue.setShippingAddressId(addressOptional.get.getShippingAddressId)
    val cartOptional = Optional.ofNullable(customer.getCart)
    if (cartOptional.isPresent) returnValue.setCartId(cartOptional.get.getCartId)
    val userOptional = Optional.ofNullable(customer.getUser)
    if (userOptional.isPresent) returnValue.setUserId(userOptional.get.getId)
    returnValue
  }


  override def customerDtoToEntity(customer: CustomerDto): CustomerEntity = {
    val returnValue = mapper.map(customer, classOf[CustomerEntity])

    val addressIdOptional = Optional.ofNullable(customer.getShippingAddressId)
    if (addressIdOptional.isPresent) {
      val addressId = addressIdOptional.get
      val addressEntity = shippingAddressRepository.findById(addressId).orElse(null)
      returnValue.setShippingAddress(addressEntity)
    }

    val cartIdOptional = Optional.ofNullable(customer.getCartId)
    if (cartIdOptional.isPresent) {
      val cartId = cartIdOptional.get
      val cartEntity = cartRepository.findById(cartId).orElse(null)
      returnValue.setCart(cartEntity)
    }

    val userIdOptional = Optional.ofNullable(customer.getUserId)
    if (userIdOptional.isPresent) {
      val userId = userIdOptional.get
      val userEntity = userRepository.findById(userId).orElse(null)
      returnValue.setUser(userEntity)
    }

    returnValue
  }


  override def shippingAddressEntityToDto(address: ShippingAddressEntity): ShippingAddressDto = {
    val returnValue = mapper.map(address, classOf[ShippingAddressDto])
    val customerOptional = Optional.ofNullable(address.getCustomer)
    if (customerOptional.isPresent) returnValue.setCustomerId(customerOptional.get.getCustomerId)
    returnValue
  }

  override def shippingAddressDtoToEntity(address: ShippingAddressDto): ShippingAddressEntity = {
    val returnValue = mapper.map(address, classOf[ShippingAddressEntity])
    val customerIdOptional = Optional.ofNullable(address.getCustomerId)
    if (customerIdOptional.isPresent) {
      val customerId = customerIdOptional.get
      val customerEntity = customerRepository.findById(customerId).orElse(null)
      returnValue.setCustomer(customerEntity)
    }
    returnValue
  }

  override def categoryEntityToDto(category: ProductCategoryEntity): ProductCategoryDto = mapper.map(category, classOf[ProductCategoryDto])

  override def categoryDtoToEntity(category: ProductCategoryDto): ProductCategoryEntity = mapper.map(category, classOf[ProductCategoryEntity])

  override def productEntityToDto(product: ProductEntity): ProductDto = {
    val returnValue = mapper.map(product, classOf[ProductDto])
    returnValue.setProductPrice(decfor.format(returnValue.getProductPrice).toFloat)
    returnValue.setDiscount(decfor.format(returnValue.getDiscount).toFloat)
    val imageOptional = Optional.ofNullable(product.getImage)
    if (imageOptional.isPresent) returnValue.setImageId(imageOptional.get.getId)
    val categoryOptional = Optional.ofNullable(product.getProductCategory)
    if (categoryOptional.isPresent) returnValue.setProductCategoryId(categoryOptional.get.getProductCategoryId)
    returnValue
  }

  override def productDtoToEntity(product: ProductDto): ProductEntity = {
    val returnValue = mapper.map(product, classOf[ProductEntity])
    returnValue.setProductPrice(decfor.format(returnValue.getProductPrice).toFloat)
    returnValue.setDiscount(decfor.format(returnValue.getDiscount).toFloat)
    val imageIdOptional = Optional.ofNullable(product.getImageId)
    if (imageIdOptional.isPresent) {
      val imageId = imageIdOptional.get
      val imageEntity = imageRepository.findById(imageId).orElse(null)
      returnValue.setImage(imageEntity)
    }

    val categoryIdOptional = Optional.ofNullable(product.getProductCategoryId)
    if (categoryIdOptional.isPresent) {
      val categoryId = categoryIdOptional.get
      val categoryEntity = categoryRepository.findById(categoryId).orElse(null)
      returnValue.setProductCategory(categoryEntity)
    }

    returnValue
  }


  override def productImageEntityToDto(image: ProductImageEntity): ProductImageDto = {
    val returnValue = mapper.map(image, classOf[ProductImageDto])
    val productOptional = Optional.ofNullable(image.getProduct)
    if (productOptional.isPresent) returnValue.setProductId(productOptional.get.getProductId)
    returnValue
  }

  override def productImageDtoToEntity(image: ProductImageDto): ProductImageEntity = {
    val returnValue = mapper.map(image, classOf[ProductImageEntity])
    val productIdOptional = Optional.ofNullable(image.getProductId)
    if (productIdOptional.isPresent) {
      val productId = productIdOptional.get
      val productEntity = productRepository.findById(productId).orElse(null)
      if (productEntity != null) returnValue.setProduct(productEntity)
    }
    returnValue
  }

  override def cartItemEntityToDto(cartItem: CartItemEntity): CartItemDto = {
    val returnValue = mapper.map(cartItem, classOf[CartItemDto])

    val productOptional = Optional.ofNullable(cartItem.getProduct)
    if (productOptional.isPresent) {
      val product = productOptional.get
      val discount = product.getDiscount
      val productPrice = product.getProductPrice
      val itemPrice = productPrice * (1 - discount / 100) * cartItem.getQuantity
      returnValue.setPrice(decfor.format(itemPrice).toFloat)
      returnValue.setProductId(product.getProductId)
    }

    val cartOptional = Optional.ofNullable(cartItem.getCart)
    if (cartOptional.isPresent) returnValue.setCartId(cartOptional.get.getCartId)

    returnValue
  }


  override def cartItemDtoToEntity(cartItem: CartItemDto): CartItemEntity = {
    val returnValue = mapper.map(cartItem, classOf[CartItemEntity])

    val productIdOptional = Optional.ofNullable(cartItem.getProductId)
    if (productIdOptional.isPresent) {
      val productId = productIdOptional.get
      val productEntity = productRepository.findById(productId).orElse(null)
      if (productEntity != null) {
        val discount = productEntity.getDiscount
        val productPrice = productEntity.getProductPrice
        val itemPrice = productPrice * (1 - discount / 100) * cartItem.getQuantity
        returnValue.setPrice(decfor.format(itemPrice).toFloat)
        returnValue.setProduct(productEntity)
      }
    }

    val cartIdOptional = Optional.ofNullable(cartItem.getCartId)
    if (cartIdOptional.isPresent) {
      val cartId = cartIdOptional.get
      val cartEntity = cartRepository.findById(cartId).orElse(null)
      if (cartEntity != null) returnValue.setCart(cartEntity)
    }

    returnValue
  }


  override def shippingAddressToOrderAddress(shippingAddress: ShippingAddressDto): OrderAddressDto = mapper.map(shippingAddress, classOf[OrderAddressDto])

  override def orderAddressEntityToDto(address: OrderAddressEntity): OrderAddressDto = {
    val returnValue = mapper.map(address, classOf[OrderAddressDto])
    val orderOptional = Optional.ofNullable(address.getOrder)
    if (orderOptional.isPresent) returnValue.setOrderId(orderOptional.get.getOrderId)
    returnValue
  }

  override def orderAddressDtoToEntity(address: OrderAddressDto): OrderAddressEntity = {
    val returnValue = mapper.map(address, classOf[OrderAddressEntity])
    val orderIdOptional = Optional.ofNullable(address.getOrderId)
    if (orderIdOptional.isPresent) {
      val orderId = orderIdOptional.get
      val orderEntity = orderRepository.findById(orderId).orElse(null)
      if (orderEntity != null) returnValue.setOrder(orderEntity)
    }
    returnValue
  }

  override def orderItemEntityToDto(orderItem: OrderItemEntity): OrderItemDto = {
    val returnValue = mapper.map(orderItem, classOf[OrderItemDto])
    val orderOptional = Optional.ofNullable(orderItem.getOrder)
    if (orderOptional.isPresent) returnValue.setOrderId(orderOptional.get.getOrderId)
    returnValue
  }

  override def orderItemDtoToEntity(orderItem: OrderItemDto): OrderItemEntity = {
    val returnValue = mapper.map(orderItem, classOf[OrderItemEntity])
    val orderIdOptional = Optional.ofNullable(orderItem.getOrderId)
    if (orderIdOptional.isPresent) {
      val orderId = orderIdOptional.get
      val orderEntity = orderRepository.findById(orderId).orElse(null)
      if (orderEntity != null) returnValue.setOrder(orderEntity)
    }
    returnValue
  }

  override def orderEntityToDto(order: OrderEntity): OrderDto = {
    val returnValue = mapper.map(order, classOf[OrderDto])

    val orderItemsIds = new util.ArrayList[Integer]
    val itemsOptional = Optional.ofNullable(order.getOrderedItems)
    if (!itemsOptional.isEmpty) {
      itemsOptional.get.forEach(item => orderItemsIds.add(item.getOrderItemId))
    }
    returnValue.setOrderedItemsIds(orderItemsIds)

    val addressOptional = Optional.ofNullable(order.getAddress)
    if (addressOptional.isPresent) {
      returnValue.setAddressId(addressOptional.get.getOrderAddressId)
    }

    val createdAtOptional = Optional.ofNullable(order.getCreatedAt)
    if (createdAtOptional.isPresent) {
      val createdAtZoned = createdAtOptional.get.toLocalDateTime.atZone(zoneId)
      val createdAtStr = createdAtZoned.format(formatter)
      returnValue.setCreatedAt(createdAtStr)
    }

    val cartOptional = Optional.ofNullable(order.getCart)
    if (cartOptional.isPresent) {
      returnValue.setCartId(cartOptional.get.getCartId)
    }

    returnValue
  }


  override def orderDtoToEntity(order: OrderDto): OrderEntity = {
    val returnValue = mapper.map(order, classOf[OrderEntity])

    val itemsIdsOptional = Optional.ofNullable(order.getOrderedItemsIds)
    val orderedItems = new util.ArrayList[OrderItemEntity]
    if (!itemsIdsOptional.isEmpty) {
      itemsIdsOptional.get.forEach(itemId => {
        val itemEntity = orderItemRepository.findById(itemId).orElse(null)
        if (itemEntity != null) {
          orderedItems.add(itemEntity)
        }
      })
    }
    returnValue.setOrderedItems(orderedItems)

    val cartIdOptional = Optional.ofNullable(order.getCartId)
    if (cartIdOptional.isPresent) {
      val cartId = cartIdOptional.get
      val cartEntity = cartRepository.findById(cartId).orElse(null)
      if (cartEntity != null) {
        returnValue.setCart(cartEntity)
      }
    }

    returnValue
  }


  override def cartItemToOrderItemDto(cartItem: CartItemDto): OrderItemDto = {
    val returnValue = mapper.map(cartItem, classOf[OrderItemDto])

    val productIdOptional = Optional.ofNullable(cartItem.getProductId)
    if (productIdOptional.isPresent) {
      val productId = productIdOptional.get
      val productEntity = productRepository.findById(productId).orElse(null)
      if (productEntity != null) {
        returnValue.setProductDiscount(productEntity.getDiscount)
        returnValue.setProductName(productEntity.getProductName)
        returnValue.setProductPrice(decfor.format(productEntity.getProductPrice).toFloat)
      }
    }

    returnValue
  }


  @Autowired
  private def injectAll(mapper: ModelMapper, customerRepository: CustomerRepository, cartItemRepository: CartItemRepository, userRepository: UserRepository, roleRepository: RoleRepository, shippingAddressRepository: ShippingAddressRepository, cartRepository: CartRepository, imageRepository: ProductImageRepository, categoryRepository: ProductCategoryRepository, productRepository: ProductRepository, orderRepository: OrderRepository, orderItemRepository: OrderItemRepository): Unit = {
    this.mapper = mapper
    this.customerRepository = customerRepository
    this.cartItemRepository = cartItemRepository
    this.userRepository = userRepository
    this.roleRepository = roleRepository
    this.shippingAddressRepository = shippingAddressRepository
    this.cartRepository = cartRepository
    this.imageRepository = imageRepository
    this.categoryRepository = categoryRepository
    this.productRepository = productRepository
    this.orderRepository = orderRepository
    this.orderItemRepository = orderItemRepository
  }
}
