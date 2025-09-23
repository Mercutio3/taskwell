import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import TaskEditPage from "./TaskEditPage";
import { AuthProvider } from "../context/AuthContext";

jest.mock("../services/api", () => ({
  getTask: jest.fn().mockResolvedValue({
    id: 1,
    title: "Test Task",
    description: "This is a test task",
    dueDate: "2025-12-31",
    status: "PENDING",
    priority: "MEDIUM",
    category: "WORK",
  }),
  updateTask: jest.fn().mockResolvedValue({}),
}));

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

beforeEach(() => {
  mockNavigate.mockClear();
});

test("Renders TaskEditPage component with correct headings", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <MemoryRouter initialEntries={["/tasks/1/edit"]}>
        <Routes>
          <Route path="/tasks/:id/edit" element={<TaskEditPage />} />
        </Routes>
      </MemoryRouter>
    </AuthProvider>,
  );
  expect(
    await screen.findByRole("heading", { name: /edit task/i }),
  ).toBeInTheDocument();
  expect(await screen.findByPlaceholderText(/task title/i)).toBeInTheDocument();
  expect(
    await screen.findByPlaceholderText(/task description/i),
  ).toBeInTheDocument();
  expect(await screen.findByPlaceholderText(/due date/i)).toBeInTheDocument();
  expect(
    await screen.findByRole("button", { name: /update task/i }),
  ).toBeInTheDocument();
});

test("Handles successful task update", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <MemoryRouter initialEntries={["/tasks/1/edit"]}>
        <Routes>
          <Route path="/tasks/:id/edit" element={<TaskEditPage skipDelay />} />
        </Routes>
      </MemoryRouter>
    </AuthProvider>,
  );

  await screen.findByDisplayValue("Test Task");

  await userEvent.clear(screen.getByPlaceholderText(/task title/i));
  await userEvent.type(
    screen.getByPlaceholderText(/task title/i),
    "Updated Task",
  );
  await userEvent.clear(screen.getByPlaceholderText(/task description/i));
  await userEvent.type(
    screen.getByPlaceholderText(/task description/i),
    "Updated Description",
  );
  await userEvent.clear(screen.getByPlaceholderText(/due date/i));
  await userEvent.type(screen.getByPlaceholderText(/due date/i), "2026-11-30");
  await userEvent.click(screen.getByRole("button", { name: /update task/i }));

  await waitFor(() => {
    expect(mockNavigate).toHaveBeenCalledWith("/tasks/1");
  });
});

test("Handles task editing error for unverified user", async () => {
  const { updateTask } = require("../services/api");
  updateTask.mockRejectedValue({ status: 403 });

  render(
    <AuthProvider value={{ isVerified: false }}>
      <MemoryRouter initialEntries={["/tasks/1/edit"]}>
        <Routes>
          <Route path="/tasks/:id/edit" element={<TaskEditPage skipDelay />} />
        </Routes>
      </MemoryRouter>
    </AuthProvider>,
  );

  await screen.findByDisplayValue("Test Task");

  await userEvent.clear(screen.getByPlaceholderText(/task title/i));
  await userEvent.type(
    screen.getByPlaceholderText(/task title/i),
    "Updated Task",
  );
  await userEvent.click(screen.getByRole("button", { name: /update task/i }));

  const errors = await screen.findAllByText(/failed to update task/i);
  expect(errors.length).toBeGreaterThan(0);
  errors.forEach((e) => expect(e).toBeVisible());
});

test("Handles due date in the past", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <MemoryRouter initialEntries={["/tasks/1/edit"]}>
        <Routes>
          <Route path="/tasks/:id/edit" element={<TaskEditPage skipDelay />} />
        </Routes>
      </MemoryRouter>
    </AuthProvider>,
  );

  await screen.findByDisplayValue("Test Task");

  await userEvent.clear(screen.getByPlaceholderText(/due date/i));
  const pastDate = new Date();
  pastDate.setDate(pastDate.getDate() - 1);
  const pastDateString = pastDate.toISOString().split("T")[0];
  await userEvent.type(
    screen.getByPlaceholderText(/due date/i),
    pastDateString,
  );
  await userEvent.click(screen.getByRole("button", { name: /update task/i }));

  await waitFor(() => {
    expect(
      screen.getByText((content) =>
        content.includes("Due date cannot be in the past"),
      ),
    ).toBeInTheDocument();
  });
});

test("Handles empty title field", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <MemoryRouter initialEntries={["/tasks/1/edit"]}>
        <Routes>
          <Route path="/tasks/:id/edit" element={<TaskEditPage skipDelay />} />
        </Routes>
      </MemoryRouter>
    </AuthProvider>,
  );

  await screen.findByDisplayValue("Test Task");

  await userEvent.clear(screen.getByPlaceholderText(/task title/i));
  await userEvent.click(screen.getByRole("button", { name: /update task/i }));

  await waitFor(() => {
    expect(
      screen.getByText((content) => content.includes("Title is required")),
    ).toBeInTheDocument();
  });
});

test("All form fields are focusable in order", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <MemoryRouter initialEntries={["/tasks/1/edit"]}>
        <Routes>
          <Route path="/tasks/:id/edit" element={<TaskEditPage skipDelay />} />
        </Routes>
      </MemoryRouter>
    </AuthProvider>,
  );

  await screen.findByDisplayValue("Test Task");

  const titleInput = screen.getByPlaceholderText(/task title/i);
  const descriptionInput = screen.getByPlaceholderText(/task description/i);
  const dueDateInput = screen.getByPlaceholderText(/due date/i);
  const statusSelect = screen.getByLabelText(/status/i);
  const prioritySelect = screen.getByLabelText(/priority/i);
  const categorySelect = screen.getByLabelText(/category/i);
  const submitButton = screen.getByRole("button", { name: /update task/i });

  await userEvent.tab();
  await userEvent.tab();
  await userEvent.tab();
  await userEvent.tab();
  await userEvent.tab();
  await userEvent.tab();
  expect(titleInput).toHaveFocus();

  await userEvent.tab();
  expect(descriptionInput).toHaveFocus();

  await userEvent.tab();
  expect(dueDateInput).toHaveFocus();

  await userEvent.tab();
  expect(statusSelect).toHaveFocus();

  await userEvent.tab();
  expect(prioritySelect).toHaveFocus();

  await userEvent.tab();
  expect(categorySelect).toHaveFocus();

  await userEvent.tab();
  expect(submitButton).toHaveFocus();
});
