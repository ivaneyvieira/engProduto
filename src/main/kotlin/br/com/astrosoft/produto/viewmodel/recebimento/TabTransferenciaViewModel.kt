package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.firstDayOfMonth
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*

class TabTransferenciaViewModel(val viewModel: RecebimentoViewModel) {
  val subView
    get() = viewModel.view.tabTransferencia

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaRecebimento.findAll(filtro)
    subView.updateNota(notas)
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun selecionaProdutos(nota: NotaRecebimento, codigoBarra: String) = viewModel.exec {
    val produto = nota.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.validaProduto()
    produto.selecionado = true
    subView.reloadGrid()
  }

  private fun NotaRecebimentoProduto.validaProduto() {
    if (this.validadeValida != "S") {
      fail("Validade não cadastrada")
    }
    val numVal = this.validade
    val validade = Validade.findValidade(numVal ?: 0)
    if (validade != null) {
      val dataRecebimento = this.data ?: fail("Data da nota não informada")
      this.vencimento ?: fail("Data de vencimento obrigatório")
      val dataFabricacao = this.fabricacao ?: fail("Data de fabricação obrigatório")
      val dataFabricacaoLimite =
          dataRecebimento.minusMonths(validade.mesesFabricacao.toLong() - 1.toLong()).firstDayOfMonth()
      if (dataFabricacao < dataFabricacaoLimite) {
        fail("Data de fabricação inferior a ${dataFabricacaoLimite.format("MM/yy")}")
      }
    }
  }

  fun salvaNotaProduto(bean: NotaRecebimentoProduto?) = viewModel.exec {
    bean ?: fail("Produto não encontrado")
    bean.validaProduto()
    bean.salva()
    updateView()
  }

  fun cadastraValidade() = viewModel.exec {
    val itens = subView.produtosSelecionados()
    val user = AppConfig.userLogin() as? UserSaci

    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    val tipoValidade = 2
    val tempoValidade = itens.firstOrNull()?.tempoValidade ?: 0

    subView.openValidade(tipoValidade, tempoValidade) { validade: ValidadeSaci ->
      if (validade.isErro() && user?.admin != true) {
        DialogHelper.showError("Os dados fornecidos para a validade estão incorretos:\n${validade.msgErro()}")
      } else {
        itens.forEach { item ->
          validade.prdno = item.prdno
          validade.save()
        }
        subView.updateProduto()
      }
    }
  }

  fun recebeNotaProduto(produtos: List<NotaRecebimentoProduto>, login: String, senha: String) {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    produtos.forEach {
      it.recebe(user)
      it.salva()
    }

    updateView()
  }

  fun enviaProdutoSelecionado() = viewModel.exec {
    val produtosSelecionados = subView.produtosSelecionados()
    if (produtosSelecionados.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    subView.formAssina(produtosSelecionados)
  }
}

interface ITabTransferencia : ITabView {
  fun filtro(): FiltroNotaRecebimentoProduto
  fun updateNota(notas: List<NotaRecebimento>)
  fun updateProduto(): NotaRecebimento?
  fun closeDialog()
  fun focusCodigoBarra()
  fun produtosSelecionados(): List<NotaRecebimentoProduto>
  fun openValidade(tipoValidade: Int, tempoValidade: Int, block: (ValidadeSaci) -> Unit)
  fun formAssina(produtos: List<NotaRecebimentoProduto>)
  fun reloadGrid()
}