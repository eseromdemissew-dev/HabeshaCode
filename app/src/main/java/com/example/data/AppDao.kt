package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Profiles
    @Query("SELECT * FROM profiles WHERE id = :userId LIMIT 1")
    fun getProfileFlow(userId: String): Flow<Profile?>

    @Query("SELECT * FROM profiles WHERE id = :userId LIMIT 1")
    suspend fun getProfile(userId: String): Profile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: Profile)

    @Query("SELECT * FROM profiles ORDER BY xp DESC LIMIT 50")
    fun getLeaderboard(): Flow<List<Profile>>

    // Courses
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :courseId LIMIT 1")
    fun getCourseById(courseId: String): Flow<Course?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)

    // Lessons
    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY lessonOrder ASC")
    fun getLessonsForCourse(courseId: String): Flow<List<Lesson>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<Lesson>)

    // Enrollments
    @Query("SELECT * FROM enrollments WHERE userId = :userId")
    fun getEnrollmentsForUser(userId: String): Flow<List<Enrollment>>

    @Query("SELECT * FROM enrollments WHERE userId = :userId AND courseId = :courseId LIMIT 1")
    fun getEnrollmentFlow(userId: String, courseId: String): Flow<Enrollment?>

    @Query("SELECT * FROM enrollments WHERE userId = :userId AND courseId = :courseId LIMIT 1")
    suspend fun getEnrollment(userId: String, courseId: String): Enrollment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: Enrollment)

    // Lesson Completions
    @Query("SELECT * FROM lesson_completions WHERE userId = :userId AND courseId = :courseId")
    fun getLessonCompletions(userId: String, courseId: String): Flow<List<LessonCompletion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessonCompletion(completion: LessonCompletion)

    // Projects
    @Query("SELECT * FROM projects ORDER BY id DESC")
    fun getAllProjectsFlow(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE userId = :userId ORDER BY id DESC")
    fun getProjectsForUser(userId: String): Flow<List<Project>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProject(projectId: String)

    // Community Posts
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: String)

    // AI Messages
    @Query("SELECT * FROM ai_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: String): Flow<List<AiMessage>>

    @Query("SELECT DISTINCT sessionId FROM ai_messages")
    fun getDistinctSessionIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiMessage(message: AiMessage)

    @Query("DELETE FROM ai_messages WHERE sessionId = :sessionId")
    suspend fun deleteAiSession(sessionId: String)
}
