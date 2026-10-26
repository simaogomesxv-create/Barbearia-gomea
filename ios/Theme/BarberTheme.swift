//
//  BarberTheme.swift
//  BarberPro iOS - Barbearia Gomes
//

import SwiftUI

public enum BarberTheme {
    // Brand Colors
    public static let bgBlack = Color(red: 9/255, green: 9/255, blue: 11/255)
    public static let surfaceDark = Color(red: 18/255, green: 18/255, blue: 21/255)
    public static let surfaceElevated = Color(red: 24/255, green: 24/255, blue: 27/255)
    public static let borderDark = Color(red: 39/255, green: 39/255, blue: 42/255)

    // Accents
    public static let accentGold = Color(red: 229/255, green: 169/255, blue: 60/255)
    public static let accentGoldLight = Color(red: 245/255, green: 197/255, blue: 107/255)
    public static let accentGoldDark = Color(red: 180/255, green: 130/255, blue: 40/255)

    // Text & Status
    public static let textWhite = Color(red: 244/255, green: 244/255, blue: 245/255)
    public static let textGray = Color(red: 161/255, green: 161/255, blue: 170/255)
    public static let textMuted = Color(red: 113/255, green: 113/255, blue: 122/255)
    public static let positiveGreen = Color(red: 16/255, green: 185/255, blue: 129/255)
    public static let negativeRed = Color(red: 239/255, green: 68/255, blue: 68/255)

    // Formatters
    private static let kzFormatter: NumberFormatter = {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.groupingSeparator = "."
        return formatter
    }()

    public static func formatKz(_ value: Int64) -> String {
        return "\(kzFormatter.string(from: NSNumber(value: value)) ?? "\(value)") Kz"
    }
}

// Reusable card style
public struct BarberCardModifier: ViewModifier {
    public func body(content: Content) -> some View {
        content
            .background(BarberTheme.surfaceDark)
            .cornerRadius(16)
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(BarberTheme.borderDark, lineWidth: 1)
            )
    }
}

public extension View {
    func barberCard() -> some View {
        self.modifier(BarberCardModifier())
    }
}
