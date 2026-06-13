package uz.tayanch.app.ui.interview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.tayanch.app.R
import uz.tayanch.app.ui.security.SecureScreenEffect
import uz.tayanch.app.ui.theme.TayanchTheme

/**
 * Mock-interview video room (the thesis's Agora WebRTC screen). Layout-only: the
 * camera tiles are placeholders. FLAG_SECURE is applied (Pillar 2) because the
 * interview is confidential and must not be screen-recorded.
 */
@Composable
fun InterviewScreen(onEnd: () -> Unit) {
    SecureScreenEffect(antiTapjacking = false)
    InterviewContent(onEnd = onEnd)
}

@Composable
private fun InterviewContent(onEnd: () -> Unit) {
    var muted by remember { mutableStateOf(false) }
    var cameraOff by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(Color(0xFF0E1116))) {
        // Peer (candidate) video — fills the screen.
        VideoTile(label = stringResource(R.string.interview_candidate), big = true, modifier = Modifier.fillMaxSize())

        // Candidate context chip (top).
        Surface(
            color = Color(0xCC000000),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.statusBarsPadding().padding(16.dp).align(Alignment.TopStart),
        ) {
            Text(
                stringResource(R.string.interview_context, "Jasur Abdullaev", "Strong Junior", 3),
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }

        // Self preview (picture-in-picture, top-right).
        Surface(
            color = Color(0xFF1B2230),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.statusBarsPadding().padding(16.dp).align(Alignment.TopEnd).size(110.dp, 150.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(if (cameraOff) Icons.Filled.VideocamOff else Icons.Filled.Person, contentDescription = null, tint = Color(0xFF9AA4B2), modifier = Modifier.size(40.dp))
                Text(stringResource(R.string.interview_you), color = Color.White, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.BottomCenter).padding(6.dp))
            }
        }

        // Controls (bottom).
        Row(
            Modifier.fillMaxWidth().align(Alignment.BottomCenter).navigationBarsPadding().padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ControlButton(if (muted) Icons.Filled.MicOff else Icons.Filled.Mic, stringResource(R.string.interview_mute), Color(0xFF2B313B)) { muted = !muted }
            ControlButton(Icons.Filled.CallEnd, stringResource(R.string.interview_end), Color(0xFFD32F2F), onClick = onEnd)
            ControlButton(if (cameraOff) Icons.Filled.VideocamOff else Icons.Filled.Videocam, stringResource(R.string.interview_camera), Color(0xFF2B313B)) { cameraOff = !cameraOff }
        }
    }
}

@Composable
private fun VideoTile(label: String, big: Boolean, modifier: Modifier = Modifier) {
    Box(modifier.background(Color(0xFF161B22)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(color = Color(0xFF2B313B), shape = CircleShape) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF9AA4B2), modifier = Modifier.padding(28.dp).size(if (big) 64.dp else 36.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(label, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ControlButton(icon: ImageVector, label: String, bg: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(color = bg, shape = CircleShape, onClick = onClick, modifier = Modifier.size(60.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(26.dp)) }
        }
        Spacer(Modifier.height(6.dp))
        Text(label, color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}

@Preview(showBackground = true)
@Composable
private fun InterviewScreenPreview() {
    TayanchTheme { InterviewContent(onEnd = {}) }
}
