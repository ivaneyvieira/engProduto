package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaVendas
import br.com.astrosoft.produto.model.report.ReportVenda
import kotlinx.coroutines.runBlocking

class TabDevAutorizaViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = runBlocking {
    val filtro = subView.filtro()
    val notas = NotaVenda.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun geraPlanilha(vendas: List<NotaVenda>): ByteArray {
    val planilha = PlanilhaVendas()
    return planilha.write(vendas)
  }

  fun imprimeRelatorio() {
    val notas = subView.itensNotasSelecionados()
    val report = ReportVenda()
    val file = report.processaRelatorio(notas)
    viewModel.view.showReport(chave = "Vendas${System.nanoTime()}", report = file)
  }

  fun formSolicitacao(nota: NotaVenda) = viewModel.exec {
    val userSolicitacao = nota.userSolicitacao ?: 0
    subView.formSolicitacao(nota, readOnly = userSolicitacao != 0)
  }

  fun formAutoriza(nota: NotaVenda) = viewModel.exec {
    val userTroca = nota.userTroca ?: 0
    if (userTroca != 0) {
      fail("Devolução já Autorizada")
    }
    subView.formAutoriza(nota)
  }

  fun autorizaNota(nota: NotaVenda, login: String, senha: String) = viewModel.exec {
    nota.solicitacaoTrocaEnnum ?: fail("Nota sem solicitação de troca")
    nota.produtoTrocaEnnum ?: fail("Nota sem produto de troca")
    if (nota.userSolicitacao == null || nota.userSolicitacao == 0) {
      fail("A solicitação de troca não foi autorizada")
    }
    if (nota.autoriza != "S") {
      fail("Nota não marcada para autorizar")
    }

    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.equals(login, ignoreCase = true) &&
        it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    if (!user.autorizaDev) {
      fail("Usuário sem permissão para autorizar devolução")
    }

    val usernoAutal = nota.userTroca ?: 0
    if (usernoAutal != 0) {
      fail("Nota já autorizada por outro usuário")
    }

    nota.userTroca = user.no
    nota.update()
    updateView()
  }

  fun autorizaNotaVenda(nota: NotaVenda, produtos: List<ProdutoNFS>, login: String, senha: String) = viewModel.exec {
    nota.solicitacaoTrocaEnnum ?: fail("Nota sem solicitação de troca")
    nota.produtoTrocaEnnum ?: fail("Nota sem produto de troca")
    /*
    if (nota.userSolicitacao == null || nota.userSolicitacao == 0) {
      fail("A solicitação de troca não foi autorizada")
    }
    if (nota.autoriza != "S") {
      fail("Nota não marcada para autorizar")
    }*/

    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.equals(login, ignoreCase = true) &&
        it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    if (!user.autorizaDev) {
      fail("Usuário sem permissão para autorizar devolução")
    }

    val usernoAutal = nota.userTroca ?: 0
    if (usernoAutal != 0) {
      fail("Nota já autorizada por outro usuário")
    }

    nota.userTroca = user.no
    nota.update()
    produtos.forEach { prd ->
      prd.updateQuantDev()
    }
    subView.updateProdutos()
  }

  fun solicitacaoNota(
    nota: NotaVenda,
    solicitacao: ESolicitacaoTroca?,
    produto: EProdutoTroca?,
    nfEntRet: Int?,
    setMotivoTroca: Set<EMotivoTroca>,
    login: String,
    senha: String
  ) = viewModel.exec {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.equals(login, ignoreCase = true) &&
        it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    if (!user.autorizaSolicitacao) {
      fail("Usuário sem permissão para solicitar devolução")
    }

    val usernoAutal = nota.userSolicitacao ?: 0
    if (usernoAutal != 0) {
      fail("Solicitação já autorizada por outro usuário")
    }
    nota.solicitacaoTrocaEnnum = solicitacao
    nota.produtoTrocaEnnum = produto
    nota.nfEntRet = nfEntRet
    nota.setMotivoTroca = setMotivoTroca

    nota.solicitacaoTrocaEnnum ?: fail("Nota sem solicitação de troca")
    nota.produtoTrocaEnnum ?: fail("Nota sem produto de troca")
    if (nota.autoriza != "S") {
      fail("Nota não marcada para autorizar")
    }

    nota.userSolicitacao = user.no
    nota.update()
    updateView()
  }

  fun saveNota(nota: NotaVenda) = viewModel.exec {
    nota.update()
  }

  fun desfazTroca() = viewModel.exec {
    val notas = subView.itensNotasSelecionados()
    if (notas.isEmpty()) {
      fail("Nenhuma nota selecionada")
    }
    notas.forEach {
      it.autoriza = "N"
      it.update()
    }
    updateView()
  }

  private fun updateProduto(bean: ProdutoNFS) {
    bean.updateQuantDev()
    subView.updateProdutos()
  }

  fun validaProcesamento(nota: NotaVenda, produtos: List<ProdutoNFS>): Boolean {
    try {
      val produtoDev = produtos.filter { it.dev == true }
      produtoDev.ifEmpty {
        fail("Nenhum produto selecionado")
      }

      nota.solicitacaoTrocaEnnum ?: fail("Tipo de devolução não informada")
      nota.produtoTrocaEnnum ?: fail("Tipo Produto não informado")
      nota.setMotivoTroca.ifEmpty {
        fail("Motivo de troca não informado")
      }
    } catch (e: Exception) {
      val msg = e.message
      viewModel.view.showWarning(msg ?: "Erro genérico")
      return false
    }
    return true
  }

  fun processaSolicitacao(nota: NotaVenda, produtos: List<ProdutoNFS>) = viewModel.exec {
    if (validaProcesamento(nota, produtos)) {
      val produtoDev = produtos.filter { it.dev == true }

      nota.update()
      produtoDev.forEach { prd ->
        prd.updateQuantDev()
      }
      subView.updateProdutos()
    }
  }

  fun desatorizaTroca(nota: NotaVenda, produtos: List<ProdutoNFS>) = viewModel.exec {
    val user = AppConfig.userLogin() as? UserSaci

    if (user?.desautorizaDev == false) {
      fail("Usuário sem permissão")
    }

    nota.solicitacaoTrocaEnnum = null
    nota.produtoTrocaEnnum = null
    nota.userTroca = 0
    nota.update()
    produtos.forEach { prd ->
      prd.dev = false
      prd.temProduto = false
      prd.quantDev = prd.quantidade
      prd.updateQuantDev()
    }
    subView.updateProdutos()
  }

  val subView
    get() = viewModel.view.tabDevAutoriza
}

interface ITabDevAutoriza : ITabView {
  fun filtro(): FiltroNotaVenda
  fun updateNotas(notas: List<NotaVenda>)
  fun itensNotasSelecionados(): List<NotaVenda>
  fun formAutoriza(nota: NotaVenda)
  fun formSolicitacao(nota: NotaVenda, readOnly: Boolean)
  fun updateProdutos()
}