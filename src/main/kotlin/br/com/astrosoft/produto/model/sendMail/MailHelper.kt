package br.com.astrosoft.produto.model.sendMail

import com.sun.mail.smtp.SMTPAddressFailedException
import jakarta.activation.DataHandler
import jakarta.activation.DataSource
import jakarta.mail.*
import jakarta.mail.internet.*
import jakarta.mail.util.ByteArrayDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

// ---------------------------
// API pública
// ---------------------------
suspend fun sendEmailAsync(request: EmailRequest): Result<Unit> =
    withContext(Dispatchers.IO) {
      runCatching {
        validateRequest(request)

        val session = buildSession(request)
        val message = buildMessage(session, request)

        Transport.send(message)
      }.recoverCatching { ex ->
        throw mapToDomainException(ex)
      }
    }

// ---------------------------
// Validações
// ---------------------------
private fun validateRequest(request: EmailRequest) {
  require(request.to.isNotEmpty()) {
    "A lista de destinatários (to) não pode estar vazia"
  }

  // Valida apenas SINTAXE (não garante existência)
  val allRecipients = request.to + request.cc + request.bcc
  val invalidFormat = allRecipients.filterNot { it.isEmailSyntaxValid() }
  require(invalidFormat.isEmpty()) {
    "Formato de e-mail inválido: ${invalidFormat.joinToString(", ")}"
  }
}

private fun String.isEmailSyntaxValid(): Boolean =
    try {
      InternetAddress(this).validate()
      true
    } catch (_: AddressException) {
      false
    }

// ---------------------------
// Session / Message
// ---------------------------
private fun buildSession(request: EmailRequest): Session {
  val props = Properties().apply {
    put("mail.smtp.host", request.smtp.host)
    put("mail.smtp.port", request.smtp.port.toString())
    put("mail.smtp.auth", "true")

    // STARTTLS (porta 587 geralmente)
    put("mail.smtp.starttls.enable", "true")
    put("mail.smtp.starttls.required", "true")

    // Timeouts / robustez
    put("mail.smtp.connectiontimeout", "10000")
    put("mail.smtp.timeout", "20000")
    put("mail.smtp.writetimeout", "20000")
    put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3")
  }

  return Session.getInstance(props, object : Authenticator() {
    override fun getPasswordAuthentication(): PasswordAuthentication =
        PasswordAuthentication(request.smtp.username, request.smtp.password)
  })
}

private fun buildMessage(session: Session, request: EmailRequest): MimeMessage {
  val msg = MimeMessage(session).apply {
    setFrom(InternetAddress(request.fromEmail))

    setRecipients(
      Message.RecipientType.TO,
      request.to.map(::InternetAddress).toTypedArray()
    )

    if (request.cc.isNotEmpty()) {
      setRecipients(
        Message.RecipientType.CC,
        request.cc.map(::InternetAddress).toTypedArray()
      )
    }

    if (request.bcc.isNotEmpty()) {
      setRecipients(
        Message.RecipientType.BCC,
        request.bcc.map(::InternetAddress).toTypedArray()
      )
    }

    subject = request.subject
    sentDate = Date()
  }

  // Corpo HTML
  val htmlPart = MimeBodyPart().apply {
    setContent(request.htmlContent, "text/html; charset=UTF-8")
  }

  // Multipart: HTML + anexos
  val multipart = MimeMultipart("mixed").apply {
    addBodyPart(htmlPart)

    request.anexos.forEach { anexo ->
      val attachmentPart = MimeBodyPart().apply {
        // Evita problemas com acentos no nome
        fileName = MimeUtility.encodeText(anexo.filename, "UTF-8", null)

        val ds: DataSource = ByteArrayDataSource(anexo.dados, anexo.mimeType)
        dataHandler = DataHandler(ds)
        disposition = Part.ATTACHMENT
      }
      addBodyPart(attachmentPart)
    }
  }

  msg.setContent(multipart)
  msg.saveChanges()
  return msg
}

// ---------------------------
// Exceções de domínio
// ---------------------------
class EmailDestinoInexistenteException(
  val invalidRecipients: List<String>,
  message: String,
  cause: Throwable? = null
) : RuntimeException(message, cause)

class EmailEnvioFalhouException(
  message: String,
  cause: Throwable? = null
) : RuntimeException(message, cause)

// ---------------------------
// Mapeamento de exceções (SMTP / Jakarta Mail)
// ---------------------------
private fun mapToDomainException(ex: Throwable): Throwable {
  // 1) Servidor rejeitou destinatário (muito comum em mailbox inexistente)
  if (ex is SendFailedException) {
    val invalid = ex.invalidAddresses
      ?.map { it.toString() }
      ?.distinct()
      .orEmpty()

    // Tenta pegar detalhe SMTP (código 550/553 etc.)
    val smtpDetails = findSmtpAddressFailed(ex)
    val detailMsg = smtpDetails?.let { d ->
      " (SMTP ${d.returnCode}: ${d.message})"
    } ?: ""

    return EmailDestinoInexistenteException(
      invalidRecipients = invalid,
      message = "Destinatário inválido/inexistente: ${invalid.joinToString(", ")}$detailMsg",
      cause = ex
    )
  }

  // 2) Outros erros de mail (auth, TLS, timeout, etc.)
  if (ex is MessagingException) {
    return EmailEnvioFalhouException(
      message = "Falha ao enviar e-mail: ${ex.message}",
      cause = ex
    )
  }

  // 3) Qualquer outra exceção
  return ex
}

/**
 * Procura no encadeamento de exceções por um SMTPAddressFailedException
 * (com retorno de código e mensagem do servidor).
 */
private fun findSmtpAddressFailed(ex: MessagingException): SMTPAddressFailedException? {
  var current: Exception? = ex
  while (current != null) {
    if (current is SMTPAddressFailedException) return current
    current = (current as? MessagingException)?.nextException
  }
  return null
}