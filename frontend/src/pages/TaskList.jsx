import Navbar from '../components/Navbar'
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'

function TaskList() {
    const [tasks, setTasks] = useState([])
    const [loading, setLoading] = useState(true)
    const [search, setSearch] = useState('')
    // Placeholder for filters
    const [statusFilter, setStatusFilter] = useState('')
    const [priorityFilter, setPriorityFilter] = useState('')

    useEffect(() => {
        async function fetchTasks() {
            try {
                const userRes = await fetch('http://localhost:8080/api/users/me', {
                    credentials: 'include',
                })
                if (!userRes.ok) throw new Error('Failed to fetch user info')
                const user = await userRes.json()
                            
                const res = await fetch(`http://localhost:8080/api/tasks/user/${user.id}`, {
                    credentials: 'include',
                })
                if (!res.ok) throw new Error('Failed to fetch tasks')
                const data = await res.json()
                setTasks(data)
            } catch (err) {
                setTasks([])
            } finally {
                setLoading(false)
            }
        }
        fetchTasks()
    }, [])

    // Filtered tasks (search + filters)
    const filteredTasks = tasks.filter(task => {
        const matchesSearch = task.title.toLowerCase().includes(search.toLowerCase())
        const matchesStatus = statusFilter ? task.status === statusFilter : true
        const matchesPriority = priorityFilter ? task.priority === priorityFilter : true
        return matchesSearch && matchesStatus && matchesPriority
    })

    return (
        <>
            <Navbar />
            <div className="tasklist-container">
                <h1>Task List</h1>
                <p>Welcome to your task list! Here you can see all your tasks and manage them effectively.</p>
                {/* Search bar */}
                <input
                    type="text"
                    placeholder="Search tasks..."
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                    className="tasklist-search"
                />
                {/* Filters (simple dropdowns for now) */}
                <div className="tasklist-filters">
                    <select value={statusFilter} onChange={e => setStatusFilter(e.target.value)}>
                        <option value="">All Statuses</option>
                        <option value="PENDING">Pending</option>
                        <option value="COMPLETED">Completed</option>
                    </select>
                    <select value={priorityFilter} onChange={e => setPriorityFilter(e.target.value)}>
                        <option value="">All Priorities</option>
                        <option value="LOW">Low</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HIGH">High</option>
                    </select>
                </div>
                <div className="tasklist-items">
                    {loading ? (
                        <div>Loading...</div>
                    ) : filteredTasks.length === 0 ? (
                        <div>You have no tasks. <Link to="/tasks/new">Create task</Link></div>
                    ) : (
                        <ul>
                            {filteredTasks.map(task => (
                                <li key={task.id}>
                                    <Link to={`/tasks/${task.id}`}>{task.title}</Link>
                                    {task.status && <span> [{task.status}]</span>}
                                    {task.priority && <span> ({task.priority})</span>}
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>
        </>
    )
}

export default TaskList