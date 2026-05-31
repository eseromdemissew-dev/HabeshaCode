package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.util.UUID

class AppRepository(private val appDao: AppDao) {

    // --- Profile Operations ---
    fun getProfileFlow(userId: String): Flow<Profile?> = appDao.getProfileFlow(userId)
    
    suspend fun getProfile(userId: String): Profile? = appDao.getProfile(userId)
    
    suspend fun saveProfile(profile: Profile) {
        appDao.insertProfile(profile)
    }

    suspend fun awardXp(userId: String, amount: Int, reason: String): Profile? {
        val currentProfile = appDao.getProfile(userId) ?: return null
        val nextXp = currentProfile.xp + amount
        // Simple linear leveling (every 1000 XP is a level up to max 20)
        val nextLevel = (nextXp / 1000).coerceAtLeast(0) + 1
        val updatedProfile = currentProfile.copy(
            xp = nextXp,
            level = nextLevel.coerceIn(1, 20),
            lastActive = System.currentTimeMillis()
        )
        appDao.insertProfile(updatedProfile)
        return updatedProfile
    }

    suspend fun incrementStreak(userId: String): Profile? {
        val currentProfile = appDao.getProfile(userId) ?: return null
        val updated = currentProfile.copy(
            streak = currentProfile.streak + 1,
            lastActive = System.currentTimeMillis()
        )
        appDao.insertProfile(updated)
        return updated
    }

    fun getLeaderboard(): Flow<List<Profile>> = appDao.getLeaderboard()

    // --- Course & Lesson Operations (with Auto-Seeding) ---
    fun getAllCourses(userId: String): Flow<List<Course>> = flow {
        // Retrieve courses from DB
        appDao.getAllCourses().collect { dbCourses ->
            if (dbCourses.isEmpty()) {
                // Pre-seed core tech items
                val seededCourses = createSeededCourses()
                appDao.insertCourses(seededCourses)
                
                // Seed accompanying lessons
                val seededLessons = createSeededLessons()
                appDao.insertLessons(seededLessons)
                
                emit(seededCourses)
            } else {
                emit(dbCourses)
            }
        }
    }

    fun getCourseById(courseId: String): Flow<Course?> = appDao.getCourseById(courseId)

    fun getLessonsForCourse(courseId: String): Flow<List<Lesson>> = appDao.getLessonsForCourse(courseId)

    // --- Enrollments & Completions ---
    fun getEnrollmentsForUser(userId: String): Flow<List<Enrollment>> = appDao.getEnrollmentsForUser(userId)
    
    fun getEnrollmentFlow(userId: String, courseId: String): Flow<Enrollment?> = appDao.getEnrollmentFlow(userId, courseId)
    
    suspend fun enrollInCourse(userId: String, courseId: String) {
        val enrollmentId = "${userId}_${courseId}"
        val enrollment = Enrollment(
            id = enrollmentId,
            userId = userId,
            courseId = courseId,
            progress = 0.0,
            completed = false
        )
        appDao.insertEnrollment(enrollment)
        
        // Award XP for expanding the scope
        awardXp(userId, 150, "Enrolled in course")
    }

    fun getLessonCompletions(userId: String, courseId: String): Flow<List<LessonCompletion>> =
        appDao.getLessonCompletions(userId, courseId)

