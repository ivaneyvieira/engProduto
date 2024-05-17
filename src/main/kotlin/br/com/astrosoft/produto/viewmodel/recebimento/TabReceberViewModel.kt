package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import java.time.LocalDate

class TabReceberViewModel(val viewModel: RecebimentoViewModel) {
  val subView
    get() = viewModel.view.tabReceber

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
    val user = AppConfig.userLogin() as? UserSaci
    val produto = nota.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.marcaEnum = EMarcaRecebimento.RECEBIDO
    produto.login = user?.login ?: ""
    produto.salva()
    val novaNota = subView.updateProduto()
    if (novaNota == null || nota.produtos.isEmpty()){
      subView.closeDialog()
    }
  }

  fun salvaNotaProduto(bean: NotaRecebimentoProduto?)= viewModel.exec {
    bean ?: fail("Produto não encontrado")
    val numVal = bean.validade ?: fail("Validade não informada")
    val validade = Validade.findValidade(numVal) ?: fail("Validade não encontrada")
    val dataNota = bean.data ?: fail("Data da nota não informada")
    val dataMaxima = validade.dataMaxima(dataNota)
    val dataVencimento = bean.vencimento ?: fail("Vencimento não informado")
    if(dataVencimento > dataMaxima) fail("Vencimento maior que a validade")
    bean.salva()
    updateView()
  }
}

interface ITabReceber : ITabView {
  fun filtro(): FiltroNotaRecebimentoProduto
  fun updateNota(notas: List<NotaRecebimento>)
  fun updateProduto(): NotaRecebimento?
  fun closeDialog()
}