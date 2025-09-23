import { render, screen } from "@testing-library/react";
import Navbar from "./Navbar";
import { MemoryRouter } from "react-router-dom";

test("renders Navbar component", () => {
  render(
    <MemoryRouter>
      <Navbar />
    </MemoryRouter>,
  );

  const nav = screen.getByRole("navigation", { name: /main navigation/i });
  expect(nav).toBeInTheDocument();

  expect(screen.getByText("Taskwell")).toBeInTheDocument();

  expect(screen.getByRole("link", { name: /dashboard/i })).toHaveAttribute(
    "href",
    "/dashboard",
  );
  expect(screen.getByRole("link", { name: /task list/i })).toHaveAttribute(
    "href",
    "/tasks",
  );
  expect(screen.getByRole("link", { name: /new task/i })).toHaveAttribute(
    "href",
    "/tasks/new",
  );
  expect(screen.getByRole("link", { name: /profile/i })).toHaveAttribute(
    "href",
    "/profile",
  );
  expect(screen.getByRole("link", { name: /logout/i })).toHaveAttribute(
    "href",
    "/logout",
  );
});
