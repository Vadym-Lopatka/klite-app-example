package app.bp

import klite.Before
import klite.HttpExchange

class TrailingSlashRemover : Before {
    override suspend fun before(exchange: HttpExchange) {
        if (exchange.path.endsWith("/"))
            exchange.redirect(exchange.path.substring(0, exchange.path.length - 1))
    }
}
