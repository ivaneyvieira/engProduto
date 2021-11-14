package br.com.astrosoft.framework.view

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.viewmodel.UserViewModel
import com.github.mvysny.karibudsl.v10.alignSelf
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.hr
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR
import com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY
import com.vaadin.flow.component.grid.GridVariant.LUMO_COMPACT
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.binder.Binder
import org.claspina.confirmdialog.ConfirmDialog
import org.vaadin.crudui.crud.CrudOperation
import org.vaadin.crudui.crud.CrudOperation.*
import org.vaadin.crudui.crud.impl.GridCrud
import org.vaadin.crudui.form.AbstractCrudFormFactory

abstract class UserLayout<B : IUser, VM : UserViewModel<B, *>> : ViewLayout<VM>() {
  abstract fun createGrid(): GridCrud<B>
  abstract fun columns(): List<String>
  abstract fun formCrud(operation: CrudOperation?, domainObject: B?, readOnly: Boolean, binder: Binder<B>): Component

  override fun isAccept(user: IUser) = user.admin

  init {
    form("Editor de usuários")
    val crud: GridCrud<B> = gridCrud()
    this.add(crud)
    setOperationd(crud)
  }

  private fun setOperationd(crud: GridCrud<B>) {
    crud.setOperations({ viewModel.findAll() },
                       { user: B? -> viewModel.add(user) },
                       { user: B? -> viewModel.update(user) },
                       { user: B? -> viewModel.delete(user) })
  }

  private fun layoutCrud(operation: CrudOperation?, domainObject: B?, readOnly: Boolean, binder: Binder<B>): Component {
    return VerticalLayout().apply {
      isSpacing = false
      isMargin = false
      val form = formCrud(operation, domainObject, readOnly, binder)
      this.addAndExpand(form)
    }
  }

  private fun gridCrud(): GridCrud<B> {
    val crud: GridCrud<B> = createGrid()
    crud.grid.apply {
      removeAllColumns()
      columns().forEach { addColumn(it) }
    }

    crud.grid.addThemeVariants(LUMO_COMPACT)

    crud.crudFormFactory = UserCrudFormFactory(::layoutCrud)
    crud.setSizeFull()
    return crud
  }
}

class UserCrudFormFactory<B : IUser>(private val createForm: (CrudOperation?, B?, Boolean, Binder<B>) -> Component) :
        AbstractCrudFormFactory<B>() {
  override fun buildNewForm(operation: CrudOperation?,
                            domainObject: B?,
                            readOnly: Boolean,
                            cancelButtonClickListener: ComponentEventListener<ClickEvent<Button>>?,
                            operationButtonClickListener: ComponentEventListener<ClickEvent<Button>>?): Component {
    val binder = Binder(domainObject?.javaClass)
    return VerticalLayout().apply {
      isSpacing = false
      isMargin = false
      addAndExpand(createForm(operation, domainObject, readOnly, binder))
      hr()
      horizontalLayout {
        this.setWidthFull()
        this.justifyContentMode = JustifyContentMode.END
        button("Confirmar") {
          alignSelf = END
          addThemeVariants(LUMO_PRIMARY)
          addClickListener {
            binder.writeBean(domainObject)
            operationButtonClickListener?.onComponentEvent(it)
          }
        }
        button("Cancelar") {
          alignSelf = END
          addThemeVariants(LUMO_ERROR)
          addClickListener(cancelButtonClickListener)
        }
      }

      binder.readBean(domainObject)
    }
  }

  override fun buildCaption(operation: CrudOperation?, domainObject: B?): String {
    return operation?.let { crudOperation ->
      when (crudOperation) {
        READ   -> "Consulta"
        ADD    -> "Adiciona"
        UPDATE -> "Atualiza"
        DELETE -> "Remove"
      }
    } ?: "Erro"
  }

  override fun showError(operation: CrudOperation?, e: Exception?) {
    ConfirmDialog.createError().withCaption("Erro do aplicativo").withMessage(e?.message ?: "Erro desconhecido").open()
  }
}