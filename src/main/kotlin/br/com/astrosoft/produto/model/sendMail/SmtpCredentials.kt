package br.com.astrosoft.produto.model.sendMail

import br.com.astrosoft.framework.model.DB

data class SmtpCredentials(
  val host: String = "smtp.gmail.com",
  val port: Int = 587,
  val fromEmail: String,
  val username: String,
  val password: String
) {
  companion object {
    fun default(): SmtpCredentials {
      val username = DB.garantiaUser
      val password = DB.garantiaPass
      val fromEmail = "${DB.garantiaName} <$username>"
      return SmtpCredentials(username = username, password = password, fromEmail = fromEmail)
    }
  }
}