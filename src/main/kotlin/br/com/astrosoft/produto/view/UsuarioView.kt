package br.com.astrosoft.produto.view

import br.com.astrosoft.framework.view.UserLayout
import br.com.astrosoft.framework.viewmodel.IUsuarioView
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.UsuarioViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.vaadin.crudui.crud.CrudOperation
import org.vaadin.crudui.crud.CrudOperation.*
import org.vaadin.crudui.crud.impl.GridCrud

@Route(layout = ProdutoLayout::class)
@PageTitle("Usuário")
class UsuarioView : UserLayout<UserSaci, UsuarioViewModel>(), IUsuarioView {
  override val viewModel = UsuarioViewModel(this)

  override fun columns(): List<String> {
    return listOf(UserSaci::no.name, UserSaci::login.name, UserSaci::name.name, UserSaci::impressora.name)
  }

  override fun createGrid() = GridCrud(UserSaci::class.java)

  override fun formCrud(
    operation: CrudOperation?,
    domainObject: UserSaci?,
    readOnly: Boolean,
    binder: Binder<UserSaci>
  ): Component {
    return FormLayout().apply {
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
        comboBox<Int>("Número Loja") {
          isReadOnly = readOnly
          isAllowCustomValue = false
          val lojas = viewModel.allLojas()
          val values = lojas.map { it.no } + listOf(0)
          setItems(values.distinct().sorted())
          this.setItemLabelGenerator { storeno ->
            when (storeno) {
              0 -> "Todas as lojas"
              else -> lojas.firstOrNull { loja ->
                loja.no == storeno
              }?.descricao ?: ""
            }
          }
          isAllowCustomValue = false
          binder.bind(this, UserSaci::storeno.name)
        }
        checkBoxGroup<String> {
          label = "Locais"
          isReadOnly = readOnly

          val locais = viewModel.allLocais()
          val values = listOf("TODOS") + locais.map { it.abreviacao }.sorted()
          setItems(values)
          binder.bind(this, UserSaci::listLocais.name)
        }
        formLayout {
          h4("Produto") {
            colspan = 2
          }
          checkBox("Produtos") {
            isReadOnly = readOnly
            binder.bind(this, UserSaci::produtoList.name)
          }
          checkBox("Retira/Entrega") {
            isReadOnly = readOnly
            binder.bind(this, UserSaci::produtoRetiraEntrega.name)
          }
          checkBox("Retira/Entrega/Edit") {
            isReadOnly = readOnly
            binder.bind(this, UserSaci::produtoRetiraEntregaEdit.name)
          }
          checkBox("Reserva") {
            isReadOnly = readOnly
            binder.bind(this, UserSaci::produtoReserva.name)
          }
        }
        formLayout {
          h4("Nota") {
            colspan = 2
          }
          checkBox("Exp") {
            isReadOnly = readOnly
            binder.bind(this, UserSaci::notaExp.name)
          }
          checkBox("CD") {
            isReadOnly = readOnly
            binder.bind(this, UserSaci::notaCD.name)
          }
          checkBox("Entrege") {
            isReadOnly = readOnly
            binder.bind(this, UserSaci::notaEnt.name)
          }
          checkBox("Voltar CD") {
            isReadOnly = readOnly
            binder.bind(this, UserSaci::voltarCD.name)
          }
          checkBox("Voltar Ent") {
            isReadOnly = readOnly
            binder.bind(this, UserSaci::voltarEnt.name)
          }
        }
        formLayout {
          h4("Pedido") {
            colspan = 2
          }
          checkBox("CD") {
            isReadOnly = readOnly
            binder.bind(this, UserSaci::pedidoCD.name)
          }
          checkBox("Entrege") {
            isReadOnly = readOnly
            binder.bind(this, UserSaci::pedidoEnt.name)
          }
        }
        formLayout {
          h4("Ressuprimento") {
            colspan = 2
          }
          checkBox("CD") {
            isReadOnly = readOnly
            binder.bind(this, UserSaci::ressuprimentoCD.name)
          }
          checkBox("Entrege") {
            isReadOnly = readOnly
            binder.bind(this, UserSaci::ressuprimentoEnt.name)
          }
        }
      }
    }
  }
}


