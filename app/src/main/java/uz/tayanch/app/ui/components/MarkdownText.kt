package uz.tayanch.app.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import uz.tayanch.app.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A small, native Markdown renderer (Pillar 6). It maps Markdown straight onto
 * Compose Text/Surface composables — there is no HTML DOM and no WebView, so an
 * injected `<script>` is rendered as inert characters and can never execute.
 * Supports headings, bold, inline code, bullet lists, block quotes and fenced
 * code blocks with a copy button — enough for the learning content.
 */
@Composable
fun MarkdownText(markdown: String, modifier: Modifier = Modifier) {
    val blocks = remember(markdown) { parseMarkdown(markdown) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        blocks.forEach { block -> RenderBlock(block) }
    }
}

@Composable
private fun RenderBlock(block: MdBlock) {
    when (block) {
        is MdBlock.Heading -> Text(
            text = inline(block.text),
            style = when (block.level) {
                1 -> MaterialTheme.typography.headlineSmall
                2 -> MaterialTheme.typography.titleLarge
                else -> MaterialTheme.typography.titleMedium
            },
            fontWeight = FontWeight.Bold,
        )
        is MdBlock.Paragraph -> Text(inline(block.text), style = MaterialTheme.typography.bodyLarge)
        is MdBlock.Bullet -> Row {
            Text("•  ", style = MaterialTheme.typography.bodyLarge)
            Text(inline(block.text), style = MaterialTheme.typography.bodyLarge)
        }
        is MdBlock.Quote -> Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                inline(block.text),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth().padding(12.dp),
            )
        }
        is MdBlock.Code -> CodeBlock(code = block.code, language = block.language)
    }
}

/** Reused by flashcards and quizzes. Monospace, horizontally scrollable, copyable. */
@Composable
fun CodeBlock(code: String, language: String?, modifier: Modifier = Modifier) {
    val clipboard = LocalClipboardManager.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = language?.uppercase() ?: "CODE",
                    color = Color(0xFF9AE6B4),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { clipboard.setText(AnnotatedString(code)) }) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = stringResource(R.string.cd_copy), tint = Color(0xFFBBBBBB))
                }
            }
            Box(Modifier.horizontalScroll(rememberScrollState())) {
                Text(
                    text = code,
                    color = Color(0xFFE6E6E6),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

// ----- tiny parser -----

private sealed interface MdBlock {
    data class Heading(val level: Int, val text: String) : MdBlock
    data class Paragraph(val text: String) : MdBlock
    data class Bullet(val text: String) : MdBlock
    data class Quote(val text: String) : MdBlock
    data class Code(val code: String, val language: String?) : MdBlock
}

private fun parseMarkdown(src: String): List<MdBlock> {
    val out = mutableListOf<MdBlock>()
    val lines = src.replace("\r\n", "\n").split("\n")
    var i = 0
    val paragraph = StringBuilder()

    fun flushParagraph() {
        if (paragraph.isNotBlank()) out += MdBlock.Paragraph(paragraph.toString().trim())
        paragraph.clear()
    }

    while (i < lines.size) {
        val line = lines[i]
        when {
            line.trimStart().startsWith("```") -> {
                flushParagraph()
                val lang = line.trimStart().removePrefix("```").trim().ifBlank { null }
                val code = StringBuilder()
                i++
                while (i < lines.size && !lines[i].trimStart().startsWith("```")) {
                    code.appendLine(lines[i]); i++
                }
                out += MdBlock.Code(code.toString().trimEnd('\n'), lang)
            }
            line.startsWith("### ") -> { flushParagraph(); out += MdBlock.Heading(3, line.removePrefix("### ")) }
            line.startsWith("## ") -> { flushParagraph(); out += MdBlock.Heading(2, line.removePrefix("## ")) }
            line.startsWith("# ") -> { flushParagraph(); out += MdBlock.Heading(1, line.removePrefix("# ")) }
            line.startsWith("> ") -> { flushParagraph(); out += MdBlock.Quote(line.removePrefix("> ")) }
            line.trimStart().startsWith("- ") -> { flushParagraph(); out += MdBlock.Bullet(line.trimStart().removePrefix("- ")) }
            line.trimStart().matches(Regex("^\\d+\\.\\s.*")) -> {
                flushParagraph(); out += MdBlock.Bullet(line.trimStart().substringAfter(". "))
            }
            line.isBlank() -> flushParagraph()
            else -> paragraph.appendLine(line)
        }
        i++
    }
    flushParagraph()
    return out
}

/** Inline formatting: **bold** and `code`. Everything else is literal text. */
private fun inline(text: String): AnnotatedString = buildAnnotatedString {
    var i = 0
    while (i < text.length) {
        when {
            text.startsWith("**", i) -> {
                val end = text.indexOf("**", i + 2)
                if (end > 0) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(text.substring(i + 2, end)) }
                    i = end + 2
                } else { append(text[i]); i++ }
            }
            text[i] == '`' -> {
                val end = text.indexOf('`', i + 1)
                if (end > 0) {
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0x22000000))) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                } else { append(text[i]); i++ }
            }
            else -> { append(text[i]); i++ }
        }
    }
}
