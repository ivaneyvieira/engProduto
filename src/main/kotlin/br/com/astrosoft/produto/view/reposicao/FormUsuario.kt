package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.printText.TextBuffer
import br.com.astrosoft.framework.viewmodel.IUsuarioView
import br.com.astrosoft.produto.model.beans.Rota
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.UsuarioViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.karibudsl.v23.multiSelectComboBox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.data.binder.Binder

class FormUsuario(val userSaci: UserSaci) : FormLayout(), IUsuarioView {
  private val binder = Binder(UserSaci::class.java)
  private val viewModel = UsuarioViewModel(this)

  init {
    binder.bean = userSaci
    textField("Login do Usuário") {
      this.width = "300px"
      binder.bind(this, UserSaci::login.name)
    }
    textField("Nome do Usuário") {
      this.width = "300px"
      this.isReadOnly = true
      binder.bind(this, UserSaci::name.name)
    }
    checkBox("Separar") {
      binder.bind(this, UserSaci::reposicaoSep.name)
    }
    checkBox("Entregar") {
      binder.bind(this, UserSaci::reposicaoEnt.name)
    }
    multiSelectComboBox<String>("Localização") {
      setItems(listOf("TODOS") + viewModel.allLocalizacao())
      binder.bind(this, UserSaci::localizacaoRepo.name)
    }
    multiSelectComboBox<String>("Impressora") {
      this.isExpand = true
      setItems(listOf("TODAS") + viewModel.allImpressoras().map { it.name })
      binder.bind(this, UserSaci::impressoraRepo.name)
    }
  }

  override fun showError(msg: String) {
    TODO("Not yet implemented")
  }

  override fun showWarning(msg: String) {
    TODO("Not yet implemented")
  }

  override fun showInformation(msg: String) {
    TODO("Not yet implemented")
  }

  override fun showQuestion(msg: String, execYes: () -> Unit) {
    TODO("Not yet implemented")
  }

  override fun showReport(chave: String, report: ByteArray) {
    TODO("Not yet implemented")
  }

  override fun showPrintText(
    text: TextBuffer,
    showPrinter: Boolean,
    printerUser: List<String>,
    rota: Rota?,
    loja: Int,
    printEvent: (impressora: String) -> Unit
  ) {
    TODO("Not yet implemented")
  }
}