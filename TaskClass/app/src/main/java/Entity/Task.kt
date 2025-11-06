package Entity

import java.util.Date

class Task {

    private var id: String = ""
    private var title: String = ""
    private var description: String = ""
    private var dueDate: Date? = null
    private var course: Course? = null
    private var status: TaskStatus = TaskStatus.PENDING
    private var images: MutableList<ImageData> = mutableListOf()
    private var reminderScheduled: Boolean = false
    var Delivered: Boolean = false

    constructor()

    constructor(
        id: String,
        title: String,
        description: String,
        dueDate: Date?,
        course: Course?,
        status: TaskStatus,
        images: MutableList<ImageData>,
        reminderScheduled: Boolean
    ) {
        this.id = id
        this.title = title
        this.description = description
        this.dueDate = dueDate
        this.course = course
        this.status = status
        this.images = images
        this.reminderScheduled = reminderScheduled
        
    }

    var Id: String
        get() = this.id
        set(value) { this.id = value }

    var Title: String
        get() = this.title
        set(value) { this.title = value }

    var Description: String
        get() = this.description
        set(value) { this.description = value }

    var DueDate: Date?
        get() = this.dueDate
        set(value) { this.dueDate = value }

    var Course: Course?
        get() = this.course
        set(value) { this.course = value }

    var Status: TaskStatus
        get() = this.status
        set(value) { this.status = value }

    var Images: MutableList<ImageData>
        get() = this.images
        set(value) { this.images = value }

    var ReminderScheduled: Boolean
        get() = this.reminderScheduled
        set(value) { this.reminderScheduled = value }
}
