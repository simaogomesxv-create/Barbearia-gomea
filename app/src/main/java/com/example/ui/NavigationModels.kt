package com.example.ui

enum class AppScreen {
  WELCOME,
  LOGIN,
  MAIN,
  RELATORIO_MENSAL,
  RELATORIO_ANUAL
}

enum class MainTab(val index: Int, val label: String) {
  INICIO(0, "Início"),
  CLIENTES(1, "Clientes"),
  VENDAS(2, "Vendas"),
  MAIS(3, "Mais")
}
