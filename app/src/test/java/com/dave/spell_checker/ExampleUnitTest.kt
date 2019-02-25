package com.dave.spell_checker

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `html 파싱`() {
        "<font color='red'>simple</font>"
        "green_text"
        "violet_text"
        "red_text"
        "blue_text"

        val htmlText = "안녕하세요 밥을 잘 먹었니? 오늘도 나는 울다 지쳐 잠을 들어도 <em class='red_text'>티브이 사람들은 할 말이</em> 있지만 오늘도 나는 달린다 내일이 <em class='blue_text'>있기 때문이다</em>"
//        val html = Jsoup.parse(htmlText)
//        val convertHtml = html.select("em").map {
//            val className = it.attr("class")
//
//        }

        val convertedHtml = htmlText
            .replace("<em class='green_text'>", "<font color='greed'>")
            .replace("<em class='violet_text'>", "<font color='violet'>")
            .replace("<em class='blue_text'>", "<font color='blue'>")
            .replace("<em class='red_text'>", "<font color='red'>")
            .replace("</em>", "</font>")

        println(convertedHtml)
    }
}
