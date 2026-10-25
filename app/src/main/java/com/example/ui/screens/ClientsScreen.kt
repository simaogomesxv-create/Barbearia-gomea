package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ClientEntity
import com.example.model.Client
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.ButtonPrimaryBg
import com.example.ui.theme.ButtonPrimaryText
import com.example.ui.theme.CardBorder
import com.example.ui.theme.InputBg
import com.example.ui.theme.InputBorder
import com.example.ui.theme.NegativeRed
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun ClientsScreen(
  clients: List<Client>,
  rawClients: List<ClientEntity>,
  searchQuery: String,
  onSearchQueryChanged: (String) -> Unit,
  onBack: () -> Unit,
  onAddClient: (name: String, phone: String, notes: String) -> Unit,
  onUpdateClient: (ClientEntity) -> Unit,
  onDeleteClient: (String) -> Unit,
  onScheduleForClient: (clientName: String, clientId: String) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current

  var showAddDialog by remember { mutableStateOf(false) }
  var selectedClientForDetail by remember { mutableStateOf<Client?>(null) }
  var clientToEdit by remember { mutableStateOf<ClientEntity?>(null) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundBlack)
      .windowInsetsPadding(WindowInsets.statusBars)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 10.dp)
        .widthIn(max = 600.dp)
    ) {
      // Top Bar: Back Arrow, Title "Clientes", Add Button "+"
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("clients_back_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Voltar",
            tint = TextWhite
          )
        }

        Text(
          text = "Clientes",
          color = TextWhite,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )

        IconButton(
          onClick = { showAddDialog = true },
          modifier = Modifier.testTag("clients_add_btn")
        ) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(SurfaceElevated)
              .border(1.dp, CardBorder, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Adicionar Cliente",
              tint = TextWhite,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Search Bar: "Buscar cliente..."
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        placeholder = { Text("Buscar cliente por nome ou telefone...", color = TextMuted, fontSize = 14.sp) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Buscar",
            tint = TextGray
          )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = InputBg,
          unfocusedContainerColor = InputBg,
          focusedBorderColor = AccentGold,
          unfocusedBorderColor = InputBorder,
          focusedTextColor = TextWhite,
          unfocusedTextColor = TextWhite,
          cursorColor = AccentGold
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("clients_search_input")
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Clients List
      if (clients.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = if (searchQuery.isBlank()) "Nenhum cliente cadastrado ainda no banco de dados." else "Nenhum cliente encontrado.",
              color = TextGray,
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
              onClick = { showAddDialog = true },
              colors = ButtonDefaults.buttonColors(
                containerColor = ButtonPrimaryBg,
                contentColor = ButtonPrimaryText
              ),
              shape = RoundedCornerShape(100.dp)
            ) {
              Text("Cadastrar Primeiro Cliente")
            }
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(clients, key = { it.id }) { client ->
            ClientItemRow(
              client = client,
              onClick = { selectedClientForDetail = client }
            )
          }

          item {
            Spacer(modifier = Modifier.height(20.dp))
          }
        }
      }
    }
  }

  // Add Client Dialog
  if (showAddDialog) {
    AddClientModal(
      onDismiss = { showAddDialog = false },
      onConfirm = { name, phone, notes ->
        onAddClient(name, phone, notes)
        showAddDialog = false
        Toast.makeText(context, "Cliente $name salvo com sucesso!", Toast.LENGTH_SHORT).show()
      }
    )
  }

  // Edit Client Dialog
  clientToEdit?.let { entity ->
    EditClientModal(
      clientEntity = entity,
      onDismiss = { clientToEdit = null },
      onSave = { updated ->
        onUpdateClient(updated)
        clientToEdit = null
        Toast.makeText(context, "Cliente atualizado com sucesso!", Toast.LENGTH_SHORT).show()
      },
      onDelete = { id ->
        onDeleteClient(id)
        clientToEdit = null
        selectedClientForDetail = null
        Toast.makeText(context, "Cliente excluído.", Toast.LENGTH_SHORT).show()
      }
    )
  }

  // Client Details Modal
  selectedClientForDetail?.let { client ->
    val fullEntity = rawClients.firstOrNull { it.id == client.id } ?: ClientEntity(
      id = client.id,
      name = client.name,
      phone = client.phone,
      lastService = client.lastService,
      lastServiceDate = client.lastServiceDate
    )

    ClientDetailModal(
      client = client,
      onEdit = {
        clientToEdit = fullEntity
      },
      onSchedule = {
        onScheduleForClient(client.name, client.id)
        selectedClientForDetail = null
      },
      onDismiss = { selectedClientForDetail = null }
    )
  }
}

