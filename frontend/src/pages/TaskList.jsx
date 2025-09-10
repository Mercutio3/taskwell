import Navbar from '../components/Navbar'

function TaskList() {
    return (
        <>
            <Navbar />
            <div className="tasklist-container">
                <h1>Task List</h1>
                <p>Welcome to your task list! Here you can see all your tasks and manage them effectively.</p>
                {/* Placeholder for future task list and filters */}
                <div className="tasklist-filters">[Task Filters]</div>
                <div className="tasklist-items">[Task Items List]</div>
            </div>
        </>
    )
}

export default TaskList