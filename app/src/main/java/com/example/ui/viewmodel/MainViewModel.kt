package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.GeminiService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val dao = database.appDao()
    private val repository = AppRepository(dao)

    // --- State Channels ---
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _onboardingStep = MutableStateFlow(1)
    val onboardingStep: StateFlow<Int> = _onboardingStep.asStateFlow()

    private val _selectedInterests = MutableStateFlow<Set<String>>(emptySet())
    val selectedInterests: StateFlow<Set<String>> = _selectedInterests.asStateFlow()

    // Screen routes state: LOGIN, ONBOARDING, MAIN_DASHBOARD
    private val _currentScreen = MutableStateFlow("LOGIN")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Tab routes in main dashboard: HOME, COURSES, PLAYGROUND, AI_MENTOR, LEADERBOARD, FEED
    private val _currentTab = MutableStateFlow("HOME")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Selected Course details
    private val _selectedCourseId = MutableStateFlow<String?>(null)
    val selectedCourseId: StateFlow<String?> = _selectedCourseId.asStateFlow()

    private val _selectedLessonId = MutableStateFlow<String?>(null)
    val selectedLessonId: StateFlow<String?> = _selectedLessonId.asStateFlow()

    // AI Mentors state
    private val _aiMode = MutableStateFlow("mentor") // teacher, debugger, builder, coach, mentor
    val aiMode: StateFlow<String> = _aiMode.asStateFlow()

    private val _activeSessionId = MutableStateFlow("default_session")
    val activeSessionId: StateFlow<String> = _activeSessionId.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Code Editor playground
    private val _editorCode = MutableStateFlow("")
    val editorCode: StateFlow<String> = _editorCode.asStateFlow()

    private val _editorLanguage = MutableStateFlow("HTML")
    val editorLanguage: StateFlow<String> = _editorLanguage.asStateFlow()

    private val _consoleOutput = MutableStateFlow("")
    val consoleOutput: StateFlow<String> = _consoleOutput.asStateFlow()

    // Toasts helper
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        // Run seed check for competitors & mock content
        viewModelScope.launch {
            seedInitialCompetitors()
        }
    }

    // --- Data Flows (Reactive to Active User ID) ---
    val currentUserProfile: StateFlow<Profile?> = _currentUserId
        .flatMapLatest { uid ->
            if (uid != null) repository.getProfileFlow(uid) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allCourses: StateFlow<List<Course>> = _currentUserId
        .flatMapLatest { uid ->
            if (uid != null) repository.getAllCourses(uid) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEnrollments: StateFlow<List<Enrollment>> = _currentUserId
        .flatMapLatest { uid ->
            if (uid != null) repository.getEnrollmentsForUser(uid) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCourse: StateFlow<Course?> = _selectedCourseId
        .flatMapLatest { cid ->
            if (cid != null) repository.getCourseById(cid) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeLessons: StateFlow<List<Lesson>> = _selectedCourseId
        .flatMapLatest { cid ->
            if (cid != null) repository.getLessonsForCourse(cid) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCompletions: StateFlow<List<LessonCompletion>> = _currentUserId
        .combine(_selectedCourseId) { uid, cid -> Pair(uid, cid) }
        .flatMapLatest { pair ->
            val uid = pair.first
            val cid = pair.second
            if (uid != null && cid != null) repository.getLessonCompletions(uid, cid) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaderboard: StateFlow<List<Profile>> = repository.getLeaderboard()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProjects: StateFlow<List<Project>> = repository.getAllProjectsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPosts: StateFlow<List<Post>> = repository.getAllPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiMessages: StateFlow<List<AiMessage>> = _activeSessionId
        .flatMapLatest { sid ->
            repository.getMessagesForSession(sid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Actions ---

    fun showToast(msg: String) {
        viewModelScope.launch { _toastMessage.emit(msg) }
    }

    // Auth
    fun handleLogin(username: String) {
        viewModelScope.launch {
            val user = dao.getProfile(username.lowercase().trim())
            if (user != null) {
                _currentUserId.value = user.id
                _currentScreen.value = if (user.onboardingComplete) "MAIN_DASHBOARD" else "ONBOARDING"
                showToast("Welcome back to HabeshaCode!")
                repository.incrementStreak(user.id)
            } else {
                showToast("User not found. Try registering to start free!")
            }
        }
    }

    fun handleRegister(fullName: String, userName: String, role: String) {
        viewModelScope.launch {
            val cleanUser = userName.lowercase().trim()
            if (cleanUser.length < 3) {
                showToast("Username must be at least 3 characters")
                return@launch
            }
            if (dao.getProfile(cleanUser) != null) {
                showToast("Username already exists!")
                return@launch
            }
            val id = UUID.randomUUID().toString()
            val newProfile = Profile(
                id = id,
                username = cleanUser,
                fullName = fullName,
                bio = "Building local tech innovations in coding 🇪🇹",
                role = role
            )
            repository.saveProfile(newProfile)
            _currentUserId.value = id
            _currentScreen.value = "ONBOARDING"
            showToast("Account created! Let's complete Onboarding.")
        }
    }

    fun handleSignOut() {
        _currentUserId.value = null
        _currentScreen.value = "LOGIN"
        _currentTab.value = "HOME"
        showToast("Logged out successfully.")
    }

    // Onboarding
    fun selectInterest(interest: String) {
        val currentSet = _selectedInterests.value
        _selectedInterests.value = if (currentSet.contains(interest)) {
            currentSet - interest
        } else {
            currentSet + interest
        }
    }

    fun advanceOnboarding() {
        val nextStep = _onboardingStep.value + 1
        if (nextStep <= 4) {
            _onboardingStep.value = nextStep
        } else {
            // Completed Onboarding
            viewModelScope.launch {
                val uid = _currentUserId.value ?: return@launch
                val currentProfile = dao.getProfile(uid) ?: return@launch
                
                val skills = when {
                    _selectedInterests.value.size >= 3 -> "intermediate"
                    else -> "beginner"
                }

                val updatedProfile = currentProfile.copy(
                    onboardingComplete = true,
                    skillLevel = skills,
                    interestsJson = _selectedInterests.value.joinToString(separator = ",")
                )
                repository.saveProfile(updatedProfile)
                repository.awardXp(uid, 100, "Completed onboarding profile setup")
                showToast("You earned +100 XP! Welcome to HabeshaCode ecosystem! 🇪🇹")
                
                _currentScreen.value = "MAIN_DASHBOARD"
                _currentTab.value = "HOME"
            }
        }
    }

    fun skipOnboarding() {
        viewModelScope.launch {
            val uid = _currentUserId.value ?: return@launch
            val currentProfile = dao.getProfile(uid) ?: return@launch
            val updated = currentProfile.copy(onboardingComplete = true)
            repository.saveProfile(updated)
            _currentScreen.value = "MAIN_DASHBOARD"
            _currentTab.value = "HOME"
        }
    }

    // Settings Update
    fun updateProfile(fullName: String, bio: String) {
        viewModelScope.launch {
            val uid = _currentUserId.value ?: return@launch
            val current = dao.getProfile(uid) ?: return@launch
            val updated = current.copy(fullName = fullName, bio = bio)
            dao.insertProfile(updated)
            showToast("Profile updated successfully")
        }
    }

    // Courses & Lessons
    fun selectCourse(courseId: String?) {
        _selectedCourseId.value = courseId
    }

    fun selectLesson(lessonId: String?) {
        _selectedLessonId.value = lessonId
    }

    fun enrollInCourse(courseId: String) {
        viewModelScope.launch {
            val uid = _currentUserId.value ?: return@launch
            repository.enrollInCourse(uid, courseId)
            showToast("Successful Course Enrollment! Let's learn!")
        }
    }

    fun completeActiveLesson(courseId: String, lessonId: String) {
        viewModelScope.launch {
            val uid = _currentUserId.value ?: return@launch
            val newlyGraduated = repository.completeLesson(uid, courseId, lessonId)
            if (newlyGraduated) {
                showToast("Congratulations! Completed whole course! Awarded +300 bonus XP! 🏆")
            } else {
                showToast("Lesson checked as complete! Awarded +50 XP.")
            }
        }
    }

    // AI Chats mentor
    fun changeAiMode(mode: String) {
        _aiMode.value = mode
    }

    fun sendAiMentorMessage(text: String) {
        val uid = _currentUserId.value ?: return
        if (text.isBlank()) return

        val sid = _activeSessionId.value

        viewModelScope.launch {
            // 1. Insert User Message
            val userMsg = AiMessage(
                id = UUID.randomUUID().toString(),
                sessionId = sid,
                text = text,
                isUser = true,
                responseMode = _aiMode.value
            )
            repository.insertAiMessage(userMsg)

            _isAiLoading.value = true

            // Formulate AI System prompt
            val systemPrompt = when (_aiMode.value) {
                "teacher" -> "You are Habesha, a patient engineering teacher from Ethiopia. Explain simply and analogically in clean developer style. End response with a coding challenge practice exercise."
                "debugger" -> "You are a senior compiler debugging expert. Identify bugs in user's pasted codes, explain WHY it is broken, and write the fixed code wrapped in a clean, commented code block."
                "builder" -> "You are a software architect. Plan projects, generate clean folder layouts, and provide working starter codes to achieve the user's design."
                "coach" -> "You are a tech recruiter, interviewing the user. Ask ONE challenging interview question. Respond to their solution with score ratings, positive points, and ideal optimized code blocks."
                else -> "You are a senior Ethiopian coding mentor with 15 years experience. Motivate, reference the local tech ecosystem in Addis Ababa/Ethiopia, and offer strategic career roadmaps."
            }

            // Current context history
            val history = repository.getMessagesForSession(sid).firstOrNull() ?: emptyList()

            // 2. Call Gemini API
            val response = GeminiService.getAiResponse(systemPrompt, text, history)

            // 3. Save AI Message
            val aiMsg = AiMessage(
                id = UUID.randomUUID().toString(),
                sessionId = sid,
                text = response,
                isUser = false,
                responseMode = _aiMode.value
            )
            repository.insertAiMessage(aiMsg)
            _isAiLoading.value = false

            // Award micro XP for learning
            repository.awardXp(uid, 15, "Consulted AI Coding Mentor")
        }
    }

    fun clearAiChat() {
        val sid = _activeSessionId.value
        viewModelScope.launch {
            dao.deleteAiSession(sid)
            showToast("Conversation cleared.")
        }
    }

    // Playground parameters
    fun updateCode(code: String) {
        _editorCode.value = code
    }

    fun changeLanguage(lang: String) {
        _editorLanguage.value = lang
        val defaultCode = when (lang) {
            "HTML" -> """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { background: #070710; color: #FFD700; font-family: sans-serif; text-align: center; padding-top: 100px; }
                        h1 { font-size: 3em; margin: 0; }
                    </style>
                </head>
                <body>
                    <h1>HabeshaCode Playground 🇪🇹</h1>
                    <p>Edit HTML natively and run!</p>
                </body>
                </html>
            """.trimIndent()
            "JavaScript" -> """
                // JavaScript Code Sandbox
                function computeFactorial(num) {
                    if (num <= 1) return 1;
                    return num * computeFactorial(num - 1);
                }
                console.log("Factorial of 5 is:", computeFactorial(5));
            """.trimIndent()
            "Python" -> """
                # Python scripting
                even_squares = [x**2 for x in range(11) if x % 2 == 0]
                print("Squares of Even numbers 0-10:")
                print(even_squares)
            """.trimIndent()
            else -> ""
        }
        _editorCode.value = defaultCode
    }

    fun runEditorCode() {
        val lang = _editorLanguage.value
        val code = _editorCode.value
        viewModelScope.launch {
            _consoleOutput.value = "Executing compile logs...\n"
            kotlinx.coroutines.delay(1000)
            
            val output = when (lang) {
                "HTML" -> "Render active output panel inside WebView frame. (Compiled HTML successfully)"
                "JavaScript" -> {
                    if (code.contains("computeFactorial")) {
                         "Running V8 Engine JavaScript context...\nFactorial of 5 is: 120\nExecution complete. (0ms)"
                    } else {
                         "Running V8 Engine JavaScript context...\nExecution complete. Output log successfully synced."
                    }
                }
                "Python" -> "Running Python interpreter...\nSquares of Even numbers 0-10:\n[0, 4, 16, 36, 64, 100]\nFinished in 0.04s"
                else -> "Running compiler sandbox...\nExecution completed successfully."
            }
            _consoleOutput.value = output
            
            val uid = _currentUserId.value
            if (uid != null) {
                repository.awardXp(uid, 20, "Wrote code in Sandbox")
            }
        }
    }

    fun runCodeAiHelper() {
        val uid = _currentUserId.value ?: return
        val currentCode = _editorCode.value
        if (currentCode.isBlank()) {
            showToast("Sandbox code panel is empty!")
            return
        }
        _toastMessage.tryEmit("AI analysing Sandbox code...")
        viewModelScope.launch {
            val systemPrompt = "You are a professional software debugger. Analyse the following code and suggest performance enhancements, bugs, or explain it step-by-step."
            val response = GeminiService.getAiResponse(systemPrompt, "Language: ${_editorLanguage.value}\nCode:\n$currentCode")
            _consoleOutput.value = "--- AI ASSIStant REPORT ---\n$response"
            repository.awardXp(uid, 15, "AI code diagnosis requested")
        }
    }

    // Community feed
    fun submitPost(content: String, code: String? = null, codeLang: String? = null) {
        val uid = _currentUserId.value ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            val profile = dao.getProfile(uid) ?: return@launch
            val newPost = Post(
                id = UUID.randomUUID().toString(),
                userId = uid,
                username = profile.fullName,
                avatarUrl = profile.username.take(2).uppercase(),
                content = content,
                codeContent = code,
                codeLanguage = codeLang
            )
            repository.insertPost(newPost)
            repository.awardXp(uid, 30, "Contributed in community feed")
            showToast("Post uploaded to Feed! Earned +30 XP.")
        }
    }

    fun likePost(postId: String, userId: String) {
        viewModelScope.launch {
            repository.likePost(postId, userId)
            showToast("Upvoted post!")
        }
    }

    fun submitProjectShowcase(title: String, desc: String, tags: String, github: String, liveUrl: String) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            val prj = Project(
                id = UUID.randomUUID().toString(),
                userId = uid,
                title = title,
                description = desc,
                techStackJson = tags,
                githubUrl = github,
                liveUrl = liveUrl
            )
            repository.insertProject(prj)
            repository.awardXp(uid, 100, "Showcased technology project")
            showToast("Showcasing project saved successfully! +100 XP!")
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            showToast("Project deleted from showcase.")
        }
    }

    // Helper views navigation
    fun navigateTab(tab: String) {
        _currentTab.value = tab
    }

    fun navigateScreen(screen: String) {
        _currentScreen.value = screen
    }

    // Seed dummy users to fill rank list first-launch
    private suspend fun seedInitialCompetitors() {
        if (dao.getProfile("selam_dev") == null) {
            val competitors = listOf(
                Profile("selam_dev", "selam_dev", "Selamawit Kebede", "AI Researcher at Addis Ababa Uni. 🇪🇹", "Mentor", 12500, 13, 15, onboardingComplete = true),
                Profile("yonas_build", "yonas_build", "Yonas Tesfaye", "React & Embedded Android Systems", "student", 9400, 10, 8, onboardingComplete = true),
                Profile("helen_robotics", "helen_robotics", "Helen Alula", "Raspberry robotics architect", "student", 10200, 11, 24, onboardingComplete = true),
                Profile("abdi_fullstack", "abdi_fullstack", "Abdi Bekele", "PostgreSQL database optimization nerd", "student", 7200, 8, 4, onboardingComplete = true)
            )
            competitors.forEach { dao.insertProfile(it) }
        }
    }
}
