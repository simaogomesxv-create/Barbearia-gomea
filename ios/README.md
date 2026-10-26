# BarberPro iOS - Barbearia Gomes • Taffinha Barber

Esta pasta contém o projeto e código-fonte completo e nativo em **SwiftUI / iOS** do aplicativo **BarberPro (Barbearia Gomes)**, permitindo compilar e instalar diretamente no iPhone via Xcode em um computador Mac.

---

## 📱 Credenciais Oficiais Configuradas

- **Identificador / Telefone:** `+244 948 840 379`
- **Senha do Gerente:** `Gomesbarber`
- **PIN Rápido (4 dígitos):** `2526`
- **Criação de novas contas:** Desativada (Acesso pessoal exclusivo da Barbearia Gomes)

---

## 📂 Estrutura dos Arquivos Criados

```text
ios/
├── BarberProApp.swift          # Ponto de entrada do aplicativo (@main)
├── Assets/
│   └── app_logo.png           # Logo oficial da Barbearia Gomes em alta resolução
├── Models/
│   └── BarberModels.swift     # Modelos (Client, ServiceItem, BusinessMetrics, SaleRecord)
├── Theme/
│   └── BarberTheme.swift      # Tema escuro e dourado (#E5A93C), fontes e formatadores (Kz)
├── Store/
│   └── BarberStore.swift      # Gerenciamento de estado, autenticação e dados
└── Views/
    ├── MainTabView.swift      # Navegação principal com barra flutuante dourada
    ├── Login/
    │   └── LoginView.swift    # Tela de acesso (PIN 4 dígitos + Senha)
    ├── Dashboard/
    │   └── DashboardView.swift# Painel com faturamento, métricas, serviços e histórico
    ├── Clients/
    │   └── ClientsView.swift  # Lista de clientes com busca e atalhos rápidos WhatsApp/Ligação
    ├── Sales/
    │   └── NewSaleView.swift  # Checkout rápido de atendimento e formas de pagamento
    ├── Reports/
    │   └── ReportsView.swift  # Gráficos de vendas e análise comparativa
    └── Settings/
        └── SettingsView.swift # Perfil do gerente, configurações e bloqueio
```

---

## 🚀 Como Abrir e Rodar no iPhone (via Xcode no Mac)

1. **Baixe o projeto:**
   - Faça o download do arquivo ZIP do projeto ou clone pelo GitHub no seu Mac.

2. **Abra o Xcode:**
   - Clique em **File > New > Project...**
   - Escolha **iOS > App** e clique em **Next**.
   - Defina o nome como `BarberPro`, Interface como **SwiftUI**, Linguagem como **Swift**.

3. **Copie os arquivos:**
   - Arraste a pasta `ios/` (ou os arquivos `.swift`) para dentro do seu projeto no Xcode.
   - Arraste a imagem `ios/Assets/app_logo.png` para dentro do catálogo `Assets.xcassets` com o nome `app_logo`.

4. **Conecte o iPhone:**
   - Conecte o seu iPhone no Mac via cabo USB ou Wi-Fi.
   - Em **Signing & Capabilities**, selecione sua conta Apple (gratuita ou Apple Developer).
   - Clique no botão **Play (Run)** no topo do Xcode. O aplicativo será instalado e abrirá no seu iPhone!
