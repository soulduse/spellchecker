package com.dave.spellchecker.util

enum class ResultColors(
    val title: String,
    val beforeHtml: String,
    val afterHtml: String
) {
    GREEN("띄어쓰기", "<em class='green_text'>", "<font color='#1EC545'>"),
    VIOLET("표준어의심", "<em class='violet_text'>", "<font color='#B139F4'>"),
    BLUE("통계적교정", "<em class='blue_text'>", "<font color='#3AACE7'>"),
    RED("맞춤법", "<em class='red_text'>", "<font color='#FC595B'>");

}
