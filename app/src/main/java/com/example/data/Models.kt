package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val bio: String,
    val role: String, // student, instructor, admin
    val xp: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val lastActive: Long = System.currentTimeMillis(),
    val onboardingComplete: Boolean = false,
    val skillLevel: String = "beginner",
    val interestsJson: String = "[]" // JSON array of selected interests
)

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey val id: String,
    val title: String,
    val slug: String,
    val shortDescription: String,
    val description: String,
    val category: String, // html, javascript, react, python, robotics
    val level: String, // beginner, intermediate, advanced, expert
    val xpReward: Int = 500,
    val totalLessons: Int = 0,
    val isFeatured: Boolean = false,
    val tagsJson: String = "[]",
    val outcomesJson: String = "[]"
)

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey val id: String,
    val courseId: String,
    val title: String,
    val content: String, // markdown content
    val durationMinutes: Int = 10,
    val lessonOrder: Int = 1,
    val isFree: Boolean = false
)

@Entity(tableName = "enrollments")
data class Enrollment(
    @PrimaryKey val id: String, // userId_courseId
    val userId: String,
    val courseId: String,
    val progress: Double = 0.0,
    val completed: Boolean = false
)

@Entity(tableName = "lesson_completions")
data class LessonCompletion(
    @PrimaryKey val id: String, // userId_lessonId
    val userId: String,
    val courseId: String,
    val lessonId: String,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val techStackJson: String = "[]",
    val githubUrl: String = "",
    val liveUrl: String = "",
    val likesCount: Int = 0,
    val viewsCount: Int = 0,
    val isPublic: Boolean = true
)

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val id: String,
    val userId: String,
    val username: String,
    val avatarUrl: String,
    val content: String,
    val codeContent: String? = null,
    val codeLanguage: String? = null,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_messages")
data class AiMessage(
    @PrimaryKey val id: String,
    val sessionId: String, // unique chat thread ID
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val responseMode: String = "mentor" // teacher, debugger, builder, coach, mentor
)
