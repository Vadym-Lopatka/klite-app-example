import app.bp.TrailingSlashRemover
import ch.tutteli.atrium.api.fluent.en_GB.messageToContain
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import klite.BadRequestException
import klite.HttpExchange
import klite.RedirectException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TrailingSlashRemoverTest {
    private val exchange = mockk<HttpExchange>(relaxed = true)
    private val trailingSlashRemover = TrailingSlashRemover()

    @Test fun `should do nothing when trailing slash is absent`() {
        every { exchange.path } returns "/hello"
        runBlocking { trailingSlashRemover.before(exchange) }
        verify(exactly = 0) { exchange.redirect(any<String>()) }
    }

    @Test fun `should throw 400 BadRequest when several trailing slashes`() {
        every { exchange.path } returns "/hello//"
        expect { runBlocking { trailingSlashRemover.before(exchange) }}.toThrow<BadRequestException>()
            .messageToContain("Can't parse the path: /hello//")
        verify(exactly = 0) { exchange.redirect(any<String>()) }
    }

    @Test fun `should remove last slash`() {
        every { exchange.path } returns "/hello/"
        every { exchange.redirect(any<String>()) } answers { callOriginal() }
        expect { runBlocking { trailingSlashRemover.before(exchange) }}.toThrow<RedirectException>()
        verify(exactly = 1) { exchange.redirect("/hello") }
    }

}