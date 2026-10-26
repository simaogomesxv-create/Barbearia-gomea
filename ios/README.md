# BarberPro iOS - Barbearia Gomes • Taffinha Barber

Projeto e código-fonte nativo em **SwiftUI / iOS** pronto para compilação local no Xcode e automação CI/CD na nuvem via **CodeMagic**.

---

## 📱 Especificações Técnicas

- **Nome do Projeto Xcode:** `BarberPro.xcodeproj`
- **Scheme:** `BarberPro`
- **Bundle Identifier:** `com.barbeariagomea.barberpro`
- **Deployment Target:** iOS 16.0+ (compatível com iPhone e iPad)
- **Linguagem / Framework:** Swift 5.0 + SwiftUI
- **Catálogo de Ativos:** `ios/Assets.xcassets` com ícone de 1024x1024 e logo oficial

---

## 🔐 Credenciais Oficiais Configuradas

- **Identificador / Telefone:** `+244 948 840 379`
- **Senha do Gerente:** `Gomesbarber`
- **PIN Rápido (4 dígitos):** `2526`
- **Criação de novas contas:** Desativada (acesso pessoal e restrito).
- **Persistência de Dados:** Salva automaticamente em `UserDefaults` (clientes, vendas, faturamento e perfil persistem entre reinicializações).

---

## 📂 Estrutura do Projeto iOS

```text
ios/
├── BarberPro.xcodeproj/
│   ├── project.pbxproj         # Configuração nativa de compilação e alvos do Xcode
│   └── xcshareddata/xcschemes/
│       └── BarberPro.xcscheme  # Scheme compartilhado para xcodebuild e CodeMagic
├── Info.plist                  # Configuração do app, permissões e orientações
├── ExportOptions.plist         # Configuração de exportação de IPA para App Store
├── BarberProApp.swift          # Ponto de entrada do aplicativo (@main)
├── Assets.xcassets/            # Catálogo oficial de ícones e cores
│   ├── AppIcon.appiconset/     # Ícone 1024x1024 oficial da Barbearia Gomes
│   ├── app_logo.imageset/      # Variações 1x, 2x, 3x do emblema
│   └── AccentColor.colorset/   # Dourado oficial (#E5A93C)
├── Models/
│   └── BarberModels.swift      # Modelos (Client, ServiceItem, BusinessMetrics, SaleRecord)
├── Theme/
│   └── BarberTheme.swift       # Paleta preta e dourada, cartões e formatador Kz
├── Store/
│   └── BarberStore.swift       # Gerenciamento de estado, autenticação e persistência JSON
└── Views/
    ├── MainTabView.swift       # Navegação com barra flutuante dourada
    ├── Login/
    │   └── LoginView.swift     # Autenticação com teclado numérico PIN e senha
    ├── Dashboard/
    │   └── DashboardView.swift # Faturamento diário, métricas e últimos atendimentos
    ├── Clients/
    │   └── ClientsView.swift   # Catálogo de clientes, busca e discagem/WhatsApp
    ├── Sales/
    │   └── NewSaleView.swift   # Registro de novo corte/atendimento e pagamentos
    ├── Reports/
    │   └── ReportsView.swift   # Gráficos de vendas e análise comparativa
    └── Settings/
        └── SettingsView.swift  # Perfil do gerente, configurações e bloqueio
```

---

## ☁️ Como Rodar a Compilação no CodeMagic

1. **Acesse o CodeMagic:**
   - Faça login em [codemagic.io](https://codemagic.io) com a sua conta GitHub.
2. **Adicione a Aplicação:**
   - Selecione o repositório `Barbearia-gomea` da sua conta GitHub.
3. **Selecione o arquivo de configuração:**
   - O CodeMagic detectará automaticamente o arquivo `codemagic.yaml` presente na raiz do repositório.
4. **Executar a Build:**
   - Selecione o workflow **BarberPro iOS Native Build** (`ios-release`).
   - Clique em **Start new build**.
   - O CodeMagic iniciará uma máquina virtual Mac (macOS com Xcode) que irá compilar o projeto `ios/BarberPro.xcodeproj`, executar o scheme `BarberPro` e empacotar o arquivo `.ipa` para download nos artefatos.

---

## 🍎 Assinatura para Publicação na App Store (Opcional)

Para gerar uma versão assinada para envio direto à App Store:
1. No painel do CodeMagic, vá em **Team settings > Apple Developer Portal**.
2. Adicione sua chave da API do App Store Connect (`Key ID`, `Issuer ID` e chave privada `.p8`).
3. O workflow `codemagic.yaml` detectará automaticamente as chaves e fará a assinatura e exportação do `.ipa` com perfil de distribuição da Apple.
