package br.com.astrosoft.produto.model.sendMail

import br.com.astrosoft.framework.model.DB

data class SmtpCredentials(
  val host: String = "smtp.gmail.com",
  val port: Int = 587,
  val username: String,
  val password: String
) {
  companion object {
    fun default(): SmtpCredentials {
      val username = DB.gmailUser
      val password = DB.gmailPass
      return SmtpCredentials(username = username, password = password)
    }
  }
}