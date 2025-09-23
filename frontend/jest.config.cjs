module.exports = {
  moduleNameMapper: {
    "\\.css$": "identity-obj-proxy",
  },
  testEnvironment: "jsdom",
  setupFilesAfterEnv: ["./jest.setup.js"],
};
