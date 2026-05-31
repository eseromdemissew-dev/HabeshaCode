package com.example.network

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

object GeminiService {
    
    suspend fun getAiResponse(systemPrompt: String, userMessage: String, contextHistory: List<com.example.data.AiMessage> = emptyList()): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        // Check for missing or placeholder API Key
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "GEMINI_API_KEY" || apiKey.length < 15) {
            return@withContext getMockResponse(systemPrompt, userMessage)
        }

        // Map context history to Gemini Contents format (limit to last 6 turns to prevent token spillover)
        val apiContents = mutableListOf<Content>()
        contextHistory.takeLast(6).forEach { msg ->
            apiContents.add(Content(listOf(Part(text = msg.text))))
        }
        // Append current message
        apiContents.add(Content(listOf(Part(text = userMessage))))

        val systemInstructionContent = Content(listOf(Part(text = systemPrompt)))
        val request = GenerateContentRequest(
            contents = apiContents,
            systemInstruction = systemInstructionContent
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "I was able to reach the model, but it didn't return any readable output tokens. Let's try restructuring our prompt!"
        } catch (e: Exception) {
            e.printStackTrace()
            // Graceful fallback to rich simulate response instead of simple crash
            "Connection failed: ${e.localizedMessage}. Fallback AI response: ${getMockResponse(systemPrompt, userMessage)}"
        }
    }

    private fun getMockResponse(systemPrompt: String, message: String): String {
        val cleanMsg = message.lowercase().trim()
        
        val banner = "*(📝 System Alert: Run in sandbox mock. To connect real Gemini power, add your GEMINI_API_KEY in the Secrets Panel in AI Studio)*\n\n"
        
        if (systemPrompt.contains("teacher", ignoreCase = true)) {
            val lessonResponse = when {
                cleanMsg.contains("html") || cleanMsg.contains("css") -> """
                    ### Akam! Let's explore HTML & CSS 🌐
                    
                    HTML (HyperText Markup Language) builds the structural bones of your view container, while CSS handles the visual gradients and styling layers.
                    
                    Here is an example of an elegant, modern dark glass card with a golden stroke in CSS:
                    
                    ```html
                    <div class="habesha-card">
                        <h1>HabeshaCode Design</h1>
                        <p>Learn. Build. Innovate.</p>
                    </div>
                    ```
                    
                    ### 💡 Challenge Exercise
                    Try writing a CSS rule that applies a linear gradient background representing the Ethiopian colors (Green, Yellow, Red) at low opacity. Type your response in our Playground!
                """.trimIndent()
                
                cleanMsg.contains("closure") || cleanMsg.contains("variable") || cleanMsg.contains("javascript") -> """
                    ### Akam! Let's master JavaScript Closures ⚡
                    
                    Think of a closure as a mini-backpack that a function carries memory in. Whenever a function is returned from another nested function, it keeps access to all variable declarations inside that parent scope.
                    
                    ```js
                    function createHabeshaStudent(name) {
                        let xp = 100; // local scope
                        return {
                            getXp: () => xp, // captures xp scope
                            addXp: (amount) => { xp += amount; }
                        };
                    }
                    ```
                    
                    ### 💡 Challenge Exercise
                    Can you write a JavaScript function that returns a counter which starts at 10 and decrements by 1 on each call? Try executing this in the Explorer!
                """.trimIndent()
                
                else -> """
                    ### Akam, student! 👋
                    
                    I am your adaptive HabeshaCode AI Programming Teacher. I am trained to explain computer science paradigms with simple analogical guides, code snippets, and active exercises.
                    
                    You asked: *"$message"*
                    
                    Here is a fast conceptual breakdown:
                    1. **Deconstruct complex blocks**: Always isolate inputs and outputs before writing code.
                    2. **Practice and build**: Write the algorithms by hand inside our Developer Playground.
                    
                    ### 💡 Brainstorm Practice
                    Can you tell me: What programming language or framework are you focusing on today? I can prepare a custom quiz!
                """.trimIndent()
            }
            return banner + lessonResponse
        } else if (systemPrompt.contains("debugger", ignoreCase = true)) {
            return banner + """
                ### Bug Investigation Report 🔧
                
                Upon deconstructing your request: *"$message"*, I have diagnosed potential syntax leaks and runtime inefficiencies.
                
                #### 🔍 Identified Issues
                1. **Scope Leaks**: Variables declared as global can overwrite runtime states. Use `const` or `let` strictly.
                2. **Unchecked Exceptions**: Lacking `try-catch` structures around async inputs can crash the main thread.
                
                #### 🚀 Solution & Optimized Pattern
                ```javascript
                // Wrap inputs in clean, bounded scopes
                const computeSafeMetrics = (data) => {
                    try {
                        if (!data) return { xp: 0 };
                        return { xp: data.level * 100 };
                    } catch (err) {
                        console.error("Scope calculation error:", err);
                    }
                };
                ```
                
                Let me know if this resolves your compiler conflicts! Try running this snippet in the code panel.
            """.trimIndent()
        } else if (systemPrompt.contains("builder", ignoreCase = true)) {
            return banner + """
                ### Project Blueprint & Architecture 🚀
                
                You are planning to build: *"$message"*. That is an excellent choice! Here is a clean, scalable architectural outline to bring this idea to life.
                
                #### 📂 Directory Structure Tree
                - `src/`
                  - `data/` -> Persistent databases and caches
                  - `ui/` -> Clean responsive views
                  - `network/` -> REST API clients
                
                #### 🔋 Suggested Starter Code
                Here is a lightweight Kotlin class setup for managing items safely:
                ```kotlin
                data class ProjectSchema(
                    val id: String,
                    val title: String,
                    val techStack: List<String>
                )
                ```
                
                #### 🗺️ Step-By-Step Roadmap
                - **Milestone 1**: Define the database schemas using Room entities locally.
                - **Milestone 2**: Build standard Jetpack Compose panels for lists.
                - **Milestone 3**: Connect the Google Gemini model to suggest smart recommendations dynamically.
            """.trimIndent()
        } else if (systemPrompt.contains("coach", ignoreCase = true)) {
            return banner + """
                ### Technical Interview Preparation coach 🎤
                
                Welcome to your Habesha tech prep! I am a simulated interviewer designed to build your engineering muscle.
                
                #### ❓ Interview Question of the Hour:
                > "What is the difference between synchronous execution and asynchronous execution? How does Kotlin prevent threads from blocking during continuous networking requests?"
                
                #### 💡 Guidelines to Craft Your Ideal Answer:
                1. Highlight **blocking vs non-blocking** thread patterns.
                2. Reference the use of lightweight `suspend` coroutines in the Dispatchers thread pool.
                
                Type your response below and I will grade it on a 1-10 scale!
            """.trimIndent()
        } else {
            // General Mentor guidance
            return banner + """
                ### HabeshaCode Industry Mentor 🤖
                
                Selam, junior! I am a senior developer with years of experience navigating the software engineering landscape.
                
                You asked: *"$message"*
                
                Here is my mentorship focus for you:
                - **Focus on Fundamentals**: Stacking up libraries is easy, but understanding memory heaps, asynchronous cycles, and database indexing is what separates juniors from senior architects.
                - **Write clean, readable code**: Code is written for humans to read first, and microprocessors to compile second. Use meaningful variable naming and modularize operations.
                - **Build in the open**: Complete our daily code missions and share your projects directly to the Showcase. Showing real working products builds immense career trust.
                
                What milestones are you tracking this week? Let me know how I can guide your roadmap.
            """.trimIndent()
        }
    }
}