    suspend fun completeLesson(userId: String, courseId: String, lessonId: String): Boolean {
        val id = "${userId}_${lessonId}"
        val completion = LessonCompletion(
            id = id,
            userId = userId,
            courseId = courseId,
            lessonId = lessonId
        )
        appDao.insertLessonCompletion(completion)

        // Recompute course progress
        val allLessons = appDao.getLessonsForCourse(courseId).firstOrNull() ?: emptyList()
        val allCompleted = appDao.getLessonCompletions(userId, courseId).firstOrNull() ?: emptyList()
        
        val totalCount = allLessons.size.coerceAtLeast(1)
        val completedCount = allCompleted.size
        val nextProgress = (completedCount.toDouble() / totalCount.toDouble()).coerceIn(0.0, 1.0)
        val isCompletedNow = completedCount == totalCount

        val currentEnrollment = appDao.getEnrollment(userId, courseId)
        if (currentEnrollment != null) {
            val updatedEnrollment = currentEnrollment.copy(
                progress = nextProgress,
                completed = isCompletedNow
            )
            appDao.insertEnrollment(updatedEnrollment)
        }

        // Award dynamic lesson completion XP
        awardXp(userId, 50, "Completed Lesson")
        
        if (isCompletedNow && currentEnrollment?.completed == false) {
            // Newly graduated! Give bonus XP
            awardXp(userId, 300, "Graduated Course!")
            return true // Trigger certificate animation / completion block
        }
        return false
    }

    // --- Project Operations ---
    fun getAllProjectsFlow(): Flow<List<Project>> = appDao.getAllProjectsFlow()
    fun getProjectsForUser(userId: String): Flow<List<Project>> = appDao.getProjectsForUser(userId)
    
    suspend fun insertProject(project: Project) {
        appDao.insertProject(project)
    }
    
    suspend fun deleteProject(projectId: String) {
        appDao.deleteProject(projectId)
    }

    // --- Community Feed ---
    fun getAllPosts(): Flow<List<Post>> = appDao.getAllPosts()
    
    suspend fun insertPost(post: Post) {
        appDao.insertPost(post)
    }

