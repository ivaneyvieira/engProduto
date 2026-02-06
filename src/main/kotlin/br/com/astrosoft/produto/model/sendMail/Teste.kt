package br.com.astrosoft.produto.model.sendMail

import kotlinx.coroutines.runBlocking

fun main() {
  runBlocking {
    val home = System.getenv("HOME")
    val fileName = System.getenv("EBEAN_PROPS") ?: "$home/ebean.properties"
    System.setProperty("ebean.props.file", fileName)

    val email = EmailRequest(
      to = listOf("ivaneyvieira@gmail.com"),
      subject = "Separação de responsabilidades ✔",
      htmlContent = "<b>Email</b> com <i>boas práticas</i>"
    )

    val run = sendEmailAsync(email)
    run.onSuccess { println("Email enviado com sucesso!") }
    run.onFailure { println("Erro ao enviar email: ${it.message}") }
  }
}