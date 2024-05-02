package com.radovan.spring.repository

import java.util
import com.radovan.spring.entity.CartItemEntity
import org.springframework.data.jpa.repository.{JpaRepository, Modifying, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait CartItemRepository extends JpaRepository[CartItemEntity, Integer]{

  @Modifying
  @Query(value="delete from cart_items where id = :itemId",nativeQuery = true)
  def removeItem(@Param("itemId")itemId: Integer):Unit

  @Query(value = "select * from cart_items where product_id = :productId",nativeQuery = true)
  def findAllByProductId(@Param("productId") productId: Integer):util.List[CartItemEntity]

  @Query(value = "select * from cart_items where cart_id = :cartId",nativeQuery = true)
  def findAllByCartId(@Param("cartId") cartId: Integer):util.List[CartItemEntity]

}
