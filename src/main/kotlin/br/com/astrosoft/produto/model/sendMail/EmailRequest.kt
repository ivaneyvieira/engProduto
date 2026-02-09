package br.com.astrosoft.produto.model.sendMail

class Anexo(
    val filename: String,
    val mimeType: String,
    val dados: ByteArray
)

data class EmailRequest(
  val smtp: SmtpCredentials = SmtpCredentials.default(),

  val fromEmail: String = smtp.username,

  val to: List<String>,
  val cc: List<String> = emptyList(),
  val bcc: List<String> = emptyList(),

  val subject: String,
  val htmlContent: String,

  val anexos: List<Anexo> = emptyList()
)
