package br.com.astrosoft.framework.util

import org.jsoup.Jsoup
import java.text.DecimalFormat
import java.text.Normalizer
import java.util.*

private val formatNumber = DecimalFormat("#,##0.00")
private val formatInteger = DecimalFormat("#,##0")

fun Double?.format(): String {
  this ?: return ""
  return formatNumber.format(this)
}

fun Double?.format(pat: String): String {
  this ?: return ""
  return DecimalFormat(pat).format(this)
}

fun Int?.format(): String {
  this ?: return ""
  return formatInteger.format(this)
}

fun String?.lpad(size: Int, filler: String): String {
  var str = this ?: ""
  if (str.length > size) return str.substring(0, size)
  val buf = StringBuilder(str)
  while (buf.length < size) buf.insert(0, filler)

  str = buf.toString()
  return str
}

fun String?.rpad(size: Int, filler: String): String {
  val str = this ?: ""
  if (str.length > size) return str.substring(0, size)
  val buf = StringBuilder(str)
  while (buf.length < size) buf.append(filler)

  return buf.toString()
}

fun String?.trimNull(): String {
  return this?.trim { it <= ' ' } ?: ""
}

fun String.mid(start: Int, len: Int): String {
  return if (this == "") ""
  else {
    val end = start + len
    val pStart = when {
      start < 0       -> 0
      start >= length -> length - 1
      else            -> start
    }
    val pEnd = when {
      end < 0      -> 0
      end > length -> length
      else         -> end
    }
    if (pStart <= pEnd) substring(pStart, pEnd)
    else ""
  }
}

fun String.mid(start: Int): String {
  return mid(start, start + length)
}

fun parameterNames(sql: String): List<String> {
  val regex = Regex(":([a-zA-Z0-9_]+)")
  val matches = regex.findAll(sql)
  return matches.map { it.groupValues }.toList().flatten().filter { !it.startsWith(":") }
}

fun htmlToText(htmlTxt: String?): String {
  htmlTxt ?: return ""
  return Jsoup.parse(htmlTxt.replace("</p>", "\n").replace("<br>", "\n")).wholeText()
}

private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

fun CharSequence.unaccent(): String {
  val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
  return REGEX_UNACCENT.replace(temp, "").uppercase(Locale.getDefault())
}

fun String.customTrim(): String {
  return trim {
    it <= ' '
  }
}