package br.com.astrosoft.produto.viewmodel.devForRecebe

import br.com.astrosoft.framework.model.DB
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.Template
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.padronizarRazaoSocial
import br.com.astrosoft.framework.util.produzirNomeReduzido
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaNFDAberta
import br.com.astrosoft.produto.model.printText.NotaExpedicaoDev
import br.com.astrosoft.produto.model.sendMail.Anexo
import br.com.astrosoft.produto.model.sendMail.EmailRequest
import br.com.astrosoft.produto.model.sendMail.sendEmailAsync
import br.com.astrosoft.produto.model.zpl.EtiquetaChave
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TabNotaNFDAbertaViewModel(val viewModel: DevFor2ViewModel) {
  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }
  
  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaSaidaDev.findDevolucao(filtro).distinctBy { nota ->
      "${nota.loja}-${nota.pdvno}-${nota.xano}=${nota.situacaoDevName}"
    }
    subView.updateNotas(notas)
  }
  
  fun findGrade(prd: ProdutoNFS?, block: (List<PrdGrade>) -> Unit) = viewModel.exec {
    prd ?: return@exec
    val list = prd.findGrades()
    block(list)
  }
  
  private fun imprimeEtiqueta(produtos: List<ProdutoNFS>) {
    val user = AppConfig.userLogin() as? UserSaci
    user?.impressoraNota?.let { impressora ->
      try {
        EtiquetaChave.printPreviewExp(impressora, produtos, 1)
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }
  
  fun imprimeProdutosNota(nota: NotaSaidaDev, itensSelecionados: List<NotaSaidaDevProduto>) = viewModel.exec {
    if (itensSelecionados.isEmpty()) fail("Nenhum produto selecionado")
    if (nota.cancelada == "S") fail("Nota cancelada")
    val report = NotaExpedicaoDev(nota)
    report.print(
      dados = itensSelecionados,
      printer = subView.printerPreview(loja = nota.loja),
    )
  }
  
  fun autorizaProduto(listaPrd: List<ProdutoNFS>, login: String, senha: String): UserSaci? {
    val lista = UserSaci.findAll()
    val user = lista.firstOrNull {
      it.login.equals(login, ignoreCase = true) && it.senha?.uppercase()?.trim() == senha.uppercase().trim()
    }
    
    if (user == null) {
      viewModel.view.showError("Usuário ou senha inválidos")
    } else {
      listaPrd.forEach { produto ->
        produto.usernoExp = user.no
        produto.salva()
      }
    }
    
    return user
  }
  
  fun saveObs(nota: NotaSaidaDev) = viewModel.exec {
    nota.saveObs()
  }
  
  fun addArquivo(nota: NotaSaidaDev, fileName: String, dados: ByteArray) = viewModel.exec {
    if (nota.situacaoDevName.isNullOrBlank()) {
      addArquivoSaida(nota, fileName, dados)
    } else {
      addArquivoEntrada(nota, fileName, dados)
    }
    subView.updateViewFile()
  }
  
  private fun addArquivoSaida(nota: NotaSaidaDev, fileName: String, dados: ByteArray) {
    val notaFile = NotaSaidaDevFile(
      seq = 0,
      loja = nota.loja,
      pdvno = nota.pdvno,
      xano = nota.xano,
      tipo = "S",
      date = LocalDate.now(),
      filename = fileName,
      file = dados
    )
    notaFile.save()
  }
  
  private fun addArquivoEntrada(nota: NotaSaidaDev, fileName: String, dados: ByteArray) {
    val notaFile = InvFileDev(
      invno = nota.invno ?: fail("NI não encontrado"),
      numero = nota.numeroDevolucao ?: fail("Número não encontrado"),
      tipoDevolucao = nota.motivoDevolucao ?: fail("Tipo de Devolução não encontrado"),
      seq = 0,
      date = LocalDate.now(),
      fileName = fileName,
      file = dados,
    )
    notaFile.save()
  }
  
  fun removeArquivosSelecionado() {
    val arquivoSelectionado = subView.arquivosSelecionados()
    arquivoSelectionado.forEach { file ->
      file.delete()
    }
    subView.updateViewFile()
  }
  
  fun geraPlanilha(produtos: List<NotaSaidaDev>): ByteArray {
    val planilha = PlanilhaNFDAberta()
    return planilha.write(produtos)
  }
  
  fun salvaObservacao(nota: NotaSaidaDev) = viewModel.exec {
    nota.salvaObservacao()
  }
  
  fun addNota(nota: NotaSaidaDev, nfSaida: String) = viewModel.exec {
    try {
      nota.addNotaSaida(nfSaida)
    } catch (e: Exception) {
      fail(e.message ?: "")
    }
  }
  
  fun emailDevolucao(nota: NotaSaidaDev): EmailDevolucao {
    val listaRep = nota.listRepresentantes()
    val listaEmail = listaRep.flatMap {
      it.emailList
    }.distinct()
    
    val anexos = nota.listArquivosDev().map { file ->
      AnexoEmail(
        id = 0, idEmail = 0, nomeArquivo = file.fileName ?: "", conteudo = file.file ?: byteArrayOf()
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
  
  private fun NotaSaidaDev.emailSubject(): String {
    val nfd = this.nota ?: ""
    val motivo = this.motivoDevolucaoName
    val nfo = nfEntrada ?: ""
    
    val fonecedorRazao = padronizarRazaoSocial(this.fornecedor ?: "")
    val fornecedorReduzido = produzirNomeReduzido(fonecedorRazao)
    
    return "$fornecedorReduzido | NFD $nfd ($motivo) NFO $nfo"
  }
  
  private fun NotaSaidaDev.emailContent(): String {
    val template = Template("/html/emailDevolucao.html")
    
    val hora = LocalTime.now().hour
    val saudacao = if (hora < 12) "Bom dia" else if (hora < 18) "Boa tarde" else "Boa noite"
    
    template.set("SAUDACAO", saudacao)
    template.set("MOTIVO", this.motivoDevolucaoName)
    template.set("NFO", nfEntrada ?: "")
    template.set("NFOEMIS", emissao.format())
    template.set("CTE", this.cteDevolucao ?: "")
    template.set("CTEEMIS", this.dataStr.format())
    template.set("NFD", this.nota ?: "")
    template.set("NFDEMIS", this.dataEmissao.format())
    template.set("NFDVALOR", this.valorNota.format())
    template.set("COLETA", this.dataColeta.format())
    
    return template.render()
  }
  
  fun enviaEmail(email: EmailDevolucao, updateEmails: () -> NotaSaidaDev?) {
    runBlocking {
      val request = EmailRequest(
        to = email.toEmailList.toList(),
        subject = email.subject,
        cc = email.ccEmailList.toList(),
        bcc = email.bccEmailList.toList(),
        htmlContent = email.htmlContent,
        anexos = email.anexos.map { anexoEmail ->
          Anexo(
            filename = anexoEmail.nomeArquivo, mimeType = anexoEmail.mimeType, dados = anexoEmail.conteudo
          )
        })
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
  
  fun removeEmail(emailSelecionados: List<EmailDevolucao>, updateEmails: () -> NotaSaidaDev?) {
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
  
  fun reenviarEmail(emailSelecionados: List<EmailDevolucao>, updateEmails: () -> NotaSaidaDev?) {
    if (emailSelecionados.isEmpty()) {
      fail("Nenhum e-mail selecionado")
    }
    
    emailSelecionados.forEach { email ->
      email.dataEmail = LocalDateTime.now()
      enviaEmail(email, updateEmails)
    }
  }
  
  fun addAnexo(email: EmailDevolucao, fileName: String, dados: ByteArray) {
    val anexo = AnexoEmail(
      id = 0, idEmail = email.id, nomeArquivo = fileName, conteudo = dados
    )
    email.addAnexo(anexo)
  }
  
  val subView
    get() = viewModel.view.tabNotaNFDAberta
}

interface ITabNotaNFDAberta : ITabView {
  fun filtro(): FiltroNotaDev
  fun updateNotas(notas: List<NotaSaidaDev>)
  fun findNota(): NotaSaidaDev?
  fun updateProdutos()
  fun produtosSelcionados(): List<NotaSaidaDevProduto>
  fun arquivosSelecionados(): List<NotaSaidaDevFile>
  fun updateViewFile()
}