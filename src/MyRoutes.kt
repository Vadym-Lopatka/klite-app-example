package app.bp

import app.bp.users.Id
import app.bp.users.User
import app.bp.users.UserRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import klite.HttpExchange
import klite.StatusCode
import klite.annotations.*
import klite.i18n.translate
import kotlinx.coroutines.delay

@Path("/hello")
@Tag(name = "cool", description = "Cool routes")
class MyRoutes(private val userRepository: UserRepository) {
    @GET
    @Operation(summary = "Just a hello")
    fun sayHello() = MyData("Hello")
    @GET("2")
    fun withExchange(exchange: HttpExchange) = "Hello2 ${exchange.method} ${exchange.path}"
    @GET("3")
    fun HttpExchange.asContext() = "${translate("hello")} $method $path"

    @GET("/user/:id")
    fun user(@PathParam id: Id<User>) = userRepository.get(id)

    @GET("/suspend")
    suspend fun suspend(exchange: HttpExchange): String {
        delay(100)
        return "Suspend ${exchange.method} ${exchange.path}"
    }

    @GET("/suspend204")
    suspend fun suspendNoContent() = delay(50)
    @POST("/201")
    fun created() = StatusCode.Created
    @GET("/null")
    fun returnNull() = null

    @GET("/admin")
    @AdminOnly
    fun onlyForAdmins() = "Only for admins"
    @POST("/admin")
    @AdminOnly
    fun onlyForAdminsPost() = "Only for admins"
    @DELETE("/admin")
    @AdminOnly
    fun onlyForAdminsDelete() = "Only for admins"

    @GET("/params")
    fun withOptionalParams(
        @QueryParam optional: Boolean = false,
        @QueryParam required: String,
        @QueryParam nullable: String?
    ) = "$optional,$required,$nullable"

    @GET("/broken-render")
    fun brokenRender() = object {
        lateinit var hello: String
    }

    @POST("/post")
    fun postExample(body: MyData, @QueryParam optional: Boolean = true) =
        "Received $body as json, optional = $optional"
}

data class MyData(val hello: String, val world: Double = Math.PI)
