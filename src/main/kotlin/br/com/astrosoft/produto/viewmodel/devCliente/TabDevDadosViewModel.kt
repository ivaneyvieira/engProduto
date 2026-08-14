package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*

class TabDevDadosViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = DadosDev.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun ajusteProduto(ajuste: AjusteProduto) {
    val produto = ajuste.produto
    produto.marcaAjuste(ajuste)
  }

  fun desfazSolicitacao(nota: DadosDev) {
    nota.apagaDados()
    updateView()
  }

  fun autorizaSolicitacao(nota: DadosDev, solicitacaoTroca: SolicitacaoTroca) = viewModel.exec {
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

    nota.tipoDevEnum = solicitacaoTroca.solicitacaoTrocaEnnum
    nota.produtoTrocaEnum = solicitacaoTroca.produtoTrocaEnum
    nota.userSolicitacao = user.no
    nota.update()

    updateView()
  }

  fun salvaNfEntRet(nota: DadosDev, nfNumero: Int) {
    TODO("Not yet implemented")
  }

  fun validaProcesamento(user: UserSaci?, nota: DadosDev, produtos: List<DadosDevProduto>): Boolean {
    TODO("Not yet implemented")
  }

  fun autorizaNotaVenda(nota: DadosDev, produtos: List<DadosDevProduto>, login: String, senha: String) {
    TODO("Not yet implemented")
  }

  fun desautorizaTroca(nota: DadosDev, produto: DadosDevProduto) {
    TODO("Not yet implemented")
  }

  fun desatorizaTroca(nota: DadosDev, produto: DadosDevProduto) {
    TODO("Not yet implemented")
  }

  val subView
    get() = viewModel.view.tabDevDados
}

interface ITabDevDados : ITabView {
  fun filtro(): FiltroDadosDev
  fun updateNotas(notas: List<DadosDev>)
  fun updateProdutos()
}
