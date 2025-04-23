package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoEstoque
import br.com.astrosoft.produto.model.printText.PrintProdutosConferenciaEstoque2
import java.time.LocalDate

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

  override fun updateProduto(bean: ProdutoEstoque?, updateGrid: Boolean) {
    if (bean != null) {
      bean.update()
      if (updateGrid) {
        updateView()
      }
    }
  }

  fun imprimeProdutosEstoque() = viewModel.exec {
    val filtroVazio = subView.filtroVazio()
    val numLoja = filtroVazio.loja
    val userno = AppConfig.userLogin()?.no ?: 0
    val data = LocalDate.now()

    val produtos = ProdutoEstoque.findProdutoEstoque(filtroVazio).filter {
      it.marcadoConf(userno, data)
    }

    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    val numero = ProdutoEstoqueAcerto.proximoNumero(numLoja)

    val produtosAcerto = produtos.toAcerto(numero)

    val report = PrintProdutosConferenciaEstoque2("Relatório de Estoque")
    val user = AppConfig.userLogin() as? UserSaci

    report.print(
      dados = produtos, printer = subView.printerPreview(showPrintBunton = false, actionSave = { form ->
        if (user?.estoqueGravaAcerto != true) {
          viewModel.view.showWarning("Usuário não tem permissão para gravar acerto")
        } else {
          val jaGravado = produtosAcerto.firstOrNull { it.jaGravado() }
          if (jaGravado != null) {
            viewModel.view.showWarning("Produto ${jaGravado.codigo} - ${jaGravado.grade} já foi gravado")
          } else {
            subView.autorizaAcerto { user ->
              form.close()
              produtosAcerto.forEach {
                it.login = user.login
                it.save()
              }
              produtos.forEach { produto ->
                produto.estoqueUser = null
                produto.estoqueLogin = null
                produto.estoqueData = null
              }
              ProdutoEstoque.update(produtos)
              updateView()
            }
          }
        }
      })
    )
  }

  fun imprimeProdutosGarantia() = viewModel.exec {
    val filtroVazio = subView.filtroVazio()
    val numLoja = filtroVazio.loja
    val userno = AppConfig.userLogin()?.no ?: 0
    val data = LocalDate.now()

    val produtos = ProdutoEstoque.findProdutoEstoque(filtroVazio).filter {
      it.marcadoConf(userno, data)
    }

    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    val numero = ProdutoPedidoGarantia.proximoNumero(numLoja)

    val produtosGarantia = produtos.toGarantia(numero)

    val report = PrintProdutosConferenciaEstoque2("Relatório de Estoque")
    val user = AppConfig.userLogin() as? UserSaci

    report.print(
      dados = produtos, printer = subView.printerPreview(showPrintBunton = false, actionSave = { form ->
        if (user?.estoqueGravaGarantia != true) {
          viewModel.view.showWarning("Usuário não tem permissão para gravar garantia")
        } else {
          val jaGravado = produtosGarantia.firstOrNull { it.jaGravadoGarantia() }
          if (jaGravado != null) {
            viewModel.view.showWarning("Produto ${jaGravado.codigo} - ${jaGravado.grade} já foi gravado na garantia")
          } else {
            subView.autorizaGarantia { user ->
              form.close()
              produtosGarantia.forEach {
                it.saveGarantia()
              }
              produtos.forEach { produto ->
                produto.estoqueUser = null
                produto.estoqueLogin = null
                produto.estoqueData = null
              }
              ProdutoEstoque.update(produtos)
              updateView()
            }
          }
        }
      })
    )
  }

  fun kardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    return ProcessamentoKardec.kardec(produto)
  }

  fun marcaProduto(listaSelecionando: List<ProdutoEstoque>) {
    val user = AppConfig.userLogin() as? UserSaci ?: return
    val data = LocalDate.now()

    listaSelecionando.forEach { produto ->
      if (!produto.marcadoConf(user.no, data)) {
        produto.estoqueUser = user.no
        produto.estoqueLogin = user.login
        produto.estoqueData = LocalDate.now()
        produto.update()
      }
    }
  }

  fun marcaProduto() {
    val listaSelecionando = subView.itensSelecionados()
    if (listaSelecionando.isEmpty()) {
      fail("Nenhum produto selecionado")
    }
    marcaProduto(listaSelecionando)
    subView.reloadGrid()
  }

  fun desmarcaProduto() {
    val listaSelecionando = subView.itensSelecionados()
    if (listaSelecionando.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    listaSelecionando.forEach { produto ->
      produto.estoqueUser = null
      produto.estoqueLogin = null
      produto.estoqueData = null
    }
    ProdutoEstoque.update(listaSelecionando)
    subView.reloadGrid()
  }
}

interface ITabEstoqueConf : ITabView {
  fun filtro(): FiltroProdutoEstoque
  fun updateProduto(produtos: List<ProdutoEstoque>)
  fun updateKardec()
  fun itensSelecionados(): List<ProdutoEstoque>
  fun reloadGrid()
  fun autorizaAcerto(block: (user: UserSaci) -> Unit)
  fun autorizaGarantia(block: (user: UserSaci) -> Unit)
  fun filtroVazio(): FiltroProdutoEstoque
}
