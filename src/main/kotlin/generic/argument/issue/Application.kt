package generic.argument.issue

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("generic.argument.issue")
		.start()
}

