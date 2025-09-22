import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { BrowserRouter } from "react-router-dom";
import TaskDetail from "./TaskDetail";

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => mockNavigate,
}));

beforeEach(() => {
    mockNavigate.mockClear();
});

test("Renders TaskDetail component", () => {
    render(
        <BrowserRouter>
            <TaskDetail />
        </BrowserRouter>
    );
    expect(screen.getByRole("main", { name: /task details/i })).toBeInTheDocument();
    expect(screen.getByText(/task details/i)).toBeInTheDocument();
});

test("Shows spinner when loading", () => {
    render(
        <BrowserRouter>
            <TaskDetail />
        </BrowserRouter>
    );
    expect(screen.getByLabelText(/loading/i)).toBeInTheDocument();
});

test("Spinner disappears after loading", async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn(() =>
        Promise.resolve({
            ok: true,
            json: () => Promise.resolve({
                id: 1,
                title: 'Test Task',
                description: 'This is a test task',
                status: 'PENDING',
                priority: 'HIGH',
                category: 'WORK',
                dueDate: '2023-12-31'
            })
        })
    );
    render(
        <BrowserRouter>
            <TaskDetail />
        </BrowserRouter>
    );
    expect(screen.getByLabelText(/loading/i)).toBeInTheDocument();
    jest.runAllTimers();
    await waitFor(() => {
        expect(screen.queryByLabelText(/loading/i)).not.toBeInTheDocument();
    });
    jest.useRealTimers();
});

test("Displays task details after successful fetch", async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn(() =>
        Promise.resolve({
            ok: true,
            json: () => Promise.resolve({
                id: 1,
                title: 'Test Task',
                description: 'This is a test task',
                status: 'PENDING',
                priority: 'HIGH',
                category: 'WORK',
                dueDate: '2023-12-31'
            })
        })
    );
    render(
        <BrowserRouter>
            <TaskDetail />
        </BrowserRouter>
    );
    jest.runAllTimers();
    await waitFor(() => {
        expect(screen.getAllByText(/test task/i).length).toBeGreaterThan(0);
        expect(screen.getByText(/this is a test task/i)).toBeInTheDocument();
        expect(screen.getByText(/status:/i)).toBeInTheDocument();
        expect(screen.getByText(/priority:/i)).toBeInTheDocument();
        expect(screen.getByText(/due date:/i)).toBeInTheDocument();
        expect(screen.getByText(/category:/i)).toBeInTheDocument();
    });
    jest.useRealTimers();
});

test("Handles fetch error gracefully", async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn(() =>
        Promise.resolve({
            ok: false
        })
    );
    render(
        <BrowserRouter>
            <TaskDetail />
        </BrowserRouter>
    );
    jest.runAllTimers();
    await waitFor(() => {
        const errorDiv = screen.getByText(/failed to fetch task/i).closest('div');
        expect(errorDiv).toHaveAttribute('aria-live', 'assertive');
        expect(screen.getByText(/failed to fetch task/i)).toBeInTheDocument();
    });
    jest.useRealTimers();
});

test("Handles missing task by redirecting to 404", async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn(() =>
        Promise.resolve({
            ok: false,
            status: 404
        })
    );
    render(
        <BrowserRouter>
            <TaskDetail />
        </BrowserRouter>
    );
    jest.runAllTimers();
    await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/404');
    });
    jest.useRealTimers();
});

test("Edit button is focusable and redirects to task edit page", async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn(() =>
        Promise.resolve({
            ok: true,
            json: () => Promise.resolve({
                id: 1,
                title: 'Test Task',
                description: 'This is a test task',
                status: 'PENDING',
                priority: 'HIGH',
                category: 'WORK',
                dueDate: '2023-12-31'
            })
        })
    );
    render(
        <BrowserRouter>
            <TaskDetail />
        </BrowserRouter>
    );
    jest.runAllTimers();
    await waitFor(() => {
        userEvent.tab();
        const editButton = screen.getByRole('button', { name: /edit task/i });
        expect(editButton).toHaveFocus();
        userEvent.click(editButton);
        expect(mockNavigate).toHaveBeenCalledWith('/tasks/edit/1');
    });
    jest.useRealTimers();
});

test("Delete button is focusable and shows confirmation dialog", async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn(() =>
        Promise.resolve({
            ok: true,
            json: () => Promise.resolve({
                id: 1,
                title: 'Test Task',
                description: 'This is a test task',
                status: 'PENDING',
                priority: 'HIGH',
                category: 'WORK',
                dueDate: '2023-12-31'
            })
        })
    );
    window.confirm = jest.fn(() => false);
    render(
        <BrowserRouter>
            <TaskDetail />
        </BrowserRouter>
    );
    jest.runAllTimers();
    await waitFor(() => {
        // Simulate tabbing to the delete button
        userEvent.tab(); // Focus first tabbable element
        userEvent.tab(); // Focus next (Edit Task)
        userEvent.tab(); // Focus next (Delete Task)
        const deleteButton = screen.getByRole('button', { name: /delete task/i });
        expect(deleteButton).toHaveFocus();
        userEvent.click(deleteButton);
        expect(window.confirm).toHaveBeenCalledWith('Are you sure you want to delete this task?');
        expect(mockNavigate).not.toHaveBeenCalled();
    });
    jest.useRealTimers();
});