package com.example.ui.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MotivationGold
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PremiumPlanScreen() {
    var selectedPlan by remember { mutableStateOf("yearly") } // "monthly", "yearly", "lifetime"
    var isUnlocked by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier.height(12.dp))
            // Hero Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF2C1B03), Color(0xFF140D02))
                        )
                    )
                    .border(1.dp, MotivationGold, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MotivationGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                }
                Spacer(Modifier.height(10.dp))
                Text("VV MOTIVATION PRO VIP", color = MotivationGold, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("Unlock the Ultimate Motivational Reels Powerhouse", color = TextSecondary, fontSize = 12.sp)
            }
        }

        // Features Checklist
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianCard)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("VIP Creator Benefits:", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                listOf(
                    "4K Ultra HD & 60 FPS Crystal Clear Export",
                    "100% Watermark Free Export",
                    "All 8 3D Typography Materials & Extrusions",
                    "Unlimited Gemini AI Auto Captions & Voice-overs",
                    "Full Access to 100+ Exclusive Motivational Soundtracks",
                    "Advanced Motion Glitch, VHS & Impact Effects",
                    "Green Screen / Chroma Key & PIP Multi-layer"
                ).forEach { feat ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(MotivationGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(feat, color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Plan Selection
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PlanOptionCard(
                    title = "Annual VIP Pass (Save 60%)",
                    price = "$2.99 / month (billed yearly)",
                    badge = "BEST VALUE 🔥",
                    isSelected = selectedPlan == "yearly",
                    onClick = { selectedPlan = "yearly" }
                )
                PlanOptionCard(
                    title = "Monthly Creator",
                    price = "$6.99 / month",
                    badge = null,
                    isSelected = selectedPlan == "monthly",
                    onClick = { selectedPlan = "monthly" }
                )
                PlanOptionCard(
                    title = "Lifetime Founder Pass",
                    price = "$49.99 one-time",
                    badge = "FOREVER ACCESS",
                    isSelected = selectedPlan == "lifetime",
                    onClick = { selectedPlan = "lifetime" }
                )
            }
        }

        // CTA Button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(MotivationGold, Color(0xFFFF9E00))
                        )
                    )
                    .clickable { isUnlocked = true }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUnlocked) "✅ PRO UNLOCKED (ACTIVE)" else "🚀 Upgrade to VV PRO VIP",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PlanOptionCard(
    title: String,
    price: String,
    badge: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) ObsidianSurfaceVariant else ObsidianCard)
            .border(
                if (isSelected) 2.dp else 1.dp,
                if (isSelected) MotivationGold else ObsidianBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                if (badge != null) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MotivationGold)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(badge, color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
            Text(price, color = MotivationGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(2.dp, if (isSelected) MotivationGold else ObsidianBorder, CircleShape)
                .background(if (isSelected) MotivationGold else Color.Transparent)
        )
    }
}
