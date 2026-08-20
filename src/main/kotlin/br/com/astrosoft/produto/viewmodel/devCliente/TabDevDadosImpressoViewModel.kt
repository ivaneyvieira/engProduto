package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.ValeTrocaDadosDev

class TabDevDadosImpressoViewModel(val viewModel: DevClienteViewModel) {
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

  fun autorizaSolicitacao(nota: DadosDev, solicitacaoTroca: SolicitacaoTrocaSimples) = viewModel.exec {
    nota.tipoDevEnum = solicitacaoTroca.solicitacaoTrocaEnnum
    nota.produtoTrocaEnum = solicitacaoTroca.produtoTrocaEnum
    nota.userSolicitacao = null
    nota.update()

    updateView()
  }

  fun salvaNfEntRet(nota: DadosDev, nfNumero: Int) = viewModel.exec {
    nota.nfEntRet = nfNumero
    nota.salvaNfEntRet()
  }

  /**************************** imprimeValeTroca ************************************/



  fun imprimeValeTroca(nota: DadosDev) = viewModel.exec {
    val relatorio = ValeTrocaDadosDev(nota)

    val dados = nota.produtos
    val printer = subView.printerPreview(loja = 0)

    relatorio.print(dados = dados, printer = printer)
  }

  /**************************** imprimeValeTroca ************************************/

  val subView
    get() = viewModel.view.tabDevDadosImpresso
}

interface ITabDevDadosImpresso : ITabView {
  fun filtro(): FiltroDadosDev
  fun updateNotas(notas: List<DadosDev>)
  fun updateProdutos()
  fun fechaFormProduto()
}
