package com.radovan.spring.repository

import com.radovan.spring.entity.CustomerEntity
import org.springframework.data.jpa.repository.{JpaRepository, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait CustomerRepository extends JpaRepository[CustomerEntity, Integer]{

  @Query(value = "select * from customers where user_id = :userId",nativeQuery = true)
  def findByUserId(@Param("userId") userId: Integer): Option[CustomerEntity]

}
