package example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ColorPickerItem(
    modifier: Modifier,
    contentDefault: @Composable ((Color) -> Unit) -> Unit,
    contentAsync: @Composable ((Color) -> Unit) -> Unit,
) {
    var backgroundColor by remember { mutableStateOf(Color.Unspecified) }

    val (isAsync, setIsAsync) = remember { mutableStateOf(false) }

    Card(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(16.dp).background(backgroundColor).border(1.dp, Color.Black))
                    Text(text = backgroundColor.toArgb().toHexString())
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Async")
                    Switch(onCheckedChange = setIsAsync, checked = isAsync)
                }
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                when (isAsync) {
                    true -> contentAsync { color ->
                        backgroundColor = color
                    }

                    false -> contentDefault { color ->
                        backgroundColor = color
                    }
                }
            }
        }
    }
}