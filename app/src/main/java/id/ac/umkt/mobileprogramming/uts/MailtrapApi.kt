package id.ac.umkt.mobileprogramming.uts

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

// Struktur data email yang diminta oleh Mailtrap
data class MailtrapRequest(
    val to: List<EmailAddress>,
    val from: EmailAddress,
    val subject: String,
    val html: String
)

data class EmailAddress(val email: String, val name: String)

// Jalan tol (API Endpoint) menuju Mailtrap Sandbox
interface MailtrapApi {
    @POST("api/send/{inbox_id}")
    fun sendEmail(
        @Path("inbox_id") inboxId: String,
        @Header("Api-Token") apiToken: String,
        @Body request: MailtrapRequest
    ): Call<Void>
}