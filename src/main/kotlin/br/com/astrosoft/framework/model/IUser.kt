package br.com.astrosoft.framework.model

interface IUser {
  var no: Int
  var login: String
  var name: String
  val admin: Boolean
  var senha: String
  var ativo: Boolean
}