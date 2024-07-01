package com.example.chummerly.ui.decks

import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

import com.example.chummerly.model.Deck
import com.example.chummerly.model.Card
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

import androidx.compose.ui.tooling.preview.Preview
import com.example.chummerly.data.entity.CardEntity


fun generateFilename(deckName: String, fileExtension: String, context: Context) : String {
    val trimmed = deckName.trim()
    val words = trimmed.split(Regex("\\s+"))
    val joined = words.joinToString("_")
    val name =  "Chummerly_Exported_$joined"

    // Check for already-existing exports and find unique file
    var counter = 1; var uniqueName = name;
    while ((File(context.filesDir, "${uniqueName}.${fileExtension}").exists()) && counter < 50) {
        uniqueName = "${name}_${counter}"
        counter += 1
    }
    return if (File(context.filesDir, uniqueName).exists()) "" else "${uniqueName}.${fileExtension}"
}

fun deckToJson(cards: List<CardEntity>) : String {
    val json = JSONObject()
    var index = 0
    for (card in cards) {
        val q = card.front
        val a = card.back
        val key = (index + 1).toString()
        val value = JSONObject().apply {
            put("front", q)
            put("back", a)
        }
        json.put(key, value)
        index += 1
    }
    return json.toString()
}

fun deckToPlainText(cards: List<CardEntity>) : String {
    var lines = mutableListOf<String>()
    var index = 1
    for (card in cards) {
        val q = card.front
        val a = card.back
        lines.add("----------")
        lines.add("Card $index")
        lines.add("----------")
        lines.add("Front: $q")
        lines.add("Back: $a")
        lines.add("")
        index += 1
    }
    return lines.joinToString("\n")
}

fun deckToCsv(cards: List<CardEntity>) : String {
    val lines = mutableListOf<String>()
    lines.add("Card Number,Front,Back")

    var index = 1
    for (card in cards) {
        val q = card.front
        val a = card.back
        val line = "$index,$q,$a"
        lines.add(line)
        index += 1
    }
    return lines.joinToString("\n")
}


