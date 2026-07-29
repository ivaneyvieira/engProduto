package br.com.astrosoft.produto.viewmodel.devForRecebe

import br.com.astrosoft.framework.model.DB
import br.com.astrosoft.framework.util.SystemUtils
import br.com.astrosoft.framework.util.Template
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.padronizarRazaoSocial
import br.com.astrosoft.framework.util.produzirNomeReduzido
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.AnexoEmail
import br.com.astrosoft.produto.model.beans.EmailDevolucao
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.model.sendMail.Anexo
import br.com.astrosoft.produto.model.sendMail.EmailRequest
import br.com.astrosoft.produto.model.sendMail.sendEmailAsync
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.LocalTime

open class EmailViewModel(val viewModel: DevFor2ViewModel) {
  fun addAnexo(email: EmailDevolucao, fileName: String, dados: ByteArray) {
    val anexo = AnexoEmail(
      id = 0,
      idEmail = email.id,
      nomeArquivo = fileName,
      conteudo = dados
    )
    email.addAnexo(anexo)
  }

  fun emailDevolucao(nota: NotaRecebimentoDev): EmailDevolucao {
    val listaRep = nota.listRepresentantes()
    val listaEmail = listaRep.flatMap {
      it.emailList
    }.distinct()

    val anexos = nota.listArquivos().map { file ->
      AnexoEmail(
        id = 0,
        idEmail = 0,
        nomeArquivo = file.fileName ?: "",
        conteudo = file.file ?: byteArrayOf()
      )
    }

    val email = EmailDevolucao()
    email.chave = nota.chaveEmail
    email.addAnexo(anexos)
    email.ccEmailList = DB.garantiaCopy.split(",").map { it.trim() }.toSet()
    email.toEmailList = listaEmail.toSet()
    email.dataEmail = LocalDateTime.now()
    email.subject = nota.emailSubject()
    email.htmlContent = nota.emailContent()
    return email
  }

  private fun NotaRecebimentoDev.emailSubject(): String {
    val nfd = this.notaDevolucao ?: ""
    val motivo = this.motivoDevolucaoName
    val nfo = nfEntrada ?: ""

    val fonecedorRazao = padronizarRazaoSocial(this.fornecedor ?: "")
    val fornecedorReduzido = produzirNomeReduzido(fonecedorRazao)

    return "$fornecedorReduzido | NFD $nfd ($motivo) NFO $nfo"
  }


  private fun NotaRecebimentoDev.emailContent(): String {
    val template = Template("/html/emailDevolucao.html")

    val hora = LocalTime.now().hour
    val saudacao = if(hora < 12) "Bom dia" else if(hora < 18) "Boa tarde" else "Boa noite"

    template.set("SAUDACAO", saudacao)
    template.set("MOTIVO",  this.motivoDevolucaoName)
    template.set("NFO", nfEntrada ?: "")
    template.set("NFOEMIS", emissao.format())
    template.set("CTE", this.cteDevolucao ?: "")
    template.set("CTEEMIS", this.dataDevolucao.format())
    template.set("NFD", this.notaDevolucao ?: "")
    template.set("NFDEMIS", this.emissaoDevolucao.format())
    template.set("NFDVALOR", this.valorDevolucao.format())
    template.set("COLETA", this.dataColeta.format())

    return template.render()
  }

  fun enviaEmail(email: EmailDevolucao, updateEmails: () -> Unit) {
    runBlocking {
      val request = EmailRequest(
        to = email.toEmailList.toList(),
        subject = email.subject,
        cc = email.ccEmailList.toList(),
        bcc = email.bccEmailList.toList(),
        htmlContent = email.htmlContent,
        anexos = email.anexos.map { anexoEmail ->
          Anexo(
            filename = anexoEmail.nomeArquivo,
            mimeType = anexoEmail.mimeType,
            dados = anexoEmail.conteudo
          )
        }
      )
      val result = sendEmailAsync(request)
      result.onSuccess {
        viewModel.view.execUI {
          email.enviado = true
          email.save()
          updateEmails()
        }
      }
      result.onFailure {
        viewModel.view.execUI {
          email.enviado = false
          email.save()
          viewModel.view.showError(it.message ?: "Erro ao enviar e-mail")
          updateEmails()
        }
      }
    }
  }

  fun reenviarEmail(emailSelecionados: List<EmailDevolucao>, updateEmails: () -> Unit) = viewModel.exec {
    if (emailSelecionados.isEmpty()) {
      fail("Nenhum e-mail selecionado")
    }

    emailSelecionados.forEach { email ->
      email.dataEmail = LocalDateTime.now()
      enviaEmail(email, updateEmails)
    }
  }

  fun removeEmail(emailSelecionados: List<EmailDevolucao>, updateEmails: () -> Unit) = viewModel.exec {
    if (emailSelecionados.isEmpty()) {
      fail("Nenhum e-mail selecionado")
    }

    viewModel.view.showQuestion("Remover e-mails selecionados?") {
      emailSelecionados.forEach { email ->
        email.dataEmail = LocalDateTime.now()
        email.delete()
      }

      updateEmails()
    }
  }
}