package generic.argument.issue

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.type.Argument
import io.micronaut.core.type.GenericArgument
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import kotlinx.coroutines.reactive.awaitSingle

@MicronautTest
@Property(name = "spec.name", value = "GenericArgumentIssueTest")
class GenericArgumentIssueTest(
    private val embeddedServer: EmbeddedServer,
): ShouldSpec({
    val clientContext: ApplicationContext = ApplicationContext.run(
        mapOf("my.port" to embeddedServer.port)
    )

    val httpClient: HttpClient = clientContext.createBean(HttpClient::class.java, embeddedServer.url)

    should("use basic client") {
        val result: Int = httpClient.retrieve(HttpRequest.GET<Any>("/httptest/someint"), Int::class.java).awaitSingle()
        result shouldBe 1
    }

    should("make this argument") {
        val arg: Argument<Pair<String, Boolean>> = object: GenericArgument<Pair<String, Boolean>>() {}
    }

    should("use GenericArgument properly for retrieveGenericSingle") {
        val arg = object: GenericArgument<Pair<String, Boolean>>() {}
        val result: Pair<String, Boolean> = httpClient.retrieve(HttpRequest.GET<Any>("/httptest/somepair"), arg).awaitSingle()

        result.first shouldBe "a"
        result.second shouldBe true
    }
}) {
    @Requires(property = "spec.name", value = "GenericArgumentIssueTest")
    @Controller("/httptest", produces = [MediaType.APPLICATION_JSON])
    class TestController {
        @Get("/someint")
        fun someInt(): Int = 1

        @Get("/someset")
        fun someSet(): Set<Int> = setOf(1, 2, 3)

        @Get("/somepair")
        fun somePair(): Pair<String, Boolean> = Pair("a", true)
    }
}