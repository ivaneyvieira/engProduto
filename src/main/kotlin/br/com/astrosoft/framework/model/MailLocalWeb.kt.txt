package br.com.astrosoft.framework.model

import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class MailLocalWeb {
  val emailRemetente = "devolucoes@engecopi.com.br"
  val nomeRemetente = "Engecopi"
  val protocolo = "smtp"
  val servidor = "smtplw.com.br" // do painel de controle do SMTP
  val username = "devolucoes@engecopi.com.br" // do painel de controle do SMTP
  val senha = "04controlle00" // do painel de controle do SMTP
  val porta = "465" // do painel de controle do SMTP
  val props = initProperties()
  val session: Session = Session.getDefaultInstance(props, GmailAuthenticator(username, senha)).apply {
    debug = false
  }
  
  fun sendMail(to: DestinoMail, subject: String, htmlMessage: String, files: List<FileAttach> = emptyList()) {
    try {
      val message = createMessage(to, subject)
      val multPart = MimeMultipart().apply {
        val partText = partText(htmlMessage)
        val partFile = partsFile(files)
        this.addBodyPart(partText)
        partFile.forEach {part->
          this.addBodyPart(part)
        }
      }
      message.setContent(multPart)
      transport(message)
    } catch(e: UnsupportedEncodingException) {
      e.printStackTrace()
    } catch(e: MessagingException) {
      e.printStackTrace()
    }
  }
  
  private fun transport(message: MimeMessage) {
    val transport: Transport = session.getTransport(protocolo)
    transport.connect(servidor, username, senha)
    message.saveChanges()
    transport.sendMessage(message, message.allRecipients)
    transport.close()
  }
  
  private fun initProperties(): Properties {
    return Properties().apply {
      this["mail.transport.protocol"] = protocolo
      this["mail.smtp.host"] = servidor
      this["mail.smtp.auth"] = "true"
      this["mail.smtp.port"] = porta
      this["mail.smtp.ssl.enable"] = "true"
    }
  }
  
  private fun partsFile(files: List<FileAttach>): List<MimeBodyPart> {
    return files.map {file ->
      MimeBodyPart().apply {
        val fileDataSource = FileDataSource(file.fileName())
        this.dataHandler = DataHandler(fileDataSource)
        this.fileName = fileDataSource.name
      }
    }
  }
  
  private fun partText(htmlMessage: String): MimeBodyPart {
    return MimeBodyPart().apply {
      this.dataHandler = DataHandler((HTMLDataSource(htmlMessage)))
    }
  }
  
  private fun createMessage(to: DestinoMail, subject: String): MimeMessage {
    val iaFrom = InternetAddress(emailRemetente, nomeRemetente)
    val iaTo = arrayOfNulls<InternetAddress>(1)
    val iaReplyTo = arrayOfNulls<InternetAddress>(1)
    iaReplyTo[0] = InternetAddress(to.email, to.nome)
    iaTo[0] = InternetAddress(to.email, to.nome)
    
    val message = MimeMessage(session)
    message.replyTo = iaReplyTo
    message.setFrom(iaFrom)
    if(iaTo.isNotEmpty()) message.setRecipients(Message.RecipientType.TO, iaTo)
    message.subject = subject
    message.sentDate = Date()
    return message
  }
}

class DestinoMail(val nome: String, val email: String)

class FileAttach(val nome: String, val bytes: ByteArray) {
  fun fileName(): String {
    val fileName = "/tmp/$nome"
    val file = File(fileName)
    file.writeBytes(bytes)
    return fileName
  }
}

internal class HTMLDataSource(private val html: String?): DataSource {
  @Throws(IOException::class)
  override fun getInputStream(): InputStream {
    if(html == null) throw IOException("html message is null!")
    return ByteArrayInputStream(html.toByteArray())
  }
  
  @Throws(IOException::class)
  override fun getOutputStream(): OutputStream {
    throw IOException("This DataHandler cannot write HTML")
  }
  
  override fun getContentType(): String {
    return "text/html"
  }
  
  override fun getName(): String {
    return "HTMLDataSource"
  }
}
/*
fun main() {
  var message = "<i>Greetings!</i><br>"
  message += "<b>Wish you a nice day!</b><br>"
  message += "<font color=red>Duke</font>"
  val mail = MailLocalWeb();
  mail.sendMail(DestinoMail("ivaneyvieira@gmail.com", "Ivaney"), "Teste 01", message)
}
*/
