package com.github.oowekyala.ooxml.messages

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FunSpec

/*
   Error messages are localized, locale is set to english in pom.xml
 */


class MalformedXmlTest : FunSpec({

    test("Test malformed xml 1") {

        val expected = """
$HEADER
<list>
    <list
        <str>oha</str>
        <str>what</str>
    </list>
    <moh>
        <str>are</str>
        <str>you</str>
    </moh>
</list>
        """.trimIndent()

        val printer = TestMessagePrinter()

        val ex = shouldThrow<XmlException> {
            expected.parseStr(printer)
        }

        ex.toString().shouldBe(
                """
XML parsing error
    2| <list>
    3|     <list
    4|         <str>oha</str>
               ^ Element type "list" must be followed by either attribute specifications, ">" or "/>".


    5|         <str>what</str>
    6|     </list>
""".trimIndent()

        )

        printer.err.shouldBe(listOf(ex.toString()))
    }

    test("Test malformed entities") {

        val expected = """
$HEADER
<list>
    <list foo="&amb;"/>
</list>
        """.trimIndent()

        val printer = TestMessagePrinter()

        val ex = shouldThrow<XmlException> {
            expected.parseStr(printer)
        }

        ex.toString().shouldBe(
                """
XML parsing error
    1| $HEADER
    2| <list>
    3|     <list foo="&amb;"/>
                           ^ The entity "amb" was referenced, but not declared.


    4| </list>
""".trimIndent()

        )

        printer.err.shouldBe(listOf(ex.toString()))
    }

    test("Test empty document") {

        val expected = """
$HEADER
""".trimIndent()

        val printer = TestMessagePrinter()

        val ex = shouldThrow<XmlException> {
            expected.parseStr(printer)
        }

        ex.toString().shouldBe(
"""XML parsing error
    1| <?xml version="1.0" encoding="UTF-8" standalone="no"?>
                                                             ^ Premature end of file.

""".trim()
        )

        printer.err.shouldBe(listOf(ex.toString()))
    }

})
