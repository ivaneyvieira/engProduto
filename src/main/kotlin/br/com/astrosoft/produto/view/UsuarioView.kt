package br.com.astrosoft.produto.view

import br.com.astrosoft.framework.view.vaadin.UserLayout
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.viewmodel.IUsuarioView
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.UsuarioViewModel
import com.github.mvysny.karibudsl.v10.formLayout
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import org.vaadin.crudui.crud.CrudOperation
import org.vaadin.crudui.crud.CrudOperation.*
import org.vaadin.crudui.crud.impl.GridCrud

@Route(layout = ProdutoLayout::class)
@PageTitle("Usuário")
@PermitAll
class UsuarioView : UserLayout<UserSaci, UsuarioViewModel>(), IUsuarioView {
  override val viewModel = UsuarioViewModel(this)
  
  override fun columns(): List<String> {
    return emptyList()
  }
  
  override fun createGrid(): GridCrud<UserSaci> {
    val crud = GridCustom()
    return crud
  }
  
  override fun formCrud(
      operation: CrudOperation?, domainObject: UserSaci?, readOnly: Boolean, binder: Binder<UserSaci>): Component {
    return VerticalLayout().apply {
      val lojas = viewModel.allLojas()
      val lojasNum = lojas.map { it.no } + listOf(0)
      
      isPadding = false
      isMargin = false
      formLayout {
        if (operation in listOf(READ, DELETE, UPDATE)) integerField("Número") {
          isReadOnly = readOnly
          binder.bind(this, UserSaci::no.name)
        }
        if (operation in listOf(ADD, READ, DELETE, UPDATE)) textField("Login") {
          isReadOnly = readOnly
          binder.bind(this, UserSaci::login.name)
        }
        if (operation in listOf(READ, DELETE, UPDATE)) textField("Nome") {
          isReadOnly = true
          binder.bind(this, UserSaci::name.name)
        }
        
        if (operation in listOf(ADD, READ, DELETE, UPDATE)) {
          select<Int>("Nome Loja") {
            isReadOnly = readOnly
            setItems(lojasNum.distinct().sorted())
            this.isEmptySelectionAllowed = true
            this.setItemLabelGenerator { storeno ->
              when (storeno) {
                0    -> "Todas as lojas"
                else -> lojas.firstOrNull { loja ->
                  loja.no == storeno
                }?.descricao ?: ""
              }
            }
            binder.bind(this, UserSaci::storeno.name)
          }
        }
      }
    }
  }
}

class GridCustom() : GridCrud<UserSaci>(UserSaci::class.java) {
  override fun createGrid(): Grid<UserSaci?> {
    return Grid(UserSaci::class.java, false).apply {
      columnGrid(UserSaci::no).setHeader("Número")
      columnGrid(UserSaci::login).setHeader("Login")
      columnGrid(UserSaci::recebimento).setHeader("Recebimento")
      columnGrid(UserSaci::ressuprimento).setHeader("Ressuprimento")
      columnGrid(UserSaci::expedicao).setHeader("Expedição")
      columnGrid(UserSaci::reposicao).setHeader("Reposição")
      columnGrid(UserSaci::pedidoTransf).setHeader("Pedido Trans")
      columnGrid(UserSaci::devCliente).setHeader("Dev Cliente")
      columnGrid(UserSaci::cliente).setHeader("Cliente")
      columnGrid(UserSaci::vendaRef).setHeader("Venda")
      columnGrid(UserSaci::pedidoRetira).setHeader("Retira")
      columnGrid(UserSaci::produto).setHeader("Produto")
      columnGrid(UserSaci::precificacao).setHeader("Precificação")
      columnGrid(UserSaci::acertoEstoque).setHeader("Acerto Estoque")
      columnGrid(UserSaci::nfd).setHeader("NFD")
      columnGrid(UserSaci::devFor2).setHeader("Dev Fornecedor")
      columnGrid(UserSaci::estoqueCD).setHeader("Controle Estoque")
      columnGrid(UserSaci::impressora).setHeader("Impressora")
    }
  }
}