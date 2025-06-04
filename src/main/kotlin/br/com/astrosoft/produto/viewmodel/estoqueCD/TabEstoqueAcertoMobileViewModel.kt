package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoEstoqueAcerto
import br.com.astrosoft.produto.model.saci

class TabEstoqueAcertoMobileViewModel(val viewModel: EstoqueCDViewModel) {
  val subView
    get() = viewModel.view.tabEstoqueAcertoMobile

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoEstoqueAcerto.findAll(filtro).agrupa().sortedBy { it.numero }.filter {
      (it.processado == false)
    }
    subView.updateProduto(produtos)
  }

  fun cancelarAcerto() = viewModel.exec {
    val itensSelecionado = subView.itensSelecionados().filter {
      it.processado == false
    }
    if (itensSelecionado.isEmpty()) {
      fail("Nenhum acerto não processado selecionado")
    }
    viewModel.view.showQuestion("Confirma o cancelamento do acerto selecionado?") {
      itensSelecionado.forEach {
        it.cancela()
      }
      updateView()
    }
  }

  fun geraPlanilha(produtos: List<ProdutoEstoqueAcerto>): ByteArray {
    val planilha = PlanilhaProdutoEstoqueAcerto()
    return planilha.write(produtos)
  }

  fun updateProduto(produto: ProdutoEstoqueAcerto) = viewModel.exec {
    produto.save()
  }

  fun gravaAcerto(acerto: EstoqueAcerto) = viewModel.exec {
    if (acerto.gravado == true) {
      fail("Acerto já gravado")
    }
    subView.autorizaAcerto { user ->
      val pordutos = acerto.findProdutos()
      pordutos.forEach {
        it.gravadoLogin = user.no
        it.gravado = true
        it.save()
      }
      updateView()
    }
  }

  fun removeAcerto(produto: ProdutoEstoqueAcerto?) = viewModel.exec {

    produto ?: fail("Nenhum acerto selecionado")

    if (produto.processado == true) {
      fail("Acerto está processado")
    }

    subView.autorizaAcerto {
      produto.remove()
      updateView()
    }
  }

  fun addProduto(produto: ProdutoEstoqueAcerto) {
    produto.save()
    updateView()
  }

  fun findProdutos(codigo: String, loja: Int): List<PrdGrade> {
    return saci.findGrades(codigo, loja)
  }
}

interface ITabEstoqueAcertoMobile : ITabView {
  fun filtro(): FiltroAcerto
  fun updateProduto(produtos: List<EstoqueAcerto>)
  fun itensSelecionados(): List<EstoqueAcerto>
  fun filtroVazio(): FiltroProdutoEstoque
  fun autorizaAcerto(block: (user: IUser) -> Unit)
}
