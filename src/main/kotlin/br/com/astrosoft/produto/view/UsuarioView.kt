package br.com.astrosoft.produto.view

import br.com.astrosoft.framework.view.vaadin.UserLayout
import br.com.astrosoft.framework.viewmodel.IUsuarioView
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.UsuarioViewModel
import com.github.mvysny.karibudsl.v10.formLayout
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
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
    return listOf(
      UserSaci::no.name,
      UserSaci::login.name,
      UserSaci::recebimento.name,
      UserSaci::ressuprimento.name,
      UserSaci::expedicao.name,
      UserSaci::reposicao.name,
      UserSaci::pedidoTransf.name,
      UserSaci::devCliente.name,
      UserSaci::cliente.name,
      UserSaci::vendaRef.name,
      UserSaci::pedidoRetira.name,
      UserSaci::produto.name,
      UserSaci::precificacao.name,
      UserSaci::acertoEstoque.name,
      UserSaci::nfd.name,
      UserSaci::devFor2.name,
      UserSaci::estoqueCD.name,
      UserSaci::impressora.name
    )
  }
  
  override fun createGrid() = GridCrud(UserSaci::class.java).apply {
    this.grid.getColumnBy(UserSaci::no).setHeader("Número")
    this.grid.getColumnBy(UserSaci::login).setHeader("Login")
    this.grid.getColumnBy(UserSaci::recebimento).setHeader("Recebimento").renderBoolean()
    this.grid.getColumnBy(UserSaci::ressuprimento).setHeader("Ressuprimento").renderBoolean()
    this.grid.getColumnBy(UserSaci::expedicao).setHeader("Expedição").renderBoolean()
    this.grid.getColumnBy(UserSaci::reposicao).setHeader("Reposição").renderBoolean()
    this.grid.getColumnBy(UserSaci::pedidoTransf).setHeader("Pedido Trans").renderBoolean()
    this.grid.getColumnBy(UserSaci::devCliente).setHeader("Dev Cliente").renderBoolean()
    this.grid.getColumnBy(UserSaci::cliente).setHeader("Cliente").renderBoolean()
    this.grid.getColumnBy(UserSaci::vendaRef).setHeader("Venda").renderBoolean()
    this.grid.getColumnBy(UserSaci::pedidoRetira).setHeader("Retira").renderBoolean()
    this.grid.getColumnBy(UserSaci::produto).setHeader("Produto").renderBoolean()
    this.grid.getColumnBy(UserSaci::precificacao).setHeader("Precificação").renderBoolean()
    this.grid.getColumnBy(UserSaci::acertoEstoque).setHeader("Acerto Estoque").renderBoolean()
    this.grid.getColumnBy(UserSaci::nfd).setHeader("NFD").renderBoolean()
    this.grid.getColumnBy(UserSaci::devFor2).setHeader("Dev Fornecedor").renderBoolean()
    this.grid.getColumnBy(UserSaci::estoqueCD).setHeader("Controle Estoque").renderBoolean()
    this.grid.getColumnBy(UserSaci::impressora).setHeader("Impressora")
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

fun Grid.Column<UserSaci>.renderBoolean() {

}