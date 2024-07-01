package com.example.chummerly.ui.decks

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Camera
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/* references:
    https://developer.android.com/codelabs/camerax-getting-started#0
    https://www.youtube.com/watch?v=Z1gu1uiPihE
    https://www.youtube.com/watch?v=pPVZambOuG8
    https://www.youtube.com/watch?v=GRHQcl496P4
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    addCardViewModel: AddCardViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onFinish: () -> Unit,
    onClickHandwriting: () -> Unit
) {
    val uiState by addCardViewModel.uiState.collectAsState()
    var isFrontSide by remember { mutableStateOf(true) }

    var isImageMenuExpanded by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    var localContext = LocalContext.current
    var isAudioMenuExpanded by remember { mutableStateOf(false) }
    var audioFile: File? = null

    val recorder by lazy {
        AudioRecorder(localContext)
    }
    val player by lazy {
        AudioPlayer(localContext)
    }

    // Used to request microphone access
    var hasMicAccess: Boolean = ContextCompat.checkSelfPermission(
        localContext,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    // Camera
    var camera by remember { mutableStateOf<Camera?>(null) }
    var cameraCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var cameraCaptureUri by remember { mutableStateOf<Uri?>(null) }
    var cameraOutputDir: File
    var cameraExecutor: ExecutorService

    var shouldShowCamera by remember { mutableStateOf(false) }
    var shouldshowPhoto by remember { mutableStateOf(false) }


    var hasCameraAccess: Boolean = ContextCompat.checkSelfPermission(
        localContext,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED



    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState(), reverseScrolling = true)
            .padding(horizontal = 30.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
            TextButton(
                modifier = Modifier,
                onClick = {
                    addCardViewModel.saveCard()
                    isFrontSide = true
                }
            ) {
                Text("Save", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Add Cards",
            modifier = Modifier.align(Alignment.Start),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (shouldShowCamera) {
            val mediaDir = localContext.externalMediaDirs.firstOrNull()?.let {
                File(it, "Chummerly").apply { mkdirs() }
            }
            if (mediaDir != null && mediaDir.exists())
                cameraOutputDir = mediaDir else cameraOutputDir = localContext.filesDir
            cameraExecutor = Executors.newSingleThreadExecutor()
            isImageMenuExpanded = false
            CameraView(outputDir = cameraOutputDir, executor = cameraExecutor, onImageCapture = {
                shouldShowCamera = false
                cameraCaptureUri = it
                shouldshowPhoto = true;
            }, onError = {})
        } else if (shouldshowPhoto){
            AsyncImage(
                model = cameraCaptureUri,
                contentDescription = "Taken Photo",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
        else {
            OutlinedTextField(
                value = if (isFrontSide) uiState.front else uiState.back,
                onValueChange = {
                    if (isFrontSide) {
                        addCardViewModel.updateFront(it)
                    } else {
                        addCardViewModel.updateBack(it)
                    }
                },
                label = { Text(if (isFrontSide) "Front Side" else "Back Side") },
                placeholder = {
                    Text(
                        if (isFrontSide) "The front side of your card - typically used for questions, prompts, or pieces of information you would like to recall." else "The back side of your card - typically used to display the answer or additional information related to the front side.",
                        color = Color.LightGray
                    )
                },
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { onClickHandwriting() }) {
                Icon(
                    Icons.Filled.Draw,
                    contentDescription = "Add Handwriting",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = { isImageMenuExpanded = !isImageMenuExpanded }) {
                Icon(
                    Icons.Filled.AddPhotoAlternate,
                    contentDescription = "Add Image",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(
                expanded = isImageMenuExpanded,
                onDismissRequest = { isImageMenuExpanded = false }) {
                DropdownMenuItem(text = { Text("Take a photo") }, onClick = {
                    if (!hasCameraAccess) {
                        val cameraPermissions = arrayOf(
                            Manifest.permission.CAMERA
                        )
                        ActivityCompat.requestPermissions(localContext as Activity, cameraPermissions, 0)
                    }
                    shouldShowCamera = true
                })
                DropdownMenuItem(text = { Text("Upload an image") }, onClick = {
                    pickImageLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                })
            }
//            IconButton(onClick = {
//                pickImageLauncher.launch(
//                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//                )
//            }) {
//                Icon(
//                    Icons.Filled.AddPhotoAlternate,
//                    contentDescription = "Add Image",
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }

            IconButton(onClick = {
                if (!hasMicAccess) {
                    val audioPermissions = arrayOf(
                        Manifest.permission.RECORD_AUDIO
                    )
                    ActivityCompat.requestPermissions(localContext as Activity, audioPermissions, 0)
                }
                isAudioMenuExpanded = !isAudioMenuExpanded
            }) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = "Add Audio",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(
                expanded = isAudioMenuExpanded,
                onDismissRequest = { isAudioMenuExpanded = false }) {
                DropdownMenuItem(text = { Text("Start Recording") }, onClick = {
                    File(localContext.cacheDir, "recording.mp3").also {
                        recorder.start(it)
                        audioFile = it
                    }
                })
                DropdownMenuItem(text = { Text("Stop Recording") }, onClick = {
                    recorder.stop()
                })
                DropdownMenuItem(text = { Text("Start Playing") }, onClick = {
                    player.playFile(audioFile ?: return@DropdownMenuItem)
                })
                DropdownMenuItem(text = { Text("Stop Playing") }, onClick = {
                    player.stop()
                })
            }
        }

        AsyncImage(
            model = imageUri,
            contentDescription = "Chosen Photo",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

//        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
//                if (isFrontSide) {
//                    savedFront.value = front
//                    back = savedBack.value
//                } else {
//                    savedBack.value = back
//                    front = savedFront.value
//                }
                isFrontSide = !isFrontSide
            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Flip Card")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.tags,
            placeholder = { Text(text = "Optional", color = Color.LightGray) },
            onValueChange = { addCardViewModel.updateTags(it) },
            label = { Text(text = "Tags") },
            modifier = Modifier.fillMaxWidth(),
        )

//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = colour,
//            placeholder = { Text(text = "Default", color = Color.LightGray) },
//            onValueChange = { colour = it },
//            label = { Text(text = "Card Colour") },
//            modifier = Modifier.fillMaxWidth()
//        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.font,
            placeholder = { Text(text = "Default", color = Color.LightGray) },
            onValueChange = { addCardViewModel.updateFont(it) },
            label = { Text(text = "Card Font") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

class AudioRecorder(
    private val context: Context
) {
    private var recorder: MediaRecorder? = null
    private val RECORDER_SAMPLE_RATE = 44100
    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    fun start(outputFile: File) {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setAudioSamplingRate(RECORDER_SAMPLE_RATE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()

            recorder = this
        }
    }

    fun stop() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }
}

class AudioPlayer(private val context: Context) {
    private var player: MediaPlayer? = null

    fun playFile(file: File) {
        MediaPlayer.create(context, file.toUri()).apply {
            player = this
            start()
        }
    }

    fun stop() {
        player?.stop()
        player?.release()
        player = null
    }
}

fun cameraTakePhoto(
    filenameFormat: String,
    imageCapture: ImageCapture,
    outputDir: File,
    executor: Executor,
    onImageCapture: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val cameraPhoto = File(
        outputDir,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val photoOutputOpt = ImageCapture.OutputFileOptions.Builder(cameraPhoto).build()

    imageCapture.takePicture(photoOutputOpt, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onError(exception: ImageCaptureException) {
            onError(exception)
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val savedUri = Uri.fromFile(cameraPhoto)
            onImageCapture(savedUri)
        }
    })
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

@Composable
fun CameraView(
    outputDir: File,
    executor: Executor,
    onImageCapture: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        IconButton(
            modifier = Modifier.padding(bottom = 20.dp),
            onClick = {
                cameraTakePhoto(
                    filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                    imageCapture = imageCapture,
                    outputDir = outputDir,
                    executor = executor,
                    onImageCapture = onImageCapture,
                    onError = onError
                )
            },
            content = {
                Icon(
                    imageVector = Icons.Sharp.Lens,
                    contentDescription = "Take picture",
                    tint = Color.White,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(1.dp)
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        )
    }
}
