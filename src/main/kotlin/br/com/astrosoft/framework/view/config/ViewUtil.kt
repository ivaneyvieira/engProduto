package br.com.astrosoft.framework.view.config

import br.com.astrosoft.framework.util.SystemUtils

object ViewUtil {
  val versao: String
    get() {
      val arquivo = "/versao.txt"
      return SystemUtils.readFile(arquivo)
    }
}