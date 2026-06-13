package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaVendas
import br.com.astrosoft.produto.model.report.ReportVenda

class TabDevCancelaViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
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

  fun autorizaNota(nota: NotaVenda, login: String, senha: String) = viewModel.exec {
    nota.solicitacaoTrocaEnnum ?: fail("Nota sem solicitação de troca")
    nota.produtoTrocaEnum ?: fail("Nota sem produto de troca")
    if (nota.autoriza != "S") {
      fail("Nota não marcada para autorizar")
    }

    val user = UserSaci.userLogin(login, senha)
    user ?: fail("Usuário ou senha inválidos")

    if (!user.autorizaDev) {
      fail("Usuário sem permissão para autorizar devolução")
    }

    nota.userTroca = user.no
    nota.update()
    updateView()
  }

  fun saveNota(nota: NotaVenda) = viewModel.exec {
    nota.update()
  }

  fun autorizaSolicitacao(nota: NotaVenda, solicitacaoTroca: SolicitacaoTroca?) = viewModel.exec {
    solicitacaoTroca ?: fail("Solicitação de troca inválida")
    val login = solicitacaoTroca.login
    val senha = solicitacaoTroca.senha
    val user = UserSaci.userLogin(login, senha)
    user ?: fail("Usuário ou senha inválidos")

    when (solicitacaoTroca.solicitacaoTrocaEnnum) {
      ESolicitacaoTroca.Troca       -> when (solicitacaoTroca.produtoTrocaEnum) {
        EProdutoTroca.Com   -> if (!user.autorizaTrocaP) {
          fail("Troca com produto não autorizada")
        }

        EProdutoTroca.Sem   -> if (!user.autorizaTroca) {
          fail("Troca sem produto não autorizada")
        }

        EProdutoTroca.Misto -> if (!user.autorizaTrocaP || !user.autorizaTroca) {
          fail("Troca mista de produto não autorizada")
        }
      }

      ESolicitacaoTroca.Estorno     -> if (!user.autorizaEstorno) {
        fail("Estorno de produto não autorizado")
      }

      ESolicitacaoTroca.Reembolso   -> if (!user.autorizaReembolso) {
        fail("Reembolso de produto não autorizado")
      }

      ESolicitacaoTroca.MudaCliente -> if (!user.autorizaMuda) {
        fail("Mudança de cliente não autorizada")
      }
    }

    nota.solicitacaoTrocaEnnum = solicitacaoTroca.solicitacaoTrocaEnnum
    nota.produtoTrocaEnum = solicitacaoTroca.produtoTrocaEnum
    nota.userSolicitacao = user.no
    nota.setMotivoTroca = setOf(solicitacaoTroca.motivo)
    nota.update()

    updateView()
  }

  fun desfazSolicitacao(nota: NotaVenda) {
    nota.solicitacaoTrocaEnnum = null
    nota.produtoTrocaEnum = null
    nota.userSolicitacao = null
    nota.motivoTroca = null
    nota.update()

    updateView()
  }

  val subView
    get() = viewModel.view.tabDevCancela
}

interface ITabDevCancela : ITabView {
  fun filtro(): FiltroNotaVenda
  fun updateNotas(notas: List<NotaVenda>)
  fun itensNotasSelecionados(): List<NotaVenda>
  fun formCancela(nota: NotaVenda)
}