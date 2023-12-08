package app.bp.users

import app.bp.DBTest
import ch.tutteli.atrium.api.fluent.en_GB.toBeEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.junit.jupiter.api.Test
import java.util.Locale.ENGLISH

class UserRepositoryTest: DBTest() {
  val user = User(Email("test@user.com"), "Test", "User", ENGLISH, "phash")
  val repository = UserRepository(db)

  @Test fun `save and load`() {
    repository.save(user)
    expect(repository.get(user.id)).toEqual(user)
    expect(repository.by(user.email)).toEqual(user)
    expect(repository.by(Email("no@email"))).toEqual(null)

    expect(repository.search("SER")).toContain(user)
    expect(repository.search("blah")).toBeEmpty()
  }
}
