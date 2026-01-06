import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int, val nombre: String,
    val apellido: String,
    val correo: String,
    val rol: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val usuario: UserDto
)
