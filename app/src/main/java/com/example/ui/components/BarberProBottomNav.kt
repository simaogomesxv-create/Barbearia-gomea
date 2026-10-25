package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainTab
import com.example.ui.theme.CardBorder
import com.example.ui.theme.NavActive
import com.example.ui.theme.NavBackground
import com.example.ui.theme.NavInactive

data class NavItemSpec(
  val tab: MainTab,
  val label: String,
  val activeIcon: ImageVector,
  val inactiveIcon: ImageVector,
  val testTag: String
)

@Composable
fun BarberProBottomNav(
  selectedTab: MainTab,
  onTabSelected: (MainTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val items = listOf(
    NavItemSpec(
      tab = MainTab.INICIO,
      label = "Início",
      activeIcon = Icons.Filled.Home,
      inactiveIcon = Icons.Outlined.Home,
      testTag = "nav_tab_inicio"
    ),
    NavItemSpec(
      tab = MainTab.CLIENTES,
      label = "Clientes",
      activeIcon = Icons.Filled.People,
      inactiveIcon = Icons.Outlined.People,
      testTag = "nav_tab_clientes"
    ),
    NavItemSpec(
      tab = MainTab.VENDAS,
      label = "Vendas",
      activeIcon = Icons.Filled.PointOfSale,
      inactiveIcon = Icons.Outlined.PointOfSale,
      testTag = "nav_tab_vendas"
    ),
    NavItemSpec(
      tab = MainTab.MAIS,
      label = "Mais",
      activeIcon = Icons.Filled.Menu,
      inactiveIcon = Icons.Outlined.Menu,
      testTag = "nav_tab_mais"
    )
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(NavBackground)
  ) {
    HorizontalDivider(
      thickness = 1.dp,
      color = CardBorder
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.navigationBars)
        .padding(horizontal = 12.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      items.forEach { item ->
        val isSelected = selectedTab == item.tab
        val contentColor = if (isSelected) NavActive else NavInactive

        Box(
          modifier = Modifier
            .weight(1f)
            .minimumInteractiveComponentSize()
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null
            ) {
              onTabSelected(item.tab)
            }
            .testTag(item.testTag)
            .padding(vertical = 4.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = if (isSelected) item.activeIcon else item.inactiveIcon,
              contentDescription = item.label,
              tint = contentColor,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = item.label,
              color = contentColor,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
          }
        }
      }
    }
  }
}