@Composable
fun ClientItemRow(
  client: Client,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(SurfaceDark)
      .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 12.dp)
      .testTag("client_card_${client.id}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Initials Avatar Circle
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(Color(0xFF3F3F46)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = client.initials,
          color = TextWhite,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      // Client Info
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = client.name,
          color = TextWhite,
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = client.phone,
          color = TextGray,
          fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = "Último serviço: ${client.lastService}",
          color = TextMuted,
          fontSize = 11.sp
        )
        Text(
          text = client.lastServiceDate,
          color = TextMuted,
          fontSize = 10.sp
        )
      }

      // Chevron
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = "Ver detalhes",
        tint = TextGray,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@Composable
fun AddClientModal(
  onDismiss: () -> Unit,
  onConfirm: (name: String, phone: String, notes: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("+244 ") }
  var notes by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Text(
        text = "Novo Cliente",
        color = TextWhite,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nome completo", color = TextGray) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = AccentGold,
            unfocusedBorderColor = InputBorder
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Telefone (+244)", color = TextGray) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = AccentGold,
            unfocusedBorderColor = InputBorder
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Observação / Preferência", color = TextGray) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = AccentGold,
            unfocusedBorderColor = InputBorder
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            onConfirm(name, phone, notes)
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = ButtonPrimaryBg,
          contentColor = ButtonPrimaryText
        ),
        shape = RoundedCornerShape(100.dp)
      ) {
        Text("Salvar Cliente", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancelar", color = TextGray)
      }
    }
  )
}

@Composable
fun EditClientModal(
  clientEntity: ClientEntity,
  onDismiss: () -> Unit,
  onSave: (ClientEntity) -> Unit,
  onDelete: (String) -> Unit
) {
  var name by remember { mutableStateOf(clientEntity.name) }
  var phone by remember { mutableStateOf(clientEntity.phone) }
  var notes by remember { mutableStateOf(clientEntity.notes) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Text(
        text = "Editar Cliente",
        color = TextWhite,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nome completo", color = TextGray) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = AccentGold,
            unfocusedBorderColor = InputBorder
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Telefone", color = TextGray) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = AccentGold,
            unfocusedBorderColor = InputBorder
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Notas ou observações", color = TextGray) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = AccentGold,
            unfocusedBorderColor = InputBorder
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Button(
          onClick = { onDelete(clientEntity.id) },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0x33EF4444),
            contentColor = NegativeRed
          ),
          shape = RoundedCornerShape(100.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Excluir Cliente")
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            onSave(clientEntity.copy(name = name.trim(), phone = phone.trim(), notes = notes.trim()))
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = ButtonPrimaryBg,
          contentColor = ButtonPrimaryText
        ),
        shape = RoundedCornerShape(100.dp)
      ) {
        Text("Salvar Alterações", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancelar", color = TextGray)
      }
    }
  )
}

@Composable
fun ClientDetailModal(
  client: Client,
  onEdit: () -> Unit,
  onSchedule: () -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceDark,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Color(0xFF3F3F46)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = client.initials,
              color = TextWhite,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(text = client.name, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = client.phone, color = TextGray, fontSize = 12.sp)
          }
        }

        IconButton(onClick = onEdit) {
          Icon(Icons.Default.Edit, contentDescription = "Editar", tint = AccentGold)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "Último Atendimento Registrado",
          color = AccentGold,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "${client.lastService} (${client.lastServiceDate})",
          color = TextWhite,
          fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${client.phone}"))
              try {
                context.startActivity(intent)
              } catch (e: Exception) {
                Toast.makeText(context, "Ligando para ${client.phone}", Toast.LENGTH_SHORT).show()
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = SurfaceElevated,
              contentColor = TextWhite
            ),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Outlined.Call, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Ligar", fontSize = 12.sp)
          }

          Button(
            onClick = {
              Toast.makeText(context, "Abrindo WhatsApp com ${client.name}...", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = SurfaceElevated,
              contentColor = TextWhite
            ),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Outlined.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("WhatsApp", fontSize = 12.sp)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
          onClick = onSchedule,
          colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceElevated,
            contentColor = AccentGold
          ),
          shape = RoundedCornerShape(100.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Novo Atendimento para este Cliente", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(
          containerColor = ButtonPrimaryBg,
          contentColor = ButtonPrimaryText
        ),
        shape = RoundedCornerShape(100.dp)
      ) {
        Text("Fechar")
      }
    }
  )
}
