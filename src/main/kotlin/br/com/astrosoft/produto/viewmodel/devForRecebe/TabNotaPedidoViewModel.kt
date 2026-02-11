package br.com.astrosoft.produto.viewmodel.devForRecebe

import br.com.astrosoft.framework.model.DB
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaNotasPedidos
import br.com.astrosoft.produto.model.report.RelatorioEspelhoNota
import br.com.astrosoft.produto.model.report.RelatorioNotaDevolucao
import br.com.astrosoft.produto.model.saci
import br.com.astrosoft.produto.model.sendMail.Anexo
import br.com.astrosoft.produto.model.sendMail.EmailRequest
import br.com.astrosoft.produto.model.sendMail.sendEmailAsync
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime

class TabNotaPedidoViewModel(val viewModel: DevFor2ViewModel) : ITabNotaViewModel {
  val subView
    get() = viewModel.view.tabNotaPedido

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaRecebimentoDev.findAllDev(filtro = filtro, situacaoDev = EStituacaoDev.PEDIDO)
    subView.updateNota(notas)
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun addArquivo(nota: NotaRecebimentoDev, fileName: String, dados: ByteArray) {
    val invFile = InvFileDev(
      invno = nota.niPrincipal,
      numero = nota.numeroDevolucao,
      tipoDevolucao = nota.motivoDevolucao,
      seq = null,
      date = LocalDate.now(),
      fileName = fileName,
      file = dados,
    )
    invFile.save()
    subView.updateArquivos()
  }

  fun removeArquivosSelecionado() {
    val selecionado = subView.arquivosSelecionados()
    selecionado.forEach {
      it.delete()
    }

    subView.updateArquivos()
  }

  fun saveNota(nota: NotaRecebimentoDev, updateGrid: Boolean = false) {
    nota.save()
    if (updateGrid) {
      updateView()
    }
  }

  fun updateMotivo(tipoDevolucao: EMotivoDevolucao?) = viewModel.exec {
    tipoDevolucao ?: return@exec
    val itens = subView.notasSelecionadas()
    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { bean ->
      bean.salvaMotivoDevolucao(tipoDevolucao.num)
    }
    updateView()
  }

  fun marcaSituacao(situacao: EStituacaoDev) = viewModel.exec {
    val itens = subView.notasSelecionadas()
    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    itens.forEach {
      it.marcaSituacao(situacao)
    }
    updateView()
  }

  override fun findProdutos(codigo: String): List<PrdGrade> {
    return saci.findProdutoGrades(codigo)
  }

  override fun addProduto(produto: NotaRecebimentoProdutoDev?): Unit = viewModel.exec {
    produto ?: fail("Nenhum produto selecionado")
    produto.insertProduto()
    subView.updateProduto()
  }

  fun removeProduto() = viewModel.exec {
    val lista = subView.produtosSelecionados()
    if (lista.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    viewModel.view.showQuestion("Remover produtos selecionados?") {
      lista.forEach {
        it.deleteProduto()
      }
      subView.updateProduto()
    }
  }

  override fun updateProduto(produto: NotaRecebimentoProdutoDev, grade: String?, ni: Int?) {
    produto.updateProduto(grade, ni)
    subView.updateProduto()
  }

  override fun updateAcertoProduto(produto: NotaRecebimentoProdutoDev) {
    produto.updateAcertoProduto()
  }

  fun removeNota() = viewModel.exec {
    val lista = subView.notasSelecionadas()
    if (lista.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    viewModel.view.showQuestion("Confirma a remoção do(s) produto(s) selecionado(s)?") {
      lista.forEach {
        it.delete()
      }
      updateView()
    }
  }

  fun imprimirEspelhoNota(nota: NotaRecebimentoDev) = viewModel.exec {
    val file = RelatorioEspelhoNota.processaRelatorio(listNota = listOf(nota))
    viewModel.view.showReport(chave = "Espelho Nota${System.nanoTime()}", report = file)
  }

  fun imprimirRelatorioCompleto(nota: NotaRecebimentoDev) = viewModel.exec {
    val file = RelatorioNotaDevolucao.processaRelatorio(listNota = listOf(nota), resumida = false)
    viewModel.view.showReport(chave = "Relatorio Completo${System.nanoTime()}", report = file)
  }

  fun imprimirRelatorioReduzido(nota: NotaRecebimentoDev) = viewModel.exec {
    val file = RelatorioNotaDevolucao.processaRelatorio(listNota = listOf(nota), resumida = true)
    viewModel.view.showReport(chave = "Relatorio Reduzido${System.nanoTime()}", report = file)
  }

  fun geraPlanilha(produtos: List<NotaRecebimentoProdutoDev>): ByteArray {
    val planilha = PlanilhaNotasPedidos()
    return planilha.write(produtos)
  }

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
    val listaEmail = nota.listRepresentantes().flatMap {
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
    return email
  }

  fun enviaEmail(email: EmailDevolucao) {
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
          subView.updateEmails()
        }
      }
      result.onFailure {
        viewModel.view.execUI {
          email.enviado = false
          email.save()
          viewModel.view.showError(it.message ?: "Erro ao enviar e-mail")
          subView.updateEmails()
        }
      }
    }
  }

  fun reenviarEmail() = viewModel.exec {
    val emailSelecionados = subView.emailSelecionados()

    if (emailSelecionados.isEmpty()) {
      fail("Nenhum e-mail selecionado")
    }

    emailSelecionados.forEach { email ->
      email.dataEmail = LocalDateTime.now()
      enviaEmail(email)
    }
  }

  fun removeEmail() {
    val emailSelecionados = subView.emailSelecionados()

    if (emailSelecionados.isEmpty()) {
      fail("Nenhum e-mail selecionado")
    }

    viewModel.view.showQuestion("Remover e-mails selecionados?") {
      emailSelecionados.forEach { email ->
        email.dataEmail = LocalDateTime.now()
        email.delete()
      }

      subView.updateEmails()
    }
  }
}

interface ITabNotaPedido : ITabView {
  fun filtro(): FiltroNotaRecebimentoProdutoDev
  fun updateNota(notas: List<NotaRecebimentoDev>)
  fun updateArquivos()
  fun arquivosSelecionados(): List<InvFileDev>
  fun produtosSelecionados(): List<NotaRecebimentoProdutoDev>
  fun notasSelecionadas(): List<NotaRecebimentoDev>
  fun updateProduto(): NotaRecebimentoDev?
  fun updateEmails()
  fun emailSelecionados(): List<EmailDevolucao>
}