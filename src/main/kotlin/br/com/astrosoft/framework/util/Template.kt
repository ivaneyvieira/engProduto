package br.com.astrosoft.framework.util

import br.com.astrosoft.framework.util.SystemUtils.readFile

class Template(val filename: String) {
  private var content = readFile(filename)

  fun set(key: String, value: Any) {
    content =  content.replace("[$key]", value.toString())
  }

  fun render() = content
}