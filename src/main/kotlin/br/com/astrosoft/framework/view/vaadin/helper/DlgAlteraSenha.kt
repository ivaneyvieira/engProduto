package br.com.astrosoft.framework.view.vaadin.helper

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.beans.UserSaci
import com.github.mvysny.karibudsl.v10.passwordField
import com.vaadin.flow.component.formlayout.FormLayout

class DlgAlteraSenha {
  fun show() {
    val user = AppConfig.userLogin() as? UserSaci ?: return
    val form = FormLayout()
    val edtSenhaAtual = form.passwordField("Senha Atual")
    val edtNovaSenha = form.passwordField("Nova Senha")
    val edtConfirmaSenha = form.passwordField("Confirma Nova Senha")

    DialogHelper.showForm("Alterar Senha", form) {
      val senhaAtual = edtSenhaAtual.value ?: ""
      val novaSenha = edtNovaSenha.value ?: ""
      val confirmaSenha = edtConfirmaSenha.value ?: ""

      when {
        user.senha?.uppercase()?.trim() != senhaAtual.uppercase().trim() -> {
          DialogHelper.showError("Senha atual incorreta")
        }
        novaSenha.isBlank() -> {
          DialogHelper.showError("Nova senha não pode ser vazia")
        }
        novaSenha != confirmaSenha -> {
          DialogHelper.showError("A nova senha e a confirmação não coincidem")
        }
        else -> {
          user.senha = novaSenha
          UserSaci.updateUser(user)
          DialogHelper.showInformation("Senha alterada com sucesso")
        }
      }
    }
  }
}
