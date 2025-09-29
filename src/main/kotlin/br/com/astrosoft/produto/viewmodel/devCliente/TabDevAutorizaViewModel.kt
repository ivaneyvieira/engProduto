package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.EProdutoTroca
import br.com.astrosoft.produto.model.beans.ESolicitacaoTroca
import br.com.astrosoft.produto.model.beans.FiltroNotaVenda
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaVenda
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.model.planilha.PlanilhaVendas
import br.com.astrosoft.produto.model.report.ReportVenda

class TabDevAutorizaViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
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
    subView.formSolicitacao(nota)
  }

  fun formAutoriza(nota: NotaVenda) = viewModel.exec {
    subView.formAutoriza(nota)
  }

  fun autorizaNota(nota: NotaVenda, login: String, senha: String) = viewModel.exec {
    nota.solicitacaoTrocaEnnum ?: fail("Nota sem solicitação de troca")
    nota.produtoTrocaEnnum ?: fail("Nota sem produto de troca")
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
    val usernoAutal = nota.userTroca ?: 0
    if (usernoAutal != 0) {
      fail("Nota já autorizada por outro usuário")
    }

    nota.userTroca = user.no
    nota.update()
    updateView()
  }

  fun solicitacaoNota(nota: NotaVenda, solicitacao: ESolicitacaoTroca?, produto: EProdutoTroca?) = viewModel.exec {
    val usernoAutal = nota.userTroca ?: 0
    if (usernoAutal != 0) {
      fail("Nota já autorizada por outro usuário")
    }
    nota.solicitacaoTrocaEnnum = solicitacao
    nota.produtoTrocaEnnum = produto

    nota.solicitacaoTrocaEnnum ?: fail("Nota sem solicitação de troca")
    nota.produtoTrocaEnnum ?: fail("Nota sem produto de troca")
    if (nota.autoriza != "S") {
      fail("Nota não marcada para autorizar")
    }

    nota.update()
    updateView()
  }

  val subView
    get() = viewModel.view.tabDevAutoriza
}

interface ITabDevAutoriza : ITabView {
  fun filtro(): FiltroNotaVenda
  fun updateNotas(notas: List<NotaVenda>)
  fun itensNotasSelecionados(): List<NotaVenda>
  fun formAutoriza(nota: NotaVenda)
  fun formSolicitacao(nota: NotaVenda)
}