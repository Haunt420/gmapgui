package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        NetScanApp()
      }
    }
  }
}

@Composable
fun NetScanApp() {
  var selectedTab by remember { mutableIntStateOf(0) }
  var targetIp by remember { mutableStateOf("192.168.1.0/24") }
  
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = Background,
    bottomBar = { AppBottomNavigation(selectedTab) { selectedTab = it } },
    floatingActionButton = {
      if (selectedTab == 0) {
        FloatingActionButton(
          onClick = { selectedTab = 2 }, // Switch to results to simulate scan
          containerColor = Primary,
          contentColor = OnPrimary,
          shape = RoundedCornerShape(16.dp)
        ) {
          Icon(Icons.Filled.PlayArrow, contentDescription = "Start Scan")
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      TopAppBar(
        title = when (selectedTab) {
          0 -> "Command Builder"
          1 -> "Network Topology"
          2 -> "Scan Results"
          3 -> "NSE Scripts"
          else -> "NetScan"
        }
      )
      
      when (selectedTab) {
        0 -> BuilderScreen(targetIp) { targetIp = it }
        1 -> TopologyScreen()
        2 -> ScanResultsScreen()
        3 -> ScriptsScreen()
      }
    }
  }
}

@Composable
fun TopAppBar(title: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(64.dp)
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(ActiveSurface)
          .clickable { },
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = OnBackground)
      }
      Spacer(modifier = Modifier.width(16.dp))
      Column {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Normal, color = OnBackground)
        Text("Nmap Core Engine v7.93", fontSize = 12.sp, color = OnSurfaceVariant)
      }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      IconButton(onClick = { }) {
        Icon(Icons.Outlined.Share, contentDescription = "Share", tint = OnBackground)
      }
      IconButton(onClick = { }) {
        Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = OnBackground)
      }
    }
  }
}

@Composable
fun BuilderScreen(targetIp: String, onTargetChange: (String) -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Target Input
    OutlinedTextField(
      value = targetIp,
      onValueChange = onTargetChange,
      modifier = Modifier.fillMaxWidth(),
      placeholder = { Text("Target IP or CIDR", color = OnSurfaceVariant) },
      leadingIcon = { Icon(Icons.Outlined.CellWifi, contentDescription = "Target", tint = OnSurfaceVariant) },
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Primary,
        unfocusedBorderColor = Outline,
        focusedTextColor = OnBackground,
        unfocusedTextColor = OnBackground,
        focusedContainerColor = SurfaceVariant,
        unfocusedContainerColor = SurfaceVariant
      ),
      shape = RoundedCornerShape(12.dp),
      singleLine = true
    )

    CommandBuilderSection()
    ScanProfileCard()
    TerminalWindow(targetIp = targetIp)
    Spacer(modifier = Modifier.height(80.dp))
  }
}

@Composable
fun CommandBuilderSection() {
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text("Scan Types", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnSurfaceVariant)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      val scanTypes = listOf("TCP SYN (-sS)", "TCP Connect (-sT)", "UDP Scan (-sU)", "Ping Sweep (-sn)")
      items(scanTypes) { type ->
        val isSelected = type == "TCP SYN (-sS)"
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = if (isSelected) Primary else Surface,
          border = if (isSelected) null else BorderStroke(1.dp, Outline),
          modifier = Modifier.clickable { }
        ) {
          Text(
            text = type,
            color = if (isSelected) OnPrimary else OnBackground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))
    Text("Timing & Performance", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnSurfaceVariant)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      val timings = listOf("T1 (Sneaky)", "T2 (Polite)", "T3 (Normal)", "T4 (Aggressive)", "T5 (Insane)")
      items(timings) { timing ->
        val isSelected = timing == "T4 (Aggressive)"
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = if (isSelected) PrimaryContainer else Surface,
          border = if (isSelected) null else BorderStroke(1.dp, Outline),
          modifier = Modifier.clickable { }
        ) {
          Text(
            text = timing,
            color = if (isSelected) Primary else OnBackground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }
      }
    }
  }
}

@Composable
fun ScanProfileCard() {
  Surface(
    shape = RoundedCornerShape(24.dp),
    color = Surface,
    border = BorderStroke(1.dp, Outline.copy(alpha = 0.3f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
          color = PrimaryContainer,
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = "IDS-AWARE",
            color = Primary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            letterSpacing = 1.sp
          )
        }
        Text("Profile Active", color = OnSurfaceVariant, fontSize = 12.sp)
      }
      
      Spacer(modifier = Modifier.height(16.dp))
      Text("Aggressive Discovery", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        "Enables OS detection, version detection, script scanning, and traceroute. Tuned for environments without strict intrusion detection systems.",
        color = OnSurfaceVariant,
        fontSize = 14.sp,
        lineHeight = 20.sp
      )
      
      Spacer(modifier = Modifier.height(16.dp))
      Divider(color = Outline)
      Spacer(modifier = Modifier.height(16.dp))
      
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
          ProfileAvatar("OS", AccentPurple)
          ProfileAvatar("SV", AccentTeal)
          ProfileAvatar("SC", ActiveSurface)
        }
        TextButton(
          onClick = { },
          colors = ButtonDefaults.textButtonColors(contentColor = Primary)
        ) {
          Text("Modify flags", fontWeight = FontWeight.Medium)
        }
      }
    }
  }
}

