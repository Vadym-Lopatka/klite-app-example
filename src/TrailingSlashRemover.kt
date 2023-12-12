package app.bp

import klite.BadRequestException
import klite.Before
import klite.HttpExchange

class TrailingSlashRemover : Before {
    override suspend fun before(ex: HttpExchange) {
        when {
            ex.path.endsWith("//") -> throw BadRequestException("Can't parse the path: ${ex.path}")
            ex.path.endsWith("/") -> ex.redirect(ex.path.substring(0, ex.path.length - 1))
        }
    }
}
