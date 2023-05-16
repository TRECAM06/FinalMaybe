import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
// This code takes in a string used as a search query, it then compiles a list of results for the app to use in displaying pdfs

@JsonClass(generateAdapter = true)
data class GoogleSearchResult(
    val items: List<GoogleSearchResult>?,
    val link: String,
    val title: String,
    @Json(name = "mime") val mimeType: String,
    val displayLink: String
)

class GoogleSearchApi(private val apiKey: String, private val cx: String) {

    private val client = OkHttpClient()


    fun search(query: String): List<GoogleSearchResult> {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("www.googleapis.com")
            .addPathSegments("customsearch/v1")
            .addQueryParameter("key", apiKey)
            .addQueryParameter("cx", cx)
            .addQueryParameter("fileType", "pdf")
            .addQueryParameter("q", query)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("Search failed: ${response.message}")
        }

        val moshi = com.squareup.moshi.Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(GoogleSearchResponse::class.java)
        val searchResponse = jsonAdapter.fromJson(response.body?.string() ?: "")

        return searchResponse?.items.orEmpty()
    }
}

@JsonClass(generateAdapter = true)
data class GoogleSearchResponse(
    val items: List<GoogleSearchResult>
)