@Composable
fun ExportDeckScreen(
    deckDetailsViewModel: DeckDetailsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by deckDetailsViewModel.uiState.collectAsState()

    val context = LocalContext.current
    var selectedOption by remember { mutableStateOf("") }
    val options = listOf("CSV", "JSON", "Plain Text")
    Column(
        modifier = Modifier.padding(horizontal = 30.dp, vertical = 25.dp)
    ) {
        // Back button
        Button(
            onClick = onBackClick,
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .size(45.dp)
                .align(Alignment.Start),
            contentPadding = PaddingValues(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ChevronLeft,
                contentDescription = "Back Icon",
                tint = Color.White
            )
        }
        // Title
        Text(
            text = "Export Deck",
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 25.dp)
        )
        // Subtitle
        Text(
            text = "Please choose your desired export format.",
            style = TextStyle(fontSize = 15.sp),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(top = 15.dp, bottom = 25.dp)
        )
        // Option buttons
        options.forEach { option ->
            Button(
                onClick = { selectedOption = option },
                border = BorderStroke(
                    width = 2.dp,
                    color = if (selectedOption == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .padding(bottom = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (option == "CSV") {
                        Icon(
                            imageVector = Icons.Filled.TableView,
                            contentDescription = "Back Icon",
                            tint = Color.Black,
                            modifier = Modifier.padding(start = 0.dp)
                        )
                    } else if (option == "JSON") {
                        Icon(
                            imageVector = Icons.Filled.SettingsEthernet,
                            contentDescription = "Back Icon",
                            tint = Color.Black,
                            modifier = Modifier.padding(start = 0.dp)
                        )
                    } else if (option == "Plain Text") {
                        Icon(
                            imageVector = Icons.Filled.Article,
                            contentDescription = "Back Icon",
                            tint = Color.Black,
                            modifier = Modifier.padding(start = 0.dp)
                        )
                    }
                    Text(
                        text = option,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 15.dp),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        // Export button
        Button(
            onClick = {
                if (selectedOption == "CSV") {
                  val fileName = generateFilename(uiState.deck.name, "csv", context)
                  if (fileName.isEmpty()) {
                      // Show error modal
                      AlertDialog.Builder(context)
                          .setMessage("Oops! An error occurred. Please try again.")
                          .setPositiveButton("OK") { dialog, _ ->
                              dialog.dismiss()
                          }
                          .show()
                  } else {
                      try {
                          val csvString = deckToCsv(uiState.cards)
                          val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                          val file = File(path, fileName)
                          file.writeText(csvString)

                          // Show success modal
                          AlertDialog.Builder(context)
                              .setMessage("Success! Your deck has been saved to the Downloads folder on your device. The file name is ‘$fileName’.")
                              .setPositiveButton("OK") { dialog, _ ->
                                  dialog.dismiss()
                              }
                              .show()

                      } catch (e: FileNotFoundException) {
                          // Show error modal
                          AlertDialog.Builder(context)
                              .setMessage("Oops! An error occurred. Please try again.")
                              .setPositiveButton("OK") { dialog, _ ->
                                  dialog.dismiss()
                              }
                              .show()
                          e.printStackTrace()
                      } catch (e: IOException) {
                          // Show error modal
                          AlertDialog.Builder(context)
                              .setMessage("Oops! An error occurred. Please try again.")
                              .setPositiveButton("OK") { dialog, _ ->
                                  dialog.dismiss()
                              }
                              .show()
                          e.printStackTrace()
                      }
                  }
                } else if (selectedOption == "JSON") {
                  val fileName = generateFilename(uiState.deck.name, "json", context)
                  if (fileName.isEmpty()) {
                      // Show error modal
                      AlertDialog.Builder(context)
                          .setMessage("Oops! An error occurred. Please try again.")
                          .setPositiveButton("OK") { dialog, _ ->
                              dialog.dismiss()
                          }
                          .show()
                  } else {
                      try {
                          val jsonString = deckToJson(uiState.cards)
                          val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                          val file = File(path, fileName)
                          file.writeText(jsonString)

                          // Show success modal
                          AlertDialog.Builder(context)
                              .setMessage("Success! Your deck has been saved to the Downloads folder on your device. The file name is ‘$fileName’.")
                              .setPositiveButton("OK") { dialog, _ ->
                                  dialog.dismiss()
                              }
                              .show()

                      } catch (e: FileNotFoundException) {
                          // Show error modal
                          AlertDialog.Builder(context)
                              .setMessage("Oops! An error occurred. Please try again.")
                              .setPositiveButton("OK") { dialog, _ ->
                                  dialog.dismiss()
                              }
                              .show()
                          e.printStackTrace()
                      } catch (e: IOException) {
                          // Show error modal
                          AlertDialog.Builder(context)
                              .setMessage("Oops! An error occurred. Please try again.")
                              .setPositiveButton("OK") { dialog, _ ->
                                  dialog.dismiss()
                              }
                              .show()
                          e.printStackTrace()
                      }
                  }
                } else if (selectedOption == "Plain Text") {
                  val fileName = generateFilename(uiState.deck.name, "txt", context)
                  if (fileName.isEmpty()) {
                      // Show error modal
                      AlertDialog.Builder(context)
                          .setMessage("Oops! An error occurred. Please try again.")
                          .setPositiveButton("OK") { dialog, _ ->
                              dialog.dismiss()
                          }
                          .show()
                  } else {
                      try {
                          val plainText = deckToPlainText(uiState.cards)
                          val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                          val file = File(path, fileName)
                          file.writeText(plainText)

                          // Show success modal
                          AlertDialog.Builder(context)
                              .setMessage("Success! Your deck has been saved to the Downloads folder on your device. The file name is ‘$fileName’.")
                              .setPositiveButton("OK") { dialog, _ ->
                                  dialog.dismiss()
                              }
                              .show()

                      } catch (e: FileNotFoundException) {
                          // Show error modal
                          AlertDialog.Builder(context)
                              .setMessage("Oops! An error occurred. Please try again.")
                              .setPositiveButton("OK") { dialog, _ ->
                                  dialog.dismiss()
                              }
                              .show()
                          e.printStackTrace()
                      } catch (e: IOException) {
                          // Show error modal
                          AlertDialog.Builder(context)
                              .setMessage("Oops! An error occurred. Please try again.")
                              .setPositiveButton("OK") { dialog, _ ->
                                  dialog.dismiss()
                              }
                              .show()
                          e.printStackTrace()
                      }
                  }
                }
            },
            enabled = selectedOption.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = if (selectedOption.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.CenterHorizontally),
            shape = MaterialTheme.shapes.small
        ) {
            Text(text = "Export")
        }
    }
}