@Composable
fun ProfileAvatar(text: String, color: Color) {
  Box(
    modifier = Modifier
      .size(32.dp)
      .clip(CircleShape)
      .background(color)
      .border(2.dp, Surface, CircleShape),
    contentAlignment = Alignment.Center
  ) {
    Text(text, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
  }
}

@Composable
fun TerminalWindow(targetIp: String) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(Color(0xFF0A0A0C))
      .padding(16.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
      Text("LIVE TRANSLATION", color = OnSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
      Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
    }
    Spacer(modifier = Modifier.height(12.dp))
    Text(
      text = "nmap -sS -T4 -A -v $targetIp",
      color = Color(0xFF4AF626),
      fontFamily = FontFamily.Monospace,
      fontSize = 14.sp
    )
  }
}

@Composable
fun ScriptsScreen() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text("NSE Script Manager Coming Soon", color = OnSurfaceVariant)
  }
}

// Data models for mock results
data class ScannedHost(
  val ip: String,
  val mac: String,
  val os: String,
  val ports: List<ScannedPort>
)

data class ScannedPort(
  val portId: Int,
  val protocol: String,
  val state: String,
  val service: String,
  val version: String
)

@Composable
fun ScanResultsScreen() {
  val mockHosts = listOf(
    ScannedHost(
      ip = "192.168.1.1",
      mac = "00:1A:2B:3C:4D:5E",
      os = "Linux 4.15 - 5.6",
      ports = listOf(
        ScannedPort(22, "tcp", "open", "ssh", "OpenSSH 8.2p1 Ubuntu"),
        ScannedPort(80, "tcp", "open", "http", "nginx 1.18.0"),
        ScannedPort(443, "tcp", "open", "https", "nginx 1.18.0")
      )
    ),
    ScannedHost(
      ip = "192.168.1.104",
      mac = "AA:BB:CC:DD:EE:FF",
      os = "Windows 10",
      ports = listOf(
        ScannedPort(135, "tcp", "open", "msrpc", "Microsoft Windows RPC"),
        ScannedPort(139, "tcp", "open", "netbios-ssn", "Microsoft Windows netbios-ssn"),
        ScannedPort(445, "tcp", "open", "microsoft-ds", "Windows 10 Pro 19041"),
        ScannedPort(3389, "tcp", "open", "ms-wbt-server", "Microsoft Terminal Services")
      )
    )
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Completed Scan: 192.168.1.0/24", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
      Surface(
        color = ActiveSurface,
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("2 Hosts Up", color = Primary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
      }
    }

    mockHosts.forEach { host ->
      HostResultCard(host)
    }
    
    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
fun HostResultCard(host: ScannedHost) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = Surface,
    border = BorderStroke(1.dp, Outline.copy(alpha = 0.5f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(PrimaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Filled.Computer, contentDescription = "Host", tint = Primary)
          }
          Column {
            Text(host.ip, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("MAC: ${host.mac}", color = OnSurfaceVariant, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
          }
        }
        Icon(Icons.Filled.CheckCircle, contentDescription = "Up", tint = AccentTeal)
      }
      
      Spacer(modifier = Modifier.height(12.dp))
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Outlined.Info, contentDescription = "OS", tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
        Text(host.os, color = OnSurfaceVariant, fontSize = 13.sp)
      }
      
      Spacer(modifier = Modifier.height(16.dp))
      HorizontalDivider(color = Outline.copy(alpha = 0.5f))
      Spacer(modifier = Modifier.height(12.dp))
      
      Text("OPEN PORTS", color = OnSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
      Spacer(modifier = Modifier.height(8.dp))
      
      host.ports.forEach { port ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = if (port.portId < 1024) Error.copy(alpha = 0.1f) else PrimaryContainer.copy(alpha = 0.2f),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.width(60.dp)
          ) {
            Text(
              "${port.portId}",
              color = if (port.portId < 1024) Error else Primary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(vertical = 4.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(port.service.uppercase(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
              Text("(${port.protocol})", color = OnSurfaceVariant, fontSize = 12.sp)
            }
            if (port.version.isNotEmpty()) {
              Text(port.version, color = OnSurfaceVariant, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
          }
        }
      }
    }
  }
}

@Composable
fun TopologyScreen() {
  // A simple visual interpretation of Force-Directed Topology Mapping
  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Network Topology", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
      Surface(color = ActiveSurface, shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentTeal))
          Text("Live", color = OnBackground, fontSize = 12.sp)
        }
      }
    }
    
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(16.dp)
        .clip(RoundedCornerShape(24.dp))
        .background(Surface)
        .border(1.dp, Outline.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
      contentAlignment = Alignment.Center
    ) {
      androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
        val node1 = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.3f)
        val node2 = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.2f)
        val node3 = androidx.compose.ui.geometry.Offset(size.width * 0.7f, size.height * 0.7f)
        val node4 = androidx.compose.ui.geometry.Offset(size.width * 0.3f, size.height * 0.8f)

        // Draw connections
        val lineColor = Outline.copy(alpha = 0.5f)
        val strokeWidth = 2.dp.toPx()
        drawLine(lineColor, center, node1, strokeWidth = strokeWidth)
        drawLine(lineColor, center, node2, strokeWidth = strokeWidth)
        drawLine(lineColor, center, node3, strokeWidth = strokeWidth)
        drawLine(lineColor, center, node4, strokeWidth = strokeWidth)

        // Draw Nodes
        drawCircle(color = PrimaryContainer, radius = 32.dp.toPx(), center = center)
        drawCircle(color = Primary, radius = 12.dp.toPx(), center = center)
        
        drawCircle(color = ActiveSurface, radius = 24.dp.toPx(), center = node1)
        drawCircle(color = AccentTeal, radius = 8.dp.toPx(), center = node1)
        
        drawCircle(color = ActiveSurface, radius = 24.dp.toPx(), center = node2)
        drawCircle(color = AccentTeal, radius = 8.dp.toPx(), center = node2)
        
        drawCircle(color = ActiveSurface, radius = 24.dp.toPx(), center = node3)
        drawCircle(color = Error, radius = 8.dp.toPx(), center = node3)
        
        drawCircle(color = ActiveSurface, radius = 24.dp.toPx(), center = node4)
        drawCircle(color = AccentTeal, radius = 8.dp.toPx(), center = node4)
      }
      
      // Node Labels superimposed
      Box(modifier = Modifier.fillMaxSize()) {
        Text("192.168.1.1\nGateway", color = OnBackground, fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.align(Alignment.Center).offset(y = 48.dp))
        Text("192.168.1.10", color = OnSurfaceVariant, fontSize = 10.sp, modifier = Modifier.align(Alignment.TopStart).offset(x = 60.dp, y = 140.dp))
        Text("192.168.1.104", color = OnSurfaceVariant, fontSize = 10.sp, modifier = Modifier.align(Alignment.TopEnd).offset(x = (-40).dp, y = 100.dp))
        Text("192.168.1.200\n(Vulnerable)", color = Error, fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-60).dp, y = (-120).dp))
        Text("192.168.1.50", color = OnSurfaceVariant, fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomStart).offset(x = 80.dp, y = (-80).dp))
      }
    }
  }
}

