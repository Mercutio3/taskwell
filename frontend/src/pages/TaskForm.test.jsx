import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { BrowserRouter } from "react-router-dom";
import TaskForm from "./TaskForm";

const initialTask = {
  id: 1,
  title: "Default Task",
  description: "Default description",
  status: "PENDING",
  priority: "HIGH",
  category: "WORK",
  dueDate: "2023-12-31",
};

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

beforeEach(() => {
  mockNavigate.mockClear();
});

global.fetch = jest.fn((url) => {
  if (url.includes("/categories")) {
    return Promise.resolve({
      ok: true,
      json: () => Promise.resolve(["WORK"]),
    });
  }
  return Promise.resolve({
    ok: true,
    json: () => Promise.resolve(initialTask),
  });
});

test("Renders TaskForm component for new task", () => {
  render(
    <BrowserRouter>
      <TaskForm />
    </BrowserRouter>,
  );
  expect(
    screen.getByRole("heading", { name: /new task/i }),
  ).toBeInTheDocument();
  expect(screen.getByText(/welcome to your task form/i)).toBeInTheDocument();
  expect(screen.getByPlaceholderText(/task title/i)).toBeInTheDocument();
  expect(screen.getByPlaceholderText(/task description/i)).toBeInTheDocument();
  expect(screen.getByPlaceholderText(/due date/i)).toBeInTheDocument();
  expect(
    screen.getByRole("button", { name: /create task/i }),
  ).toBeInTheDocument();
});

test("Renders TaskForm component with initialTask prop", async () => {
  const initialTask = {
    id: 1,
    title: "Test Task",
    description: "This is a test task",
    status: "PENDING",
    priority: "HIGH",
    category: "WORK",
    dueDate: "2023-12-31",
  };
  render(
    <BrowserRouter>
      <TaskForm initialTask={initialTask} />
    </BrowserRouter>,
  );
  await waitFor(() => {
    expect(
      screen.getByRole("heading", { name: /edit task/i }),
    ).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/task title/i)).toHaveValue("Test Task");
    expect(screen.getByPlaceholderText(/task description/i)).toHaveValue(
      "This is a test task",
    );
    expect(screen.getByPlaceholderText(/due date/i)).toHaveValue("2023-12-31");
    expect(screen.getByDisplayValue("Pending")).toBeInTheDocument();
    expect(screen.getByDisplayValue("High")).toBeInTheDocument();
    expect(screen.getByDisplayValue("Work")).toBeInTheDocument();
  });
});

test("Form validation shows error when required fields are missing", async () => {
  render(
    <BrowserRouter>
      <TaskForm onSubmit={jest.fn()} />
    </BrowserRouter>,
  );
  const submitButton = screen.getByRole("button", { name: /create task/i });
  await userEvent.click(submitButton);
  await waitFor(() => {
    expect(
      screen.getByText((content) => content.includes("Title is required")),
    ).toBeInTheDocument();
  });

  await userEvent.type(screen.getByPlaceholderText(/task title/i), "New Task");
  await userEvent.click(submitButton);
  await waitFor(() => {
    expect(
      screen.getByText((content) => content.includes("Due date is required")),
    ).toBeInTheDocument();
  });
});

test("Due date validation shows error when date is in the past", async () => {
  render(
    <BrowserRouter>
      <TaskForm onSubmit={jest.fn()} />
    </BrowserRouter>,
  );
  const titleInput = screen.getByPlaceholderText(/task title/i);
  const dueDateInput = screen.getByPlaceholderText(/due date/i);
  const submitButton = screen.getByRole("button", { name: /create task/i });

  await userEvent.type(titleInput, "New Task");
  const pastDate = new Date();
  pastDate.setDate(pastDate.getDate() - 1);
  const pastDateString = pastDate.toISOString().split("T")[0];
  await userEvent.type(dueDateInput, pastDateString);
  const categorySelect = await screen.findByPlaceholderText(/category/i);
  await userEvent.selectOptions(categorySelect, "WORK");
  await userEvent.click(submitButton);

  await waitFor(() => {
    expect(
      screen.getByText((content) =>
        content.includes("Due date cannot be in the past."),
      ),
    ).toBeInTheDocument();
  });
});

test("Successful form submission for task creation", async () => {
  const mockOnSubmit = jest.fn().mockResolvedValue();
  render(
    <BrowserRouter>
      <TaskForm onSubmit={mockOnSubmit} />
    </BrowserRouter>,
  );
  const titleInput = screen.getByPlaceholderText(/task title/i);
  const dueDateInput = screen.getByPlaceholderText(/due date/i);
  const submitButton = screen.getByRole("button", { name: /create task/i });

  await userEvent.type(titleInput, "New Task");
  const futureDate = new Date();
  futureDate.setDate(futureDate.getDate() + 1);
  const futureDateString = futureDate.toISOString().split("T")[0];
  await userEvent.type(dueDateInput, futureDateString);
  const categorySelect = await screen.findByPlaceholderText(/category/i);
  await userEvent.selectOptions(categorySelect, "WORK");
  await userEvent.click(submitButton);

  await waitFor(() => {
    expect(mockOnSubmit).toHaveBeenCalledWith(
      expect.objectContaining({
        title: "New Task",
        dueDate: futureDateString,
      }),
    );
  });
});

test("Successful form submission for task editing", async () => {
  const initialTask = {
    id: 1,
    title: "Test Task",
    description: "This is a test task",
    status: "PENDING",
    priority: "HIGH",
    category: "WORK",
    dueDate: "2023-12-31",
  };
  const mockOnSubmit = jest.fn().mockResolvedValue();
  render(
    <BrowserRouter>
      <TaskForm initialTask={initialTask} onSubmit={mockOnSubmit} />
    </BrowserRouter>,
  );
  const titleInput = screen.getByPlaceholderText(/task title/i);
  const dueDateInput = screen.getByPlaceholderText(/due date/i);
  const submitButton = screen.getByRole("button", { name: /update task/i });

  await userEvent.clear(titleInput);
  await userEvent.type(titleInput, "Updated Task");
  // Set due date to tomorrow
  const futureDate = new Date();
  futureDate.setDate(futureDate.getDate() + 1);
  const futureDateString = futureDate.toISOString().split("T")[0];
  await userEvent.clear(dueDateInput);
  await userEvent.type(dueDateInput, futureDateString);
  await userEvent.click(submitButton);

  await waitFor(() => {
    expect(mockOnSubmit).toHaveBeenCalledWith(
      expect.objectContaining({
        id: 1,
        title: "Updated Task",
        dueDate: futureDateString,
      }),
    );
  });
});
