package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class AudioTrack(
    val id: String,
    val title: String,
    val file: File
)

data class MusicState(
    val playlist: List<AudioTrack> = emptyList(),
    val currentTrackIndex: Int = -1,
    val isPlaying: Boolean = false,
    val volumePercent: Int = 80 // 0 to 100 in 10% increments
)

class MusicPlayerManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val musicDir = File(context.filesDir, "music")

    private val _state = MutableStateFlow(MusicState())
    val state: StateFlow<MusicState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        if (!musicDir.exists()) {
            musicDir.mkdirs()
        }
        // Protect music directory from gallery scanners as well
        val noMedia = File(musicDir, ".nomedia")
        if (!noMedia.exists()) {
            try { noMedia.createNewFile() } catch (_: Exception) {}
        }
        refreshPlaylist()
    }

    fun refreshPlaylist() {
        val files = musicDir.listFiles { file ->
            file.isFile && !file.name.startsWith(".") &&
                    (file.extension.equals("mp3", true) ||
                     file.extension.equals("m4a", true) ||
                     file.extension.equals("wav", true) ||
                     file.extension.equals("aac", true) ||
                     file.extension.equals("ogg", true))
        }?.toList() ?: emptyList()

        val tracks = files.map { file ->
            AudioTrack(
                id = file.name,
                title = file.nameWithoutExtension.replace('_', ' '),
                file = file
            )
        }

        _state.value = _state.value.copy(playlist = tracks)
    }

    fun addTrackFromUri(uri: Uri, onComplete: () -> Unit = {}) {
        scope.launch(Dispatchers.IO) {
            try {
                var fileName = "track_${System.currentTimeMillis()}.mp3"
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        val originalName = cursor.getString(nameIndex)
                        if (!originalName.isNullOrBlank()) {
                            fileName = originalName
                        }
                    }
                }

                val destFile = File(musicDir, fileName)
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }

                withContext(Dispatchers.Main) {
                    refreshPlaylist()
                    // Auto start playing if playlist was empty
                    if (_state.value.playlist.isNotEmpty() && !_state.value.isPlaying) {
                        playTrack(0)
                    }
                    onComplete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playTrack(index: Int) {
        val tracks = _state.value.playlist
        if (tracks.isEmpty() || index !in tracks.indices) return

        val track = tracks[index]
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(track.file.absolutePath)
                prepare()
                val vol = _state.value.volumePercent / 100f
                setVolume(vol, vol)
                start()
                setOnCompletionListener {
                    // Continuous playback without interruption -> play next track or loop back to 0
                    playNext()
                }
            }
            _state.value = _state.value.copy(
                currentTrackIndex = index,
                isPlaying = true
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer
        if (player != null) {
            if (player.isPlaying) {
                player.pause()
                _state.value = _state.value.copy(isPlaying = false)
            } else {
                player.start()
                _state.value = _state.value.copy(isPlaying = true)
            }
        } else {
            val tracks = _state.value.playlist
            if (tracks.isNotEmpty()) {
                val idx = if (_state.value.currentTrackIndex in tracks.indices) _state.value.currentTrackIndex else 0
                playTrack(idx)
            }
        }
    }

    fun playNext() {
        val tracks = _state.value.playlist
        if (tracks.isEmpty()) return
        val nextIndex = (_state.value.currentTrackIndex + 1) % tracks.size
        playTrack(nextIndex)
    }

    fun playPrevious() {
        val tracks = _state.value.playlist
        if (tracks.isEmpty()) return
        val prevIndex = if (_state.value.currentTrackIndex - 1 < 0) tracks.size - 1 else _state.value.currentTrackIndex - 1
        playTrack(prevIndex)
    }

    fun increaseVolume() {
        val current = _state.value.volumePercent
        val newVol = (current + 10).coerceAtMost(100)
        setVolumePercent(newVol)
    }

    fun decreaseVolume() {
        val current = _state.value.volumePercent
        val newVol = (current - 10).coerceAtLeast(0)
        setVolumePercent(newVol)
    }

    fun setVolumePercent(vol: Int) {
        val clamped = vol.coerceIn(0, 100)
        _state.value = _state.value.copy(volumePercent = clamped)
        val floatVol = clamped / 100f
        mediaPlayer?.setVolume(floatVol, floatVol)
    }

    fun removeTrack(track: AudioTrack) {
        if (_state.value.playlist.indexOf(track) == _state.value.currentTrackIndex) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            _state.value = _state.value.copy(isPlaying = false, currentTrackIndex = -1)
        }
        try {
            if (track.file.exists()) {
                track.file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        refreshPlaylist()
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