    suspend fun likePost(postId: String, updaterUserId: String) {
        // Toggles likes locally for the static demo feel
        // In fully native we fetch the posts, find mapping, toggle, save back
        appDao.getAllPosts().firstOrNull()?.find { it.id == postId }?.let { post ->
            val nextLiked = !post.isLiked
            val nextLikesCount = if (nextLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
            appDao.insertPost(post.copy(isLiked = nextLiked, likesCount = nextLikesCount))
        }
    }

    // --- AI Session Interactions ---
    fun getMessagesForSession(sessionId: String): Flow<List<AiMessage>> = appDao.getMessagesForSession(sessionId)
    fun getDistinctSessions(): Flow<List<String>> = appDao.getDistinctSessionIds()
    
    suspend fun insertAiMessage(message: AiMessage) {
        appDao.insertAiMessage(message)
    }

    // --- Creators helper ---
    private fun createSeededCourses(): List<Course> {
        return listOf(
            Course(
                id = "html_css_mastery",
                title = "HTML & CSS Mastery",
                slug = "html-css-mastery",
                shortDescription = "Master the core visual rendering blocks of the web from scratch.",
                description = "Learn HTML5 and CSS3 semantic structuring, CSS custom variables, gradients, flexbox alignment, and fully fluid CSS standard grids for responsive modern Android and web screens.",
                category = "html",
                level = "beginner",
                xpReward = 300,
                totalLessons = 3,
                isFeatured = true,
                tagsJson = "[\"html\", \"css\", \"web\", \"beginner\"]",
                outcomesJson = "[\"Write semantic markups\", \"Understand responsive containers\", \"Apply custom ambient card themes\"]"
            ),
            Course(
                id = "javascript_complete",
                title = "JavaScript Complete Course",
                slug = "javascript-complete",
                shortDescription = "From variables and closure scopes to asynchronous api parsing.",
                description = "Master JavaScript from fundamental types to deep closure binding, asynchronous Promises, REST networking arrays, and using local offline state engines.",
                category = "javascript",
                level = "intermediate",
                xpReward = 600,
                totalLessons = 3,
                isFeatured = true,
                tagsJson = "[\"javascript\", \"js\", \"frontend\", \"programming\"]",
                outcomesJson = "[\"Master javascript closures\", \"Construct async network promises\", \"Interact with local engines\"]"
            ),
            Course(
                id = "python_ai_engineering",
                title = "Python for AI Engineering",
                slug = "python-ai-engineering",
                shortDescription = "Harness Python arrays & invoke Google Gemini model APIs.",
                description = "Deep dive into clean Python programming, structured dictionaries, array utilities, prompt framing parameters, and invoking Gemini API endpoints for smart assistants.",
                category = "python",
                level = "intermediate",
                xpReward = 750,
                totalLessons = 3,
                isFeatured = true,
                tagsJson = "[\"python\", \"ai\", \"ml\", \"data-science\"]",
                outcomesJson = "[\"Form elegant list comprehensions\", \"Structure intelligent system prompts\", \"Integrate LLMs natively\"]"
            ),
            Course(
                id = "arduino_robotics",
                title = "Arduino & Robotics Builder",
                slug = "arduino-robotics",
                shortDescription = "Physical computing, Ultrasonic radar sensors, and physical servos.",
                description = "Learn digital pinouts, analog voltage thresholds, physical computing boards, connecting digital buzzers, radar scanning loops, and driving robot arms.",
                category = "robotics",
                level = "beginner",
                xpReward = 500,
                totalLessons = 3,
                isFeatured = false,
                tagsJson = "[\"robotics\", \"arduino\", \"hardware\", \"iot\"]",
                outcomesJson = "[\"Conduct digital microcontrollers setup\", \"Wire photocell analogue signals\", \"Co-ordinate rotational server drives\"]"
            )
        )
    }

    private fun createSeededLessons(): List<Lesson> {
        return listOf(
            // HTML & CSS Lessons
            Lesson(
                id = "html_l1",
                courseId = "html_css_mastery",
                title = "Introduction to Semantic HTML5",
                content = """
                    ### Semantic HTML Structures
                    
                    Semantic elements clearly describe their meaning in a human- and machine-readable way. 
                    Rather than grouping everything in un-styled standard widgets like basic `<divs>`, use containers that outline document hierarchies:
                    
                    - `<header>`: Visual elements displayed at the top of the viewport.
                    - `<nav>`: Menu items that outline user routing options.
                    - `<article>`: Self-contained blocks of text (like independent coding blogs).
                    - `<footer>`: Closing copyright alignments at the base of the page.
                    
                    ```html
                    <article>
                        <header>
                            <h2>Declaring Semantic Elements</h2>
                        </header>
                        <p>This paragraph is contained inside a semantic article tag.</p>
                    </article>
                    ```
                    
                    Using these semantic elements makes web applications highly responsive to SEO indexing crawlers and assistive accessibility screen readers, representing standard developer best practices.
                """.trimIndent(),
                durationMinutes = 12,
                lessonOrder = 1,
                isFree = true
            ),
            Lesson(
                id = "html_l2",
                courseId = "html_css_mastery",
                title = "Modern CSS Variables & Ambient Gradients",
                content = """
                    ### Advanced Color Schemes & Custom Properties
                    
                    CSS Variables let you store theme parameters in a centralized parent block and apply them throughout components:
                    
                    ```css
                    :root {
                        --hb-gold: #D4AF37;
                        --hb-black: #070710;
                        --hb-ambient: linear-gradient(135deg, #070710, #D4AF37);
                    }
                    
                    .glass-card {
                        background: var(--hb-black);
                        border: 1px solid var(--hb-gold);
                    }
                    ```
                    
                    Using gradients and variables ensures that modifying parameters in one specific root variable scales color consistency globally. Always align variables carefully with the background parameters.
                """.trimIndent(),
                durationMinutes = 15,
                lessonOrder = 2,
                isFree = true
            ),
            Lesson(
                id = "html_l3",
                courseId = "html_css_mastery",
                title = "Building Fluid Flexbox & Grid Layouts",
                content = """
                    ### Dynamic Container Layouts
                    
                    Understand fluid styling with Flexbox and standard CSS grids to build layouts that look amazing on compact hand-held mobile devices as well as expanded widescreen monitors.
                    
                    ```css
                    .grid-container {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                        gap: 20px;
                    }
                    ```
                    
                    Always avoid fixed pixel dimensions. Rely instead on relative units (`%`, `vw`, `vh`, `fr`) to support DeX monitors and varying viewport setups automatically.
                """.trimIndent(),
                durationMinutes = 20,
                lessonOrder = 3,
                isFree = false
            ),
            
            // JavaScript Lessons
            Lesson(
                id = "js_l1",
                courseId = "javascript_complete",
                title = "Variable Scope & Closure Masterclass",
                content = """
                    ### JavaScript Execution Scopes
                    
                    JavaScript scope determines the accessibility of user variables in lexical environments.
                    
                    - **Block Scope**: Declared using modern ES6 `let` or `const` keyword. Available only inside the closest curly brackets.
                    - **Function Scope**: Available inside the declaring function body.
                    - **Closure**: A closure represents a function bundled together with references to its surrounding state. It lets an inner function access outer scope variables even after execution finishes.
                    
                    ```javascript
                    function createCounter() {
                        let count = 0; // Lexical scope variable
                        return function() {
                            count++;
                            return count; // Closure access
                        };
                    }
                    const counter = createCounter();
                    console.log(counter()); // Output: 1
                    console.log(counter()); // Output: 2
                    ```
                """.trimIndent(),
                durationMinutes = 15,
                lessonOrder = 1,
                isFree = true
            ),
            Lesson(
                id = "js_l2",
                courseId = "javascript_complete",
                title = "Promises, Resolves & Async-Await Loops",
                content = """
                    ### Master Asynchronous JS Loops
                    
                    A Promise handles values that will be resolved or rejected asynchronously in the future.
                    
                    Using clean ES8 `async / await` syntax simplifies asynchronous code, making it read sequentially and preventing nested Callback Hell:
                    
                    ```javascript
                    async function fetchHabeshaStudent(email) {
                        try {
                            const response = await fetch("https://api.habeshacode.com/students/" + email);
                            if (!response.ok) throw new Error("Connection failed");
                            const profile = await response.json();
                            return profile;
                        } catch (err) {
                            console.error("Failed fetching:", err);
                        }
                    }
                    ```
                """.trimIndent(),
                durationMinutes = 18,
                lessonOrder = 2,
                isFree = true
            ),
            Lesson(
                id = "js_l3",
                courseId = "javascript_complete",
                title = "LocalStorage & Device State Engines",
                content = """
                    ### Caching State On Device
                    
                    Save small user datasets natively using local storage to maintain responsive offline experiences:
                    
                    ```javascript
                    // Saving to local memory client-side
                    localStorage.setItem("user_streak", "15");
                    
                    // Retrieving the value later
                    const streak = localStorage.getItem("user_streak");
                    console.log("Current Streak:", streak); // Output: 15
                    ```
                    
                    Ensure values saved represent serialized strings (using `JSON.stringify` for objects) to avoid structural data loss.
                """.trimIndent(),
                durationMinutes = 22,
                lessonOrder = 3,
                isFree = false
            ),

            // Python Lessons
            Lesson(
                id = "py_l1",
                courseId = "python_ai_engineering",
                title = "Python Iterations & List Comprehensions",
                content = """
                    ### Fast Comprehensions and Iterations
                    
                    Python is incredibly readable and fast for looping operations. List comprehensions offer a concise syntax to create lists based on existing lists:
                    
                    ```python
                    # Generate a list of squares for even indexes
                    even_squares = [x**2 for x in range(10) if x % 2 == 0]
                    print(even_squares) # Output: [0, 4, 16, 36, 64]
                    ```
                    
                    Use dictionary comprehensions to build key-value pairs instantly, which is highly useful when parsing JSON responses from AI models.
                """.trimIndent(),
                durationMinutes = 14,
                lessonOrder = 1,
                isFree = true
            ),
            Lesson(
                id = "py_l2",
                courseId = "python_ai_engineering",
                title = "Prompt Engineering & Few-Shot Templates",
                content = """
                    ### Designing High-Quality AI Input Prompts
                    
                    Prompt engineering involves curating standard structured queries to elicit highly predictable behavior from large models like Gemini:
                    
                    - **System Prompt**: Set the core operational rules of the AI (e.g. "You are an Ethiopian robotics teacher who explains simply").
                    - **Few-Shot Injecting**: Provide concrete input-output examples directly in the prompt context to guide token formatting behavior.
                    
                    ```text
                    Review the following bug in Python. 
                    Input: def multiply(a,b) return a*b
                    Ideal Output: Syntax Error (Missing colon at end of function header: def multiply(a,b):)
                    ```
                """.trimIndent(),
                durationMinutes = 16,
                lessonOrder = 2,
                isFree = true
            ),
            Lesson(
                id = "py_l3",
                courseId = "python_ai_engineering",
                title = "Invoking Google Gemini AI APIs",
                content = """
                    ### Constructing an AI Request
                    
                    Use Python scripts to connect directly to Google's REST model endpoints and stream responses:
                    
                    ```python
                    import google.generativeai as genai
                    
                    genai.configure(api_key="YOUR_GEMINI_API_KEY")
                    model = genai.GenerativeModel("gemini-3.5-flash")
                    
                    response = model.generate_content("Explain closures in JavaScript Simply.")
                    print(response.text)
                    ```
                """.trimIndent(),
                durationMinutes = 25,
                lessonOrder = 3,
                isFree = false
            ),

            // Robotics Lessons
            Lesson(
                id = "robot_l1",
                courseId = "arduino_robotics",
                title = "C++ Logic, Digital Outputs & Microcontrollers Setup",
                content = """
                    ### Standard Hardware Arduino Loops
                    
                    Robotics interfaces compile embedded C++ loops. Every board setup consists of two primary runtime execution functions:
                    
                    - `setup()`: Runs once upon board initialization to declare signal pins.
                    - `loop()`: Runs continuously to control rotational components or measure input thresholds.
                    
                    ```cpp
                    void setup() {
                        pinMode(13, OUTPUT); // Configure digital pin 13 as output
                    }
                    
                    void loop() {
                        digitalWrite(13, HIGH); // Send voltage output
                        delay(1000);            // Pause 1 second
                        digitalWrite(13, LOW);  // Cut voltage output
                        delay(1000);
                    }
                    ```
                """.trimIndent(),
                durationMinutes = 20,
                lessonOrder = 1,
                isFree = true
            ),
            Lesson(
                id = "robot_l2",
                courseId = "arduino_robotics",
                title = "Wiring Photocell Analog Sensors",
                content = """
                    ### Reading Analog Inputs
                    
                    Microcontrollers read varyingly mapped analogue voltages (usually 0 to 5 Volts mapped from 0 to 1023 integers) from real-world physical light-sensitive cells:
                    
                    ```cpp
                    int sensorPin = A0; // Photocell linked to A0
                    int value = 0;
                    
                    void setup() {
                        Serial.begin(9600); // Open connection channel
                    }
                    
                    void loop() {
                        value = analogRead(sensorPin); // Acquire real-world integer
                        Serial.println(value);
                        delay(500);
                    }
                    ```
                """.trimIndent(),
                durationMinutes = 20,
                lessonOrder = 2,
                isFree = true
            ),
            Lesson(
                id = "robot_l3",
                courseId = "arduino_robotics",
                title = "Interfacing Rotational Continuous Servos",
                content = """
                    ### Actuating Mechanical Gears
                    
                    To rotate joints of robotic grabbers or wheels, hardware setups leverage analog motor controllers (Servos) activated via standard signal pulses:
                    
                    ```cpp
                    #include <Servo.h>
                    
                    Servo gripperServo;
                    
                    void setup() {
                        gripperServo.attach(9); // Servo output signal at digital pin 9
                    }
                    
                    void loop() {
                        gripperServo.write(90); // Turn to center 90 degrees
                        delay(1000);
                        gripperServo.write(180); // Move to 180 degrees
                        delay(1000);
                    }
                    ```
                """.trimIndent(),
                durationMinutes = 28,
                lessonOrder = 3,
                isFree = false
            )
        )
    }
}
