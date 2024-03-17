package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.reposicao.ITabReposicaoSep
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoSepViewModel
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField

class TabReposicaoSep(val viewModel: TabReposicaoSepViewModel) :
  TabPanelGrid<Reposicao>(Reposicao::class), ITabReposicaoSep {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField

  init {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    cmbLoja.value = viewModel.findLoja(0) ?: Loja.lojaZero
  }

  override fun HorizontalLayout.toolBarConfig() {
    cmbLoja = select("Loja") {
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateView()
      }
    }
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<Reposicao>.gridPanel() {
    this.addClassName("styling")
    this.format()

    columnGrid(Reposicao::numero, "Pedido")
    columnGrid(Reposicao::data, "Data")
    columnGrid(Reposicao::localizacao, "Loc")

    addColumnButton(VaadinIcon.SIGN_IN, "Assina", "Assina") { pedido ->
      viewModel.formEntregue(pedido)
    }
    columnGrid(Reposicao::entregueSNome, "Entregue")
    addColumnButton(VaadinIcon.SIGN_IN, "Assina", "Assina") { pedido ->
      viewModel.formRecebido(pedido)
    }
    columnGrid(Reposicao::recebidoSNome, "Recebido")
    columnGrid(Reposicao::usuarioApp, "Login")
  }

  override fun filtro(): FiltroReposicao {
    return FiltroReposicao(
      loja = cmbLoja.value.no,
      pesquisa = edtPesquisa.value ?: "",
      marca = EMarcaReposicao.SEP
    )
  }

  override fun updateReposicoes(reposicoes: List<Reposicao>) {
    this.updateGrid(reposicoes)
  }

  override fun formEntregue(pedido: Reposicao) {
    val form = FormAutoriza()
    DialogHelper.showForm(caption = "Entregue", form = form) {
      viewModel.entreguePedido(pedido, form.login, form.senha)
    }
  }

  override fun formRecebe(pedido: Reposicao) {
    val form = FormAutoriza()
    DialogHelper.showForm(caption = "Entregue", form = form) {
      viewModel.recebePedido(pedido, form.login, form.senha)
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.reposicaoSep == true
  }

  override val label: String
    get() = "Separado"

  override fun updateComponent() {
    viewModel.updateView()
  }
}