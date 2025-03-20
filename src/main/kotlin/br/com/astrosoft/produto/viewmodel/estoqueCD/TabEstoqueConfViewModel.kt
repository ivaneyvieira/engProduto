package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoEstoque
import br.com.astrosoft.produto.model.printText.PrintProdutosConferenciaEstoque

class TabEstoqueConfViewModel(val viewModel: EstoqueCDViewModel) : IModelConferencia {
  val subView
    get() = viewModel.view.tabEstoqueConf

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoEstoque.findProdutoEstoque(filtro)
    subView.updateProduto(produtos)
  }

  fun geraPlanilha(produtos: List<ProdutoEstoque>): ByteArray {
    val planilha = PlanilhaProdutoEstoque()
    return planilha.write(produtos)
  }
  /*
    private fun fetchKardecHoje(produto: ProdutoEstoque): List<ProdutoKardec> {
      val date = LocalDate.now()
      val lista: List<ProdutoKardec> =
          produto.recebimentos(date) +
          produto.ressuprimento(date) +
          produto.expedicao(date) +
          produto.reposicao(date) +
          produto.acertoEstoque(date)
      return lista.ajustaOrdem()
    }

    fun updateKardec() = viewModel.exec {
      val produtos: List<ProdutoEstoque> = subView.itensSelecionados()
      ProcessamentoKardec.updateKardec(produtos)
      subView.reloadGrid()
    }*/

  override fun updateProduto(bean: ProdutoEstoque?, updateGrid: Boolean) {
    if (bean != null) {
      bean.update()
      if (updateGrid) {
        updateView()
      }
    }
  }

  fun imprimeProdutosEstoque() = viewModel.exec {
    /*val filtroVazio = subView.filtroVazio()
    val userno = AppConfig.userLogin()?.no ?: 0
    val data = LocalDate.now()

    val produtos = ProdutoEstoque.findProdutoEstoque(filtroVazio).filter {
      it.marcadoConf(userno, data)
    }*/
    val produtos = subView.itensSelecionados()
    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    val produtosAcerto = produtos.toAcerto()

    val report = PrintProdutosConferenciaEstoque()
    val user = AppConfig.userLogin() as? UserSaci

    report.print(
      dados = produtos, printer = subView.printerPreview(actionSave = {
        if (user?.estoqueGravaAcerto != true) {
          viewModel.view.showWarning("Usuário não tem permissão para gravar acerto")
        } else {
          val jaGravado = produtosAcerto.firstOrNull { it.jaGravado() }
          if (jaGravado != null) {
            viewModel.view.showWarning("Produto ${jaGravado.codigo} - ${jaGravado.grade} já foi gravado")
          } else {
            subView.autorizaAcerto {
              produtosAcerto.forEach {
                it.save()
              }
              subView.reloadGrid()
            }
          }
        }
      })
    )
  }

  /*
  fun imprimeProdutosAcerto() = viewModel.exec {
    val produtos = subView.itensSelecionados().filter {
      (it.estoqueDif ?: 0) != 0
    }.sortedBy { it.estoqueDif ?: 999999 }
    if (produtos.isEmpty()) {
      fail("Nenhum produto válido selecionado")
    }

    val report = PrintProdutosConferenciaAcerto()

    val produtosAcerto = produtos.toAcerto()

    val user = AppConfig.userLogin() as? UserSaci

    report.print(

      dados = produtosAcerto, printer = subView.printerPreview(
        showPrintBunton = false,
        actionSave = {
          if (user?.estoqueGravaAcerto != true) {
            viewModel.view.showWarning("Usuário não tem permissão para gravar acerto")
          } else {
            val jaGravado = produtosAcerto.firstOrNull { it.jaGravado() }
            if (jaGravado != null) {
              viewModel.view.showWarning("Produto ${jaGravado.codigo} - ${jaGravado.grade} já foi gravado")
            } else {
              subView.autorizaAcerto {
                produtosAcerto.forEach {
                  it.save()
                }

                produtos.forEach { prd ->
                  prd.estoqueCD = null
                  prd.estoqueLoja = null
                  prd.estoqueData = null
                  prd.estoqueUser = null
                  prd.estoqueLogin = null
                  prd.update()
                }
                subView.reloadGrid()
              }
            }
          }
        })
    )
  }*/

  fun kardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    return ProcessamentoKardec.kardec(produto)
  }
  /*
    fun limpaAcerto() {
      val itensSelecionado = subView.itensSelecionados()
      if (itensSelecionado.isEmpty()) {
        fail("Nenhum acerto selecionado")
      }
      viewModel.view.showQuestion("Confirma a limpeza dos acertos selecionados?") {
        itensSelecionado.forEach { produto ->
          produto.estoqueCD = null
          produto.estoqueLoja = null
          produto.limpaAcerto()
          produto.update()
        }
        updateView()
      }
    }*/
  /*
    fun marcaProduto() {
      val listaSelecionando = subView.itensSelecionados()
      if (listaSelecionando.isEmpty()) {
        fail("Nenhum produto selecionado")
      }

      val user = AppConfig.userLogin() as? UserSaci

      listaSelecionando.forEach { produto ->
        produto.estoqueUser = user?.no
        produto.estoqueLogin = user?.login
        produto.estoqueData = LocalDate.now()
      }
      ProdutoEstoque.update(listaSelecionando)
      subView.reloadGrid()
    }

    fun desmarcaProduto() {
      val listaSelecionando = subView.itensSelecionados()
      if (listaSelecionando.isEmpty()) {
        fail("Nenhum produto selecionado")
      }

      val user = AppConfig.userLogin() as? UserSaci

      listaSelecionando.forEach { produto ->
        produto.estoqueUser = null
        produto.estoqueLogin = null
        produto.estoqueData = null
      }
      ProdutoEstoque.update(listaSelecionando)
      subView.reloadGrid()
    }

   */
}

interface ITabEstoqueConf : ITabView {
  fun filtro(): FiltroProdutoEstoque
  fun updateProduto(produtos: List<ProdutoEstoque>)
  fun updateKardec()
  fun itensSelecionados(): List<ProdutoEstoque>
  fun reloadGrid()
  fun autorizaAcerto(block: () -> Unit)
  fun filtroVazio(): FiltroProdutoEstoque
}