@Composable
fun AppBottomNavigation(selectedTab: Int, onTabSelected: (Int) -> Unit) {
  NavigationBar(
    containerColor = Surface,
    tonalElevation = 0.dp,
    modifier = Modifier.height(80.dp)
  ) {
    val tabs = listOf(
      Triple("Builder", Icons.Filled.Build, Icons.Outlined.Build),
      Triple("Topology", Icons.Filled.Share, Icons.Outlined.Share),
      Triple("History", Icons.Filled.History, Icons.Outlined.History),
      Triple("Scripts", Icons.Filled.Code, Icons.Outlined.Code)
    )

    tabs.forEachIndexed { index, tab ->
      val isSelected = selectedTab == index
      NavigationBarItem(
        icon = {
          Box(
            modifier = Modifier
              .size(width = 64.dp, height = 32.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) ActiveSurface else Color.Transparent),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isSelected) tab.second else tab.third,
              contentDescription = tab.first,
              tint = if (isSelected) Primary else OnSurfaceVariant
            )
          }
        },
        label = {
          Text(
            text = tab.first,
            color = if (isSelected) Primary else OnSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        },
        selected = isSelected,
        onClick = { onTabSelected(index) },
        colors = NavigationBarItemDefaults.colors(
          indicatorColor = Color.Transparent,
          selectedIconColor = Primary,
          unselectedIconColor = OnSurfaceVariant,
          selectedTextColor = Primary,
          unselectedTextColor = OnSurfaceVariant
        )
      )
    }
  }
}

