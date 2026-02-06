package br.com.astrosoft.produto.model.sendMail

import jakarta.activation.DataHandler
import jakarta.activation.DataSource
import jakarta.mail.*
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import jakarta.mail.util.ByteArrayDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

suspend fun sendEmailAsync(
  request: EmailRequest
): Result<Unit> = withContext(Dispatchers.IO) {
  runCatching {
    require(request.to.isNotEmpty()) {
      "A lista de destinatários (to) não pode estar vazia"
    }

    val props = Properties().apply {
      put("mail.smtp.host", request.smtp.host)
      put("mail.smtp.port", request.smtp.port.toString())
      put("mail.smtp.auth", "true")
      put("mail.smtp.starttls.enable", "true")
      put("mail.smtp.starttls.required", "true")
    }

    val session = Session.getInstance(props, object : Authenticator() {
      override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(
          request.smtp.username,
          request.smtp.password
        )
      }
    })

    val message = MimeMessage(session).apply {
      setFrom(InternetAddress(request.fromEmail))

      setRecipients(
        Message.RecipientType.TO,
        request.to.map { InternetAddress(it) }.toTypedArray()
      )

      if (request.cc.isNotEmpty()) {
        setRecipients(
          Message.RecipientType.CC,
          request.cc.map { InternetAddress(it) }.toTypedArray()
        )
      }

      if (request.bcc.isNotEmpty()) {
        setRecipients(
          Message.RecipientType.BCC,
          request.bcc.map { InternetAddress(it) }.toTypedArray()
        )
      }

      subject = request.subject
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
          fileName = anexo.filename

          val ds: DataSource =
              ByteArrayDataSource(anexo.dados, anexo.mimeType)
          dataHandler = DataHandler(ds)
          disposition = MimeBodyPart.ATTACHMENT
        }
        addBodyPart(attachmentPart)
      }
    }

    message.setContent(multipart)

    Transport.send(message)
  }
}