package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.DummyPrinter
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaVendas
import br.com.astrosoft.produto.model.printText.ValeTrocaDevolucao
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

    nota.userTroca = user.no
    nota.update()
    updateView()
  }

  fun autorizaNotaVenda(nota: NotaVenda, produtos: List<ProdutoNFS>, login: String, senha: String) = viewModel.exec {
    nota.solicitacaoTrocaEnnum ?: fail("Nota sem solicitação de troca")
    nota.produtoTrocaEnnum ?: fail("Nota sem produto de troca")

    nota.autoriza = "S"

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
      val user = AppConfig.userLogin() as? UserSaci ?: fail("Usuário não logado")
      val produtosDev = produtos
        .filter { it.devDB == false }
        .filter { it.dev == true }
      produtosDev.ifEmpty {
        fail("Nenhum produto selecionado")
      }

      val solicitacao = nota.solicitacaoTrocaEnnum ?: fail("Tipo de devolução não informada")
      val produto = nota.produtoTrocaEnnum ?: fail("Tipo de devolução (com ou sem produto) não informada")
      nota.setMotivoTroca.ifEmpty {
        fail("Motivo de troca não informado")
      }

      val produtosDevComProduto = produtosDev.filter { it.temProduto == true }
      val produtosDevSemProduto = produtosDev.filter { it.temProduto == false }

      val tipoResultante = when {
        produtosDevComProduto.isNotEmpty() && produtosDevSemProduto.isEmpty() -> EProdutoTroca.Com
        produtosDevComProduto.isEmpty() && produtosDevSemProduto.isNotEmpty() -> EProdutoTroca.Sem
        else                                                                  -> EProdutoTroca.Misto
      }

      if (tipoResultante != produto) {
        fail("Tipo de devolução de produto inválida")
      }

      when {
        produto == EProdutoTroca.Misto               -> if (!user.autorizaMista) {
          fail("O usuário não tem permissão para autorizar troca mista")
        }

        solicitacao == ESolicitacaoTroca.Troca       -> when (produto) {
          EProdutoTroca.Sem   -> if (!user.autorizaTroca) {
            fail("O usuário não tem permissão para autorizar troca sem produto")
          }

          EProdutoTroca.Com   -> if (!user.autorizaTrocaP) {
            fail("O usuário não tem permissão para autorizar troca com produto")
          }

          EProdutoTroca.Misto -> {

          }
        }

        solicitacao == ESolicitacaoTroca.Estorno     -> if (!user.autorizaEstorno) {
          fail("O usuário não tem permissão para estorno")
        }

        solicitacao == ESolicitacaoTroca.Reembolso   -> if (!user.autorizaReembolso) {
          fail("O usuário não tem permissão para reembolso")
        }

        solicitacao == ESolicitacaoTroca.MudaCliente -> if (!user.autorizaMuda) {
          fail("O usuário não tem permissão para muda de cliente")
        }
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
    nota.nfEntRet = null
    nota.userTroca = 0
    nota.setMotivoTroca = emptySet()
    nota.update()
    produtos.forEach { prd ->
      prd.dev = false
      prd.temProduto = false
      prd.quantDev = prd.quantidade
      prd.updateQuantDev()
    }
    subView.updateProdutos()
  }

  fun imprimeValeTroca(nota: NotaVenda) = viewModel.exec {
    val notaDev = nota.notaDev()
    if (notaDev.isEmpty()) {
      fail("Não foi encontrado nenhuma nota de devolução")
    }
    imprimeValeTroca(notaDev)
  }

  private fun imprimeValeTroca(notas: List<EntradaDevCli>) = viewModel.exec {
    val dummyPrinter = DummyPrinter()
    notas.forEach { nota ->
      val relatorio = ValeTrocaDevolucao(nota = nota, autorizacao = nota.nameAutorizacao ?: "")
      relatorio.print(nota.produtos(), dummyPrinter)
    }

    val text = dummyPrinter.textBuffer()

    viewModel.view.showPrintText(text) {
    }
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