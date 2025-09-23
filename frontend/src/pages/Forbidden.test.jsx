import { fireEvent, render, screen } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import Forbidden from "./Forbidden";

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

function mockFetchOk(ok) {
  jest.spyOn(global, "fetch").mockImplementation(() => Promise.resolve({ ok }));
}

beforeEach(() => {
  jest
    .spyOn(global, "fetch")
    .mockImplementation(() => Promise.resolve({ ok: false }));
});

afterEach(() => {
  global.fetch.mockRestore();
});

test("Renders Forbidden component", async () => {
  mockFetchOk(true); // Simulate logged in
  render(
    <BrowserRouter>
      <Forbidden />
    </BrowserRouter>,
  );
  expect(
    screen.getByRole("main", { name: /forbidden page/i }),
  ).toBeInTheDocument();
  expect(screen.getByText(/403 - forbidden/i)).toBeInTheDocument();
  expect(
    screen.getByText(
      /you do not have permission to access this page or resource./i,
    ),
  ).toBeInTheDocument();
  const button = await screen.findByRole("button", {
    name: /go to dashboard/i,
  });
  expect(button).toBeInTheDocument();
});

test("Dashboard button redirects to /dashboard", async () => {
  mockFetchOk(true); // Simulate logged in
  render(
    <BrowserRouter>
      <Forbidden />
    </BrowserRouter>,
  );
  const button = await screen.findByRole("button", {
    name: /go to dashboard/i,
  });
  fireEvent.click(button);
  expect(mockNavigate).toHaveBeenCalledWith("/dashboard");
});

test("Dashboard button is focusable", async () => {
  mockFetchOk(true); // Simulate logged in
  render(
    <BrowserRouter>
      <Forbidden />
    </BrowserRouter>,
  );
  const button = await screen.findByRole("button", {
    name: /go to dashboard/i,
  });
  button.focus();
  expect(button).toHaveFocus();
});

test("Login button is shown when not logged in", async () => {
  mockFetchOk(false); // Simulate not logged in
  render(
    <BrowserRouter>
      <Forbidden />
    </BrowserRouter>,
  );
  await screen.findByRole("button", { name: /login/i });
  expect(screen.getByRole("button", { name: /login/i })).toBeInTheDocument();
});

test("Dashboard button is shown when logged in", async () => {
  mockFetchOk(true); // Simulate logged in
  render(
    <BrowserRouter>
      <Forbidden />
    </BrowserRouter>,
  );
  // Wait for fetch and state update
  await screen.findByRole("button", { name: /go to dashboard/i });
  expect(
    screen.getByRole("button", { name: /go to dashboard/i }),
  ).toBeInTheDocument();
});
