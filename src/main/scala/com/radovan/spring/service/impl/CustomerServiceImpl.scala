package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.{CustomerDto, OrderDto, ShippingAddressDto, UserDto}
import com.radovan.spring.entity.{CartEntity, RoleEntity, ShippingAddressEntity, UserEntity}
import com.radovan.spring.exceptions.{ExistingInstanceException, InstanceUndefinedException}
import com.radovan.spring.repository.{CartRepository, CustomerRepository, RoleRepository, ShippingAddressRepository, UserRepository}
import com.radovan.spring.service.{CustomerService, OrderService, UserService}
import com.radovan.spring.utils.RegistrationForm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.stream.Collectors

@Service
class CustomerServiceImpl extends CustomerService{

  private var customerRepository: CustomerRepository = _
  private var userRepository: UserRepository = _
  private var cartRepository: CartRepository = _
  private var addressRepository: ShippingAddressRepository = _
  private var roleRepository: RoleRepository = _
  private var passwordEncoder: BCryptPasswordEncoder = _
  private var tempConverter: TempConverter = _
  private var userService: UserService = _
  private var orderService: OrderService = _

  @Autowired
  private def injectAll(customerRepository: CustomerRepository, userRepository: UserRepository, cartRepository: CartRepository,
                        addressRepository: ShippingAddressRepository, roleRepository: RoleRepository, passwordEncoder: BCryptPasswordEncoder,
                        tempConverter: TempConverter, userService: UserService, orderService: OrderService): Unit = {

    this.customerRepository = customerRepository
    this.userRepository = userRepository
    this.cartRepository = cartRepository
    this.addressRepository = addressRepository
    this.roleRepository = roleRepository
    this.passwordEncoder = passwordEncoder
    this.tempConverter = tempConverter
    this.userService = userService
    this.orderService = orderService
  }

  @Transactional
  override def addCustomer(form: RegistrationForm): CustomerDto = {
    val roleEntity = roleRepository.findByRole("ROLE_USER") match {
      case Some(role) => role
      case None => throw new InstanceUndefinedException(new Error("The role has not been found!"))
    }
    val userEntity = createUserEntity(form.getUser, roleEntity)
    val cartEntity = createCartEntity
    val addressEntity = createShippingAddressEntity(form.getShippingAddress)
    val customerEntity = createCustomerEntity(form.getCustomer, userEntity, cartEntity, addressEntity)
    cartEntity.setCustomer(customerEntity)
    cartRepository.save(cartEntity)
    tempConverter.customerEntityToDto(customerRepository.save(customerEntity))
  }

  private def createUserEntity(userDto: UserDto, roleEntity: RoleEntity) = {
     userRepository.findByEmail(userDto.getEmail) match {
      case Some(_) => throw new ExistingInstanceException(new Error("Email already exists!"))
      case None =>
    }

    val userEntity = tempConverter.userDtoToEntity(userDto)
    userEntity.setPassword(passwordEncoder.encode(userDto.getPassword))
    userEntity.setEnabled(1.toByte)
    userEntity.setRoles(util.List.of[RoleEntity](roleEntity))
    userRepository.save(userEntity)
  }

  private def createCartEntity = {
    val cartEntity = new CartEntity
    cartEntity.setCartPrice(0f)
    cartRepository.save(cartEntity)
  }

  private def createCustomerEntity(customerDto: CustomerDto, userEntity: UserEntity, cartEntity: CartEntity, addressEntity: ShippingAddressEntity) = {
    val customerEntity = tempConverter.customerDtoToEntity(customerDto)
    customerEntity.setShippingAddress(addressEntity)
    customerEntity.setCart(cartEntity)
    customerEntity.setUser(userEntity)
    customerEntity
  }

  private def createShippingAddressEntity(addressDto: ShippingAddressDto) = {
    val addressEntity = tempConverter.shippingAddressDtoToEntity(addressDto)
    addressRepository.save(addressEntity)
  }

  @Transactional(readOnly = true)
  override def getCustomerById(customerId: Integer): CustomerDto = customerRepository.findById(customerId).map[CustomerDto](tempConverter.customerEntityToDto).orElseThrow(() => new InstanceUndefinedException(new Error("The customer has not been found!")))

  @Transactional(readOnly = true)
  override def getCustomerByUserId(userId: Integer): CustomerDto =
    customerRepository.findByUserId(userId)
      .map(tempConverter.customerEntityToDto)
      .getOrElse(throw new InstanceUndefinedException(new Error("The customer has not been found!")))


  @Transactional(readOnly = true)
  override def listAll: util.List[CustomerDto] =
    customerRepository.findAll().stream.map[CustomerDto](tempConverter.customerEntityToDto).collect(Collectors.toList[CustomerDto])

  @Transactional(readOnly = true)
  override def getCurrentCustomer: CustomerDto = {
    val currentUser = userService.getCurrentUser
    getCustomerByUserId(currentUser.getId)
  }

  @Transactional
  override def updateCustomer(customer: CustomerDto): CustomerDto = {
    val currentCustomer = getCurrentCustomer
    customer.setCartId(currentCustomer.getCartId)
    customer.setCustomerId(currentCustomer.getCustomerId)
    customer.setShippingAddressId(currentCustomer.getShippingAddressId)
    customer.setUserId(currentCustomer.getUserId)
    val updatedCustomer = customerRepository.saveAndFlush(tempConverter.customerDtoToEntity(customer))
    tempConverter.customerEntityToDto(updatedCustomer)
  }

  @Transactional
  override def removeCustomer(customerId: Integer): Unit = {
    val customer = getCustomerById(customerId)
    val allOrders = orderService.listAllByCartId(customer.getCartId)
    allOrders.forEach((order: OrderDto) => orderService.deleteOrder(order.getOrderId))
    customerRepository.deleteById(customerId)
    customerRepository.flush()
  }
}
