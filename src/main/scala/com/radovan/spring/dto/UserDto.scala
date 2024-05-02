package com.radovan.spring.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access
import java.util
import scala.beans.BeanProperty
import jakarta.validation.constraints.{Email, NotEmpty, Size}


@SerialVersionUID(1L)
class UserDto extends Serializable {

  @BeanProperty
  var id: Integer = _

  @BeanProperty
  @NotEmpty
  @Size(min = 2, max = 30)
  var firstName: String = _

  @BeanProperty
  @NotEmpty
  @Size(min = 2, max = 30)
  var lastName: String = _

  @BeanProperty
  @NotEmpty
  @Size(max = 50)
  @Email(regexp = ".+[@].+[\\.].+")
  var email: String = _

  @BeanProperty
  @NotEmpty
  @Size(min = 6)
  @JsonProperty(access = Access.WRITE_ONLY)
  var password: String = _

  @BeanProperty
  var enabled: Short = _

  @BeanProperty
  var rolesIds: util.List[Integer] = _

  @BeanProperty
  var authToken: String = _

  override def toString: String = s"UserDto [firstName=$firstName, lastName=$lastName, email=$email, password=$password, enabled=$enabled]"

}
