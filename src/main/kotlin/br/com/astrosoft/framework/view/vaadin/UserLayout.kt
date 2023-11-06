package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
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
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.function.SerializableSupplier
import org.vaadin.crudui.crud.CrudOperation
import org.vaadin.crudui.crud.CrudOperation.*
import org.vaadin.crudui.crud.impl.GridCrud
import org.vaadin.crudui.form.AbstractCrudFormFactory

abstract class UserLayout<B : IUser, VM : UserViewModel<B, *>>() : ViewLayout<VM>() {
  abstract fun createGrid(): GridCrud<B>
  abstract fun columns(): List<String>
  abstract fun formCrud(operation: CrudOperation?, domainObject: B?, readOnly: Boolean, binder: Binder<B>): Component

  override fun isAccept() = AppConfig.userLogin()?.admin == true

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

  private fun layoutCrud(
    operation: CrudOperation?,
    domainObject: B?,
    readOnly: Boolean,
    binder: Binder<B>
  ): Component {
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

    crud.grid.addThemeVariants()

    crud.crudFormFactory = UserCrudFormFactory(::layoutCrud) {
      viewModel.createNew()
    }
    crud.setSizeFull()
    return crud
  }
}

class UserCrudFormFactory<B : IUser>(
  private val createForm: (CrudOperation?, B?, Boolean, Binder<B>) -> Component,
  val createNew: () -> B
) :
  AbstractCrudFormFactory<B>() {
  override fun buildNewForm(
    operation: CrudOperation?,
    domainObject: B?,
    readOnly: Boolean,
    cancelButtonClickListener: ComponentEventListener<ClickEvent<Button>>?,
    operationButtonClickListener: ComponentEventListener<ClickEvent<Button>>?
  ): Component {
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
        READ -> "Consulta"
        ADD -> "Adiciona"
        UPDATE -> "Atualiza"
        DELETE -> "Remove"
      }
    } ?: "Erro"
  }

  private var aInstanceSupplier: SerializableSupplier<B>? = null

  override fun setNewInstanceSupplier(newInstanceSupplier: SerializableSupplier<B>?) {
    this.aInstanceSupplier = newInstanceSupplier
  }

  override fun getNewInstanceSupplier(): SerializableSupplier<B> {
    if (aInstanceSupplier == null) {
      aInstanceSupplier = SerializableSupplier<B> {
        try {
          createNew()
        } catch (e: InstantiationException) {
          e.printStackTrace()
          null
        } catch (e: IllegalAccessException) {
          e.printStackTrace()
          null
        }
      }
    }
    return aInstanceSupplier!!
  }

  override fun showError(operation: CrudOperation?, e: Exception?) {
    DialogHelper.showError(e?.message ?: "Erro desconhecido")
  }
}