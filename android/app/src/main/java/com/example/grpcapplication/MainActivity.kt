package com.example.grpcapplication

import CRUDServiceGrpc
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.grpcapplication.ui.theme.GRPCApplicationTheme
import createRequest
import deleteRequest
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import readRequest
import updateRequest

class MainActivity : ComponentActivity() {

    private val uri by lazy { Uri.parse("http://192.168.29.148:50051/") }
    private val myGrpcClient by lazy { MyGrpcClient(uri = uri) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GRPCApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Sujeet", myGrpcClient)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myGrpcClient.close()


    }
}

@Composable
fun Greeting(name: String, myGrpcClient: MyGrpcClient, modifier: Modifier = Modifier) {
    myGrpcClient.readResponse.value = "Hello $name!"

    LaunchedEffect(key1 = Unit) {
        myGrpcClient.performRequests()
    }

    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = myGrpcClient.readResponse.value, modifier = modifier.clickable { })
    }
}

class MyGrpcClient(private val uri: Uri) {
    val readResponse = mutableStateOf("")

//    println("Connecting to ${uri.host}:${uri.port}")

    private val channel = ManagedChannelBuilder.forAddress(uri.host, uri.port).apply {
        if (uri.scheme == "https")
            useTransportSecurity()
        else
            usePlaintext()

    }.build()


    private val stub = CRUDServiceGrpc.newBlockingStub(channel)

    suspend fun performRequests() {
        val createId = withContext(Dispatchers.IO) { create("item") }
        Log.d("MyGrpc", "performRequests: $createId")

        var readData = withContext(Dispatchers.IO) { read(createId) }
        readResponse.value = readData ?: "Read failed"
        Log.d("MyGrpc", "performRequests: ${readResponse.value}")
        val updateResponse = withContext(Dispatchers.IO) { update(createId, "Updated Data") }
        if (updateResponse) {
            readData = withContext(Dispatchers.IO) { read(createId) }
            readResponse.value = readData ?: "Read failed"
            Log.d("MyGrpc", "performRequests: ${readResponse.value}")

        }
        // Perform delete request if needed
        withContext(Dispatchers.IO) { delete(createId) }
    }

    private fun create(data: String): Int {
        val request = createRequest { this.data = data }
        return stub.create(request).id
    }

    private fun update(id: Int, data: String): Boolean {
        val request = updateRequest { this.id = id; this.data = data }
        return try {
            stub.update(request)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun delete(id: Int) {
        val request = deleteRequest { this.id = id }
        stub.delete(request)
    }

    private fun read(id: Int): String? {
        val request = readRequest { this.id = id }
        return try {
            val response = stub.read(request)
            response.data.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun close() {
        channel.shutdown()
    }
}
