//
//  LoginView.swift
//  BarberPro iOS - Barbearia Gomes
//

import SwiftUI

public struct LoginView: View {
    @EnvironmentObject var store: BarberStore

    @State private var usePinMode: Bool = true
    @State private var identifierInput: String = BarberStore.primaryIdentifier
    @State private var passwordInput: String = ""
    @State private var isPasswordVisible: Bool = false
    @State private var pinBuffer: String = ""
    @State private var showErrorAlert: Bool = false

    public var body: some View {
        ZStack {
            BarberTheme.bgBlack.ignoresSafeArea()

            ScrollView {
                VStack(spacing: 24) {
                    Spacer().frame(height: 20)

                    // Logo & Brand Header
                    VStack(spacing: 12) {
                        Image("app_logo")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 88, height: 88)
                            .clipShape(Circle())
                            .overlay(Circle().stroke(BarberTheme.accentGold, lineWidth: 2))

                        HStack(spacing: 4) {
                            Text("Barber")
                                .font(.system(size: 28, weight: .bold))
                                .foregroundColor(BarberTheme.textWhite)
                            Text("Pro")
                                .font(.system(size: 28, weight: .bold))
                                .foregroundColor(BarberTheme.accentGold)
                        }

                        Text("Barbearia Gomes • Taffinha Barber")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(BarberTheme.accentGoldLight)

                        Text("Gestão exclusiva e pessoal do salão")
                            .font(.system(size: 13))
                            .foregroundColor(BarberTheme.textGray)
                    }

                    // Mode Selector (PIN vs Senha)
                    HStack(spacing: 0) {
                        Button(action: {
                            withAnimation(.easeInOut(duration: 0.2)) { usePinMode = true }
                        }) {
                            Text("PIN Rápido")
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(usePinMode ? BarberTheme.bgBlack : BarberTheme.textGray)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                                .background(usePinMode ? BarberTheme.accentGold : Color.clear)
                                .cornerRadius(10)
                        }

                        Button(action: {
                            withAnimation(.easeInOut(duration: 0.2)) { usePinMode = false }
                        }) {
                            Text("Senha Completa")
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(!usePinMode ? BarberTheme.bgBlack : BarberTheme.textGray)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                                .background(!usePinMode ? BarberTheme.accentGold : Color.clear)
                                .cornerRadius(10)
                        }
                    }
                    .padding(4)
                    .background(BarberTheme.surfaceElevated)
                    .cornerRadius(12)
                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(BarberTheme.borderDark, lineWidth: 1))
                    .padding(.horizontal, 24)

                    // Error Message
                    if let error = store.authErrorMessage {
                        HStack(spacing: 8) {
                            Image(systemName: "exclamationmark.triangle.fill")
                                .foregroundColor(BarberTheme.negativeRed)
                            Text(error)
                                .font(.system(size: 13))
                                .foregroundColor(BarberTheme.negativeRed)
                        }
                        .padding(.horizontal, 24)
                    }

                    if usePinMode {
                        pinSection
                    } else {
                        passwordSection
                    }

                    Spacer().frame(height: 30)
                }
            }
        }
    }

    // MARK: - PIN Section
    private var pinSection: some View {
        VStack(spacing: 20) {
            Text("Digite o PIN de 4 dígitos")
                .font(.system(size: 15, weight: .medium))
                .foregroundColor(BarberTheme.textGray)

            // 4 Pin dots
            HStack(spacing: 16) {
                ForEach(0..<4) { index in
                    Circle()
                        .fill(index < pinBuffer.count ? BarberTheme.accentGold : BarberTheme.surfaceElevated)
                        .frame(width: 18, height: 18)
                        .overlay(Circle().stroke(BarberTheme.accentGold.opacity(0.4), lineWidth: 1))
                }
            }
            .padding(.vertical, 8)

            // Numeric Keypad
            VStack(spacing: 14) {
                ForEach(0..<3) { row in
                    HStack(spacing: 24) {
                        ForEach(1..<4) { col in
                            let number = row * 3 + col
                            keypadButton("\(number)")
                        }
                    }
                }
                HStack(spacing: 24) {
                    Color.clear.frame(width: 72, height: 72)
                    keypadButton("0")
                    Button(action: deletePinDigit) {
                        Image(systemName: "delete.left")
                            .font(.system(size: 20, weight: .medium))
                            .foregroundColor(BarberTheme.textGray)
                            .frame(width: 72, height: 72)
                            .background(BarberTheme.surfaceDark)
                            .clipShape(Circle())
                            .overlay(Circle().stroke(BarberTheme.borderDark, lineWidth: 1))
                    }
                }
            }
            .padding(.top, 10)
        }
    }

    private func keypadButton(_ text: String) -> some View {
        Button(action: { appendPinDigit(text) }) {
            Text(text)
                .font(.system(size: 26, weight: .semibold))
                .foregroundColor(BarberTheme.textWhite)
                .frame(width: 72, height: 72)
                .background(BarberTheme.surfaceDark)
                .clipShape(Circle())
                .overlay(Circle().stroke(BarberTheme.borderDark, lineWidth: 1))
        }
    }

    private func appendPinDigit(_ digit: String) {
        guard pinBuffer.count < 4 else { return }
        pinBuffer.append(digit)
        if pinBuffer.count == 4 {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                let success = store.unlockWithPin(pinBuffer)
                if !success {
                    pinBuffer = ""
                }
            }
        }
    }

    private func deletePinDigit() {
        if !pinBuffer.isEmpty {
            pinBuffer.removeLast()
        }
    }

    // MARK: - Password Section
    private var passwordSection: some View {
        VStack(spacing: 16) {
            VStack(alignment: .leading, spacing: 6) {
                Text("Identificador / Telefone")
                    .font(.system(size: 13, weight: .medium))
                    .foregroundColor(BarberTheme.textGray)

                TextField("", text: $identifierInput)
                    .foregroundColor(BarberTheme.textWhite)
                    .padding(14)
                    .background(BarberTheme.surfaceDark)
                    .cornerRadius(12)
                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(BarberTheme.borderDark, lineWidth: 1))
                    .keyboardType(.phonePad)
            }

            VStack(alignment: .leading, spacing: 6) {
                Text("Senha de Acesso")
                    .font(.system(size: 13, weight: .medium))
                    .foregroundColor(BarberTheme.textGray)

                HStack {
                    if isPasswordVisible {
                        TextField("", text: $passwordInput)
                            .foregroundColor(BarberTheme.textWhite)
                    } else {
                        SecureField("", text: $passwordInput)
                            .foregroundColor(BarberTheme.textWhite)
                    }

                    Button(action: { isPasswordVisible.toggle() }) {
                        Image(systemName: isPasswordVisible ? "eye.slash" : "eye")
                            .foregroundColor(BarberTheme.textGray)
                    }
                }
                .padding(14)
                .background(BarberTheme.surfaceDark)
                .cornerRadius(12)
                .overlay(RoundedRectangle(cornerRadius: 12).stroke(BarberTheme.borderDark, lineWidth: 1))
            }

            Button(action: {
                _ = store.loginWithCredentials(identifier: identifierInput, password: passwordInput)
            }) {
                Text("Entrar no Sistema")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(BarberTheme.bgBlack)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 16)
                    .background(BarberTheme.accentGold)
                    .cornerRadius(12)
            }
            .padding(.top, 10)
        }
        .padding(.horizontal, 24)
    }
}
