package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaAjusteEst
import br.com.astrosoft.produto.model.printText.PrintAjusteEst

class TabAjusteEstViewModel(val viewModel: ProdutoViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    subView.execThread {
      val filtro = subView.filtro()
      val produtos = AjusteEst.findAjusteEst(filtro)

      subView.updateProdutos(produtos)
    }
  }

  fun geraPlanilha(produtos: List<AjusteEst>): ByteArray {
    val planilha = PlanilhaAjusteEst()
    return planilha.write(produtos)
  }

  fun imprimeProdutos() = viewModel.exec {
    val produtos = subView.produtosSelecionados()
    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }
    val filtro = subView.filtro()

    val report = PrintAjusteEst(filtro)

    report.print(
      dados = produtos,
      printer = subView.printerPreview(loja = 0)
    )
  }

  fun cadastraValidade() = viewModel.exec {
    val itens = subView.produtosSelecionados()
    val user = AppConfig.userLogin() as? UserSaci

    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    val tipoValidade = 2
    val tempoValidade = itens.firstOrNull()?.mesesGarantia ?: 0

    subView.openValidade(tipoValidade, tempoValidade) { validade: ValidadeSaci ->
      if (validade.isErro() && user?.admin != true) {
        DialogHelper.showError("Os dados fornecidos para a validade estão incorretos:\n${validade.msgErro()}")
      } else {
        itens.forEach { item ->
          validade.prdno = item.prdno
          validade.save()
        }
        updateView()
      }
    }
  }

  val subView
    get() = viewModel.view.tabAjusteEst
}

interface ITabAjusteEst : ITabView {
  fun filtro(): FiltroAjusteEst
  fun updateProdutos(produtos: List<AjusteEst>)
  fun produtosSelecionados(): List<AjusteEst>
  fun openValidade(tipoValidade: Int, tempoValidade: Int, block: (ValidadeSaci) -> Unit)
}